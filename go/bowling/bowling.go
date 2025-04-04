package bowling

import "errors"

const maxPins = 10
const numFrames = 10

// Game represents a single game of bowling.
type Game struct {
	rolls []int
	// We need state to validate rolls immediately as per test expectations.
	// However, calculating state on every roll is complex.
	// Let's try validating *within* Roll by simulating the game state up to the potential *next* roll.
}

// NewGame creates a new bowling game.
func NewGame() *Game {
	// Initialize with capacity for maximum possible rolls (21)
	return &Game{rolls: make([]int, 0, 21)}
}

// isGameOver checks if the game has the correct number of rolls for completion.
// This is a simplified check focusing only on whether *more* rolls are allowed.
func (g *Game) isGameOver() bool {
	rollIndex := 0
	rolls := g.rolls

	for frame := 1; frame <= numFrames; frame++ {
		if rollIndex >= len(rolls) {
			return false // Game not finished yet
		}

		firstRoll := rolls[rollIndex]

		if firstRoll == maxPins { // Strike
			if frame == numFrames {
				if rollIndex+2 >= len(rolls) { return false } // Need bonus rolls
				rollIndex += 3 // Strike + 2 bonus
			} else {
				rollIndex++ // Strike in frames 1-9
			}
		} else { // Not a strike
			if rollIndex+1 >= len(rolls) { return false } // Need second roll
			secondRoll := rolls[rollIndex+1]

			if firstRoll+secondRoll == maxPins { // Spare
				if frame == numFrames {
					if rollIndex+2 >= len(rolls) { return false } // Need bonus roll
					rollIndex += 3 // Spare + 1 bonus
				} else {
					rollIndex += 2 // Spare in frames 1-9
				}
			} else { // Open frame
				rollIndex += 2
			}
		}
	}
	// If we finished 10 frames and rollIndex matches len(rolls), game is exactly complete.
	// If rollIndex < len(rolls), there are extra rolls.
	return rollIndex <= len(rolls) // Game is over if we processed 10 frames worth of rolls or more
}

// pinsLeftInFrame calculates pins left before the current roll attempt.
// Returns -1 if it's the first roll of a frame.
func (g *Game) pinsLeftInFrame() int {
	rollIndex := 0
	rolls := g.rolls

	for frame := 1; frame <= numFrames; frame++ {
		if rollIndex >= len(rolls) {
			return maxPins // Start of a new frame
		}

		firstRoll := rolls[rollIndex]

		if firstRoll == maxPins { // Strike
			if frame == numFrames {
				// 10th frame strike logic
				if rollIndex+1 >= len(rolls) { return maxPins } // First bonus roll
				bonus1 := rolls[rollIndex+1]
				if rollIndex+2 >= len(rolls) { // Attempting second bonus roll
					if bonus1 == maxPins { return maxPins } // After strike bonus
					return maxPins - bonus1 // After non-strike bonus
				}
				// Game should be over
				return -2 // Indicates game over state
			}
			// Strike in frames 1-9, next roll is start of new frame
			rollIndex++
			continue
		}

		// Not a strike, check second roll
		if rollIndex+1 >= len(rolls) {
			return maxPins - firstRoll // Waiting for second roll
		}

		// Have both rolls for the frame (or it's 10th frame spare/open)
		secondRoll := rolls[rollIndex+1]
		if frame == numFrames {
			if firstRoll+secondRoll == maxPins { // Spare in 10th
				if rollIndex+2 >= len(rolls) { return maxPins } // Waiting for bonus roll
			}
			// Open 10th frame or Spare with bonus already rolled
			return -2 // Indicates game over state
		}

		// Spare or Open in frames 1-9, next roll is start of new frame
		rollIndex += 2
	}
	return -2 // Game complete
}


// Roll records the number of pins knocked down in a roll.
func (g *Game) Roll(pins int) error {
	if pins < 0 {
		return errors.New("negative roll is invalid")
	}
	if pins > maxPins {
		return errors.New("pin count exceeds pins on the lane") // General check
	}

	// Check game over state *before* checking pins left
	if g.isGameOver() {
		return errors.New("cannot roll after game is over")
	}

	pinsLeft := g.pinsLeftInFrame()
	if pinsLeft != -2 && pins > pinsLeft { // Check against pins remaining in the frame
	    // pinsLeft == -2 means game over, handled above
		// pinsLeft == maxPins means first roll, already checked pins <= maxPins
		return errors.New("pin count exceeds pins on the lane")
	}


	g.rolls = append(g.rolls, pins)
	return nil
}


