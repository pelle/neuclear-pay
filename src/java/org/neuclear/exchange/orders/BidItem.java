package org.neuclear.exchange.orders;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.Value;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jan 12, 2004
 * Time: 5:27:24 PM
 * To change this template use Options | File Templates.
 */
public  class BidItem {
    public BidItem(Asset asset, Value amount) {
        this.asset = asset;
        this.amount = amount;
    }

    public final Asset getAsset() {
        return asset;
    }

    public final Value getAmount() {
        return amount;
    }

    private final Asset asset;
    private final Value amount;
}
