package org.neuclear.exchange.orders.builders;

import org.neuclear.exchange.orders.ExchangeOrderGlobals;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.id.builders.EmbeddedSignedObjectBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jan 12, 2004
 * Time: 4:31:10 PM
 * To change this template use Options | File Templates.
 */
public class CancelExchangeOrderBuilder extends EmbeddedSignedObjectBuilder {
    public CancelExchangeOrderBuilder(final ExchangeOrderReceipt embedded) {
        super(ExchangeOrderGlobals.createQName(ExchangeOrderGlobals.CANCEL_TAGNAME), embedded);
    }
}
