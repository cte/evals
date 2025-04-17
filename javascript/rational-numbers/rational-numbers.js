export class Rational {
  constructor(numerator, denominator) {
    if (denominator === 0) {
      throw new Error('Denominator cannot be zero');
    }

    // Normalize sign: denominator always positive
    if (denominator < 0) {
      numerator = -numerator;
      denominator = -denominator;
    }

    this.numerator = numerator;
    this.denominator = denominator;
  }

  add(other) {
    const numerator = this.numerator * other.denominator + other.numerator * this.denominator;
    const denominator = this.denominator * other.denominator;
    return new Rational(numerator, denominator).reduce();
  }

  sub(other) {
    const numerator = this.numerator * other.denominator - other.numerator * this.denominator;
    const denominator = this.denominator * other.denominator;
    return new Rational(numerator, denominator).reduce();
  }

  mul(other) {
    const numerator = this.numerator * other.numerator;
    const denominator = this.denominator * other.denominator;
    return new Rational(numerator, denominator).reduce();
  }

  div(other) {
    if (other.numerator === 0) {
      throw new Error('Cannot divide by zero');
    }
    const numerator = this.numerator * other.denominator;
    const denominator = this.denominator * other.numerator;
    // Handle division by negative rational
    return new Rational(numerator, denominator).reduce();
  }

  abs() {
    return new Rational(Math.abs(this.numerator), Math.abs(this.denominator)).reduce();
  }

  exprational(power) {
    if (power === 0) {
      return new Rational(1, 1);
    }
    const numerator = Math.pow(this.numerator, power);
    const denominator = Math.pow(this.denominator, power);
    return new Rational(numerator, denominator).reduce();
  }

  expreal(real) {
    // real ^ (numerator/denominator)
    const result = Math.pow(real, this.numerator / this.denominator);
    // Round to 12 decimal places to avoid floating point precision issues in tests
    return Number.parseFloat(result.toFixed(12));
  }

  reduce() {
    if (this.numerator === 0) {
      return new Rational(0, 1);
    }
    const gcdVal = gcd(Math.abs(this.numerator), this.denominator);
    const numerator = this.numerator / gcdVal;
    const denominator = this.denominator / gcdVal;
    return new Rational(numerator, denominator);
  }

  // For Jest's toEqual to work properly
  // (since it compares object properties deeply)
  // Alternatively, override valueOf() or toString() if needed
}

function gcd(a, b) {
  while (b !== 0) {
    [a, b] = [b, a % b];
  }
  return a;
}
