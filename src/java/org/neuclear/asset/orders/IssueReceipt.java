package org.neuclear.asset.orders;

import org.dom4j.Element;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:37:10 PM
 */
public class IssueReceipt extends AssetTransactionContract {

    private IssueReceipt(final SignedNamedCore core, final IssueOrder order, final Date valuetime) {
        super(core, order.getAsset());
        this.valuetime = valuetime.getTime();
        this.order = order;
    }

    public final IssueOrder getOrder() {
        return order;
    }

    public final Date getValueTime() {
        return new Timestamp(valuetime);
    }

    private final long valuetime;
    private final IssueOrder order;

    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         *
         * @param elem
         * @return
         */
        public final SignedNamedObject read(final SignedNamedCore core, final Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().getURI().equals(TransferGlobals.XFER_NSURI))
                throw new InvalidNamedObjectException(core.getName(), "Not in XML NameSpace: " + TransferGlobals.XFER_NSURI);
            if (!elem.getName().equals(TransferGlobals.ISSUE_RCPT_TAGNAME))
                throw new InvalidNamedObjectException(core.getName(), "Incorrect XML Tagname for reader: " + TransferGlobals.ISSUE_RCPT_TAGNAME);

            return new IssueReceipt(core,
                    (IssueOrder) TransferGlobals.parseEmbedded(elem, TransferGlobals.createQName(TransferGlobals.ISSUE_TAGNAME)),
                    TransferGlobals.parseValueTimeElement(elem));

        }
    }
}
