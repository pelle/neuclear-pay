package org.neuclear.asset;

import org.neuclear.exchange.orders.ExchangeCompletionOrder;

/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public final class ExpiredHeldTransferException extends InvalidTransferException {
    public ExpiredHeldTransferException(final ExchangeCompletionOrder held) {
        super("expired");
        this.held = held;
    }

    private final ExchangeCompletionOrder held;

    public ExchangeCompletionOrder getRequest() {
        return held;
    }

    public String getSubMessage() {
        return "Not possible to complete held payment at this time: " + held.getValuetime();
    }
}
