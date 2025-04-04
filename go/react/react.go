package react

import "sync"

// reactor implements the Reactor interface.
type reactor struct{}

// inputCell implements the InputCell interface.
type inputCell struct {
	value       int
	dependents  []*computeCell // Cells that depend on this input cell
	mu          sync.Mutex
	reactorInst *reactor // Reference back to the reactor (might be needed for coordination)
}

// computeCell implements the ComputeCell interface.
type computeCell struct {
	value        int
	computeFunc  interface{} // Can be func(int) int or func(int, int) int
	dependencies []Cell      // Cells this compute cell depends on
	dependents   []*computeCell // Cells that depend on this compute cell
	callbacks    map[int]func(int) // Map callback ID to function
	nextCallbackID int
	mu           sync.Mutex
	reactorInst  *reactor // Reference back to the reactor
}

// canceler implements the Canceler interface.
type canceler struct {
	cell       *computeCell
	callbackID int
}

// New creates a new Reactor.
func New() Reactor {
	return &reactor{}
}

// --- inputCell Methods ---

// Value returns the current value of the input cell.
func (ic *inputCell) Value() int {
	ic.mu.Lock()
	defer ic.mu.Unlock()
	return ic.value
}

// SetValue sets the value of the input cell and triggers updates.
func (ic *inputCell) SetValue(newValue int) {
	ic.mu.Lock()
	oldValue := ic.value
	if oldValue == newValue {
		ic.mu.Unlock()
		return // No change, no update needed
	}
	ic.value = newValue
	dependentsToUpdate := make([]*computeCell, len(ic.dependents))
	copy(dependentsToUpdate, ic.dependents)
	ic.mu.Unlock() // Unlock before triggering updates to avoid deadlocks

	// Trigger updates in dependents (needs proper update mechanism)
	ic.reactorInst.propagateUpdates(dependentsToUpdate)
}

// --- computeCell Methods ---

// Value returns the current value of the compute cell.
// It might need recalculation if dependencies changed.
func (cc *computeCell) Value() int {
	// This basic implementation relies on propagation keeping the value updated.
	// A more robust version might check a 'dirty' flag and recompute if needed.
	cc.mu.Lock()
	defer cc.mu.Unlock()
	return cc.value
}

// AddCallback adds a callback function to the compute cell.
func (cc *computeCell) AddCallback(callback func(int)) Canceler {
	cc.mu.Lock()
	defer cc.mu.Unlock()

	if cc.callbacks == nil {
		cc.callbacks = make(map[int]func(int))
	}
	id := cc.nextCallbackID
	cc.callbacks[id] = callback
	cc.nextCallbackID++

	return &canceler{cell: cc, callbackID: id}
}

// --- canceler Methods ---

// Cancel removes the associated callback from the compute cell.
func (c *canceler) Cancel() {
	c.cell.mu.Lock()
	defer c.cell.mu.Unlock()
	// Check if callbacks map exists and the ID is present
	if c.cell.callbacks != nil {
		delete(c.cell.callbacks, c.callbackID)
	}
}

// --- reactor Methods ---

// CreateInput creates a new input cell.
func (r *reactor) CreateInput(initial int) InputCell {
	cell := &inputCell{
		value:       initial,
		reactorInst: r,
		// dependents initialized lazily or empty
	}
	return cell
}

// CreateCompute1 creates a compute cell dependent on one other cell.
func (r *reactor) CreateCompute1(dep Cell, compute func(int) int) ComputeCell {
	cc := &computeCell{
		computeFunc:  compute,
		dependencies: []Cell{dep},
		reactorInst:  r,
		callbacks:    make(map[int]func(int)),
		// dependents initialized lazily or empty
	}
	// Compute initial value *before* registering dependents to avoid premature updates
	cc.recompute() 

	// Register this compute cell as a dependent of its dependency
	r.registerDependent(dep, cc)

	return cc
}

// CreateCompute2 creates a compute cell dependent on two other cells.
func (r *reactor) CreateCompute2(dep1, dep2 Cell, compute func(int, int) int) ComputeCell {
	cc := &computeCell{
		computeFunc:  compute,
		dependencies: []Cell{dep1, dep2},
		reactorInst:  r,
		callbacks:    make(map[int]func(int)),
		// dependents initialized lazily or empty
	}
	// Compute initial value *before* registering dependents
	cc.recompute() 

	// Register this compute cell as a dependent of its dependencies
	r.registerDependent(dep1, cc)
	r.registerDependent(dep2, cc)

	return cc
}

// --- Helper Methods ---

