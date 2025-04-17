def recite(start, take=1):
    def number_to_words(n):
        words = {
            0: "no",
            1: "One",
            2: "Two",
            3: "Three",
            4: "Four",
            5: "Five",
            6: "Six",
            7: "Seven",
            8: "Eight",
            9: "Nine",
            10: "Ten"
        }
        return words.get(n, str(n))

    def number_to_words_lower(n):
        word = number_to_words(n)
        return word.lower() if n != 0 else word

    verses = []
    for n in range(start, start - take, -1):
        current = n
        next_bottle = n - 1

        # Determine current bottle wording
        if current == 1:
            current_bottles = "One green bottle"
        else:
            current_bottles = f"{number_to_words(current)} green bottles"

        # Determine next bottle wording
        if next_bottle == 1:
            next_bottles = "one green bottle"
        elif next_bottle == 0:
            next_bottles = "no green bottles"
        else:
            next_bottles = f"{number_to_words_lower(next_bottle)} green bottles"

        verses.append(f"{current_bottles} hanging on the wall,")
        verses.append(f"{current_bottles} hanging on the wall,")
        verses.append("And if one green bottle should accidentally fall,")
        verses.append(f"There'll be {next_bottles} hanging on the wall.")

        if n != start - take + 1:
            verses.append("")

    return verses
