package org.neuclear.asset.controllers.receivers;

import org.neuclear.id.receiver.Receiver;
import org.neuclear.ledger.LedgerController;

/*
$Id: LedgerReceiver.java,v 1.2 2004/09/10 19:48:01 pelle Exp $
$Log: LedgerReceiver.java,v $
Revision 1.2  2004/09/10 19:48:01  pelle
Refactored all the Exchange related receivers into a new package under org.neuclear.exchange.
Refactored the way the Receivers handle embedded objects. Now they pass them on to the parent receiver for processing before they do their own thing.

Revision 1.1  2004/07/21 23:11:22  pelle
Added single function Receivers and a DelegatingAssetController. These will eventually replace the CurrencyController and Auditor.

*/

/**
 * User: pelleb
 * Date: Jul 21, 2004
 * Time: 12:41:48 PM
 */
public abstract class LedgerReceiver implements Receiver {

    protected LedgerReceiver(Receiver parent, LedgerController ledger) {
        this.ledger = ledger;
        this.parent = parent;
    }

    protected final LedgerController ledger;
    protected final Receiver parent;
}
