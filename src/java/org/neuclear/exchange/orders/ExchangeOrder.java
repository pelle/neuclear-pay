package org.neuclear.exchange.orders;

import org.dom4j.Element;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.id.*;

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
    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         *
         * @param elem
         * @return
         */
        public final SignedNamedObject read(final SignedNamedCore core, final Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().equals(AssetGlobals.NS_ASSET))
                throw new InvalidNamedObjectException(core.getName(),"Not in XML NameSpace: "+AssetGlobals.NS_ASSET.getURI());

            if (elem.getName().equals(ExchangeGlobals.EXCHANGE_TAGNAME))
                return new ExchangeOrder(core,
                        ExchangeGlobals.parseBidAssetTag(elem),
                        ExchangeGlobals.parseAgentTag(elem),
                        TransferGlobals.parseAmountTag(elem),
                        ExchangeGlobals.parseSettlementAssetTag(elem),
                        TransferGlobals.parseAmountTag(elem),
                        TransferGlobals.getCommentElement(elem),
                        null//TODO getExpiryTime
                        );
            throw new InvalidNamedObjectException(core.getName(),"Not Matched");
        }


    }

}
