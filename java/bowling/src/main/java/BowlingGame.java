import java.util.Arrays;

class BowlingGame {

    private static final int MAX_PINS = 10;
    private static final int MAX_FRAMES = 10;
    private int[] rolls = new int[21]; // Max 21 rolls possible
    private int currentRoll = 0;
    private boolean gameIsOver = false; // Track if game is determined to be over


    void roll(int pins) {
        if (gameIsOver) {
            throw new IllegalStateException("Cannot roll after game is over");
        }

        // --- Consolidated Validation: Throw IllegalStateException for ALL invalid pin counts ---
        if (pins < 0 || pins > MAX_PINS) {
            // Changed exception type and message to match test expectations
            throw new IllegalStateException("Pins must be between 0 and 10");
        }

        // Check frame-specific pin count rules BEFORE adding the roll temporarily
        // This requires knowing the context (which roll in frame/bonus)
        int frame = 0;
        int rollIndex = 0;
        boolean isFirstInFrame = true;
        while (frame < MAX_FRAMES && rollIndex < currentRoll) {
            if (isStrikeInternal(rollIndex)) {
                rollIndex++;
                isFirstInFrame = true;
            } else {
                rollIndex++; // Move past first roll
                isFirstInFrame = false;
                if (rollIndex == currentRoll) break; // Current roll is the second in this frame
                rollIndex++; // Move past second roll
                isFirstInFrame = true;
            }
            frame++;
        }

        // Now 'frame' and 'isFirstInFrame' represent the context for the *current* roll attempt
        if (frame < MAX_FRAMES) { // Still in frames 1-10
            if (!isFirstInFrame) { // This is the second roll of the frame
                int firstRollPins = rolls[currentRoll - 1]; // The roll just before this attempt
                if (firstRollPins + pins > MAX_PINS) {
                    throw new IllegalStateException("Pin count exceeds pins on the lane");
                }
            }
        } else { // Bonus roll validation (frame >= MAX_FRAMES)
            int tenthFrameStartRoll = findFrameStartIndex(MAX_FRAMES - 1);
            boolean tenthWasStrike = rolls[tenthFrameStartRoll] == MAX_PINS;

            // Check only if this is the *second* bonus roll after a strike in the 10th
            if (tenthWasStrike && currentRoll == tenthFrameStartRoll + 2) {
                 int firstBonusRoll = rolls[currentRoll - 1]; // The previous roll was the first bonus
                 if (firstBonusRoll < MAX_PINS && firstBonusRoll + pins > MAX_PINS) {
                     throw new IllegalStateException("Pin count exceeds pins on the lane");
                 }
            }
        }
        // --- End Validation ---

        // If validation passes, add the roll
        rolls[currentRoll++] = pins;

        // Determine if the game is over *after* this roll is added
        gameIsOver = checkGameOver();
    }

    // Checks if the game is logically over based on currentRoll count and 10th frame status
    private boolean checkGameOver() {
        int frame = 0;
        int rollIndex = 0;
        while (frame < MAX_FRAMES && rollIndex < currentRoll) {
            if (isStrikeInternal(rollIndex)) {
                rollIndex++;
            } else {
                // Need 2 rolls for non-strike, check if second exists *within recorded rolls*
                if (rollIndex + 1 >= currentRoll) {
                    return false; // Frame incomplete, game not over
                }
                rollIndex += 2;
            }
            frame++;
        }

        // If we finished iterating through the rolls needed for 10 frames
        if (frame == MAX_FRAMES) {
            // Check if the 10th frame requires bonus rolls
            int tenthFrameStart = findFrameStartIndex(MAX_FRAMES - 1);
            if (isStrikeInternal(tenthFrameStart)) {
                // Strike in 10th needs 2 bonus rolls (total 3 rolls starting from tenthFrameStart)
                return currentRoll >= tenthFrameStart + 3;
            } else if (isSpareInternal(tenthFrameStart)) {
                // Spare in 10th needs 1 bonus roll (total 3 rolls starting from tenthFrameStart)
                return currentRoll >= tenthFrameStart + 3;
            } else {
                // Open 10th frame, game ends after 2 rolls (rollIndex should equal currentRoll here)
                return rollIndex == currentRoll;
            }
        }

        // If we haven't completed 10 frames worth of rolls
        return false;
    }


