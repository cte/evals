export class InputCell {
  constructor(value) {
    this._value = value;
    this._dependents = new Set(); // Cells that depend on this one
  }

  get value() {
    return this._value;
  }

  setValue(newValue) {
    if (this._value !== newValue) {
      this._value = newValue;
      this._propagate(); // Start the propagation wave
    }
  }

  _propagate() {
    const queue = Array.from(this._dependents);
    const visitedInWave = new Set(); // Track cells processed in this wave to prevent cycles/redundancy
    const changedInWave = new Set(); // Track ComputeCells whose values changed

    let count = 0;
    const MAX_COUNT = 1000; // Safety break

    // Seed the queue with initial dependents
    const initialQueue = Array.from(this._dependents);
    const processingQueue = [...initialQueue]; // Use a copy to iterate

    while (processingQueue.length > 0 && count < MAX_COUNT) {
      count++;
      const cell = processingQueue.shift();

      // Skip if already processed *successfully* in this wave
      if (visitedInWave.has(cell)) {
        continue;
      }

      let changed = false;
      if (cell instanceof ComputeCell) {
        const oldValue = cell.value;
        // Recompute based on potentially updated inputs. _computeValue reads current input values.
        const newValue = cell._computeValue();

        if (newValue !== oldValue) {
          cell._value = newValue; // Update internal value *before* adding dependents
          changedInWave.add(cell); // Mark that this cell changed
          changed = true;
        }
      } else {
          // If it's not a ComputeCell, it cannot change value based on inputs (it's an InputCell)
          // Or handle other cell types if they exist
      }

      // Mark as processed for this wave
      visitedInWave.add(cell);

      // If the cell changed OR if it's the initial input propagation, add its dependents
      // We need to ensure dependents are always considered if an upstream cell is processed.
      // Let's refine: Add dependents only if the current cell *could* influence them.
      // For ComputeCells, this happens if their value changed.
      if (changed && cell instanceof ComputeCell) {
          cell._dependents.forEach(dep => {
            // Add dependent to the queue. If it was already processed,
            // it might need reprocessing if its inputs changed.
            // Remove from visited so it can be processed again if needed.
            visitedInWave.delete(dep); // Allow reprocessing
            // Avoid adding duplicates to the queue itself
            if (!processingQueue.includes(dep)) {
                 processingQueue.push(dep);
            }
          });
      } else if (this._dependents.has(cell)) {
          // If it's a direct dependent of the initial InputCell, ensure its dependents are queued once.
          // This seems overly complex. Let's simplify the logic.

          // Simpler approach: Always add dependents if the current cell was processed.
          // The `visitedInWave` check prevents infinite loops. If a dependent needs
          // re-evaluation due to another input changing, it will be processed again.
           if (cell instanceof ComputeCell) { // Only compute cells have dependents to propagate to
               cell._dependents.forEach(dep => {
                   if (!processingQueue.includes(dep)) { // Basic check to avoid queue bloat
                       processingQueue.push(dep);
                   }
               });
           }
      }
    }


    if (count >= MAX_COUNT) {
      console.warn("Max propagation limit reached; potential cycle or large graph.");
    }

    // After propagation is complete, run callbacks *only once* for cells that changed.
    changedInWave.forEach(cell => cell._runCallbacks());
  }


  _addDependent(cell) {
    this._dependents.add(cell);
  }

  _removeDependent(cell) {
    this._dependents.delete(cell);
  }
}

export class ComputeCell {
  constructor(inputCells, fn) {
    this._inputs = inputCells;
    this._fn = fn;
    this._dependents = new Set();
    this._callbacks = new Set(); // Stores CallbackCell instances
    this._value = undefined; // Initialize value

    // Register this compute cell as a dependent of its inputs
    this._inputs.forEach(input => {
        if (!input || typeof input._addDependent !== 'function') {
             console.error("Invalid input cell provided:", input);
             throw new Error("Invalid input cell provided to ComputeCell constructor.");
        }
        input._addDependent(this);
    });


    // Calculate initial value - This should ideally happen within a propagation system
    // but for initialization, we compute directly. Callbacks are not run here.
    // Handle potential errors during initial computation
    try {
        this._value = this._computeValue();
    } catch (e) {
        console.error("Error during initial computation:", e);
        this._value = undefined; // Or some default error state
    }
  }

  get value() {
    // In this eager system, the value should be up-to-date.
    return this._value;
  }

  // Internal method to compute value based on current input cells
  _computeValue() {
     // Pass the array of input cell instances to the compute function
     try {
        // The compute function provided by the test expects the cell instances
        return this._fn(this._inputs);
     } catch (e) {
        console.error("Error computing value:", e);
        // Decide how to handle compute errors (e.g., return undefined, keep old value?)
        return this._value; // Keep old value on error for now
     }
  }

  addCallback(cbCell) {
    if (!(cbCell instanceof CallbackCell)) {
      throw new Error("Argument must be an instance of CallbackCell");
    }
    this._callbacks.add(cbCell);
  }

  removeCallback(cbCell) {
    if (!(cbCell instanceof CallbackCell)) {
      throw new Error("Argument must be an instance of CallbackCell");
    }
    this._callbacks.delete(cbCell);
  }

  // Internal method called by the propagation logic after value has changed
  _runCallbacks() {
    this._callbacks.forEach(cbCell => {
      cbCell._executeCallback(this);
    });
  }

  // Methods for graph structure
  _addDependent(cell) {
    this._dependents.add(cell);
  }

  _removeDependent(cell) {
    this._dependents.delete(cell);
  }

  // Optional: Cleanup method
  dispose() {
    this._inputs.forEach(input => {
        if (input && typeof input._removeDependent === 'function') {
            input._removeDependent(this);
        }
    });
    this._callbacks.clear();
    this._dependents.clear(); // Prevent this cell from being updated or updating others
  }
}

export class CallbackCell {
  constructor(fn) {
    if (typeof fn !== 'function') {
      throw new Error("CallbackCell requires a function argument.");
    }
    this.fn = fn; // The actual callback function
    this.values = []; // Stores the history of values reported by the callback
  }

  // Internal method called by ComputeCell to execute the callback and store the value
  _executeCallback(computeCellInstance) {
    try {
        this.values.push(this.fn(computeCellInstance));
    } catch (e) {
        console.error("Error executing callback:", e);
        // Decide how to handle callback errors
    }
  }
}
