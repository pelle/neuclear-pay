package org.neuclear.exchange.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.Amount;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.contracts.ExchangeAgentGlobals;
import org.neuclear.exchange.orders.BidItem;
import org.neuclear.exchange.orders.ExchangeOrderGlobals;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.exchange.orders.builders.ExchangeOrderBuilder;
import org.neuclear.id.Signatory;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.ledger.simple.SimpleLedgerController;

import java.security.GeneralSecurityException;
import java.util.Date;

/*
$Id: ExchangeOrderReceiptReceiverTest.java,v 1.1 2004/09/10 19:48:02 pelle Exp $
$Log: ExchangeOrderReceiptReceiverTest.java,v $
Revision 1.1  2004/09/10 19:48:02  pelle
Refactored all the Exchange related receivers into a new package under org.neuclear.exchange.
Refactored the way the Receivers handle embedded objects. Now they pass them on to the parent receiver for processing before they do their own thing.

Revision 1.1  2004/08/18 09:42:56  pelle
Many fixes to the various Signing and SigningRequest Servlets etc.

Revision 1.1  2004/07/22 21:48:46  pelle
Further receivers and unit tests for for Exchanges etc.
I've also changed the internal asset to ledger id from being the pk of the contract signer, to being the pk of the service key.

Revision 1.1  2004/07/21 23:11:22  pelle
Added single function Receivers and a DelegatingAssetController. These will eventually replace the CurrencyController and Auditor.

*/

/**
 * User: pelleb
 * Date: Jul 21, 2004
 * Time: 1:38:45 PM
 */
public class ExchangeOrderReceiptReceiverTest extends AbstractExchangeReceiverTest {

    public ExchangeOrderReceiptReceiverTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
        ExchangeOrderGlobals.registerReaders();
        ExchangeAgentGlobals.registerReaders();
        AssetGlobals.registerReaders();
    }

    protected void setUp() throws Exception {
        generatePrimaryTestAsset();
        ledger = new SimpleLedgerController("test");
        receiver = createReceiver();
        agent = createTestExchangeAgent();
        shoes = createShoeAsset();
    }


    public void testExchangeOrderReceipt() throws NeuClearException, InvalidTransferException, LowlevelLedgerException, UnknownBookException, InvalidTransactionException {
        Signatory recipient = new Signatory(signer.getPublicKey("alice"));
        Signatory sender = new Signatory(signer.getPublicKey("bob"));
        final double senderstart = ledger.getBalance(asset.getServiceId(), sender.getName());
        ledger.transfer(asset.getServiceId(), "bluesky", sender.getName(), 10 - senderstart, "bla");
        assertEquals(10, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);

        ExchangeOrderReceipt receipt = (ExchangeOrderReceipt) receiver.receive(new ExchangeOrderBuilder(asset, agent, new Amount(10), new Date(System.currentTimeMillis() + 10000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "test").convert("bob", signer));
        receiver.receive(receipt);
        assertEquals(10, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);
        assertEquals(0, ledger.getAvailableBalance(asset.getServiceId(), sender.getName()), 0);
    }

    public void testExchangeOrderReceiptAudit() throws NeuClearException, InvalidTransferException, LowlevelLedgerException, UnknownBookException, InvalidTransactionException {
        LedgerController ac = new SimpleLedgerController("asset");
        Signatory recipient = new Signatory(signer.getPublicKey("alice"));
        Signatory sender = new Signatory(signer.getPublicKey("bob"));
        final double senderstart = ledger.getBalance(asset.getServiceId(), sender.getName());
        ledger.transfer(asset.getServiceId(), "bluesky", sender.getName(), 10 - senderstart, "bla");
        assertEquals(10, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);
        ac.transfer(asset.getServiceId(), "bluesky", sender.getName(), 10 - senderstart, "bla");
        assertEquals(10, ac.getBalance(asset.getServiceId(), sender.getName()), 0);

        ExchangeOrderReceipt receipt = (ExchangeOrderReceipt) new ExchangeOrderReceiver(receiver, signer, ac).receive(new ExchangeOrderBuilder(asset, agent, new Amount(10), new Date(System.currentTimeMillis() + 10000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "test").convert("bob", signer));
        receiver.receive(receipt);
        assertEquals(10, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);
        assertEquals(0, ledger.getAvailableBalance(asset.getServiceId(), sender.getName()), 0);
    }

}
