use std::collections::HashMap;
use std::thread;
use std::sync::mpsc;

pub fn frequency(input: &[&str], worker_count: usize) -> HashMap<char, usize> {
    let mut final_counts = HashMap::new();
    let input_len = input.len();

    if input_len == 0 {
        return final_counts;
    }

    // Ensure worker_count is at least 1 and not more than the number of inputs
    // (although more workers than inputs is fine, just potentially inefficient)
    let num_workers = worker_count.max(1);

    let (tx, rx) = mpsc::channel();
    let mut handles = vec![];

    // Calculate chunk size, ensuring all items are processed
    let chunk_size = (input_len + num_workers - 1) / num_workers;

    for chunk in input.chunks(chunk_size) {
        // Need to own the data for the thread
        let thread_input: Vec<String> = chunk.iter().map(|s| s.to_string()).collect();
        let thread_tx = tx.clone();

        let handle = thread::spawn(move || {
            let mut local_counts = HashMap::new();
            for text in thread_input {
                for c in text.chars() {
                    if c.is_alphabetic() {
                        // Convert to lowercase and update count
                        let lower_char = c.to_ascii_lowercase();
                        *local_counts.entry(lower_char).or_insert(0) += 1;
                    }
                }
            }
            // Send local results back
            thread_tx.send(local_counts).unwrap();
        });
        handles.push(handle);
    }

    // Drop the original sender so the receiver knows when all threads are done
    drop(tx);

    // Collect results from threads
    for received in rx {
        for (key, value) in received {
            *final_counts.entry(key).or_insert(0) += value;
        }
    }

    // Wait for all threads to complete (optional here as recv blocks, but good practice)
    for handle in handles {
        handle.join().unwrap();
    }

    final_counts
}
