package org.neuclear.asset;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 12:01:03 PM
 */
public class HeldTransferRequest extends TransferRequest implements Held {
    public HeldTransferRequest(Account from, Account to, double amount, Date valuedate, Date helduntil, String comment) throws NegativeTransferException, AssetMismatchException {

        super(from, to, amount, valuedate, comment);

        this.helduntil = helduntil;
    }

    public final Date getHeldUntil() {
        return helduntil;
    }

    private final Date helduntil;
}
