package org.neuclear.asset;


import org.neuclear.exchange.orders.ExchangeCompletionOrder;

/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public final class TransferLargerThanHeldException extends InvalidTransferException {
    public TransferLargerThanHeldException(final ExchangeCompletionOrder complete,final double amount) {
        super("amount too large");
        this.complete = complete;
        this.amount=amount;

    }

    private final ExchangeCompletionOrder complete;
    private final double amount;

    public final ExchangeCompletionOrder getRequest() {
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
