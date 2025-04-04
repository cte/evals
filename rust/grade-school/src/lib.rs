use std::collections::{BTreeMap, BTreeSet};

#[derive(Default)]
pub struct School {
    roster: BTreeMap<u32, BTreeSet<String>>,
}

impl School {
    pub fn new() -> School {
        School::default()
    }

    pub fn add(&mut self, grade: u32, student: &str) {
        // Check if student already exists in any grade
        if self.roster.values().any(|students| students.contains(student)) {
            // Student already exists, do nothing as per implied requirement
            // (though instructions mention indicating incorrectness, the signature is `()`)
            return;
        }

        self.roster
            .entry(grade)
            .or_insert_with(BTreeSet::new)
            .insert(student.to_string());
    }

    pub fn grades(&self) -> Vec<u32> {
        self.roster.keys().cloned().collect()
    }

    // If `grade` returned a reference, `School` would be forced to keep a `Vec<String>`
    // internally to lend out. By returning an owned vector of owned `String`s instead,
    // the internal structure can be completely arbitrary. The tradeoff is that some data
    // must be copied each time `grade` is called.
    pub fn grade(&self, grade: u32) -> Vec<String> {
        self.roster
            .get(&grade)
            .map(|students| students.iter().cloned().collect())
            .unwrap_or_else(Vec::new)
    }
}
