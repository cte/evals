use std::collections::HashMap;

#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)]
pub struct InputCellId(usize);

#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)]
pub struct ComputeCellId(usize);

#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)]
pub struct CallbackId(usize);

#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)]
pub enum CellId {
    Input(InputCellId),
    Compute(ComputeCellId),
}

#[derive(Debug, PartialEq, Eq)]
pub enum RemoveCallbackError {
    NonexistentCell,
    NonexistentCallback,
}

pub struct Reactor<'a, T> {
    next_input_id: usize,
    next_compute_id: usize,
    next_callback_id: usize,
    inputs: HashMap<InputCellId, T>,
    computes: HashMap<ComputeCellId, ComputeCell<'a, T>>,
}

struct ComputeCell<'a, T> {
    dependencies: Vec<CellId>,
    compute_func: Box<dyn Fn(&[T]) -> T + 'a>,
    value: T,
    callbacks: HashMap<CallbackId, Box<dyn FnMut(T) + 'a>>,
}

impl<'a, T: Copy + PartialEq> Reactor<'a, T> {
    pub fn new() -> Self {
        Reactor {
            next_input_id: 0,
            next_compute_id: 0,
            next_callback_id: 0,
            inputs: HashMap::new(),
            computes: HashMap::new(),
        }
    }

    pub fn create_input(&mut self, initial: T) -> InputCellId {
        let id = InputCellId(self.next_input_id);
        self.next_input_id += 1;
        self.inputs.insert(id, initial);
        id
    }

    pub fn create_compute<F: Fn(&[T]) -> T + 'static>(
        &mut self,
        dependencies: &[CellId],
        compute_func: F,
    ) -> Result<ComputeCellId, CellId> {
        // Check dependencies exist
        let mut dep_values = Vec::with_capacity(dependencies.len());
        for dep in dependencies {
            match dep {
                CellId::Input(id) => {
                    if let Some(val) = self.inputs.get(id) {
                        dep_values.push(*val);
                    } else {
                        return Err(*dep);
                    }
                }
                CellId::Compute(id) => {
                    if let Some(cell) = self.computes.get(id) {
                        dep_values.push(cell.value);
                    } else {
                        return Err(*dep);
                    }
                }
            }
        }

        let value = compute_func(&dep_values);
        let id = ComputeCellId(self.next_compute_id);
        self.next_compute_id += 1;
        self.computes.insert(
            id,
            ComputeCell {
                dependencies: dependencies.to_vec(),
                compute_func: Box::new(compute_func),
                value,
                callbacks: HashMap::new(),
            },
        );
        Ok(id)
    }

    pub fn value(&self, id: CellId) -> Option<T> {
        match id {
            CellId::Input(input_id) => self.inputs.get(&input_id).copied(),
            CellId::Compute(compute_id) => self.computes.get(&compute_id).map(|c| c.value),
        }
    }

    pub fn set_value(&mut self, id: InputCellId, new_value: T) -> bool {
        if let Some(input_val) = self.inputs.get_mut(&id) {
            if *input_val != new_value {
                *input_val = new_value;
                self.update_compute_cells();
            }
            true
        } else {
            false
        }
    }

    fn update_compute_cells(&mut self) {
        let mut changed = true;
        // Track which compute cells changed during propagation
        let mut changed_cells: HashMap<ComputeCellId, T> = HashMap::new();

        while changed {
            changed = false;
            let compute_ids: Vec<ComputeCellId> = self.computes.keys().cloned().collect();
            for id in compute_ids {
                let dependencies = if let Some(cell) = self.computes.get(&id) {
                    cell.dependencies.clone()
                } else {
                    continue;
                };

                let dep_values: Vec<T> = dependencies
                    .iter()
                    .filter_map(|dep| match dep {
                        CellId::Input(input_id) => self.inputs.get(input_id).copied(),
                        CellId::Compute(compute_id) => self.computes.get(compute_id).map(|c| c.value),
                    })
                    .collect();

                let new_value = {
                    let cell = self.computes.get(&id).unwrap();
                    (cell.compute_func)(&dep_values)
                };

                let cell = self.computes.get_mut(&id).unwrap();
                if new_value != cell.value {
                    cell.value = new_value;
                    changed_cells.insert(id, new_value);
                    changed = true;
                }
            }
        }

        // After all updates stabilize, fire callbacks once per changed compute cell
        for (id, new_value) in changed_cells {
            if let Some(cell) = self.computes.get_mut(&id) {
                for callback in cell.callbacks.values_mut() {
                    callback(new_value);
                }
            }
        }
    }

    pub fn add_callback<F: FnMut(T) + 'a>(
        &mut self,
        id: ComputeCellId,
        callback: F,
    ) -> Option<CallbackId> {
        let compute_cell = self.computes.get_mut(&id)?;
        let cb_id = CallbackId(self.next_callback_id);
        self.next_callback_id += 1;
        compute_cell.callbacks.insert(cb_id, Box::new(callback));
        Some(cb_id)
    }

    pub fn remove_callback(
        &mut self,
        cell: ComputeCellId,
        callback: CallbackId,
    ) -> Result<(), RemoveCallbackError> {
        let compute_cell = self
            .computes
            .get_mut(&cell)
            .ok_or(RemoveCallbackError::NonexistentCell)?;
        if compute_cell.callbacks.remove(&callback).is_some() {
            Ok(())
        } else {
            Err(RemoveCallbackError::NonexistentCallback)
        }
    }
}
