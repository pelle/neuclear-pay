package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.orders.IssueOrder;
import org.neuclear.asset.orders.IssueReceipt;
import org.neuclear.asset.orders.builders.IssueReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.*;

/*
$Id: IssueOrderReceiver.java,v 1.3 2004/09/10 19:48:01 pelle Exp $
$Log: IssueOrderReceiver.java,v $
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
public class IssueOrderReceiver extends SigningLedgerReceiver implements HandlingReceiver {
    public IssueOrderReceiver(Receiver parent, Signer signer, LedgerController ledger) {
        super(parent, signer, ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            IssueOrder order = (IssueOrder) obj;
            if (!order.getSignatory().getName().equals(order.getAsset().getIssuer().getName()))
                throw new InvalidTransferException("Only Issuer is allowed to issue");

            String name = order.getAsset().getServiceId();
            ;
            final PostedTransaction posted = ledger.transfer(name, order.getDigest(), order.getSignatory().getName(), order.getRecipient(), order.getAmount().getAmount(), order.getComment());
            if (!signer.canSignFor(name))
                return null;
            final IssueReceipt receipt = (IssueReceipt) new IssueReceiptBuilder(order, posted.getTransactionTime()).convert(name, signer);
            ledger.setReceiptId(order.getDigest(), receipt.getDigest());
            return receipt;

        } catch (ClassCastException e) {
            throw new UnsupportedTransaction(obj);
        } catch (UnknownTransactionException e) {
            throw new NeuClearException(e);
        } catch (UnknownBookException e) {
            throw new NeuClearException(e);
        } catch (InsufficientFundsException e) {
            throw new NeuClearException(e);
        } catch (NegativeTransferException e) {
            throw new NeuClearException(e);
        } catch (InvalidTransferException e) {
            throw new NeuClearException(e);
        } catch (UnBalancedTransactionException e) {
            throw new NeuClearException(e);
        } catch (LowlevelLedgerException e) {
            throw new NeuClearException(e);
        } catch (InvalidTransactionException e) {
            throw new NeuClearException(e);
        }
    }

    public String handlesTagName() {
        return "IssueOrder";
    }
}
