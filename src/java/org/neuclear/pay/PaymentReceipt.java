package org.neuclear.pay;

import java.util.Date;

/**
 * 
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:37:10 PM
 */
public class PaymentReceipt extends Payment {
    private PaymentRequest preq;
    private String id;
    private PaymentProcessor proc;


    PaymentReceipt(PaymentProcessor proc,PaymentRequest preq,String id) {
        this.preq=preq;
        this.id=id;
        this.proc=proc;
    }

    public double getAmount() {
        return preq.getAmount();
    }

    public Date getValuedate() {
        return preq.getValuedate();
    }

    public Account getTo() {
        return preq.getTo();
    }

    public Account getFrom() {
        return preq.getFrom();
    }

    public PaymentProcessor getProc() {
        return proc;
    }
    public String getId() {
        return id;
    }
}
