package piglatin

import "strings"

func Sentence(sentence string) string {
	words := strings.Split(sentence, " ")
	for i, word := range words {
		words[i] = translateWord(word)
	}
	return strings.Join(words, " ")
}

func translateWord(word string) string {
	if startsWithVowelSound(word) {
		return word + "ay"
	}
	
	// Handle consonant clusters
	for _, cluster := range []string{"squ", "qu", "thr", "th", "sch", "ch"} {
		if strings.HasPrefix(word, cluster) {
			return word[len(cluster):] + cluster + "ay"
		}
	}

	// Move leading consonants to the end
	for i := 0; i < len(word); i++ {
		if isVowel(word[i]) || (word[i] == 'y' && i != 0) {
			return word[i:] + word[:i] + "ay"
		}
	}
	return word + "ay"
}

func startsWithVowelSound(word string) bool {
	return strings.HasPrefix(word, "xr") ||
		strings.HasPrefix(word, "yt") ||
		isVowel(word[0])
}

func isVowel(b byte) bool {
	return b == 'a' || b == 'e' || b == 'i' || b == 'o' || b == 'u'
}
