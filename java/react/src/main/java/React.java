import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class React {

    public static abstract class Cell<T> {
        protected T value;
        protected final List<ComputeCell<T>> dependents = new ArrayList<>();

        public T getValue() {
            return value;
        }

        protected void addDependent(ComputeCell<T> cell) {
            dependents.add(cell);
        }

        protected void notifyDependents() {
            Set<ComputeCell<T>> visited = new HashSet<>();
            List<ComputeCell<T>> queue = new ArrayList<>(dependents);

            // Collect all affected compute cells
            while (!queue.isEmpty()) {
                ComputeCell<T> current = queue.remove(0);
                if (!visited.add(current)) continue;
                queue.addAll(current.dependents);
            }

            // Topological sort: repeatedly add cells with no unvisited dependencies
            List<ComputeCell<T>> sorted = new ArrayList<>();
            Set<ComputeCell<T>> tempVisited = new HashSet<>(visited);

            while (!tempVisited.isEmpty()) {
                boolean progress = false;
                for (ComputeCell<T> cell : new ArrayList<>(tempVisited)) {
                    boolean allDepsSorted = true;
                    for (Cell<T> dep : cell.dependencies) {
                        if (dep instanceof ComputeCell && tempVisited.contains(dep)) {
                            allDepsSorted = false;
                            break;
                        }
                    }
                    if (allDepsSorted) {
                        sorted.add(cell);
                        tempVisited.remove(cell);
                        progress = true;
                    }
                }
                if (!progress) {
                    throw new IllegalStateException("Cycle detected in compute cell dependencies");
                }
            }

            // Record old values
            java.util.Map<ComputeCell<T>, T> oldValues = new java.util.HashMap<>();
            for (ComputeCell<T> cell : sorted) {
                oldValues.put(cell, cell.value);
            }

            // Recompute in topological order
            for (ComputeCell<T> cell : sorted) {
                cell.recompute();
            }

            // Fire callbacks for changed cells
            for (ComputeCell<T> cell : sorted) {
                T oldVal = oldValues.get(cell);
                T newVal = cell.value;
                if ((oldVal == null && newVal != null) ||
                    (oldVal != null && !oldVal.equals(newVal))) {
                    cell.fireCallbacks();
                }
            }
        }
    }

    public static class InputCell<T> extends Cell<T> {
        public InputCell(T initialValue) {
            this.value = initialValue;
        }

        public void setValue(T newValue) {
            if ((this.value == null && newValue != null) ||
                (this.value != null && !this.value.equals(newValue))) {
                this.value = newValue;
                notifyDependents();
            }
        }
    }

    public static class ComputeCell<T> extends Cell<T> {
        private final List<Cell<T>> dependencies;
        private final Function<List<T>, T> computeFunction;
        private final List<Consumer<T>> callbacks = new ArrayList<>();

        public ComputeCell(Function<List<T>, T> computeFunction, List<Cell<T>> dependencies) {
            this.computeFunction = computeFunction;
            this.dependencies = dependencies;

            for (Cell<T> dep : dependencies) {
                dep.addDependent(this);
            }

            this.value = computeFunction.apply(getDependencyValues());
        }

        private List<T> getDependencyValues() {
            List<T> values = new ArrayList<>();
            for (Cell<T> dep : dependencies) {
                values.add(dep.getValue());
            }
            return values;
        }

        private void recompute() {
            this.value = computeFunction.apply(getDependencyValues());
        }

        public void addCallback(Consumer<T> callback) {
            callbacks.add(callback);
        }

        public void removeCallback(Consumer<T> callback) {
            callbacks.remove(callback);
        }

        private void fireCallbacks() {
            for (Consumer<T> callback : callbacks) {
                callback.accept(this.value);
            }
        }
    }

    public static <T> InputCell<T> inputCell(T initialValue) {
        return new InputCell<>(initialValue);
    }

    public static <T> ComputeCell<T> computeCell(Function<List<T>, T> function, List<Cell<T>> cells) {
        return new ComputeCell<>(function, cells);
    }
}
