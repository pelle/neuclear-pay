package org.neuclear.asset;

import org.neuclear.id.Identity;


/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public final class InsufficientFundsException extends TransferException {
    public InsufficientFundsException(final AssetController proc, final Identity account, final double amount) {
        super(proc);
        this.account = account;
        this.amount = amount;
    }

    private final Identity account;
    private final double amount;

    public final Identity getAccount() {
        return account;
    }

    public final double getAmount() {
        return amount;
    }

    public final String getSubMessage() {
        return "Insufficient Funds in Account: " + account.getName() + " to pay: " + amount;
    }
}
