use rand::{thread_rng, Rng};
use std::collections::HashSet;
use std::sync::Mutex;
use once_cell::sync::Lazy;

static NAMES: Lazy<Mutex<HashSet<String>>> = Lazy::new(|| Mutex::new(HashSet::new()));

pub struct Robot {
    name: String,
}

impl Robot {
    pub fn new() -> Self {
        let name = generate_unique_name();
        Robot { name }
    }

    pub fn name(&self) -> &str {
        &self.name
    }

    pub fn reset_name(&mut self) {
        let mut names = NAMES.lock().unwrap();
        names.remove(&self.name);
        self.name = generate_unique_name_locked(&mut names);
    }
}

fn generate_unique_name() -> String {
    let mut names = NAMES.lock().unwrap();
    generate_unique_name_locked(&mut names)
}

fn generate_unique_name_locked(names: &mut HashSet<String>) -> String {
    let mut rng = thread_rng();
    loop {
        let name = format!(
            "{}{}{:03}",
            (b'A' + rng.gen_range(0..26)) as char,
            (b'A' + rng.gen_range(0..26)) as char,
            rng.gen_range(0..1000)
        );
        if !names.contains(&name) {
            names.insert(name.clone());
            return name;
        }
    }
}
