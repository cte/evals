def recite(start_verse, end_verse):
    animals = [
        "fly",
        "spider",
        "bird",
        "cat",
        "dog",
        "goat",
        "cow",
        "horse",
    ]

    comments = {
        "fly": "",
        "spider": "It wriggled and jiggled and tickled inside her.",
        "bird": "How absurd to swallow a bird!",
        "cat": "Imagine that, to swallow a cat!",
        "dog": "What a hog, to swallow a dog!",
        "goat": "Just opened her throat and swallowed a goat!",
        "cow": "I don't know how she swallowed a cow!",
        "horse": "",
    }

    result = []

    for verse_num in range(start_verse, end_verse + 1):
        animal = animals[verse_num - 1]
        verse = []

        # Opening line
        verse.append(f"I know an old lady who swallowed a {animal}.")

        # Unique comment line if any
        comment = comments[animal]
        if comment:
            verse.append(comment)

        # Special case for horse
        if animal == "horse":
            verse.append("She's dead, of course!")
            result.extend(verse)
            if verse_num != end_verse:
                result.append("")
            continue

        # Cumulative lines
        for i in range(verse_num - 1, 0, -1):
            current = animals[i]
            prev = animals[i - 1]
            if current == "bird" and prev == "spider":
                verse.append(
                    "She swallowed the bird to catch the spider that wriggled and jiggled and tickled inside her."
                )
            elif current == "spider" and prev == "fly":
                verse.append("She swallowed the spider to catch the fly.")
            else:
                verse.append(f"She swallowed the {current} to catch the {prev}.")

        # Closing line
        verse.append("I don't know why she swallowed the fly. Perhaps she'll die.")

        # Add verse to result
        result.extend(verse)

        # Add empty line if not last verse
        if verse_num != end_verse:
            result.append("")

    return result
