import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AffineCipher {

    private static final int M = 26; // Size of the alphabet

    // Calculates the greatest common divisor of two integers
    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // Calculates the modular multiplicative inverse of a modulo m
    // Throws ArithmeticException if no inverse exists (i.e., a and m are not coprime)
    private int mmi(int a, int m) {
        if (gcd(a, m) != 1) {
            // Should be caught by the check in encode/decode, but good practice
            throw new ArithmeticException("a and m must be coprime to find the modular inverse.");
        }
        // Find x such that (a * x) % m == 1
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        // Should not be reached if gcd(a, m) == 1
        throw new ArithmeticException("Modular inverse does not exist.");
    }

    public String encode(String text, int a, int b) {
        if (gcd(a, M) != 1) {
            throw new IllegalArgumentException("Error: keyA and alphabet size must be coprime.");
        }

        StringBuilder processedText = new StringBuilder();
        for (char c : text.toLowerCase().toCharArray()) {
            if (Character.isLetter(c)) {
                int i = c - 'a';
                char encodedChar = (char) ('a' + (a * i + b) % M);
                processedText.append(encodedChar);
            } else if (Character.isDigit(c)) {
                processedText.append(c);
            }
        }

        // Add spaces every 5 characters
        return IntStream.range(0, processedText.length())
                .mapToObj(i -> (i > 0 && i % 5 == 0 ? " " : "") + processedText.charAt(i))
                .collect(Collectors.joining(""));
    }

    public String decode(String cipherText, int a, int b) {
        if (gcd(a, M) != 1) {
            throw new IllegalArgumentException("Error: keyA and alphabet size must be coprime.");
        }

        int mmi = mmi(a, M);
        StringBuilder decodedText = new StringBuilder();

        // Remove spaces and process
        for (char c : cipherText.replaceAll("\\s", "").toCharArray()) {
            if (Character.isLetter(c)) {
                int y = c - 'a';
                // Apply decryption formula: D(y) = mmi * (y - b) mod m
                // Handle potential negative result of (y - b)
                int decodedIndex = (mmi * (y - b % M + M)) % M;
                char decodedChar = (char) ('a' + decodedIndex);
                decodedText.append(decodedChar);
            } else if (Character.isDigit(c)) {
                decodedText.append(c);
            }
        }

        return decodedText.toString();
    }
}