package org.neuclear.pay;

import org.neuclear.ledger.*;

import java.util.Date;

/**
 * 
 * User: pelleb
 * Date: Jul 22, 2003
 * Time: 6:14:35 PM
 */
public class Issuer extends Account {
    Issuer(PaymentProcessor proc, Book book) {
        super(proc, book);
    }

    final public PaymentReceipt fundAccount(Account recipient, double amount, Date valueDate) throws UnknownBookException, UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException, NegativePaymentException {
        return getProc().processPayment(new PaymentRequest(this, recipient, amount, valueDate, "Funding"));
    }

    final public double getCirculationBalance(Date date) throws LowlevelLedgerException {
        return -getBook().getBalance(date);
    }

    final public double getCirculationBalance() throws LowlevelLedgerException {
        return getCirculationBalance(new Date());
    }

}
