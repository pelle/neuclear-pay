package org.neuclear.asset;
/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 10:20:02 AM
 * $Id: NonExistantHoldException.java,v 1.3 2004/01/03 20:36:26 pelle Exp $
 * $Log: NonExistantHoldException.java,v $
 * Revision 1.3  2004/01/03 20:36:26  pelle
 * Renamed HeldTransfer to Exchange
 * Dropped valuetime from the request objects.
 * Doesnt yet compile. New commit to follow soon.
 *
 * Revision 1.2  2003/11/21 04:43:04  pelle
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
public final class NonExistantHoldException extends InvalidTransferException{
    public NonExistantHoldException(final String holdid) {
        super("Exchange transaction:"+holdid+" doesnt exist");
    }
}
