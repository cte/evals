ANIMALS = [
    None,  # 0-index placeholder
    ("fly", None),
    ("spider", "It wriggled and jiggled and tickled inside her."),
    ("bird", "How absurd to swallow a bird!"),
    ("cat", "Imagine that, to swallow a cat!"),
    ("dog", "What a hog, to swallow a dog!"),
    ("goat", "Just opened her throat and swallowed a goat!"),
    ("cow", "I don't know how she swallowed a cow!"),
    ("horse", "She's dead, of course!"),
]

def get_verse(n):
    """Generates the lines for a single verse."""
    animal, remark = ANIMALS[n]
    lines = [f"I know an old lady who swallowed a {animal}."]

    if n == 8:  # Special case for the horse
        lines.append(remark)
        return lines

    if remark:
        lines.append(remark)

    # Generate the chain of "swallowed to catch" lines
    for i in range(n, 1, -1):
        current_animal, _ = ANIMALS[i]
        previous_animal, _ = ANIMALS[i-1]
        line = f"She swallowed the {current_animal} to catch the {previous_animal}"
        # Add the spider's unique line when it's the animal being caught
        if i - 1 == 2:
            line += " that wriggled and jiggled and tickled inside her"
        line += "."
        lines.append(line)

    # Add the common last line for verses 1-7
    lines.append("I don't know why she swallowed the fly. Perhaps she'll die.")

    return lines

def recite(start_verse, end_verse):
    """Generates the song lines for the specified verse range."""
    song = []
    for i in range(start_verse, end_verse + 1):
        if i > start_verse:
            song.append("")  # Add separator between verses
        song.extend(get_verse(i))
    return song
