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
public class PaymentNotStartedException extends PaymentException {
    public PaymentNotStartedException(PaymentProcessor proc, Payment pmt,Date  time) {
        super(proc);
        this.pmt=pmt;
        this.time=time;
    }

    private Payment pmt;
    private Date time;

    public Payment getPayment() {
        return pmt;
    }

    public Date getTime() {
        return time;
    }

    public String getSubMessage() {
        return "This payment commences at: "+pmt.getValuedate()+". Action attempted at: "+time;
    }
}
