package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedCore;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 12:01:03 PM
 */
public final class ExchangeRequest extends TransferContract implements Exchange {
    ExchangeRequest(final SignedNamedCore core,
                    final Asset asset, final Identity agent, final double amount,  final String comment, final Date helduntil)  {
        super(core, asset,  amount,  comment);
        this.validto = helduntil.getTime();
        this.agent=agent;
    }

    public final Date getValidTo() {
        return new Timestamp(validto);
    }

    public Identity getAgent() {
        return agent;  //To change body of implemented methods use Options | File Templates.
    }
    public final Identity getFrom() {
        return getSignatory();
    }

    private final long validto;
    private final Identity agent;

}
