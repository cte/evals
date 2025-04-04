import collections

class School:
    def __init__(self):
        self._roster = collections.defaultdict(list)
        self._added_log = []
        self._all_students = set() # Helper set to quickly check if a student exists

    def add_student(self, name, grade):
        if name in self._all_students:
            self._added_log.append(False)
            return False
        else:
            self._roster[grade].append(name)
            self._all_students.add(name)
            self._added_log.append(True)
            return True

    def roster(self):
        full_roster = []
        for grade in sorted(self._roster.keys()):
            full_roster.extend(sorted(self._roster[grade]))
        return full_roster

    def grade(self, grade_number):
        return sorted(self._roster.get(grade_number, []))

    def added(self):
        return self._added_log
