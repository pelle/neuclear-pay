package org.neuclear.exchange.orders.builders;

import org.neuclear.id.builders.EmbeddedSignedObjectBuilder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.exchange.orders.ExchangeOrder;
import org.neuclear.exchange.orders.ExchangeGlobals;
import org.dom4j.QName;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jan 12, 2004
 * Time: 4:31:10 PM
 * To change this template use Options | File Templates.
 */
public class CancelExchangeOrderBuilder extends EmbeddedSignedObjectBuilder {
    public CancelExchangeOrderBuilder(final ExchangeOrder embedded) {
        super(ExchangeGlobals.createQName(ExchangeGlobals.CANCEL_TAGNAME), embedded);
    }
}
