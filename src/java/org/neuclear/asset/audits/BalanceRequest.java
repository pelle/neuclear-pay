package org.neuclear.asset.audits;

import org.dom4j.Element;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;

/*
$Id: BalanceRequest.java,v 1.1 2004/07/21 16:00:51 pelle Exp $
$Log: BalanceRequest.java,v $
Revision 1.1  2004/07/21 16:00:51  pelle
Added Balance and History related classes.

*/

/**
 * User: pelleb
 * Date: Jul 9, 2004
 * Time: 2:37:52 PM
 */
public class BalanceRequest extends SignedNamedObject {
    private BalanceRequest(SignedNamedCore core, Asset asset) {
        super(core);
        this.asset = asset;
    }

    public Asset getAsset() {
        return asset;
    }

    private final Asset asset;
    public static final String BALANCE_REQUEST_TAGNAME = "BalanceRequest";

    public static final class Reader implements NamedObjectReader {

        public SignedNamedObject read(SignedNamedCore core, Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().getURI().equals(TransferGlobals.XFER_NSURI))
                throw new InvalidNamedObjectException(core.getName(), "Not in XML NameSpace: " + AssetGlobals.NS_ASSET.getURI());
            if (!elem.getName().equals(BALANCE_REQUEST_TAGNAME))
                throw new InvalidNamedObjectException(core.getName(), "Incorrect XML Tagname for reader: " + BALANCE_REQUEST_TAGNAME);

            return new BalanceRequest(core, TransferGlobals.parseAssetTag(elem));
        }
    }
}
