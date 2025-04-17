package palindrome

import (
	"errors"
	"fmt"
)

// Product represents a palindromic product and its factor pairs.
type Product struct {
	Product        int
	Factorizations [][2]int
}

// Products returns the smallest and largest palindromic products within the factor range.
func Products(fmin, fmax int) (Product, Product, error) {
	if fmin > fmax {
		return Product{}, Product{}, fmt.Errorf("fmin > fmax: %d > %d", fmin, fmax)
	}

	var pmin Product
	var pmax Product
	found := false

	for i := fmin; i <= fmax; i++ {
		for j := i; j <= fmax; j++ {
			p := i * j
			if isPalindrome(p) {
				if !found {
					// First palindrome found
					pmin = Product{p, [][2]int{{i, j}}}
					pmax = Product{p, [][2]int{{i, j}}}
					found = true
				} else {
					if p < pmin.Product {
						pmin = Product{p, [][2]int{{i, j}}}
					} else if p == pmin.Product {
						pmin.Factorizations = append(pmin.Factorizations, [2]int{i, j})
					}

					if p > pmax.Product {
						pmax = Product{p, [][2]int{{i, j}}}
					} else if p == pmax.Product {
						pmax.Factorizations = append(pmax.Factorizations, [2]int{i, j})
					}
				}
			}
		}
	}

	if !found {
		return Product{}, Product{}, errors.New("no palindromes")
	}

	return pmin, pmax, nil
}

// isPalindrome checks if an integer is a palindrome.
func isPalindrome(n int) bool {
	if n < 0 {
		n = -n
	}
	s := fmt.Sprintf("%d", n)
	for i := 0; i < len(s)/2; i++ {
		if s[i] != s[len(s)-1-i] {
			return false
		}
	}
	return true
}
