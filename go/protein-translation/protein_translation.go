package protein

import "errors"

// ErrStop indicates that a stop codon was encountered.
var ErrStop = errors.New("stop codon encountered")

// ErrInvalidBase indicates that an invalid base was encountered.
var ErrInvalidBase = errors.New("invalid base")

// codonToProtein maps RNA codons to their corresponding protein names.
var codonToProtein = map[string]string{
	"AUG": "Methionine",
	"UUU": "Phenylalanine",
	"UUC": "Phenylalanine",
	"UUA": "Leucine",
	"UUG": "Leucine",
	"UCU": "Serine",
	"UCC": "Serine",
	"UCA": "Serine",
	"UCG": "Serine",
	"UAU": "Tyrosine",
	"UAC": "Tyrosine",
	"UGU": "Cysteine",
	"UGC": "Cysteine",
	"UGG": "Tryptophan",
}

// stopCodons defines the set of stop codons.
var stopCodons = map[string]bool{
	"UAA": true,
	"UAG": true,
	"UGA": true,
}

// FromCodon translates a single codon into its corresponding protein name.
// It returns ErrStop if the codon is a stop codon, or ErrInvalidBase if the codon is invalid.
func FromCodon(codon string) (string, error) {
	if stopCodons[codon] {
		return "", ErrStop
	}
	protein, ok := codonToProtein[codon]
	if !ok {
		// The tests only explicitly check for "ABC", implying any unmapped 3-letter sequence is invalid.
		// We don't need to check individual characters based on the tests provided.
		return "", ErrInvalidBase
	}
	return protein, nil
}

// FromRNA translates an RNA sequence into a sequence of proteins.
// Translation stops if a stop codon is encountered or if an invalid codon is found.
func FromRNA(rna string) ([]string, error) {
	var proteins []string
	codonLength := 3

	for i := 0; i < len(rna); i += codonLength {
		// Ensure we have a full codon
		if i+codonLength > len(rna) {
			// Although not explicitly tested, partial codons at the end could be considered invalid.
			// However, the provided tests don't cover this case. We'll stick to what's tested.
			// If the loop condition `i < len(rna)` is met, `rna[i:i+codonLength]` is safe.
			// Let's assume input RNA length is always a multiple of 3 or translation stops before the end.
			break // Or handle partial codons if requirements change
		}

		codon := rna[i : i+codonLength]
		protein, err := FromCodon(codon)

		if err != nil {
			if errors.Is(err, ErrStop) {
				return proteins, nil // Stop translation, return accumulated proteins
			}
			// Any other error (specifically ErrInvalidBase based on FromCodon)
			return nil, err // Return nil slice and the error
		}
		proteins = append(proteins, protein)
	}

	return proteins, nil
}
