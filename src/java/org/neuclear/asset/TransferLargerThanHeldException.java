package org.neuclear.asset;


import org.neuclear.asset.contracts.HeldTransferReceipt;

/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public class TransferLargerThanHeldException extends TransferException {
    public TransferLargerThanHeldException(AssetController proc, HeldTransferReceipt hold, double amount) {
        super(proc);
        this.hold = hold;
        this.amount = amount;
    }

    private HeldTransferReceipt hold;
    private double amount;

    public HeldTransferReceipt getHeldPaymentReceipt() {
        return hold;
    }

    public double getAmount() {
        return amount;
    }

    public String getSubMessage() {
        return "Transfer requested: " + amount + " is larger than held amount: " + hold.getAmount();
    }
}
