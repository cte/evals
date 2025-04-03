def encode(numbers):
    encoded_bytes = []
    for number in numbers:
        if number == 0:
            encoded_bytes.append(0)
            continue
        groups = []
        while number > 0:
            groups.append(number & 0x7F)
            number >>= 7
        for i in range(len(groups) - 1, -1, -1):
            byte = groups[i]
            if i != 0:
                byte |= 0x80  # set continuation bit
            encoded_bytes.append(byte)
    return encoded_bytes


def decode(bytes_):
    numbers = []
    value = 0
    in_sequence = False
    for byte in bytes_:
        value = (value << 7) | (byte & 0x7F)
        if byte & 0x80:
            in_sequence = True
        else:
            numbers.append(value)
            value = 0
            in_sequence = False
    if in_sequence:
        raise ValueError("incomplete sequence")
    return numbers
