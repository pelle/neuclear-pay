package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.asset.orders.builders.TransferReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.*;

/*
$Id: TransferOrderReceiver.java,v 1.2 2004/07/22 21:48:42 pelle Exp $
$Log: TransferOrderReceiver.java,v $
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
    public TransferOrderReceiver(Signer signer, LedgerController ledger) {
        super(signer, ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            TransferOrder order = (TransferOrder) obj;
            String name = order.getAsset().getServiceId();
            if (!signer.canSignFor(name)) {
                ledger.transfer(name, order.getDigest(), order.getSignatory().getName(), order.getRecipient(), order.getAmount().getAmount(), order.getComment());
                return null;
            } else {
                final PostedTransaction posted = ledger.verifiedTransfer(name, order.getDigest(), order.getSignatory().getName(), order.getRecipient(), order.getAmount().getAmount(), order.getComment());
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
