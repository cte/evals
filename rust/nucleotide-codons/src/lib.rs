use std::collections::HashMap;

pub struct CodonsInfo<'a> {
    // Store normalized codons (T instead of U) as keys
    map: HashMap<String, &'a str>,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub struct Error;

// Helper function to expand codons including shorthand
fn expand_codon(codon: &str) -> Result<Vec<String>, Error> {
    // This function assumes input codon is already normalized (T instead of U)
    // and is 3 characters long (checked by caller).
    let mut expanded_list: Vec<String> = vec![String::new()];

    for nucleotide in codon.chars() {
        let possibilities: Vec<char> = match nucleotide {
            'A' => vec!['A'], 'C' => vec!['C'], 'G' => vec!['G'], 'T' => vec!['T'],
            'R' => vec!['A', 'G'], 'Y' => vec!['C', 'T'], 'W' => vec!['A', 'T'],
            'S' => vec!['C', 'G'], 'K' => vec!['G', 'T'], 'M' => vec!['A', 'C'],
            'B' => vec!['C', 'G', 'T'], 'D' => vec!['A', 'G', 'T'], 'H' => vec!['A', 'C', 'T'],
            'V' => vec!['A', 'C', 'G'], 'N' => vec!['A', 'C', 'G', 'T'],
            _ => return Err(Error), // Invalid character
        };

        let mut next_expanded = Vec::new();
        for prefix in &expanded_list {
            for p in &possibilities {
                let mut new_codon = prefix.clone();
                new_codon.push(*p);
                next_expanded.push(new_codon);
            }
        }
        expanded_list = next_expanded;
    }
    // Return only fully expanded 3-character codons
    Ok(expanded_list.into_iter().filter(|c| c.len() == 3).collect())
}


impl<'a> CodonsInfo<'a> {
    pub fn name_for(&self, codon: &str) -> Result<&'a str, Error> {
        if codon.len() != 3 {
            return Err(Error);
        }

        // Normalize U to T for lookup and expansion
        let codon_t = codon.replace('U', "T");

        // Expand the potentially shorthand codon
        let expanded_codons = expand_codon(&codon_t)?;

        if expanded_codons.is_empty() {
            // If expansion results in nothing (e.g., invalid chars), it's an error
            return Err(Error);
        }

        let mut expected_name: Option<&'a str> = None;

        for expanded in &expanded_codons {
            // Lookup the normalized, expanded codon in the map
            match self.map.get(expanded).copied() {
                Some(current_name) => {
                    if let Some(name) = expected_name {
                        // Check consistency among all expansions
                        if current_name != name {
                            return Err(Error); // Shorthand maps to different proteins
                        }
                    } else {
                        // Store the first valid name found
                        expected_name = Some(current_name);
                    }
                }
                None => {
                    // If any specific codon resulting from shorthand expansion is not in the map,
                    // the shorthand is considered invalid for this set of pairs.
                    return Err(Error);
                }
            }
        }

        // If we found a consistent name across all expansions, return it.
        // If expected_name is None, it means none of the expanded codons were in the map.
        expected_name.ok_or(Error)
    }

    pub fn of_rna(&self, rna: &str) -> Result<Vec<&'a str>, Error> {
        let mut proteins = Vec::new();
        // Process the RNA string by codons (3 characters/bytes at a time)
        for codon_bytes in rna.as_bytes().chunks(3) {
             // Ensure we have a full codon
            if codon_bytes.len() != 3 {
                return Err(Error); // Incomplete codon at the end or invalid length
            }
            // Convert bytes to string slice; safe for valid RNA/DNA sequences
            let codon_str = std::str::from_utf8(codon_bytes).map_err(|_| Error)?;

            // Use the updated name_for which handles shorthand and U/T normalization
            match self.name_for(codon_str) {
                Ok(name) => {
                    // Check for STOP codon to terminate translation
                    if name == "STOP" {
                        break;
                    }
                    proteins.push(name);
                }
                Err(_) => {
                    // If any codon is invalid or cannot be resolved, the whole sequence is invalid
                    return Err(Error);
                }
            }
        }
        Ok(proteins)
    }
}

pub fn parse<'a>(pairs: Vec<(&'a str, &'a str)>) -> CodonsInfo<'a> {
    // Normalize codons to use 'T' instead of 'U' and store them as Strings in the map keys
    let map: HashMap<String, &'a str> = pairs
        .into_iter()
        .map(|(codon, name)| (codon.replace('U', "T"), name))
        .collect();
    CodonsInfo { map }
}
