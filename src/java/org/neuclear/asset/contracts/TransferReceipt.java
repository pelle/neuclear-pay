package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedCore;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:37:10 PM
 */
public class TransferReceipt extends TransferContract {

    TransferReceipt(final SignedNamedCore core, final Asset asset, final Identity from, final Identity to, final String reqid, final double amount, final Date valuetime, final String comment)  {
        super(core, asset,  amount, comment);
        this.from = from;
        this.reqid = reqid;
        this.to=to;
        this.valuetime=valuetime.getTime();
    }

    public final Identity getFrom() {
        return from;
    }

    public final String getRequestId() {
        return reqid;
    }

    public final Identity getTo() {
        return to;
    }
    public final Date getValueTime() {
        return new Timestamp(valuetime);
    }
    private final long valuetime;
    private final Identity from;
    private final String reqid;
    private final Identity to;

}
