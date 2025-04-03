# Game status categories
# Change the values as you see fit
STATUS_WIN = 'win'
STATUS_LOSE = 'lose'
STATUS_ONGOING = 'ongoing'


class Hangman:
    def __init__(self, word):
        self.word = word
        self.remaining_guesses = 9
        self.status = STATUS_ONGOING
        self.guessed_letters = set()

    def guess(self, char):
        if self.status != STATUS_ONGOING:
            raise ValueError("The game has already ended.")

        if char in self.guessed_letters:
            self.remaining_guesses -= 1
        elif char in self.word:
            self.guessed_letters.add(char)
        else:
            self.guessed_letters.add(char)
            self.remaining_guesses -= 1

        # After processing the guess, update status
        if all(c in self.guessed_letters for c in set(self.word)):
            self.status = STATUS_WIN
        elif self.remaining_guesses < 0:
            # Losing only if not all letters guessed
            if all(c in self.guessed_letters for c in set(self.word)):
                self.status = STATUS_WIN
            else:
                self.status = STATUS_LOSE

    def get_masked_word(self):
        return ''.join(c if c in self.guessed_letters else '_' for c in self.word)

    def get_status(self):
        return self.status
