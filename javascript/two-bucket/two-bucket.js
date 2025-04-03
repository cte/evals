function gcd(a, b) {
  a = Math.abs(a);
  b = Math.abs(b);
  while (b !== 0) {
    const temp = b;
    b = a % b;
    a = temp;
  }
  return a;
}

export class TwoBucket {
  constructor(bucketOneSize, bucketTwoSize, goal, startingBucket) {
    this.bucketOneSize = bucketOneSize;
    this.bucketTwoSize = bucketTwoSize;
    this.goal = goal;
    this.startingBucket = startingBucket;

    if (goal > bucketOneSize && goal > bucketTwoSize) {
      throw new Error('Goal cannot be larger than both buckets');
    }

    const divisor = gcd(bucketOneSize, bucketTwoSize);
    if (goal % divisor !== 0) {
      throw new Error('Goal is not measurable with these bucket sizes');
    }
  }

  solve() {
    const simulate = (startBucket) => {
      let moves = 0;
      let one = 0;
      let two = 0;

      const maxOne = this.bucketOneSize;
      const maxTwo = this.bucketTwoSize;

      while (true) {
        moves++;

        if (startBucket === 'one') {
          if (one === 0) {
            one = maxOne; // fill bucket one
          } else if (two === maxTwo) {
            two = 0; // empty bucket two
          } else {
            const pourAmount = Math.min(one, maxTwo - two);
            one -= pourAmount;
            two += pourAmount;
          }
        } else { // startBucket === 'two'
          if (two === 0) {
            two = maxTwo; // fill bucket two
          } else if (one === maxOne) {
            one = 0; // empty bucket one
          } else {
            const pourAmount = Math.min(two, maxOne - one);
            two -= pourAmount;
            one += pourAmount;
          }
        }

        if (one === this.goal || two === this.goal) {
          return {
            moves,
            goalBucket: one === this.goal ? 'one' : 'two',
            otherBucket: one === this.goal ? two : one,
          };
        }
      }
    };

    return simulate(this.startingBucket);
  }
}
