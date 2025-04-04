import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class ProteinTranslator {

    private static final Map<String, String> codonToProtein = new HashMap<>();

    static {
        // Methionine
        codonToProtein.put("AUG", "Methionine");
        // Phenylalanine
        codonToProtein.put("UUU", "Phenylalanine");
        codonToProtein.put("UUC", "Phenylalanine");
        // Leucine
        codonToProtein.put("UUA", "Leucine");
        codonToProtein.put("UUG", "Leucine");
        // Serine
        codonToProtein.put("UCU", "Serine");
        codonToProtein.put("UCC", "Serine");
        codonToProtein.put("UCA", "Serine");
        codonToProtein.put("UCG", "Serine");
        // Tyrosine
        codonToProtein.put("UAU", "Tyrosine");
        codonToProtein.put("UAC", "Tyrosine");
        // Cysteine
        codonToProtein.put("UGU", "Cysteine");
        codonToProtein.put("UGC", "Cysteine");
        // Tryptophan
        codonToProtein.put("UGG", "Tryptophan");
        // STOP codons
        codonToProtein.put("UAA", "STOP");
        codonToProtein.put("UAG", "STOP");
        codonToProtein.put("UGA", "STOP");
    }

    List<String> translate(String rnaSequence) {
        List<String> proteins = new ArrayList<>();
        int codonLength = 3;

        if (rnaSequence == null) {
            return proteins; // Return empty list for null input
        }

        boolean stopped = false;
        // Loop through codons, ensuring we don't read past the end
        for (int i = 0; i <= rnaSequence.length() - codonLength; i += codonLength) {
            String codon = rnaSequence.substring(i, i + codonLength);
            String protein = codonToProtein.get(codon);

            if (protein == null) {
                // Throw an exception for unknown codons with the exact message required by tests
                throw new IllegalArgumentException("Invalid codon");
            }

            if ("STOP".equals(protein)) {
                stopped = true;
                break; // Stop translation
            }

            proteins.add(protein);
        }

        // Check for incomplete sequence only if translation wasn't stopped by a STOP codon
        if (!stopped && rnaSequence.length() % codonLength != 0) {
            throw new IllegalArgumentException("Invalid codon");
        }

        return proteins;
    }
}
