package org.neuclear.asset;

import org.neuclear.asset.contracts.Held;
import org.neuclear.asset.contracts.CompleteHeldTransferRequest;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public class ExpiredHeldTransferException extends InvalidTransferException {
    public ExpiredHeldTransferException(CompleteHeldTransferRequest held) {
        super("expired");
        this.held = held;
    }

    private CompleteHeldTransferRequest held;

    public CompleteHeldTransferRequest getRequest() {
        return held;
    }

    public String getSubMessage() {
        return "Not possible to complete held payment at this time: " + held.getValueTime();
    }
}
