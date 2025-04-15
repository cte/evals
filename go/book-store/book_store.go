package bookstore


const bookPrice = 800 // Price in cents

// discountCosts maps the number of distinct books in a set to the total cost of that set in cents.
var discountCosts = map[int]int{
	1: 1 * bookPrice,                            // 0% discount
	2: 2 * bookPrice * 95 / 100,                 // 5% discount
	3: 3 * bookPrice * 90 / 100,                 // 10% discount
	4: 4 * bookPrice * 80 / 100,                 // 20% discount
	5: 5 * bookPrice * 75 / 100,                 // 25% discount
}

// Cost calculates the minimum cost for a basket of books considering discounts.
func Cost(books []int) int {
	if len(books) == 0 {
		return 0
	}

	// Count occurrences of each book
	bookCounts := make(map[int]int)
	for _, book := range books {
		bookCounts[book]++
	}

	// Form groups of distinct books greedily
	var groups []int
	for len(bookCounts) > 0 {
		currentGroupSize := 0
		var booksInGroup []int
		for bookID, count := range bookCounts {
			if count > 0 {
				currentGroupSize++
				booksInGroup = append(booksInGroup, bookID)
			}
		}

		if currentGroupSize == 0 {
			break // Should not happen if len(bookCounts) > 0 initially, but good practice
		}

		groups = append(groups, currentGroupSize)

		// Decrement counts for books included in this group
		for _, bookID := range booksInGroup {
			bookCounts[bookID]--
			if bookCounts[bookID] == 0 {
				delete(bookCounts, bookID) // Remove book type if count reaches zero
			}
		}
	}

	// Optimization: Prefer two groups of 4 over a group of 5 and a group of 3
	// Count 5s and 3s
	count5 := 0
	count3 := 0
	otherGroups := []int{}
	for _, size := range groups {
		switch size {
		case 5:
			count5++
		case 3:
			count3++
		default:
			otherGroups = append(otherGroups, size)
		}
	}

	// Perform replacements
	replacements := 0
	if count5 < count3 {
		replacements = count5
	} else {
		replacements = count3
	}


	optimizedGroups := otherGroups
	for i := 0; i < replacements; i++ {
		optimizedGroups = append(optimizedGroups, 4, 4) // Add two 4s for each (5, 3) pair
	}
	// Add remaining 5s and 3s
	for i := 0; i < count5-replacements; i++ {
		optimizedGroups = append(optimizedGroups, 5)
	}
	for i := 0; i < count3-replacements; i++ {
		optimizedGroups = append(optimizedGroups, 3)
	}


	// Calculate total cost from optimized groups
	totalCost := 0
	for _, size := range optimizedGroups {
		cost, exists := discountCosts[size]
		if exists {
			totalCost += cost
		}
		// Note: If size is 0 or > 5, it indicates an error in grouping logic,
		// but based on the algorithm, this shouldn't happen.
	}

	return totalCost
}
