package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.*;

/*
$Id: TransferReceiptReceiver.java,v 1.1 2004/07/21 23:11:22 pelle Exp $
$Log: TransferReceiptReceiver.java,v $
Revision 1.1  2004/07/21 23:11:22  pelle
Added single function Receivers and a DelegatingAssetController. These will eventually replace the CurrencyController and Auditor.

*/

/**
 * User: pelleb
 * Date: Jul 21, 2004
 * Time: 12:45:42 PM
 */
public class TransferReceiptReceiver extends LedgerReceiver implements HandlingReceiver {
    public TransferReceiptReceiver(LedgerController ledger) {
        super(ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            TransferReceipt receipt = (TransferReceipt) obj;
            String name = receipt.getAsset().getSignatory().getName();
            final TransferOrder order = receipt.getOrder();
            if (!order.getSignatory().getName().equals(order.getAsset().getIssuer().getName()))
                throw new InvalidTransferException("Only Issuer is allowed to issue");

            if (!ledger.transactionExists(order.getDigest()))
                ledger.transfer(order.getAsset().getSignatory().getName(), order.getDigest(), order.getSignatory().getName(), order.getRecipient(), order.getAmount().getAmount(), order.getComment());
            ledger.setReceiptId(order.getDigest(), receipt.getDigest());
        } catch (ClassCastException e) {
            throw new UnsupportedTransaction(obj);
        } catch (UnknownTransactionException e) {
            throw new NeuClearException(e);
        } catch (UnknownBookException e) {
            throw new NeuClearException(e);
        } catch (InsufficientFundsException e) {
            throw new NeuClearException(e);
        } catch (UnBalancedTransactionException e) {
            throw new NeuClearException(e);
        } catch (LowlevelLedgerException e) {
            throw new NeuClearException(e);
        } catch (InvalidTransactionException e) {
            throw new NeuClearException(e);
        } catch (InvalidTransferException e) {
            throw new NeuClearException(e);
        }
        return null;
    }

    public String handlesTagName() {
        return "TransferReceipt";
    }
}
