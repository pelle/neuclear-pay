package org.neuclear.asset;

import org.neuclear.asset.contracts.*;
import org.neuclear.asset.contracts.builders.TransferBuilder;
import org.neuclear.asset.contracts.builders.CancelHeldTransferReceiptBuilder;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnBalancedTransactionException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.id.builders.NamedObjectBuilder;
import org.neuclear.id.SignedNamedObject;

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

$Id: AssetController.java,v 1.5 2003/11/10 19:27:53 pelle Exp $
$Log: AssetController.java,v $
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
 *
 * This abstract class generalizes all actions that an AssetController must implement to manage Assets according to the
 * <a href="http://neuclear.org">NeuClear</a> model.
 * User: pelleb
 * Date: Nov 6, 2003
 * Time: 3:53:17 PM
 */
public abstract class AssetController {

    /**
     * Process the the request and returns and unsigned object for signing and sending.
     * @param contract
     * @return
     * @throws TransferDeniedException
     * @throws LowLevelPaymentException
     * @throws InvalidTransferException
     */
    public final NamedObjectBuilder process(AssetTransactionContract contract) throws  TransferDeniedException,LowLevelPaymentException, InvalidTransferException{
        if (contract instanceof TransferRequest)
            return processTransfer((TransferRequest)contract);
        if (contract instanceof HeldTransferRequest)
            return processHeldTransfer((HeldTransferRequest)contract);
        if (contract instanceof CompleteHeldTransferRequest)
            return processCompleteHold((CompleteHeldTransferRequest)contract);
        if (contract instanceof CancelHeldTransferRequest)
            return processCancelHold((CancelHeldTransferRequest) contract);

        return null;
    }

    /**
     * Verify that the asset controller handles the given asset
     * @param asset
     * @return true if able to process
     */
    public abstract boolean canProcess(Asset asset);


    /**
     * Performs an asset transfer.
     * @param req TransferRequest
     * @return Unsigned Receipt
     * @throws LowLevelPaymentException
     * @throws TransferDeniedException
     * @throws InvalidTransferException
     */
    public abstract org.neuclear.asset.contracts.builders.TransferReceiptBuilder processTransfer(TransferRequest req) throws LowLevelPaymentException, TransferDeniedException, InvalidTransferException;

    /**
     * Creates a HeldTransfer. This gives the recipient right within a limited period to "complete" the Transfer.
     * Completion means performing the actual transfer with an amount up to but not greater than the amount set in the
     * HeldTransfer Object.
     * @param req Valid HeldTransferRequest
     * @return Unsigned HeldTransferReceiptBuilder
     * @throws LowLevelPaymentException
     * @throws TransferDeniedException
     * @throws InvalidTransferException
     */
    public abstract org.neuclear.asset.contracts.builders.HeldTransferReceiptBuilder processHeldTransfer(HeldTransferRequest req) throws LowLevelPaymentException, TransferDeniedException, InvalidTransferException;

    /**
     * Completes a HeldTransfer. This must be signed by the recipient of the HeldTransfer.
     * @param complete
     * @return Unsigned TransferReceiptBuilder
     * @throws LowLevelPaymentException
     * @throws TransferDeniedException
     * @throws InvalidTransferException
     */
    public abstract org.neuclear.asset.contracts.builders.TransferReceiptBuilder processCompleteHold(CompleteHeldTransferRequest complete) throws LowLevelPaymentException, TransferDeniedException, InvalidTransferException;

    /**
     * Cancels a HeldTransfer. This must be signed by the recipient of the HeldTransfer.
     * @param cancel
     * @return Unsigned CancelHeldTransferReceiptBuilder
     * @throws LowLevelPaymentException
     * @throws TransferDeniedException
     * @throws InvalidTransferException
     */

    public abstract CancelHeldTransferReceiptBuilder processCancelHold(CancelHeldTransferRequest cancel) throws LowLevelPaymentException, TransferDeniedException, InvalidTransferException;

    //TODO Add getBalance
}
