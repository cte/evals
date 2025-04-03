// This is only a SKELETON file for the 'Robot Name' exercise. It's been
// provided as a convenience to get your started writing code faster.

export class Robot {
  static assignedNames = new Set();

  static generateName() {
    const letters = () =>
      String.fromCharCode(65 + Math.floor(Math.random() * 26)) +
      String.fromCharCode(65 + Math.floor(Math.random() * 26));
    const digits = () =>
      String(Math.floor(Math.random() * 1000)).padStart(3, '0');

    let name;
    do {
      name = `${letters()}${digits()}`;
    } while (Robot.assignedNames.has(name));
    Robot.assignedNames.add(name);
    return name;
  }

  constructor() {
    this._name = Robot.generateName();
    Object.defineProperty(this, 'name', {
      get: () => this._name,
      set: () => {
        throw new Error('Cannot modify robot name');
      },
      configurable: false,
      enumerable: true,
    });
  }

  reset() {
    let newName = Robot.generateName();
    this._name = newName;
  }

  static releaseNames() {
    Robot.assignedNames.clear();
  }
}
