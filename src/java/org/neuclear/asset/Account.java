package org.neuclear.asset;

import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnBalancedTransactionException;
import org.neuclear.ledger.UnknownBookException;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:12:54 PM
 * To change this template use Options | File Templates.
 */
public abstract class Account {

    private final AssetController controller;

    protected Account(AssetController controller) {
        this.controller = controller;
    }

    public final AssetController getController() {
        return controller;
    }

    abstract public String getID();

    abstract public double getBalance() throws LowlevelLedgerException;

    abstract public double getAvailableBalance() throws LowlevelLedgerException;

    abstract public double getBalance(Date time) throws LowlevelLedgerException;

    abstract public double getAvailableBalance(Date time) throws LowlevelLedgerException;

    abstract public String getDisplayName();

    final public TransferReceipt pay(Account to, double amount, Date valuedate, String comment) throws UnknownBookException, UnBalancedTransactionException, InvalidTransactionException, LowlevelLedgerException, NegativeTransferException, InsufficientFundsException, AssetMismatchException {
        if (amount < 0)
            throw new NegativeTransferException(controller, amount);
        if (getAvailableBalance(valuedate) - amount < 0)
            throw new InsufficientFundsException(controller, this, amount);

        return controller.processTransfer(new TransferRequest(this, to, amount, valuedate, comment));
    }

    final public HeldTransferReceipt hold(Account to, double amount, Date valuedate, Date helduntil, String comment) throws UnknownBookException, UnBalancedTransactionException, InvalidTransactionException, LowlevelLedgerException, NegativeTransferException, InsufficientFundsException, AssetMismatchException {
        if (amount < 0)
            throw new NegativeTransferException(controller, amount);
        if (getAvailableBalance(valuedate) - amount < 0)
            throw new InsufficientFundsException(controller, this, amount);

        return controller.processHeldTransfer(new HeldTransferRequest(this, to, amount, valuedate, helduntil, comment));
    }
}
