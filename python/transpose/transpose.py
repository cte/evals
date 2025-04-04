import itertools

def transpose(text):
    """
    Transposes the input text according to the rules:
    - Rows become columns, columns become rows.
    - Pad shorter lines with spaces on the left/top in the transposed output.
    - Do not pad with spaces on the right/bottom in the transposed output.
    """
    lines = text.splitlines()
    if not lines:
        return ""

    transposed_rows = []
    # Use None as a placeholder for missing characters in shorter lines
    zipped_columns = itertools.zip_longest(*lines, fillvalue=None)

    for column in zipped_columns:
        processed_column = []
        # Determine the extent of the actual content in this transposed row
        # Find the index of the last character that came from the original input
        last_char_index = -1
        for i in range(len(column) - 1, -1, -1):
            if column[i] is not None:
                last_char_index = i
                break

        # If last_char_index remains -1, the column was all Nones (empty input case handled earlier)
        # This shouldn't happen if lines is not empty.

        # Build the transposed row string up to the last real character
        # Replace None placeholders with spaces within this range
        for i in range(last_char_index + 1):
            char = column[i]
            processed_column.append(char if char is not None else ' ')

        transposed_rows.append("".join(processed_column))

    return "\n".join(transposed_rows)
