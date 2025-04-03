pub fn lowest_price(books: &[u32]) -> u32 {
    use std::collections::HashMap;

    if books.is_empty() {
        return 0;
    }

    let mut counts = HashMap::new();
    for &book in books {
        *counts.entry(book).or_insert(0) += 1;
    }

    fn price_for_group(size: usize) -> u32 {
        let base = 800 * size as u32;
        (base as f64 * match size {
            0..=5 => [1.0, 1.0, 0.95, 0.90, 0.80, 0.75][size],
            _ => 1.0,
        }) as u32
    }

    let mut counts_vec: Vec<u32> = counts.values().cloned().collect();

    fn helper(counts: &mut [u32]) -> u32 {
        let non_zero: Vec<u32> = counts.iter().cloned().filter(|&c| c > 0).collect();
        if non_zero.is_empty() {
            return 0;
        }

        let mut min_price = u32::MAX;

        for group_size in (1..=5).rev() {
            if non_zero.len() < group_size {
                continue;
            }

            let mut temp_counts = counts.to_vec();
            let mut taken = 0;
            for i in 0..temp_counts.len() {
                if temp_counts[i] > 0 {
                    temp_counts[i] -= 1;
                    taken += 1;
                    if taken == group_size {
                        break;
                    }
                }
            }

            let mut price = price_for_group(group_size) + helper(&mut temp_counts);

            // Multi-swap optimization: count all groups after this grouping
            let mut group_sizes = vec![group_size];
            let mut temp = temp_counts.clone();
            while temp.iter().any(|&c| c > 0) {
                let size = temp.iter().filter(|&&c| c > 0).count();
                group_sizes.push(size);
                for i in 0..temp.len() {
                    if temp[i] > 0 {
                        temp[i] -= 1;
                    }
                }
            }

            let fives = group_sizes.iter().filter(|&&s| s == 5).count();
            let threes = group_sizes.iter().filter(|&&s| s == 3).count();
            let min_swap = fives.min(threes);
            if min_swap > 0 {
                let original_price = group_sizes.iter().map(|&s| price_for_group(s)).sum::<u32>();
                let swapped_price = original_price
                    - (min_swap as u32) * (price_for_group(5) + price_for_group(3))
                    + (min_swap as u32) * 2 * price_for_group(4);
                if swapped_price < price {
                    price = swapped_price;
                }
            }

            if price < min_price {
                min_price = price;
            }
        }

        min_price
    }

    helper(&mut counts_vec)
}
