public class TwoBucket {
    private int totalMoves;
    private String finalBucket;
    private int otherBucket;

    public TwoBucket(int bucketOneCap, int bucketTwoCap, int goal, String startBucket) {
        boolean startWithOne = startBucket.equals("one");
        Result result = simulate(bucketOneCap, bucketTwoCap, goal, startWithOne);

        this.totalMoves = result.moves;
        this.finalBucket = result.goalBucket;
        this.otherBucket = result.otherBucketAmount;
    }

    private record Result(int moves, String goalBucket, int otherBucketAmount) {}

    private Result simulate(int cap1, int cap2, int goal, boolean startWithOne) {
        int moves = 1;
        int b1 = startWithOne ? cap1 : 0;
        int b2 = startWithOne ? 0 : cap2;

        // Special case: if the goal equals the capacity of the non-start bucket, fill it immediately in move 2
        if (startWithOne && cap2 == goal) {
            return new Result(2, "two", cap1);
        }
        if (!startWithOne && cap1 == goal) {
            return new Result(2, "one", cap2);
        }

        if (b1 == goal) return new Result(moves, "one", b2);
        if (b2 == goal) return new Result(moves, "two", b1);

        while (true) {
            if (startWithOne) {
                if (b1 == 0) {
                    b1 = cap1;
                    moves++;
                } else if (b2 == cap2) {
                    b2 = 0;
                    moves++;
                } else {
                    int pour = Math.min(b1, cap2 - b2);
                    b1 -= pour;
                    b2 += pour;
                    moves++;
                }
            } else {
                if (b2 == 0) {
                    b2 = cap2;
                    moves++;
                } else if (b1 == cap1) {
                    b1 = 0;
                    moves++;
                } else {
                    int pour = Math.min(b2, cap1 - b1);
                    b2 -= pour;
                    b1 += pour;
                    moves++;
                }
            }

            if (b1 == goal) return new Result(moves, "one", b2);
            if (b2 == goal) return new Result(moves, "two", b1);
        }
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public String getFinalBucket() {
        return finalBucket;
    }

    public int getOtherBucket() {
        return otherBucket;
    }
}
