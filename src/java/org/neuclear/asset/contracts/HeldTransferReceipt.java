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
public final class HeldTransferReceipt extends TransferReceipt implements Held {

    HeldTransferReceipt(final SignedNamedCore core, final Asset asset, final Identity from, final Identity to, final String reqid, final double amount, final Date valuetime, final String comment, final Date helduntil)  {
        super(core, asset, from, to, reqid, amount, valuetime, comment);
        this.helduntil = helduntil.getTime();
    }

    public final Date getHeldUntil() {
        return new Timestamp(helduntil);
    }

    private final long helduntil;
}
