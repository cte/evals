import re

def grep(pattern, flags, files):
    """
    Searches files for lines matching a pattern based on specified flags.

    Args:
        pattern: The string pattern to search for.
        flags: A string containing zero or more flags (-n, -l, -i, -v, -x).
        files: A list of file paths to search within.

    Returns:
        A string containing the matching lines or filenames, formatted according
        to the flags.
    """
    results = []
    matched_files = set()

    # Parse flags
    print_line_numbers = '-n' in flags
    print_filenames_only = '-l' in flags
    ignore_case = '-i' in flags
    invert_match = '-v' in flags
    match_entire_line = '-x' in flags

    # Determine if filename prefix is needed
    prepend_filename = len(files) > 1

    # Adjust pattern for case-insensitivity if needed
    search_pattern = pattern
    re_flags = 0
    if ignore_case:
        re_flags |= re.IGNORECASE

    for filename in files:
        try:
            with open(filename, 'r') as f:
                file_matched = False
                for line_num, line in enumerate(f, 1):
                    line = line.rstrip('\n') # Remove trailing newline for accurate matching
                    original_line = line # Keep original for output

                    # Prepare line and pattern for comparison based on flags
                    compare_line = line
                    compare_pattern = search_pattern

                    if ignore_case:
                        compare_line = line.lower()
                        compare_pattern = search_pattern.lower()

                    # Determine if the line matches
                    match = False
                    if match_entire_line:
                        if ignore_case:
                             match = compare_line == compare_pattern
                        else:
                             match = line == pattern # Use original case if -i not set
                    else:
                        if ignore_case:
                            match = compare_pattern in compare_line
                        else:
                            match = pattern in line # Use original case if -i not set


                    # Apply invert match flag
                    if invert_match:
                        match = not match

                    if match:
                        if print_filenames_only:
                            matched_files.add(filename)
                            file_matched = True
                            break  # Move to the next file once a match is found
                        else:
                            file_matched = True
                            output_line = ""
                            if prepend_filename:
                                output_line += f"{filename}:"
                            if print_line_numbers:
                                output_line += f"{line_num}:"
                            output_line += original_line
                            results.append(output_line)

        except FileNotFoundError:
            # According to standard grep behavior, errors like file not found
            # are often printed to stderr, but the command continues.
            # For this exercise, we'll skip missing files silently.
            continue

    if print_filenames_only:
        # Return filenames in the order they were provided in the input list
        ordered_matched_files = [f for f in files if f in matched_files]
        return "\n".join(ordered_matched_files) + ("\n" if ordered_matched_files else "")
    else:
        return "\n".join(results) + ("\n" if results else "")
