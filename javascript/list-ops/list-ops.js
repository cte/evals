//
// This is only a SKELETON file for the 'List Ops' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class List {
  constructor(values = []) {
    this.values = values;
  }

  append(list) {
    return new List([...this.values, ...list.values]);
  }

  concat(listOfLists) {
    const concatenated = this.values.slice();
    for (const lst of listOfLists.values) {
      concatenated.push(...lst.values);
    }
    return new List(concatenated);
  }

  filter(fn) {
    return new List(this.values.filter(fn));
  }

  map(fn) {
    return new List(this.values.map(fn));
  }

  length() {
    return this.values.length;
  }

  foldl(fn, initial) {
    let acc = initial;
    for (const el of this.values) {
      acc = fn(acc, el);
    }
    return acc;
  }

  foldr(fn, initial) {
    let acc = initial;
    for (let i = this.values.length - 1; i >= 0; i--) {
      acc = fn(acc, this.values[i]);
    }
    return acc;
  }

  reverse() {
    return new List([...this.values].reverse());
  }
}
