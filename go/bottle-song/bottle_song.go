package bottlesong

func Recite(startBottles, takeDown int) []string {
	var result []string

	for i := 0; i < takeDown; i++ {
		count := startBottles - i
		nextCount := count - 1

		countWord := bottleWord(count, true)
		nextCountWord := bottleWord(nextCount, false)

		result = append(result,
			countWord+" green "+pluralize("bottle", count)+" hanging on the wall,",
			countWord+" green "+pluralize("bottle", count)+" hanging on the wall,",
			"And if one green bottle should accidentally fall,",
			"There'll be "+nextCountWord+" green "+pluralize("bottle", nextCount)+" hanging on the wall.",
		)

		if i != takeDown-1 {
			result = append(result, "")
		}
	}

	return result
}

func bottleWord(n int, capitalize bool) string {
	var word string
	switch n {
	case 0:
		word = "no"
	case 1:
		word = "one"
	case 2:
		word = "two"
	case 3:
		word = "three"
	case 4:
		word = "four"
	case 5:
		word = "five"
	case 6:
		word = "six"
	case 7:
		word = "seven"
	case 8:
		word = "eight"
	case 9:
		word = "nine"
	case 10:
		word = "ten"
	default:
		word = ""
	}

	if capitalize && len(word) > 0 {
		return string(word[0]-32) + word[1:]
	}
	return word
}

func pluralize(word string, count int) string {
	if count == 1 {
		return word
	}
	return word + "s"
}
