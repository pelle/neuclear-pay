package org.neuclear.asset;

import org.neuclear.asset.orders.Value;


/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public final class NegativeTransferException extends InvalidTransferException {
    public NegativeTransferException(final Value amount) {
        super("negative amount");
        this.amount = amount;
    }

    private final Value amount;


    public final Value getAmount() {
        return amount;
    }

    public final String getSubMessage() {
        return "Not possible to perform payment of negative amount: " + amount;
    }
}
