package org.neuclear.exchange.orders.builders;

import org.dom4j.Element;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.Value;
import org.neuclear.commons.Utility;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.exchange.orders.ExchangeOrderGlobals;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.id.builders.EmbeddedSignedObjectBuilder;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jan 12, 2004
 * Time: 4:33:52 PM
 * To change this template use Options | File Templates.
 */
public class ExchangeCompletionOrderBuilder extends EmbeddedSignedObjectBuilder {
    public ExchangeCompletionOrderBuilder(final ExchangeOrderReceipt embedded, final Date exchangedate, final String recipient, final Value amount, final String comment) throws InvalidTransferException {
        super(ExchangeOrderGlobals.createQName(ExchangeOrderGlobals.COMPLETE_TAGNAME), embedded);
        final Element element = getElement();
        element.add(TransferGlobals.createElement(TransferGlobals.RECIPIENT_TAG, recipient));

        getElement().addElement(ExchangeOrderGlobals.createQName(ExchangeOrderGlobals.EXCHANGE_TIME_TAGNAME)).setText(TimeTools.formatTimeStamp(exchangedate));
        element.add(TransferGlobals.createValueTag(amount));

        if (!Utility.isEmpty(comment))
            element.add(TransferGlobals.createElement(TransferGlobals.COMMENT_TAG, comment));

    }
}
