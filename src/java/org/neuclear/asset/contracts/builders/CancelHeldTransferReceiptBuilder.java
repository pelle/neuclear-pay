package org.neuclear.asset.contracts.builders;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.contracts.CancelHeldTransferRequest;
import org.neuclear.asset.contracts.TransferGlobals;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.NSTools;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 10:46:12 AM
 * $Id: CancelHeldTransferReceiptBuilder.java,v 1.3 2003/12/06 00:16:10 pelle Exp $
 * $Log: CancelHeldTransferReceiptBuilder.java,v $
 * Revision 1.3  2003/12/06 00:16:10  pelle
 * Updated various areas in NSTools.
 * Updated URI Validation in particular to support new expanded format
 * Updated createUniqueID and friends to be a lot more unique and more efficient.
 * In CryptoTools updated getRandom() to finally use a SecureRandom.
 * Changed CryptoTools.getFormatURLSafe to getBase36 because that is what it really is.
 *
 * Revision 1.2  2003/11/21 04:43:03  pelle
 * EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
 * Otherwise You will Finaliate.
 * Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
 * This should hopefully make everything more stable (and secure).
 * <p/>
 * Revision 1.1  2003/11/10 17:42:07  pelle
 * The AssetController interface has been more or less finalized.
 * CurrencyController fully implemented
 * AssetControlClient implementes a remote client for communicating with AssetControllers
 */
public final class CancelHeldTransferReceiptBuilder extends CancelHeldTransferBuilder {
    public CancelHeldTransferReceiptBuilder(final CancelHeldTransferRequest req) throws InvalidTransferException, NegativeTransferException, NeuClearException {
        super(NSTools.createUniqueTransactionID(req.getAsset().getName(), req.getSignatory().getName()), TransferGlobals.CANCEL_RCPT_TAGNAME, req.getAsset(), req.getHoldId());
    }

}
