package org.neuclear.asset;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:37:10 PM
 */
public class TransferReceipt {
    private final double amount;
    private final Date valuetime;
    private final Account from;
    private final Account to;
    private final String comment;
    private final String id;
    private final AssetController controller;


    TransferReceipt(AssetController controller, TransferRequest preq, String id) {
        this.id = id;
        this.controller = controller;
        amount = preq.getAmount();
        valuetime = preq.getValueTime();
        from = preq.getFrom();
        to = preq.getTo();
        comment = preq.getComment();
    }

    public final double getAmount() {
        return amount;
    }

    public final Date getValueTime() {
        return valuetime;
    }

    public final Account getTo() {
        return to;
    }

    public final Account getFrom() {
        return from;
    }

    public final AssetController getController() {
        return controller;
    }

    public final String getId() {
        return id;
    }

    public final String getComment() {
        return comment;
    }
}
