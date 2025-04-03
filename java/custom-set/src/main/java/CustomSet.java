import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class CustomSet<T> {
    private final Set<T> elements;

    CustomSet() {
        this.elements = new HashSet<>();
    }

    CustomSet(Collection<T> data) {
        this.elements = new HashSet<>(data);
    }

    boolean isEmpty() {
        return elements.isEmpty();
    }

    boolean contains(T element) {
        return elements.contains(element);
    }

    boolean isDisjoint(CustomSet<T> other) {
        for (T elem : elements) {
            if (other.elements.contains(elem)) {
                return false;
            }
        }
        return true;
    }

    boolean add(T element) {
        return elements.add(element);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CustomSet)) return false;
        CustomSet<?> other = (CustomSet<?>) obj;
        return this.elements.equals(other.elements);
    }

    CustomSet<T> getIntersection(CustomSet<T> other) {
        Set<T> intersection = new HashSet<>(this.elements);
        intersection.retainAll(other.elements);
        return new CustomSet<>(intersection);
    }

    CustomSet<T> getUnion(CustomSet<T> other) {
        Set<T> union = new HashSet<>(this.elements);
        union.addAll(other.elements);
        return new CustomSet<>(union);
    }

    CustomSet<T> getDifference(CustomSet<T> other) {
        Set<T> difference = new HashSet<>(this.elements);
        difference.removeAll(other.elements);
        return new CustomSet<>(difference);
    }

    boolean isSubset(CustomSet<T> other) {
        return this.elements.containsAll(other.elements);
    }
}
