package org.neuclear.asset;

import org.neuclear.ledger.LowlevelLedgerException;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 11:59:36 AM
 */
public class HeldTransferReceipt extends TransferReceipt implements Held {

    HeldTransferReceipt(AssetController proc, HeldTransferRequest preq, String id) {
        super(proc, preq, id);
        helduntil = preq.getHeldUntil();
    }

    public final void cancel() throws LowlevelLedgerException, ExpiredHeldTransferException {
        getController().processCancelHold(this);
    }

    public final TransferReceipt complete(Date valueDate, double amount, String comment) throws LowlevelLedgerException, TransferNotStartedException, ExpiredHeldTransferException, TransferLargerThanHeldException, NegativeTransferException {

        return getController().processCompleteHold(this, valueDate, amount, comment);

    }

    public final Date getHeldUntil() {
        return helduntil;
    }

    private final Date helduntil;
}
