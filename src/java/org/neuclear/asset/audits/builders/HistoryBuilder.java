package org.neuclear.asset.audits.builders;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.neuclear.asset.audits.History;
import org.neuclear.asset.audits.HistoryRequest;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.builders.ReceiptBuilder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.browser.BookBrowser;

import java.util.Date;

/*
$Id: HistoryBuilder.java,v 1.1 2004/07/21 16:00:50 pelle Exp $
$Log: HistoryBuilder.java,v $
Revision 1.1  2004/07/21 16:00:50  pelle
Added Balance and History related classes.

*/

/**
 * User: pelleb
 * Date: Jul 9, 2004
 * Time: 4:07:22 PM
 */
public class HistoryBuilder extends ReceiptBuilder {
    public HistoryBuilder(HistoryRequest req, BookBrowser browser, Date time) {
        super(TransferGlobals.createQName(History.HISTORY_TAGNAME), req, time);
        try {
            while (browser.next()) {
                SignedNamedObject obj = Resolver.resolveFromCache(browser.getRequestId());
                getElement().add(DocumentHelper.parseText(obj.getEncoded()).getRootElement());
            }
        } catch (LowlevelLedgerException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
