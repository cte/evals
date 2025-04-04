package zebra

// Define constants for properties
type Color int

const (
	Red Color = iota
	Green
	Ivory
	Yellow
	Blue
)

var Colors = []Color{Red, Green, Ivory, Yellow, Blue}

func (c Color) String() string { return []string{"Red", "Green", "Ivory", "Yellow", "Blue"}[c] }

type Nationality int

const (
	Englishman Nationality = iota
	Spaniard
	Ukrainian
	Norwegian
	Japanese
)

var Nationalities = []Nationality{Englishman, Spaniard, Ukrainian, Norwegian, Japanese}

func (n Nationality) String() string {
	return []string{"Englishman", "Spaniard", "Ukrainian", "Norwegian", "Japanese"}[n]
}

type Pet int

const (
	Dog Pet = iota
	Snails
	Fox
	Horse
	Zebra
)

var Pets = []Pet{Dog, Snails, Fox, Horse, Zebra}

func (p Pet) String() string { return []string{"Dog", "Snails", "Fox", "Horse", "Zebra"}[p] }

type Drink int

const (
	Coffee Drink = iota
	Tea
	Milk
	OrangeJuice
	Water
)

var Drinks = []Drink{Coffee, Tea, Milk, OrangeJuice, Water}

func (d Drink) String() string { return []string{"Coffee", "Tea", "Milk", "Orange Juice", "Water"}[d] }

type Cigarette int

const (
	OldGold Cigarette = iota
	Kools
	Chesterfields
	LuckyStrike
	Parliaments
)

var Cigarettes = []Cigarette{OldGold, Kools, Chesterfields, LuckyStrike, Parliaments}

func (c Cigarette) String() string {
	return []string{"Old Gold", "Kools", "Chesterfields", "Lucky Strike", "Parliaments"}[c]
}

// House structure
type House struct {
	Position    int // 0-4
	Color       Color
	Nationality Nationality
	Pet         Pet
	Drink       Drink
	Cigarette   Cigarette
}

type Solution struct {
	DrinksWater string
	OwnsZebra   string
}

// permutations generates all permutations of integers from 0 to n-1.
// Uses Heap's algorithm.
func permutations(n int) [][]int {
	p := make([]int, n)
	for i := 0; i < n; i++ {
		p[i] = i
	}

	var result [][]int
	var generate func(k int, arr []int)

	generate = func(k int, arr []int) {
		if k == 1 {
			perm := make([]int, n)
			copy(perm, arr)
			result = append(result, perm)
			return
		}

		// Generate permutations for k-1 recursively
		generate(k-1, arr)

		// Generate permutations for k by swapping the (k-1)th element
		for i := 0; i < k-1; i++ {
			// Swap based on whether k is odd or even
			if k%2 == 0 {
				arr[i], arr[k-1] = arr[k-1], arr[i]
			} else {
				arr[0], arr[k-1] = arr[k-1], arr[0]
			}
			generate(k-1, arr)
		}
	}

	generate(n, p)
	return result
}

// abs helper function
func abs(x int) int {
	if x < 0 {
		return -x
	}
	return x
}

// Helper function to find house by property
func findHouseByNat(houses []House, nat Nationality) *House {
	for i := range houses {
		if houses[i].Nationality == nat {
			return &houses[i]
		}
	}
	return nil
}
func findHouseByColor(houses []House, col Color) *House {
	for i := range houses {
		if houses[i].Color == col {
			return &houses[i]
		}
	}
	return nil
}
func findHouseByPet(houses []House, pet Pet) *House {
	for i := range houses {
		if houses[i].Pet == pet {
			return &houses[i]
		}
	}
	return nil
}
// func findHouseByDrink(houses []House, dri Drink) *House {
// 	for i := range houses { if houses[i].Drink == dri { return &houses[i] } }
// 	return nil
// }
func findHouseByCig(houses []House, cig Cigarette) *House {
	for i := range houses {
		if houses[i].Cigarette == cig {
			return &houses[i]
		}
	}
	return nil
}

// Helper function to find position by property
// func findPosByNat(houses []House, nat Nationality) int {
// 	for i := range houses { if houses[i].Nationality == nat { return i } }
// 	return -1
// }
func findPosByColor(houses []House, col Color) int {
	for i := range houses {
		if houses[i].Color == col {
			return i
		}
	}
	return -1
}
func findPosByPet(houses []House, pet Pet) int {
	for i := range houses {
		if houses[i].Pet == pet {
			return i
		}
	}
	return -1
}
// func findPosByDrink(houses []House, dri Drink) int {
// 	for i := range houses { if houses[i].Drink == dri { return i } }
// 	return -1
// }
func findPosByCig(houses []House, cig Cigarette) int {
	for i := range houses {
		if houses[i].Cigarette == cig {
			return i
		}
	}
	return -1
}

