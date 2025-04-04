//
// This is only a SKELETON file for the 'Forth' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Forth {
  constructor() {
    this._stack = [];
    // Primitives map words to functions directly modifying the stack/state
    this._words = {
      '+': () => this.binaryOp((a, b) => a + b),
      '-': () => this.binaryOp((a, b) => a - b),
      '*': () => this.binaryOp((a, b) => a * b),
      '/': () => this.binaryOp((a, b) => {
        if (b === 0) throw new Error('Division by zero');
        // Integer division consistent with tests
        return Math.floor(a / b);
      }),
      'dup': () => this.dup(),
      'drop': () => this.drop(),
      'swap': () => this.swap(),
      'over': () => this.over(),
    };
    this._definingWord = false; // Are we currently defining a new word?
    this._currentWordName = null; // Name of the word being defined
    this._currentWordActions = []; // Store compiled actions for the definition
  }

  evaluate(program) {
    const tokens = program.toLowerCase().split(/\s+/);

    for (let i = 0; i < tokens.length; i++) {
      const token = tokens[i];
      if (token === '') continue; // Skip empty tokens

      if (this._definingWord) {
        // --- Inside a definition ---
        if (token === ';') {
          // End of definition
          if (!this._currentWordName || this._currentWordActions.length === 0) {
             throw new Error('Invalid definition');
          }
          // Store the compiled actions as the word's execution logic
          const actionsToExecute = [...this._currentWordActions]; // Capture current actions
          this._words[this._currentWordName] = () => this.executeWordActions(actionsToExecute);

          // Reset definition state
          this._definingWord = false;
          this._currentWordName = null;
          this._currentWordActions = [];
        } else if (!this._currentWordName) {
           // This token is the name of the new word
           if (!isNaN(parseInt(token))) {
              throw new Error('Invalid definition'); // Cannot redefine numbers
           }
           this._currentWordName = token;
        } else {
          // This token is part of the definition body - compile it to an action
          if (!isNaN(parseInt(token))) {
            // If it's a number, create an action to push it onto the stack
            const num = parseInt(token);
            this._currentWordActions.push(() => this._stack.push(num));
          } else if (this._words[token]) {
            // If it's a known word, capture its *current* action
            const action = this._words[token];
            this._currentWordActions.push(action);
          } else {
            // Word is not known at definition time - this is an error.
            throw new Error('Unknown command');
          }
        }
      } else {
        // --- Outside a definition ---
        if (token === ':') {
          // Start of definition
          this._definingWord = true;
          this._currentWordName = null; // Reset name for the new definition
          this._currentWordActions = []; // Reset actions
        } else if (!isNaN(parseInt(token))) {
          // It's a number, push onto stack
          this._stack.push(parseInt(token));
        } else if (this._words[token]) {
          // It's a known word, execute its associated action
          this._words[token]();
        } else {
          // Unknown word
          throw new Error('Unknown command');
        }
      }
    }

    // Check if definition was left open
    if (this._definingWord) {
      throw new Error('Invalid definition'); // Unterminated definition
    }
  }

  get stack() {
    return this._stack;
  }

  // Executes a pre-compiled list of actions for a custom word
  executeWordActions(actions) {
      for (const action of actions) {
          action();
      }
  }

  // --- Helper methods (remain unchanged) ---

  checkStackSize(required) {
    if (this._stack.length < required) {
      throw new Error('Stack empty');
    }
  }

  binaryOp(op) {
    this.checkStackSize(2);
    const b = this._stack.pop();
    const a = this._stack.pop();
    this._stack.push(op(a, b));
  }

  dup() {
    this.checkStackSize(1);
    this._stack.push(this._stack[this._stack.length - 1]);
  }

  drop() {
    this.checkStackSize(1);
    this._stack.pop();
  }

  swap() {
    this.checkStackSize(2);
    const b = this._stack.pop();
    const a = this._stack.pop();
    this._stack.push(b);
    this._stack.push(a);
  }

  over() {
    this.checkStackSize(2);
    this._stack.push(this._stack[this._stack.length - 2]);
  }
}
