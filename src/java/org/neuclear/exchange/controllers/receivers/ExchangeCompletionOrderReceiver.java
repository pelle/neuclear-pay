package org.neuclear.exchange.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.controllers.receivers.SigningLedgerReceiver;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.exchange.orders.ExchangeCompletedReceipt;
import org.neuclear.exchange.orders.ExchangeCompletionOrder;
import org.neuclear.exchange.orders.ExchangeOrderGlobals;
import org.neuclear.exchange.orders.builders.ExchangeCompletedReceiptBuilder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.*;

/*
$Id: ExchangeCompletionOrderReceiver.java,v 1.1 2004/09/10 19:48:02 pelle Exp $
$Log: ExchangeCompletionOrderReceiver.java,v $
Revision 1.1  2004/09/10 19:48:02  pelle
Refactored all the Exchange related receivers into a new package under org.neuclear.exchange.
Refactored the way the Receivers handle embedded objects. Now they pass them on to the parent receiver for processing before they do their own thing.

Revision 1.3  2004/09/08 23:17:23  pelle
Fees now work for everything but Exchange Completion.

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
public class ExchangeCompletionOrderReceiver extends SigningLedgerReceiver implements HandlingReceiver {
    public ExchangeCompletionOrderReceiver(Receiver parent, Signer signer, LedgerController ledger) {
        super(parent, signer, ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            ExchangeCompletionOrder complete = (ExchangeCompletionOrder) obj;
            final Asset asset = complete.getAsset();
            String name = asset.getServiceId();
            if (!complete.getSignatory().getName().equals(complete.getReceipt().getOrder().getAgent().getServiceId()))
                throw new InvalidTransferException("Only Agent is allowed to Sign Completion Order");
            final double amount = complete.getAmount().getAmount();
            if (amount > complete.getReceipt().getOrder().getAmount().getAmount())
                throw new InvalidTransferException("Attempting to complete larger than authorized amount");
            if (ledger.findHeldTransaction(complete.getReceipt().getOrder().getDigest()) == null)
                parent.receive(complete.getReceipt());
            PostedTransaction tran = ledger.complete(complete.getReceipt().getOrder().getDigest(), complete.getSignatory().getName(), complete.getCounterparty(), asset.round(amount - asset.getFeeStructure().calculateFee(amount)), complete.getComment());
            if (!signer.canSignFor(name))
                return null;
            ExchangeCompletedReceipt receipt = (ExchangeCompletedReceipt) new ExchangeCompletedReceiptBuilder(complete, tran.getTransactionTime()).convert(name, signer);
            ledger.setReceiptId(complete.getReceipt().getOrder().getDigest(), receipt.getDigest());
            return receipt;

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
    }

    public String handlesTagName() {
        return ExchangeOrderGlobals.COMPLETE_TAGNAME;
    }
}
