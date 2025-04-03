package bowling

import (
	"errors"
)

type Game struct {
	rolls []int
}

func NewGame() *Game {
	return &Game{rolls: []int{}}
}

func (g *Game) Roll(pins int) error {
	if pins < 0 {
		return errors.New("Negative roll is invalid")
	}
	if pins > 10 {
		return errors.New("Pin count exceeds pins on the lane")
	}

	if g.isGameOver() {
		return errors.New("Cannot roll after game is over")
	}

	rolls := g.rolls
	n := len(rolls)

	frame := 0
	i := 0
	for frame < 9 && i < n {
		if rolls[i] == 10 {
			i++
		} else if i+1 < n {
			i += 2
		} else {
			break
		}
		frame++
	}

	if frame < 9 {
		// frames 1-9
		if i < n && n > 0 && i == n-1 {
			// last frame is incomplete, this is the 2nd roll
			lastRoll := rolls[n-1]
			if lastRoll != 10 && lastRoll+pins > 10 {
				return errors.New("Pin count exceeds pins on the lane")
			}
		}
	} else if i <= n { // 10th frame and bonuses
		tenth := rolls[i:]
		switch len(tenth) {
		case 0:
			// first roll, no validation needed
		case 1:
			first := tenth[0]
			if first != 10 && first+pins > 10 {
				return errors.New("Pin count exceeds pins on the lane")
			}
		case 2:
			first, second := tenth[0], tenth[1]
			if first == 10 {
				if second != 10 && second+pins > 10 {
					return errors.New("Pin count exceeds pins on the lane")
				}
			} else if first+second == 10 {
				if pins > 10 {
					return errors.New("Pin count exceeds pins on the lane")
				}
			} else if first+second < 10 {
				return errors.New("Cannot roll after game is over")
			}
		case 3:
			return errors.New("Cannot roll after game is over")
		}
	}

	g.rolls = append(g.rolls, pins)
	return nil
}

func (g *Game) isGameOver() bool {
	rolls := g.rolls
	frame := 0
	i := 0
	n := len(rolls)
	for frame < 9 && i < n {
		if rolls[i] == 10 {
			i++
		} else if i+1 < n {
			i += 2
		} else {
			return false
		}
		frame++
	}
	if i > n {
		return false
	}
	if i == n {
		return false
	}
	tenth := rolls[i:]
	if len(tenth) < 2 {
		return false
	}
	first, second := tenth[0], tenth[1]
	if first == 10 || first+second == 10 {
		return len(tenth) == 3
	}
	return len(tenth) == 2
}

func (g *Game) Score() (int, error) {
	if !g.isGameOver() {
		return 0, errors.New("Score cannot be taken until the end of the game")
	}

	score := 0
	i := 0
	for frame := 0; frame < 10; frame++ {
		if g.rolls[i] == 10 {
			score += 10 + g.rolls[i+1] + g.rolls[i+2]
			i++
		} else if g.rolls[i]+g.rolls[i+1] == 10 {
			score += 10 + g.rolls[i+2]
			i += 2
		} else {
			score += g.rolls[i] + g.rolls[i+1]
			i += 2
		}
	}
	return score, nil
}
