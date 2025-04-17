class PigLatinTranslator {
    public String translate(String input) {
        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int w = 0; w < words.length; w++) {
            String word = words[w];
            String translatedWord;

            if (word.startsWith("xr") || word.startsWith("yt")) {
                translatedWord = word + "ay";
            } else if (startsWithVowel(word)) {
                translatedWord = word + "ay";
            } else {
                int index = firstVowelIndex(word);
                translatedWord = word.substring(index) + word.substring(0, index) + "ay";
            }

            result.append(translatedWord);
            if (w < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    private boolean startsWithVowel(String word) {
        return word.matches("^[aeiou].*");
    }

    private int firstVowelIndex(String word) {
        int i = 0;
        while (i < word.length()) {
            char c = word.charAt(i);

            if (isVowel(c)) {
                if (c == 'u' && i > 0 && word.charAt(i - 1) == 'q') {
                    i++; // include 'u' after 'q' in consonant cluster
                    continue;
                }
                break;
            }

            if (c == 'y' && i != 0) { // 'y' is vowel if not first letter
                break;
            }

            i++;
        }
        return i;
    }

    private boolean isVowel(char c) {
        return "aeiou".indexOf(c) != -1;
    }
}