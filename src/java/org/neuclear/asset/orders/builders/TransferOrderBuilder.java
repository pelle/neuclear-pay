package org.neuclear.asset.orders.builders;

import org.dom4j.Element;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.Value;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.id.Signatory;
import org.neuclear.id.builders.Builder;

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

$Id: TransferOrderBuilder.java,v 1.5 2004/04/02 23:04:35 pelle Exp $
$Log: TransferOrderBuilder.java,v $
Revision 1.5  2004/04/02 23:04:35  pelle
Got TransferOrder and Builder working with their test cases.
Working on TransferReceipt which is the first embedded receipt. This is causing some problems at the moment.

Revision 1.4  2004/02/18 00:13:30  pelle
Many, many clean ups. I've readded Targets in a new method.
Gotten rid of NamedObjectBuilder and revamped Identity and Resolvers

Revision 1.3  2004/01/13 15:11:17  pelle
Now builds.
Now need to do unit tests

Revision 1.2  2004/01/12 22:39:14  pelle
Completed all the builders and contracts.
Added a new abstract Value class to contain either an amount or a list of serial numbers.
Now ready to finish off the AssetControllers.

Revision 1.1  2004/01/11 00:39:06  pelle
Cleaned up the schemas even more they now all verifiy.
The Order/Receipt pairs for neuclear pay, should now work. They all have Readers using the latest
Schema.
The TransferBuilders are done and the ExchangeBuilders are nearly there.
The new EmbeddedSignedNamedObject builder is useful for creating new Receipts. The new ReceiptBuilder uses
this to create the embedded transaction.
ExchangeOrders now have the concept of BidItem's, you could create an ExchangeOrder bidding on various items at the same time, to be exchanged as one atomic multiparty exchange.
Still doesnt build yet, but very close now ;-)

Revision 1.2  2004/01/10 00:00:44  pelle
Implemented new Schema for Transfer*
Working on it for Exchange*, so far all Receipts are implemented.
Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
Changed SignedNamedObject.getDigest() from byte array to String.
The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.

Revision 1.1  2004/01/05 23:47:09  pelle
Create new Document classification "order", which is really just inherint in the new
package layout.
Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.

Revision 1.9  2004/01/03 20:36:25  pelle
Renamed HeldTransfer to Exchange
Dropped valuetime from the request objects.
Doesnt yet compile. New commit to follow soon.

Revision 1.8  2003/12/06 00:16:10  pelle
Updated various areas in NSTools.
Updated URI Validation in particular to support new expanded format
Updated createUniqueID and friends to be a lot more unique and more efficient.
In CryptoTools updated getRandom() to finally use a SecureRandom.
Changed CryptoTools.getFormatURLSafe to getBase36 because that is what it really is.

Revision 1.7  2003/11/28 00:11:50  pelle
Getting the NeuClear web transactions working.

Revision 1.6  2003/11/21 04:43:03  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.5  2003/11/12 23:47:04  pelle
Much work done in creating good test environment.
PaymentReceiverTest works, but needs a abit more work in its environment to succeed testing.

Revision 1.4  2003/11/11 21:17:19  pelle
Further vital reshuffling.
org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
Did a bit of work on the Canonicalizer and changed a few other minor bits.

Revision 1.3  2003/11/10 17:42:07  pelle
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
 * Time: 3:13:27 PM
 */
public class TransferOrderBuilder extends Builder {
    public TransferOrderBuilder(final Asset asset, final Signatory recipient, final Value amount, final String comment) throws InvalidTransferException, NegativeTransferException, NeuClearException {
        this(asset.getName(), recipient.getName(), amount, comment);
    }

    public TransferOrderBuilder(final String assetname, final String recipient, final Value amount, final String comment) throws InvalidTransferException, NegativeTransferException, NeuClearException {
        super(TransferGlobals.createQName(TransferGlobals.XFER_TAGNAME));
        if (amount.getAmount() < 0)
            throw new NegativeTransferException(amount);
        if (assetname == null)
            throw new InvalidTransferException("assetName");
        if (recipient == null)
            throw new InvalidTransferException("to");

        final Element element = getElement();
        element.add(TransferGlobals.createElement(TransferGlobals.RECIPIENT_TAG, recipient));
        element.add(TransferGlobals.createElement(TransferGlobals.ASSET_TAG, assetname));
        element.add(TransferGlobals.createValueTag(amount));

        if (!Utility.isEmpty(comment))
            element.add(TransferGlobals.createElement(TransferGlobals.COMMENT_TAG, comment));
    }

}
