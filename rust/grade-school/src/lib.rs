use std::collections::{BTreeMap, BTreeSet};

pub struct School {
    roster: BTreeMap<u32, BTreeSet<String>>,
}

impl School {
    pub fn new() -> School {
        School {
            roster: BTreeMap::new(),
        }
    }

    pub fn add(&mut self, grade: u32, student: &str) {
        // Check if student already exists in any grade
        let already_exists = self.roster.values().any(|students| students.contains(student));
        if already_exists {
            // Do not add student again if they are already in any grade
            return;
        }
        // Otherwise, add student to the specified grade
        self.roster
            .entry(grade)
            .or_insert_with(BTreeSet::new)
            .insert(student.to_string());
    }

    pub fn grades(&self) -> Vec<u32> {
        self.roster
            .iter()
            .filter(|(_, students)| !students.is_empty())
            .map(|(grade, _)| *grade)
            .collect()
    }

    pub fn grade(&self, grade: u32) -> Vec<String> {
        self.roster
            .get(&grade)
            .map(|students| students.iter().cloned().collect())
            .unwrap_or_else(Vec::new)
    }
}
