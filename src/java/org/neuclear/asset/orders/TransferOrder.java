package org.neuclear.asset.orders;

import org.dom4j.Element;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.id.*;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public class TransferOrder extends AssetTransactionContract {

    private TransferOrder(final SignedNamedCore core, final Asset asset, final Identity recipient, final double amount, final String comment)  {
        super(core, asset);
        this.amount = amount;
        this.comment = comment;
        this.recipient=recipient;
    }

    public final Identity getRecipient() {
        return recipient;
    }
    public final double getAmount() {
            return amount;
        }

    public final String getComment() {
        return comment;
    }

    private final Identity recipient;

    private final double amount;
    private final String comment;

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
            if (elem.getName().equals(TransferGlobals.XFER_TAGNAME))
                throw new InvalidNamedObjectException(core.getName(),"Incorrect XML Tagname for reader: "+TransferGlobals.XFER_TAGNAME);

            return new TransferOrder(core,
                    TransferGlobals.parseAssetTag(elem),
                    TransferGlobals.parseRecipientTag(elem),
                    TransferGlobals.parseAmountTag(elem),
                    TransferGlobals.getCommentElement(elem)
                    );
        }
    }

}
