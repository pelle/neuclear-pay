package org.neuclear.asset.contracts.builders;

import org.dom4j.Element;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.TransferGlobals;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.id.Identity;
import org.neuclear.id.NSTools;
import org.neuclear.id.builders.NamedObjectBuilder;

import java.util.Date;

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

$Id: TransferBuilder.java,v 1.8 2003/12/06 00:16:10 pelle Exp $
$Log: TransferBuilder.java,v $
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
public abstract class TransferBuilder extends NamedObjectBuilder {
    protected TransferBuilder(final String tagname, final Asset asset, final Identity signer, final Identity to, final double amount, final Date valuetime, final String comment) throws InvalidTransferException, NegativeTransferException, NeuClearException {
        super(NSTools.createUniqueTransactionID(signer.getName(), to.getName()), TransferGlobals.createQName(tagname));
        if (amount < 0)
            throw new NegativeTransferException(amount);
        if (Utility.isEmpty(asset))
            throw new InvalidTransferException("assetName");
        if (to == null)
            throw new InvalidTransferException("to");
        if (valuetime == null)
            throw new InvalidTransferException("valuetime");

        this.asset = asset;
        final Element element = getElement();
        element.add(TransferGlobals.createAttribute(element, "recipient", to.getName()));
        element.add(TransferGlobals.createAttribute(element, "assetName", asset.getName()));
        element.add(TransferGlobals.createAttribute(element, "amount", Double.toString(amount)));
        element.add(TransferGlobals.createAttribute(element, "valuetime", TimeTools.formatTimeStamp(valuetime)));
        if (!Utility.isEmpty(comment))
            element.add(TransferGlobals.createElement("comment", comment));
    }

    public final Asset getAsset() {
        return asset;
    }

    private final Asset asset;
}
