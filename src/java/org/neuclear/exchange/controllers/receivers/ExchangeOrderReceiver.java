package org.neuclear.exchange.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.controllers.receivers.SigningLedgerReceiver;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.exchange.orders.ExchangeOrder;
import org.neuclear.exchange.orders.ExchangeOrderGlobals;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.exchange.orders.builders.ExchangeOrderReceiptBuilder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.*;

/*
$Id: ExchangeOrderReceiver.java,v 1.1 2004/09/10 19:48:02 pelle Exp $
$Log: ExchangeOrderReceiver.java,v $
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
public class ExchangeOrderReceiver extends SigningLedgerReceiver implements HandlingReceiver {
    public ExchangeOrderReceiver(Receiver parent, Signer signer, LedgerController ledger) {
        super(parent, signer, ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            ExchangeOrder order = (ExchangeOrder) obj;
            Asset asset = order.getAsset();
            String name = asset.getServiceId();
            double amount = order.getAmount().getAmount();
            double fee = asset.getFeeStructure().calculateFee(amount);
            final PostedTransaction posted = ledger.hold(name, order.getDigest(), order.getSignatory().getName(), order.getAgent().getServiceId(), order.getExpiry(), asset.round(amount - fee), order.getComment());
            if (fee > 0) {
                String req = "-" + order.getDigest().substring(1);
                PostedTransaction postedFee = ledger.transfer(name, req, order.getSignatory().getName(), asset.getFeeAccount().getName(), fee, "Exchange " + order.getDigest());
                ledger.setReceiptId(req, order.getDigest());
            }
            if (!signer.canSignFor(name))
                return null;
            final ExchangeOrderReceipt receipt = (ExchangeOrderReceipt) new ExchangeOrderReceiptBuilder(order, posted.getTransactionTime()).convert(name, signer);
            ledger.setHeldReceiptId(order.getDigest(), receipt.getDigest());
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
        return ExchangeOrderGlobals.EXCHANGE_TAGNAME;
    }
}
