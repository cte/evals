export class RestAPI {
  constructor(database = { users: [] }) {
    // Deep copy to avoid modifying the original seed data
    this.users = JSON.parse(JSON.stringify(database.users));
  }

  // Helper to find a user by name
  _findUser(name) {
    return this.users.find(user => user.name === name);
  }

  // Helper to update debt records, removing entries if amount is 0
  _updateDebt(debtor, creditor, amount) {
    if (amount === 0) {
      delete debtor.owes[creditor.name];
      delete creditor.owed_by[debtor.name];
    } else {
      debtor.owes[creditor.name] = amount;
      creditor.owed_by[debtor.name] = amount;
    }
  }

  get(url) {
    const urlParts = url.split('?');
    const path = urlParts[0];
    const params = new URLSearchParams(urlParts[1] || '');

    if (path === '/users') {
      const userNames = params.get('users');
      if (userNames) {
        const namesList = userNames.split(',');
        const filteredUsers = this.users.filter(user => namesList.includes(user.name));
        return { users: filteredUsers };
      } else {
        // Return a deep copy to prevent external modification
        return { users: JSON.parse(JSON.stringify(this.users)) };
      }
    }
    // Handle other GET endpoints if needed, though not specified by tests
    throw new Error(`Unknown GET endpoint: ${path}`);
  }

  post(url, payload) {
    if (url === '/add') {
      const userName = payload.user;
      // Check if user already exists (optional, but good practice)
      if (this._findUser(userName)) {
         throw new Error(`User ${userName} already exists.`);
      }
      const newUser = {
        name: userName,
        owes: {},
        owed_by: {},
        balance: 0,
      };
      this.users.push(newUser);
      // Return a copy
      return JSON.parse(JSON.stringify(newUser));
    }

    if (url === '/iou') {
      const { lender: lenderName, borrower: borrowerName, amount } = payload;
      const lender = this._findUser(lenderName);
      const borrower = this._findUser(borrowerName);

      if (!lender || !borrower) {
        throw new Error('Lender or borrower not found.');
      }

      // Calculate existing debt/credit between them
      const lenderOwesBorrower = lender.owes[borrowerName] || 0;
      const borrowerOwesLender = borrower.owes[lenderName] || 0; // Should be same as lender.owed_by[borrowerName]

      // Adjust balances first
      lender.balance += amount;
      borrower.balance -= amount;

      // Settle debts
      if (lenderOwesBorrower > 0) {
          if (lenderOwesBorrower >= amount) {
              // New loan is less than or equal to existing debt lender->borrower
              const remainingDebt = lenderOwesBorrower - amount;
              this._updateDebt(lender, borrower, remainingDebt);
          } else {
              // New loan is more than existing debt lender->borrower
              // Lender clears debt to borrower, and borrower now owes lender the difference
              const newDebtToLender = amount - lenderOwesBorrower;
              this._updateDebt(lender, borrower, 0); // Clear lender->borrower debt
              this._updateDebt(borrower, lender, newDebtToLender); // Create borrower->lender debt
          }
      } else {
          // No existing debt from lender to borrower, or borrower already owes lender
          const totalDebtToLender = borrowerOwesLender + amount;
          this._updateDebt(borrower, lender, totalDebtToLender);
      }


      // Return the updated lender and borrower, sorted alphabetically
      const updatedUsers = [lender, borrower]
        .sort((a, b) => a.name.localeCompare(b.name))
        .map(user => JSON.parse(JSON.stringify(user))); // Return copies

      return { users: updatedUsers };
    }

    // Handle other POST endpoints if needed
    throw new Error(`Unknown POST endpoint: ${url}`);
  }
}
