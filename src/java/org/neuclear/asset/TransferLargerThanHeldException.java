package org.neuclear.asset;


import org.neuclear.asset.contracts.HeldTransferReceipt;
import org.neuclear.asset.contracts.CompleteHeldTransferRequest;

/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public final class TransferLargerThanHeldException extends InvalidTransferException {
    public TransferLargerThanHeldException(final CompleteHeldTransferRequest complete,final double amount) {
        super("amount too large");
        this.complete = complete;
        this.amount=amount;

    }

    private final CompleteHeldTransferRequest complete;
    private final double amount;

    public final CompleteHeldTransferRequest getRequest() {
        return complete;
    }

    /**
     *
     * @return The original amount of the transaction
     */


    public final double getAmount(){
        return amount;
    }

    public final String getSubMessage() {
        return "Transfer requested: " + complete.getAmount()+ " is larger than held amount: " + amount;
    }
}
