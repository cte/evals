import io.reactivex.Observable;

class Hangman {

    Observable<Output> play(Observable<String> words, Observable<String> letters) {
        return words.switchMap(word -> {
            // Emit initial game state immediately
            Observable<Output> initial = Observable.just(
                new Output(
                    word,
                    word.replaceAll(".", "_"),
                    java.util.Collections.emptySet(),
                    java.util.Collections.emptySet(),
                    java.util.Collections.emptyList(),
                    Status.PLAYING
                )
            );

            // Process guesses for this word
            Observable<Output> game = letters
                .scan(
                    new java.util.HashSet<String>(),
                    (guessed, letter) -> {
                        if (guessed.contains(letter)) {
                            throw new IllegalArgumentException("Letter " + letter + " was already played");
                        }
                        guessed.add(letter);
                        return guessed;
                    }
                )
                .map(guessed -> {
                    // Compute discovered letters
                    StringBuilder discovered = new StringBuilder();
                    for (char c : word.toCharArray()) {
                        String s = String.valueOf(c);
                        if (guessed.contains(s)) {
                            discovered.append(s);
                        } else {
                            discovered.append("_");
                        }
                    }

                    // Compute misses
                    java.util.Set<String> misses = new java.util.HashSet<>();
                    for (String g : guessed) {
                        if (!word.contains(g)) {
                            misses.add(g);
                        }
                    }

                    // Compute parts based on misses count
                    java.util.List<Part> parts = new java.util.ArrayList<>();
                    Part[] allParts = Part.values();
                    int missCount = Math.min(misses.size(), allParts.length);
                    for (int i = 0; i < missCount; i++) {
                        parts.add(allParts[i]);
                    }

                    // Compute status
                    Status status;
                    if (discovered.indexOf("_") == -1) {
                        status = Status.WIN;
                    } else if (misses.size() >= allParts.length) {
                        status = Status.LOSS;
                    } else {
                        status = Status.PLAYING;
                    }

                    // Separate correct and incorrect guesses
                    java.util.Set<String> correctGuesses = new java.util.HashSet<>();
                    for (String g : guessed) {
                        if (word.contains(g)) {
                            correctGuesses.add(g);
                        }
                    }

                    return new Output(
                        word,
                        discovered.toString(),
                        correctGuesses,
                        misses,
                        parts,
                        status
                    );
                });

            return initial.concatWith(game);
        });
    }

}