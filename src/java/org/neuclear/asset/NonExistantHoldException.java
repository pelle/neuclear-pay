package org.neuclear.asset;
/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 10:20:02 AM
 * $Id: NonExistantHoldException.java,v 1.1 2003/11/10 17:42:07 pelle Exp $
 * $Log: NonExistantHoldException.java,v $
 * Revision 1.1  2003/11/10 17:42:07  pelle
 * The AssetController interface has been more or less finalized.
 * CurrencyController fully implemented
 * AssetControlClient implementes a remote client for communicating with AssetControllers
 *
 */
public class NonExistantHoldException extends InvalidTransferException{
    public NonExistantHoldException(String holdid) {
        super("Held transaction:"+holdid+" doesnt exist");
    }
}
