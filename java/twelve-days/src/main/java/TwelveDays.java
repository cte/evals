public class TwelveDays {
    private static final String[] ORDINALS = {
        "first", "second", "third", "fourth", "fifth", "sixth",
        "seventh", "eighth", "ninth", "tenth", "eleventh", "twelfth"
    };

    private static final String[] GIFTS = {
        "a Partridge in a Pear Tree",
        "two Turtle Doves",
        "three French Hens",
        "four Calling Birds",
        "five Gold Rings",
        "six Geese-a-Laying",
        "seven Swans-a-Swimming",
        "eight Maids-a-Milking",
        "nine Ladies Dancing",
        "ten Lords-a-Leaping",
        "eleven Pipers Piping",
        "twelve Drummers Drumming"
    };

    public String verse(int verseNumber) {
        StringBuilder verse = new StringBuilder();
        verse.append("On the ")
             .append(ORDINALS[verseNumber - 1])
             .append(" day of Christmas my true love gave to me: ");

        for (int i = verseNumber - 1; i >= 0; i--) {
            if (i == 0 && verseNumber != 1) {
                verse.append("and ");
            }
            verse.append(GIFTS[i]);
            if (i > 0) {
                verse.append(", ");
            } else {
                verse.append(".\n");
            }
        }
        return verse.toString();
    }

    public String verses(int startVerse, int endVerse) {
        StringBuilder verses = new StringBuilder();
        for (int i = startVerse; i <= endVerse; i++) {
            verses.append(verse(i).trim());
            if (i != endVerse) {
                verses.append("\n\n");
            } else {
                verses.append("\n");
            }
        }
        return verses.toString();
    }

    public String sing() {
        return verses(1, 12);
    }
}
