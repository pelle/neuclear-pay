package org.neuclear.pay;

import org.neuclear.ledger.LedgerException;
import org.neuclear.ledger.Ledger;

/**
 * 
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public class PaymentLargerThanHeldException extends PaymentException {
    public PaymentLargerThanHeldException(PaymentProcessor proc, HeldPaymentReceipt hold, double amount) {
        super(proc);
        this.hold=hold;
        this.amount=amount;
    }

    private HeldPaymentReceipt hold;
    private double amount;

    public HeldPaymentReceipt getHeldPaymentReceipt() {
        return hold;
    }

    public double getAmount() {
        return amount;
    }

    public String getSubMessage() {
        return "Payment requested: "+amount+" is larger than held amount: "+hold.getAmount();
    }
}
