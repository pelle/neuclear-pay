package org.neuclear.asset;

import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.orders.CancelExchangeOrder;
import org.neuclear.exchange.orders.ExchangeCompletionOrder;
import org.neuclear.exchange.orders.ExchangeOrder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.id.receiver.UnsupportedTransaction;
import org.neuclear.ledger.Ledger;

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

$Id: Auditor.java,v 1.1 2004/04/01 23:18:33 pelle Exp $
$Log: Auditor.java,v $
Revision 1.1  2004/04/01 23:18:33  pelle
Split Identity into Signatory and Identity class.
Identity remains a signed named object and will in the future just be used for self declared information.
Signatory now contains the PublicKey etc and is NOT a signed object.

Revision 1.14  2004/03/02 18:58:35  pelle
Further cleanups in neuclear-id. Moved everything under id.

Revision 1.13  2004/01/13 15:11:17  pelle
Now builds.
Now need to do unit tests

Revision 1.12  2004/01/12 22:39:14  pelle
Completed all the builders and contracts.
Added a new abstract Value class to contain either an amount or a list of serial numbers.
Now ready to finish off the AssetControllers.

Revision 1.11  2004/01/10 00:00:45  pelle
Implemented new Schema for Transfer*
Working on it for Exchange*, so far all Receipts are implemented.
Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
Changed SignedNamedObject.getDigest() from byte array to String.
The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.

Revision 1.10  2004/01/05 23:47:10  pelle
Create new Document classification "order", which is really just inherint in the new
package layout.
Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.

Revision 1.9  2004/01/03 20:36:25  pelle
Renamed HeldTransfer to Exchange
Dropped valuetime from the request objects.
Doesnt yet compile. New commit to follow soon.

Revision 1.8  2003/11/22 00:22:29  pelle
All unit tests in commons, id and xmlsec now work.
AssetController now successfully processes payments in the unit test.
Payment Web App has working form that creates a TransferOrder presents it to the signer
and forwards it to AssetControlServlet. (Which throws an XML Parser Exception) I think the XMLReaderServlet is bust.

Revision 1.7  2003/11/21 04:43:04  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.6  2003/11/12 23:47:05  pelle
Much work done in creating good test environment.
PaymentReceiverTest works, but needs a abit more work in its environment to succeed testing.

Revision 1.5  2003/11/10 19:27:53  pelle
Mainly documentation.

Revision 1.4  2003/11/10 17:42:07  pelle
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
TransferOrder/Receipt and its Exchange companions are now SignedNamedObjects. Thus to create them you must use
their matching TransferOrder/ReceiptBuilder classes.
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
 * This abstract class generalizes all actions that an AssetController must implement to manage Assets according to the
 * <a href="http://neuclear.org">NeuClear</a> model.
 * User: pelleb
 * Date: Nov 6, 2003
 * Time: 3:53:17 PM
 */
public class Auditor implements Receiver {
    public Auditor(Ledger ledger) {
        this.ledger = ledger;
    }

    /**
     * Process the the request and returns and unsigned object for signing and sending.
     * 
     * @param contract 
     * @return 
     */
    public final SignedNamedObject receive(final SignedNamedObject contract) throws UnsupportedTransaction, NeuClearException {
        if (contract instanceof TransferOrder) {
            TransferOrder order = (TransferOrder) contract;
        } else if (contract instanceof ExchangeOrder)

            if (contract instanceof ExchangeCompletionOrder)

                if (contract instanceof CancelExchangeOrder) ;


        return null;
    }

    private final Ledger ledger;

}
