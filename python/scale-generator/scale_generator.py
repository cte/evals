# Define scales and key sets outside the class for clarity
SHARP_SCALE = ["A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"]
FLAT_SCALE = ["A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab"]

# Keys that use flats based on the table in instructions.md
USE_FLATS = ["F", "Bb", "Eb", "Ab", "Db", "Gb", "d", "g", "c", "f", "bb", "eb"]

# Mappings for enharmonic equivalents
SHARP_TO_FLAT = {"C#": "Db", "D#": "Eb", "F#": "Gb", "G#": "Ab", "A#": "Bb"}
FLAT_TO_SHARP = {"Db": "C#", "Eb": "D#", "Gb": "F#", "Ab": "G#", "Bb": "A#"}

INTERVAL_MAP = {'m': 1, 'M': 2, 'A': 3}

class Scale:
    def __init__(self, tonic):
        original_tonic = tonic # Keep original for scale selection

        # Determine which scale to use based on the original tonic
        if original_tonic in USE_FLATS:
            self._scale = FLAT_SCALE
            use_flats = True
        else: # Default to sharps (includes C, G, D, A, E, B, F#, a, e, b, f#, c#, g#, d#)
            self._scale = SHARP_SCALE
            use_flats = False

        # Normalize the tonic to match the chosen scale's notation
        normalized_tonic = original_tonic[0].upper() + original_tonic[1:] # Capitalize first letter

        if len(normalized_tonic) > 1:
            note = normalized_tonic[0]
            accidental = normalized_tonic[1]

            if use_flats and accidental == '#':
                # Convert sharp tonic to flat equivalent if using flat scale
                self.tonic = SHARP_TO_FLAT.get(normalized_tonic, normalized_tonic)
            elif not use_flats and accidental == 'b':
                 # Convert flat tonic to sharp equivalent if using sharp scale
                 self.tonic = FLAT_TO_SHARP.get(normalized_tonic, normalized_tonic)
            else:
                 # Tonic already matches scale notation or is natural
                 self.tonic = normalized_tonic
        else:
            # Single letter tonic (natural)
            self.tonic = normalized_tonic


    def chromatic(self):
        """Generates the chromatic scale starting with the tonic."""
        try:
            # Use the correctly normalized self.tonic
            start_index = self._scale.index(self.tonic)
        except ValueError:
             # This might happen if normalization fails or input is unexpected
             raise ValueError(f"Normalized tonic '{self.tonic}' not found in the selected scale: {self._scale}")

        # Rotate the scale
        rotated_scale = self._scale[start_index:] + self._scale[:start_index]
        return rotated_scale

    def interval(self, intervals):
        """Generates a scale based on the tonic and a sequence of intervals."""
        # Use the correctly normalized self.tonic
        current_note = self.tonic
        result_scale = [current_note]
        try:
            # Index in the non-rotated A-based scale (_scale)
            current_index_in_full_scale = self._scale.index(current_note)
        except ValueError:
             raise ValueError(f"Normalized tonic '{self.tonic}' not found in the selected scale for interval calculation: {self._scale}")

        for i in intervals:
            step = INTERVAL_MAP.get(i)
            if step is None:
                raise ValueError(f"Invalid interval: {i}")

            current_index_in_full_scale = (current_index_in_full_scale + step) % 12
            next_note = self._scale[current_index_in_full_scale]
            result_scale.append(next_note)

        return result_scale
