def encode(numbers):
    """Encodes a list of unsigned 32-bit integers into VLQ bytes."""
    vlq_bytes = []
    for number in numbers:
        if number == 0:
            vlq_bytes.append(0)
            continue

        num_bytes = []
        # Extract 7-bit chunks from right to left
        while number > 0:
            num_bytes.insert(0, number & 0x7F) # Get the lowest 7 bits
            number >>= 7

        # Set the MSB (continuation bit) for all but the last byte
        if len(num_bytes) == 0: # Handle case where input was 0, though covered above
             num_bytes.append(0)

        for i in range(len(num_bytes) - 1):
            num_bytes[i] |= 0x80

        vlq_bytes.extend(num_bytes)

    return vlq_bytes


def decode(bytes_):
    """Decodes a list of VLQ bytes into unsigned 32-bit integers."""
    numbers = []
    current_number = 0
    byte_count = 0
    start_index = 0 # Keep track of the start of the current number sequence for overflow check

    for i, byte in enumerate(bytes_):
        byte_count += 1

        # More precise overflow check:
        # A 32-bit number requires at most 5 VLQ bytes.
        # The first byte of a 5-byte sequence cannot have payload bits > 0x0F (15)
        if byte_count == 5 and (bytes_[start_index] & 0x7F) > 0x0F:
             raise ValueError("Overflow error: decoded value exceeds 32 bits")
        # If we already have a large number and are adding more bits, check potential overflow
        if current_number > (0xFFFFFFFF >> 7):
             raise ValueError("Overflow error: decoded value exceeds 32 bits")


        current_number = (current_number << 7) | (byte & 0x7F)

        if not (byte & 0x80): # Check if the continuation bit (MSB) is 0 (last byte)
            # Final overflow check after constructing the number
            # This check is technically redundant if the check inside the loop is correct,
            # but serves as a safeguard.
            if current_number > 0xFFFFFFFF:
                 raise ValueError("Overflow error: decoded value exceeds 32 bits")

            numbers.append(current_number)
            current_number = 0
            byte_count = 0
            start_index = i + 1 # Next number starts after this byte

    # Check if the sequence ended prematurely (last byte had continuation bit set)
    if byte_count > 0:
        raise ValueError("incomplete sequence")

    return numbers
