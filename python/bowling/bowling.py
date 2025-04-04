class BowlingGame:
    def __init__(self):
        self.rolls = []
        self._MAX_PINS = 10
        self._MAX_FRAMES = 10

    def roll(self, pins):
        if pins < 0:
            raise ValueError("Negative roll is invalid")
        if pins > self._MAX_PINS:
            raise ValueError("Pin count exceeds pins on the lane")

        self._validate_roll(pins)
        self.rolls.append(pins)

    def _validate_roll(self, pins):
        """Validates the current roll based on game state."""
        # 1. Check game over first
        if self._is_game_over():
            raise IndexError("Cannot roll after game is over")

        # 2. Basic pin validation (pins < 0 or pins > 10) is done in roll()

        # 3. Determine frame context
        start_of_10th = self._get_start_of_frame(9)
        current_roll_index = len(self.rolls)
        is_in_or_after_10th = current_roll_index >= start_of_10th

        # 4. Frame 1-9 Validation
        if not is_in_or_after_10th:
            # Simpler check for frames 1-9:
            # Iterate through past rolls to determine if the *last* roll
            # completed a frame or was the first roll of the current frame.
            frame_index = 0
            roll_index = 0
            is_first_roll_of_current_frame = True # Assume start state

            while roll_index < current_roll_index:
                if frame_index >= self._MAX_FRAMES - 1: # Should not be reached if not is_in_or_after_10th
                    break

                if self._is_strike(roll_index):
                    frame_index += 1
                    roll_index += 1
                    is_first_roll_of_current_frame = True # Next roll starts a new frame
                else:
                    # Check if there's a second roll for this frame *already recorded*
                    if roll_index + 1 < current_roll_index:
                        # Frame completed with two rolls
                        frame_index += 1
                        roll_index += 2
                        is_first_roll_of_current_frame = True # Next roll starts a new frame
                    else:
                        # Only one roll recorded for this frame so far (the last one)
                        is_first_roll_of_current_frame = False # The current roll is the second
                        break # Found the state

            # If the last recorded roll was the first of its frame,
            # then the current roll 'pins' is the second roll. Validate it.
            if not is_first_roll_of_current_frame:
                last_roll_pins = self.rolls[current_roll_index - 1]
                if last_roll_pins + pins > self._MAX_PINS:
                     raise ValueError("Pin count exceeds pins on the lane for frame")

        # 5. Frame 10 / Bonus Validation
        else: # is_in_or_after_10th is True
            rolls_in_10th_so_far = self.rolls[start_of_10th:]
            roll_count_10th = len(rolls_in_10th_so_far) # Rolls already made in 10th/bonus

            if roll_count_10th == 0:
                # First roll of 10th frame. No validation needed beyond pins <= 10.
                pass
            elif roll_count_10th == 1:
                # Attempting second roll in 10th.
                first_roll = rolls_in_10th_so_far[0]
                if first_roll != self._MAX_PINS: # If first wasn't a strike
                    if first_roll + pins > self._MAX_PINS:
                        raise ValueError("Pin count exceeds pins on the lane for frame")
            elif roll_count_10th == 2:
                # Attempting third roll (bonus) in 10th.
                first_roll = rolls_in_10th_so_far[0]
                second_roll = rolls_in_10th_so_far[1]
                is_strike_first = (first_roll == self._MAX_PINS)
                is_spare = (not is_strike_first) and (first_roll + second_roll == self._MAX_PINS)

                # Must have had strike or spare to allow a third roll.
                # This is implicitly checked by _is_game_over(), but being explicit helps clarity
                if not is_strike_first and not is_spare:
                     # This path should ideally not be reachable if _is_game_over() is correct
                     raise IndexError("Cannot roll bonus if no strike/spare in 10th frame")

                # Apply bonus roll validation rules:
                if is_strike_first:
                    # Case: 10, X, pins (attempting third roll)
                    if second_roll != self._MAX_PINS: # If second wasn't a strike (e.g., 10, 6, pins)
                        # Test Case 1: 10, 6, 5 -> Error (6+5 > 10)
                        if second_roll + pins > self._MAX_PINS:
                            raise ValueError("Invalid bonus roll: second + third > 10")
                        # Test Case 2: 10, 6, 10 -> Error (Cannot be strike if second wasn't)
                        # This condition (pins == self._MAX_PINS) is implicitly covered by the previous check
                        # because if pins is 10, second_roll + pins will be > 10 unless second_roll is 0.
                        # Let's add an explicit check for clarity and robustness.
                        if pins == self._MAX_PINS:
                             raise ValueError("Cannot roll strike as third roll if second wasn't strike")
                    # else: Case 10, 10, pins. Third roll 'pins' can be anything up to 10. No extra validation needed.
                # else: # Must be a spare (e.g. 7, 3, pins)
                    # Bonus roll 'pins' can be anything up to 10. No extra validation needed here.
                    pass
            # else: roll_count_10th >= 3. Should be caught by _is_game_over() check at the start.
    def score(self):
        if not self._is_game_over():
             raise IndexError("Score cannot be taken until the end of the game")

        total_score = 0
        roll_index = 0
        for frame in range(self._MAX_FRAMES):
            if self._is_strike(roll_index):
                total_score += self._MAX_PINS + self._strike_bonus(roll_index)
                roll_index += 1
            elif self._is_spare(roll_index):
                total_score += self._MAX_PINS + self._spare_bonus(roll_index)
                roll_index += 2
            else:
                total_score += self._frame_score(roll_index)
                roll_index += 2
        return total_score

    def _is_strike(self, roll_index):
        # Check bounds before accessing rolls
        return roll_index < len(self.rolls) and self.rolls[roll_index] == self._MAX_PINS

    def _is_spare(self, roll_index):
        # Check bounds before accessing rolls
        return (roll_index + 1 < len(self.rolls) and
                self.rolls[roll_index] != self._MAX_PINS and # Ensure not a strike
                self.rolls[roll_index] + self.rolls[roll_index + 1] == self._MAX_PINS)

    def _strike_bonus(self, roll_index):
        # Check bounds before accessing rolls
        if roll_index + 2 < len(self.rolls):
            return self.rolls[roll_index + 1] + self.rolls[roll_index + 2]
        # Handle potential incomplete bonus rolls if validation allows (shouldn't with _is_game_over)
        elif roll_index + 1 < len(self.rolls):
             return self.rolls[roll_index + 1] # Should only happen if game isn't over
        return 0 # Should not happen if game is validated correctly before scoring

    def _spare_bonus(self, roll_index):
         # Check bounds before accessing rolls
        if roll_index + 2 < len(self.rolls):
            return self.rolls[roll_index + 2]
        return 0 # Should not happen if game is validated correctly before scoring

    def _frame_score(self, roll_index):
        # Check bounds before accessing rolls
        if roll_index + 1 < len(self.rolls):
            return self.rolls[roll_index] + self.rolls[roll_index + 1]
         # Handle potential incomplete frame if validation allows (shouldn't with _is_game_over)
        elif roll_index < len(self.rolls):
             return self.rolls[roll_index] # Should only happen if game isn't over
        return 0 # Should not happen if game is validated correctly

    def _get_start_of_frame(self, target_frame_index):
        """Finds the roll index corresponding to the start of a given frame index (0-based)."""
        roll_index = 0
        frame_index = 0
        while frame_index < target_frame_index and roll_index < len(self.rolls):
            # Check frame boundary carefully
            current_frame_start_roll_index = roll_index

            if self._is_strike(current_frame_start_roll_index):
                roll_index += 1
            else:
                # Need two rolls for a non-strike frame, check if available
                if roll_index + 1 < len(self.rolls):
                    roll_index += 2
                else:
                    # Incomplete frame encountered before target frame
                    roll_index += 1 # Move past the single roll
                    # This indicates an issue if called when game isn't finished properly
                    # Or if target_frame_index is beyond the current game state
                    break

            frame_index += 1

        # If loop finished because roll_index reached end, but frame_index is still less
        # it means the target frame hasn't started yet.
        if frame_index < target_frame_index:
             # This can happen if called mid-game, return current end index
             # Or if called with invalid target_frame_index
             # For validation purposes, returning len(self.rolls) might be suitable
             # For scoring, this state implies an incomplete game error should have been raised
             pass # Let the caller handle based on context, roll_index holds the current position

        return roll_index


    def _get_current_frame_info(self):
        """ Calculates the current frame index (0-based) and roll within the frame (0 or 1).
            Handles 10th frame logic. Returns frame_index >= 10 if game is over."""
        frame_index = 0
        roll_index = 0
        roll_in_frame = 0 # 0 for first roll, 1 for second
        while roll_index < len(self.rolls):
            if frame_index >= self._MAX_FRAMES:
                 # Processed 10 frames, these are bonus rolls or invalid rolls
                 break

            current_frame_start_roll_index = roll_index

            if self._is_strike(current_frame_start_roll_index):
                frame_index += 1
                roll_index += 1
                roll_in_frame = 0 # Reset for next frame
            else:
                # Non-strike path
                if roll_in_frame == 0:
                    # First roll of a non-strike frame
                    roll_in_frame = 1
                    roll_index += 1
                    # If this is the last roll added, we are mid-frame
                    if roll_index == len(self.rolls):
                         break
                else:
                    # Second roll of a non-strike frame
                    frame_index += 1
                    roll_index += 1
                    roll_in_frame = 0 # Reset for next frame

        # If frame_index is exactly 9, and we are on roll_in_frame 0, it means we are about to start frame 10.
        # If frame_index is 9 and roll_in_frame is 1, we are in the middle of frame 10.
        # If frame_index becomes 10, it means the 10th frame (non-bonus part) is complete.
        return frame_index, roll_in_frame


    def _is_game_over(self):
        """Checks if the game is complete, including bonus rolls."""
        frame_index, _ = self._get_current_frame_info()

        # If fewer than 10 frames have been fully processed, game is not over
        if frame_index < self._MAX_FRAMES:
            return False

        # If 10 frames are processed, check if bonus rolls are needed and completed
        start_of_10th = self._get_start_of_frame(9) # Frame index 9 is the 10th frame
        rolls_in_10th = self.rolls[start_of_10th:]
        num_rolls_10th = len(rolls_in_10th)

        if num_rolls_10th == 0:
             # This case should be caught by frame_index < 10, but defensive check
             return False

        is_strike_first = rolls_in_10th[0] == self._MAX_PINS
        is_spare = num_rolls_10th >= 2 and not is_strike_first and (rolls_in_10th[0] + rolls_in_10th[1] == self._MAX_PINS)

        if is_strike_first:
            # Need 3 total rolls in the 10th frame sequence (1 strike + 2 bonus)
            return num_rolls_10th >= 3
        elif is_spare:
            # Need 3 total rolls in the 10th frame sequence (2 for spare + 1 bonus)
            return num_rolls_10th >= 3
        else:
            # Open frame in 10th, need exactly 2 rolls
            return num_rolls_10th >= 2
