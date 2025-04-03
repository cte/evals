public class FoodChain {

    private static final String[] ANIMALS = {
            "fly",
            "spider",
            "bird",
            "cat",
            "dog",
            "goat",
            "cow",
            "horse"
    };

    private static final String[] COMMENTS = {
            "",
            "It wriggled and jiggled and tickled inside her.",
            "How absurd to swallow a bird!",
            "Imagine that, to swallow a cat!",
            "What a hog, to swallow a dog!",
            "Just opened her throat and swallowed a goat!",
            "I don't know how she swallowed a cow!",
            "She's dead, of course!"
    };

    @SuppressWarnings("StringConcatenationInLoop")
    public String verse(int verseNumber) {
        int index = verseNumber - 1;
        StringBuilder verse = new StringBuilder();

        verse.append("I know an old lady who swallowed a ").append(ANIMALS[index]).append(".\n");

        // Special case: horse
        if (ANIMALS[index].equals("horse")) {
            verse.append(COMMENTS[index]);
            return verse.toString();
        }

        // Add comment if exists
        if (!COMMENTS[index].isEmpty()) {
            verse.append(COMMENTS[index]).append("\n");
        }

        // Add cumulative lines
        for (int i = index; i > 0; i--) {
            verse.append("She swallowed the ").append(ANIMALS[i])
                    .append(" to catch the ").append(ANIMALS[i - 1]);
            if (ANIMALS[i - 1].equals("spider")) {
                verse.append(" that wriggled and jiggled and tickled inside her");
            }
            verse.append(".\n");
        }

        verse.append("I don't know why she swallowed the fly. Perhaps she'll die.");

        return verse.toString();
    }

    public String verses(int startVerse, int endVerse) {
        StringBuilder allVerses = new StringBuilder();
        for (int i = startVerse; i <= endVerse; i++) {
            allVerses.append(verse(i));
            if (i != endVerse) {
                allVerses.append("\n\n");
            }
        }
        return allVerses.toString();
    }
}