package org.neuclear.asset.audits.builders;

import org.dom4j.Element;
import org.neuclear.asset.audits.HistoryRequest;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.commons.Utility;
import org.neuclear.id.builders.Builder;

/*
$Id: HistoryRequestBuilder.java,v 1.1 2004/07/21 16:00:50 pelle Exp $
$Log: HistoryRequestBuilder.java,v $
Revision 1.1  2004/07/21 16:00:50  pelle
Added Balance and History related classes.

*/

/**
 * User: pelleb
 * Date: Jul 9, 2004
 * Time: 4:07:22 PM
 */
public class HistoryRequestBuilder extends Builder {
    public HistoryRequestBuilder(Asset asset, String lastid) {
        super(TransferGlobals.createQName(HistoryRequest.HISTORY_REQUEST_TAGNAME));
        final Element assetElem = TransferGlobals.createElement(TransferGlobals.ASSET_TAG, asset.getURL());
        assetElem.addAttribute(TransferGlobals.createQName("digest"), asset.getDigest());
        addElement(assetElem);
        if (!Utility.isEmpty(lastid))
            getElement().addAttribute(HistoryRequest.LASTKNOWNID_ATTR, lastid);
    }
}
