package org.neuclear.asset.orders;

import org.dom4j.Element;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public final class IssueOrder extends AssetTransactionContract {

    private IssueOrder(final SignedNamedCore core, final Asset asset, final String recipient, final Value amount, final String comment) {
        super(core, asset);
        this.amount = amount;
        this.comment = comment;
        this.recipient = recipient;
    }

    public final String getRecipient() {
        return recipient;
    }

    public final Value getAmount() {
        return amount;
    }

    public final String getComment() {
        return comment;
    }

    private final String recipient;

    private final Value amount;
    private final String comment;

    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         *
         * @param elem
         * @return
         */
        public final SignedNamedObject read(final SignedNamedCore core, final Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().getURI().equals(TransferGlobals.XFER_NSURI))
                throw new InvalidNamedObjectException(core.getName(), "Not in XML NameSpace: " + AssetGlobals.NS_ASSET.getURI());
            if (!elem.getName().equals(TransferGlobals.ISSUE_TAGNAME))
                throw new InvalidNamedObjectException(core.getName(), "Incorrect XML Tagname for reader: " + TransferGlobals.XFER_TAGNAME);
            final Asset asset = TransferGlobals.parseAssetTag(elem);
            if (asset.getIssuer() != null && core.getSignatory().equals(asset.getIssuer()))
                return new IssueOrder(core,
                        asset,
                        TransferGlobals.parseRecipientTag(elem),
                        TransferGlobals.parseValueTag(elem),
                        TransferGlobals.parseCommentElement(elem));
            throw new InvalidNamedObjectException(core.getSignatory().getName() + " is not the issuer for asset: " + asset.getName());
        }
    }

}
