import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class ZebraPuzzle {

    // --- Enums for Categories ---
    enum Color { RED, GREEN, IVORY, YELLOW, BLUE }
    enum Nationality { ENGLISHMAN, SPANIARD, UKRAINIAN, NORWEGIAN, JAPANESE }
    enum Pet { DOG, SNAILS, FOX, HORSE, ZEBRA }
    enum Drink { COFFEE, TEA, MILK, ORANGE_JUICE, WATER }
    enum Hobby { DANCING, PAINTER, READING, FOOTBALL, CHESS }

    // --- Solution Holder ---
    private static final Solution SOLVED_PUZZLE = solve();

    // --- House Representation (Implicit: Index 0-4 represents house number) ---
    private static class Solution {
        final Color[] colors;
        final Nationality[] nationalities;
        final Pet[] pets;
        final Drink[] drinks;
        final Hobby[] hobbies;

        Solution(Color[] c, Nationality[] n, Pet[] p, Drink[] d, Hobby[] h) {
            colors = c; nationalities = n; pets = p; drinks = d; hobbies = h;
        }

        // Helper to get index of a specific attribute
        int indexOf(Object attribute) {
            if (attribute instanceof Color) return indexOf(colors, (Color) attribute);
            if (attribute instanceof Nationality) return indexOf(nationalities, (Nationality) attribute);
            if (attribute instanceof Pet) return indexOf(pets, (Pet) attribute);
            if (attribute instanceof Drink) return indexOf(drinks, (Drink) attribute);
            if (attribute instanceof Hobby) return indexOf(hobbies, (Hobby) attribute);
            return -1;
        }

        private <T> int indexOf(T[] array, T value) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == value) return i;
            }
            return -1;
        }

        // Helper to check if house at index i has attribute value
        boolean has(int index, Object attribute) {
             if (attribute instanceof Color) return colors[index] == attribute;
             if (attribute instanceof Nationality) return nationalities[index] == attribute;
             if (attribute instanceof Pet) return pets[index] == attribute;
             if (attribute instanceof Drink) return drinks[index] == attribute;
             if (attribute instanceof Hobby) return hobbies[index] == attribute;
             return false;
        }
    }

    // --- Solver Logic ---
    private static Solution solve() {
        // Generate all permutations for each category
        List<Color[]> colorPermutations = generatePermutations(Color.values());
        List<Nationality[]> nationalityPermutations = generatePermutations(Nationality.values());
        List<Pet[]> petPermutations = generatePermutations(Pet.values());
        List<Drink[]> drinkPermutations = generatePermutations(Drink.values());
        List<Hobby[]> hobbyPermutations = generatePermutations(Hobby.values());

        // Iterate through all combinations of permutations
        for (Color[] colors : colorPermutations) {
            // Constraint 10: The Norwegian lives in the first house (house 0).
            // Constraint 15: The Norwegian lives next to the blue house.
            // Apply these early to prune possibilities for nationality and color.
            if (colors[1] != Color.BLUE) continue; // If house 0 is Norwegian, house 1 must be blue

            for (Nationality[] nationalities : nationalityPermutations) {
                 if (nationalities[0] != Nationality.NORWEGIAN) continue; // Constraint 10

                for (Pet[] pets : petPermutations) {
                    for (Drink[] drinks : drinkPermutations) {
                         // Constraint 9: The person in the middle house (house 2) drinks milk.
                         if (drinks[2] != Drink.MILK) continue;

                        for (Hobby[] hobbies : hobbyPermutations) {
                            Solution potentialSolution = new Solution(colors, nationalities, pets, drinks, hobbies);
                            if (checkConstraints(potentialSolution)) {
                                return potentialSolution; // Found the unique solution
                            }
                        }
                    }
                }
            }
        }
        throw new IllegalStateException("Solution not found!"); // Should not happen if puzzle is solvable
    }

    // --- Constraint Checking ---
    private static boolean checkConstraints(Solution s) {
        // 1. There are five houses. (Implicit by array size 5)

        // 2. The Englishman lives in the red house.
        if (s.colors[s.indexOf(Nationality.ENGLISHMAN)] != Color.RED) return false;

        // 3. The Spaniard owns the dog.
        if (s.pets[s.indexOf(Nationality.SPANIARD)] != Pet.DOG) return false;

        // 4. The person in the green house drinks coffee.
        if (s.drinks[s.indexOf(Color.GREEN)] != Drink.COFFEE) return false;

        // 5. The Ukrainian drinks tea.
        if (s.drinks[s.indexOf(Nationality.UKRAINIAN)] != Drink.TEA) return false;

        // 6. The green house is immediately to the right of the ivory house.
        int greenIdx = s.indexOf(Color.GREEN);
        int ivoryIdx = s.indexOf(Color.IVORY);
        if (greenIdx != ivoryIdx + 1) return false;

        // 7. The snail owner likes to go dancing.
        if (s.hobbies[s.indexOf(Pet.SNAILS)] != Hobby.DANCING) return false;

        // 8. The person in the yellow house is a painter.
        if (s.hobbies[s.indexOf(Color.YELLOW)] != Hobby.PAINTER) return false;

        // 9. The person in the middle house drinks milk. (Checked earlier during generation)
        // if (s.drinks[2] != Drink.MILK) return false; // Redundant check

        // 10. The Norwegian lives in the first house. (Checked earlier during generation)
        // if (s.nationalities[0] != Nationality.NORWEGIAN) return false; // Redundant check

        // 11. The person who enjoys reading lives in the house next to the person with the fox.
        int readingIdx = s.indexOf(Hobby.READING);
        int foxIdx = s.indexOf(Pet.FOX);
        if (Math.abs(readingIdx - foxIdx) != 1) return false;

        // 12. The painter's house is next to the house with the horse.
        int painterIdx = s.indexOf(Hobby.PAINTER);
        int horseIdx = s.indexOf(Pet.HORSE);
        if (Math.abs(painterIdx - horseIdx) != 1) return false;

        // 13. The person who plays football drinks orange juice.
        if (s.drinks[s.indexOf(Hobby.FOOTBALL)] != Drink.ORANGE_JUICE) return false;

        // 14. The Japanese person plays chess.
        if (s.hobbies[s.indexOf(Nationality.JAPANESE)] != Hobby.CHESS) return false;

        // 15. The Norwegian lives next to the blue house. (Partially checked earlier)
        int norwegianIdx = s.indexOf(Nationality.NORWEGIAN); // Should be 0
        int blueIdx = s.indexOf(Color.BLUE);
        if (Math.abs(norwegianIdx - blueIdx) != 1) return false;

        // All constraints passed
        return true;
    }


    // --- Permutation Generation (Heap's Algorithm) ---
    private static <T> List<T[]> generatePermutations(T[] values) {
        List<T[]> list = new java.util.ArrayList<>();
        generate(values.length, values.clone(), list);
        return list;
    }

    private static <T> void generate(int k, T[] a, List<T[]> output) {
        if (k == 1) {
            output.add(a.clone());
        } else {
            generate(k - 1, a, output);
            for (int i = 0; i < k - 1; i++) {
                if (k % 2 == 0) {
                    swap(a, i, k - 1);
                } else {
                    swap(a, 0, k - 1);
                }
                generate(k - 1, a, output);
            }
        }
    }

    private static <T> void swap(T[] a, int i, int j) {
        T temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // --- Public API Methods ---
    String getWaterDrinker() {
        int waterDrinkerIndex = SOLVED_PUZZLE.indexOf(Drink.WATER);
        if (waterDrinkerIndex == -1) {
             throw new IllegalStateException("Could not find water drinker in the solution.");
        }
        return toTitleCase(SOLVED_PUZZLE.nationalities[waterDrinkerIndex].name());
    }

    String getZebraOwner() {
        int zebraOwnerIndex = SOLVED_PUZZLE.indexOf(Pet.ZEBRA);
         if (zebraOwnerIndex == -1) {
             throw new IllegalStateException("Could not find zebra owner in the solution.");
        }
        return toTitleCase(SOLVED_PUZZLE.nationalities[zebraOwnerIndex].name());
    }
}
