import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class PythagoreanTriplet {
    private final int a;
    private final int b;
    private final int c;

    PythagoreanTriplet(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    static TripletListBuilder makeTripletsList() {
        return new TripletListBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PythagoreanTriplet)) return false;
        PythagoreanTriplet that = (PythagoreanTriplet) o;
        return a == that.a && b == that.b && c == that.c;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }

    @Override
    public String toString() {
        return String.format("PythagoreanTriplet(%d, %d, %d)", a, b, c);
    }

    static class TripletListBuilder {
        private Integer maxFactor = null;
        private int sum = 0;

        TripletListBuilder withFactorsLessThanOrEqualTo(int maxFactor) {
            this.maxFactor = maxFactor;
            return this;
        }

        TripletListBuilder thatSumTo(int sum) {
            this.sum = sum;
            return this;
        }

        List<PythagoreanTriplet> build() {
            List<PythagoreanTriplet> triplets = new ArrayList<>();
            int maxA = sum / 3;
            for (int a = 1; a <= maxA; a++) {
                int maxB = (sum - a) / 2;
                for (int b = a; b <= maxB; b++) {
                    int c = sum - a - b;
                    if (c <= 0) continue;
                    if (a * a + b * b == c * c) {
                        if (maxFactor != null) {
                            if (a > maxFactor || b > maxFactor || c > maxFactor) continue;
                        }
                        triplets.add(new PythagoreanTriplet(a, b, c));
                    }
                }
            }
            return triplets;
        }
    }
}