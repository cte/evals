use once_cell::sync::Lazy;
use rand::{thread_rng, Rng};
use std::collections::HashSet;
use std::sync::Mutex;

// Global set to keep track of all names currently in use.
// Lazy ensures thread-safe one-time initialization.
static USED_NAMES: Lazy<Mutex<HashSet<String>>> = Lazy::new(|| Mutex::new(HashSet::new()));

fn generate_unique_name() -> String {
    let mut rng = thread_rng();
    let mut used_names = USED_NAMES.lock().unwrap();

    loop {
        let letters: String = (0..2)
            .map(|_| rng.gen_range('A'..='Z'))
            .collect();
        let digits: String = (0..3)
            .map(|_| rng.gen_range('0'..='9'))
            .collect();
        let name = format!("{}{}", letters, digits);

        // Attempt to insert the new name into the set.
        // `insert` returns true if the name was not present, false otherwise.
        if used_names.insert(name.clone()) {
            return name; // Name is unique, return it.
        }
        // If insert returned false, the name was already present, loop again.
    }
}

pub struct Robot {
    // Use Mutex for interior mutability, allowing name generation/storage via &self.
    name: Mutex<Option<&'static str>>,
}

impl Robot {
    pub fn new() -> Self {
        Robot {
            name: Mutex::new(None),
        }
    }

    pub fn name(&self) -> &str {
        // Lock the mutex to access the name.
        let mut name_guard = self.name.lock().unwrap();

        // If the name is None, generate and store a new one.
        if name_guard.is_none() {
            let new_name = generate_unique_name();
            // Box::leak creates a 'static reference from the String.
            // This satisfies the &str return type but leaks memory.
            *name_guard = Some(Box::leak(new_name.into_boxed_str()));
        }

        // Now we know it's Some, unwrap and return the 'static str reference.
        name_guard.unwrap()
    }

    pub fn reset_name(&mut self) {
        // Lock the mutex and set the name back to None.
        // No need to remove from USED_NAMES, uniqueness check handles reuse.
        let mut name_guard = self.name.lock().unwrap();
        *name_guard = None;
    }
}