// registerDependent adds a compute cell to the dependents list of another cell.
func (r *reactor) registerDependent(dependency Cell, dependent *computeCell) {
	switch dep := dependency.(type) {
	case *inputCell:
		dep.mu.Lock()
		dep.dependents = append(dep.dependents, dependent)
		dep.mu.Unlock()
	case *computeCell:
		dep.mu.Lock()
		dep.dependents = append(dep.dependents, dependent)
		dep.mu.Unlock()
	}
}

// recompute calculates the value of a compute cell based on its dependencies.
// Returns true if the value changed.
func (cc *computeCell) recompute() bool {
	var newValue int
	// Ensure dependencies are accessed safely if they might be updated concurrently
	// For simplicity here, assuming Value() calls are safe or handled by caller locks
	switch fn := cc.computeFunc.(type) {
	case func(int) int:
		newValue = fn(cc.dependencies[0].Value())
	case func(int, int) int:
		newValue = fn(cc.dependencies[0].Value(), cc.dependencies[1].Value())
	}

	cc.mu.Lock()
	// Store current value before potentially changing it
	oldValue := cc.value 
	changed := false
	if newValue != oldValue {
		cc.value = newValue
		changed = true
	}
	cc.mu.Unlock() // Unlock before returning

	return changed
}


// propagateUpdates handles the cascading updates through the dependency graph.
func (r *reactor) propagateUpdates(initialCellsToUpdate []*computeCell) {
	// Map to track cells whose values have changed during this update cycle and their values *before* the change.
	changedCells := make(map[*computeCell]int)
	// Queue for breadth-first update propagation.
	queue := make([]*computeCell, 0, len(initialCellsToUpdate))
	// Set to keep track of cells already added to the queue to avoid redundant processing.
	inQueue := make(map[*computeCell]bool)

	// Initialize the queue with the initial set of cells triggered by SetValue.
	for _, cell := range initialCellsToUpdate {
		if !inQueue[cell] {
			queue = append(queue, cell)
			inQueue[cell] = true
			// Store the value *before* any recomputation in this cycle
			changedCells[cell] = cell.Value() 
		}
	}

	// Process the queue until empty.
	head := 0
	for head < len(queue) {
		current := queue[head]
		head++

		// Recompute the cell's value. If it changes, propagate the update.
		if current.recompute() {
			// The value changed. Add its dependents to the queue if they aren't already there.
			current.mu.Lock()
			dependents := make([]*computeCell, len(current.dependents))
			copy(dependents, current.dependents)
			current.mu.Unlock()

			for _, dep := range dependents {
				if !inQueue[dep] {
					// Store the value *before* it might be recomputed later in the queue
					if _, exists := changedCells[dep]; !exists {
                         changedCells[dep] = dep.Value()
                    }
					queue = append(queue, dep)
					inQueue[dep] = true
				}
			}
		} else {
            // Value didn't change, remove from changedCells if it was added optimistically
            // This happens if a cell was added to the queue but its value didn't actually change upon recomputation.
            // However, the initial trigger cells *must* retain their original value for callback comparison.
            isInitialTrigger := false
            for _, initialCell := range initialCellsToUpdate {
                if current == initialCell {
                    isInitialTrigger = true
                    break
                }
            }
            if !isInitialTrigger {
                 // If recompute returned false, it means the value is the same as the *last* computed value.
                 // We still need to compare against the value *before* the entire update cycle began for callbacks.
                 // The logic below handles this comparison correctly.
            }
        }
	}

	// After all values have stabilized, trigger callbacks for cells whose final value
	// is different from their value *before* this update cycle started.
	for cell, valueBeforeUpdate := range changedCells {
		cell.triggerCallbacks(valueBeforeUpdate)
	}
}


// triggerCallbacks executes the callbacks registered for a compute cell if its value changed
// compared to its value *before* the update cycle began.
func (cc *computeCell) triggerCallbacks(valueBeforeUpdate int) {
	cc.mu.Lock()
	currentValue := cc.value
	// Only trigger if the final value is different from the value before the update cycle.
	if currentValue == valueBeforeUpdate {
		cc.mu.Unlock()
		return
	}

	// Copy callbacks to run them outside the lock.
	callbacksToRun := make([]func(int), 0, len(cc.callbacks))
	if cc.callbacks != nil {
		for _, cb := range cc.callbacks {
			callbacksToRun = append(callbacksToRun, cb)
		}
	}
	cc.mu.Unlock() 

	// Execute callbacks.
	for _, cb := range callbacksToRun {
		cb(currentValue) // Pass the new, stable value
	}
}
