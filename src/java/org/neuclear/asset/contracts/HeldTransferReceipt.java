package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 30, 2003
 * Time: 11:59:36 AM
 */
public class HeldTransferReceipt extends TransferReceipt implements Held {

    public HeldTransferReceipt(String name, Identity signer, Timestamp timestamp, String digest, Asset asset, Identity from, Identity to, String reqid, double amount, Date valuetime, String comment, Date helduntil) throws NeuClearException {
        super(name, signer, timestamp, digest, asset, from, to, reqid, amount, valuetime, comment);
        this.helduntil = helduntil;
    }

    public final Date getHeldUntil() {
        return helduntil;
    }

    private final Date helduntil;
}
