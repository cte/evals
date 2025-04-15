export class GradeSchool {
  constructor() {
    this._roster = {};
  }

  roster() {
    // Create a deep copy to prevent external modification
    const rosterCopy = {};
    for (const grade in this._roster) {
      rosterCopy[grade] = [...this._roster[grade]]; // Copy array for each grade
    }
    return rosterCopy;
  }

  add(name, grade) {
    // Remove student from any existing grade first
    for (const existingGrade in this._roster) {
      const studentIndex = this._roster[existingGrade].indexOf(name);
      if (studentIndex !== -1) {
        this._roster[existingGrade].splice(studentIndex, 1);
        // Optional: Clean up empty grade arrays if needed, though tests don't require it
        // if (this._roster[existingGrade].length === 0) {
        //   delete this._roster[existingGrade];
        // }
      }
    }

    // Add student to the new grade
    if (!this._roster[grade]) {
      this._roster[grade] = [];
    }
    this._roster[grade].push(name);
    this._roster[grade].sort(); // Keep the grade list sorted
  }

  grade(gradeNum) {
    // Return a copy of the grade list, or an empty array
    return this._roster[gradeNum] ? [...this._roster[gradeNum]] : [];
  }
}
