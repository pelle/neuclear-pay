package org.neuclear.asset;

import org.neuclear.asset.contracts.Exchange;
import org.neuclear.asset.contracts.CompleteExchangeRequest;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public final class ExpiredHeldTransferException extends InvalidTransferException {
    public ExpiredHeldTransferException(final CompleteExchangeRequest held) {
        super("expired");
        this.held = held;
    }

    private final CompleteExchangeRequest held;

    public CompleteExchangeRequest getRequest() {
        return held;
    }

    public String getSubMessage() {
        return "Not possible to complete held payment at this time: " + held.getValueTime();
    }
}
