package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.builders.IssueOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Signatory;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.ledger.simple.SimpleLedgerController;
import org.neuclear.tests.AbstractSigningTest;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

/*
$Id: IssueOrderReceiverTest.java,v 1.1 2004/07/22 00:19:03 pelle Exp $
$Log: IssueOrderReceiverTest.java,v $
Revision 1.1  2004/07/22 00:19:03  pelle
Added unit test for IssueOrder

Revision 1.1  2004/07/21 23:11:22  pelle
Added single function Receivers and a DelegatingAssetController. These will eventually replace the CurrencyController and Auditor.

*/

/**
 * User: pelleb
 * Date: Jul 21, 2004
 * Time: 1:38:45 PM
 */
public class IssueOrderReceiverTest extends AbstractSigningTest {

    public IssueOrderReceiverTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
        TransferGlobals.registerReaders();
        AssetGlobals.registerReaders();
    }

    protected void setUp() throws Exception {
        PublicKey pub = signer.generateKey();
        asset = (Asset) new AssetBuilder("test", "http://localhost:8080/rules.html", "http://localhost:8080/Asset", pub, signer.getPublicKey("carol"), 2, 0, "t").convert("bux", signer);
        ledger = new SimpleLedgerController("test");
        receiver = new IssueOrderReceiver(signer, ledger);
    }

    public void testIssueOrder() throws NeuClearException, InvalidTransferException, LowlevelLedgerException, UnknownBookException, InvalidTransactionException {
        Signatory recipient = new Signatory(signer.getPublicKey("alice"));
        Signatory sender = new Signatory(signer.getPublicKey("carol"));
        final double senderstart = ledger.getBalance(asset.getSignatory().getName(), sender.getName());
        double aliceBalance = ledger.getBalance(asset.getSignatory().getName(), recipient.getName());
        receiver.receive(new IssueOrderBuilder(asset, recipient, new Amount(10), "test").convert("carol", signer));
        assertEquals(aliceBalance + 10, ledger.getBalance(asset.getSignatory().getName(), recipient.getName()), 0);
        assertEquals(senderstart - 10, ledger.getBalance(asset.getSignatory().getName(), sender.getName()), 0);
    }

    protected Receiver receiver;
    protected Asset asset;
    private LedgerController ledger;

}
