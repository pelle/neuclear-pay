package org.neuclear.asset;

import org.neuclear.id.Identity;


/**
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 11:38:10 AM
 */
public class InsufficientFundsException extends TransferException {
    public InsufficientFundsException(AssetController proc, Identity account, double amount) {
        super(proc);
        this.account = account;
        this.amount = amount;
    }

    private Identity account;
    private double amount;

    public Identity getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

    public String getSubMessage() {
        return "Insufficient Funds in Account: " + account.getName() + " to pay: " + amount;
    }
}
