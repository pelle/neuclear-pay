package org.neuclear.exchange.orders;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.exchange.contracts.ExchangeAgent;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public final class CancelExchangeOrder extends ExchangeTransactionContract{

    private CancelExchangeOrder(final SignedNamedCore core, final Asset asset,final ExchangeAgent agent, final String exchangeid)  {
        super(core, asset,agent);
        this.exchangeid = exchangeid;
    }
    public ExchangeOrderReceipt getExchangeReceipt(){
        //TODO Implement
        return null;
    }

    public String getExchangeid() {
        return exchangeid;
    }

    private final String exchangeid;
}
