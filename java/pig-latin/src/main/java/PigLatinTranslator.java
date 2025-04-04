import java.util.Arrays;
import java.util.stream.Collectors;

class PigLatinTranslator {

    private static final String VOWELS = "aeiou";

    // Translates a whole phrase by splitting into words, translating each, and rejoining.
    public String translate(String phrase) {
        if (phrase == null || phrase.isEmpty()) {
            return "";
        }
        // Split the phrase by spaces, translate each word, and join back with spaces.
        return Arrays.stream(phrase.split(" "))
                     .map(this::translateWord)
                     .collect(Collectors.joining(" "));
    }

    // Translates a single word according to Pig Latin rules.
    private String translateWord(String word) {
        // Rule 1: Starts with a vowel sound (a, e, i, o, u) or special prefixes (xr, yt)
        if (startsWithVowelSound(word)) {
            return word + "ay";
        }

        // Rules 2, 3, 4: Starts with a consonant sound
        int consonantClusterEnd = 0; // Index where the initial consonant cluster ends
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);

            // Handle 'qu' as a single consonant sound (Rule 3)
            // If 'u' follows 'q', treat 'u' as part of the consonant cluster.
            if (currentChar == 'u' && i > 0 && word.charAt(i - 1) == 'q') {
                consonantClusterEnd = i + 1;
                continue; // Continue searching after 'qu'
            }

            // Check if the current character is a vowel (marks the end of the consonant cluster)
            if (isVowel(currentChar)) {
                consonantClusterEnd = i;
                break; // Found the first vowel, stop searching
            }

            // Handle 'y' as a vowel sound if it's not the first letter (Rule 4)
            // If 'y' is encountered after the first letter, it acts as a vowel.
            if (currentChar == 'y' && i > 0) {
                consonantClusterEnd = i;
                break; // Found 'y' acting as a vowel, stop searching
            }

            // If it's a consonant (or 'y' at the start), it's part of the cluster
            consonantClusterEnd = i + 1;
            // If the loop finishes without breaking, the entire word might be consonants,
            // or end in 'y' treated as a consonant initially.
            // In this case, consonantClusterEnd will correctly be word.length().
        }

        // Perform the Pig Latin transformation: move consonant cluster and add "ay"
        String startPart = word.substring(consonantClusterEnd); // Part of the word after the cluster
        String movedPart = word.substring(0, consonantClusterEnd); // The consonant cluster itself
        return startPart + movedPart + "ay";
    }

    // Helper method to check if a word starts with a vowel sound as per Rule 1.
    private boolean startsWithVowelSound(String word) {
        if (word == null || word.isEmpty()) {
            return false; // An empty word cannot start with a vowel sound
        }
        // Check for special consonant prefixes ("xr", "yt") that are treated like vowels
        if (word.startsWith("xr") || word.startsWith("yt")) {
            return true;
        }
        // Check if the first letter is a standard vowel (a, e, i, o, u)
        return isVowel(word.charAt(0));
    }

    // Helper method to check if a character is a vowel (a, e, i, o, u). Case-insensitive.
    private boolean isVowel(char c) {
        // Check if the lowercase version of the character exists in the VOWELS string
        return VOWELS.indexOf(Character.toLowerCase(c)) != -1;
    }
}