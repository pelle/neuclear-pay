package org.neuclear.exchange.orders;

import org.dom4j.Element;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.Value;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.id.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 12:01:03 PM
 */
public final class ExchangeOrder extends ExchangeTransactionContract {
    private ExchangeOrder(final SignedNamedCore core,
                          final Service bidAsset, final ExchangeAgent agent, final Value amount,
                          BidItem items[], final String comment, final Date expires) {
        super(core, bidAsset, agent);
        this.items = makeSafeCopy(items);
        this.amount = amount;
        this.comment = (comment != null) ? comment : "";
        this.expires = expires.getTime();
    }

    private static BidItem[] makeSafeCopy(final BidItem src[]) {
        BidItem items[] = new BidItem[src.length];
        for (int i = 0; i < src.length; i++)
            items[i] = src[i];
        return items;
    }

    public final Date getExpiry() {
        return new Timestamp(expires);
    }

    public final Signatory getBidder() {
        return getSignatory();
    }


    public final Value getAmount() {
        return amount;
    }

    public final String getComment() {
        return comment;
    }

    public final Iterator iterateBidItems() {
        return new Iterator() {
            private int i = 0;

            public void remove() {
            }

            public boolean hasNext() {
                return i < items.length;
            }

            public Object next() {
                return items[i++];
            }
        };
    }

    private final Value amount;
    private final String comment;
    private final long expires;
    private final BidItem items[];

    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         *
         * @param elem
         * @return
         */
        public final SignedNamedObject read(final SignedNamedCore core, final Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().equals(ExchangeOrderGlobals.NS_EXCHANGE))
                throw new InvalidNamedObjectException(core.getName(), "Not in XML NameSpace: " + ExchangeOrderGlobals.NS_EXCHANGE.getURI());

            if (elem.getName().equals(ExchangeOrderGlobals.EXCHANGE_TAGNAME))
                return new ExchangeOrder(core,
                        TransferGlobals.parseAssetTag(elem),
                        ExchangeOrderGlobals.parseAgentTag(elem),
                        TransferGlobals.parseValueTag(elem),
                        parseBidItems(elem),
                        TransferGlobals.parseCommentElement(elem),
                        TransferGlobals.parseTimeStampElement(elem, ExchangeOrderGlobals.createQName(ExchangeOrderGlobals.EXPIRY_TAG)));
            throw new InvalidNamedObjectException(core.getName(), "Not Matched");
        }

        private BidItem[] parseBidItems(Element elem) throws InvalidNamedObjectException {
            List list = elem.elements(ExchangeOrderGlobals.createQName(ExchangeOrderGlobals.BID_ITEM_TAG));
            BidItem items[] = new BidItem[list.size()];
            for (int i = 0; i < list.size(); i++)
                items[i] = parseBidItem(elem);
            return items;
        }

        private BidItem parseBidItem(Element element) throws InvalidNamedObjectException {
            return new BidItem(TransferGlobals.parseAssetTag(element),
                    TransferGlobals.parseValueTag(element));

        }


    }

}
