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

    HeldTransferReceipt(SignedNamedCore core, Asset asset, Identity from, Identity to, String reqid, double amount, Date valuetime, String comment, Date helduntil) throws NeuClearException {
        super(core, asset, from, to, reqid, amount, valuetime, comment);
        this.helduntil = helduntil;
    }

    public final Date getHeldUntil() {
        return helduntil;
    }

    private final Date helduntil;
}
