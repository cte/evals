package say

func Say(n int64) (string, bool) {
	if n < 0 || n >= 1_000_000_000_000 {
		return "", false
	}
	if n == 0 {
		return "zero", true
	}

	var parts []string

	billions := n / 1_000_000_000
	if billions > 0 {
		parts = append(parts, sayBelowThousand(billions)+" billion")
		n %= 1_000_000_000
	}

	millions := n / 1_000_000
	if millions > 0 {
		parts = append(parts, sayBelowThousand(millions)+" million")
		n %= 1_000_000
	}

	thousands := n / 1_000
	if thousands > 0 {
		parts = append(parts, sayBelowThousand(thousands)+" thousand")
		n %= 1_000
	}

	if n > 0 {
		parts = append(parts, sayBelowThousand(n))
	}

	return joinParts(parts), true
}

func sayBelowThousand(n int64) string {
	var parts []string

	hundreds := n / 100
	if hundreds > 0 {
		parts = append(parts, digitToWord(hundreds)+" hundred")
		n %= 100
	}

	if n > 0 {
		parts = append(parts, sayBelowHundred(n))
	}

	return joinParts(parts)
}

func sayBelowHundred(n int64) string {
	if n < 20 {
		return digitToWord(n)
	}

	tens := n / 10
	ones := n % 10

	tensWord := tensToWord(tens * 10)
	if ones == 0 {
		return tensWord
	}
	return tensWord + "-" + digitToWord(ones)
}

func digitToWord(n int64) string {
	switch n {
	case 0:
		return "zero"
	case 1:
		return "one"
	case 2:
		return "two"
	case 3:
		return "three"
	case 4:
		return "four"
	case 5:
		return "five"
	case 6:
		return "six"
	case 7:
		return "seven"
	case 8:
		return "eight"
	case 9:
		return "nine"
	case 10:
		return "ten"
	case 11:
		return "eleven"
	case 12:
		return "twelve"
	case 13:
		return "thirteen"
	case 14:
		return "fourteen"
	case 15:
		return "fifteen"
	case 16:
		return "sixteen"
	case 17:
		return "seventeen"
	case 18:
		return "eighteen"
	case 19:
		return "nineteen"
	default:
		return ""
	}
}

func tensToWord(n int64) string {
	switch n {
	case 20:
		return "twenty"
	case 30:
		return "thirty"
	case 40:
		return "forty"
	case 50:
		return "fifty"
	case 60:
		return "sixty"
	case 70:
		return "seventy"
	case 80:
		return "eighty"
	case 90:
		return "ninety"
	default:
		return ""
	}
}

func joinParts(parts []string) string {
	result := ""
	for i, part := range parts {
		if i > 0 {
			result += " "
		}
		result += part
	}
	return result
}
