import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class BankAccount {

    private AtomicInteger balance = new AtomicInteger(0);
    private AtomicBoolean isOpen = new AtomicBoolean(false);

    void open() throws BankAccountActionInvalidException {
        if (!isOpen.compareAndSet(false, true)) {
            throw new BankAccountActionInvalidException("Account already open");
        }
        // Explicitly set balance to 0 on open, especially for re-opening scenarios
        balance.set(0);
    }

    void close() throws BankAccountActionInvalidException {
        if (!isOpen.compareAndSet(true, false)) {
             throw new BankAccountActionInvalidException("Account not open");
        }
        // Optionally clear balance on close, though not strictly required by instructions
        // balance.set(0);
    }

    int getBalance() throws BankAccountActionInvalidException {
        if (!isOpen.get()) {
            throw new BankAccountActionInvalidException("Account closed");
        }
        return balance.get();
    }

    // deposit and withdraw methods need to be synchronized to prevent race conditions
    // between checking the balance and updating it, especially for withdraw.
    // While AtomicInteger provides atomic operations, the sequence of check-then-act
    // (like checking balance before withdrawing) needs external synchronization.
    synchronized void deposit(int amount) throws BankAccountActionInvalidException {
        if (!isOpen.get()) {
            throw new BankAccountActionInvalidException("Account closed");
        }
        if (amount < 0) {
            throw new BankAccountActionInvalidException("Cannot deposit or withdraw negative amount");
        }
        balance.addAndGet(amount);
    }

    synchronized void withdraw(int amount) throws BankAccountActionInvalidException {
        if (!isOpen.get()) {
            throw new BankAccountActionInvalidException("Account closed");
        }
        if (amount < 0) {
            throw new BankAccountActionInvalidException("Cannot deposit or withdraw negative amount");
        }
        int currentBalance = balance.get();
        if (currentBalance < amount) {
            throw new BankAccountActionInvalidException("Cannot withdraw more money than is currently in the account");
        }
        balance.addAndGet(-amount);
    }
}