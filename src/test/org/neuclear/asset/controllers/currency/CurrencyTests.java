package org.neuclear.asset.controllers.currency;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.asset.controllers.auditor.Auditor;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.asset.orders.builders.TransferOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.exchange.contracts.ExchangeAgentGlobals;
import org.neuclear.exchange.orders.BidItem;
import org.neuclear.exchange.orders.CancelExchangeReceipt;
import org.neuclear.exchange.orders.ExchangeCompletedReceipt;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.exchange.orders.builders.CancelExchangeOrderBuilder;
import org.neuclear.exchange.orders.builders.ExchangeCompletionOrderBuilder;
import org.neuclear.exchange.orders.builders.ExchangeOrderBuilder;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.ledger.simple.SimpleLedgerController;
import org.neuclear.tests.AbstractSigningTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 6:05:04 PM
 */
public class CurrencyTests extends AbstractSigningTest {

    public CurrencyTests(final String s) throws LowlevelLedgerException, GeneralSecurityException, NeuClearException {
        super(s);
        AssetGlobals.registerReaders();
        ExchangeAgentGlobals.registerReaders();
        asset = createTestAsset();
        shoes = createShoeAsset();
        agent = createTestExchangeAgent();
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
        ledger = createControllerLedger();
        auditLedger = createAuditLedger();
        proc = new CurrencyController(ledger, asset, getSigner(), "neu://test/bux");
        auditor = new Auditor(auditLedger);
    }

    protected LedgerController createControllerLedger() throws IOException, ClassNotFoundException, LowlevelLedgerException {
        return new SimpleLedgerController("asset");
    }

