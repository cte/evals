use std::collections::HashMap;

pub fn lowest_price(books: &[u32]) -> u32 {
    if books.is_empty() {
        return 0;
    }

    // 1. Count book frequencies
    let mut counts = HashMap::new();
    for &book in books {
        *counts.entry(book).or_insert(0) += 1;
    }

    // 2. Form sets greedily and count sets of each size
    // sets[i] = number of sets with i distinct books
    let mut sets = [0; 6]; // Index 0 unused

    while !counts.is_empty() {
        // Find distinct books remaining
        let distinct_books: Vec<u32> = counts.keys().cloned().collect();
        let set_size = distinct_books.len();

        if set_size > 0 {
            // Increment count for this set size
            sets[set_size] += 1;
            // Decrement count for each book included in this set
            for book in distinct_books {
                let count = counts.entry(book).or_insert(0);
                *count -= 1;
                // Remove book from map if count reaches zero
                if *count == 0 {
                    counts.remove(&book);
                }
            }
        } else {
             // Should not happen if counts started non-empty, but safety break
            break;
        }
    }

    // 3. Apply optimization: (1 group of 5 + 1 group of 3) -> (2 groups of 4)
    while sets[5] > 0 && sets[3] > 0 {
        sets[5] -= 1;
        sets[3] -= 1;
        sets[4] += 2;
    }

    // 4. Calculate total price in cents
    let price_per_book = 800.0; // 8 dollars in cents as float for calculation
    let discounts = [
        1.0,    // 0% - Placeholder for index 0
        1.0,    // 0% for 1 book
        0.95,   // 5% for 2 different books
        0.90,   // 10% for 3 different books
        0.80,   // 20% for 4 different books
        0.75,   // 25% for 5 different books
    ];

    let mut total_price = 0.0;
    for i in 1..=5 {
        let set_count = sets[i] as f64;
        let books_in_set = i as f64;
        let discount_factor = discounts[i];
        total_price += set_count * books_in_set * price_per_book * discount_factor;
    }

    // Round to nearest cent and return as u32
    total_price.round() as u32
}
