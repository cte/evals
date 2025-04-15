import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class React {
    // Global counter to track distinct update waves originating from InputCell changes
    private static int globalUpdateWave = 0;

    // Method to advance to the next update wave
    // Should be called ONLY by InputCell.setValue
    private static void startUpdateWave() {
        globalUpdateWave++;
    }

    // Method to get the current update wave number
    private static int getCurrentUpdateWave() {
        return globalUpdateWave;
    }

    // Interface for anything that can be listened to
    interface Observable<T> {
        void addListener(Observer<T> listener);
        void removeListener(Observer<T> listener);
    }

    // Interface for anything that listens
    interface Observer<T> {
        // Method called when an observed value changes
        void update(Observable<T> source);
    }

    // Base Cell concept - holds a value and notifies listeners
    public static abstract class Cell<T> implements Observable<T> {
        protected T value;
        // Use Set to avoid duplicate listeners and for efficient add/remove
        protected Set<Observer<T>> listeners = new HashSet<>();

        // Concrete classes must implement how value is obtained/calculated
        public abstract T getValue();

        @Override
        public void addListener(Observer<T> listener) {
            listeners.add(listener);
        }

        @Override
        public void removeListener(Observer<T> listener) {
            listeners.remove(listener);
        }

        // Notify all registered listeners about a potential change
        protected void notifyListeners() {
            // Create a copy to iterate over, avoiding ConcurrentModificationException
            Set<Observer<T>> listenersCopy = new HashSet<>(listeners);
            for (Observer<T> listener : listenersCopy) {
                // Trigger the update method on downstream observers (ComputeCells)
                listener.update(this);
            }
        }

        // Helper to check for value changes, correctly handling nulls
        protected boolean valueChanged(T oldValue, T newValue) {
            return !Objects.equals(oldValue, newValue);
        }
    }

    // Input cells: Value is set externally
    public static class InputCell<T> extends Cell<T> {

        // Constructor to initialize the input cell with a value
        public InputCell(T initialValue) {
            this.value = initialValue;
            // Input cells don't participate in wave tracking for their own value
        }

        @Override
        public T getValue() {
            // Simply return the stored value
            return this.value;
        }

        // Method to update the value of the input cell
        public void setValue(T newValue) {
            // Only proceed if the value has actually changed
            if (valueChanged(this.value, newValue)) {
                this.value = newValue;
                // Start a new update wave *before* notifying listeners
                React.startUpdateWave();
                // Notify dependent compute cells, starting the propagation cascade
                notifyListeners();
            }
        }
    }

    // Compute cells: Value is computed based on other cells (dependencies)
    public static class ComputeCell<T> extends Cell<T> implements Observer<T> {
        private final Function<List<T>, T> computer;
        private final List<Cell<T>> dependencies;
        private final List<Consumer<T>> callbacks = new ArrayList<>();
        // Stores the value that was last reported via callbacks
        private T previousValueForCallback;
        // Tracks the last update wave this cell was recomputed in
        private int lastUpdateWave = -1;

        // Constructor for compute cell
        public ComputeCell(Function<List<T>, T> function, List<Cell<T>> cells) {
            this.computer = function;
            // Store a copy of the dependencies list
            this.dependencies = new ArrayList<>(cells);

            // Register this compute cell as a listener to all its dependencies
            for (Cell<T> dep : this.dependencies) {
                dep.addListener(this);
            }

            // Compute the initial value. Dependencies should be stable initially.
            // We use getValue() on dependencies here to establish initial links if needed,
            // although in a simple setup, they might already have values.
            this.value = computeValueInternal();
            // Set the initial baseline for callback change detection
            this.previousValueForCallback = this.value;
            // Mark as computed in the initial state (wave 0)
            this.lastUpdateWave = React.getCurrentUpdateWave();
        }

        @Override
        public T getValue() {
            // Ensure the cell's value is up-to-date with the current wave.
            // If this cell's last update was before the current global wave,
            // it means an upstream change occurred, and we need to recompute.
            if (this.lastUpdateWave < React.getCurrentUpdateWave()) {
                 recomputeIfNeeded();
            }
            // Return the potentially updated value
            return this.value;
        }

        // This method is called by dependencies when their value changes (via notifyListeners)
        @Override
        public void update(Observable<T> source) {
             // A dependency changed. This cell *might* need to recompute.
             // We trigger recomputation eagerly to propagate changes down the chain.
             // The recomputeIfNeeded method handles ensuring it only happens once per wave.
             recomputeIfNeeded();
        }

        // Central recomputation logic, ensures computation happens at most once per wave
        private void recomputeIfNeeded() {
            // If already computed or checked in the current wave, do nothing more.
            // This prevents redundant computations and breaks potential cycles if getValue was called recursively.
            if (this.lastUpdateWave == React.getCurrentUpdateWave()) {
                return;
            }

            // Perform the computation using dependency values.
            // Calling getValue() on dependencies ensures they are also brought up-to-date
            // with the current wave recursively before their value is used.
            T newValue = computeValueInternal();

            // Mark this cell as updated in this wave *before* notifying downstream cells.
            // This is crucial to prevent infinite loops in case of cyclic dependencies (though not expected by tests)
            // and to ensure that subsequent calls to getValue() or update() within the same wave don't recompute again.
            this.lastUpdateWave = React.getCurrentUpdateWave();

            // Check if the value actually changed compared to the last computed value
            if (valueChanged(this.value, newValue)) {
                // Store the new value
                this.value = newValue;

                // Notify downstream cells *after* updating value and wave marker
                // This continues the propagation wave.
                notifyListeners();

                // Now, check if this new value warrants triggering callbacks.
                // This compares the new value against the last value that *successfully* triggered a callback.
                if (valueChanged(this.previousValueForCallback, this.value)) {
                    // Trigger the callbacks with the new value
                    triggerCallbacks();
                    // Update the baseline for the next callback change detection
                    this.previousValueForCallback = this.value;
                }
            }
            // If valueChanged(this.value, newValue) is false, we do nothing further.
            // The cell is marked as updated in this wave (lastUpdateWave is set),
            // but its value didn't change, so no notifications or callbacks are sent.
        }

        // Computes the cell's value using the provided function and current dependency values
        private T computeValueInternal() {
            // Ensure dependencies are up-to-date by calling their getValue()
            List<T> dependencyValues = dependencies.stream()
                                                  .map(Cell::getValue) // getValue() triggers their recomputeIfNeeded if necessary
                                                  .collect(Collectors.toList());
            return computer.apply(dependencyValues);
        }

        // Adds a callback to be notified of changes
        public void addCallback(Consumer<T> callback) {
            callbacks.add(callback);
        }

        // Removes a previously added callback
        public void removeCallback(Consumer<T> callback) {
            callbacks.remove(callback);
        }

        // Executes all registered callbacks with the current value
        private void triggerCallbacks() {
            // Iterate over a copy in case a callback modifies the list
            List<Consumer<T>> callbacksCopy = new ArrayList<>(callbacks);
            for (Consumer<T> callback : callbacksCopy) {
                try {
                    // Pass the current, stable value for this wave to the callback
                    callback.accept(this.value);
                } catch (Exception e) {
                    // Optional: Log or handle exceptions thrown by callbacks
                    System.err.println("Callback execution failed: " + e.getMessage());
                    e.printStackTrace(); // Print stack trace for debugging
                }
            }
        }
    }

    // Factory method to create an InputCell
    public static <T> InputCell<T> inputCell(T initialValue) {
        return new InputCell<>(initialValue);
    }

    // Factory method to create a ComputeCell
    public static <T> ComputeCell<T> computeCell(Function<List<T>, T> function, List<Cell<T>> cells) {
        return new ComputeCell<>(function, cells);
    }
}
