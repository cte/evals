#[derive(Debug)]
pub struct Error;

pub struct Scale {
    notes: Vec<String>,
}

impl Scale {
    pub fn new(tonic: &str, intervals: &str) -> Result<Scale, Error> {
        let use_flats = prefers_flats_dynamic(tonic, intervals);
        let chromatic = generate_chromatic_scale(tonic, use_flats)?;
        let mut scale_notes = Vec::new();
        let mut index = 0;
        scale_notes.push(chromatic[index].clone());
        for interval in intervals.chars() {
            index = match interval {
                'm' => (index + 1) % 12,
                'M' => (index + 2) % 12,
                'A' => (index + 3) % 12,
                _ => return Err(Error),
            };
            scale_notes.push(chromatic[index].clone());
        }
        Ok(Scale { notes: scale_notes })
    }

    pub fn chromatic(tonic: &str) -> Result<Scale, Error> {
        let use_flats = prefers_flats(tonic);
        let chromatic = generate_chromatic_scale(tonic, use_flats)?;
        let mut notes = chromatic.clone();
        notes.push(notes[0].clone());
        Ok(Scale { notes })
    }

    pub fn enumerate(&self) -> Vec<String> {
        self.notes.clone()
    }
}

fn prefers_flats_dynamic(tonic: &str, _intervals: &str) -> bool {
    matches!(
        tonic.to_ascii_lowercase().as_str(),
        "d" | "g" | "f" | "bb" | "eb" | "ab" | "db" | "gb" | "cb"
    )
}

fn prefers_flats(tonic: &str) -> bool {
    matches!(
        tonic.to_ascii_lowercase().as_str(),
        "f" | "bb" | "eb" | "ab" | "db" | "gb" | "cb"
    )
}

fn generate_chromatic_scale(tonic: &str, use_flats: bool) -> Result<Vec<String>, Error> {
    let sharp_scale = vec![
        "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",
    ];
    let flat_scale = vec![
        "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B",
    ];

    let tonic_normalized = capitalize_tonic(tonic);

    let scale = if use_flats { &flat_scale } else { &sharp_scale };

    let pos = scale
        .iter()
        .position(|&note| note.eq_ignore_ascii_case(&tonic_normalized))
        .ok_or(Error)?;

    let mut rotated = scale[pos..].to_vec();
    rotated.extend_from_slice(&scale[..pos]);
    Ok(rotated.into_iter().map(|s| s.to_string()).collect())
}

fn capitalize_tonic(tonic: &str) -> String {
    let mut chars = tonic.chars();
    match chars.next() {
        None => String::new(),
        Some(first) => first.to_ascii_uppercase().to_string() + &chars.as_str().to_ascii_lowercase(),
    }
}
