export class InputCell {
  constructor(value) {
    this.value = value;
    this.dependents = new Set();
  }

  setValue(newValue) {
    if (this.value === newValue) return;
    this.value = newValue;
    this._propagateAndNotify();
  }

  _propagateAndNotify() {
    const affected = new Set();
    const changed = new Set();

    const collect = (cell) => {
      if (!(cell instanceof ComputeCell)) return;
      if (affected.has(cell)) return;
      affected.add(cell);
      if (cell.dependents) {
        for (const dep of cell.dependents) {
          collect(dep);
        }
      }
    };

    for (const dep of this.dependents) {
      collect(dep);
    }

    // Topological sort: repeatedly add cells whose inputs are not in affected or already sorted
    const sorted = [];
    const temp = new Set(affected);
    while (temp.size > 0) {
      let progress = false;
      for (const cell of Array.from(temp)) {
        const inputsInAffected = cell.inputCells.filter(c => affected.has(c));
        const inputsSorted = inputsInAffected.every(c => sorted.includes(c));
        if (inputsSorted) {
          sorted.push(cell);
          temp.delete(cell);
          progress = true;
        }
      }
      if (!progress) break; // prevent infinite loop on cycles (shouldn't happen)
    }

    for (const cell of sorted) {
      const oldValue = cell.value;
      const newValue = cell.computeFn(cell.inputCells);
      cell.value = newValue;
      if (oldValue !== newValue) {
        changed.add(cell);
      }
    }

    for (const cell of changed) {
      cell._fireCallbacks();
    }
  }
}

export class ComputeCell {
  constructor(inputCells, computeFn) {
    this.inputCells = inputCells;
    this.computeFn = computeFn;
    this.callbacks = new Set();
    this.value = this.computeFn(this.inputCells);

    for (const cell of inputCells) {
      if (!cell.dependents) cell.dependents = new Set();
      cell.dependents.add(this);
    }
    this.dependents = new Set();
  }

  addCallback(callbackCell) {
    this.callbacks.add(callbackCell);
  }

  removeCallback(callbackCell) {
    this.callbacks.delete(callbackCell);
  }

  _fireCallbacks() {
    for (const cb of this.callbacks) {
      cb._call(this);
    }
  }
}

export class CallbackCell {
  constructor(callback) {
    this.callback = callback;
    this.values = [];
  }

  _call(cell) {
    const result = this.callback(cell);
    this.values.push(result);
  }
}
