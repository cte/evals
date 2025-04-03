import random

class Robot:
    _used_names = set()

    def __init__(self):
        self.name = self._generate_unique_name()

    def reset(self):
        old_name = self.name
        new_name = self._generate_unique_name(exclude=old_name)
        self.name = new_name

    @classmethod
    def _generate_unique_name(cls, exclude=None):
        while True:
            name = cls._generate_name()
            if (name not in cls._used_names) and (name != exclude):
                cls._used_names.add(name)
                return name

    @staticmethod
    def _generate_name():
        letters = ''.join(random.choices('ABCDEFGHIJKLMNOPQRSTUVWXYZ', k=2))
        digits = ''.join(random.choices('0123456789', k=3))
        return f"{letters}{digits}"
