package org.neuclear.asset;

import org.neuclear.asset.contracts.*;
import org.neuclear.exchange.orders.builders.CancelExchangeReceiptBuilder;
import org.neuclear.asset.orders.exchanges.ExchangeOrder;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.builders.NamedObjectBuilder;
import org.neuclear.exchange.orders.CancelExchangeOrder;
import org.neuclear.exchange.orders.ExchangeCompletionOrder;
import org.neuclear.exchange.orders.*;
import org.neuclear.exchange.orders.builders.CancelExchangeReceiptBuilder;

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

$Id: AssetController.java,v 1.10 2004/01/05 23:47:10 pelle Exp $
$Log: AssetController.java,v $
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
public abstract class AssetController {

    /**
     * Process the the request and returns and unsigned object for signing and sending.
     * 
     * @param contract 
     * @return 
     * @throws TransferDeniedException  
     * @throws LowLevelPaymentException 
     * @throws InvalidTransferException 
     */
    public final NamedObjectBuilder process(final AssetTransactionContract contract) throws TransferDeniedException, LowLevelPaymentException, InvalidTransferException, NeuClearException {
        if (contract instanceof TransferOrder)
            return process((TransferOrder) contract);
        if (contract instanceof org.neuclear.exchange.orders.ExchangeOrder)
            return process((ExchangeOrder) contract);
        if (contract instanceof ExchangeCompletionOrder)
            return process((ExchangeCompletionOrder) contract);
        if (contract instanceof CancelExchangeOrder)
            return process((CancelExchangeOrder) contract);

        return null;
    }

    /**
     * Verify that the assetName controller handles the given assetName
     * 
     * @param asset 
     * @return true if able to process
     */
    public abstract boolean canProcess(Asset asset);


    /**
     * Performs an assetName transfer.
     * 
     * @param req TransferOrder
     * @return Unsigned Receipt
     * @throws LowLevelPaymentException 
     * @throws TransferDeniedException  
     * @throws InvalidTransferException 
     */
    public abstract org.neuclear.asset.orders.builders.TransferReceiptBuilder process(TransferOrder req) throws LowLevelPaymentException, TransferDeniedException, InvalidTransferException, NeuClearException;

    /**
     * Creates a HeldTransfer. This gives the recipient right within a limited period to "complete" the Transfer.
     * Completion means performing the actual transfer with an amount up to but not greater than the amount set in the
     * HeldTransfer Object.
     * 
     * @param req Valid ExchangeOrder
     * @return Unsigned ExchangeReceiptBuilder
     * @throws LowLevelPaymentException 
     * @throws TransferDeniedException  
     * @throws InvalidTransferException 
     */
    public abstract org.neuclear.exchange.orders.builders.ExchangeReceiptBuilder process(ExchangeOrder req) throws LowLevelPaymentException, TransferDeniedException, InvalidTransferException, NeuClearException;

    /**
     * Completes a HeldTransfer. This must be signed by the recipient of the HeldTransfer.
     * 
     * @param complete 
     * @return Unsigned TransferReceiptBuilder
     * @throws LowLevelPaymentException 
     * @throws TransferDeniedException  
     * @throws InvalidTransferException 
     */
    public abstract org.neuclear.asset.orders.builders.TransferReceiptBuilder process(ExchangeCompletionOrder complete) throws LowLevelPaymentException, TransferDeniedException, InvalidTransferException, NeuClearException;

    /**
     * Cancels a HeldTransfer. This must be signed by the recipient of the HeldTransfer.
     * 
     * @param cancel 
     * @return Unsigned CancelExchangeReceiptBuilder
     * @throws LowLevelPaymentException 
     * @throws TransferDeniedException  
     * @throws InvalidTransferException 
     */

    public abstract CancelExchangeReceiptBuilder process(CancelExchangeOrder cancel) throws LowLevelPaymentException, TransferDeniedException, InvalidTransferException, NeuClearException;



    //TODO Add getBalance
}
