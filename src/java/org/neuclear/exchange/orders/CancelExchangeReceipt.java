package org.neuclear.exchange.orders;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.exchange.orders.CancelExchangeOrder;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public final class CancelExchangeReceipt extends AssetTransactionContract{

    private CancelExchangeReceipt(final SignedNamedCore core, final Asset asset, final Date canceltime,final String exchangeid, final String cancelid)  {
        super(core, asset);
        this.canceltime=canceltime.getTime();
        this.exchangeid=exchangeid;
        this.cancelid=cancelid;
    }

    public CancelExchangeOrder getCancellationOrder(){
        //TODO implement
        return null;
    }
    public CancelExchangeOrder getExchangeReceipt(){
        //TODO implement
        return null;
    }
    public Timestamp getCanceltime() {
        return new Timestamp(canceltime);
    }

    public String getExchangeid() {
        return exchangeid;
    }

    public String getCancelid() {
        return cancelid;
    }

    private final long   canceltime;
    private final String exchangeid;
    private final String cancelid;
}
