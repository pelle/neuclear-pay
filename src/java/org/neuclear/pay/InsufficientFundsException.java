package org.neuclear.pay;

import org.neuclear.ledger.LedgerException;
import org.neuclear.ledger.Ledger;

/**
 * 
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public class InsufficientFundsException extends PaymentException {
    public InsufficientFundsException(PaymentProcessor proc, Account account, double amount) {
        super(proc);
        this.account=account;
        this.amount=amount;
    }

    private Account account;
    private double amount;

    public Account getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

    public String getSubMessage() {
        return "Insufficient Funds in Account: "+account.getID()+" to pay: "+amount;
    }
}
