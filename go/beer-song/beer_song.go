package beer

import (
	"errors"
	"fmt"
	"strings"
)

// Song generates the entire lyrics for "99 Bottles of Beer".
func Song() string {
	verses, _ := Verses(99, 0) // Error handling is done in Verses
	return verses
}

// Verses generates lyrics for a range of verses, from start down to stop.
func Verses(start, stop int) (string, error) {
	if start < stop || start < 0 || stop < 0 || start > 99 || stop > 99 {
		return "", errors.New("invalid verse range")
	}

	var sb strings.Builder
	for i := start; i >= stop; i-- {
		verse, err := Verse(i)
		if err != nil {
			// This should theoretically not happen if Verse range check is correct
			return "", fmt.Errorf("error generating verse %d: %w", i, err)
		}
		sb.WriteString(verse)
		if i > stop {
			sb.WriteString("\n") // Add blank line between verses (Verse now includes trailing \n)
		}
	}
	sb.WriteString("\n") // Add final newline required by tests
	return sb.String(), nil
}

// Verse generates the lyrics for a single verse n.
func Verse(n int) (string, error) {
	if n < 0 || n > 99 {
		return "", errors.New("verse number must be between 0 and 99")
	}

	switch n {
	case 0:
		return "No more bottles of beer on the wall, no more bottles of beer.\nGo to the store and buy some more, 99 bottles of beer on the wall.\n", nil
	case 1:
		return "1 bottle of beer on the wall, 1 bottle of beer.\nTake it down and pass it around, no more bottles of beer on the wall.\n", nil
	case 2:
		return "2 bottles of beer on the wall, 2 bottles of beer.\nTake one down and pass it around, 1 bottle of beer on the wall.\n", nil
	default:
		return fmt.Sprintf("%d bottles of beer on the wall, %d bottles of beer.\nTake one down and pass it around, %d bottles of beer on the wall.\n", n, n, n-1), nil
	}
}
