package org.neuclear.pay;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:13:28 PM
 */
public abstract class Payment {

    Payment() {
    }

    public abstract double getAmount();

    public abstract Date getValuedate();

    public abstract Account getTo();

    public abstract Account getFrom();

}
