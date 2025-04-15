import math

class StackUnderflowError(Exception):
    """Exception raised when Stack is not full enough to perform the operation."""
    pass


def evaluate(input_data):
    """
    Evaluates a list of strings representing Forth commands using an iterative approach.

    Args:
        input_data: A list of strings, each representing a line of Forth code.

    Returns:
        A list representing the final state of the stack.

    Raises:
        StackUnderflowError: If an operation requires more items than are on the stack.
        ZeroDivisionError: If division by zero is attempted.
        ValueError: If an unknown word is encountered or definition is invalid.
    """
    definitions = {}
    stack = []
    defining = False
    new_word_name = None
    new_word_definition = []

    # --- Built-in Operations ---
    def op_add():
        if len(stack) < 2: raise StackUnderflowError("Insufficient number of items in stack")
        b = stack.pop(); a = stack.pop(); stack.append(a + b)
    def op_sub():
        if len(stack) < 2: raise StackUnderflowError("Insufficient number of items in stack")
        b = stack.pop(); a = stack.pop(); stack.append(a - b)
    def op_mul():
        if len(stack) < 2: raise StackUnderflowError("Insufficient number of items in stack")
        b = stack.pop(); a = stack.pop(); stack.append(a * b)
    def op_div():
        if len(stack) < 2: raise StackUnderflowError("Insufficient number of items in stack")
        b = stack.pop(); a = stack.pop()
        if b == 0: raise ZeroDivisionError("divide by zero")
        stack.append(int(a / b)) # Integer division
    def op_dup():
        if not stack: raise StackUnderflowError("Insufficient number of items in stack")
        stack.append(stack[-1])
    def op_drop():
        if not stack: raise StackUnderflowError("Insufficient number of items in stack")
        stack.pop()
    def op_swap():
        if len(stack) < 2: raise StackUnderflowError("Insufficient number of items in stack")
        stack[-1], stack[-2] = stack[-2], stack[-1]
    def op_over():
        if len(stack) < 2: raise StackUnderflowError("Insufficient number of items in stack")
        stack.append(stack[-2])

    builtins = {
        '+': op_add, '-': op_sub, '*': op_mul, '/': op_div,
        'dup': op_dup, 'drop': op_drop, 'swap': op_swap, 'over': op_over
    }

    # --- Token Processing ---
    token_stream = []
    for line in input_data:
        token_stream.extend(line.split()) # Split all lines into a single token stream

    idx = 0
    while idx < len(token_stream):
        token = token_stream[idx]
        token_lower = token.lower()

        if defining:
            # --- Handling Definition Mode ---
            if token == ';':
                if new_word_name is None:
                    # Should not happen if ':' logic is correct
                    raise ValueError("Invalid definition state: missing name before ';'")

                # Store the definition (lowercase words, numbers as strings)
                processed_definition = []
                for def_token in new_word_definition:
                    try:
                        # Check if it's a number, store as string if so
                        int(def_token)
                        processed_definition.append(def_token)
                    except ValueError:
                        # Store word as lowercase
                        processed_definition.append(def_token.lower())

                definitions[new_word_name] = processed_definition # new_word_name is already lowercase
                defining = False
                new_word_name = None
                new_word_definition = []
                idx += 1 # Consume ';'
            else:
                # Add token to the raw definition list being built
                new_word_definition.append(token)
                idx += 1 # Consume token within definition

        elif token == ':':
            # --- Entering Definition Mode ---
            defining = True
            idx += 1 # Move past ':'
            if idx >= len(token_stream):
                raise ValueError("Invalid definition: missing word name after ':'")

            new_word_name_token = token_stream[idx]
            # Check if trying to define a number (illegal operation)
            try:
                int(new_word_name_token)
                # If int() succeeds, it's an illegal redefinition of a number
                raise ValueError("illegal operation: cannot redefine numbers")
            except ValueError:
                # It's not a number, proceed to define the word
                new_word_name = new_word_name_token.lower() # Store definition name lowercase
                new_word_definition = [] # Start new definition list
                idx += 1 # Consume word name

        elif token_lower in definitions:
            # --- Executing User-Defined Word ---
            # Get a copy of the definition tokens
            definition_tokens = definitions[token_lower][:]
            # Replace the current token (the word name) with its definition
            token_stream[idx:idx+1] = definition_tokens
            # Do not increment idx, the loop will now process the first token
            # of the expanded definition in the next iteration.

        elif token_lower in builtins:
            # --- Executing Built-in Word ---
            builtins[token_lower]()
            idx += 1 # Consume built-in word token

        else:
            # --- Handling Numbers ---
            try:
                stack.append(int(token))
                idx += 1 # Consume number token
            except ValueError:
                # If it's not a definition, not built-in, and not a number, it's undefined
                raise ValueError(f"undefined operation: {token}")


    # After processing all tokens, check if still defining (missing ';')
    if defining:
        raise ValueError("Invalid definition: definition not closed with ';'")

    return stack
