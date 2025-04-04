def verse(n):
    """Generates a single verse of the song as a list of two strings."""
    if n == 0:
        return [
            "No more bottles of beer on the wall, no more bottles of beer.",
            "Go to the store and buy some more, 99 bottles of beer on the wall.",
        ]
    elif n == 1:
        return [
            "1 bottle of beer on the wall, 1 bottle of beer.",
            "Take it down and pass it around, no more bottles of beer on the wall.",
        ]
    elif n == 2:
        return [
            "2 bottles of beer on the wall, 2 bottles of beer.",
            "Take one down and pass it around, 1 bottle of beer on the wall.",
        ]
    else:
        return [
            f"{n} bottles of beer on the wall, {n} bottles of beer.",
            f"Take one down and pass it around, {n-1} bottles of beer on the wall.",
        ]

def recite(start, take=1):
    """Recites verses of the song, returning a list including separators."""
    result_verses = []
    for i in range(take):
        current_bottle = start - i
        if current_bottle < 0:
            break # Stop if we go below 0 bottles

        current_verse_lines = verse(current_bottle)
        result_verses.extend(current_verse_lines)

        # Add separator "" if this is not the last verse requested *and*
        # the next verse would still be valid (>= 0 bottles)
        if i < take - 1:
            next_bottle = start - (i + 1)
            if next_bottle >= 0:
                 result_verses.append("")

    return result_verses
