package zebra

type Solution struct {
	DrinksWater string
	OwnsZebra   string
}

func SolvePuzzle() Solution {
	colors := []string{"red", "green", "ivory", "yellow", "blue"}
	nationalities := []string{"Englishman", "Spaniard", "Ukrainian", "Norwegian", "Japanese"}
	drinks := []string{"coffee", "tea", "milk", "orange juice", "water"}
	smokes := []string{"Old Gold", "Kools", "Chesterfields", "Lucky Strike", "Parliaments"}
	pets := []string{"dog", "snails", "fox", "horse", "zebra"}

	permute := func(arr []string) [][]string {
		var helper func([]string, int)
		res := [][]string{}

		helper = func(a []string, n int) {
			if n == 1 {
				tmp := make([]string, len(a))
				copy(tmp, a)
				res = append(res, tmp)
			} else {
				for i := 0; i < n; i++ {
					helper(a, n-1)
					if n%2 == 1 {
						a[0], a[n-1] = a[n-1], a[0]
					} else {
						a[i], a[n-1] = a[n-1], a[i]
					}
				}
			}
		}
		helper(arr, len(arr))
		return res
	}

	for _, color := range permute(colors) {
		for _, nat := range permute(nationalities) {
			// Clues 10 and 26: Norwegian in first house, Norwegian next to blue house
			if nat[0] != "Norwegian" {
				continue
			}
			blueIdx := -1
			for i, c := range color {
				if c == "blue" {
					blueIdx = i
					break
				}
			}
			if blueIdx == -1 || (blueIdx != 1 && blueIdx != 0) {
				continue
			}
			if blueIdx == 1 && nat[0] != "Norwegian" && nat[1] != "Norwegian" {
				continue
			}
			if blueIdx == 0 && nat[1] != "Norwegian" {
				continue
			}

			for _, drink := range permute(drinks) {
				// Clue 9: Milk in middle house
				if drink[2] != "milk" {
					continue
				}
				for _, smoke := range permute(smokes) {
					for _, pet := range permute(pets) {
						valid := true

						for i := 0; i < 5; i++ {
							// Clue 2: Englishman in red house
							if nat[i] == "Englishman" && color[i] != "red" {
								valid = false
								break
							}
							// Clue 3: Spaniard owns dog
							if nat[i] == "Spaniard" && pet[i] != "dog" {
								valid = false
								break
							}
							// Clue 4: Coffee in green house
							if drink[i] == "coffee" && color[i] != "green" {
								valid = false
								break
							}
							// Clue 5: Ukrainian drinks tea
							if nat[i] == "Ukrainian" && drink[i] != "tea" {
								valid = false
								break
							}
							// Clue 7: Old Gold smoker owns snails
							if smoke[i] == "Old Gold" && pet[i] != "snails" {
								valid = false
								break
							}
							// Clue 8: Kools smoked in yellow house
							if smoke[i] == "Kools" && color[i] != "yellow" {
								valid = false
								break
							}
							// Clue 13: Lucky Strike smoker drinks orange juice
							if smoke[i] == "Lucky Strike" && drink[i] != "orange juice" {
								valid = false
								break
							}
							// Clue 14: Japanese smokes Parliaments
							if nat[i] == "Japanese" && smoke[i] != "Parliaments" {
								valid = false
								break
							}
						}

						if !valid {
							continue
						}

						// Clue 6: Green house immediately right of ivory
						greenIdx := -1
						ivoryIdx := -1
						for i := 0; i < 5; i++ {
							if color[i] == "green" {
								greenIdx = i
							}
							if color[i] == "ivory" {
								ivoryIdx = i
							}
						}
						if greenIdx != ivoryIdx+1 {
							continue
						}

						// Clue 11: Chesterfields next to fox
						for i := 0; i < 5; i++ {
							if smoke[i] == "Chesterfields" {
								if (i > 0 && pet[i-1] == "fox") || (i < 4 && pet[i+1] == "fox") {
									goto checkHorse
								}
							}
						}
						continue

					checkHorse:
						// Clue 12: Kools next to horse
						for i := 0; i < 5; i++ {
							if smoke[i] == "Kools" {
								if (i > 0 && pet[i-1] == "horse") || (i < 4 && pet[i+1] == "horse") {
									goto found
								}
							}
						}
						continue

					found:
						var waterDrinker, zebraOwner string
						for i := 0; i < 5; i++ {
							if drink[i] == "water" {
								waterDrinker = nat[i]
							}
							if pet[i] == "zebra" {
								zebraOwner = nat[i]
							}
						}
						return Solution{DrinksWater: waterDrinker, OwnsZebra: zebraOwner}
					}
				}
			}
		}
	}
	return Solution{}
}