    int score() {
        // Validate if game is in a scoreable state (i.e., is logically over)
        if (!checkGameOver()) { // Use the same logic that roll() uses
            throw new IllegalStateException("Score cannot be taken until the end of the game");
        }

        int score = 0;
        int rollIndex = 0;
        for (int frame = 0; frame < MAX_FRAMES; frame++) {
            // We know enough rolls exist due to the checkGameOver() validation

            if (isStrike(rollIndex)) {
                score += MAX_PINS + strikeBonus(rollIndex);
                rollIndex++;
            } else if (isSpare(rollIndex)) {
                score += MAX_PINS + spareBonus(rollIndex);
                rollIndex += 2;
            } else {
                score += openFrameScore(rollIndex);
                rollIndex += 2;
            }
        }
        return score;
    }


    // Helper to find the start index of a frame (0-9)
    // Assumes it's called when sufficient rolls exist to determine previous frame types
    private int findFrameStartIndex(int targetFrame) {
         int frame = 0;
         int rollIndex = 0;
         while(frame < targetFrame) {
             // Check bounds before accessing rolls array
             if (rollIndex >= currentRoll) throw new IllegalStateException("Insufficient rolls to find frame start for frame " + targetFrame + " at roll index " + rollIndex);

             if (isStrikeInternal(rollIndex)) {
                 rollIndex++;
             } else {
                 // Check bounds before accessing rolls array for second roll
                 if (rollIndex + 1 >= currentRoll) throw new IllegalStateException("Insufficient rolls for non-strike frame " + frame + " at roll index " + rollIndex);
                 rollIndex += 2;
             }
             frame++;
         }
         return rollIndex;
    }


    // Scoring helpers - check bounds carefully
    private boolean isStrike(int rollIndex) {
        if (rollIndex >= currentRoll) return false;
        return rolls[rollIndex] == MAX_PINS;
    }
    // Internal version for game logic helpers to avoid state dependency
    private boolean isStrikeInternal(int rollIndex) {
         if (rollIndex >= currentRoll) return false; // Check bounds even for internal
        return rolls[rollIndex] == MAX_PINS;
    }


    private boolean isSpare(int rollIndex) {
        if (rollIndex + 1 >= currentRoll) return false;
        return rolls[rollIndex] + rolls[rollIndex + 1] == MAX_PINS && rolls[rollIndex] != MAX_PINS;
    }
     // Internal version for game logic helpers
    private boolean isSpareInternal(int rollIndex) {
        if (rollIndex + 1 >= currentRoll) return false; // Check bounds even for internal
        return rolls[rollIndex] + rolls[rollIndex + 1] == MAX_PINS && rolls[rollIndex] != MAX_PINS;
    }

    private int strikeBonus(int rollIndex) {
        int bonus = 0;
        if (rollIndex + 1 < currentRoll) {
            bonus += rolls[rollIndex + 1];
        }
        if (rollIndex + 2 < currentRoll) {
            bonus += rolls[rollIndex + 2];
        }
        return bonus;
    }

    private int spareBonus(int rollIndex) {
        if (rollIndex + 2 < currentRoll) {
            return rolls[rollIndex + 2];
        }
        return 0;
    }

    private int openFrameScore(int rollIndex) {
         // Check bounds first
         if (rollIndex + 1 < currentRoll) {
             // Ensure it's not actually a spare before summing
             if (rolls[rollIndex] + rolls[rollIndex+1] < MAX_PINS) {
                 return rolls[rollIndex] + rolls[rollIndex+1];
             } else {
                 // This implies a spare, which should have been caught by isSpare check in score()
                 // Return sum for robustness, but indicates potential logic issue if reached.
                 return rolls[rollIndex] + rolls[rollIndex+1];
             }
         } else if (rollIndex < currentRoll) {
             // Only one roll recorded for this frame index - should not happen in score() for open frame
             return rolls[rollIndex];
         }
         return 0; // Should not happen
    }
}