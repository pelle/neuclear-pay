package org.neuclear.asset.orders.builders;

import org.neuclear.id.builders.EmbeddedSignedObjectBuilder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.asset.orders.TransferGlobals;
import org.dom4j.QName;
import org.dom4j.DocumentException;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jan 10, 2004
 * Time: 6:26:50 PM
 * To change this template use Options | File Templates.
 */
public class ReceiptBuilder extends EmbeddedSignedObjectBuilder {
    protected ReceiptBuilder(final QName qname, final SignedNamedObject embedded, final QName timename,Date timestamp)  {
        super(qname, embedded);
        getElement().addElement(timename).setText(TimeTools.formatTimeStamp(timestamp));
    }
    public ReceiptBuilder(final QName qname, final SignedNamedObject embedded, final Date timestamp)  {
        this(qname,embedded,TransferGlobals.createQName(TransferGlobals.VALUE_TIME_TAG),timestamp);
    }
}
