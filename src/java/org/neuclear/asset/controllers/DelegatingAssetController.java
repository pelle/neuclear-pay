package org.neuclear.asset.controllers;

import org.neuclear.asset.controllers.receivers.IssueOrderReceiver;
import org.neuclear.asset.controllers.receivers.IssueReceiptReceiver;
import org.neuclear.asset.controllers.receivers.TransferOrderReceiver;
import org.neuclear.asset.controllers.receivers.TransferReceiptReceiver;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.id.receiver.DelegatingReceiver;
import org.neuclear.ledger.LedgerController;

/*
$Id: DelegatingAssetController.java,v 1.2 2004/09/10 19:48:01 pelle Exp $
$Log: DelegatingAssetController.java,v $
Revision 1.2  2004/09/10 19:48:01  pelle
Refactored all the Exchange related receivers into a new package under org.neuclear.exchange.
Refactored the way the Receivers handle embedded objects. Now they pass them on to the parent receiver for processing before they do their own thing.

Revision 1.1  2004/07/21 23:11:22  pelle
Added single function Receivers and a DelegatingAssetController. These will eventually replace the CurrencyController and Auditor.

*/

/**
 * User: pelleb
 * Date: Jul 21, 2004
 * Time: 1:23:10 PM
 */
public class DelegatingAssetController extends DelegatingReceiver {
    public DelegatingAssetController(final Signer signer, final LedgerController ledger) {
        register(new TransferOrderReceiver(this, signer, ledger));
        register(new TransferReceiptReceiver(this, ledger));
        register(new IssueOrderReceiver(this, signer, ledger));
        register(new IssueReceiptReceiver(this, ledger));
    }
}
