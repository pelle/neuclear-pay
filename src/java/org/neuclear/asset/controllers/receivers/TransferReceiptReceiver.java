package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.*;

/*
$Id: TransferReceiptReceiver.java,v 1.4 2004/09/08 23:17:23 pelle Exp $
$Log: TransferReceiptReceiver.java,v $
Revision 1.4  2004/09/08 23:17:23  pelle
Fees now work for everything but Exchange Completion.

Revision 1.3  2004/08/18 09:42:55  pelle
Many fixes to the various Signing and SigningRequest Servlets etc.

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
public class TransferReceiptReceiver extends LedgerReceiver implements HandlingReceiver {
    public TransferReceiptReceiver(LedgerController ledger) {
        super(ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            final TransferReceipt receipt = (TransferReceipt) obj;
            final Asset asset = receipt.getAsset();
            final String name = asset.getServiceId();
            final TransferOrder order = receipt.getOrder();

            if (!ledger.transactionExists(order.getDigest())) {
                double amount = order.getAmount().getAmount();
                UnPostedTransaction transaction = new UnPostedTransaction(name, order.getDigest(), order.getComment());
                double fee = asset.getFeeStructure().calculateFee(amount);
                transaction.addItem(ledger.getBook(order.getSignatory().getName()), -amount);
                if (fee > 0) {
                    amount = -transaction.addItem(ledger.getBook(asset.getFeeAccount().getName()), fee);
                }
                transaction.addItem(ledger.getBook(order.getRecipient()), amount);

                ledger.performTransaction(transaction);
            }
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
        }
        return null;
    }

    public String handlesTagName() {
        return TransferGlobals.XFER_RCPT_TAGNAME;
    }
}
