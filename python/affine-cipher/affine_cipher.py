def encode(plain_text, a, b):
    from math import gcd

    m = 26
    if gcd(a, m) != 1:
        raise ValueError("a and m must be coprime.")

    def encode_char(c):
        if c.isalpha():
            x = ord(c.lower()) - ord('a')
            return chr(((a * x + b) % m) + ord('a'))
        elif c.isdigit():
            return c
        else:
            return ''

    encoded = ''.join(encode_char(c) for c in plain_text)
    # group into blocks of 5
    grouped = []
    for i in range(0, len(encoded), 5):
        grouped.append(encoded[i:i+5])
    return ' '.join(grouped)


def decode(ciphered_text, a, b):
    from math import gcd

    m = 26
    if gcd(a, m) != 1:
        raise ValueError("a and m must be coprime.")

    # Compute modular inverse of a mod m
    def modinv(a, m):
        # Extended Euclidean Algorithm
        t, new_t = 0, 1
        r, new_r = m, a
        while new_r != 0:
            quotient = r // new_r
            t, new_t = new_t, t - quotient * new_t
            r, new_r = new_r, r - quotient * new_r
        if r > 1:
            raise ValueError("a and m must be coprime.")
        if t < 0:
            t += m
        return t

    a_inv = modinv(a, m)

    def decode_char(c):
        if c.isalpha():
            y = ord(c.lower()) - ord('a')
            return chr((a_inv * (y - b) % m) + ord('a'))
        elif c.isdigit():
            return c
        else:
            return ''

    # ignore spaces in ciphered_text
    ciphered_text = ciphered_text.replace(' ', '')
    decoded = ''.join(decode_char(c) for c in ciphered_text)
    return decoded
