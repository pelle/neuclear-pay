package org.neuclear.pay;

import java.util.Date;

/**
 * 
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:37:10 PM
 */
public class PaymentReceipt extends Payment {
    private final PaymentRequest preq;
    private final String id;
    private final PaymentProcessor proc;


    PaymentReceipt(PaymentProcessor proc, PaymentRequest preq, String id) {
        this.preq = preq;
        this.id = id;
        this.proc = proc;
    }

    public final double getAmount() {
        return preq.getAmount();
    }

    public final Date getValuedate() {
        return preq.getValuedate();
    }

    public final Account getTo() {
        return preq.getTo();
    }

    public final Account getFrom() {
        return preq.getFrom();
    }

    public final PaymentProcessor getProc() {
        return proc;
    }

    public final String getId() {
        return id;
    }
}
