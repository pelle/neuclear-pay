package org.neuclear.asset;

import org.neuclear.asset.contracts.Held;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public class ExpiredHeldTransferException extends TransferException {
    public ExpiredHeldTransferException(AssetController proc, Held held, Date time) {
        super(proc);
        this.held = held;
        this.time = time;
    }

    private Held held;
    private Date time;

    public Held getHeld() {
        return held;
    }

    public Date getTime() {
        return time;
    }

    public String getSubMessage() {
        return "Not possible to complete held payment at this time: " + time + ". Hold expired at: " + held.getHeldUntil();
    }
}
