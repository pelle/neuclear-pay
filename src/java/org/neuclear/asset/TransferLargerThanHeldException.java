package org.neuclear.asset;


import org.neuclear.asset.contracts.HeldTransferReceipt;
import org.neuclear.asset.contracts.CompleteHeldTransferRequest;

/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public class TransferLargerThanHeldException extends InvalidTransferException {
    public TransferLargerThanHeldException(CompleteHeldTransferRequest complete,double amount) {
        super("amount too large");
        this.complete = complete;
        this.amount=amount;

    }

    private CompleteHeldTransferRequest complete;
    private double amount;

    public CompleteHeldTransferRequest getRequest() {
        return complete;
    }

    /**
     *
     * @return The original amount of the transaction
     */


    public double getAmount(){
        return amount;
    }

    public String getSubMessage() {
        return "Transfer requested: " + complete.getAmount()+ " is larger than held amount: " + amount;
    }
}
