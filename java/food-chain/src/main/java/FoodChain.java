import java.util.stream.Collectors;
import java.util.stream.IntStream;

class FoodChain {

    private static final String[] ANIMALS = {
            "fly", "spider", "bird", "cat", "dog", "goat", "cow", "horse"
    };

    private static final String[] REMARKS = {
            null, // fly has no specific remark line, handled by the ending
            "It wriggled and jiggled and tickled inside her.",
            "How absurd to swallow a bird!",
            "Imagine that, to swallow a cat!",
            "What a hog, to swallow a dog!",
            "Just opened her throat and swallowed a goat!",
            "I don't know how she swallowed a cow!",
            null // horse has a special ending line
    };

    private static final String FLY_ENDING = "I don't know why she swallowed the fly. Perhaps she'll die.";
    private static final String HORSE_ENDING = "She's dead, of course!";

    String verse(int verseNum) {
        if (verseNum < 1 || verseNum > ANIMALS.length) {
            throw new IllegalArgumentException("Verse number must be between 1 and " + ANIMALS.length);
        }

        int index = verseNum - 1; // Adjust to 0-based index
        String animal = ANIMALS[index];
        StringBuilder verseBuilder = new StringBuilder();

        // First line
        verseBuilder.append("I know an old lady who swallowed a ").append(animal).append(".\n");

        // Special case: Horse (last verse)
        if (verseNum == ANIMALS.length) {
            verseBuilder.append(HORSE_ENDING);
            return verseBuilder.toString();
        }

        // Remark line (if applicable)
        String remark = REMARKS[index];
        if (remark != null) {
            verseBuilder.append(remark).append("\n");
        }

        // Special case: Fly (first verse)
        if (verseNum == 1) {
            verseBuilder.append(FLY_ENDING);
            return verseBuilder.toString();
        }

        // Cumulative lines for verses 2 through 7
        for (int i = index; i > 0; i--) {
            verseBuilder.append("She swallowed the ").append(ANIMALS[i]).append(" to catch the ").append(ANIMALS[i - 1]);
            // Add spider's specific line when it's the *caught* animal
            if (i - 1 == 1) { // index 1 is spider
                verseBuilder.append(" that wriggled and jiggled and tickled inside her");
            }
            verseBuilder.append(".\n");
        }

        // Final line for verses 1 through 7
        verseBuilder.append(FLY_ENDING);
        return verseBuilder.toString();
    }

    String verses(int startVerse, int endVerse) {
        if (startVerse < 1 || endVerse > ANIMALS.length || startVerse > endVerse) {
            throw new IllegalArgumentException("Invalid verse range: " + startVerse + "-" + endVerse);
        }

        return IntStream.rangeClosed(startVerse, endVerse)
                .mapToObj(this::verse)
                .collect(Collectors.joining("\n\n"));
    }
}