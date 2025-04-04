import java.util.Objects;
import java.lang.Math; // Import Math for abs and pow

class Rational {

    private final int numerator;
    private final int denominator;

    Rational(int numerator, int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero.");
        }

        // Calculate GCD
        int commonDivisor = gcd(Math.abs(numerator), Math.abs(denominator));

        // Reduce to lowest terms
        int reducedNumerator = numerator / commonDivisor;
        int reducedDenominator = denominator / commonDivisor;

        // Ensure standard form (positive denominator)
        if (reducedDenominator < 0) {
            this.numerator = -reducedNumerator;
            this.denominator = -reducedDenominator;
        } else {
            this.numerator = reducedNumerator;
            this.denominator = reducedDenominator;
        }
    }

    // Euclidean algorithm for GCD
    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        // Ensure GCD is positive for normalization
        return Math.abs(a);
    }

    int getNumerator() {
        return this.numerator;
    }

    int getDenominator() {
        return this.denominator;
    }

    Rational add(Rational other) {
        // (a1 * b2 + a2 * b1) / (b1 * b2)
        // Use long for intermediate calculations to prevent overflow
        long newNumerator = (long)this.numerator * other.denominator + (long)other.numerator * this.denominator;
        long newDenominator = (long)this.denominator * other.denominator;
        // Check for potential overflow before casting back to int - though constructor handles reduction
        if (newNumerator > Integer.MAX_VALUE || newNumerator < Integer.MIN_VALUE ||
            newDenominator > Integer.MAX_VALUE || newDenominator < Integer.MIN_VALUE) {
             throw new ArithmeticException("Integer overflow during addition.");
        }
        return new Rational((int)newNumerator, (int)newDenominator);
    }

    Rational subtract(Rational other) {
        // (a1 * b2 - a2 * b1) / (b1 * b2)
        long newNumerator = (long)this.numerator * other.denominator - (long)other.numerator * this.denominator;
        long newDenominator = (long)this.denominator * other.denominator;
        if (newNumerator > Integer.MAX_VALUE || newNumerator < Integer.MIN_VALUE ||
            newDenominator > Integer.MAX_VALUE || newDenominator < Integer.MIN_VALUE) {
             throw new ArithmeticException("Integer overflow during subtraction.");
        }
        return new Rational((int)newNumerator, (int)newDenominator);
    }

    Rational multiply(Rational other) {
        // (a1 * a2) / (b1 * b2)
        long newNumerator = (long)this.numerator * other.numerator;
        long newDenominator = (long)this.denominator * other.denominator;
         if (newNumerator > Integer.MAX_VALUE || newNumerator < Integer.MIN_VALUE ||
            newDenominator > Integer.MAX_VALUE || newDenominator < Integer.MIN_VALUE) {
             throw new ArithmeticException("Integer overflow during multiplication.");
        }
        return new Rational((int)newNumerator, (int)newDenominator);
    }

    Rational divide(Rational other) {
        // (a1 * b2) / (a2 * b1)
        if (other.numerator == 0) {
            throw new ArithmeticException("Cannot divide by zero rational number.");
        }
        long newNumerator = (long)this.numerator * other.denominator;
        long newDenominator = (long)this.denominator * other.numerator;
         if (newNumerator > Integer.MAX_VALUE || newNumerator < Integer.MIN_VALUE ||
            newDenominator > Integer.MAX_VALUE || newDenominator < Integer.MIN_VALUE) {
             throw new ArithmeticException("Integer overflow during division.");
        }
        return new Rational((int)newNumerator, (int)newDenominator);
    }

    Rational abs() {
        return new Rational(Math.abs(this.numerator), this.denominator); // Denominator is already positive
    }

    Rational pow(int power) {
        // Use double for intermediate calculations to avoid potential int overflow with Math.pow
        double newNumeratorDbl;
        double newDenominatorDbl;
        if (power >= 0) {
            newNumeratorDbl = Math.pow(this.numerator, power);
            newDenominatorDbl = Math.pow(this.denominator, power);
        } else {
            // r^n = (b^m)/(a^m), where m = |n|
            int m = Math.abs(power);
            if (this.numerator == 0) {
                 throw new ArithmeticException("Cannot raise zero to a negative power.");
            }
            // Denominator becomes numerator^m, Numerator becomes denominator^m
            newNumeratorDbl = Math.pow(this.denominator, m);
            newDenominatorDbl = Math.pow(this.numerator, m);
            // Sign normalization is handled by the constructor
        }

        // Check for potential overflow or loss of precision before casting
        if (newNumeratorDbl > Integer.MAX_VALUE || newNumeratorDbl < Integer.MIN_VALUE ||
            newDenominatorDbl > Integer.MAX_VALUE || newDenominatorDbl < Integer.MIN_VALUE ||
            Double.isInfinite(newNumeratorDbl) || Double.isInfinite(newDenominatorDbl) ||
            Double.isNaN(newNumeratorDbl) || Double.isNaN(newDenominatorDbl)) {
             // Check if the result should be 0/1 or handle as error
             if (newNumeratorDbl == 0 && newDenominatorDbl != 0 && !Double.isNaN(newDenominatorDbl) && !Double.isInfinite(newDenominatorDbl)) {
                 // Allow 0 numerator if denominator is valid
             } else {
                throw new ArithmeticException("Exponentiation resulted in overflow, infinity, or NaN.");
             }
        }

        // The constructor will handle reduction and sign normalization
        // Cast carefully after checks
        int finalNumerator = (int) newNumeratorDbl;
        int finalDenominator = (int) newDenominatorDbl;

        // Handle case where result is exactly zero
        if (newNumeratorDbl == 0) {
            finalNumerator = 0;
            finalDenominator = 1; // Rational zero is 0/1
        }


        return new Rational(finalNumerator, finalDenominator);
    }

    // This method implements "exponentiation of a real number to a rational number"
    // as required by the instructions and tests.
    // Tests call it as: rationalInstance.exp(base)
    // It calculates: base ^ rationalInstance (base ^ (numerator/denominator))
    double exp(double base) {
         // Handle negative base with fractional exponent where denominator is even
         // (results in complex number - return NaN as per standard Math.pow behavior)
         if (base < 0 && this.denominator % 2 == 0) {
             // Check if numerator is also even, e.g., (-1)^(2/2) = (-1)^1 = -1
             // Check if numerator is odd, e.g., (-1)^(1/2) = i (NaN)
             if (this.numerator % 2 != 0) {
                return Double.NaN;
             }
             // If numerator is even, the result might be real, let Math.pow handle it.
             // Example: (-8)^(2/6) = (-8)^(1/3) = -2. Math.pow handles this.
         }

         // Calculate base^(numerator/denominator) using Math.pow
         // Calculate root first, then power, for potentially better precision
         double root = Math.pow(base, 1.0 / this.denominator);
         return Math.pow(root, this.numerator);
    }

    @Override
    public String toString() {
        return String.format("%d/%d", this.getNumerator(), this.getDenominator());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Optimization
        if (obj instanceof Rational other) {
            // Already reduced and normalized in constructor, so direct comparison works
            return this.numerator == other.numerator
                    && this.denominator == other.denominator;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // Use Objects.hash for better hash code generation
        return Objects.hash(this.numerator, this.denominator);
    }
}
