package org.neuclear.asset.controllers.currency;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.controllers.auditor.Auditor;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.IssueReceipt;
import org.neuclear.asset.orders.builders.IssueOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.simple.SimpleLedgerController;
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
        ledger = new SimpleLedgerController("test");
        proc = new CurrencyController(ledger, asset, getSigner(), "neu://test/bux");
        auditLedger = new SimpleLedgerController("auditor");
        auditor = new Auditor(auditLedger);
    }

    public void testSimpleIssuance() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        // Sanity Check

        assertAudit(getBob().getName());
        assertAudit(asset.getIssuer().getName());

        double issuerBalance = ledger.getBalance(asset.getSignatory().getName(), asset.getIssuer().getName());
        double bobBalance = ledger.getBalance(asset.getSignatory().getName(), getBob().getName());

        assertEquals(bobBalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getBob().getName()), 0);

        final int amount = 1000;

        Builder builder = new IssueOrderBuilder(asset, getBob(), new Amount(amount), "fund");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof IssueReceipt);
        assertEquals(issuerBalance - amount, ledger.getAvailableBalance(asset.getSignatory().getName(), asset.getIssuer().getName()), 0);
        assertEquals(issuerBalance - amount, ledger.getBalance(asset.getSignatory().getName(), asset.getIssuer().getName()), 0);
        assertEquals(bobBalance + amount, ledger.getAvailableBalance(asset.getSignatory().getName(), getBob().getName()), 0);
        assertEquals(bobBalance + amount, ledger.getBalance(asset.getSignatory().getName(), getBob().getName()), 0);
        assertAudit(getBob().getName());
        assertAudit(asset.getIssuer().getName());
        assertTrue(ledger.isBalanced());
        assertTrue(auditLedger.isBalanced());
    }

/*
    public void testNegativeIssuance() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        // Sanity Check

        assertAudit(getBob().getName());
        assertAudit(asset.getIssuer().getName());

        double issuerBalance = ledger.getBalance(asset.getIssuer().getName());
        double bobBalance = ledger.getBalance(getBob().getName());

        assertEquals(bobBalance, ledger.getAvailableBalance(getBob().getName()), 0);

        final int amount = -1000;

        Builder builder = new IssueOrderBuilder(asset, getBob(), new Amount(amount), "fund");
        SignedNamedObject receipt = process(builder);
        assertNotNull(receipt);
        assertTrue(receipt instanceof IssueReceipt);
        assertEquals(issuerBalance - amount, ledger.getAvailableBalance(asset.getIssuer().getName()), 0);
        assertEquals(issuerBalance - amount, ledger.getBalance(asset.getIssuer().getName()), 0);
        assertEquals(bobBalance + amount, ledger.getAvailableBalance(getBob().getName()), 0);
        assertEquals(bobBalance + amount, ledger.getBalance(getBob().getName()), 0);
        assertAudit(getBob().getName());
        assertAudit(asset.getIssuer().getName());
        assertTrue(ledger.isBalanced());
        assertTrue(auditLedger.isBalanced());
    }
*/

    public void testFailOnIllegalIssuance() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        // Sanity Check

        assertAudit(getBob().getName());
        assertAudit(asset.getIssuer().getName());

        double issuerBalance = ledger.getBalance(asset.getSignatory().getName(), asset.getIssuer().getName());
        double bobBalance = ledger.getBalance(asset.getSignatory().getName(), getBob().getName());

        assertEquals(bobBalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getBob().getName()), 0);

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
        assertEquals(issuerBalance, ledger.getAvailableBalance(asset.getSignatory().getName(), asset.getIssuer().getName()), 0);
        assertEquals(issuerBalance, ledger.getBalance(asset.getSignatory().getName(), asset.getIssuer().getName()), 0);
        assertEquals(bobBalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getBob().getName()), 0);
        assertEquals(bobBalance, ledger.getBalance(asset.getSignatory().getName(), getBob().getName()), 0);
        assertAudit(getBob().getName());
        assertAudit(asset.getIssuer().getName());
        assertTrue(ledger.isBalanced());
        assertTrue(auditLedger.isBalanced());
    }

    public void testFailOnIllegalNegativeIssuance() throws InvalidTransferException, NeuClearException, InvalidTransactionException, LowlevelLedgerException {
        // Sanity Check

        assertAudit(getBob().getName());
        assertAudit(asset.getIssuer().getName());

        double issuerBalance = ledger.getBalance(asset.getSignatory().getName(), asset.getIssuer().getName());
        double bobBalance = ledger.getBalance(asset.getSignatory().getName(), getBob().getName());

        assertEquals(bobBalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getBob().getName()), 0);

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
        assertEquals(issuerBalance, ledger.getAvailableBalance(asset.getSignatory().getName(), asset.getIssuer().getName()), 0);
        assertEquals(issuerBalance, ledger.getBalance(asset.getSignatory().getName(), asset.getIssuer().getName()), 0);
        assertEquals(bobBalance, ledger.getAvailableBalance(asset.getSignatory().getName(), getBob().getName()), 0);
        assertEquals(bobBalance, ledger.getBalance(asset.getSignatory().getName(), getBob().getName()), 0);
        assertAudit(getBob().getName());
        assertAudit(asset.getIssuer().getName());
        assertTrue(ledger.isBalanced());
        assertTrue(auditLedger.isBalanced());
    }

    public void assertAudit(String name) throws LowlevelLedgerException {
        assertEquals(auditLedger.getBalance(asset.getSignatory().getName(), name), ledger.getBalance(asset.getSignatory().getName(), name), 0);
        assertEquals(auditLedger.getAvailableBalance(asset.getSignatory().getName(), name), ledger.getAvailableBalance(asset.getSignatory().getName(), name), 0);
    }

    private SignedNamedObject process(Builder builder) throws NeuClearException {
        final SignedNamedObject obj = builder.convert("carol", getSigner());

        return auditor.receive(proc.receive(obj));
    }

    private SignedNamedObject processIlegal(Builder builder) throws NeuClearException {
        final SignedNamedObject obj = builder.convert("neu://bob@test", getSigner());

        return auditor.receive(proc.receive(obj));
    }

    public Asset createTestAsset() throws NeuClearException {
        return (Asset) Resolver.resolveIdentity("http://bux.neuclear.org/bux.html");

    }

    private Receiver proc;
    private LedgerController ledger;
    private LedgerController auditLedger;
    private Auditor auditor;
    private Asset asset;


}
