package org.neuclear.exchange.orders.builders;

import org.dom4j.Element;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.orders.builders.TransferBuilder;
import org.neuclear.asset.orders.transfers.TransferGlobals;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.id.Identity;

/*
NeuClear Distributed Transaction Clearing Platform
(C) 2003 Pelle Braendgaard

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

$Id: CompleteExchangeRequestBuilder.java,v 1.2 2004/01/10 00:00:46 pelle Exp $
$Log: CompleteExchangeRequestBuilder.java,v $
Revision 1.2  2004/01/10 00:00:46  pelle
Implemented new Schema for Transfer*
Working on it for Exchange*, so far all Receipts are implemented.
Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
Changed SignedNamedObject.getDigest() from byte array to String.
The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.

Revision 1.1  2004/01/05 23:47:10  pelle
Create new Document classification "order", which is really just inherint in the new
package layout.
Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.

Revision 1.1  2004/01/03 20:36:25  pelle
Renamed HeldTransfer to Exchange
Dropped valuetime from the request objects.
Doesnt yet compile. New commit to follow soon.

Revision 1.2  2003/11/21 04:43:03  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.1  2003/11/10 17:42:07  pelle
The AssetController interface has been more or less finalized.
CurrencyController fully implemented
AssetControlClient implementes a remote client for communicating with AssetControllers

Revision 1.2  2003/11/09 03:26:47  pelle
More house keeping and shuffling about mainly pay

Revision 1.1  2003/11/09 03:10:13  pelle
Major changes that apparently didnt get properly checked in earlier.

Revision 1.2  2003/11/06 23:47:43  pelle
Major Refactoring of CurrencyController.
Factored out AssetController to be new abstract parent class together with most of its support classes.
Created (Half way) AssetControlClient, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the AssetControlClient.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

Revision 1.1  2003/10/03 23:48:29  pelle
Did various security related updates in the pay package with regards to immutability of fields etc.
AssetControllerReceiver should now be operational. Real testing needs to be done including in particular setting the
private key of the Receiver.
A new class TransferGlobals contains usefull settings for making life easier in the other contract based classes.
TransferContract the signed contract is functional and has a matching TransferRequestBuilder class for programmatically creating
TransferRequests for signing.
TransferReceiptBuilder has been created for use by Transfer processors. It is used in the AssetControllerReceiver.

*/

/**
 * User: pelleb
 * Date: Oct 3, 2003
 * Time: 6:28:26 PM
 */
public final class CompleteExchangeRequestBuilder extends TransferBuilder {
    public CompleteExchangeRequestBuilder(final ExchangeOrderReceipt req,final Identity signatory,final String id,final double amount, final String comment) throws InvalidTransferException, NegativeTransferException, NeuClearException {
        super(TransferGlobals.COMPLETE_TAGNAME,
                req.getAsset(),
                signatory,
                req.getAgent(),
                amount,
                comment);
        final Element element = getElement();
        element.add(TransferGlobals.createAttribute(element, "sender", req.getFrom().getName()));
        element.add(TransferGlobals.createAttribute(element, "holdid", req.getName()));
    }

}
