def transpose(text):
    if not text:
        return ""

    lines = text.split('\n')
    max_len = max(len(line) for line in lines)
    padded_lines = [line.ljust(max_len) for line in lines]

    transposed_lines = [''.join(row) for row in zip(*padded_lines)]

    if transposed_lines:
        transposed_lines[-1] = transposed_lines[-1].rstrip()

    return '\n'.join(transposed_lines)
