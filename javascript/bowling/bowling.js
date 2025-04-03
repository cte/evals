//
// This is only a SKELETON file for the 'Bowling' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Bowling {
  constructor() {
    this.rolls = [];
    this.currentFrame = 1;
    this.isFirstRoll = true;
    this.tenthFrameRolls = [];
    this.isGameOver = false;
  }

  roll(pins) {
    if (this.isGameOver) {
      throw new Error('Cannot roll after game is over');
    }
    if (pins < 0) {
      throw new Error('Negative roll is invalid');
    }
    if (pins > 10) {
      throw new Error('Pin count exceeds pins on the lane');
    }

    if (this.currentFrame < 10) {
      if (!this.isFirstRoll && this.rolls[this.rolls.length - 1] + pins > 10) {
        throw new Error('Pin count exceeds pins on the lane');
      }

      this.rolls.push(pins);

      if (pins === 10 && this.isFirstRoll) {
        this.currentFrame++;
      } else if (!this.isFirstRoll) {
        this.currentFrame++;
        this.isFirstRoll = true;
      } else {
        this.isFirstRoll = false;
      }

      if (this.currentFrame > 10) {
        this.isGameOver = true;
      }
    } else {
      // 10th frame logic
      this.tenthFrameRolls.push(pins);

      const len = this.tenthFrameRolls.length;

      if (len === 1) {
        this.rolls.push(pins);
      } else if (len === 2) {
        if (
          this.tenthFrameRolls[0] !== 10 &&
          this.tenthFrameRolls[0] + this.tenthFrameRolls[1] > 10
        ) {
          throw new Error('Pin count exceeds pins on the lane');
        }
        this.rolls.push(pins);
        if (
          this.tenthFrameRolls[0] === 10 || // strike
          this.tenthFrameRolls[0] + this.tenthFrameRolls[1] === 10 // spare
        ) {
          // allow third roll
        } else {
          this.isGameOver = true;
        }
      } else if (len === 3) {
        const first = this.tenthFrameRolls[0];
        const second = this.tenthFrameRolls[1];
        const third = this.tenthFrameRolls[2];

        if (first === 10) {
          // strike on first ball
          if (
            second !== 10 && second + third > 10
          ) {
            throw new Error('Pin count exceeds pins on the lane');
          }
        } else if (first + second === 10) {
          // spare on first two balls
          if (third > 10) {
            throw new Error('Pin count exceeds pins on the lane');
          }
        }
        this.rolls.push(pins);
        this.isGameOver = true;
      } else {
        throw new Error('Cannot roll after game is over');
      }
    }
  }

  score() {
    if (!this.isGameOver) {
      throw new Error('Score cannot be taken until the end of the game');
    }

    let totalScore = 0;
    let rollIndex = 0;

    for (let frame = 0; frame < 10; frame++) {
      if (this.rolls[rollIndex] === 10) { // Strike
        totalScore += 10 + this.rolls[rollIndex + 1] + this.rolls[rollIndex + 2];
        rollIndex += 1;
      } else if (
        this.rolls[rollIndex] + this.rolls[rollIndex + 1] === 10
      ) { // Spare
        totalScore += 10 + this.rolls[rollIndex + 2];
        rollIndex += 2;
      } else { // Open frame
        totalScore += this.rolls[rollIndex] + this.rolls[rollIndex + 1];
        rollIndex += 2;
      }
    }

    return totalScore;
  }
}
