package org.neuclear.exchange.orders.builders;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.exchange.orders.builders.CancelExchangeBuilder;
import org.neuclear.asset.orders.transfers.TransferGlobals;
import org.neuclear.asset.orders.transfers.TransferGlobals;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.NSTools;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 10:46:12 AM
 * $Id: CancelExchangeRequestBuilder.java,v 1.1 2004/01/05 23:47:10 pelle Exp $
 * $Log: CancelExchangeRequestBuilder.java,v $
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
public final class CancelExchangeRequestBuilder extends CancelExchangeBuilder {
    public CancelExchangeRequestBuilder(final ExchangeOrderReceipt held) throws InvalidTransferException, NegativeTransferException, NeuClearException {
        super(NSTools.createUniqueTransactionID(held.getAgent().getName(), held.getAsset().getName()), TransferGlobals.CANCEL_TAGNAME, held.getAsset(), held.getName());
    }

}