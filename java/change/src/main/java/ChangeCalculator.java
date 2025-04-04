import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;

class ChangeCalculator {

    private final List<Integer> coins;

    ChangeCalculator(List<Integer> currencyCoins) {
        if (currencyCoins == null || currencyCoins.isEmpty()) {
            throw new IllegalArgumentException("Currency coins cannot be null or empty.");
        }
        // Store distinct, positive coins. Sorting isn't strictly necessary for DP
        // but doesn't hurt and might offer minor optimization in inner loop.
        this.coins = currencyCoins.stream()
                                  .distinct()
                                  .filter(coin -> coin > 0)
                                  .sorted() // Sort ascending for potentially slightly faster DP loop
                                  .collect(Collectors.toList());
        if (this.coins.isEmpty()) {
             throw new IllegalArgumentException("Currency coins must contain positive values.");
        }
    }

    List<Integer> computeMostEfficientChange(int grandTotal) {
        if (grandTotal < 0) {
            throw new IllegalArgumentException("Negative totals are not allowed.");
        }
        if (grandTotal == 0) {
            return new ArrayList<>();
        }

        // dp[i] stores the minimum number of coins to make change for amount i
        int[] dp = new int[grandTotal + 1];
        // lastCoin[i] stores the last coin used to make optimal change for amount i
        int[] lastCoin = new int[grandTotal + 1];

        // Initialize dp array with a value representing infinity
        Arrays.fill(dp, grandTotal + 1); // More than possible coins needed
        dp[0] = 0; // Base case: 0 coins for amount 0

        for (int amount = 1; amount <= grandTotal; amount++) {
            for (int coin : coins) {
                if (coin <= amount) {
                    // If using this coin results in fewer coins than the current minimum for 'amount'
                    if (dp[amount - coin] + 1 < dp[amount]) {
                        dp[amount] = dp[amount - coin] + 1;
                        lastCoin[amount] = coin; // Record the coin used
                    }
                } else {
                    // Since coins are sorted, no need to check larger coins
                    break;
                }
            }
        }

        // If dp[grandTotal] is still the 'infinity' value, change cannot be made
        if (dp[grandTotal] > grandTotal) {
            throw new IllegalArgumentException(
                String.format("The total %d cannot be represented in the given currency.", grandTotal)
            );
        }

        // Reconstruct the change list by backtracking
        List<Integer> change = new ArrayList<>();
        int currentAmount = grandTotal;
        while (currentAmount > 0) {
            int coinUsed = lastCoin[currentAmount];
            change.add(coinUsed);
            currentAmount -= coinUsed;
        }

        // The reconstruction adds coins in reverse order of calculation,
        // sort ascending to match test expectations.
        Collections.sort(change);
        return change;
    }
}
