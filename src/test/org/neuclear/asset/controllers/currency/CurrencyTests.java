package org.neuclear.asset.controllers.currency;

import org.neuclear.asset.Auditor;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.asset.orders.builders.TransferOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.exchange.contracts.builders.ExchangeAgentBuilder;
import org.neuclear.exchange.orders.BidItem;
import org.neuclear.exchange.orders.CancelExchangeReceipt;
import org.neuclear.exchange.orders.ExchangeCompletedReceipt;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.exchange.orders.builders.CancelExchangeOrderBuilder;
import org.neuclear.exchange.orders.builders.ExchangeCompletionOrderBuilder;
import org.neuclear.exchange.orders.builders.ExchangeOrderBuilder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.Ledger;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.simple.SimpleLedger;
import org.neuclear.tests.AbstractSigningTest;

import java.security.GeneralSecurityException;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 6:05:04 PM
 */
public final class CurrencyTests extends AbstractSigningTest {
    public CurrencyTests(final String s) throws LowlevelLedgerException, GeneralSecurityException, NeuClearException {
        super(s);
        asset = createTestAsset();
        shoes = createShoeAsset();
        agent = createTestExchangeAgent();
        ledger = new SimpleLedger("test");
        proc = new CurrencyController(ledger, asset, getSigner(), "neu://test/bux");
        auditLedger = new SimpleLedger("auditor");
        auditor = new Auditor(asset, auditLedger);
    }

    public void testTransfer() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        fundAccount();
        double alicebalance = ledger.getBalance(getAlice().getName());
        assertAudit(getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(getAlice().getName()), 0);
        Builder builder = new TransferOrderBuilder(asset, getBob(), new Amount(25), "test");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof TransferReceipt);
        assertEquals(alicebalance - 25, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertEquals(alicebalance - 25, ledger.getBalance(getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    private void fundAccount() throws InvalidTransactionException, LowlevelLedgerException {
        ledger.transfer("test", getAlice().getName(), 50, "fund");
        auditLedger.transfer("test", getAlice().getName(), 50, "fund");
    }

    public void assertAudit(String name) throws LowlevelLedgerException {
        assertEquals(auditLedger.getBalance(name), ledger.getBalance(name), 0);
        assertEquals(auditLedger.getAvailableBalance(name), ledger.getAvailableBalance(name), 0);
    }

    public void testExchangeOrderAndExpire() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        fundAccount();
        double alicebalance = ledger.getBalance(getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        Builder builder = new ExchangeOrderBuilder(asset, agent, new Amount(20), new Date(System.currentTimeMillis() + 5000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "give me shoes");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof ExchangeOrderReceipt);
        assertEquals(alicebalance, ledger.getBalance(getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertAudit(getAlice().getName());
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            ;
        }
        assertEquals(alicebalance, ledger.getBalance(getAlice().getName()), 0);
        assertEquals(alicebalance, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    public void testExchangeOrderAndCancel() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        fundAccount();
        double alicebalance = ledger.getBalance(getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        Builder builder = new ExchangeOrderBuilder(asset, agent, new Amount(20), new Date(System.currentTimeMillis() + 5000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "give me shoes");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof ExchangeOrderReceipt);
        assertEquals(alicebalance, ledger.getBalance(getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertAudit(getAlice().getName());
        SignedNamedObject cr = process(new CancelExchangeOrderBuilder((ExchangeOrderReceipt) receipt));
        assertNotNull(cr);
        assertTrue(cr instanceof CancelExchangeReceipt);
        assertEquals(alicebalance, ledger.getBalance(getAlice().getName()), 0);
        assertEquals(alicebalance, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    public void testExchangeOrderAndComplete() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        fundAccount();
        double alicebalance = ledger.getBalance(getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        Builder builder = new ExchangeOrderBuilder(asset, agent, new Amount(20), new Date(System.currentTimeMillis() + 20000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "give me shoes");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof ExchangeOrderReceipt);
        assertEquals(alicebalance, ledger.getBalance(getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        SignedNamedObject cr = process(new ExchangeCompletionOrderBuilder((ExchangeOrderReceipt) receipt, new Date(), getBob().getName(), new Amount(18), "done"));
        assertNotNull(cr);
        assertTrue(cr instanceof ExchangeCompletedReceipt);
        assertEquals(alicebalance - 18, ledger.getBalance(getAlice().getName()), 0);
        assertEquals(alicebalance - 18, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    private SignedNamedObject process(Builder builder) throws NeuClearException {
        final SignedNamedObject obj = builder.convert("neu://alice@test", getSigner());

        return auditor.receive(proc.receive(obj));
    }

    public Asset createTestAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("http://bux.neuclear.org",
                getSigner().getPublicKey("neu://test/bux"),
                getAlice().getPublicKey(),
                2, 0);
        return (Asset) builder.convert("neu://test/bux", getSigner());

    }

    public Asset createShoeAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("http://shoes.neuclear.org",
                getSigner().getPublicKey("neu://test/bux"),
                getAlice().getPublicKey(),
                2, 0);
        return (Asset) builder.convert("neu://test", getSigner());

    }

    public ExchangeAgent createTestExchangeAgent() throws NeuClearException {
        ExchangeAgentBuilder builder = new ExchangeAgentBuilder("http://tradex.neuclear.org",
                getSigner().getPublicKey("neu://bob@test"));
        return (ExchangeAgent) builder.convert("neu://test", getSigner());

    }

    private Receiver proc;
    private Ledger ledger;
    private Ledger auditLedger;
    private Auditor auditor;
    private ExchangeAgent agent;
    private Asset asset;
    private Asset shoes;


}