// Score calculates the final score and validates the game.
func (g *Game) Score() (int, error) {
	score := 0
	rollIndex := 0
	rolls := g.rolls

	for frame := 1; frame <= numFrames; frame++ {
		// Check if rolls are exhausted prematurely
		if rollIndex >= len(rolls) {
			return 0, errors.New("game not complete")
		}

		firstRoll := rolls[rollIndex]
		// Basic validation (already done in Roll, but good practice)
		if firstRoll < 0 || firstRoll > maxPins {
			return 0, errors.New("invalid pin count detected in score calculation")
		}

		// --- Strike ---
		if firstRoll == maxPins {
			if frame == numFrames { // 10th Frame Strike
				if rollIndex+1 >= len(rolls) || rollIndex+2 >= len(rolls) {
					return 0, errors.New("game not complete: strike in 10th requires two bonus rolls")
				}
				bonus1 := rolls[rollIndex+1]
				bonus2 := rolls[rollIndex+2]
				if bonus1 < 0 || bonus1 > maxPins || bonus2 < 0 || bonus2 > maxPins {
					return 0, errors.New("invalid pin count for bonus roll")
				}
				// Special 10th frame validation: if first bonus is not a strike, second bonus cannot make total > 10
				if bonus1 != maxPins && bonus1+bonus2 > maxPins {
					return 0, errors.New("invalid bonus rolls in 10th frame after strike")
				}
				score += maxPins + bonus1 + bonus2
				rollIndex += 3 // Mark rolls as consumed for final check
			} else { // Strike in Frames 1-9
				if rollIndex+1 >= len(rolls) || rollIndex+2 >= len(rolls) {
					return 0, errors.New("game not complete: strike requires two rolls in following frames")
				}
				bonus1 := rolls[rollIndex+1]
				bonus2 := rolls[rollIndex+2]
				// Basic validation on bonus rolls (0-10)
				if bonus1 < 0 || bonus1 > maxPins || bonus2 < 0 || bonus2 > maxPins {
					return 0, errors.New("invalid pin count for bonus roll")
				}
				// More complex validation (like bonus1+bonus2 > 10 if bonus1 != 10)
				// should be handled when those frames are scored. Just add raw pins here.
				score += maxPins + bonus1 + bonus2
				rollIndex++ // Advance one roll index for the frame
			}
		// --- Not a Strike ---
		} else {
			if rollIndex+1 >= len(rolls) {
				return 0, errors.New("game not complete: frame requires a second roll")
			}
			secondRoll := rolls[rollIndex+1]
			if secondRoll < 0 || secondRoll > maxPins {
				return 0, errors.New("invalid pin count detected in score calculation")
			}
			if firstRoll+secondRoll > maxPins {
				return 0, errors.New("invalid frame: two rolls exceed 10 pins")
			}

			frameScore := firstRoll + secondRoll

			// --- Spare ---
			if frameScore == maxPins {
				if frame == numFrames { // 10th Frame Spare
					if rollIndex+2 >= len(rolls) {
						return 0, errors.New("game not complete: spare in 10th requires one bonus roll")
					}
					bonus := rolls[rollIndex+2]
					if bonus < 0 || bonus > maxPins {
						return 0, errors.New("invalid pin count for bonus roll")
					}
					score += maxPins + bonus
					rollIndex += 3 // Mark rolls as consumed
				} else { // Spare in Frames 1-9
					if rollIndex+2 >= len(rolls) {
						return 0, errors.New("game not complete: spare requires one bonus roll in following frame")
					}
					bonus := rolls[rollIndex+2]
					if bonus < 0 || bonus > maxPins {
						return 0, errors.New("invalid pin count for bonus roll")
					}
					score += maxPins + bonus
					rollIndex += 2 // Advance two roll indices
				}
			// --- Open Frame ---
			} else {
				score += frameScore
				// No bonus rolls needed, just advance roll index
				rollIndex += 2 // Advance two roll indices
			}
		}
	} // End of frame loop

	// Final validation: Check if the calculated roll index matches the total number of rolls
	if rollIndex != len(rolls) {
		// This indicates either too few rolls (caught earlier by "game not complete")
		// or too many rolls (game should have ended earlier).
		return 0, errors.New("invalid game: incorrect number of rolls for completed frames")
	}

	return score, nil
}