    protected LedgerController createAuditLedger() throws LowlevelLedgerException {
        return new SimpleLedgerController("audit");
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     */
    protected void tearDown() throws Exception {
        proc = null;
        auditor = null;
        ledger.close();
        auditLedger.close();
        ledger = null;
        auditLedger = null;
    }

    public void testTransfer() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        fundAccount();
        double alicebalance = ledger.getBalance(asset.getSignatory().getName(), getAlice().getName());
        assertAudit(getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        Builder builder = new TransferOrderBuilder(asset, getBob(), new Amount(25), "test");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof TransferReceipt);
        assertEquals(alicebalance - 25, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 25, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    private void fundAccount() throws InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        double orig = ledger.getBalance(asset.getSignatory().getName(), getAlice().getName());
        ledger.transfer(asset.getSignatory().getName(), asset.getIssuer().getName(), getAlice().getName(), 50, "fund");
        assertEquals(50 + orig, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        orig = auditLedger.getBalance(asset.getSignatory().getName(), getAlice().getName());
        auditLedger.transfer(asset.getSignatory().getName(), asset.getIssuer().getName(), getAlice().getName(), 50, "fund");
        assertEquals(50 + orig, auditLedger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
    }

    public void assertAudit(String name) throws LowlevelLedgerException {
        assertEquals(auditLedger.getBalance(asset.getSignatory().getName(), name), ledger.getBalance(asset.getSignatory().getName(), name), 0);
        assertEquals(auditLedger.getAvailableBalance(asset.getSignatory().getName(), name), ledger.getAvailableBalance(asset.getSignatory().getName(), name), 0);
    }

    public void testExchangeOrderAndExpire() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        fundAccount();
        double alicebalance = ledger.getBalance(asset.getSignatory().getName(), getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        final long expire = System.currentTimeMillis() + 20000;
        Builder builder = new ExchangeOrderBuilder(asset, agent, new Amount(20), new Date(expire), new BidItem[]{new BidItem(shoes, new Amount(10))}, "give me shoes");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof ExchangeOrderReceipt);
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        final double avail = ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName());
        assertTrue(System.currentTimeMillis() <= expire && (alicebalance - 20) == avail);
        assertAudit(getAlice().getName());
        try {
            final long left = expire - System.currentTimeMillis();
            if (left > 0) {
                System.out.println("sleeping " + left);
                Thread.currentThread().sleep(left);
            }
        } catch (InterruptedException e) {
            ;
        }
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    public void testExchangeOrderAndCancelByOwner() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        fundAccount();
        double alicebalance = ledger.getBalance(asset.getSignatory().getName(), getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        Builder builder = new ExchangeOrderBuilder(asset, agent, new Amount(20), new Date(System.currentTimeMillis() + 20000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "give me shoes");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof ExchangeOrderReceipt);
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
        SignedNamedObject cr = process(new CancelExchangeOrderBuilder((ExchangeOrderReceipt) receipt));
        assertNotNull(cr);
        assertTrue(cr instanceof CancelExchangeReceipt);
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    public void testExchangeOrderAndCancelByAgent() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        fundAccount();
        double alicebalance = ledger.getBalance(asset.getSignatory().getName(), getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        Builder builder = new ExchangeOrderBuilder(asset, agent, new Amount(20), new Date(System.currentTimeMillis() + 20000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "give me shoes");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof ExchangeOrderReceipt);
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
        SignedNamedObject cr = processAgent(new CancelExchangeOrderBuilder((ExchangeOrderReceipt) receipt));
        assertNotNull(cr);
        assertTrue(cr instanceof CancelExchangeReceipt);
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    public void testFailOnExchangeOrderAndCancelByBaddie() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        fundAccount();
        double alicebalance = ledger.getBalance(asset.getSignatory().getName(), getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        Builder builder = new ExchangeOrderBuilder(asset, agent, new Amount(20), new Date(System.currentTimeMillis() + 30000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "give me shoes");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof ExchangeOrderReceipt);
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
        try {
            SignedNamedObject cr = processHacker(new CancelExchangeOrderBuilder((ExchangeOrderReceipt) receipt));
            assertTrue(false);
        } catch (InvalidNamedObjectException e) {
            assertTrue(true);
        }
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    public void testExchangeOrderAndComplete() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        fundAccount();
        double alicebalance = ledger.getBalance(asset.getSignatory().getName(), getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        Builder builder = new ExchangeOrderBuilder(asset, agent, new Amount(20), new Date(System.currentTimeMillis() + 30000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "give me shoes");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof ExchangeOrderReceipt);
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        SignedNamedObject cr = processAgent(new ExchangeCompletionOrderBuilder((ExchangeOrderReceipt) receipt, new Date(), getBob().getName(), new Amount(18), "done"));
        assertNotNull(cr);
        assertTrue(cr instanceof ExchangeCompletedReceipt);
        assertEquals(alicebalance - 18, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 18, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    public void testFailOnExchangeOrderAndCompleteByOwner() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        fundAccount();
        double alicebalance = ledger.getBalance(asset.getSignatory().getName(), getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        Builder builder = new ExchangeOrderBuilder(asset, agent, new Amount(20), new Date(System.currentTimeMillis() + 30000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "give me shoes");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof ExchangeOrderReceipt);
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        try {
            SignedNamedObject cr = process(new ExchangeCompletionOrderBuilder((ExchangeOrderReceipt) receipt, new Date(), getBob().getName(), new Amount(18), "done"));
            assertTrue(false);
        } catch (InvalidNamedObjectException e) {
            assertTrue(true);
        } catch (InvalidTransferException e) {
            assertTrue(true);
        }

        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    public void testFailOnExchangeOrderAndCompleteByBaddie() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        fundAccount();
        double alicebalance = ledger.getBalance(asset.getSignatory().getName(), getAlice().getName());
        assertEquals(alicebalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        Builder builder = new ExchangeOrderBuilder(asset, agent, new Amount(20), new Date(System.currentTimeMillis() + 30000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "give me shoes");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof ExchangeOrderReceipt);
        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());

        try {
            SignedNamedObject cr = processHacker(new ExchangeCompletionOrderBuilder((ExchangeOrderReceipt) receipt, new Date(), getBob().getName(), new Amount(18), "done"));
            assertTrue(false);
        } catch (InvalidNamedObjectException e) {
            assertTrue(true);
        } catch (InvalidTransferException e) {
            assertTrue(true);
        }

        assertEquals(alicebalance, ledger.getBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertEquals(alicebalance - 20, ledger.getAvailableBalance(asset.getSignatory().getName(), getAlice().getName()), 0);
        assertAudit(getAlice().getName());
    }

    private SignedNamedObject process(Builder builder) throws NeuClearException {
        final SignedNamedObject obj = builder.convert("neu://alice@test", getSigner());

        return auditor.receive(proc.receive(obj));
    }

    private SignedNamedObject processAgent(Builder builder) throws NeuClearException {
        final SignedNamedObject obj = builder.convert("exchange", getSigner());

        return auditor.receive(proc.receive(obj));
    }

    private SignedNamedObject processHacker(Builder builder) throws NeuClearException {
        final SignedNamedObject obj = builder.convert("neu://test", getSigner());

        return auditor.receive(proc.receive(obj));
    }


    public Asset createTestAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("bux", "http://bux.neuclear.org/bux.html", "html://bux.neuclear.org/bux.html",
                getSigner().getPublicKey("bux"),
                getIssuer().getPublicKey(),
                2, 0, "bux");
        final SignedNamedObject object = builder.convert("ivan", getSigner());
        System.out.println("Object type: " + object.getClass().getName());
        return (Asset) object;

    }

    public Asset createShoeAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("Super Shoes", "http://shoes.neuclear.org/shoes.html", "http://shoes.neuclear.org/Asset",
                getSigner().getPublicKey("shoes"),
                getIssuer().getPublicKey(),
                2, 0, "pairs of shoes");
        return (Asset) builder.convert("neu://test", getSigner());

    }

    public ExchangeAgent createTestExchangeAgent() throws NeuClearException {
//        ExchangeAgentBuilder builder = new ExchangeAgentBuilder("Tradex", "http://tradex.neuclear.org/rules.html", "http://tradex.neuclear.org/Exchange",
//                getSigner().getPublicKey("exchange"));
//        return (ExchangeAgent) builder.convert("neu://test", getSigner());
        return (ExchangeAgent) Resolver.resolveIdentity("http://tradex.neuclear.org/rules.html");
    }

    private Receiver proc;
    private LedgerController ledger;
    private LedgerController auditLedger;
    private Auditor auditor;
    private ExchangeAgent agent;
    private Asset asset;
    private Asset shoes;


}
