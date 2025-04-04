package variablelengthquantity

import (
	"errors"
	"math"
)

// EncodeVarint encodes a slice of uint32 integers into a VLQ byte slice.
func EncodeVarint(input []uint32) []byte {
	var result []byte
	for _, val := range input {
		// Handle the zero case explicitly
		if val == 0 {
			result = append(result, 0x00)
			continue
		}

		var encodedBytes []byte
		// Extract 7 bits at a time until the value is zero
		for val > 0 {
			// Get the least significant 7 bits
			sevenBits := byte(val & 0x7f)
			val >>= 7
			// Prepend the byte (we build it in reverse order initially)
			encodedBytes = append([]byte{sevenBits}, encodedBytes...)
		}

		// Set the continuation bit (MSB) for all but the last byte
		for i := 0; i < len(encodedBytes)-1; i++ {
			encodedBytes[i] |= 0x80
		}
		result = append(result, encodedBytes...)
	}
	return result
}

// DecodeVarint decodes a VLQ byte slice into a slice of uint32 integers.
// Returns an error if the input is incomplete or represents an overflow.
func DecodeVarint(input []byte) ([]uint32, error) {
	var result []uint32
	var currentNum uint64 // Use uint64 to detect overflow before assigning to uint32
	expectingMoreBytes := false // Flag to track if the last byte indicated continuation

	for _, b := range input {
		// Check for potential overflow before shifting and adding
		if (currentNum >> (32 - 7)) > 0 {
			return nil, errors.New("overflow detected during decoding")
		}

		currentNum = (currentNum << 7) | uint64(b&0x7f)
		expectingMoreBytes = (b & 0x80) != 0 // Update flag based on current byte's MSB

		// Check for overflow after adding the new 7 bits
		if currentNum > math.MaxUint32 {
			return nil, errors.New("overflow detected during decoding")
		}

		// If this byte does not have the continuation bit set, finish the number
		if !expectingMoreBytes {
			result = append(result, uint32(currentNum))
			currentNum = 0 // Reset for the next number
		}
	}

	// After processing all bytes, if the last byte indicated we need more, it's incomplete.
	if expectingMoreBytes {
		return nil, errors.New("incomplete sequence")
	}

	return result, nil
}
