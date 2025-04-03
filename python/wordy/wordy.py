def answer(question):
    if not question.startswith("What is") or not question.endswith("?"):
        raise ValueError("unknown operation")

    # Remove 'What is' prefix and '?' suffix
    expr = question[7:-1].strip()

    # Handle empty expression
    if not expr:
        raise ValueError("syntax error")

    # Normalize multi-word operators
    expr = expr.replace("multiplied by", "multiplied_by")
    expr = expr.replace("divided by", "divided_by")

    tokens = expr.split()

    # Special case: "What is 52 cubed?" or other unsupported operations
    unsupported_ops = {"cubed", "squared", "power", "root"}
    if any(token in unsupported_ops for token in tokens):
        raise ValueError("unknown operation")

    # Handle single number case
    if len(tokens) == 1:
        try:
            return int(tokens[0])
        except ValueError:
            raise ValueError("syntax error")

    # Validate token sequence: number, operator, number, operator, number, ...
    if len(tokens) < 3 or len(tokens) % 2 == 0:
        raise ValueError("syntax error")

    try:
        result = int(tokens[0])
    except ValueError:
        raise ValueError("syntax error")

    idx = 1
    while idx < len(tokens):
        op = tokens[idx]
        try:
            num = int(tokens[idx + 1])
        except ValueError:
            raise ValueError("syntax error")

        if op == "plus":
            result += num
        elif op == "minus":
            result -= num
        elif op == "multiplied_by":
            result *= num
        elif op == "divided_by":
            result //= num
        elif op in unsupported_ops:
            raise ValueError("unknown operation")
        else:
            raise ValueError("unknown operation")

        idx += 2

    return result
