package org.neuclear.exchange.orders;

import org.neuclear.id.SignedNamedCore;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.exchange.contracts.ExchangeAgent;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 11:59:36 AM
 */
public final class ExchangeOrderReceipt extends ExchangeTransactionContract {

    private ExchangeOrderReceipt(final SignedNamedCore core, final Asset asset, final ExchangeAgent agent, final String orderid, final Date valuetime, Asset neededAsset, double neededAmount, double bidAmount, final String comment, final Date expires) {
        super(core, asset,agent);
        this.orderid = orderid;
        this.valuetime = valuetime.getTime();
        this.neededAsset = neededAsset;
        this.neededAmount = neededAmount;
        this.bidAmount = bidAmount;
        this.comment = comment;
        this.expires = expires.getTime();
    }
    public ExchangeOrder getExchangeOrder(){
        //TODO Implement
        return null;
    }


    public String getOrderid() {
        return orderid;
    }

    public Timestamp getValuetime() {
        return new Timestamp(valuetime);
    }

    public Asset getNeededAsset() {
        return neededAsset;
    }

    public double getNeededAmount() {
        return neededAmount;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getExpires() {
        return new Timestamp(expires);
    }

    private final String orderid;
    private final long valuetime;
    private final Asset neededAsset;
    private final double neededAmount;
    private final double bidAmount;
    private final String comment;
    private final long expires;

}
