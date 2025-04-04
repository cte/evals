// complex-numbers.js
export class ComplexNumber {
  constructor(real, imag) {
    this._real = real;
    this._imag = imag;
  }

  get real() {
    return this._real;
  }

  get imag() {
    return this._imag;
  }

  add(other) {
    // (a + i * b) + (c + i * d) = (a + c) + (b + d) * i
    const real = this.real + other.real;
    const imag = this.imag + other.imag;
    return new ComplexNumber(real, imag);
  }

  sub(other) {
    // (a + i * b) - (c + i * d) = (a - c) + (b - d) * i
    const real = this.real - other.real;
    const imag = this.imag - other.imag;
    return new ComplexNumber(real, imag);
  }

  div(other) {
    // (a + i * b) / (c + i * d) = (a * c + b * d)/(c^2 + d^2) + (b * c - a * d)/(c^2 + d^2) * i
    const denominator = other.real ** 2 + other.imag ** 2;
    const real = (this.real * other.real + this.imag * other.imag) / denominator;
    const imag = (this.imag * other.real - this.real * other.imag) / denominator;
    return new ComplexNumber(real, imag);
  }

  mul(other) {
    // (a + i * b) * (c + i * d) = (a * c - b * d) + (b * c + a * d) * i
    const real = this.real * other.real - this.imag * other.imag;
    const imag = this.imag * other.real + this.real * other.imag;
    return new ComplexNumber(real, imag);
  }

  get abs() {
    // |z| = sqrt(a^2 + b^2)
    return Math.sqrt(this.real ** 2 + this.imag ** 2);
  }

  get conj() {
    // a - b * i
    return new ComplexNumber(this.real, -this.imag || 0); // Handle -0 case
  }

  get exp() {
    // e^(a + i * b) = e^a * e^(i * b) = e^a * (cos(b) + i * sin(b))
    const expA = Math.exp(this.real);
    const real = expA * Math.cos(this.imag);
    const imag = expA * Math.sin(this.imag);
    return new ComplexNumber(real, imag);
  }
}
