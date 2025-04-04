export class Element {
  constructor(value) {
    this._value = value;
    this._next = null;
  }

  get value() {
    return this._value;
  }

  get next() {
    return this._next;
  }

  // Internal method to set the next element, used by List
  _setNext(element) {
    this._next = element;
  }
}

export class List {
  constructor(values = []) {
    this._head = null;
    this._length = 0;

    // Initialize list from array values
    values.forEach(value => this.add(new Element(value)));
  }

  add(nextElement) {
    // Add to the head of the list
    nextElement._setNext(this._head);
    this._head = nextElement;
    this._length++;
  }

  get length() {
    return this._length;
  }

  get head() {
    return this._head;
  }

  toArray() {
    const result = [];
    let current = this._head;
    while (current) {
      result.push(current.value);
      current = current.next;
    }
    return result;
  }

  reverse() {
    const reversedList = new List();
    let current = this._head;
    while (current) {
      reversedList.add(new Element(current.value)); // Add creates the reversed order
      current = current.next;
    }
    return reversedList;
  }
}
