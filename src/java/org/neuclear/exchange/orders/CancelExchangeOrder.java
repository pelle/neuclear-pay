package org.neuclear.exchange.orders;

import org.dom4j.Element;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.id.*;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public final class CancelExchangeOrder extends ExchangeTransactionContract {

    private CancelExchangeOrder(final SignedNamedCore core, final ExchangeOrderReceipt receipt) {
        super(core, receipt.getAsset(), receipt.getAgent());
        this.receipt = receipt;
    }

    public ExchangeOrderReceipt getReceipt() {
        return receipt;
    }


    private final ExchangeOrderReceipt receipt;

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

            if (elem.getName().equals(ExchangeOrderGlobals.CANCEL_TAGNAME)) {
                final ExchangeOrderReceipt receipt = (ExchangeOrderReceipt) TransferGlobals.parseEmbedded(elem, ExchangeOrderGlobals.createQName(ExchangeOrderGlobals.EXCHANGE_RCPT_TAGNAME));
                if (core.getSignatory().getName().equals(receipt.getOrder().getSignatory().getName())
                        || core.getSignatory().equals(new Signatory(receipt.getOrder().getAgent().getServiceKey())))
                    return new CancelExchangeOrder(core, receipt);
                throw new InvalidNamedObjectException("Signer does not have permission to Cancel");
            }
            throw new InvalidNamedObjectException(core.getName(), "Not Matched");
        }
    }
}
