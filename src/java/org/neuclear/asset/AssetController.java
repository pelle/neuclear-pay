package org.neuclear.asset;

import org.neuclear.asset.contracts.*;
import org.neuclear.asset.contracts.builders.TransferBuilder;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnBalancedTransactionException;
import org.neuclear.ledger.UnknownBookException;

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

$Id: AssetController.java,v 1.3 2003/11/09 03:47:35 pelle Exp $
$Log: AssetController.java,v $
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
Created (Half way) RemoteAssetController, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the RemoteAssetController.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

*/

/**
 * User: pelleb
 * Date: Nov 6, 2003
 * Time: 3:53:17 PM
 */
public abstract class AssetController {

    public final TransferBuilder process(TransferContract contract) throws UnknownBookException, LowlevelLedgerException, UnBalancedTransactionException, InvalidTransactionException, TransferDeniedException{
        if (contract instanceof TransferRequest)
            return processTransfer((TransferRequest)contract);
        if (contract instanceof HeldTransferRequest)
            return processHeldTransfer((HeldTransferRequest)contract);
        return null;//TODO implement for all
    }
    public abstract boolean canProcess(Asset asset);

    public abstract org.neuclear.asset.contracts.builders.TransferReceiptBuilder processTransfer(TransferRequest req) throws UnknownBookException, LowlevelLedgerException, UnBalancedTransactionException, InvalidTransactionException, TransferDeniedException;

    public abstract org.neuclear.asset.contracts.builders.HeldTransferReceiptBuilder processHeldTransfer(HeldTransferRequest req) throws UnknownBookException, LowlevelLedgerException, InvalidTransactionException;

    //TODO implement completeHold and cancelHold contract/builder pairs
    public abstract TransferReceipt processCompleteHold(HeldTransferReceipt hold, Date valuedate, double amount, String comment) throws LowlevelLedgerException, NegativeTransferException, TransferLargerThanHeldException, TransferNotStartedException, ExpiredHeldTransferException, InvalidTransferException;

    public abstract void processCancelHold(HeldTransferReceipt hold) throws LowlevelLedgerException;

}
