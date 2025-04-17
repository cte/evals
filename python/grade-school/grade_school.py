class School:
    def __init__(self):
        self._grades = {}  # grade_number -> set of names
        self._all_students = set()  # all unique student names
        self._add_results = []  # list of bools for add attempts

    def add_student(self, name, grade):
        if name in self._all_students:
            # Student already added somewhere
            self._add_results.append(False)
            return
        # New student
        self._all_students.add(name)
        if grade not in self._grades:
            self._grades[grade] = set()
        self._grades[grade].add(name)
        self._add_results.append(True)

    def roster(self):
        result = []
        for grade in sorted(self._grades):
            result.extend(sorted(self._grades[grade]))
        return result

    def grade(self, grade_number):
        if grade_number not in self._grades:
            return []
        return sorted(self._grades[grade_number])

    def added(self):
        return self._add_results
