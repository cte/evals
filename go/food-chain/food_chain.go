package foodchain

import "strings"

type animalInfo struct {
	name   string
	remark string
}

var animals = []animalInfo{
	{name: "fly", remark: "I don't know why she swallowed the fly. Perhaps she'll die."},
	{name: "spider", remark: "It wriggled and jiggled and tickled inside her."},
	{name: "bird", remark: "How absurd to swallow a bird!"},
	{name: "cat", remark: "Imagine that, to swallow a cat!"},
	{name: "dog", remark: "What a hog, to swallow a dog!"},
	{name: "goat", remark: "Just opened her throat and swallowed a goat!"},
	{name: "cow", remark: "I don't know how she swallowed a cow!"},
	{name: "horse", remark: "She's dead, of course!"},
}

// Verse generates a single verse of the song.
func Verse(v int) string {
	if v < 1 || v > len(animals) {
		return "" // Or handle error appropriately
	}
	idx := v - 1 // Adjust to 0-based index
	var sb strings.Builder

	// First line
	sb.WriteString("I know an old lady who swallowed a ")
	sb.WriteString(animals[idx].name)
	sb.WriteString(".\n")

	// Remark (except for fly, which is handled later, and horse)
	if idx > 0 && idx < len(animals)-1 {
		sb.WriteString(animals[idx].remark)
		sb.WriteString("\n")
	}

	// Special case for the last verse (horse)
	if idx == len(animals)-1 {
		sb.WriteString(animals[idx].remark)
		return sb.String()
	}

	// Cumulative lines
	for i := idx; i > 0; i-- {
		sb.WriteString("She swallowed the ")
		sb.WriteString(animals[i].name)
		sb.WriteString(" to catch the ")
		sb.WriteString(animals[i-1].name)
		// Special line for spider catching fly
		if i-1 == 1 { // animals[i-1] is spider
			sb.WriteString(" that wriggled and jiggled and tickled inside her")
		}
		sb.WriteString(".\n")
	}

	// Last line (remark for the fly)
	sb.WriteString(animals[0].remark)

	return sb.String()
}

// Verses generates a range of verses from start to end (inclusive).
func Verses(start, end int) string {
	if start < 1 || end > len(animals) || start > end {
		return "" // Or handle error
	}
	var sb strings.Builder
	for i := start; i <= end; i++ {
		sb.WriteString(Verse(i))
		if i < end {
			sb.WriteString("\n\n") // Add blank line between verses
		}
	}
	return sb.String()
}

// Song generates the entire song.
func Song() string {
	return Verses(1, len(animals))
}
