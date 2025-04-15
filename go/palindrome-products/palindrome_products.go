package palindrome

import (
	"errors"
	"math"
	"strconv"
)

// Product holds a palindromic product and its factor pairs.
type Product struct {
	Product        int      // palindromic product
	Factorizations [][2]int // list of factor pairs, e.g., [ [f1, f2], [f3, f4], ... ]
}

var ErrNoSuchPalindrome = errors.New("no palindromes found")
var ErrLimits = errors.New("fmin > fmax")

// isPalindrome checks if a number is a palindrome.
func isPalindrome(n int) bool {
	s := strconv.Itoa(n)
	runes := []rune(s)
	for i, j := 0, len(runes)-1; i < j; i, j = i+1, j-1 {
		runes[i], runes[j] = runes[j], runes[i]
	}
	return s == string(runes)
}

// Products finds the smallest and largest palindrome products within a given factor range.
func Products(fmin, fmax int) (pmin Product, pmax Product, err error) {
	if fmin > fmax {
		return Product{}, Product{}, ErrLimits
	}

	pmin.Product = math.MaxInt64
	pmax.Product = -1 // Use -1 to indicate no palindrome found yet

	foundPalindrome := false

	for i := fmin; i <= fmax; i++ {
		for j := i; j <= fmax; j++ { // Start j from i to avoid duplicate pairs (like [2,1] after [1,2]) and ensure factors are ordered [a, b] where a <= b
			product := i * j

			if isPalindrome(product) {
				foundPalindrome = true

				// Check for smallest
				if product < pmin.Product {
					pmin.Product = product
					pmin.Factorizations = [][2]int{{i, j}}
				} else if product == pmin.Product {
					pmin.Factorizations = append(pmin.Factorizations, [2]int{i, j})
				}

				// Check for largest
				if product > pmax.Product {
					pmax.Product = product
					pmax.Factorizations = [][2]int{{i, j}}
				} else if product == pmax.Product {
					pmax.Factorizations = append(pmax.Factorizations, [2]int{i, j})
				}
			}
		}
	}

	if !foundPalindrome {
		return Product{}, Product{}, ErrNoSuchPalindrome
	}

	// If pmax.Product is still -1, it means the only palindromes found were <= 0,
	// which shouldn't happen with positive factors, but handle defensively.
	// However, the smallest could legitimately be 0 if fmin <= 0 <= fmax.
	// The tests likely assume positive factors, but the logic handles it.
	// If pmax wasn't updated, it means no palindrome > 0 was found.
	// If pmin wasn't updated from MaxInt64, no palindrome was found.
	// The foundPalindrome flag already covers the "no palindrome" case.

	return pmin, pmax, nil
}
