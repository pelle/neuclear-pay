package org.neuclear.pay;

import org.neuclear.ledger.*;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:12:54 PM
 * To change this template use Options | File Templates.
 */
public class Account {

    private final Book book;
    private final PaymentProcessor proc;

    Account(PaymentProcessor proc, Book book) {
        this.book = book;
        this.proc = proc;
    }

    public final PaymentProcessor getProc() {
        return proc;
    }

    final Book getBook() {
        return book;
    }

    final public String getID() {
        return book.getBookID();
    }

    final public double getBalance() throws LowlevelLedgerException {
        return book.getBalance();
    }

    final public double getAvailableBalance() throws LowlevelLedgerException {
        return book.getAvailableBalance();
    }

    final public double getBalance(Date time) throws LowlevelLedgerException {
        return book.getBalance(time);
    }

    final public double getAvailableBalance(Date time) throws LowlevelLedgerException {
        return book.getAvailableBalance(time);
    }

    final public String getDisplayName() {
        return book.getDisplayName();
    }

    final public PaymentReceipt pay(Account to, double amount, Date valuedate, String comment) throws UnknownBookException, UnBalancedTransactionException, InvalidTransactionException, LowlevelLedgerException, NegativePaymentException, InsufficientFundsException {
        if (amount < 0)
            throw new NegativePaymentException(proc, amount);
        if (getAvailableBalance(valuedate) - amount < 0)
            throw new InsufficientFundsException(proc, this, amount);

        return proc.processPayment(new PaymentRequest(this, to, amount, valuedate, comment));
    }

    final public HeldPaymentReceipt hold(Account to, double amount, Date valuedate, Date helduntil, String comment) throws UnknownBookException, UnBalancedTransactionException, InvalidTransactionException, LowlevelLedgerException, NegativePaymentException, InsufficientFundsException {
        if (amount < 0)
            throw new NegativePaymentException(proc, amount);
        if (getAvailableBalance(valuedate) - amount < 0)
            throw new InsufficientFundsException(proc, this, amount);

        return proc.processHeldPayment(new HeldPaymentRequest(this, to, amount, valuedate, helduntil, comment));
    }
}
