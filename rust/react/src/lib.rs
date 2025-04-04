use std::collections::{HashMap, HashSet, VecDeque};

// --- ID Structs (already defined in stub) ---
/// `InputCellId` is a unique identifier for an input cell.
#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)] // Added Hash
pub struct InputCellId(usize); // Added inner value for uniqueness
/// `ComputeCellId` is a unique identifier for a compute cell.
/// Values of type `InputCellId` and `ComputeCellId` should not be mutually assignable,
/// demonstrated by the following tests:
///
/// ```compile_fail
/// let mut r = react::Reactor::new();
/// let input: react::ComputeCellId = r.create_input(111);
/// ```
///
/// ```compile_fail
/// let mut r = react::Reactor::new();
/// let input = r.create_input(111);
/// let compute: react::InputCellId = r.create_compute(&[react::CellId::Input(input)], |_| 222).unwrap();
/// ```
#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)] // Added Hash
pub struct ComputeCellId(usize); // Added inner value for uniqueness
#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)] // Added Hash
pub struct CallbackId(usize); // Added inner value for uniqueness

#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)] // Added Hash
pub enum CellId {
    Input(InputCellId),
    Compute(ComputeCellId),
}

#[derive(Debug, PartialEq, Eq)]
pub enum RemoveCallbackError {
    NonexistentCell,
    NonexistentCallback,
}

// --- Internal Structs ---

// Represents data stored for a compute cell
struct ComputeCellData<'a, T> {
    dependencies: Vec<CellId>,
    compute_func: Box<dyn Fn(&[T]) -> T + 'a>, // Added lifetime 'a
    value: T, // Store the current value
    callbacks: HashMap<CallbackId, Box<dyn FnMut(T) + 'a>>, // Added lifetime 'a
    // dependents: HashSet<ComputeCellId>, // Removed: Not used, dependency_graph is source of truth
}

// Represents the main reactive system state
pub struct Reactor<'a, T: Copy + PartialEq> { // Added lifetime 'a
    input_cells: HashMap<InputCellId, T>,
    compute_cells: HashMap<ComputeCellId, ComputeCellData<'a, T>>,
    // Keep track of dependencies for compute cells
    // Map: dependency_cell -> set_of_compute_cells_that_depend_on_it
    dependency_graph: HashMap<CellId, HashSet<ComputeCellId>>,
    next_input_id: usize,
    next_compute_id: usize,
    next_callback_id: usize,
    // PhantomData needed because of the lifetime 'a in ComputeCellData
    _phantom: ::std::marker::PhantomData<&'a T>,
}

// --- Implementation ---

// You are guaranteed that Reactor will only be tested against types that are Copy + PartialEq.
impl<'a, T: Copy + PartialEq> Reactor<'a, T> {
    pub fn new() -> Self {
        Reactor {
            input_cells: HashMap::new(),
            compute_cells: HashMap::new(),
            dependency_graph: HashMap::new(),
            next_input_id: 0,
            next_compute_id: 0,
            next_callback_id: 0,
            _phantom: ::std::marker::PhantomData,
        }
    }

    // Creates an input cell with the specified initial value, returning its ID.
    pub fn create_input(&mut self, initial: T) -> InputCellId {
        let id = InputCellId(self.next_input_id);
        self.next_input_id += 1;
        self.input_cells.insert(id, initial);
        id
    }

    // --- Placeholder for other methods ---

