public class AffineCipher {

    private static final int ALPHABET_SIZE = 26;

    private boolean isCoprime(int a, int m) {
        return gcd(a, m) == 1;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    private int mod(int a, int m) {
        int result = a % m;
        return result < 0 ? result + m : result;
    }

    private int modularInverse(int a, int m) {
        int t = 0, newT = 1;
        int r = m, newR = a;

        while (newR != 0) {
            int quotient = r / newR;
            int tempT = t - quotient * newT;
            t = newT;
            newT = tempT;

            int tempR = r - quotient * newR;
            r = newR;
            newR = tempR;
        }

        if (r > 1) {
            throw new IllegalArgumentException("Error: keyA and alphabet size must be coprime.");
        }
        if (t < 0) {
            t += m;
        }
        return t;
    }

    public String encode(String text, int a, int b) {
        if (!isCoprime(a, ALPHABET_SIZE)) {
            throw new IllegalArgumentException("Error: keyA and alphabet size must be coprime.");
        }

        StringBuilder encoded = new StringBuilder();
        int groupCount = 0;

        for (char ch : text.toLowerCase().toCharArray()) {
            if (Character.isLetter(ch)) {
                int x = ch - 'a';
                int encrypted = mod(a * x + b, ALPHABET_SIZE);
                char encChar = (char) ('a' + encrypted);
                encoded.append(encChar);
                groupCount++;
            } else if (Character.isDigit(ch)) {
                encoded.append(ch);
                groupCount++;
            } else {
                continue; // ignore spaces and punctuation
            }

            if (groupCount % 5 == 0) {
                encoded.append(' ');
            }
        }

        String result = encoded.toString().trim();
        return result;
    }

    public String decode(String text, int a, int b) {
        if (!isCoprime(a, ALPHABET_SIZE)) {
            throw new IllegalArgumentException("Error: keyA and alphabet size must be coprime.");
        }

        int a_inv = modularInverse(a, ALPHABET_SIZE);
        StringBuilder decoded = new StringBuilder();

        for (char ch : text.toLowerCase().toCharArray()) {
            if (Character.isLetter(ch)) {
                int y = ch - 'a';
                int decrypted = mod(a_inv * (y - b), ALPHABET_SIZE);
                char decChar = (char) ('a' + decrypted);
                decoded.append(decChar);
            } else if (Character.isDigit(ch)) {
                decoded.append(ch);
            } else {
                continue; // ignore spaces and punctuation
            }
        }

        return decoded.toString();
    }
}