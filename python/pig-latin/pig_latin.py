import re

def translate_word(word):
    vowels = "aeiou"

    # Rule 1: Starts with vowel sound (a, e, i, o, u) or special cases 'xr', 'yt'
    if re.match(r'^[aeiou]|yt|xr', word):
        return word + "ay"

    # Find the consonant cluster
    # Match consonant(s), including 'y' if not first, potentially followed by 'qu'
    # Or handle 'y' as the first letter explicitly as a consonant
    if word.startswith('y'):
         # Match 'y' plus following consonants (if any)
         match = re.match(r'^(y[^aeiou]*)', word)
    else:
        # Match initial consonants (not 'y'), optionally followed by 'qu'
        # Or match initial consonants (not 'y')
        match = re.match(r'^([^aeiouy]*qu|[^aeiouy]+)', word)

    if match:
        consonant_cluster = match.group(0)
        # Check if the whole word matched (like 'rhythm' where 'y' is the vowel)
        if len(consonant_cluster) == len(word):
             # Check if 'y' is present and not the first letter (acts as vowel)
             y_pos = word.find('y', 1)
             if y_pos != -1:
                 # Split at 'y'
                 consonant_cluster = word[:y_pos]
                 remaining_word = word[y_pos:]
                 return remaining_word + consonant_cluster + "ay"
             else:
                 # No vowel 'y' found after consonants (e.g., "shh") - add "ay"
                 # Test cases don't cover this, but adding 'ay' seems consistent
                 return word + "ay"

        remaining_word = word[len(consonant_cluster):]
        return remaining_word + consonant_cluster + "ay"

    # Fallback (should not be reached with comprehensive regex, but for safety)
    return word + "ay"


def translate(text):
    words = text.split()
    translated_words = [translate_word(word) for word in words]
    return " ".join(translated_words)
