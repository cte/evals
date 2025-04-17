import java.util.Objects;

class Rational {

    private final int numerator;
    private final int denominator;

    public Rational(int numerator, int denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("Denominator cannot be zero");
        }

        // Normalize sign: denominator always positive
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        int gcd = gcd(Math.abs(numerator), denominator);
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    int getNumerator() {
        return numerator;
    }

    int getDenominator() {
        return denominator;
    }

    Rational add(Rational other) {
        int num = this.numerator * other.denominator + other.numerator * this.denominator;
        int den = this.denominator * other.denominator;
        return new Rational(num, den);
    }

    Rational subtract(Rational other) {
        int num = this.numerator * other.denominator - other.numerator * this.denominator;
        int den = this.denominator * other.denominator;
        return new Rational(num, den);
    }

    Rational multiply(Rational other) {
        int num = this.numerator * other.numerator;
        int den = this.denominator * other.denominator;
        return new Rational(num, den);
    }

    Rational divide(Rational other) {
        if (other.numerator == 0) {
            throw new ArithmeticException("Division by zero");
        }
        int num = this.numerator * other.denominator;
        int den = this.denominator * other.numerator;
        return new Rational(num, den);
    }

    Rational abs() {
        return new Rational(Math.abs(numerator), denominator);
    }

    Rational pow(int power) {
        if (power == 0) {
            return new Rational(1,1);
        } else if (power > 0) {
            return new Rational(
                (int) Math.pow(this.numerator, power),
                (int) Math.pow(this.denominator, power)
            );
        } else { // negative power
            return new Rational(
                (int) Math.pow(this.denominator, -power),
                (int) Math.pow(this.numerator, -power)
            );
        }
    }

    double exp(double base) {
        return Math.pow(base, (double) this.numerator / this.denominator);
    }

    @Override
    public String toString() {
        return String.format("%d/%d", this.getNumerator(), this.getDenominator());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rational other) {
            return this.getNumerator() == other.getNumerator()
                    && this.getDenominator() == other.getDenominator();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getNumerator(), this.getDenominator());
    }
}
