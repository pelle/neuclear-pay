package org.neuclear.asset.contracts.builders;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.TransferGlobals;
import org.neuclear.asset.contracts.HeldTransferReceipt;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.id.Identity;
import org.neuclear.id.NSTools;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 10:46:12 AM
 * $Id: CancelHeldTransferRequestBuilder.java,v 1.1 2003/11/10 17:42:07 pelle Exp $
 * $Log: CancelHeldTransferRequestBuilder.java,v $
 * Revision 1.1  2003/11/10 17:42:07  pelle
 * The AssetController interface has been more or less finalized.
 * CurrencyController fully implemented
 * AssetControlClient implementes a remote client for communicating with AssetControllers
 *
 */
public class CancelHeldTransferRequestBuilder extends CancelHeldTransferBuilder {
    public CancelHeldTransferRequestBuilder(HeldTransferReceipt held) throws InvalidTransferException, NegativeTransferException {
        super(NSTools.createUniqueNamedID(held.getTo().getName(),held.getAsset().getName()),TransferGlobals.CANCEL_TAGNAME, held.getAsset(),held.getName());
    }

}
