package bottlesong

import (
	"fmt"
	"strings"
)

var numToWord = map[int]string{
	10: "Ten", 9: "Nine", 8: "Eight", 7: "Seven", 6: "Six",
	5: "Five", 4: "Four", 3: "Three", 2: "Two", 1: "One", 0: "no",
}

func bottles(n int) string {
	if n == 1 {
		return "bottle"
	}
	return "bottles"
}

func verse(n int) []string {
	if n < 0 || n > 10 {
		// Or handle error appropriately
		return []string{}
	}

	currentNumWord := numToWord[n]
	currentBottles := bottles(n)
	hanging := "hanging on the wall"
	line1 := fmt.Sprintf("%s green %s %s,", currentNumWord, currentBottles, hanging)
	line2 := line1
	line3 := "And if one green bottle should accidentally fall,"

	nextN := n - 1
	nextNumWord := strings.ToLower(numToWord[nextN]) // Lowercase for the last line
	if nextN == 0 {
		nextNumWord = numToWord[nextN] // "no" is already lowercase
	}
	nextBottles := bottles(nextN)
	line4 := fmt.Sprintf("There'll be %s green %s %s.", nextNumWord, nextBottles, hanging)

	// Special case for 0 bottles
	if n == 0 {
		return []string{} // No verse for 0 bottles according to the pattern
	}
    // Special case for 1 bottle verse ending
    if n == 1 {
        line4 = fmt.Sprintf("There'll be %s green %s %s.", numToWord[0], bottles(0), hanging)
    }


	return []string{line1, line2, line3, line4}
}

func Recite(startBottles, takeDown int) []string {
	lyrics := []string{}
	for i := 0; i < takeDown; i++ {
		currentBottles := startBottles - i
		if currentBottles < 0 {
			break // Stop if we go below zero bottles
		}
		verseLines := verse(currentBottles)
		if len(verseLines) > 0 {
			if len(lyrics) > 0 {
				lyrics = append(lyrics, "") // Add empty line between verses
			}
			lyrics = append(lyrics, verseLines...)
		}
	}
	return lyrics
}
