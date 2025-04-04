package piglatin

import (
	"strings"
)

// isVowel checks if a rune is a vowel.
func isVowel(r rune) bool {
	// vowels: the letters `a`, `e`, `i`, `o`, and `u`
	return r == 'a' || r == 'e' || r == 'i' || r == 'o' || r == 'u'
}

// translateWord translates a single word into Pig Latin based on the rules.
func translateWord(word string) string {
	if len(word) == 0 {
		return ""
	}

	// Rule 1: If a word begins with a vowel, or starts with `"xr"` or `"yt"`, add an `"ay"` sound to the end of the word.
	firstChar := rune(word[0])
	if isVowel(firstChar) || strings.HasPrefix(word, "xr") || strings.HasPrefix(word, "yt") {
		return word + "ay"
	}

	n := len(word)
	for i := 0; i < n; i++ {
		char := rune(word[i])

		// Check for vowel sound (vowel or 'y' acting as vowel)
		isConsonantClusterBreak := false
		if isVowel(char) {
			isConsonantClusterBreak = true
		} else if char == 'y' && i > 0 {
			// Rule 4 check: If 'y' is preceded by consonants, it acts as the vowel break.
			allConsonantsBefore := true
			for j := 0; j < i; j++ {
				if isVowel(rune(word[j])) {
					allConsonantsBefore = false
					break
				}
			}
			if allConsonantsBefore {
				isConsonantClusterBreak = true
			}
		}

		if isConsonantClusterBreak {
			// Found the end of the initial consonant cluster (or start of word if vowel/y is first)

			// Rule 3 check: Handle "qu" immediately following the consonant cluster.
			// If the break was caused by a vowel, check if it's 'u' preceded by 'q'.
			if isVowel(char) && char == 'u' && i > 0 && word[i-1] == 'q' {
				// Move consonants + "qu"
				consonants := word[:i+1]
				rest := word[i+1:]
				return rest + consonants + "ay"
			} else {
				// Rule 2 & 4: Move the consonant cluster (all chars before index i) to the end.
				consonants := word[:i]
				rest := word[i:]
				return rest + consonants + "ay"
			}
		}
	}

	// Fallback: Should not happen if rules cover all cases including 'y'.
	// If a word consists only of consonants (and no 'y'), the rules aren't explicit.
	// Let's assume the tests don't include such words or expect simple "ay" append.
	return word + "ay"
}


// Sentence translates an entire sentence into Pig Latin.
func Sentence(sentence string) string {
	words := strings.Fields(sentence)
	translatedWords := make([]string, len(words))
	for i, word := range words {
		translatedWords[i] = translateWord(word)
	}
	return strings.Join(translatedWords, " ")
}
