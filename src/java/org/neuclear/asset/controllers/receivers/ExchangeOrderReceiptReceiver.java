package org.neuclear.asset.controllers.receivers;

import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.orders.ExchangeOrder;
import org.neuclear.exchange.orders.ExchangeOrderGlobals;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.HandlingReceiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.*;

/*
$Id: ExchangeOrderReceiptReceiver.java,v 1.1 2004/08/18 09:42:55 pelle Exp $
$Log: ExchangeOrderReceiptReceiver.java,v $
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
public class ExchangeOrderReceiptReceiver extends LedgerReceiver implements HandlingReceiver {
    public ExchangeOrderReceiptReceiver(LedgerController ledger) {
        super(ledger);
    }

    public SignedNamedObject receive(SignedNamedObject obj) throws UnsupportedTransaction, NeuClearException {
        try {
            ExchangeOrderReceipt receipt = (ExchangeOrderReceipt) obj;
            ExchangeOrder order = receipt.getOrder();
            String name = order.getAsset().getServiceId();
            if (!ledger.heldTransactionExists(order.getDigest()))
                ledger.hold(name, order.getDigest(), order.getSignatory().getName(), order.getAgent().getServiceId(), order.getExpiry(), order.getAmount().getAmount(), order.getComment());
            ledger.setHeldReceiptId(order.getDigest(), receipt.getDigest());
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
        return ExchangeOrderGlobals.EXCHANGE_RCPT_TAGNAME;
    }
}
