package org.neuclear.asset;

import org.neuclear.asset.contracts.TransferRequest;
import org.neuclear.asset.contracts.CancelHeldTransferRequest;
import org.neuclear.asset.contracts.AssetTransactionContract;
import org.neuclear.id.SignedNamedObject;

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

$Id: TransferDeniedException.java,v 1.3 2003/11/10 17:42:07 pelle Exp $
$Log: TransferDeniedException.java,v $
Revision 1.3  2003/11/10 17:42:07  pelle
The AssetController interface has been more or less finalized.
CurrencyController fully implemented
AssetControlClient implementes a remote client for communicating with AssetControllers

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
 * User: pelleb
 * Date: Nov 6, 2003
 * Time: 6:18:11 PM
 */
public class TransferDeniedException extends TransferException {
    public TransferDeniedException(AssetTransactionContract req) {
        this.req = req;
    }
    public TransferDeniedException(CancelHeldTransferRequest req) {
        this.req = req;
    }

    public String getSubMessage() {
        return "No permission to perform action for :"+req.getSignatory().getName();
    }

    private final SignedNamedObject req;

}
