package org.neuclear.asset.controllers.receivers;

import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.id.receiver.SigningReceiver;
import org.neuclear.ledger.LedgerController;

/*
$Id: SigningLedgerReceiver.java,v 1.1 2004/07/21 23:11:22 pelle Exp $
$Log: SigningLedgerReceiver.java,v $
Revision 1.1  2004/07/21 23:11:22  pelle
Added single function Receivers and a DelegatingAssetController. These will eventually replace the CurrencyController and Auditor.

*/

/**
 * User: pelleb
 * Date: Jul 21, 2004
 * Time: 12:41:48 PM
 */
public abstract class SigningLedgerReceiver extends SigningReceiver {
    protected SigningLedgerReceiver(Signer signer, LedgerController ledger) {
        super(signer);
        this.ledger = ledger;
    }

    protected final LedgerController ledger;
}