// Function to check all constraints for a given house configuration
func checkConstraints(houses []House) bool {
	// Constraints checked earlier by pruning: 6, 9, 10, 15

	// 2. The Englishman lives in the red house.
	hEng := findHouseByNat(houses, Englishman)
	if hEng == nil || hEng.Color != Red {
		return false
	}

	// 3. The Spaniard owns the dog.
	hSpa := findHouseByNat(houses, Spaniard)
	if hSpa == nil || hSpa.Pet != Dog {
		return false
	}

	// 4. Coffee is drunk in the green house.
	hGreen := findHouseByColor(houses, Green)
	if hGreen == nil || hGreen.Drink != Coffee {
		return false
	}

	// 5. The Ukrainian drinks tea.
	hUkr := findHouseByNat(houses, Ukrainian)
	if hUkr == nil || hUkr.Drink != Tea {
		return false
	}

	// 7. The Old Gold smoker owns snails.
	hOG := findHouseByCig(houses, OldGold)
	if hOG == nil || hOG.Pet != Snails {
		return false
	}

	// 8. Kools are smoked in the yellow house.
	hYellow := findHouseByColor(houses, Yellow)
	if hYellow == nil || hYellow.Cigarette != Kools {
		return false
	}

	// 11. The man who smokes Chesterfields lives in the house next to the man with the fox.
	chesterPos := findPosByCig(houses, Chesterfields)
	foxPos := findPosByPet(houses, Fox)
	if chesterPos == -1 || foxPos == -1 || abs(chesterPos-foxPos) != 1 {
		return false
	}

	// 12. Kools are smoked in the house next to the house where the horse is kept.
	koolsPos := findPosByCig(houses, Kools) // Already found via hYellow
	horsePos := findPosByPet(houses, Horse)
	if koolsPos == -1 || horsePos == -1 || abs(koolsPos-horsePos) != 1 {
		return false
	}

	// 13. The Lucky Strike smoker drinks orange juice.
	hLS := findHouseByCig(houses, LuckyStrike)
	if hLS == nil || hLS.Drink != OrangeJuice {
		return false
	}

	// 14. The Japanese smokes Parliaments.
	hJap := findHouseByNat(houses, Japanese)
	if hJap == nil || hJap.Cigarette != Parliaments {
		return false
	}

	// All constraints passed
	return true
}

func SolvePuzzle() Solution {
	perms := permutations(5) // Generate permutations of [0, 1, 2, 3, 4]

	// Iterate through all possible assignments (permutations) for each category
	for _, natPerm := range perms { // Nationality permutation
		houses := make([]House, 5)
		for i := 0; i < 5; i++ {
			houses[i].Position = i
			// Assign nationality based on the current permutation
			houses[i].Nationality = Nationality(natPerm[i])
		}

		// Constraint 10: The Norwegian lives in the first house (index 0).
		if houses[0].Nationality != Norwegian {
			continue
		}

		for _, colPerm := range perms { // Color permutation
			for i := 0; i < 5; i++ {
				houses[i].Color = Color(colPerm[i])
			}

			// Constraint 15: The Norwegian lives next to the blue house.
			// Since Norwegian is in house 0, Blue must be in house 1 (index 1).
			if houses[1].Color != Blue {
				continue
			}

			// Constraint 6: The green house is immediately to the right of the ivory house.
			ivoryPos, greenPos := -1, -1
			for i := 0; i < 5; i++ {
				if houses[i].Color == Ivory {
					ivoryPos = i
				}
				if houses[i].Color == Green {
					greenPos = i
				}
			}
			// Check if found and if green is right of ivory
			if ivoryPos == -1 || greenPos == -1 || greenPos != ivoryPos+1 {
				continue
			}

			for _, petPerm := range perms { // Pet permutation
				for i := 0; i < 5; i++ {
					houses[i].Pet = Pet(petPerm[i])
				}

				for _, driPerm := range perms { // Drink permutation
					for i := 0; i < 5; i++ {
						houses[i].Drink = Drink(driPerm[i])
					}

					// Constraint 9: Milk is drunk in the middle house (index 2).
					if houses[2].Drink != Milk {
						continue
					}

					for _, cigPerm := range perms { // Cigarette permutation
						for i := 0; i < 5; i++ {
							houses[i].Cigarette = Cigarette(cigPerm[i])
						}

						// Check all remaining constraints
						if checkConstraints(houses) {
							// Found the solution
							waterDrinker := ""
							zebraOwner := ""
							for _, h := range houses {
								if h.Drink == Water {
									waterDrinker = h.Nationality.String()
								}
								if h.Pet == Zebra {
									zebraOwner = h.Nationality.String()
								}
							}
							// Ensure both were found before returning
							if waterDrinker != "" && zebraOwner != "" {
								return Solution{DrinksWater: waterDrinker, OwnsZebra: zebraOwner}
							}
							// If only one is found, something is wrong with the logic or constraints
						}
					}
				}
			}
		}
	}

	// Should not happen if a solution exists and logic is correct
	panic("Solution not found")
}
