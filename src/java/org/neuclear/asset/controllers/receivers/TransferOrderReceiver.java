package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.asset.orders.builders.TransferReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.*;

/*
$Id: TransferOrderReceiver.java,v 1.4 2004/09/10 19:48:01 pelle Exp $
$Log: TransferOrderReceiver.java,v $
Revision 1.4  2004/09/10 19:48:01  pelle
Refactored all the Exchange related receivers into a new package under org.neuclear.exchange.
Refactored the way the Receivers handle embedded objects. Now they pass them on to the parent receiver for processing before they do their own thing.

Revision 1.3  2004/09/08 20:07:43  pelle
Added support for fees to TransferOrderReceiver

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
public class TransferOrderReceiver extends SigningLedgerReceiver implements HandlingReceiver {
    public TransferOrderReceiver(Receiver parent, Signer signer, LedgerController ledger) {
        super(parent, signer, ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            TransferOrder order = (TransferOrder) obj;
            Asset asset = order.getAsset();
            String name = asset.getServiceId();
            double amount = order.getAmount().getAmount();
            UnPostedTransaction transaction = new UnPostedTransaction(name, order.getDigest(), order.getComment());
            double fee = asset.getFeeStructure().calculateFee(amount);
            transaction.addItem(ledger.getBook(order.getSignatory().getName()), -amount);
            if (fee > 0) {
                amount = -transaction.addItem(ledger.getBook(asset.getFeeAccount().getName()), fee);
            }
            transaction.addItem(ledger.getBook(order.getRecipient()), amount);
            if (!signer.canSignFor(name)) {
                ledger.performTransaction(transaction);
                return null;
            } else {
                final PostedTransaction posted = ledger.performVerifiedTransfer(transaction);
                final TransferReceipt receipt = (TransferReceipt) new TransferReceiptBuilder(order, posted.getTransactionTime()).convert(name, signer);
                ledger.setReceiptId(order.getDigest(), receipt.getDigest());
                receipt.log();
                return receipt;
            }

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
        return "TransferOrder";
    }
}
