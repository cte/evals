class PhoneNumber:
    def __init__(self, number):
        # Check for letters
        if any(c.isalpha() for c in number):
            raise ValueError("letters not permitted")
        # Check for invalid punctuation (non-digit, non-space, non-()+-.)
        allowed_punct = set("0123456789 ()-+.")
        if any(not c.isdigit() and c not in allowed_punct for c in number):
            raise ValueError("punctuations not permitted")

        # Remove all non-digit characters
        digits = ''.join(filter(str.isdigit, number))

        # Length checks
        if len(digits) < 10:
            raise ValueError("must not be fewer than 10 digits")
        if len(digits) > 11:
            raise ValueError("must not be greater than 11 digits")
        if len(digits) == 11:
            if digits[0] != '1':
                raise ValueError("11 digits must start with 1")
            digits = digits[1:]

        # Area code checks
        if digits[0] == '0':
            raise ValueError("area code cannot start with zero")
        if digits[0] == '1':
            raise ValueError("area code cannot start with one")

        # Exchange code checks
        if digits[3] == '0':
            raise ValueError("exchange code cannot start with zero")
        if digits[3] == '1':
            raise ValueError("exchange code cannot start with one")

        self.number = digits

    @property
    def area_code(self):
        return self.number[:3]

    def pretty(self):
        return f"({self.number[:3]})-{self.number[3:6]}-{self.number[6:]}"
