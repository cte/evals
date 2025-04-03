class BottleSong {

    String recite(int startBottles, int takeDown) {
        StringBuilder song = new StringBuilder();
        for (int i = 0; i < takeDown; i++) {
            int current = startBottles - i;
            int next = current - 1;

            String currentWord = numberToWords(current, true);
            String nextWord = numberToWords(next, false);

            String currentBottle = current == 1 ? "bottle" : "bottles";
            String nextBottle = next == 1 ? "bottle" : "bottles";

            song.append(currentWord).append(" green ").append(currentBottle).append(" hanging on the wall,\n");
            song.append(currentWord).append(" green ").append(currentBottle).append(" hanging on the wall,\n");
            song.append("And if one green bottle should accidentally fall,\n");
            song.append("There'll be ").append(nextWord).append(" green ").append(nextBottle).append(" hanging on the wall.\n");

            if (i < takeDown - 1) {
                song.append("\n");
            }
        }
        return song.toString();
    }

    private String numberToWords(int number, boolean capitalize) {
        String word;
        switch (number) {
            case 10: word = "ten"; break;
            case 9: word = "nine"; break;
            case 8: word = "eight"; break;
            case 7: word = "seven"; break;
            case 6: word = "six"; break;
            case 5: word = "five"; break;
            case 4: word = "four"; break;
            case 3: word = "three"; break;
            case 2: word = "two"; break;
            case 1: word = "one"; break;
            case 0: word = "no"; break;
            default: word = Integer.toString(number); break;
        }
        if (capitalize && word.length() > 0) {
            word = word.substring(0,1).toUpperCase() + word.substring(1);
        }
        return word;
    }

}