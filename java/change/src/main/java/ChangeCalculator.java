import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ChangeCalculator {

    private final List<Integer> coins;

    ChangeCalculator(List<Integer> currencyCoins) {
        if (currencyCoins == null || currencyCoins.isEmpty()) {
            throw new IllegalArgumentException("Currency coins list cannot be null or empty.");
        }
        this.coins = new ArrayList<>(currencyCoins);
        Collections.sort(this.coins);
    }

    List<Integer> computeMostEfficientChange(int grandTotal) {
        if (grandTotal < 0) {
            throw new IllegalArgumentException("Negative totals are not allowed.");
        }
        if (grandTotal == 0) {
            return Collections.emptyList();
        }

        int[] minCoins = new int[grandTotal + 1];
        int[] lastCoin = new int[grandTotal + 1];

        // Initialize DP arrays
        for (int i = 1; i <= grandTotal; i++) {
            minCoins[i] = Integer.MAX_VALUE;
            lastCoin[i] = -1;
        }

        for (int coin : coins) {
            for (int amount = coin; amount <= grandTotal; amount++) {
                if (minCoins[amount - coin] != Integer.MAX_VALUE && minCoins[amount - coin] + 1 < minCoins[amount]) {
                    minCoins[amount] = minCoins[amount - coin] + 1;
                    lastCoin[amount] = coin;
                }
            }
        }

        if (minCoins[grandTotal] == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("The total " + grandTotal + " cannot be represented in the given currency.");
        }

        List<Integer> result = new ArrayList<>();
        int amount = grandTotal;
        while (amount > 0) {
            int coin = lastCoin[amount];
            result.add(coin);
            amount -= coin;
        }

        Collections.sort(result);
        return result;
    }

}
