package org.neuclear.asset;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public class TransferRequest {
    private final double amount;
    private final Date valuedate;
    private final Account to;
    private final Account from;
    private final String comment;

    public TransferRequest(Account from, Account to, double amount, Date valuedate, String comment) throws NegativeTransferException, AssetMismatchException {
        if (!from.getController().equals(to.getController()))
            throw new AssetMismatchException(from, to);
        if (amount < 0)
            throw new NegativeTransferException(from.getController(), amount);
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.valuedate = valuedate;
        this.comment = (comment != null) ? comment : "";
    }

    public final double getAmount() {
        return amount;
    }

    public final Date getValueTime() {
        return valuedate;
    }

    public final Account getTo() {
        return to;
    }

    public final Account getFrom() {
        return from;
    }

    public final String getComment() {
        return comment;
    }


}
