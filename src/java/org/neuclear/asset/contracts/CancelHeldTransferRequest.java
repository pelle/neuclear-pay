package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedObject;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public class CancelHeldTransferRequest extends AssetTransactionContract{

    public CancelHeldTransferRequest(String name, Identity signer, Timestamp timestamp, String digest,
                                       Asset asset, String holdid) throws NeuClearException {
        super(name, signer, timestamp, digest,asset);
        this.holdid=holdid;
    }


    public String getHoldId() {
        return holdid;
    }

    private final String holdid;
}
