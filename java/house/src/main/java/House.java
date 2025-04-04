import java.util.stream.Collectors;
import java.util.stream.IntStream;

class House {

    private static final String[] SUBJECTS = {
        "the house that Jack built.",
        "the malt",
        "the rat",
        "the cat",
        "the dog",
        "the cow with the crumpled horn",
        "the maiden all forlorn",
        "the man all tattered and torn",
        "the priest all shaven and shorn",
        "the rooster that crowed in the morn",
        "the farmer sowing his corn",
        "the horse and the hound and the horn"
    };

    private static final String[] VERBS = {
        "lay in",
        "ate",
        "killed",
        "worried",
        "tossed",
        "milked",
        "kissed",
        "married",
        "woke",
        "kept",
        "belonged to"
    };

    String verse(int verse) {
        if (verse < 1 || verse > SUBJECTS.length) {
            throw new IllegalArgumentException("Verse number must be between 1 and " + SUBJECTS.length);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("This is ");
        sb.append(SUBJECTS[verse - 1]);

        for (int i = verse - 2; i >= 0; i--) {
            sb.append(" that ");
            sb.append(VERBS[i]);
            sb.append(" ");
            sb.append(SUBJECTS[i]);
        }

        return sb.toString();
    }

    String verses(int startVerse, int endVerse) {
        if (startVerse < 1 || endVerse > SUBJECTS.length || startVerse > endVerse) {
             throw new IllegalArgumentException("Invalid verse range");
        }
        return IntStream.rangeClosed(startVerse, endVerse)
                .mapToObj(this::verse)
                .collect(Collectors.joining("\n"));
    }

    String sing() {
        return verses(1, SUBJECTS.length);
    }
}