package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.orders.IssueOrder;
import org.neuclear.asset.orders.IssueReceipt;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownTransactionException;

/*
$Id: IssueReceiptReceiver.java,v 1.3 2004/09/10 19:48:01 pelle Exp $
$Log: IssueReceiptReceiver.java,v $
Revision 1.3  2004/09/10 19:48:01  pelle
Refactored all the Exchange related receivers into a new package under org.neuclear.exchange.
Refactored the way the Receivers handle embedded objects. Now they pass them on to the parent receiver for processing before they do their own thing.

Revision 1.2  2004/07/22 21:48:42  pelle
Further receivers and unit tests for for Exchanges etc.
I've also changed the internal asset to ledger id from being the pk of the contract signer, to being the pk of the service key.

Revision 1.1  2004/07/21 23:11:22  pelle
Added single function Receivers and a DelegatingAssetController. These will eventually replace the CurrencyController and Auditor.

*/

/**
 * User: pelleb
 * Date: Jul 21, 2004
 * Time: 12:45:42 PM
 */
public class IssueReceiptReceiver extends LedgerReceiver implements HandlingReceiver {
    public IssueReceiptReceiver(Receiver parent, LedgerController ledger) {
        super(parent, ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            IssueReceipt receipt = (IssueReceipt) obj;
            String name = receipt.getAsset().getServiceId();
            final IssueOrder order = receipt.getOrder();
            if (!ledger.transactionExists(order.getDigest()))
                parent.receive(order);
            ledger.setReceiptId(order.getDigest(), receipt.getDigest());
        } catch (ClassCastException e) {
            throw new UnsupportedTransaction(obj);
        } catch (UnknownTransactionException e) {
            throw new NeuClearException(e);
        } catch (LowlevelLedgerException e) {
            throw new NeuClearException(e);
        }
        return null;
    }

    public String handlesTagName() {
        return "IssueReceipt";
    }
}
