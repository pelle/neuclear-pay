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
    HeldTransferRequest(final SignedNamedCore core, final Asset asset, final Identity to, final double amount, final Date valuetime, final String comment, final Date helduntil) throws NeuClearException {
        super(core, asset, to, amount, valuetime, comment);
        this.helduntil = helduntil.getTime();
    }

    public final Date getHeldUntil() {
        return new Timestamp(helduntil);
    }

    private final long helduntil;
}
