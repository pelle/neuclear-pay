package org.neuclear.asset.contracts.builders;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.TransferGlobals;
import org.neuclear.asset.contracts.HeldTransferReceipt;
import org.neuclear.asset.contracts.CancelHeldTransferRequest;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.id.Identity;
import org.neuclear.id.NSTools;
import org.neuclear.commons.NeuClearException;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 10:46:12 AM
 * $Id: CancelHeldTransferReceiptBuilder.java,v 1.2 2003/11/21 04:43:03 pelle Exp $
 * $Log: CancelHeldTransferReceiptBuilder.java,v $
 * Revision 1.2  2003/11/21 04:43:03  pelle
 * EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
 * Otherwise You will Finaliate.
 * Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
 * This should hopefully make everything more stable (and secure).
 *
 * Revision 1.1  2003/11/10 17:42:07  pelle
 * The AssetController interface has been more or less finalized.
 * CurrencyController fully implemented
 * AssetControlClient implementes a remote client for communicating with AssetControllers
 *
 */
public final class CancelHeldTransferReceiptBuilder extends CancelHeldTransferBuilder {
    public CancelHeldTransferReceiptBuilder(final CancelHeldTransferRequest req) throws InvalidTransferException, NegativeTransferException, NeuClearException {
        super(NSTools.createUniqueNamedID(req.getAsset().getName(),req.getSignatory().getName()),TransferGlobals.CANCEL_RCPT_TAGNAME, req.getAsset(),req.getHoldId());
    }

}
