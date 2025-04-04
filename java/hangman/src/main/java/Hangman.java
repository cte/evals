import io.reactivex.Observable;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;


class Hangman {

    private static final int MAX_MISSES = Part.values().length;

    Observable<Output> play(Observable<String> words, Observable<String> letters) {
        // Normalize letter inputs to lowercase
        Observable<String> normalizedLetters = letters.map(String::toLowerCase);

        return words
            // Removed distinctUntilChanged() here based on previous attempt, switchMap handles new words
            .switchMap(word -> {
                // Normalize word to lowercase
                String secretWord = word.toLowerCase();
                // Handle empty word case gracefully (as per potential edge cases)
                if (secretWord.isEmpty()) {
                    return Observable.just(new Output(
                        "", "", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Status.WIN
                    ));
                }

                String initialDiscovered = "_".repeat(secretWord.length());

                Output initialState = new Output(
                    secretWord,
                    initialDiscovered,
                    Collections.emptySet(), // Initially no letters guessed (correctly)
                    Collections.emptySet(), // Initially no misses
                    Collections.emptyList(), // Initially no parts
                    Status.PLAYING
                );

                // Keep track of ALL letters attempted internally to detect duplicates
                Set<String> allAttemptedLetters = new HashSet<>();

                return normalizedLetters
                    .scan(initialState, (currentState, letter) -> {
                        // Ensure letter is a single character
                        if (letter == null || letter.length() != 1) {
                             return currentState; // Ignore invalid input
                        }

                        // If game is already over, no more state changes
                        if (currentState.status != Status.PLAYING) {
                            return currentState;
                        }

                        // --- CHANGE 1: Check for duplicate guess ---
                        // Use the internal tracking set 'allAttemptedLetters'
                        if (!allAttemptedLetters.add(letter)) {
                            // Letter was already in the set (add returned false)
                            // Throw exception as required by tests
                            throw new IllegalArgumentException("Letter " + letter + " was already played");
                        }
                        // If add returned true, the letter is new and now tracked.

                        // We proceed only if the letter is new.
                        // Copy state for modification
                        Set<String> currentCorrectGuesses = new HashSet<>(currentState.guess); // This set now tracks only CORRECT guesses for output
                        Set<String> currentMisses = new HashSet<>(currentState.misses);
                        List<Part> currentParts = new ArrayList<>(currentState.parts);
                        String currentDiscovered = currentState.discovered;
                        Status currentStatus = currentState.status;


                        if (secretWord.contains(letter)) {
                            // Correct guess: Update discovered string and add to correct guesses set
                            currentCorrectGuesses.add(letter); // Add to the set tracking correct guesses
                            StringBuilder discoveredBuilder = new StringBuilder(currentDiscovered);
                            for (int i = 0; i < secretWord.length(); i++) {
                                if (secretWord.charAt(i) == letter.charAt(0)) {
                                    discoveredBuilder.setCharAt(i, letter.charAt(0));
                                }
                            }
                            currentDiscovered = discoveredBuilder.toString();

                            // Check for WIN condition
                            if (!currentDiscovered.contains("_")) {
                                currentStatus = Status.WIN;
                            }
                        } else {
                            // Incorrect guess: Add to misses and update parts
                            currentMisses.add(letter);
                            if (currentMisses.size() <= MAX_MISSES) {
                                // Add part only if within the limit
                                currentParts.add(Part.values()[currentMisses.size() - 1]);
                            }

                            // Check for LOSS condition AFTER adding the part
                            if (currentMisses.size() >= MAX_MISSES) {
                                currentStatus = Status.LOSS;
                            }
                        }

                        // --- CHANGE 2: Output uses 'currentCorrectGuesses' for the 'guess' field ---
                        return new Output(
                            secretWord,
                            currentDiscovered,
                            currentCorrectGuesses, // Output only CORRECT guesses
                            currentMisses,
                            currentParts,
                            currentStatus
                        );
                    })
                    // Emit the initial state for the new word immediately
                    .startWith(initialState);
                    // Distinct based on relevant state fields
//                     .distinctUntilChanged((prev, curr) ->
//                         prev.secret.equals(curr.secret) &&
//                         prev.discovered.equals(curr.discovered) &&
//                         prev.guess.equals(curr.guess) && // Compares correct guesses
//                         prev.misses.equals(curr.misses) &&
//                         prev.parts.equals(curr.parts) &&
//                         prev.status.equals(curr.status)
//                     );
            });
    }
}