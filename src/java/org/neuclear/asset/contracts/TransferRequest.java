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
public class TransferRequest extends TransferContract {
    private final Identity to;

    TransferRequest(final SignedNamedCore core, final Asset asset, final Identity to, final double amount, final String comment)  {
        super(core, asset,  amount,  comment);
        this.to=to;
    }

    public final Identity getFrom() {
        return getSignatory();
    }

    public final Identity getTo() {
        return to;
    }


}
