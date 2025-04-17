from collections import deque

class InputCell:
    def __init__(self, initial_value):
        self._value = initial_value
        self._dependents = []

    @property
    def value(self):
        return self._value

    @value.setter
    def value(self, new_value):
        if self._value != new_value:
            self._value = new_value
            self._propagate_and_notify()

    def _propagate_and_notify(self):
        # Collect all affected compute cells
        affected = set()
        queue = deque(self._dependents)
        while queue:
            cell = queue.popleft()
            if cell in affected:
                continue
            affected.add(cell)
            queue.extend(cell._dependents)

        # Topologically sort affected cells
        sorted_cells = []
        visited = set()

        def visit(cell):
            if cell in visited:
                return
            visited.add(cell)
            for dep in cell._inputs:
                if isinstance(dep, ComputeCell) and dep in affected:
                    visit(dep)
            sorted_cells.append(cell)

        for cell in affected:
            visit(cell)

        # Phase 1: update values
        changed_cells = set()
        for cell in sorted_cells:
            old_value = cell._value
            new_value = cell._compute()
            if new_value != old_value:
                cell._value = new_value
                changed_cells.add(cell)

        # Phase 2: fire callbacks
        for cell in changed_cells:
            for callback in cell._callbacks:
                callback(cell._value)

    def add_dependent(self, dependent):
        self._dependents.append(dependent)


class ComputeCell:
    def __init__(self, inputs, compute_function):
        self._inputs = inputs
        self._compute_function = compute_function
        self._dependents = []
        self._callbacks = []
        self._value = self._compute()

        # Register self as dependent of inputs
        for input_cell in self._inputs:
            input_cell.add_dependent(self)

    @property
    def value(self):
        return self._value

    def _compute(self):
        input_values = [cell.value for cell in self._inputs]
        return self._compute_function(input_values)

    def add_dependent(self, dependent):
        self._dependents.append(dependent)

    def add_callback(self, callback):
        if callback not in self._callbacks:
            self._callbacks.append(callback)

    def remove_callback(self, callback):
        if callback in self._callbacks:
            self._callbacks.remove(callback)