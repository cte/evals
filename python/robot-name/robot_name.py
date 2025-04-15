import random
import string

class Robot:
    used_names = set()

    def __init__(self):
        self._name = self._assign_new_name()

    def _generate_candidate_name(self):
        """Generates a random robot name candidate."""
        # Use random.choices for potentially better performance/readability
        letters = "".join(random.choices(string.ascii_uppercase, k=2))
        digits = "".join(random.choices(string.digits, k=3))
        return letters + digits

    def _assign_new_name(self, name_to_avoid=None):
        """Generates and assigns a unique name, avoiding a specific name if provided."""
        while True:
            name = self._generate_candidate_name()
            # Ensure the generated name is not the one we explicitly want to avoid
            # AND it's not already in the global used set.
            if name != name_to_avoid and name not in Robot.used_names:
                Robot.used_names.add(name)
                return name

    def reset(self):
        """Resets the robot's name to a new random unique name."""
        old_name = self._name
        # Remove the old name from the set *before* generating the new one
        Robot.used_names.remove(old_name)
        try:
            # Pass the old name to _assign_new_name to ensure the new name is different,
            # even if the random sequence repeats due to seeding.
            self._name = self._assign_new_name(name_to_avoid=old_name)
        except Exception as e:
            # If assigning a new name fails for some reason (e.g., namespace exhaustion),
            # add the old one back to maintain the state and prevent inconsistency.
            Robot.used_names.add(old_name)
            raise e # Re-raise the exception

    @property
    def name(self):
        """Returns the robot's current name."""
        return self._name
