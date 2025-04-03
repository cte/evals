class StackUnderflowError(Exception):
    pass


def evaluate(input_data):
    stack = []
    # global dictionary of user-defined words, case-insensitive keys
    global_user_words = {}

    def parse_number(token):
        try:
            return int(token)
        except ValueError:
            return None

    def process_token(token, user_words):
        num = parse_number(token)
        if num is not None:
            stack.append(num)
            return

        token_lower = token.lower()

        # Check user-defined words
        if token_lower in user_words:
            definition_tokens, captured_dict = user_words[token_lower]
            for sub_token in definition_tokens:
                process_token(sub_token, captured_dict)
            return

        # Built-in operations
        if token_lower in {"+", "-", "*", "/"}:
            if len(stack) < 2:
                raise StackUnderflowError("Insufficient number of items in stack")
            b = stack.pop()
            a = stack.pop()
            if token_lower == "+":
                stack.append(a + b)
            elif token_lower == "-":
                stack.append(a - b)
            elif token_lower == "*":
                stack.append(a * b)
            elif token_lower == "/":
                if b == 0:
                    raise ZeroDivisionError("divide by zero")
                stack.append(a // b)
            return

        if token_lower == "dup":
            if len(stack) < 1:
                raise StackUnderflowError("Insufficient number of items in stack")
            stack.append(stack[-1])
            return

        if token_lower == "drop":
            if len(stack) < 1:
                raise StackUnderflowError("Insufficient number of items in stack")
            stack.pop()
            return

        if token_lower == "swap":
            if len(stack) < 2:
                raise StackUnderflowError("Insufficient number of items in stack")
            stack[-1], stack[-2] = stack[-2], stack[-1]
            return

        if token_lower == "over":
            if len(stack) < 2:
                raise StackUnderflowError("Insufficient number of items in stack")
            stack.append(stack[-2])
            return

        raise ValueError("undefined operation")

    # Process each input string
    i = 0
    while i < len(input_data):
        line = input_data[i]
        tokens = line.split()
        j = 0
        while j < len(tokens):
            token = tokens[j]
            token_lower = token.lower()
            if token_lower == ":":
                # start of definition
                if j + 1 >= len(tokens):
                    # invalid, but assume no such case in tests
                    break
                word_name = tokens[j + 1].lower()
                # check if word_name is a number
                if parse_number(word_name) is not None:
                    raise ValueError("illegal operation")
                # collect definition tokens until ';'
                definition = []
                j += 2
                while True:
                    if j >= len(tokens):
                        # continue to next input line if ; not found
                        i += 1
                        if i >= len(input_data):
                            # invalid, but assume no such case
                            break
                        tokens = input_data[i].split()
                        j = 0
                        continue
                    if tokens[j].lower() == ";":
                        break
                    definition.append(tokens[j].lower())
                    j += 1
                # store definition with a snapshot of current dictionary
                # shallow copy is sufficient
                global_user_words[word_name] = (definition, global_user_words.copy())
            else:
                process_token(token, global_user_words)
            j += 1
        i += 1

    return stack
