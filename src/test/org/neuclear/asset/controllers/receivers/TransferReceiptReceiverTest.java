package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.builders.TransferOrderBuilder;
import org.neuclear.asset.orders.builders.TransferReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Signatory;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.ledger.hibernate.HibernateLedgerController;
import org.neuclear.tests.AbstractSigningTest;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Date;

/*
$Id: TransferReceiptReceiverTest.java,v 1.1 2004/08/18 09:42:56 pelle Exp $
$Log: TransferReceiptReceiverTest.java,v $
Revision 1.1  2004/08/18 09:42:56  pelle
Many fixes to the various Signing and SigningRequest Servlets etc.

Revision 1.2  2004/07/22 21:48:46  pelle
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
public class TransferReceiptReceiverTest extends AbstractSigningTest {

    public TransferReceiptReceiverTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
        TransferGlobals.registerReaders();
        AssetGlobals.registerReaders();
    }

    protected void setUp() throws Exception {
        PublicKey pub = signer.generateKey();
        signeralias = new Signatory(pub).getName();
        asset = (Asset) new AssetBuilder("test", "http://localhost:8080/rules.html", "http://localhost:8080/Asset", pub, signer.getPublicKey("carol"), 2, 0, "t").convert("bux", signer);
        ledger = new HibernateLedgerController("test");
        receiver = new TransferReceiptReceiver(ledger);
    }

    public void testTransferReceipt() throws NeuClearException, InvalidTransferException, LowlevelLedgerException, UnknownBookException, InvalidTransactionException {
        Signatory recipient = new Signatory(signer.getPublicKey("alice"));
        Signatory sender = new Signatory(signer.getPublicKey("bob"));
        final double senderstart = ledger.getBalance(asset.getServiceId(), sender.getName());
        ledger.transfer(asset.getServiceId(), "bluesky", sender.getName(), 10 - senderstart, "bla");
        assertEquals(10, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);
        double aliceBalance = ledger.getBalance(asset.getServiceId(), recipient.getName());
        receiver.receive(new TransferReceiptBuilder((TransferOrder) new TransferOrderBuilder(asset, recipient, new Amount(10), "test").convert("bob", signer), new Date()).convert(signeralias, signer));
        assertEquals(aliceBalance + 10, ledger.getBalance(asset.getServiceId(), recipient.getName()), 0);
        assertEquals(0, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);
    }

    protected Receiver receiver;
    protected Asset asset;
    private LedgerController ledger;
    private String signeralias;

}