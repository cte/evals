export class Zipper {
  constructor(focus, breadcrumbs = []) {
    this.focus = focus;
    this.breadcrumbs = breadcrumbs;
  }

  static fromTree(tree) {
    return new Zipper(tree, []);
  }

  toTree() {
    let tree = this.focus;
    let crumbs = this.breadcrumbs.slice();
    while (crumbs.length > 0) {
      const { direction, value, other } = crumbs.pop();
      if (direction === 'left') {
        tree = { value, left: tree, right: other };
      } else {
        tree = { value, left: other, right: tree };
      }
    }
    return tree;
  }

  value() {
    return this.focus.value;
  }

  left() {
    if (!this.focus.left) return null;
    const newBreadcrumb = {
      direction: 'left',
      value: this.focus.value,
      other: this.focus.right,
    };
    return new Zipper(this.focus.left, [...this.breadcrumbs, newBreadcrumb]);
  }

  right() {
    if (!this.focus.right) return null;
    const newBreadcrumb = {
      direction: 'right',
      value: this.focus.value,
      other: this.focus.left,
    };
    return new Zipper(this.focus.right, [...this.breadcrumbs, newBreadcrumb]);
  }

  up() {
    if (this.breadcrumbs.length === 0) return null;
    const crumbs = this.breadcrumbs.slice();
    const { direction, value, other } = crumbs.pop();
    let parentNode;
    if (direction === 'left') {
      parentNode = { value, left: this.focus, right: other };
    } else {
      parentNode = { value, left: other, right: this.focus };
    }
    return new Zipper(parentNode, crumbs);
  }

  setValue(newValue) {
    const newFocus = {
      value: newValue,
      left: this.focus.left,
      right: this.focus.right,
    };
    return new Zipper(newFocus, this.breadcrumbs.slice());
  }

  setLeft(newLeft) {
    const newFocus = {
      value: this.focus.value,
      left: newLeft,
      right: this.focus.right,
    };
    return new Zipper(newFocus, this.breadcrumbs.slice());
  }

  setRight(newRight) {
    const newFocus = {
      value: this.focus.value,
      left: this.focus.left,
      right: newRight,
    };
    return new Zipper(newFocus, this.breadcrumbs.slice());
  }
}
