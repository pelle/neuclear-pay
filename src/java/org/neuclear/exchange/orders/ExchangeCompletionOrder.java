package org.neuclear.exchange.orders;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.asset.orders.TransferContract;
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
public final class ExchangeCompletionOrder extends ExchangeTransactionContract {
    private ExchangeCompletionOrder(final SignedNamedCore core, final Asset asset, final ExchangeAgent agent, final Date valuetime, final double amount, final String holdid, final String counterpartyid) {
        super(core, asset,agent);
        this.valuetime = valuetime.getTime();
        this.amount = amount;
        this.holdid = holdid;
        this.counterpartyid = counterpartyid;
    }

    public Timestamp getValuetime() {
        return new Timestamp(valuetime);
    }

    public double getAmount() {
        return amount;
    }

    public String getHoldid() {
        return holdid;
    }

    public String getCounterpartyid() {
        return counterpartyid;
    }

    private final long valuetime;
    private final double amount;
    private final String holdid;
    private final String counterpartyid;

}
