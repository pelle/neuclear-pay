package org.neuclear.asset.orders;

import org.neuclear.exchange.orders.ExchangeOrder;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jan 12, 2004
 * Time: 4:46:51 PM
 * To change this template use Options | File Templates.
 */
public final class SerialNumbers extends Value{
    public SerialNumbers(String[] src) {
        numbers= new String[src.length];
        for (int i=0;i<src.length;i++)
            numbers[i]=src[i];
    }

    public double getAmount() {
        return numbers.length;  //To change body of implemented methods use Options | File Templates.
    }
    public String getNumber(int i){
        return numbers[i];
    }
    private final String numbers[];
}
