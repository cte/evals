import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

class VariableLengthQuantity {

    List<String> encode(List<Long> numbers) {
        List<String> encodedBytes = new ArrayList<>();

        for (long number : numbers) {
            // Ensure number fits within 32-bit unsigned range for this exercise
            if (number < 0 || number > 0xFFFFFFFFL) {
                 throw new IllegalArgumentException("Number out of 32-bit unsigned range: " + number);
            }

            LinkedList<String> currentNumberBytes = new LinkedList<>();
            long tempNum = number;

            // Handle zero separately as the loop condition won't catch it
            if (tempNum == 0) {
                currentNumberBytes.addFirst("0x0");
            } else {
                do {
                    long sevenBits = tempNum & 0x7f; // Get the lowest 7 bits
                    tempNum >>>= 7; // Unsigned right shift by 7

                    // If tempNum is 0 now, this 'sevenBits' chunk is the last (least significant)
                    // byte in the original number's VLQ sequence, so it doesn't get the MSB set.
                    // Otherwise, set the MSB.
                    if (tempNum != 0 || currentNumberBytes.isEmpty()) { // Check if more bytes follow OR if it's the first byte being processed (which implies it's not the *very last* byte unless it's a single-byte number)
                         // Correction: The logic should be simpler. The *first* byte extracted (least significant 7 bits)
                         // becomes the *last* byte in the VLQ sequence. Only this last byte has MSB cleared.
                         // All preceding bytes (extracted earlier in the loop) need MSB set.
                         // Let's rethink the loop structure slightly.

                         // Alternative approach: Extract all 7-bit chunks first, then set MSBs.
                         // Let's stick to the original plan but fix the MSB logic.

                         // If this is NOT the first byte added (meaning it's not the last byte of the sequence)
                         // OR if the original number required more than 7 bits (tempNum was > 0 before shift)
                         // then set the MSB.
                         // Let's try adding MSB first and removing it for the last one.

                         // Extract 7 bits
                         // Shift number
                         // Add 0x80 to the extracted bits
                         // Add hex string to front of list
                    }
                    // This logic is getting complicated. Let's simplify.

                } while (tempNum > 0); // This loop structure is problematic for setting MSB correctly.

                // --- Revised Encoding Logic ---
                tempNum = number; // Reset tempNum
                if (tempNum == 0) {
                     currentNumberBytes.addFirst("0x0");
                } else {
                    // Extract the first 7 bits (least significant)
                    long firstByte = tempNum & 0x7f;
                    tempNum >>>= 7;
                    currentNumberBytes.addFirst("0x" + Long.toHexString(firstByte)); // Add last byte (MSB clear)

                    // Process remaining 7-bit chunks
                    while (tempNum > 0) {
                        long nextByte = tempNum & 0x7f;
                        tempNum >>>= 7;
                        nextByte |= 0x80; // Set MSB for intermediate bytes
                        currentNumberBytes.addFirst("0x" + Long.toHexString(nextByte));
                    }
                }
            }
            encodedBytes.addAll(currentNumberBytes);
        }
        return encodedBytes;
    }

    List<String> decode(List<Long> bytes) {
        List<String> decodedNumbers = new ArrayList<>();
        long currentNumber = 0;
        boolean numberComplete = true; // Start assuming complete until we see a byte with MSB set

        for (long vlqByte : bytes) {
             if (vlqByte < 0 || vlqByte > 0xFF) {
                 throw new IllegalArgumentException("Input bytes must be 0-255");
             }

            // Check for potential overflow before shifting and adding
            // If currentNumber already has bits set in the high 7 places, shifting left by 7 will lose them.
            // Since we are aiming for a 32-bit result (max 0xFFFFFFFF), check if currentNumber exceeds max >> 7
             if (currentNumber > (0xFFFFFFFFL >>> 7)) {
                 throw new ArithmeticException("Overflow detected during VLQ decoding");
             }

            currentNumber <<= 7;
            currentNumber |= (vlqByte & 0x7f);
            numberComplete = (vlqByte & 0x80) == 0; // Is MSB clear?

            if (numberComplete) {
                // Check if the final number exceeds 32-bit unsigned limit
                if (currentNumber > 0xFFFFFFFFL) {
                     throw new ArithmeticException("Decoded value exceeds 32-bit unsigned limit");
                }
                decodedNumbers.add("0x" + Long.toHexString(currentNumber));
                currentNumber = 0; // Reset for next number
            }
        }

        // After loop, if the last byte had MSB set, the sequence is incomplete
        if (!numberComplete) {
            throw new IllegalArgumentException("Invalid variable-length quantity encoding");
        }

        return decodedNumbers;
    }
}
