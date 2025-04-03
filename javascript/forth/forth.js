//
// This is only a SKELETON file for the 'Forth' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Forth {
  constructor() {
    this._stack = [];
    this._words = Object.create(null);
  }

  evaluate(input) {
    const tokens = this._tokenize(input);
    this._execute(tokens);
  }

  get stack() {
    return [...this._stack];
  }

  _tokenize(input) {
    return input
      .replace(/\s+/g, ' ')
      .trim()
      .split(' ')
      .filter(token => token.length > 0);
  }

  _execute(tokens, dict = this._words) {
    let i = 0;
    while (i < tokens.length) {
      let token = tokens[i];
      const lowerToken = token.toLowerCase();

      if (lowerToken === ':') {
        i++;
        if (i >= tokens.length) throw new Error('Invalid definition');
        const wordName = tokens[i].toLowerCase();
        if (/^-?\d+$/.test(wordName)) throw new Error('Invalid definition');
        const definition = [];
        i++;
        while (i < tokens.length && tokens[i].toLowerCase() !== ';') {
          definition.push(tokens[i]);
          i++;
        }
        if (i === tokens.length) throw new Error('Invalid definition');
        this._words[wordName] = {
          tokens: definition.map(t => t.toLowerCase()),
          dict: { ...this._words }
        };
      } else {
        if (/^-?\d+$/.test(token)) {
          this._stack.push(parseInt(token, 10));
        } else if (dict[lowerToken]) {
          const def = dict[lowerToken];
          this._execute([...def.tokens], def.dict);
        } else if (['+', '-', '*', '/'].includes(lowerToken)) {
          this._binaryOp(lowerToken);
        } else if (lowerToken === 'dup') {
          if (this._stack.length < 1) throw new Error('Stack empty');
          this._stack.push(this._stack[this._stack.length - 1]);
        } else if (lowerToken === 'drop') {
          if (this._stack.length < 1) throw new Error('Stack empty');
          this._stack.pop();
        } else if (lowerToken === 'swap') {
          if (this._stack.length < 2) throw new Error('Stack empty');
          const top = this._stack.pop();
          const second = this._stack.pop();
          this._stack.push(top, second);
        } else if (lowerToken === 'over') {
          if (this._stack.length < 2) throw new Error('Stack empty');
          this._stack.push(this._stack[this._stack.length - 2]);
        } else {
          throw new Error('Unknown command');
        }
      }
      i++;
    }
  }

  _binaryOp(op) {
    if (this._stack.length < 2) throw new Error('Stack empty');
    const b = this._stack.pop();
    const a = this._stack.pop();
    let result;
    switch (op) {
      case '+':
        result = a + b;
        break;
      case '-':
        result = a - b;
        break;
      case '*':
        result = a * b;
        break;
      case '/':
        if (b === 0) throw new Error('Division by zero');
        result = Math.trunc(a / b);
        break;
    }
    this._stack.push(result);
  }
}
