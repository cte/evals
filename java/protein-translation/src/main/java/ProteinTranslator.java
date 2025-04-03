import java.util.List;

class ProteinTranslator {

    List<String> translate(String rnaSequence) {
        java.util.Map<String, String> codonMap = new java.util.HashMap<>();
        codonMap.put("AUG", "Methionine");
        codonMap.put("UUU", "Phenylalanine");
        codonMap.put("UUC", "Phenylalanine");
        codonMap.put("UUA", "Leucine");
        codonMap.put("UUG", "Leucine");
        codonMap.put("UCU", "Serine");
        codonMap.put("UCC", "Serine");
        codonMap.put("UCA", "Serine");
        codonMap.put("UCG", "Serine");
        codonMap.put("UAU", "Tyrosine");
        codonMap.put("UAC", "Tyrosine");
        codonMap.put("UGU", "Cysteine");
        codonMap.put("UGC", "Cysteine");
        codonMap.put("UGG", "Tryptophan");
        codonMap.put("UAA", "STOP");
        codonMap.put("UAG", "STOP");
        codonMap.put("UGA", "STOP");

        java.util.List<String> proteins = new java.util.ArrayList<>();
        int i = 0;
        while (i + 3 <= rnaSequence.length()) {
            String codon = rnaSequence.substring(i, i + 3);
            String protein = codonMap.get(codon);
            if (protein == null) {
                throw new IllegalArgumentException("Invalid codon");
            }
            if (protein.equals("STOP")) {
                return proteins;
            }
            proteins.add(protein);
            i += 3;
        }
        // After processing all full codons, check if any trailing incomplete codon exists
        if (i != rnaSequence.length()) {
            throw new IllegalArgumentException("Invalid codon");
        }
        return proteins;
    }
}
