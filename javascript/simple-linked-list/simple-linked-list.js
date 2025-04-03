//
// This is only a SKELETON file for the 'Simple Linked List' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class Element {
  constructor(value, next = null) {
    this._value = value;
    this._next = next;
  }

  get value() {
    return this._value;
  }

  get next() {
    return this._next;
  }
}

export class List {
  constructor(values = []) {
    this._head = null;
    this._length = 0;

    if (Array.isArray(values)) {
      values.forEach(value => {
        this.add(new Element(value));
      });
    }
  }

  add(element) {
    element._next = this._head;
    this._head = element;
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
      reversedList.add(new Element(current.value));
      current = current.next;
    }
    return reversedList;
  }
}
