package org.neuclear.pay;

import org.neuclear.ledger.LedgerException;
import org.neuclear.ledger.Ledger;

import java.util.Date;

/**
 * 
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public class ExpiredHeldPaymentException extends PaymentException {
    public ExpiredHeldPaymentException(PaymentProcessor proc, Held held,Date  time) {
        super(proc);
        this.held=held;
        this.time=time;
    }

    private Held held;
    private Date time;

    public Held getHeld() {
        return held;
    }

    public Date getTime() {
        return time;
    }

    public String getSubMessage() {
        return "Not possible to complete held payment at this time: "+time+". Hold expired at: "+held.getHeldUntil();
    }
}
