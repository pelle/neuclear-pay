package org.neuclear.asset.controllers;

import org.neuclear.asset.controllers.receivers.IssueOrderReceiver;
import org.neuclear.asset.controllers.receivers.IssueReceiptReceiver;
import org.neuclear.asset.controllers.receivers.TransferOrderReceiver;
import org.neuclear.asset.controllers.receivers.TransferReceiptReceiver;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.id.receiver.DelegatingReceiver;
import org.neuclear.ledger.LedgerController;

/*
$Id: DelegatingAssetController.java,v 1.1 2004/07/21 23:11:22 pelle Exp $
$Log: DelegatingAssetController.java,v $
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
        register(new TransferOrderReceiver(signer, ledger));
        register(new TransferReceiptReceiver(ledger));
        register(new IssueOrderReceiver(signer, ledger));
        register(new IssueReceiptReceiver(ledger));
    }
}
