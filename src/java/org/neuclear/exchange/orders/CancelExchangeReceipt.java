package org.neuclear.exchange.orders;

import org.dom4j.Element;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.verifier.VerifyingReader;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public final class CancelExchangeReceipt extends ExchangeTransactionContract {

    private CancelExchangeReceipt(SignedNamedCore core, CancelExchangeOrder order, final Date canceltime) {
        super(core, order.getAsset(), order.getAgent());
        this.canceltime = canceltime.getTime();
        this.order = order;
    }

    public CancelExchangeOrder getOrder() {
        return order;
    }

    public Timestamp getCanceltime() {
        return new Timestamp(canceltime);
    }

    private final long canceltime;
    private final CancelExchangeOrder order;

    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         *
         * @param elem
         * @return
         */
        public final SignedNamedObject read(final SignedNamedCore core, final Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().equals(AssetGlobals.NS_ASSET))
                throw new InvalidNamedObjectException(core.getName(), "Not in XML NameSpace: " + AssetGlobals.NS_ASSET.getURI());

            if (elem.getName().equals(ExchangeGlobals.CANCEL_RCPT_TAGNAME)) {
                final Date valuetime = TransferGlobals.parseValueTimeElement(elem);
                CancelExchangeOrder order = (CancelExchangeOrder) VerifyingReader.getInstance().read(elem.element(ExchangeGlobals.createQName(ExchangeGlobals.CANCEL_TAGNAME)));
                return new CancelExchangeReceipt(core, order, valuetime);
            }
            throw new InvalidNamedObjectException(core.getName(), "Not Matched");
        }
    }

}
