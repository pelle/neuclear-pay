package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.exchange.orders.ExchangeCompletedReceipt;
import org.neuclear.exchange.orders.ExchangeCompletionOrder;
import org.neuclear.exchange.orders.ExchangeOrderGlobals;
import org.neuclear.exchange.orders.builders.ExchangeCompletedReceiptBuilder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.*;

/*
$Id: ExchangeCompletionOrderReceiver.java,v 1.1 2004/07/22 21:48:42 pelle Exp $
$Log: ExchangeCompletionOrderReceiver.java,v $
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
    public ExchangeCompletionOrderReceiver(Signer signer, LedgerController ledger) {
        super(signer, ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            ExchangeCompletionOrder complete = (ExchangeCompletionOrder) obj;
            String name = complete.getAsset().getServiceId();
            if (!complete.getSignatory().getName().equals(complete.getReceipt().getOrder().getAgent().getServiceId()))
                throw new InvalidTransferException("Only Agent is allowed to Sign Completion Order");
            if (complete.getAmount().getAmount() > complete.getReceipt().getOrder().getAmount().getAmount())
                throw new InvalidTransferException("Attempting to complete larger than authorized amount");
            PostedTransaction tran = ledger.complete(complete.getReceipt().getOrder().getDigest(), complete.getAmount().getAmount(), complete.getComment());
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
        }
    }

    public String handlesTagName() {
        return ExchangeOrderGlobals.COMPLETE_TAGNAME;
    }
}
