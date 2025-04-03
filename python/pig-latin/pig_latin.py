def translate(text):
    def translate_word(word):
        vowels = ('a', 'e', 'i', 'o', 'u')
        # Special cases treated as vowels
        if word.startswith(('xr', 'yt')):
            return word + 'ay'
        # Word starts with vowel
        if word[0] in vowels:
            return word + 'ay'

        idx = 0
        while idx < len(word):
            # Handle 'qu' as a unit
            if word[idx:idx+2] == 'qu':
                idx += 2
                continue
            # 'y' acts as a vowel if not the first letter
            if word[idx] in vowels or (word[idx] == 'y' and idx != 0):
                break
            idx += 1
        return word[idx:] + word[:idx] + 'ay'

    return ' '.join(translate_word(word) for word in text.split())
