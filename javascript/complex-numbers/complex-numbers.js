//
// This is only a SKELETON file for the 'Complex Numbers' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class ComplexNumber {
  constructor(real, imag) {
    this._real = real;
    this._imag = imag;
    Object.defineProperty(this, 'conj', {
      get: () => {
        if (this._imag === 0) {
          return this;
        }
        return new ComplexNumber(this._real, -this._imag);
      },
      enumerable: false,
    });

    Object.defineProperty(this, 'exp', {
      get: () => {
        const expReal = Math.exp(this._real);
        return new ComplexNumber(
          expReal * Math.cos(this._imag),
          expReal * Math.sin(this._imag)
        );
      },
      enumerable: false,
    });
}

  get real() {
    return this._real;
  }

  get imag() {
    return this._imag;
  }

  add(other) {
    return new ComplexNumber(this._real + other.real, this._imag + other.imag);
  }

  toJSON() {
    return { real: this.real, imag: this.imag };
  }

  sub(other) {
    return new ComplexNumber(this._real - other.real, this._imag - other.imag);
  }

  mul(other) {
    const real = this._real * other.real - this._imag * other.imag;
    const imag = this._real * other.imag + this._imag * other.real;
    return new ComplexNumber(real, imag);
  }

  div(other) {
    const denominator = other.real ** 2 + other.imag ** 2;
    const real = (this._real * other.real + this._imag * other.imag) / denominator;
    const imag = (this._imag * other.real - this._real * other.imag) / denominator;
    return new ComplexNumber(real, imag);
  }

  get abs() {
    return Math.hypot(this._real, this._imag);
  }

  valueOf() {
    return { real: this.real, imag: this.imag };
  }
  conj() {
    return new ComplexNumber(this._real, -this._imag);
  }

  exp() {
    const expReal = Math.exp(this._real);
    return new ComplexNumber(
      expReal * Math.cos(this._imag),
      expReal * Math.sin(this._imag)
    );
  }
}
