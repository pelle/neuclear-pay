package org.neuclear.pay;

import java.util.Date;

/**
 * 
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 12:01:03 PM
 */
public class HeldPaymentRequest extends PaymentRequest implements Held {
    public HeldPaymentRequest(Account from, Account to, double amount, Date valuedate,Date helduntil, String comment) throws NegativePaymentException {

        super(from, to, amount, valuedate, comment);

        this.helduntil=helduntil;
    }

    public Date getHeldUntil() {
        return helduntil;
    }
    private Date helduntil;
}
