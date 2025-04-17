export class GradeSchool {
  constructor() {
    this._db = {};
  }

  roster() {
    // Return a deep copy with sorted arrays to prevent external mutation
    const copy = {};
    for (const grade in this._db) {
      copy[grade] = [...this._db[grade]].sort();
    }
    return copy;
  }

  add(name, grade) {
    // Remove student from any existing grade
    for (const g in this._db) {
      this._db[g] = this._db[g].filter(student => student !== name);
      // Clean up empty grades
      if (this._db[g].length === 0) {
        delete this._db[g];
      }
    }

    // Add student to the new grade
    if (!this._db[grade]) {
      this._db[grade] = [];
    }
    this._db[grade].push(name);
    this._db[grade].sort();
  }

  grade(gradeNumber) {
    if (!this._db[gradeNumber]) {
      return [];
    }
    // Return a sorted copy to prevent external mutation
    return [...this._db[gradeNumber]].sort();
  }
}
