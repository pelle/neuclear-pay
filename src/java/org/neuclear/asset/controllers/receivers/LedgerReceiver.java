package org.neuclear.asset.controllers.receivers;

import org.neuclear.id.receiver.Receiver;
import org.neuclear.ledger.LedgerController;

/*
$Id: LedgerReceiver.java,v 1.1 2004/07/21 23:11:22 pelle Exp $
$Log: LedgerReceiver.java,v $
Revision 1.1  2004/07/21 23:11:22  pelle
Added single function Receivers and a DelegatingAssetController. These will eventually replace the CurrencyController and Auditor.

*/

/**
 * User: pelleb
 * Date: Jul 21, 2004
 * Time: 12:41:48 PM
 */
public abstract class LedgerReceiver implements Receiver {
    protected LedgerReceiver(LedgerController ledger) {
        this.ledger = ledger;
    }

    protected final LedgerController ledger;
}
