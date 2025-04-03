import java.util.List;
import java.util.ArrayList;

class Dominoes {

    List<Domino> formChain(List<Domino> inputDominoes) throws ChainNotFoundException {
        if (inputDominoes.isEmpty()) {
            return new ArrayList<>();
        }
        if (inputDominoes.size() == 1) {
            Domino d = inputDominoes.get(0);
            if (d.getLeft() == d.getRight()) {
                List<Domino> result = new ArrayList<>();
                result.add(d);
                return result;
            } else {
                throw new ChainNotFoundException("No domino chain found.");
            }
        }

        List<Domino> chain = new ArrayList<>();
        boolean[] used = new boolean[inputDominoes.size()];

        for (int i = 0; i < inputDominoes.size(); i++) {
            Domino d = inputDominoes.get(i);

            // Try original orientation
            chain.clear();
            chain.add(d);
            used[i] = true;
            if (backtrack(inputDominoes, chain, used, d.getRight(), inputDominoes.size())) {
                if (chain.get(0).getLeft() == chain.get(chain.size() -1).getRight()) {
                    return new ArrayList<>(chain);
                }
            }
            used[i] = false;

            // Try flipped orientation
            Domino flipped = new Domino(d.getRight(), d.getLeft());
            chain.clear();
            chain.add(flipped);
            used[i] = true;
            if (backtrack(inputDominoes, chain, used, flipped.getRight(), inputDominoes.size())) {
                if (chain.get(0).getLeft() == chain.get(chain.size() -1).getRight()) {
                    return new ArrayList<>(chain);
                }
            }
            used[i] = false;
        }

        throw new ChainNotFoundException("No domino chain found.");
    }

    private boolean backtrack(List<Domino> input, List<Domino> chain, boolean[] used, int currentRight, int targetLength) {
        if (chain.size() == targetLength) {
            return true;
        }

        for (int i = 0; i < input.size(); i++) {
            if (used[i]) continue;
            Domino d = input.get(i);

            // original orientation
            if (d.getLeft() == currentRight) {
                used[i] = true;
                chain.add(d);
                if (backtrack(input, chain, used, d.getRight(), targetLength)) {
                    return true;
                }
                chain.remove(chain.size() -1);
                used[i] = false;
            }

            // flipped orientation
            if (d.getRight() == currentRight) {
                Domino flipped = new Domino(d.getRight(), d.getLeft());
                used[i] = true;
                chain.add(flipped);
                if (backtrack(input, chain, used, flipped.getRight(), targetLength)) {
                    return true;
                }
                chain.remove(chain.size() -1);
                used[i] = false;
            }
        }
        return false;
    }
}