package org.neuclear.exchange.orders;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.asset.orders.exchanges.Exchange;
import org.neuclear.asset.orders.TransferContract;
import org.neuclear.asset.orders.TransferContract;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.exchange.contracts.ExchangeAgent;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 12:01:03 PM
 */
public final class ExchangeOrder extends ExchangeTransactionContract {
    private ExchangeOrder(final SignedNamedCore core,
                    final Asset bidAsset, final ExchangeAgent agent, final double bid,
                    final Asset neededAsset, final double neededAmount,  final String comment, final Date expires)  {
        super(core, bidAsset,agent);
        this.neededAsset = neededAsset;
        this.neededAmount = neededAmount;
        this.bidAmount = bid;
        this.comment = (comment != null) ? comment : "";
        this.expires = expires.getTime();
    }

    public final Date getExpiry() {
        return new Timestamp(expires);
    }

    public final Identity getBidder() {
        return getSignatory();
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

    private final Asset neededAsset;
    private final double neededAmount;
    private final double bidAmount;
    private final String comment;
    private final long expires;

}
