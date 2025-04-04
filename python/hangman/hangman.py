# Game status categories
# Change the values as you see fit
STATUS_WIN = 'win'
STATUS_LOSE = 'lose'
STATUS_ONGOING = 'ongoing'


class Hangman:
    def __init__(self, word):
        self.word = word.lower()
        self.remaining_guesses = 9
        self.status = STATUS_ONGOING
        self.guessed_chars = set()

    def guess(self, char):
        char = char.lower()

        if self.status != STATUS_ONGOING:
            raise ValueError("The game has already ended.")

        if char in self.guessed_chars:
            # Repeated guess (correct or incorrect) - penalize as per test
            self.remaining_guesses -= 1
        elif char in self.word:
            # New correct guess
            self.guessed_chars.add(char)
        else:
            # New incorrect guess
            self.guessed_chars.add(char) # Still record the incorrect guess
            self.remaining_guesses -= 1

        # Update status *after* processing the guess
        if all(c in self.guessed_chars for c in self.word):
            self.status = STATUS_WIN
        elif self.remaining_guesses < 0:
            self.status = STATUS_LOSE
        # else: status remains ONGOING (implicit)


    def get_masked_word(self):
        return "".join([char if char in self.guessed_chars else "_" for char in self.word])

    def get_status(self):
        # Ensure status is updated before returning, in case guess() wasn't called but state implies end
        # Example: Initial word is empty? (Edge case, maybe not tested)
        # Let's re-evaluate status here just in case, although guess() should handle it.
        if self.status == STATUS_ONGOING: # Only check if not already decided
             if all(c in self.guessed_chars for c in self.word):
                 self.status = STATUS_WIN
             elif self.remaining_guesses < 0:
                 self.status = STATUS_LOSE
        return self.status
