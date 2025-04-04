//
// This is only a SKELETON file for the 'Bowling' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Bowling {
  constructor() {
    this.rolls = [];
    this._gameOver = false; // Internal flag to track game completion
  }

  /**
   * Record a roll in the game.
   * @param {number} pins The number of pins knocked down.
   */
  roll(pins) {
    if (this._gameOver) {
      throw new Error('Cannot roll after game is over');
    }
    if (pins < 0) {
      throw new Error('Negative roll is invalid');
    }
    if (pins > 10) {
      throw new Error('Pin count exceeds pins on the lane');
    }

    // Validate pin count based on current frame rules before adding the roll
    this._validateRollRules(pins);

    this.rolls.push(pins);

    // Check if this roll concludes the game
    this._checkAndUpdateGameOverState();
  }

  /**
   * Calculate the total score for the game.
   * @returns {number} The total score.
   */
  score() {
    if (!this._isGameComplete()) {
        // Check if the game has started at all
        if (this.rolls.length === 0) {
             throw new Error('Score cannot be taken until the end of the game');
        }
        // Check based on expected rolls vs actual rolls
        if (!this._isGameComplete()) { // Re-check for clarity and robustness
             throw new Error('Score cannot be taken until the end of the game');
        }
    }
    // Mark game as over definitively if scoring is allowed and calculation proceeds
    this._gameOver = true;


    let totalScore = 0;
    let rollIndex = 0;
    for (let frame = 1; frame <= 10; frame++) {
      if (this._isStrike(rollIndex)) {
        totalScore += 10 + this._strikeBonus(rollIndex);
        rollIndex++;
      } else if (this._isSpare(rollIndex)) {
        totalScore += 10 + this._spareBonus(rollIndex);
        rollIndex += 2;
      } else {
        totalScore += this._frameScore(rollIndex);
        rollIndex += 2;
      }
    }
    return totalScore;
  }

  // --- Private Helper Methods ---

  /** Checks if the current roll ('pins') is valid based on game rules and current state. */
  _validateRollRules(pins) {
    let currentFrame = 1;
    let rollIndex = 0;
    let firstRollInFrame = true;

    // Determine the frame number and roll context for the *next* roll
    while (currentFrame <= 10 && rollIndex < this.rolls.length) {
        if (this._isStrike(rollIndex)) {
            // Strike completes the frame in one roll (except frame 10)
            if (currentFrame === 10) {
                 // Need to know how many rolls already in 10th
                 const tenthFrameStartIndex = this._calculateFrameStartIndex(10);
                 const rollsInTenth = this.rolls.length - tenthFrameStartIndex;
                 if (rollsInTenth === 1) { // After 1st strike roll
                     firstRollInFrame = false; // Next is 2nd roll (1st bonus)
                 } else if (rollsInTenth === 2) { // After 2nd roll (1st bonus)
                     firstRollInFrame = false; // Next is 3rd roll (2nd bonus)
                 }
                 // Stay in frame 10 logic
                 rollIndex++; // Advance past the roll being considered
                 break; // Exit loop, handle frame 10 below
            } else {
                 // Strike in frames 1-9
                 rollIndex++;
                 currentFrame++;
                 firstRollInFrame = true;
            }
        } else {
            // Not a strike on the first roll
            if (rollIndex + 1 >= this.rolls.length) {
                // Game ends mid-frame (only one roll recorded)
                firstRollInFrame = false; // Next roll is the second roll
                rollIndex++;
                // currentFrame stays the same
                break; // Exit loop, handle current frame below
            } else {
                // Two rolls completed the frame
                const first = this.rolls[rollIndex];
                const second = this.rolls[rollIndex + 1];
                if (currentFrame === 10 && first + second === 10) { // Spare in 10th
                    const tenthFrameStartIndex = this._calculateFrameStartIndex(10);
                    const rollsInTenth = this.rolls.length - tenthFrameStartIndex;
                     if (rollsInTenth === 2) { // After spare completion
                         firstRollInFrame = false; // Next is 3rd roll (bonus)
                     }
                     // Stay in frame 10 logic
                     rollIndex += 2; // Advance past the rolls being considered
                     break; // Exit loop, handle frame 10 below
                } else {
                     // Open frame (frames 1-9 or 10)
                     rollIndex += 2;
                     currentFrame++;
                     firstRollInFrame = true;
                }
            }
        }
    }

    // Validate 'pins' based on the calculated state (currentFrame, firstRollInFrame)
    if (currentFrame <= 9) {
        if (!firstRollInFrame) {
            // Validating the second roll of frames 1-9
            const firstRollPins = this.rolls[this.rolls.length - 1]; // The actual previous roll
            if (firstRollPins + pins > 10) {
                throw new Error('Pin count exceeds pins on the lane');
            }
        }
        // If firstRollInFrame is true, any roll 0-10 is valid (pins > 10 already checked).
    } else if (currentFrame === 10) {
        const tenthFrameStartIndex = this._calculateFrameStartIndex(10);
        // If frame 10 hasn't started, tenthFrameStartIndex will be == this.rolls.length or -1 if rolls is empty
        if (tenthFrameStartIndex === -1 || tenthFrameStartIndex === this.rolls.length) {
             // This is the first roll of the 10th frame. Any 0-10 is valid.
             return;
        }

        const rollsInTenthSoFar = this.rolls.length - tenthFrameStartIndex;
        const firstRoll = this.rolls[tenthFrameStartIndex];
        const secondRoll = (rollsInTenthSoFar >= 1) ? this.rolls[tenthFrameStartIndex + 1] : undefined;

        if (rollsInTenthSoFar === 1) {
            // Validating the second roll of the 10th frame
            if (firstRoll !== 10 && firstRoll + pins > 10) {
                throw new Error('Pin count exceeds pins on the lane');
            }
        } else if (rollsInTenthSoFar === 2) {
            // Validating the third (bonus) roll of the 10th frame
            const isStrike = firstRoll === 10;
            // Ensure secondRoll is defined before checking spare
            const isSpare = !isStrike && secondRoll !== undefined && (firstRoll + secondRoll === 10);

            if (!isStrike && !isSpare) {
                // Should not be possible to roll a 3rd time on an open frame.
                // The main roll() check for _gameOver should prevent this.
                throw new Error('Cannot roll after open frame in 10th'); // Defensive
            }

            if (isStrike) {
                // Bonus roll after a strike
                // If the first bonus roll (secondRoll) was NOT a strike,
                // the sum of the two bonus rolls cannot exceed 10.
                if (secondRoll !== undefined && secondRoll !== 10 && secondRoll + pins > 10) {
                    throw new Error('Pin count exceeds pins on the lane');
                }
            }
            // If it was a spare, the bonus roll 'pins' (0-10) is valid.
            // If it was strike-strike, the bonus roll 'pins' (0-10) is valid.
        }
        // If rollsInTenthSoFar >= 3, the main roll() check should prevent rolling.
    }
    // If currentFrame > 10, the main roll() check should prevent rolling.
  }


  /** Checks if the game is over based on rolls and updates the internal flag. */
  _checkAndUpdateGameOverState() {
    const tenthFrameStartIndex = this._getFrameStartIndex(10);
    if (tenthFrameStartIndex === -1) return; // Game hasn't reached the 10th frame

    const rollsInTenth = this.rolls.length - tenthFrameStartIndex;
    if (rollsInTenth < 2) return; // 10th frame incomplete

    const firstRoll = this.rolls[tenthFrameStartIndex];
    // Ensure second roll exists before checking spare/open frame completion
    if (rollsInTenth >= 2) {
        const secondRoll = this.rolls[tenthFrameStartIndex + 1];
        const isStrike = firstRoll === 10;
        const isSpare = !isStrike && (firstRoll + secondRoll === 10);

        if (isStrike) {
            if (rollsInTenth === 3) this._gameOver = true;
        } else if (isSpare) {
            if (rollsInTenth === 3) this._gameOver = true;
        } else { // Open frame
            // Game ends after 2 rolls in an open 10th frame
            this._gameOver = true;
        }
    }
  }

   /** Determines if the game has the required number of rolls to be considered complete. */
   _isGameComplete() {
        let expectedRolls = 0;
        let tempRollIndex = 0;
        let frame = 1;
        while(frame <= 10) {
            if (tempRollIndex >= this.rolls.length) return false; // Not enough rolls recorded yet

            const first = this.rolls[tempRollIndex];
            if (first === 10) { // Strike
                expectedRolls++;
                tempRollIndex++;
                if (frame === 10) expectedRolls += 2; // Strike in 10th needs 2 bonus rolls
            } else { // Not a strike on the first ball
                expectedRolls += 2; // Expect two rolls for the frame
                 if (tempRollIndex + 1 >= this.rolls.length) return false; // Second roll missing
                 const second = this.rolls[tempRollIndex + 1];
                 tempRollIndex += 2;
                 if (frame === 10 && first + second === 10) {
                     expectedRolls++; // Spare in 10th needs 1 bonus roll
                 }
            }
            frame++;
        }
        // Game is complete if the number of rolls matches the expected number
        return this.rolls.length === expectedRolls;
   }


  /** Calculates the start index of a given frame (1-based). Returns -1 if frame not started. */
  _getFrameStartIndex(targetFrame) {
      return this._calculateFrameStartIndex(targetFrame);
  }

  /** Robust calculation of frame start index, used by validation and scoring. */
   _calculateFrameStartIndex(targetFrame) {
        let rollIndex = 0;
        let currentFrame = 1;
        while (currentFrame < targetFrame && rollIndex < this.rolls.length) {
            if (this._isStrike(rollIndex)) {
                rollIndex++;
            } else {
                 // Need two rolls to complete a non-strike frame
                 if (rollIndex + 1 >= this.rolls.length) return -1; // Frame incomplete
                rollIndex += 2;
            }
            currentFrame++;
        }
        // If we finished because we reached the target frame
        if (currentFrame === targetFrame) {
             // Ensure the calculated index is valid within the current rolls
             // (It's possible to calculate an index for a frame that *should* start but hasn't yet)
             if (rollIndex > this.rolls.length) return -1;
             return rollIndex;
        }
        // If we finished because we ran out of rolls before reaching the target frame
        return -1;
    }


  _isStrike(rollIndex) {
    return this.rolls[rollIndex] === 10;
  }

  _isSpare(rollIndex) {
    // Check that both rolls exist and sum to 10
    return rollIndex + 1 < this.rolls.length &&
           this.rolls[rollIndex] + this.rolls[rollIndex + 1] === 10;
  }

  _strikeBonus(rollIndex) {
    // Ensure the next two rolls exist for the bonus calculation
    if (rollIndex + 2 >= this.rolls.length) {
        // Handle cases where bonus rolls might be missing (e.g., strike in 9th, only one roll in 10th)
        // The score() check should prevent scoring incomplete games, but add safety.
        const bonus1 = this.rolls[rollIndex + 1] !== undefined ? this.rolls[rollIndex + 1] : 0;
        const bonus2 = this.rolls[rollIndex + 2] !== undefined ? this.rolls[rollIndex + 2] : 0;
        return bonus1 + bonus2;
    }
    return this.rolls[rollIndex + 1] + this.rolls[rollIndex + 2];
  }

  _spareBonus(rollIndex) {
    // Ensure the roll after the spare exists
    if (rollIndex + 2 >= this.rolls.length) {
        return 0; // No bonus if the next roll doesn't exist
    }
    return this.rolls[rollIndex + 2];
  }

  _frameScore(rollIndex) {
    // Ensure both rolls of the frame exist
    if (rollIndex + 1 >= this.rolls.length) {
        // Should only happen in an incomplete game, return score so far.
        return this.rolls[rollIndex] !== undefined ? this.rolls[rollIndex] : 0;
    }
    return this.rolls[rollIndex] + this.rolls[rollIndex + 1];
  }
}
