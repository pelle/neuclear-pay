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
public final class CompleteHeldTransferRequest extends TransferContract {
    CompleteHeldTransferRequest(final SignedNamedCore core, final Asset asset, final Identity from, final Identity to, final double amount, final Date valuetime, final String comment, final String holdid) throws NeuClearException {
        super(core, asset, to, amount, valuetime, comment);
        this.from = from;
        this.holdid = holdid;
    }

    public final Identity getFrom() {
        return from;
    }

    public final String getHoldId() {
        return holdid;
    }

    private final Identity from;
    private final String holdid;
}
