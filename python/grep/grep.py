def grep(pattern, flags, files):
    import re

    flag_set = set(flags.split())

    ignore_case = "-i" in flag_set
    invert_match = "-v" in flag_set
    match_entire_line = "-x" in flag_set
    print_line_numbers = "-n" in flag_set
    print_file_names = "-l" in flag_set

    # Compile regex pattern
    regex_flags = re.IGNORECASE if ignore_case else 0
    if match_entire_line:
        pat = f"^{re.escape(pattern)}$"
    else:
        pat = re.escape(pattern)
    regex = re.compile(pat, regex_flags)

    matched_files = set()
    output_lines = []

    for fname in files:
        with open(fname, 'r') as f:
            lines = f.readlines()

        matched_in_file = False
        for idx, line in enumerate(lines, 1):
            line_content = line.rstrip('\n')

            match = bool(regex.search(line_content))
            if invert_match:
                match = not match

            if match:
                matched_in_file = True
                if not print_file_names:
                    prefix = ""
                    if len(files) > 1:
                        prefix += f"{fname}:"
                    if print_line_numbers:
                        prefix += f"{idx}:"
                    output_lines.append(f"{prefix}{line_content}\n")

        if matched_in_file and print_file_names:
            matched_files.add(fname)

    if print_file_names:
        return ''.join(f"{fname}\n" for fname in matched_files)
    else:
        return ''.join(output_lines)
