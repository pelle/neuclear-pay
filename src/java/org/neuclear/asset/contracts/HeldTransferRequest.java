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
public final class HeldTransferRequest extends TransferRequest implements Held {
    HeldTransferRequest(SignedNamedCore core, Asset asset, Identity to, double amount, Date valuetime, String comment, Date helduntil) throws NeuClearException {
        super(core, asset, to, amount, valuetime, comment);
        this.helduntil = helduntil;
    }

    public final Date getHeldUntil() {
        return helduntil;
    }

    private final Date helduntil;
}
