package org.neuclear.asset.orders;

import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.id.*;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.asset.orders.TransferContract;
import org.neuclear.asset.orders.exchanges.*;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.dom4j.Element;

import java.sql.Timestamp;
import java.util.Date;
import java.text.ParseException;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public class TransferOrder extends AssetTransactionContract {

    private TransferOrder(final SignedNamedCore core, final Asset asset, final Identity to, final double amount, final String comment)  {
        super(core, asset);
        this.amount = amount;
        this.comment = (comment != null) ? comment : "";
        this.to=to;
    }

    public final Identity getFrom() {
        return getSignatory();
    }

    public final Identity getTo() {
        return to;
    }
    public final double getAmount() {
            return amount;
        }

    public final String getComment() {
        return comment;
    }

    private final Identity to;

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

            try {
                //TODO Validate properly
                final Asset asset = (Asset) NSResolver.resolveIdentity(elem.attributeValue("assetName"));


                final double amount = Double.parseDouble(elem.attributeValue("amount"));
                final Identity to = NSResolver.resolveIdentity(elem.attributeValue("recipient"));
                final Element commentElement = elem.element(TransferGlobals.createQName("comment"));

                final String comment = (commentElement != null) ? commentElement.getText() : "";
                return new TransferOrder(core, asset, to, amount,  comment);
            } catch (NameResolutionException e) {
                throw new InvalidNamedObjectException(core.getName(),e);
            }
        }
    }

}
