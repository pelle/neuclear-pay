package org.neuclear.pay;

import java.util.Date;

/**
 * 
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public class PaymentRequest extends Payment {
    private double amount;
    private Date  valuedate;
    private Account to;
    private Account from;
    private String comment = "";

    public PaymentRequest( Account from, Account to, double amount, Date valuedate, String comment ) throws NegativePaymentException{
        if (amount<0)
            throw new NegativePaymentException(from.getProc(), amount);
        this.from=from;
        this.to=to;
        this.amount=amount;
        this.valuedate=valuedate;
        this.comment = comment;
    }

    public double getAmount() {
        return amount;
    }

    public Date getValuedate() {
        return valuedate;
    }

    public Account getTo() {
        return to;
    }

    public Account getFrom() {
        return from;
    }
    
    public String getComment() {
        return comment;
    }


}
