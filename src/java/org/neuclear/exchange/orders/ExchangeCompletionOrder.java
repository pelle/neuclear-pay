package org.neuclear.exchange.orders;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.Value;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.id.*;
import org.dom4j.Element;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public final class ExchangeCompletionOrder extends ExchangeTransactionContract {
    private ExchangeCompletionOrder(final SignedNamedCore core, final ExchangeOrderReceipt receipt, final Identity counterparty, final Value amount, final Date exchangetime,final String comment) {
        super(core,receipt.getAsset(), receipt.getAgent());
        this.exchangetime = exchangetime.getTime();
        this.amount = amount;
        this.counterparty=counterparty;
        this.receipt=receipt;
        this.comment=comment;
    }

    public final Timestamp getExchangeTime() {
        return new Timestamp(exchangetime);
    }

    public final Value getAmount() {
        return amount;
    }

    public final Identity getCounterparty() {
        return counterparty;
    }

    public ExchangeOrderReceipt getReceipt() {
        return receipt;
    }

    public String getComment() {
        return comment;
    }

    private final long exchangetime;
    private final Value amount;
    private final Identity counterparty;
    private final ExchangeOrderReceipt receipt;
    private final String comment;

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

            if (elem.getName().equals(ExchangeGlobals.COMPLETE_TAGNAME)){
                return new ExchangeCompletionOrder(core,
                        (ExchangeOrderReceipt)TransferGlobals.parseEmbedded(elem,ExchangeGlobals.createQName(ExchangeGlobals.EXCHANGE_RCPT_TAGNAME)),
                        TransferGlobals.parseRecipientTag(elem),
                        TransferGlobals.parseValueTag(elem),
                        TransferGlobals.parseTimeStampElement(elem,ExchangeGlobals.createQName(ExchangeGlobals.EXCHANGE_TIME_TAGNAME)),
                        TransferGlobals.parseCommentElement(elem)
                        );
            }
            throw new InvalidNamedObjectException(core.getName(),"Not Matched");
        }
    }

}
