package org.neuclear.exchange.orders;

import org.dom4j.Element;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.id.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 12:01:03 PM
 */
public final class ExchangeOrder extends ExchangeTransactionContract {
    private ExchangeOrder(final SignedNamedCore core,
                    final Asset bidAsset, final ExchangeAgent agent, final double bid,
                    BidItem items[],  final String comment, final Date expires)  {
        super(core, bidAsset,agent);
        this.items =makeSafeCopy(items);
        this.bidAmount = bid;
        this.comment = (comment != null) ? comment : "";
        this.expires = expires.getTime();
    }
    private static BidItem[] makeSafeCopy(final BidItem src[]) {
        BidItem items[]= new BidItem[src.length];
        for (int i=0;i<src.length;i++)
            items[i]=src[i];
        return items;
    }

    public final Date getExpiry() {
        return new Timestamp(expires);
    }

    public final Identity getBidder() {
        return getSignatory();
    }


    public final double getBidAmount() {
        return bidAmount;
    }

    public final String getComment() {
        return comment;
    }

    public final Iterator iterateBidItems(){
        return new Iterator(){
            private int i=0;
            public void remove() {
            }

            public boolean hasNext() {
                return i<items.length;
            }

            public Object next() {
                return items[i++];
            }
        };
    }
    private final double bidAmount;
    private final String comment;
    private final long expires;
    private final BidItem items[];

    public static  class BidItem {
        private BidItem(Asset asset, double amount) {
            this.asset = asset;
            this.amount = amount;
        }

        public final Asset getAsset() {
            return asset;
        }

        public final double getAmount() {
            return amount;
        }

        private final Asset asset;
        private final double amount;
    }
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
                        TransferGlobals.parseAssetTag(elem),
                        ExchangeGlobals.parseAgentTag(elem),
                        TransferGlobals.parseAmountTag(elem),
                        parseBidItems(elem),
                        TransferGlobals.getCommentElement(elem),
                        TransferGlobals.parseTimeStampElement(elem,ExchangeGlobals.createQName(ExchangeGlobals.EXPIRY_TAG))
                        );
            throw new InvalidNamedObjectException(core.getName(),"Not Matched");
        }

        private BidItem[] parseBidItems(Element elem) throws InvalidNamedObjectException {
            List list=elem.elements(ExchangeGlobals.createQName(ExchangeGlobals.BID_ITEM_TAG));
            BidItem items[]=new BidItem[list.size()];
            for (int i = 0; i < list.size(); i++)
                items[i]= parseBidItem(elem);
            return items;
        }

        private BidItem parseBidItem(Element element) throws InvalidNamedObjectException {
            return new BidItem(
                    TransferGlobals.parseAssetTag(element),
                    TransferGlobals.parseAmountTag(element));

        }


    }

}
