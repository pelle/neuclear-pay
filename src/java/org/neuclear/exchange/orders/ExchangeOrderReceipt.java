package org.neuclear.exchange.orders;

import org.dom4j.Element;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 11:59:36 AM
 */
public final class ExchangeOrderReceipt extends ExchangeTransactionContract {

    private ExchangeOrderReceipt(final SignedNamedCore core, final ExchangeOrder order,final Date valuetime) {
        super(core, order.getAsset(), order.getAgent());
        this.valuetime = valuetime.getTime();
        this.order=order;
    }
    public ExchangeOrder getExchangeOrder(){
        return order;
    }

    public Timestamp getValuetime() {
        return new Timestamp(valuetime);
    }

    private final long valuetime;
    private final ExchangeOrder order;

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

            if (elem.getName().equals(ExchangeGlobals.EXCHANGE_RCPT_TAGNAME)){
                return new ExchangeOrderReceipt(core,
                        (ExchangeOrder) TransferGlobals.parseEmbedded(elem,ExchangeGlobals.createQName(ExchangeGlobals.EXCHANGE_TAGNAME)),
                        TransferGlobals.parseValueTimeElement(elem));
            }
            throw new InvalidNamedObjectException(core.getName(),"Not Matched");
        }
    }

}
