package org.neuclear.asset.orders;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jan 12, 2004
 * Time: 4:44:24 PM
 * To change this template use Options | File Templates.
 */
public final class Amount extends Value {
    public Amount(double amount){
        this.amount=amount;
    }
    public double getAmount() {
        return amount;
    }
    private final double amount;
}
