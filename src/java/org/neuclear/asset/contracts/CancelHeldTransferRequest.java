package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.SignedNamedCore;

import java.sql.Timestamp;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 5:35:26 PM
 */
public final class CancelHeldTransferRequest extends AssetTransactionContract{

    CancelHeldTransferRequest(final SignedNamedCore core, final Asset asset, final String holdid)  {
        super(core, asset);
        this.holdid = holdid;
    }

    public final String getHoldId() {
        return holdid;
    }

    private final String holdid;
}
