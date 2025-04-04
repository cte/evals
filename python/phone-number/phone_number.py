import re

class PhoneNumber:
    def __init__(self, number):
        # Check for letters
        if re.search(r'[a-zA-Z]', number):
            raise ValueError("letters not permitted")

        # Check for disallowed punctuation
        # Allowed characters are digits, spaces, and ()-.+
        if re.search(r'[^\d\(\)\-\.\+ ]', number):
            raise ValueError("punctuations not permitted")

        # Extract digits
        cleaned_digits = "".join(re.findall(r'\d', number))
        num_digits = len(cleaned_digits)

        # Validate length
        if num_digits < 10:
            raise ValueError("must not be fewer than 10 digits")
        if num_digits > 11:
            raise ValueError("must not be greater than 11 digits")

        # Handle 11-digit numbers (must start with 1)
        if num_digits == 11:
            if cleaned_digits[0] != '1':
                raise ValueError("11 digits must start with 1")
            nanp_number = cleaned_digits[1:] # Strip the leading '1'
        else: # num_digits == 10
            nanp_number = cleaned_digits

        # Validate NANP format (area code and exchange code)
        area_code = nanp_number[0:3]
        exchange_code = nanp_number[3:6]

        if area_code[0] == '0':
            raise ValueError("area code cannot start with zero")
        if area_code[0] == '1':
            raise ValueError("area code cannot start with one")
        if exchange_code[0] == '0':
            raise ValueError("exchange code cannot start with zero")
        if exchange_code[0] == '1':
            raise ValueError("exchange code cannot start with one")

        # Store the valid 10-digit number and area code
        self.number = nanp_number
        self.area_code = area_code

    def pretty(self):
        """Formats the number as (XXX)-XXX-XXXX"""
        return f"({self.area_code})-{self.number[3:6]}-{self.number[6:]}"
