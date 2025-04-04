package dndcharacter

import (
	"math"
	"math/rand"
	"sort"
	"time"
)

type Character struct {
	Strength     int
	Dexterity    int
	Constitution int
	Intelligence int
	Wisdom       int
	Charisma     int
	Hitpoints    int
}

// Modifier calculates the ability modifier for a given ability score
func Modifier(score int) int {
	// The modifier is calculated as floor((score - 10) / 2)
	return int(math.Floor(float64(score-10) / 2.0))
}

// Ability uses randomness to generate the score for an ability
// It simulates rolling 4 6-sided dice and dropping the lowest score.
func Ability() int {
	// Seed the random number generator only once
	// Note: In a real application, seeding should happen at application start.
	// For this exercise, seeding here is acceptable for testability.
	// Using time.Now().UnixNano() for better randomness.
	// rand.Seed(time.Now().UnixNano()) // Deprecated since Go 1.20
	// No explicit seeding needed with Go 1.20+ math/rand/v2 or the global rand

	rolls := make([]int, 4)
	for i := 0; i < 4; i++ {
		// rand.Intn(n) returns a random int in [0, n). Add 1 for [1, 6].
		rolls[i] = rand.Intn(6) + 1
	}

	// Sort the rolls to easily find the lowest
	sort.Ints(rolls)

	// Sum the highest 3 rolls
	score := 0
	for i := 1; i < 4; i++ {
		score += rolls[i]
	}
	return score
}

// GenerateCharacter creates a new Character with random scores for abilities
func GenerateCharacter() Character {
	constitution := Ability()
	return Character{
		Strength:     Ability(),
		Dexterity:    Ability(),
		Constitution: constitution,
		Intelligence: Ability(),
		Wisdom:       Ability(),
		Charisma:     Ability(),
		Hitpoints:    10 + Modifier(constitution),
	}
}

// Seed the random number generator once when the package is loaded.
// This is the preferred way over seeding in Ability() for Go 1.20+
func init() {
	rand.Seed(time.Now().UnixNano())
}
