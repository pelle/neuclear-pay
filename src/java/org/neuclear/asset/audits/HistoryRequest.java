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
$Id: HistoryRequest.java,v 1.1 2004/07/21 16:00:51 pelle Exp $
$Log: HistoryRequest.java,v $
Revision 1.1  2004/07/21 16:00:51  pelle
Added Balance and History related classes.

*/

/**
 * User: pelleb
 * Date: Jul 9, 2004
 * Time: 2:37:52 PM
 */
public class HistoryRequest extends SignedNamedObject {
    private HistoryRequest(SignedNamedCore core, Asset asset, String lastid) {
        super(core);
        this.asset = asset;
        this.lastid = lastid;
    }

    public String getLastId() {
        return lastid;
    }

    public Asset getAsset() {
        return asset;
    }

    private final String lastid;
    private final Asset asset;
    public final static String HISTORY_REQUEST_TAGNAME = "HistoryRequest";
    public static final String LASTKNOWNID_ATTR = "lastknownid";

    public static final class Reader implements NamedObjectReader {

        public SignedNamedObject read(SignedNamedCore core, Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().getURI().equals(TransferGlobals.XFER_NSURI))
                throw new InvalidNamedObjectException(core.getName(), "Not in XML NameSpace: " + AssetGlobals.NS_ASSET.getURI());
            if (!elem.getName().equals(HISTORY_REQUEST_TAGNAME))
                throw new InvalidNamedObjectException(core.getName(), "Incorrect XML Tagname for reader: " + HISTORY_REQUEST_TAGNAME);

            return new HistoryRequest(core, TransferGlobals.parseAssetTag(elem), elem.attributeValue(LASTKNOWNID_ATTR));
        }
    }

}
