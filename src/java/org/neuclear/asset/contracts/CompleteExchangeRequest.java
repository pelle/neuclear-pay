package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedCore;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public final class CompleteExchangeRequest extends TransferContract {

    CompleteExchangeRequest(final SignedNamedCore core, final Asset asset, final Identity from, final Identity to, final double amount, final Date valuetime, final String comment, final String holdid)  {
        super(core, asset, amount,  comment);
        this.from = from;
        this.holdid = holdid;
        this.to = to;
        this.valuetime=valuetime.getTime();
    }

    public final Identity getFrom() {
        return from;
    }

    public final String getHoldId() {
        return holdid;
    }
    public final Identity getTo() {
        return to;
    }
    public final Date getValueTime() {
        return new Timestamp(valuetime);
    }
    private final long valuetime;

    private final Identity from;
    private final String holdid;
    private final Identity to;

}
