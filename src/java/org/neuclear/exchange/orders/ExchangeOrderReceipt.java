package org.neuclear.exchange.orders;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.asset.orders.exchanges.Exchange;
import org.neuclear.asset.orders.TransferContract;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.asset.contracts.Asset;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 11:59:36 AM
 */
public final class ExchangeOrderReceipt extends AssetTransactionContract {

    private ExchangeOrderReceipt(final SignedNamedCore core, final Asset asset, Identity agent, String orderid, Date valuetime, Asset neededAsset, double neededAmount, double bidAmount, String comment, Date expires) {
        super(core, asset);
        this.agent = agent;
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

    public Identity getAgent() {
        return agent;
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

    private final Identity agent;
    private final String orderid;
    private final long valuetime;
    private final Asset neededAsset;
    private final double neededAmount;
    private final double bidAmount;
    private final String comment;
    private final long expires;

}
