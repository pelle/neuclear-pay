package org.neuclear.asset.controllers.currency;

import org.neuclear.asset.Auditor;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.IssueReceipt;
import org.neuclear.asset.orders.builders.IssueOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.Ledger;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.simple.SimpleLedger;
import org.neuclear.tests.AbstractSigningTest;

import java.security.GeneralSecurityException;

/**
 * In this unit test Alice is the Issuer.
 */
public final class IssuanceTests extends AbstractSigningTest {
    public IssuanceTests(final String s) throws LowlevelLedgerException, GeneralSecurityException, NeuClearException {
        super(s);
        AssetGlobals.registerReaders();

        asset = createTestAsset();
        ledger = new SimpleLedger("test");
        proc = new CurrencyController(ledger, asset, getSigner(), "neu://test/bux");
        auditLedger = new SimpleLedger("auditor");
        auditor = new Auditor(asset, auditLedger);
    }

    public void testSimpleIssuance() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        // Sanity Check

        assertAudit(getBob().getName());
        assertAudit(getAlice().getName());

        double issuerBalance = ledger.getBalance(getAlice().getName());
        double bobBalance = ledger.getBalance(getBob().getName());

        assertEquals(bobBalance, ledger.getAvailableBalance(getBob().getName()), 0);

        final int amount = 1000;

        Builder builder = new IssueOrderBuilder(asset, getBob(), new Amount(amount), "fund");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof IssueReceipt);
        assertEquals(issuerBalance - amount, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertEquals(issuerBalance - amount, ledger.getBalance(getAlice().getName()), 0);
        assertEquals(bobBalance + amount, ledger.getAvailableBalance(getBob().getName()), 0);
        assertEquals(bobBalance + amount, ledger.getBalance(getBob().getName()), 0);
        assertAudit(getBob().getName());
        assertAudit(getAlice().getName());
        assertTrue(ledger.isBalanced());
        assertTrue(auditLedger.isBalanced());
    }

/*
    public void testNegativeIssuance() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        // Sanity Check

        assertAudit(getBob().getName());
        assertAudit(getAlice().getName());

        double issuerBalance = ledger.getBalance(getAlice().getName());
        double bobBalance = ledger.getBalance(getBob().getName());

        assertEquals(bobBalance, ledger.getAvailableBalance(getBob().getName()), 0);

        final int amount = -1000;

        Builder builder = new IssueOrderBuilder(asset, getBob(), new Amount(amount), "fund");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof IssueReceipt);
        assertEquals(issuerBalance - amount, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertEquals(issuerBalance - amount, ledger.getBalance(getAlice().getName()), 0);
        assertEquals(bobBalance + amount, ledger.getAvailableBalance(getBob().getName()), 0);
        assertEquals(bobBalance + amount, ledger.getBalance(getBob().getName()), 0);
        assertAudit(getBob().getName());
        assertAudit(getAlice().getName());
        assertTrue(ledger.isBalanced());
        assertTrue(auditLedger.isBalanced());
    }
*/

    public void testFailOnIllegalIssuance() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        // Sanity Check

        assertAudit(getBob().getName());
        assertAudit(getAlice().getName());

        double issuerBalance = ledger.getBalance(getAlice().getName());
        double bobBalance = ledger.getBalance(getBob().getName());

        assertEquals(bobBalance, ledger.getAvailableBalance(getBob().getName()), 0);

        final int amount = 1000;

        try {
            Builder builder = new IssueOrderBuilder(asset, getAlice(), new Amount(amount), "fund");
            SignedNamedObject receipt = processIlegal(builder);
            assertNull(receipt);
            assertFalse(receipt instanceof IssueReceipt);
            assertTrue(false);
        } catch (InvalidNamedObjectException e) {
            assertTrue(true);
        } catch (InvalidTransferException e) {
            assertTrue(true);
        }
        assertEquals(issuerBalance, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertEquals(issuerBalance, ledger.getBalance(getAlice().getName()), 0);
        assertEquals(bobBalance, ledger.getAvailableBalance(getBob().getName()), 0);
        assertEquals(bobBalance, ledger.getBalance(getBob().getName()), 0);
        assertAudit(getBob().getName());
        assertAudit(getAlice().getName());
        assertTrue(ledger.isBalanced());
        assertTrue(auditLedger.isBalanced());
    }

    public void testFailOnIllegalNegativeIssuance() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        // Sanity Check

        assertAudit(getBob().getName());
        assertAudit(getAlice().getName());

        double issuerBalance = ledger.getBalance(getAlice().getName());
        double bobBalance = ledger.getBalance(getBob().getName());

        assertEquals(bobBalance, ledger.getAvailableBalance(getBob().getName()), 0);

        final int amount = -1000;

        try {
            Builder builder = new IssueOrderBuilder(asset, getAlice(), new Amount(amount), "fund");
            SignedNamedObject receipt = processIlegal(builder);
            assertNull(receipt);
            assertFalse(receipt instanceof IssueReceipt);
            assertTrue(false);
        } catch (InvalidNamedObjectException e) {
            assertTrue(true);
        } catch (InvalidTransferException e) {
            assertTrue(true);
        }
        assertEquals(issuerBalance, ledger.getAvailableBalance(getAlice().getName()), 0);
        assertEquals(issuerBalance, ledger.getBalance(getAlice().getName()), 0);
        assertEquals(bobBalance, ledger.getAvailableBalance(getBob().getName()), 0);
        assertEquals(bobBalance, ledger.getBalance(getBob().getName()), 0);
        assertAudit(getBob().getName());
        assertAudit(getAlice().getName());
        assertTrue(ledger.isBalanced());
        assertTrue(auditLedger.isBalanced());
    }

    public void assertAudit(String name) throws LowlevelLedgerException {
        assertEquals(auditLedger.getBalance(name), ledger.getBalance(name), 0);
        assertEquals(auditLedger.getAvailableBalance(name), ledger.getAvailableBalance(name), 0);
    }

    private SignedNamedObject process(Builder builder) throws NeuClearException {
        final SignedNamedObject obj = builder.convert("neu://alice@test", getSigner());

        return auditor.receive(proc.receive(obj));
    }

    private SignedNamedObject processIlegal(Builder builder) throws NeuClearException {
        final SignedNamedObject obj = builder.convert("neu://bob@test", getSigner());

        return auditor.receive(proc.receive(obj));
    }

    public Asset createTestAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("http://bux.neuclear.org", "bux",
                getSigner().getPublicKey("neu://test/bux"),
                getAlice().getPublicKey(),
                2, 0);
        return (Asset) builder.convert("neu://test/bux", getSigner());

    }

    private Receiver proc;
    private Ledger ledger;
    private Ledger auditLedger;
    private Auditor auditor;
    private Asset asset;


}