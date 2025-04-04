import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class CustomSet<T> {

    private Set<T> elements;

    CustomSet() {
        this.elements = new HashSet<>();
    }

    CustomSet(Collection<T> data) {
        this.elements = new HashSet<>(data);
    }

    boolean isEmpty() {
        return this.elements.isEmpty();
    }

    boolean contains(T element) {
        return this.elements.contains(element);
    }

    // Checks if this set has no elements in common with the other set.
    boolean isDisjoint(CustomSet<T> other) {
        if (other == null) {
            return true; // Or throw an exception, depending on requirements. Assuming disjoint.
        }
        for (T element : this.elements) {
            if (other.contains(element)) {
                return false;
            }
        }
        return true;
        // Alternative using streams (potentially less efficient for small sets due to overhead):
        // return this.elements.stream().noneMatch(other.elements::contains);
        // Alternative using Collections.disjoint (requires access to other.elements):
        // return Collections.disjoint(this.elements, other.elements);
    }

    // Adds an element. Returns true if the set did not already contain the element.
    boolean add(T element) {
        return this.elements.add(element);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CustomSet<?> other = (CustomSet<?>) obj;
        // Two sets are equal if they have the same size and contain the same elements.
        // The second condition (containsAll) implies the first (same size) for sets.
        return this.elements.equals(other.elements);
    }

    @Override
    public int hashCode() {
        // It's good practice to override hashCode when overriding equals.
        return this.elements.hashCode();
    }

    // Returns a new set with elements common to both sets.
    CustomSet<T> getIntersection(CustomSet<T> other) {
        if (other == null) {
            return new CustomSet<>(); // Intersection with null is empty.
        }
        Set<T> intersectionElements = this.elements.stream()
                .filter(other.elements::contains)
                .collect(Collectors.toSet());
        return new CustomSet<>(intersectionElements);
    }

    // Returns a new set with all unique elements from both sets.
    CustomSet<T> getUnion(CustomSet<T> other) {
        CustomSet<T> unionSet = new CustomSet<>(this.elements);
        if (other != null) {
            unionSet.elements.addAll(other.elements);
        }
        return unionSet;
    }

    // Returns a new set with elements in this set but not in the other set.
    CustomSet<T> getDifference(CustomSet<T> other) {
        if (other == null) {
            return new CustomSet<>(this.elements); // Difference with null is the set itself.
        }
        Set<T> differenceElements = this.elements.stream()
                .filter(element -> !other.contains(element))
                .collect(Collectors.toSet());
        return new CustomSet<>(differenceElements);
    }

    // Checks if the other set is a subset of this set (i.e., all elements of 'other' are in 'this').
    boolean isSubset(CustomSet<T> other) {
        if (other == null) {
            return true; // The empty set (represented by null or an empty CustomSet) is a subset of any set.
                         // If other is null, let's consider it represents an empty set for this check.
        }
        return this.elements.containsAll(other.elements);
    }

    // Helper for tests or debugging, not strictly required by the interface described
    @Override
    public String toString() {
        return elements.toString();
    }

    // Optional: Provide direct access to underlying elements if needed by some operations
    // or for more optimized inter-set operations if direct access is allowed.
    // Collection<T> getElements() {
    //     return Collections.unmodifiableSet(this.elements);
    // }
}
