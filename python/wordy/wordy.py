import re

def answer(question):
    # Remove the prefix "What is " and the suffix "?"
    # Allow for negative numbers and ensure the question ends properly.
    match = re.match(r"What is (-?\d+(?: (?:plus|minus|multiplied by|divided by) -?\d+)*)\?", question)

    core_question = question[len("What is "):-1].strip()

    if not core_question:
        raise ValueError("syntax error") # Empty question after stripping

    # Check for unsupported operations explicitly first
    if any(op in core_question for op in ["cubed"]):
         raise ValueError("unknown operation")

    # Handle just a number after "What is"
    if core_question.isdigit() or (core_question.startswith('-') and core_question[1:].isdigit()):
        return int(core_question)

    # Tokenize the core question part
    tokens = core_question.split()

    # Check if it starts with a number
    try:
        result = int(tokens[0])
    except (ValueError, IndexError):
        # If it doesn't start with a number, it could be a non-math question or syntax error
        if not any(op in tokens for op in ["plus", "minus", "multiplied", "divided"]):
             raise ValueError("unknown operation") # No operators, likely non-math
        else:
             raise ValueError("syntax error") # Has operators but doesn't start correctly

    i = 1
    while i < len(tokens):
        operator = tokens[i]
        num_index = i + 1

        # Combine "multiplied by" and "divided by"
        if operator == "multiplied" and num_index < len(tokens) and tokens[num_index] == "by":
            operator = "multiplied by"
            num_index += 1
        elif operator == "divided" and num_index < len(tokens) and tokens[num_index] == "by":
            operator = "divided by"
            num_index += 1
        # Check if an operator is immediately followed by another operator or end of string
        elif operator in ["plus", "minus", "multiplied", "divided"]:
             if num_index >= len(tokens) or tokens[num_index] in ["plus", "minus", "multiplied", "divided", "by"]:
                 raise ValueError("syntax error")


        # Ensure there's a number following the operator
        if num_index >= len(tokens):
            raise ValueError("syntax error") # Operator at the end without number

        # Ensure the token after operator is a number
        try:
            number_str = tokens[num_index]
            # Check for invalid number format (e.g., "1plus") - int() might catch some, but be explicit
            if not (number_str.isdigit() or (number_str.startswith('-') and number_str[1:].isdigit())):
                 raise ValueError("syntax error") # Not a valid number where one is expected
            number = int(number_str)
        except (ValueError, IndexError):
             # Catches non-integer strings and index errors if num_index was wrong
             raise ValueError("syntax error")


        # Perform the operation
        if operator == "plus":
            result += number
        elif operator == "minus":
            result -= number
        elif operator == "multiplied by":
            result *= number
        elif operator == "divided by":
            # Division by zero is not explicitly tested but good practice
            if number == 0:
                raise ValueError("division by zero")
            result //= number # Integer division
        else:
            # If the token isn't a number or a known operator/part of operator
             raise ValueError("syntax error") # Treat unexpected words as syntax errors


        i = num_index + 1 # Move index past the number for the next iteration

    return result