    // Creates a compute cell with the specified dependencies and compute function.
    // ... (rest of the methods will be implemented step-by-step)
    pub fn create_compute<F: Fn(&[T]) -> T + 'a>( // Added 'a lifetime bound
        &mut self,
        dependencies: &[CellId],
        compute_func: F,
    ) -> Result<ComputeCellId, CellId> {
        // 1. Check if all dependencies exist and collect their values
        let mut dep_values = Vec::with_capacity(dependencies.len());
        for &dep_id in dependencies {
            match self.value(dep_id) {
                Some(val) => dep_values.push(val),
                None => return Err(dep_id), // Dependency doesn't exist
            }
        }

        // 2. Calculate the initial value
        let initial_value = compute_func(&dep_values);

        // 3. Generate new ID and create the cell data
        let id = ComputeCellId(self.next_compute_id);
        self.next_compute_id += 1;

        let cell_data = ComputeCellData {
            dependencies: dependencies.to_vec(),
            compute_func: Box::new(compute_func),
            value: initial_value,
            callbacks: HashMap::new(),
            // dependents: HashSet::new(), // Removed field
        };

        // 4. Store the compute cell
        self.compute_cells.insert(id, cell_data);

        // 5. Update the dependency graph (both forward and reverse)
        // let compute_cell_id_enum = CellId::Compute(id); // Not needed directly here
        for &dep_id in dependencies {
            // Add this compute cell to the list of dependents for each dependency
            self.dependency_graph
                .entry(dep_id)
                .or_default()
                .insert(id);

            // The logic to update ComputeCellData.dependents was here, but removed as the field is removed.
        }


        Ok(id)
    }

    // Retrieves the current value of the cell, or None if the cell does not exist.
    pub fn value(&self, id: CellId) -> Option<T> {
        match id {
            CellId::Input(input_id) => self.input_cells.get(&input_id).copied(),
            CellId::Compute(compute_id) => self.compute_cells.get(&compute_id).map(|data| data.value),
        }
    }

     // Recalculates the value of a compute cell and its dependents recursively.
    // Returns a set of compute cells whose values might have changed.
    // fn update_computations(&mut self, start_cell_id: ComputeCellId) -> HashMap<ComputeCellId, T> {
        // This function was removed as the logic is integrated into set_value
        // let mut changed_cells = HashMap::new();
        // ... implementation removed ...
        // changed_cells // Placeholder return
    // }


    // Sets the value of the specified input cell.
    pub fn set_value(&mut self, id: InputCellId, new_value: T) -> bool {
        // 1. Check if input cell exists and if value actually changed
        let _old_value = match self.input_cells.get_mut(&id) { // Prefixed with _ as it's not read later
            Some(value_ref) => {
                if *value_ref == new_value {
                    return true; // Value didn't change, no updates needed
                }
                let old = *value_ref;
                *value_ref = new_value; // Update the value
                old
            }
            None => return false, // Cell doesn't exist
        };

        // 2. Identify all compute cells that need recomputation (BFS traversal)
        let mut cells_to_process = VecDeque::new(); // Queue for BFS
        let mut visited_for_processing = HashSet::new(); // Track visited during BFS

        // Start BFS from direct dependents of the changed input cell
        if let Some(direct_dependents) = self.dependency_graph.get(&CellId::Input(id)) {
            for &compute_id in direct_dependents {
                if visited_for_processing.insert(compute_id) {
                    cells_to_process.push_back(compute_id);
                }
            }
        }

        // Store old values of potentially affected compute cells *before* recomputation
        // We need to traverse the dependency graph starting from the initial dependents
        // to find *all* potentially affected cells.
        let mut old_compute_values: HashMap<ComputeCellId, T> = HashMap::new();
        let mut propagation_queue = cells_to_process.clone(); // Use the initial dependents
        let mut processed_for_old_value = HashSet::new();

        while let Some(current_id) = propagation_queue.pop_front() {
             if processed_for_old_value.insert(current_id) {
                 if let Some(cell_data) = self.compute_cells.get(&current_id) {
                     old_compute_values.insert(current_id, cell_data.value);
                     // Add its dependents (found via dependency_graph) to the queue
                     if let Some(dependents) = self.dependency_graph.get(&CellId::Compute(current_id)) {
                         for &dependent_id in dependents {
                             if !processed_for_old_value.contains(&dependent_id) { // Avoid cycles/redundancy
                                propagation_queue.push_back(dependent_id);
                             }
                         }
                     }
                 }
             }
        }


        // 3. Recompute values in topological order (implicitly handled by BFS order)
        let mut callbacks_to_run: Vec<(ComputeCellId, T)> = Vec::new();
        // Use the `cells_to_process` queue which contains the initial set of direct dependents.
        // The BFS ensures we process cells only after their dependencies might have changed.
        let mut recomputation_queue = cells_to_process; // Reuse the queue from step 2
        // let mut processed_in_recomputation: HashSet<ComputeCellId> = HashSet::new(); // Removed: Not used, logic relies on visited_for_processing


        while let Some(compute_id) = recomputation_queue.pop_front() {

            // Check if already processed in this recomputation wave
            // This check might be redundant due to the initial visited_for_processing,
            // but let's keep it for safety, especially if the graph structure is complex.
            // if !processed_in_recomputation.insert(compute_id) {
            //     continue;
            // }
            // Let's rely on the initial `visited_for_processing` check when adding to the queue.

            // Get dependencies and their *current* values
            let (deps, compute_func_box) = match self.compute_cells.get(&compute_id) {
                 // Clone dependencies, but get a reference to the function Box
                 Some(data) => (data.dependencies.clone(), &data.compute_func),
                 None => continue, // Should not happen if graph is consistent
            };

            let mut dep_values = Vec::with_capacity(deps.len());
            let mut deps_valid = true;
            for dep_id in &deps {
                match self.value(*dep_id) {
                    Some(val) => dep_values.push(val),
                    None => {
                        deps_valid = false; // Should not happen
                        break;
                    }
                }
            }

            if !deps_valid { continue; } // Skip if a dependency somehow vanished

            // Recompute the value using the referenced Box
            let new_compute_value = (compute_func_box)(&dep_values);


            // Update the cell's value in the main storage
            // Important: Update the value *before* adding dependents to the queue
            let cell_data = self.compute_cells.get_mut(&compute_id).unwrap();
            cell_data.value = new_compute_value;


            // Add dependents of this cell to the queue if they haven't been visited yet
            if let Some(dependents) = self.dependency_graph.get(&CellId::Compute(compute_id)) {
                 for &dependent_id in dependents {
                     if visited_for_processing.insert(dependent_id) { // Use the main visited set
                        recomputation_queue.push_back(dependent_id);
                     }
                 }
             }
        }


        // 4. Trigger callbacks for cells whose final value changed
        // Iterate through the cells whose old values we stored
        for (compute_id, old_compute_value) in old_compute_values {
            // Get the *final* new value after all computations
            // We can safely unwrap here because we know the cell exists (it was in old_compute_values)
            let new_value = self.compute_cells.get(&compute_id).unwrap().value;
            if old_compute_value != new_value {
                // Value changed, schedule callbacks for this cell
                callbacks_to_run.push((compute_id, new_value));
            }
        }

        // Execute the callbacks (outside the main computation loop)
        for (compute_id, final_value) in callbacks_to_run {
            if let Some(cell_data) = self.compute_cells.get_mut(&compute_id) {
                for callback in cell_data.callbacks.values_mut() {
                    callback(final_value);
                }
            }
        }

        true // Successfully set value and updated dependents
    }


    // Adds a callback to the specified compute cell.
    pub fn add_callback<F: FnMut(T) + 'a>( // Added 'a lifetime bound
        &mut self,
        id: ComputeCellId,
        callback: F,
    ) -> Option<CallbackId> {
        match self.compute_cells.get_mut(&id) {
            Some(cell_data) => {
                let callback_id = CallbackId(self.next_callback_id);
                self.next_callback_id += 1;
                cell_data.callbacks.insert(callback_id, Box::new(callback));
                Some(callback_id)
            }
            None => None, // Cell doesn't exist
        }
    }

    // Removes the specified callback, using an ID returned from add_callback.
    pub fn remove_callback(
        &mut self,
        cell_id: ComputeCellId,
        callback_id: CallbackId,
    ) -> Result<(), RemoveCallbackError> {
        match self.compute_cells.get_mut(&cell_id) {
            Some(cell_data) => {
                match cell_data.callbacks.remove(&callback_id) {
                    Some(_) => Ok(()),
                    None => Err(RemoveCallbackError::NonexistentCallback),
                }
            }
            None => Err(RemoveCallbackError::NonexistentCell),
        }
    }
}
