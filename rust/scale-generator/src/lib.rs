const SHARPS: [&str; 12] = ["A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"];
const FLATS: [&str; 12] = ["A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab"];

// Tonics that typically use sharps (including C major / a minor for consistency)
const SHARP_TONICS: [&str; 14] = ["C", "G", "D", "A", "E", "B", "F#", "a", "e", "b", "f#", "c#", "g#", "d#"];
// Tonics that typically use flats
const FLAT_TONICS: [&str; 12] = ["F", "Bb", "Eb", "Ab", "Db", "Gb", "d", "g", "c", "f", "bb", "eb"];


#[derive(Debug)]
pub struct Error; // Placeholder error type

pub struct Scale {
    notes: Vec<String>,
}

impl Scale {
    pub fn new(tonic: &str, intervals: &str) -> Result<Scale, Error> {
        let (notes_to_use, start_index) = Self::get_notes_and_start_index(tonic)?;

        let mut current_index = start_index;
        let mut scale_notes = vec![notes_to_use[current_index].to_string()];

        for interval in intervals.chars() {
            let step = match interval {
                'm' => 1, // minor second (half step)
                'M' => 2, // major second (whole step)
                'A' => 3, // augmented second (whole + half step)
                _ => return Err(Error), // Invalid interval
            };
            current_index = (current_index + step) % 12;
            scale_notes.push(notes_to_use[current_index].to_string());
        }

        Ok(Scale { notes: scale_notes })
    }

    pub fn chromatic(tonic: &str) -> Result<Scale, Error> {
        // Chromatic scale uses all 12 notes, interval 'm' between each
        let intervals = "mmmmmmmmmmmm"; // 12 half steps cover the octave
        Self::new(tonic, intervals) // Generate 13 notes (including octave) using 12 'm' intervals
    }

    pub fn enumerate(&self) -> Vec<String> {
        self.notes.clone()
    }

    // Helper function to determine note set (sharps/flats) and starting index
    fn get_notes_and_start_index(tonic: &str) -> Result<(&'static [&'static str; 12], usize), Error> {
        let use_flats = FLAT_TONICS.contains(&tonic) ||
                      (tonic.len() > 1 && tonic.ends_with('b')) || // e.g. Db, Gb
                      ["F", "Bb", "Eb", "Ab", "Db", "Gb"].contains(&tonic); // Major flat keys explicitly

        // Default to sharps if not explicitly flat, or if it's a sharp key
        let notes_to_use = if use_flats && !SHARP_TONICS.contains(&tonic.to_lowercase().as_str()) { // Check lowercase tonic against SHARP_TONICS
             &FLATS
        } else {
             &SHARPS
        };

        // Normalize tonic for searching (e.g., "f#" -> "F#", "bb" -> "Bb")
         let normalized_tonic = if tonic.len() == 1 {
             tonic.to_uppercase()
         } else {
             tonic[0..1].to_uppercase() + &tonic[1..]
         };


        notes_to_use
            .iter()
            .position(|&note| note == normalized_tonic)
            .map(|index| (notes_to_use, index))
            .ok_or(Error) // Tonic not found in the selected scale
    }
}
