import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class Dominoes {

    List<Domino> formChain(List<Domino> inputDominoes) throws ChainNotFoundException {
        if (inputDominoes == null) {
             throw new IllegalArgumentException("Input list cannot be null.");
        }
        if (inputDominoes.isEmpty()) {
            return new ArrayList<>();
        }

        // Handle the single domino case separately for clarity and efficiency
        if (inputDominoes.size() == 1) {
            Domino single = inputDominoes.get(0);
            if (single.getLeft() == single.getRight()) {
                return new ArrayList<>(inputDominoes); // Return a copy as per convention
            } else {
                throw new ChainNotFoundException("No domino chain found.");
            }
        }

        List<Domino> mutableDominoes = new ArrayList<>(inputDominoes);

        // Try starting the chain with each domino
        for (int i = 0; i < mutableDominoes.size(); i++) {
            Domino startDomino = mutableDominoes.get(i);
            List<Domino> remaining = new ArrayList<>(mutableDominoes);
            remaining.remove(i); // Remove the starting domino by index

            // Attempt 1: Start with the domino in its original orientation
            LinkedList<Domino> chain = new LinkedList<>();
            chain.add(startDomino);
            List<Domino> result = findChainRecursive(chain, remaining);
            if (result != null) {
                return result; // Found a valid chain
            }

            // Attempt 2: If the domino is not symmetrical, try starting with it flipped
            // We only need to try flipping if the first attempt failed and left != right
            if (startDomino.getLeft() != startDomino.getRight()) {
                 Domino flippedStart = new Domino(startDomino.getRight(), startDomino.getLeft());
                 LinkedList<Domino> flippedChain = new LinkedList<>();
                 flippedChain.add(flippedStart);
                 // Use the same 'remaining' list as the original objects need to be tracked
                 result = findChainRecursive(flippedChain, remaining);
                 if (result != null) {
                     return result; // Found a valid chain starting with the flipped domino
                 }
            }
        }

        // If no starting domino leads to a complete valid chain
        throw new ChainNotFoundException("No domino chain found.");
    }

    private List<Domino> findChainRecursive(LinkedList<Domino> currentChain, List<Domino> remainingDominoes) {
        // Base case: All dominoes have been placed in the chain
        if (remainingDominoes.isEmpty()) {
            // Check if the chain is valid (ends match)
            if (currentChain.getFirst().getLeft() == currentChain.getLast().getRight()) {
                return new ArrayList<>(currentChain); // Success! Return a copy of the chain.
            } else {
                return null; // Chain formed, but ends don't match. Invalid.
            }
        }

        Domino lastDominoInChain = currentChain.getLast();
        int targetValue = lastDominoInChain.getRight(); // Value to match

        // Try appending each remaining domino
        for (int i = 0; i < remainingDominoes.size(); i++) {
            Domino candidate = remainingDominoes.get(i);
            List<Domino> nextRemaining = new ArrayList<>(remainingDominoes);
            nextRemaining.remove(i); // Prepare the list for the recursive call

            // Option 1: Candidate matches without flipping
            if (candidate.getLeft() == targetValue) {
                currentChain.addLast(candidate); // Add to chain
                List<Domino> result = findChainRecursive(currentChain, nextRemaining); // Recurse
                if (result != null) {
                    return result; // Solution found, propagate up
                }
                currentChain.removeLast(); // Backtrack: remove the candidate
            }
            // Option 2: Candidate matches only when flipped (and is not symmetrical)
            // Avoid redundant checks if left == right
            else if (candidate.getRight() == targetValue && candidate.getLeft() != candidate.getRight()) {
                 Domino flippedCandidate = new Domino(candidate.getRight(), candidate.getLeft());
                 currentChain.addLast(flippedCandidate); // Add flipped version to chain
                 List<Domino> result = findChainRecursive(currentChain, nextRemaining); // Recurse
                 if (result != null) {
                     return result; // Solution found, propagate up
                 }
                 currentChain.removeLast(); // Backtrack: remove the flipped candidate
            }
            // Option 3: Candidate is symmetrical and matches (candidate.getLeft() == candidate.getRight() == targetValue)
            // This case is covered by Option 1, so no separate check needed.
        }

        // If we tried all remaining dominoes and none led to a solution
        return null;
    }
}