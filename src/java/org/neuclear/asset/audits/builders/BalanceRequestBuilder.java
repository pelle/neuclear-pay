package org.neuclear.asset.audits.builders;

import org.dom4j.Element;
import org.neuclear.asset.audits.BalanceRequest;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.id.builders.Builder;

/*
$Id: BalanceRequestBuilder.java,v 1.1 2004/07/21 16:00:50 pelle Exp $
$Log: BalanceRequestBuilder.java,v $
Revision 1.1  2004/07/21 16:00:50  pelle
Added Balance and History related classes.

*/

/**
 * User: pelleb
 * Date: Jul 9, 2004
 * Time: 4:07:22 PM
 */
public class BalanceRequestBuilder extends Builder {
    public BalanceRequestBuilder(Asset asset) {
        super(TransferGlobals.createQName(BalanceRequest.BALANCE_REQUEST_TAGNAME));
        final Element assetElem = TransferGlobals.createElement(TransferGlobals.ASSET_TAG, asset.getURL());
        assetElem.addAttribute(TransferGlobals.createQName("digest"), asset.getDigest());
        addElement(assetElem);

    }
}
