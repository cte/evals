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

    // Getters might be useful for testing or inspection, but not strictly required by the problem description.
    // Let's add them just in case.
    public int getA() { return a; }
    public int getB() { return b; }
    public int getC() { return c; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PythagoreanTriplet that = (PythagoreanTriplet) o;
        return a == that.a && b == that.b && c == that.c;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }

    static TripletListBuilder makeTripletsList() {
        return new TripletListBuilder();
    }

    static class TripletListBuilder {
        private int sum;
        // Default maxFactor to a value that likely won't restrict unless explicitly set.
        // Using sum/2 since c must be less than sum/2 for a valid triplet where a+b+c=sum and a<b<c.
        // However, the problem asks for factors less than or equal to maxFactor.
        // Let's default it higher, maybe Integer.MAX_VALUE, or just the sum itself.
        // Let's start with sum as the default upper bound for c.
        private int maxFactor = Integer.MAX_VALUE; // Default to no upper limit unless specified

        TripletListBuilder thatSumTo(int sum) {
            this.sum = sum;
            // If maxFactor hasn't been explicitly set, maybe default it based on sum?
            // Let's keep the default as MAX_VALUE unless withFactorsLessThanOrEqualTo is called.
            return this;
        }

        TripletListBuilder withFactorsLessThanOrEqualTo(int maxFactor) {
            this.maxFactor = maxFactor;
            return this;
        }

        List<PythagoreanTriplet> build() {
            List<PythagoreanTriplet> triplets = new ArrayList<>();
            // Iterate through possible values for a and b.
            // a must be less than sum / 3 because a < b < c and a + b + c = sum => 3a < a + b + c = sum
            for (int a = 1; a < sum / 3; a++) {
                // b must be greater than a.
                // Also, b < c => b < sum - a - b => 2b < sum - a => b < (sum - a) / 2
                // We iterate b up to sum/2 and add an explicit check b < c inside.
                for (int b = a + 1; b < sum / 2; b++) { // Iterate b up towards sum/2
                    int c = sum - a - b;

                    // Ensure b < c before checking the Pythagorean condition
                    if (b < c) {
                        // Check Pythagorean condition and maxFactor constraint (only need to check c)
                        if (a * a + b * b == c * c) {
                             // Check if c is within the maxFactor limit. Since a < b < c, only c needs checking.
                            if (c <= this.maxFactor) {
                                 triplets.add(new PythagoreanTriplet(a, b, c));
                            }
                        }
                    }
                }
            }
            return triplets;
        }
    }
}