package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.orders.ExchangeCompletedReceipt;
import org.neuclear.exchange.orders.ExchangeCompletionOrder;
import org.neuclear.exchange.orders.ExchangeOrder;
import org.neuclear.exchange.orders.ExchangeOrderGlobals;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.*;

/*
$Id: ExchangeCompletedReceiptReceiver.java,v 1.1 2004/08/18 09:42:55 pelle Exp $
$Log: ExchangeCompletedReceiptReceiver.java,v $
Revision 1.1  2004/08/18 09:42:55  pelle
Many fixes to the various Signing and SigningRequest Servlets etc.

Revision 1.2  2004/07/23 18:58:39  pelle
Updated to use the new complete method in ledger.

Revision 1.1  2004/07/22 21:48:42  pelle
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
public class ExchangeCompletedReceiptReceiver extends LedgerReceiver implements HandlingReceiver {
    public ExchangeCompletedReceiptReceiver(LedgerController ledger) {
        super(ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            ExchangeCompletedReceipt receipt = (ExchangeCompletedReceipt) obj;
            ExchangeCompletionOrder complete = receipt.getOrder();
            String name = complete.getAsset().getServiceId();

            if (!complete.getSignatory().getName().equals(complete.getReceipt().getOrder().getAgent().getServiceId()))
                throw new InvalidTransferException("Only Agent is allowed to Sign Completion Order");
            if (complete.getAmount().getAmount() > complete.getReceipt().getOrder().getAmount().getAmount())
                throw new InvalidTransferException("Attempting to complete larger than authorized amount");

            if (!ledger.transactionExists(complete.getDigest())) {
                if (!ledger.heldTransactionExists(complete.getReceipt().getOrder().getDigest())) {
                    ExchangeOrder order = complete.getReceipt().getOrder();
                    ledger.hold(name, order.getDigest(), order.getSignatory().getName(), order.getAgent().getServiceId(), order.getExpiry(), order.getAmount().getAmount(), order.getComment());
                    ledger.setHeldReceiptId(order.getDigest(), complete.getReceipt().getDigest());
                }
                ledger.complete(complete.getReceipt().getOrder().getDigest(), complete.getSignatory().getName(), complete.getCounterparty(), complete.getAmount().getAmount(), complete.getComment());
            }
            ledger.setReceiptId(complete.getReceipt().getOrder().getDigest(), receipt.getDigest());


        } catch (ClassCastException e) {
            throw new UnsupportedTransaction(obj);
        } catch (UnknownTransactionException e) {
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
        } catch (TransactionExpiredException e) {
            throw new NeuClearException(e);
        } catch (UnknownBookException e) {
            throw new NeuClearException(e);
        }
        return null;
    }

    public String handlesTagName() {
        return ExchangeOrderGlobals.COMPLETE_RCPT_TAGNAME;
    }
}
