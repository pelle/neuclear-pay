package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedCore;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 11:59:36 AM
 */
public final class ExchangeReceipt extends TransferContract implements Exchange {

    ExchangeReceipt(final SignedNamedCore core, final Asset asset, final Identity from, final Identity agent, final String reqid, final double amount, final Date valuetime, final String comment, final Date helduntil)  {
        super(core, asset,  amount,  comment);
        this.from = from;
        this.reqid = reqid;
        this.helduntil = helduntil.getTime();
        this.agent=agent;
        this.valuetime=valuetime.getTime();
    }

    public final Date getValidTo() {
        return new Timestamp(helduntil);
    }

    public Identity getAgent() {
        return agent;  //To change body of implemented methods use Options | File Templates.
    }


    public Identity getFrom() {
        return from;  //To change body of implemented methods use Options | File Templates.
    }
    public final Date getValueTime() {
        return new Timestamp(valuetime);
    }
    private final long valuetime;

    private final long helduntil;
    private final Identity from;
    private final String reqid;
    private final Identity agent;

}
