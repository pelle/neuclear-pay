package org.neuclear.asset.controllers.currency;

import junit.framework.TestCase;
import org.neuclear.asset.*;
import org.neuclear.asset.controllers.currency.CurrencyController;
import org.neuclear.asset.contracts.HeldTransferReceipt;
import org.neuclear.asset.contracts.TransferReceipt;
import org.neuclear.commons.configuration.ConfigurationException;
import org.neuclear.ledger.*;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 6:05:04 PM
 */
public class CurrencyTests extends TestCase {
    public CurrencyTests(String s) throws SQLException, IOException, LowlevelLedgerException, BookExistsException, LedgerCreationException, ConfigurationException {
        super(s);
//        proc = CurrencyController.getInstance();
    }
/*
    public void testFundAccount() throws LowlevelLedgerException, BookExistsException, UnknownBookException, UnBalancedTransactionException, InvalidTransactionException, NegativeTransferException, AssetMismatchException {
        Account bob = createBobAccount();
        assertNotNull(bob);
        assertEquals(bob.getBalance(), 0.0, 0);
        proc.getIssuer().issue(bob, 100, new Date());
        assertEquals(bob.getBalance(), 100.0, 0);
    }

    public void testValidPayment() throws LowlevelLedgerException, BookExistsException, UnknownBookException, UnBalancedTransactionException, InvalidTransactionException, InsufficientFundsException, NegativeTransferException, AssetMismatchException {
        double initial = 100;
        double payment = 75;
        Account bob = createBobAccount();
        Account alice = createAliceAccount();
        assertNotNull(bob);
        assertNotNull(alice);
        assertEquals(bob.getBalance(), 0.0, 0);
        assertEquals(alice.getBalance(), 0.0, 0);
        proc.getIssuer().issue(bob, initial, new Date());
        assertEquals(bob.getBalance(), initial, 0);
        bob.pay(alice, payment, new Date(), "Test Valid Transfer");
        assertEquals(bob.getBalance(), initial - payment, 0);
        assertEquals(alice.getBalance(), payment, 0);
    }

    public void testInValidPayment() throws LowlevelLedgerException, BookExistsException, UnknownBookException, UnBalancedTransactionException, InvalidTransactionException, InsufficientFundsException, NegativeTransferException, AssetMismatchException {
        double initial = 75;
        double payment = 100;
        Account bob = createBobAccount();
        Account alice = createAliceAccount();
        assertNotNull(bob);
        assertNotNull(alice);
        assertEquals(bob.getBalance(), 0.0, 0);
        assertEquals(alice.getBalance(), 0.0, 0);
        proc.getIssuer().issue(bob, initial, new Date());
        assertEquals(bob.getBalance(), initial, 0);

        // check for insufficient funds
        try {
            bob.pay(alice, payment, new Date(), "Test for Insufficient Funds");
            assertTrue("Didnt get Insufficient Funds Exception", false);
        } catch (InsufficientFundsException e) {
            assertTrue("Got Insufficient Funds Exception", true);
        }
        assertEquals(bob.getBalance(), initial, 0);
        assertEquals(alice.getBalance(), 0, 0);
        // Check for negative payments
        try {
            bob.pay(alice, -payment, new Date(), "Attempted negative payment");
            assertTrue("Performed Negative Transfer", false);
        } catch (NegativeTransferException e) {
            assertTrue("Couldnt perform Negative Transfer", true);
        }
        assertEquals(bob.getBalance(), initial, 0);
        assertEquals(alice.getBalance(), 0, 0);

    }

    public void testHeldPayment() throws LowlevelLedgerException, BookExistsException, UnknownBookException, UnBalancedTransactionException, InvalidTransactionException, InsufficientFundsException, NegativeTransferException, TransferNotStartedException, TransferLargerThanHeldException, ExpiredHeldTransferException, AssetMismatchException {
        double initial = 100;
        double payment = 75;
        Account bob = createBobAccount();
        Account alice = createAliceAccount();
        assertNotNull(bob);
        assertNotNull(alice);
        Calendar cal = Calendar.getInstance();
        Date t1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t3 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t4 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t5 = cal.getTime();


        assertEquals(bob.getBalance(), 0.0, 0);
        assertEquals(alice.getBalance(), 0.0, 0);
        proc.getIssuer().issue(bob, initial, t1);
        assertEquals(bob.getBalance(t1), initial, 0);

        HeldTransferReceipt hold = bob.hold(alice, payment, t2, t4, "Test Hold");
        assertEquals(bob.getBalance(t2), initial, 0);
        assertEquals(bob.getBalance(t3), initial, 0);
        assertEquals(bob.getBalance(t4), initial, 0);
        assertEquals(bob.getAvailableBalance(t1), initial, 0);
        assertEquals(bob.getAvailableBalance(t2), initial - payment, 0);
        assertEquals(bob.getAvailableBalance(t3), initial - payment, 0);
        assertEquals(bob.getAvailableBalance(t4), initial - payment, 0);
        assertEquals(bob.getAvailableBalance(t5), initial, 0);

        // check for insufficient funds
        try {
            bob.pay(alice, payment, t3, "Test for Insufficient Funds");
            assertTrue("Didnt get Insufficient Funds Exception", false);
        } catch (InsufficientFundsException e) {
            assertTrue("Got Insufficient Funds Exception", true);
        }

        try {
            hold.complete(t3, payment + 20, "attempt at completing with higher amount");
            assertTrue("Should throw TransferLargerThanHeldException", false);
        } catch (TransferLargerThanHeldException e) {
            assertTrue("Got Transfer Larger Than Held Exception", true);

        } catch (TransferNotStartedException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

        try {
            hold.complete(t3, -payment, "attempt at completing with negative amount");
            assertTrue("Should throw NegativeTransferException", false);
        } catch (NegativeTransferException e) {
            assertTrue("Got Negative Transfer Exception", true);

        }

        try {
            hold.complete(t5, payment, "attempt at completing payment at past date");
            assertTrue("Should throw ExpiredHeldTransferException", false);
        } catch (ExpiredHeldTransferException e) {
            assertTrue("Threw ExpiredHeldTransferException", true);
        }

        try {
            hold.complete(t1, payment, "attempt at completing payment early");
            assertTrue("Should throw ExpiredHeldTransferException", false);
        } catch (ExpiredHeldTransferException e) {
            assertTrue("Threw ExpiredHeldTransferException", true);
        }

        TransferReceipt receipt = hold.complete(t3, payment, "valid completion of held payment");
        System.out.println("Completed held: " + hold.getId() + " complete= " + receipt.getId());
        assertEquals(bob.getBalance(t2), initial, 0);
        assertEquals(bob.getBalance(t3), initial - payment, 0);
        assertEquals(bob.getBalance(t4), initial - payment, 0);
        assertEquals(bob.getBalance(t5), initial - payment, 0);
        assertEquals(bob.getAvailableBalance(t1), initial, 0);
        assertEquals(bob.getAvailableBalance(t2), initial, 0);
        assertEquals(bob.getAvailableBalance(t3), initial - payment, 0);
        assertEquals(bob.getAvailableBalance(t4), initial - payment, 0);
        assertEquals(bob.getAvailableBalance(t5), initial - payment, 0);


    }

*/
    private CurrencyController proc;
}
