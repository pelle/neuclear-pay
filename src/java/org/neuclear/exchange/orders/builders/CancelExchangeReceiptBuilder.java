package org.neuclear.exchange.orders.builders;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.builders.ReceiptBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.orders.CancelExchangeOrder;
import org.neuclear.exchange.orders.ExchangeGlobals;
import org.neuclear.id.NSTools;

import java.util.Date;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 10:46:12 AM
 * $Id: CancelExchangeReceiptBuilder.java,v 1.4 2004/01/13 15:11:17 pelle Exp $
 * $Log: CancelExchangeReceiptBuilder.java,v $
 * Revision 1.4  2004/01/13 15:11:17  pelle
 * Now builds.
 * Now need to do unit tests
 *
 * Revision 1.3  2004/01/12 22:39:14  pelle
 * Completed all the builders and contracts.
 * Added a new abstract Value class to contain either an amount or a list of serial numbers.
 * Now ready to finish off the AssetControllers.
 *
 * Revision 1.2  2004/01/10 00:00:46  pelle
 * Implemented new Schema for Transfer*
 * Working on it for Exchange*, so far all Receipts are implemented.
 * Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
 * Changed SignedNamedObject.getDigest() from byte array to String.
 * The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.
 *
 * Revision 1.1  2004/01/05 23:47:10  pelle
 * Create new Document classification "order", which is really just inherint in the new
 * package layout.
 * Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.
 *
 * Revision 1.1  2004/01/03 20:36:25  pelle
 * Renamed HeldTransfer to Exchange
 * Dropped valuetime from the request objects.
 * Doesnt yet compile. New commit to follow soon.
 *
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
public final class CancelExchangeReceiptBuilder extends ReceiptBuilder {
    public CancelExchangeReceiptBuilder(final CancelExchangeOrder req,Date time) throws InvalidTransferException, NegativeTransferException, NeuClearException {
        super(ExchangeGlobals.createQName(ExchangeGlobals.CANCEL_RCPT_TAGNAME), req,time);
    }

}
