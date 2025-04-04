import java.util.StringJoiner;

class BottleSong {

    String recite(int startBottles, int takeDown) {
        StringJoiner verses = new StringJoiner("\n\n");
        for (int i = 0; i < takeDown; i++) {
            verses.add(createVerse(startBottles - i));
        }
        // Append a single newline at the very end, as expected by the tests.
        return verses.toString() + "\n";
    }

    private String createVerse(int currentBottles) {
        String currentNumWordCapitalized = numberToWord(currentBottles, true); // Capitalized for start of line
        String nextNumWordLowercase = numberToWord(currentBottles - 1, false); // Lowercase for middle of line

        String currentBottlePlural = (currentBottles == 1) ? "bottle" : "bottles";
        // Determine pluralization for the *next* number of bottles
        String nextBottlePlural = ((currentBottles - 1) == 1) ? "bottle" : "bottles";

        String line1 = String.format("%s green %s hanging on the wall,", currentNumWordCapitalized, currentBottlePlural);
        String line2 = String.format("%s green %s hanging on the wall,", currentNumWordCapitalized, currentBottlePlural);
        String line3 = "And if one green bottle should accidentally fall,";
        String line4;

        if (currentBottles > 1) {
            line4 = String.format("There'll be %s green %s hanging on the wall.", nextNumWordLowercase, nextBottlePlural);
        } else {
            // Special case for the very last line (when starting with one bottle)
            line4 = "There'll be no green bottles hanging on the wall.";
        }

        // Return the verse without a trailing newline. The recite method handles joining and the final newline.
        return String.join("\n", line1, line2, line3, line4);
    }

    // Helper method to convert number to word with capitalization control
    private String numberToWord(int number, boolean capitalize) {
        String word;
        switch (number) {
            case 10: word = "ten"; break;
            case 9:  word = "nine"; break;
            case 8:  word = "eight"; break;
            case 7:  word = "seven"; break;
            case 6:  word = "six"; break;
            case 5:  word = "five"; break;
            case 4:  word = "four"; break;
            case 3:  word = "three"; break;
            case 2:  word = "two"; break;
            case 1:  word = "one"; break;
            case 0:  word = "no"; break; // Use 'no' for zero
            default: return String.valueOf(number); // Fallback, should not happen for this problem
        }
        if (capitalize) {
            // Capitalize the first letter
            return word.substring(0, 1).toUpperCase() + word.substring(1);
        } else {
            return word;
        }
    }
}