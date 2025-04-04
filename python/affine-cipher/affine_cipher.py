import math
import string

ALPHABET = string.ascii_lowercase
M = len(ALPHABET)

def gcd(a, b):
    """Calculate the Greatest Common Divisor of a and b."""
    while b:
        a, b = b, a % b
    return a

def mmi(a, m):
    """Calculate the Modular Multiplicative Inverse of a mod m."""
    # Based on the extended Euclidean algorithm, simplified for this case
    # ax + my = gcd(a, m)
    # We need ax === 1 (mod m), which only exists if gcd(a, m) == 1
    if gcd(a, m) != 1:
        raise ValueError("a and m must be coprime for MMI to exist.")

    # Find x such that (a * x) % m == 1
    for x in range(1, m):
        if (a * x) % m == 1:
            return x
    # Should not happen if gcd is 1, but as a safeguard
    raise ValueError("MMI not found, something went wrong.")


def encode(plain_text, a, b):
    """Encrypt plain_text using Affine Cipher with key (a, b)."""
    if gcd(a, M) != 1:
        raise ValueError("a and m must be coprime.")

    encoded_chars = []
    processed_text = ''.join(filter(str.isalnum, plain_text)).lower()

    for char in processed_text:
        if char.isdigit():
            encoded_chars.append(char)
        elif char in ALPHABET:
            i = ALPHABET.index(char)
            encrypted_index = (a * i + b) % M
            encoded_chars.append(ALPHABET[encrypted_index])
        # Ignore other characters (already filtered by isalnum and lower)

    # Group into chunks of 5
    cipher_text = "".join(encoded_chars)
    grouped_text = " ".join(cipher_text[i:i+5] for i in range(0, len(cipher_text), 5))
    return grouped_text


def decode(ciphered_text, a, b):
    """Decrypt ciphered_text using Affine Cipher with key (a, b)."""
    if gcd(a, M) != 1:
        raise ValueError("a and m must be coprime.")

    try:
        a_inv = mmi(a, M)
    except ValueError as e:
        # Propagate the coprime error if MMI calculation fails due to it
        raise ValueError("a and m must be coprime.") from e

    decoded_chars = []
    processed_text = ''.join(filter(str.isalnum, ciphered_text)) # Remove spaces and punctuation

    for char in processed_text:
        if char.isdigit():
            decoded_chars.append(char)
        elif char in ALPHABET:
            y = ALPHABET.index(char)
            # Ensure (y - b) is non-negative before modulo
            decrypted_index = (a_inv * (y - b + M)) % M
            decoded_chars.append(ALPHABET[decrypted_index])
        # Ignore other characters (already filtered by isalnum)

    return "".join(decoded_chars)
