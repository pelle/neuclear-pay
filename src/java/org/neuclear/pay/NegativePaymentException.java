package org.neuclear.pay;

import org.neuclear.ledger.LedgerException;
import org.neuclear.ledger.Ledger;

/**
 * 
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public class NegativePaymentException extends PaymentException {
    public NegativePaymentException(PaymentProcessor proc, double amount) {
        super(proc);
        this.amount=amount;
    }

    private double amount;


    public double getAmount() {
        return amount;
    }

    public String getSubMessage() {
        return "Not possible to perform payment of negative amount: "+amount;
    }
}
