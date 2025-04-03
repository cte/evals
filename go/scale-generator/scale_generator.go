package scale

func Scale(tonic, interval string) []string {
	originalTonic := tonic
	normalizedTonic := normalizeTonic(tonic)

	// Determine if flats should be used based on original tonic (case-sensitive)
	useFlats := isFlatKey(originalTonic)

	var chromatic []string
	if useFlats {
		chromatic = []string{"F", "Gb", "G", "Ab", "A", "Bb", "B", "C", "Db", "D", "Eb", "E"}
	} else {
		chromatic = []string{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"}
	}

	// Find starting index using normalized tonic
	var start int
	for i, note := range chromatic {
		if note == normalizedTonic {
			start = i
			break
		}
	}

	// If interval is empty, return chromatic scale starting from tonic
	if interval == "" {
		scale := make([]string, 12)
		for i := 0; i < 12; i++ {
			scale[i] = chromatic[(start+i)%12]
		}
		return scale
	}

	// Otherwise, build scale based on interval pattern
	scale := []string{chromatic[start]}
	pos := start
	for _, step := range interval {
		var inc int
		switch step {
		case 'm':
			inc = 1
		case 'M':
			inc = 2
		case 'A':
			inc = 3
		default:
			inc = 0
		}
		pos = (pos + inc) % 12
		scale = append(scale, chromatic[pos])
	}
	return scale
}

// normalizeTonic capitalizes the tonic correctly
func normalizeTonic(tonic string) string {
	if len(tonic) == 0 {
		return tonic
	}
	if len(tonic) == 1 {
		return string(tonic[0]&^0x20) // uppercase single letter
	}
	// Capitalize first letter, keep accidental as is
	return string(tonic[0]&^0x20) + tonic[1:]
}

// isFlatKey determines if the tonic prefers flats
func isFlatKey(tonic string) bool {
	switch tonic {
	case "F", "Bb", "Eb", "Ab", "Db", "Gb",
		"d", "g", "c", "f", "bb", "eb":
		return true
	default:
		return false
	}
}
