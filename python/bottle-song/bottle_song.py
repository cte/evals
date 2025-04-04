NUM_WORDS = {
    10: "Ten", 9: "Nine", 8: "Eight", 7: "Seven", 6: "Six",
    5: "Five", 4: "Four", 3: "Three", 2: "Two", 1: "One", 0: "no"
}

def get_bottle_text(n):
    """Returns the correct 'bottle' or 'bottles' string."""
    return "bottle" if n == 1 else "bottles"

def recite(start, take=1):
    """Recites verses of the Green Bottles song."""
    verses = []
    for i in range(take):
        current_bottles = start - i
        next_bottles = current_bottles - 1

        if current_bottles < 0:  # Should not happen based on tests, but good practice
            break

        current_num_word = NUM_WORDS[current_bottles]
        next_num_word = NUM_WORDS[next_bottles]
        current_bottle_str = get_bottle_text(current_bottles)
        next_bottle_str = get_bottle_text(next_bottles)

        verse = [
            f"{current_num_word} green {current_bottle_str} hanging on the wall,",
            f"{current_num_word} green {current_bottle_str} hanging on the wall,",
            "And if one green bottle should accidentally fall,",
            f"There'll be {next_num_word.lower()} green {next_bottle_str} hanging on the wall."
        ]
        verses.extend(verse)

        # Add empty line between verses if not the last verse
        if i < take - 1:
            verses.append("")

    return verses
