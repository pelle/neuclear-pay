package org.neuclear.asset;

import org.neuclear.asset.orders.TransferReceipt;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public final class TransferNotStartedException extends TransferException {
    public TransferNotStartedException(final AssetController proc, final TransferReceipt pmt, final Date time) {
        super(proc);
        this.pmt = pmt;
        this.time = time;
    }

    private final TransferReceipt pmt;
    private final Date time;

    public final TransferReceipt getPayment() {
        return pmt;
    }

    public final Date getTime() {
        return time;
    }

    public final String getSubMessage() {
        return "This payment commences at: " + pmt.getValueTime() + ". Action attempted at: " + time;
    }
}
