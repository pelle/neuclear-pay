package org.neuclear.asset;


/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public final class NegativeTransferException extends InvalidTransferException {
    public NegativeTransferException(final double amount) {
        super("negative amount");
        this.amount = amount;
    }

    private final double amount;


    public final double getAmount() {
        return amount;
    }

    public final String getSubMessage() {
        return "Not possible to perform payment of negative amount: " + amount;
    }
}
