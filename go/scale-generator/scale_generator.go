package scale

import "strings"

var sharps = []string{"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"}
var flats = []string{"A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab"}

// useFlats determines if a scale starting with the tonic should use flats.
// Based on the table provided in the instructions.
func useFlats(tonic string) bool {
	// Handle uppercase versions of flat keys explicitly for matching
	upperTonic := strings.ToUpper(string(tonic[0])) + tonic[1:]
	switch upperTonic {
	case "F", "Bb", "Eb", "Ab", "Db", "Gb":
		return true
	}
	// Handle minor keys (lowercase)
	switch tonic {
	case "d", "g", "c", "f", "bb", "eb":
		return true
	default:
		// Includes sharps keys and also C major / a minor which use sharps for ascending scales.
		return false
	}
}

// normalizeTonic ensures the tonic matches the casing in the sharp/flat arrays.
func normalizeTonic(tonic string) string {
	if len(tonic) < 1 {
		return ""
	}
	// Uppercase the first letter, keep the rest (handles 'Bb', 'F#')
	return strings.ToUpper(string(tonic[0])) + tonic[1:]
}

// findIndex finds the index of a note in a given scale.
func findIndex(scale []string, note string) int {
	for i, n := range scale {
		// Case-insensitive comparison for robustness, although base scales are consistent
		if strings.EqualFold(n, note) {
			return i
		}
	}
	// Attempt normalization again if direct match fails (e.g. input "a#" vs scale "A#")
	normalizedNote := normalizeTonic(note)
	for i, n := range scale {
		if n == normalizedNote {
			return i
		}
	}
	return -1 // Should not happen with valid input as per exercise constraints
}

// Scale generates a musical scale based on a tonic and an optional interval pattern.
func Scale(tonic, interval string) []string {
	useFlatsScale := useFlats(tonic)
	normalizedTonic := normalizeTonic(tonic) // Normalize for lookup

	var baseScale []string
	if useFlatsScale {
		baseScale = flats
	} else {
		baseScale = sharps
	}

	startIndex := findIndex(baseScale, normalizedTonic)
	if startIndex == -1 {
		// This case indicates an issue, potentially an invalid tonic not covered
		// by the sharp/flat lists or normalization logic.
		// For the exercise, assume valid inputs mean this won't be -1.
		// If it could happen, returning nil or an error might be appropriate.
		return []string{} // Return empty slice as a fallback
	}


	if interval == "" {
		// Chromatic scale generation
		result := make([]string, 12)
		for i := 0; i < 12; i++ {
			result[i] = baseScale[(startIndex+i)%12]
		}
		return result
	}

	// Interval-based scale generation
	result := make([]string, 0, len(interval)+1)
	result = append(result, baseScale[startIndex]) // Start with the actual note from the chosen scale
	currentIndex := startIndex

	for _, step := range interval {
		var jump int
		switch step {
		case 'm':
			jump = 1
		case 'M':
			jump = 2
		case 'A':
			jump = 3
		default:
			jump = 0 // Ignore invalid intervals as per typical exercise constraints
		}
		currentIndex = (currentIndex + jump) % 12
		result = append(result, baseScale[currentIndex])
	}

	return result
}
