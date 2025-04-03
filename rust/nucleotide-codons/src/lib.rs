// This exercise is deprecated.
// Consider working on protein-translation instead.

use std::collections::HashMap;

pub struct CodonsInfo<'a> {
    map: HashMap<&'a str, &'a str>,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub struct Error;

impl<'a> CodonsInfo<'a> {
    pub fn name_for(&self, codon: &str) -> Result<&'a str, Error> {
        if codon.len() != 3 {
            return Err(Error);
        }
        // Valid IUPAC codes for DNA
        let valid_codes = "ACGTRYMKWSBDHVN";
        if !codon.chars().all(|c| valid_codes.contains(c)) {
            return Err(Error);
        }

        for (&key, &protein) in &self.map {
            if codon_match(codon, key) {
                return Ok(protein);
            }
        }
        Err(Error)
    }

    pub fn of_rna(&self, rna: &str) -> Result<Vec<&'a str>, Error> {
        if rna.len() % 3 != 0 {
            return Err(Error);
        }
        let mut proteins = Vec::new();
        for chunk in rna.as_bytes().chunks(3) {
            let codon = std::str::from_utf8(chunk).map_err(|_| Error)?;
            let protein = self.name_for(codon)?;
            proteins.push(protein);
        }
        Ok(proteins)
    }
}

pub fn parse<'a>(pairs: Vec<(&'a str, &'a str)>) -> CodonsInfo<'a> {
    let mut map = HashMap::new();
    for (codon, protein) in pairs {
        map.insert(codon, protein);
    }
    CodonsInfo { map }
}

// Helper function to match codons with ambiguity codes
fn codon_match(pattern: &str, codon: &str) -> bool {
    pattern.chars().zip(codon.chars()).all(|(p, c)| match p {
        'A' => c == 'A',
        'C' => c == 'C',
        'G' => c == 'G',
        'T' => c == 'T',
        'R' => c == 'A' || c == 'G',
        'Y' => c == 'C' || c == 'T',
        'S' => c == 'G' || c == 'C',
        'W' => c == 'A' || c == 'T',
        'K' => c == 'G' || c == 'T',
        'M' => c == 'A' || c == 'C',
        'B' => c == 'C' || c == 'G' || c == 'T',
        'D' => c == 'A' || c == 'G' || c == 'T',
        'H' => c == 'A' || c == 'C' || c == 'T',
        'V' => c == 'A' || c == 'C' || c == 'G',
        'N' => true,
        _ => false,
    })
}
