package org.neuclear.asset;

import org.neuclear.asset.contracts.TransferReceipt;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public class TransferNotStartedException extends TransferException {
    public TransferNotStartedException(AssetController proc, TransferReceipt pmt, Date time) {
        super(proc);
        this.pmt = pmt;
        this.time = time;
    }

    private TransferReceipt pmt;
    private Date time;

    public TransferReceipt getPayment() {
        return pmt;
    }

    public Date getTime() {
        return time;
    }

    public String getSubMessage() {
        return "This payment commences at: " + pmt.getValueTime() + ". Action attempted at: " + time;
    }
}
