use std::collections::HashMap;

pub fn frequency(input: &[&str], worker_count: usize) -> HashMap<char, usize> {
    use std::thread;

    if input.is_empty() || worker_count == 0 {
        return HashMap::new();
    }

    // Collect all characters from input, filtering and lowercasing
    let chars: Vec<char> = input
        .iter()
        .flat_map(|line| line.chars())
        .filter(|c| c.is_alphabetic())
        .map(|c| c.to_lowercase().next().unwrap())
        .collect();

    if chars.is_empty() {
        return HashMap::new();
    }

    let chunk_size = (chars.len() + worker_count - 1) / worker_count; // ceil division
    let mut handles = Vec::new();

    for chunk in chars.chunks(chunk_size) {
        let chunk = chunk.to_owned();
        handles.push(thread::spawn(move || {
            let mut freq = HashMap::new();
            for c in chunk {
                *freq.entry(c).or_insert(0) += 1;
            }
            freq
        }));
    }

    let mut final_freq = HashMap::new();
    for handle in handles {
        let freq = handle.join().unwrap();
        for (c, count) in freq {
            *final_freq.entry(c).or_insert(0) += count;
        }
    }

    final_freq
}
