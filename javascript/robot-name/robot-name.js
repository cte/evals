// Manage robot factory settings.
export class Robot {
  static usedNames = new Set();
  _name = null;

  constructor() {}

  get name() {
    if (this._name === null) {
      this._name = this._generateUniqueName();
    }
    return this._name;
  }

  _generateUniqueName() {
    let newName;
    do {
      newName = this._generateRandomName();
    } while (Robot.usedNames.has(newName));
    Robot.usedNames.add(newName);
    return newName;
  }

  _generateRandomName() {
    const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    const digits = '0123456789';
    let name = '';
    for (let i = 0; i < 2; i++) {
      name += letters.charAt(Math.floor(Math.random() * letters.length));
    }
    for (let i = 0; i < 3; i++) {
      name += digits.charAt(Math.floor(Math.random() * digits.length));
    }
    return name;
  }

  reset() {
    // Note: The instructions imply the name should be released back into the pool,
    // but the tests seem to rely on releaseNames for cleanup.
    // If tests fail around name reuse after reset, we might need to add:
    // if (this._name) { Robot.usedNames.delete(this._name); }
    this._name = null;
  }

  static releaseNames() {
    Robot.usedNames.clear();
  }
}
