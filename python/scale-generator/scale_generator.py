class Scale:
    SHARP_SCALE = ["A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"]
    FLAT_SCALE = ["A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab"]

    # Flat keys exactly as per instructions
    FLAT_KEYS = {
        "F", "Bb", "Eb", "Ab", "Db", "Gb",  # major flat keys (uppercase)
        "d", "g", "c", "f", "bb", "eb"      # minor flat keys (lowercase)
    }

    def __init__(self, tonic):
        original_tonic = tonic.strip()

        # Normalize tonic for scale lookup: capitalize letter, preserve accidental
        if len(original_tonic) > 1 and original_tonic[1] in ['#', 'b']:
            self.tonic = original_tonic[0].upper() + original_tonic[1]
        else:
            self.tonic = original_tonic.capitalize()

        # Determine if flats should be used (case-sensitive, based on original input)
        if original_tonic in self.FLAT_KEYS:
            self.scale = self.FLAT_SCALE
        else:
            self.scale = self.SHARP_SCALE

    def chromatic(self):
        idx = self.scale.index(self.tonic)
        return self.scale[idx:] + self.scale[:idx]

    def interval(self, intervals):
        scale = self.chromatic()
        notes = [scale[0]]
        idx = 0
        for step in intervals:
            if step == 'm':
                idx = (idx + 1) % 12
            elif step == 'M':
                idx = (idx + 2) % 12
            elif step == 'A':
                idx = (idx + 3) % 12
            notes.append(scale[idx])
        return notes
