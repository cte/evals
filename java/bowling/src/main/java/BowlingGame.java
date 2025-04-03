import java.util.ArrayList;
import java.util.List;

class BowlingGame {

    private final List<Integer> rolls = new ArrayList<>();

    public void roll(int pins) {
        if (pins < 0) {
            throw new IllegalStateException("Negative roll is invalid");
        }
        if (pins > 10) {
            throw new IllegalStateException("Pin count exceeds pins on the lane");
        }
        if (isGameOver()) {
            throw new IllegalStateException("Cannot roll after game is over");
        }

        int currentRollIndex = rolls.size();
        if (currentRollIndex >= 1 && !isInTenthFrame()) {
            if (isSecondRollOfFrame(currentRollIndex)) {
                int lastRoll = rolls.get(currentRollIndex - 1);
                if (lastRoll + pins > 10) {
                    throw new IllegalStateException("Pin count exceeds pins on the lane");
                }
            }
        }

        // Special validation for 10th frame fill balls
        if (isInTenthFrame()) {
            int frameStart = getTenthFrameStartIndex();
            int rollsInTenth = currentRollIndex - frameStart;
            if (rollsInTenth == 1) {
                int first = rolls.get(frameStart);
                if (first != 10 && first + pins > 10) {
                    throw new IllegalStateException("Pin count exceeds pins on the lane");
                }
            } else if (rollsInTenth == 2) {
                int first = rolls.get(frameStart);
                int second = rolls.get(frameStart + 1);
                if (first == 10) { // strike in first roll
                    if (second != 10 && second + pins > 10) {
                        throw new IllegalStateException("Pin count exceeds pins on the lane");
                    }
                    if (second != 10 && pins == 10) {
                        throw new IllegalStateException("Pin count exceeds pins on the lane");
                    }
                } else if (first + second == 10) { // spare
                    // no special validation needed
                } else {
                    // no fill balls allowed
                }
            }
        }

        rolls.add(pins);
    }

    public int score() {
        if (!isGameComplete()) {
            throw new IllegalStateException("Score cannot be taken until the end of the game");
        }

        int total = 0;
        int rollIndex = 0;
        for (int frame = 0; frame < 10; frame++) {
            if (rolls.get(rollIndex) == 10) { // strike
                total += 10 + strikeBonus(rollIndex);
                rollIndex += 1;
            } else if (rolls.get(rollIndex) + rolls.get(rollIndex + 1) == 10) { // spare
                total += 10 + spareBonus(rollIndex);
                rollIndex += 2;
            } else { // open frame
                total += rolls.get(rollIndex) + rolls.get(rollIndex + 1);
                rollIndex += 2;
            }
        }
        return total;
    }

    private int strikeBonus(int rollIndex) {
        return rolls.get(rollIndex + 1) + rolls.get(rollIndex + 2);
    }

    private int spareBonus(int rollIndex) {
        return rolls.get(rollIndex + 2);
    }

    private boolean isSecondRollOfFrame(int currentRollIndex) {
        int frame = 0;
        int idx = 0;
        while (idx < currentRollIndex && frame < 10) {
            if (rolls.get(idx) == 10) {
                idx += 1;
            } else {
                idx += 2;
            }
            frame++;
        }
        return idx - 1 == currentRollIndex;
    }

    private boolean isGameComplete() {
        int frame = 0;
        int rollIndex = 0;
        while (frame < 10 && rollIndex < rolls.size()) {
            if (rolls.get(rollIndex) == 10) {
                rollIndex += 1;
            } else if (rollIndex + 1 < rolls.size()) {
                rollIndex += 2;
            } else {
                return false;
            }
            frame++;
        }
        if (frame < 10) {
            return false;
        }
        int extraRolls = rolls.size() - rollIndex;
        int tenthStart = getTenthFrameStartIndex();
        int first = rolls.get(tenthStart);
        int second = rolls.size() > tenthStart + 1 ? rolls.get(tenthStart + 1) : -1;

        if (first == 10) { // strike
            return extraRolls >= 2;
        } else if (second != -1 && first + second == 10) { // spare
            return extraRolls >= 1;
        } else {
            return extraRolls == 0;
        }
    }

    private boolean isGameOver() {
        if (rolls.size() < 12) {
            return isGameComplete();
        }
        try {
            return isGameComplete();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isInTenthFrame() {
        int frame = 0;
        int rollIndex = 0;
        while (frame < 9 && rollIndex < rolls.size()) {
            if (rolls.get(rollIndex) == 10) {
                rollIndex += 1;
            } else if (rollIndex + 1 < rolls.size()) {
                rollIndex += 2;
            } else {
                break;
            }
            frame++;
        }
        return frame == 9;
    }

    private int getTenthFrameStartIndex() {
        int frame = 0;
        int rollIndex = 0;
        while (frame < 9 && rollIndex < rolls.size()) {
            if (rolls.get(rollIndex) == 10) {
                rollIndex += 1;
            } else {
                rollIndex += 2;
            }
            frame++;
        }
        return rollIndex;
    }
}