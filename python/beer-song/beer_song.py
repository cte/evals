def recite(start, take=1):
    verses = []
    for n in range(start, start - take, -1):
        if n > 2:
            verses.append(f"{n} bottles of beer on the wall, {n} bottles of beer.")
            verses.append(f"Take one down and pass it around, {n - 1} bottles of beer on the wall.")
        elif n == 2:
            verses.append("2 bottles of beer on the wall, 2 bottles of beer.")
            verses.append("Take one down and pass it around, 1 bottle of beer on the wall.")
        elif n == 1:
            verses.append("1 bottle of beer on the wall, 1 bottle of beer.")
            verses.append("Take it down and pass it around, no more bottles of beer on the wall.")
        else:  # n == 0
            verses.append("No more bottles of beer on the wall, no more bottles of beer.")
            verses.append("Go to the store and buy some more, 99 bottles of beer on the wall.")
        if n != start - take + 1:
            verses.append("")
    return verses
