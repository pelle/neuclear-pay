package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 12:01:03 PM
 */
public class HeldTransferRequest extends TransferRequest implements Held {
    public HeldTransferRequest(String name, Identity signer, Timestamp timestamp, String digest, Asset asset, Identity to, double amount, Date valuetime, String comment, Date helduntil) throws NeuClearException {
        super(name, signer, timestamp, digest, asset, to, amount, valuetime, comment);
        this.helduntil = helduntil;
    }

    public final Date getHeldUntil() {
        return helduntil;
    }

    private final Date helduntil;
}
