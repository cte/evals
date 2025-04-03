package variablelengthquantity

import "strconv"
func EncodeVarint(input []uint32) []byte {
	var output []byte
	for _, num := range input {
		if num == 0 {
			output = append(output, 0x00)
			continue
		}
		var bytesForNum []byte
		for num > 0 {
			b := byte(num & 0x7F)
			num >>= 7
			if len(bytesForNum) > 0 {
				b |= 0x80 // set continuation bit
			}
			bytesForNum = append([]byte{b}, bytesForNum...)
		}
		output = append(output, bytesForNum...)
	}
	return output
}

func DecodeVarint(input []byte) ([]uint32, error) {
	var output []uint32
	var value uint32
	var shift uint
	for i, b := range input {
		value <<= 7
		value |= uint32(b & 0x7F)
		if b&0x80 == 0 {
			output = append(output, value)
			value = 0
			shift = 0
		} else {
			shift += 7
			if shift >= 35 { // 5 bytes * 7 bits = 35 bits, overflow check
				return nil,  errorOverflow(i)
			}
		}
	}
	if shift != 0 {
		return nil, errorIncomplete()
	}
	return output, nil
}

func errorIncomplete() error {
	return &decodeError{"incomplete sequence"}
}

func errorOverflow(i int) error {
	return &decodeError{"overflow at byte index " + strconv.Itoa(i)}
}

type decodeError struct {
	msg string
}

func (e *decodeError) Error() string {
	return e.msg
}

