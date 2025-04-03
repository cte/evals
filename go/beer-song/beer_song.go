package beer

import (
	"fmt"
)
func Song() string {
	song, _ := Verses(99, 0)
	return song
}

func Verses(start, stop int) (string, error) {
	if start > 99 || start < 0 || stop > 99 || stop < 0 || start < stop {
		return "", fmt.Errorf("invalid range")
	}

	var verses []string
	for i := start; i >= stop; i-- {
		v, err := Verse(i)
		if err != nil {
			return "", err
		}
		verses = append(verses, v)
	}

	return joinVerses(verses), nil
}

func Verse(n int) (string, error) {
	if n > 99 || n < 0 {
		return "", fmt.Errorf("invalid verse number")
	}

	switch n {
	case 0:
		return "No more bottles of beer on the wall, no more bottles of beer.\n" +
			"Go to the store and buy some more, 99 bottles of beer on the wall.\n", nil
	case 1:
		return "1 bottle of beer on the wall, 1 bottle of beer.\n" +
			"Take it down and pass it around, no more bottles of beer on the wall.\n", nil
	case 2:
		return "2 bottles of beer on the wall, 2 bottles of beer.\n" +
			"Take one down and pass it around, 1 bottle of beer on the wall.\n", nil
	default:
		return fmt.Sprintf("%d bottles of beer on the wall, %d bottles of beer.\n"+
			"Take one down and pass it around, %d bottles of beer on the wall.\n", n, n, n-1), nil
	}
}

func joinVerses(verses []string) string {
	if len(verses) == 0 {
		return ""
	}
	for i, v := range verses {
		// remove trailing newline from each verse
		if len(v) > 0 && v[len(v)-1] == '\n' {
			verses[i] = v[:len(v)-1]
		}
	}
	return fmt.Sprintf("%s\n\n", joinWithDoubleNewline(verses))
}

func joinWithDoubleNewline(verses []string) string {
	if len(verses) == 0 {
		return ""
	}
	result := verses[0]
	for _, v := range verses[1:] {
		result += "\n\n" + v
	}
	return result
}
