package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:37:10 PM
 */
public class TransferReceipt extends TransferContract {
    private final Identity from;
    private final String reqid;

    TransferReceipt(String name, Identity signer, Timestamp timestamp, String digest, Asset asset, Identity from, Identity to, String reqid, double amount, Date valuetime, String comment) throws NeuClearException {
        super(name, signer, timestamp, digest, asset, to, amount, valuetime, comment);
        this.from = from;
        this.reqid = reqid;
    }


    public final Identity getFrom() {
        return from;
    }

    public String getRequestId() {
        return reqid;
    }

}
