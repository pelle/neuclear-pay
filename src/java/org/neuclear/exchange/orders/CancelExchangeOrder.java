package org.neuclear.exchange.orders;

import org.dom4j.Element;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public final class CancelExchangeOrder extends ExchangeTransactionContract{

    private CancelExchangeOrder(final SignedNamedCore core, final Asset asset,final ExchangeAgent agent, final String exchangeid)  {
        super(core, asset,agent);
        this.exchangeid = exchangeid;
    }
    public ExchangeOrderReceipt getExchangeReceipt(){
        //TODO Implement
        return null;
    }

    public String getExchangeid() {
        return exchangeid;
    }

    private final String exchangeid;

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

            if (elem.getName().equals(ExchangeGlobals.CANCEL_TAGNAME))
                return new CancelExchangeOrder(core,
                        TransferGlobals.parseAssetTag(elem),
                        ExchangeGlobals.parseAgentTag(elem),
                        ExchangeGlobals.parseExchangeOrderId(elem));

            throw new InvalidNamedObjectException(core.getName(),"Not Matched");
        }
    }
}
