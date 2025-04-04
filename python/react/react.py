class InputCell:
    def __init__(self, initial_value):
        self._value = initial_value
        self._dependents = set() # Stores ComputeCells that depend on this cell

    @property
    def value(self):
        return self._value

    @value.setter
    def value(self, new_value):
        if new_value == self._value:
            return # No change, do nothing

        self._value = new_value

        # --- Update propagation ---
        # Keep track of cells that changed during this update cycle
        # Store the value they had *before* this update cycle started
        changed_in_cycle = {}

        # Use a queue for processing, allowing re-queueing
        queue = list(self._dependents)
        # Keep track of cells added to the queue to avoid redundant immediate adds,
        # but allow re-adding later if an input changes. Using a set for quick lookups.
        queued_or_processed = set(self._dependents)

        idx = 0
        while idx < len(queue): # Process queue elements without popping until done
            cell = queue[idx]
            idx += 1 # Move to next item for next iteration

            # Need to handle potential AttributeError if cell is not ComputeCell
            # Although dependents should always be ComputeCells based on _add_dependent usage
            if not isinstance(cell, ComputeCell):
                 continue # Should not happen in valid usage

            old_value = cell.value # Get value before recomputing
            new_value = cell._compute() # Recompute based on current inputs

            if new_value != old_value:
                # Value changed, record original value if first change in cycle
                if cell not in changed_in_cycle:
                    changed_in_cycle[cell] = old_value

                # Update the cell's internal value
                cell._update_value(new_value) # Use internal update method

                # Add dependents to the queue for potential updates
                for dep in cell._dependents:
                    # Add dependent to queue if it hasn't been queued/processed *yet* in this wave
                    # OR if it has already been processed but needs re-evaluation because this input changed.
                    # Simple approach: always add dependents if the current cell changed.
                    # Let the `if new_value != old_value` check prevent infinite loops for stable values.
                    # We might process cells multiple times, but it ensures correctness.
                    if dep not in queued_or_processed:
                         queue.append(dep)
                         queued_or_processed.add(dep)
                    else:
                         # If already processed, ensure it's re-added to the queue
                         # if it's not already pending later in the queue.
                         # This check avoids adding duplicates right next to each other.
                         if dep not in queue[idx:]:
                              queue.append(dep)


        # --- Callback firing ---
        # After the system has stabilized, call callbacks for compute cells whose
        # final value is different from their value before the update cycle began.
        for cell, original_value in changed_in_cycle.items():
             if isinstance(cell, ComputeCell): # Ensure it's a compute cell
                 # Check if the *final* value differs from the *original* value
                 if cell.value != original_value:
                      cell._fire_callbacks()


    def _add_dependent(self, compute_cell):
        """Internal method called by ComputeCell during its init."""
        # Ensure we only add ComputeCells as dependents
        if isinstance(compute_cell, ComputeCell):
             self._dependents.add(compute_cell)


class ComputeCell:
    def __init__(self, inputs, compute_function):
        self._inputs = inputs
        self._compute_function = compute_function
        self._callbacks = []
        self._dependents = set() # Stores ComputeCells that depend on this cell

        # Register this cell as a dependent of its inputs
        # Crucially, this must happen *before* the initial compute
        for inp in self._inputs:
            # Works for both InputCell and ComputeCell inputs
            inp._add_dependent(self)

        # Calculate and store initial value
        # This might trigger _compute on cells that haven't fully initialized dependents yet?
        # Let's ensure _add_dependent is robust.
        self._value = self._compute()


    @property
    def value(self):
        """Return the current value of the cell."""
        return self._value

    def add_callback(self, callback):
        """Add a callback function."""
        self._callbacks.append(callback)

    def remove_callback(self, callback):
        """Remove a previously added callback."""
        try:
            self._callbacks.remove(callback)
        except ValueError:
            pass # Callback not found

    def _add_dependent(self, compute_cell):
        """Internal method called by other ComputeCells during their init."""
         # Ensure we only add ComputeCells as dependents
        if isinstance(compute_cell, ComputeCell):
            self._dependents.add(compute_cell)

    def _compute(self):
        """Internal method to compute the value based on inputs."""
        # This might be called during init before all dependencies are set?
        # The value property access should be safe.
        return self._compute_function([i.value for i in self._inputs])

    def _update_value(self, new_value):
        """Internal method to update the cell's value during propagation."""
        self._value = new_value

    def _fire_callbacks(self):
        """Internal method to call callbacks with the current value."""
        for cb in self._callbacks:
            cb(self._value)