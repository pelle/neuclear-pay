package org.neuclear.asset.remote;

import org.neuclear.asset.contracts.CancelHeldTransferReceipt;
import org.neuclear.asset.contracts.HeldTransferReceipt;
import org.neuclear.asset.contracts.TransferReceipt;
import org.neuclear.asset.contracts.builders.*;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.xml.XMLException;

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

$Id: AssetControlClient.java,v 1.7 2003/11/21 04:43:04 pelle Exp $
$Log: AssetControlClient.java,v $
Revision 1.7  2003/11/21 04:43:04  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.6  2003/11/19 23:32:20  pelle
Signers now can generatekeys via the generateKey() method.
Refactored the relationship between SignedNamedObject and NamedObjectBuilder a bit.
SignedNamedObject now contains the full xml which is returned with getEncoded()
This means that it is now possible to further send on or process a SignedNamedObject, leaving
NamedObjectBuilder for its original purposes of purely generating new Contracts.
NamedObjectBuilder.sign() now returns a SignedNamedObject which is the prefered way of processing it.
Updated all major interfaces that used the old model to use the new model.

Revision 1.5  2003/11/12 23:47:05  pelle
Much work done in creating good test environment.
PaymentReceiverTest works, but needs a abit more work in its environment to succeed testing.

Revision 1.4  2003/11/11 21:17:19  pelle
Further vital reshuffling.
org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
Did a bit of work on the Canonicalizer and changed a few other minor bits.

Revision 1.3  2003/11/10 21:08:41  pelle
More JavaDoc

Revision 1.2  2003/11/10 19:27:53  pelle
Mainly documentation.

Revision 1.1  2003/11/10 17:42:07  pelle
The AssetController interface has been more or less finalized.
CurrencyController fully implemented
AssetControlClient implementes a remote client for communicating with AssetControllers

Revision 1.3  2003/11/09 03:47:35  pelle
AssetController has now got a single process(transaction) method, which calls its abstract methods.
The AssetControllerReceiver uses this and is at once massively simplified.

Revision 1.2  2003/11/08 01:39:58  pelle
WARNING this rev is majorly unstable and will almost certainly not compile.
More major refactoring in neuclear-pay.
Got rid of neuclear-ledger like features of pay such as Account and Issuer.
Accounts have been replaced by Identity from neuclear-id
Issuer is now Asset which is a subclass of Identity
AssetController supports more than one Asset. Which is important for most non ecurrency implementations.
TransferRequest/Receipt and its Held companions are now SignedNamedObjects. Thus to create them you must use
their matching TransferRequest/ReceiptBuilder classes.
PaymentProcessor has been renamed CurrencyController. I will extract a superclass later to be named AbstractLedgerController
which will handle all neuclear-ledger based AssetControllers.

Revision 1.1  2003/11/06 23:47:43  pelle
Major Refactoring of CurrencyController.
Factored out AssetController to be new abstract parent class together with most of its support classes.
Created (Half way) AssetControlClient, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the AssetControlClient.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

*/

/**
 * This client can be used to perform all the major Asset Transfer functionality using the Assets remote assetName server.
 */
public final class AssetControlClient {
    public AssetControlClient(final Signer signer) throws NeuClearException {
        this.signer = signer;
    }

    public final TransferReceipt performTransfer(final TransferRequestBuilder req) throws NeuClearException, XMLException {
        return (TransferReceipt) send(req);
    }


    public final HeldTransferReceipt performHeldTransfer(final HeldTransferRequestBuilder req) throws NeuClearException, XMLException {
        return (HeldTransferReceipt) send(req);
    }

    public final TransferReceipt performCompleteHeld(final CompleteHeldTransferRequestBuilder req) throws NeuClearException, XMLException {
        return (TransferReceipt) send(req);
    }

    public final CancelHeldTransferReceipt performCancelHeld(final CancelHeldTransferRequestBuilder req) throws NeuClearException, XMLException {
        return (CancelHeldTransferReceipt) req.getAsset().send(req.sign(signer));
    }

    private SignedNamedObject send(final TransferBuilder req) throws NeuClearException, XMLException {
        return req.getAsset().send(req.sign(signer));
    }

    private final Signer signer;
}
