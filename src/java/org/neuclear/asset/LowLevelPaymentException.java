package org.neuclear.asset;
/**
 * This is used to encapsulate lowerlevel errors such as database errors, which we cant possibly deal
 * with at this level.
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 10:01:35 AM
 * $Id: LowLevelPaymentException.java,v 1.2 2003/11/21 04:43:04 pelle Exp $
 * $Log: LowLevelPaymentException.java,v $
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
public final class LowLevelPaymentException extends TransferException{
    public LowLevelPaymentException(final Throwable e) {
        super(e);
    }

    public final String getSubMessage() {
        return "Caused by: "+getCause().getLocalizedMessage();
    }


}
