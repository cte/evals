class BowlingGame:
    def __init__(self):
        self.rolls = []

    def roll(self, pins):
        if pins < 0 or pins > 10:
            raise Exception("Invalid number of pins")

        # Determine current frame
        frame_index = 0
        i = 0
        while i < len(self.rolls) and frame_index < 9:
            if self.rolls[i] == 10:
                i += 1
            else:
                i += 2
            frame_index += 1

        # Validation for frames 1-9
        if frame_index < 9:
            # Find start index of current frame
            frame_start = 0
            frame = 0
            idx = 0
            while frame < frame_index and idx < len(self.rolls):
                if self.rolls[idx] == 10:
                    idx += 1
                else:
                    idx += 2
                frame += 1
            frame_start = idx

            # If this is the second roll in the current frame, validate sum
            if len(self.rolls) == frame_start + 1:
                first_roll = self.rolls[frame_start]
                if first_roll != 10 and first_roll + pins > 10:
                    raise Exception("Frame pin count exceeds 10")

            if self.is_game_over():
                raise Exception("Cannot roll after game is over")

        # Validation for 10th frame
        if frame_index == 9:
            tenth_frame = self.rolls[i:]
            if len(tenth_frame) == 0:
                pass  # first roll, always valid
            elif len(tenth_frame) == 1:
                first = tenth_frame[0]
                if first != 10 and first + pins > 10:
                    raise Exception("Bonus roll pin count exceeds 10")
            elif len(tenth_frame) == 2:
                first, second = tenth_frame[0], tenth_frame[1]
                if first == 10:
                    # strike in first roll
                    if second != 10 and second + pins > 10:
                        raise Exception("Bonus roll pin count exceeds 10")
                    if second != 10 and pins == 10:
                        raise Exception("Invalid strike after non-strike bonus roll")
                elif first + second == 10:
                    # spare in first two rolls, third roll unrestricted (max 10)
                    pass
                else:
                    # open frame, no third roll allowed
                    raise Exception("No bonus roll allowed")

            # Check if 10th frame is already over
            tenth_len = len(tenth_frame)
            if tenth_len == 3:
                raise Exception("Cannot roll after game is over")

        else:
            # Not in 10th frame, check if game is over before appending
            if self.is_game_over():
                raise Exception("Cannot roll after game is over")

        # Append only after validation
        self.rolls.append(pins)

    def score(self):
        if not self.rolls:
            raise Exception("Game not started")

        score = 0
        roll_index = 0
        for frame in range(10):
            if roll_index >= len(self.rolls):
                raise Exception("Incomplete game")

            if self.rolls[roll_index] == 10:
                if roll_index + 2 >= len(self.rolls):
                    raise Exception("Strike bonus rolls missing")
                score += 10 + self.rolls[roll_index + 1] + self.rolls[roll_index + 2]
                roll_index += 1
            elif roll_index + 1 < len(self.rolls):
                frame_score = self.rolls[roll_index] + self.rolls[roll_index + 1]
                if frame_score > 10:
                    raise Exception("Invalid frame score")
                if frame_score == 10:
                    if roll_index + 2 >= len(self.rolls):
                        raise Exception("Spare bonus roll missing")
                    score += 10 + self.rolls[roll_index + 2]
                else:
                    score += frame_score
                roll_index += 2
            else:
                raise Exception("Incomplete frame")

        if roll_index < len(self.rolls):
            # Parse frames explicitly to identify 10th frame and bonus rolls
            rolls = self.rolls
            frame = 0
            i = 0
            while frame < 9 and i < len(rolls):
                if rolls[i] == 10:
                    i += 1
                else:
                    i += 2
                frame += 1

            # Now at start of 10th frame
            tenth_frame = rolls[i:]
            if len(tenth_frame) < 2:
                raise Exception("Incomplete 10th frame")

            first = tenth_frame[0]
            second = tenth_frame[1] if len(tenth_frame) > 1 else None
            third = tenth_frame[2] if len(tenth_frame) > 2 else None

            if first == 10:
                # strike in first roll
                if second is None or third is None:
                    raise Exception("Strike bonus rolls missing")
                if len(tenth_frame) > 3:
                    raise Exception("Too many bonus rolls after strike in 10th frame")
            elif first + second == 10:
                # spare
                if third is None:
                    raise Exception("Spare bonus roll missing")
                if len(tenth_frame) > 3:
                    raise Exception("Too many rolls after spare in 10th frame")
            else:
                # open frame
                if len(tenth_frame) > 2:
                    raise Exception("Extra rolls after game over")

        return score

    def is_game_over(self):
        rolls = self.rolls
        frame = 0
        i = 0
        while frame < 10 and i < len(rolls):
            if rolls[i] == 10:
                i += 1
            else:
                if i + 1 >= len(rolls):
                    return False
                i += 2
            frame += 1

        if frame < 10:
            return False

        # 10th frame bonus logic
        tenth_frame = rolls[i - 2:]
        if len(tenth_frame) < 2:
            return False  # 10th frame incomplete

        first = tenth_frame[0]
        second = tenth_frame[1] if len(tenth_frame) > 1 else None
        third = tenth_frame[2] if len(tenth_frame) > 2 else None

        if first == 10:
            # strike in first roll
            if second is None or third is None:
                return False
            return True
        elif first + second == 10:
            # spare in first two rolls
            if third is None:
                return False
            return True
        else:
            # open frame, only two rolls allowed
            return True
