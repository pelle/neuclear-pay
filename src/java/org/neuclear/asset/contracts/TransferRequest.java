package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public class TransferRequest extends TransferContract {

    TransferRequest(String name, Identity signer, Timestamp timestamp, String digest, Asset asset, Identity to, double amount, Date valuetime, String comment) throws NeuClearException {
        super(name, signer, timestamp, digest, asset, to, amount, valuetime, comment);
    }


    public final Identity getFrom() {
        return getSignatory();
    }


}
