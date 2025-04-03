//
// This is only a SKELETON file for the 'Rest API' exercise. It's been provided as a
// convenience to get you started writing code faster.
//

export class RestAPI {
  constructor(data) {
    this.users = {};
    if (data && data.users) {
      for (const user of data.users) {
        this.users[user.name] = { ...user };
      }
    }
  }

  get(url) {
    const urlObj = new URL('http://localhost' + url);
    const searchParams = urlObj.searchParams;
    let resultUsers;

    if (searchParams.has('users')) {
      const names = searchParams.get('users').split(',');
      resultUsers = names
        .map(name => this.users[name])
        .filter(Boolean)
        .sort((a, b) => a.name.localeCompare(b.name));
    } else {
      resultUsers = Object.values(this.users).sort((a, b) => a.name.localeCompare(b.name));
    }

    return { users: resultUsers.map(u => this._cloneUser(u)) };
  }

  post(url, payload) {
    if (url === '/add') {
      const name = payload.user;
      const newUser = {
        name,
        owes: {},
        owed_by: {},
        balance: 0,
      };
      this.users[name] = newUser;
      return this._cloneUser(newUser);
    }

    if (url === '/iou') {
      const { lender, borrower, amount } = payload;
      const lenderUser = this.users[lender];
      const borrowerUser = this.users[borrower];

      // Check if lender owes borrower
      const lenderOwesBorrower = lenderUser.owes[borrower] || 0;
      if (lenderOwesBorrower > 0) {
        if (lenderOwesBorrower > amount) {
          lenderUser.owes[borrower] -= amount;
          borrowerUser.owed_by[lender] -= amount;
        } else if (lenderOwesBorrower === amount) {
          delete lenderUser.owes[borrower];
          delete borrowerUser.owed_by[lender];
        } else {
          const remaining = amount - lenderOwesBorrower;
          delete lenderUser.owes[borrower];
          delete borrowerUser.owed_by[lender];
          lenderUser.owed_by[borrower] = (lenderUser.owed_by[borrower] || 0) + remaining;
          borrowerUser.owes[lender] = (borrowerUser.owes[lender] || 0) + remaining;
        }
      } else {
        // Borrower owes lender or no existing debt
        borrowerUser.owes[lender] = (borrowerUser.owes[lender] || 0) + amount;
        lenderUser.owed_by[borrower] = (lenderUser.owed_by[borrower] || 0) + amount;
      }

      // Update balances
      lenderUser.balance = this._calculateBalance(lenderUser);
      borrowerUser.balance = this._calculateBalance(borrowerUser);

      const users = [lenderUser, borrowerUser].sort((a, b) => a.name.localeCompare(b.name));
      return { users: users.map(u => this._cloneUser(u)) };
    }

    throw new Error('Unknown endpoint');
  }

  _calculateBalance(user) {
    const owedBySum = Object.values(user.owed_by).reduce((a, b) => a + b, 0);
    const owesSum = Object.values(user.owes).reduce((a, b) => a + b, 0);
    return owedBySum - owesSum;
  }

  _cloneUser(user) {
    return {
      name: user.name,
      owes: { ...user.owes },
      owed_by: { ...user.owed_by },
      balance: user.balance,
    };
  }
}
