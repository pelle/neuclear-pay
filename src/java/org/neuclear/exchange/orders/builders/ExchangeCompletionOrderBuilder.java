package org.neuclear.exchange.orders.builders;

import org.neuclear.id.builders.EmbeddedSignedObjectBuilder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.exchange.orders.ExchangeOrder;
import org.neuclear.exchange.orders.ExchangeGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.Value;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.commons.Utility;
import org.neuclear.commons.time.TimeTools;
import org.dom4j.QName;
import org.dom4j.Element;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jan 12, 2004
 * Time: 4:33:52 PM
 * To change this template use Options | File Templates.
 */
public class ExchangeCompletionOrderBuilder extends EmbeddedSignedObjectBuilder{
    public ExchangeCompletionOrderBuilder(final ExchangeOrder embedded, final Date exchangedate,final Value amount, final String comment) throws InvalidTransferException {
        super(ExchangeGlobals.createQName(ExchangeGlobals.COMPLETE_TAGNAME), embedded);
        final Element element = getElement();
        getElement().addElement(ExchangeGlobals.EXCHANGE_TIME_TAGNAME).setText(TimeTools.formatTimeStamp(exchangedate));
        element.add(TransferGlobals.createValueTag(amount));

        if (!Utility.isEmpty(comment))
            element.add(TransferGlobals.createElement(TransferGlobals.COMMENT_TAG, comment));

    }
}
