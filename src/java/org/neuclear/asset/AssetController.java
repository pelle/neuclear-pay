package org.neuclear.asset;

import org.neuclear.ledger.*;

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

$Id: AssetController.java,v 1.1 2003/11/06 23:47:43 pelle Exp $
$Log: AssetController.java,v $
Revision 1.1  2003/11/06 23:47:43  pelle
Major Refactoring of PaymentProcessor.
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
    protected AssetController(String title) {
        this.title = title;
    }

    public abstract TransferReceipt processTransfer(TransferRequest req) throws UnknownBookException, LowlevelLedgerException, UnBalancedTransactionException, InvalidTransactionException, TransferDeniedException;

    public abstract HeldTransferReceipt processHeldTransfer(HeldTransferRequest req) throws UnknownBookException, LowlevelLedgerException, InvalidTransactionException;

    public abstract TransferReceipt processCompleteHold(HeldTransferReceipt hold, Date valuedate, double amount, String comment) throws LowlevelLedgerException, NegativeTransferException, TransferLargerThanHeldException, TransferNotStartedException, ExpiredHeldTransferException;

    public abstract void processCancelHold(HeldTransferReceipt hold) throws LowlevelLedgerException;

    public abstract Account getAccount(String id) throws UnknownBookException, LowlevelLedgerException;

    public abstract Account createAccount(String id, String title) throws BookExistsException, LowlevelLedgerException;

    public String getTitle() {
        return title;
    }

    protected final TransferReceipt createTransferReceipt(TransferRequest req, String id) {
        return new TransferReceipt(this, req, id);
    }

    protected final HeldTransferReceipt createHeldTransferReceipt(HeldTransferRequest req, String id) {
        return new HeldTransferReceipt(this, req, id);
    }

    public abstract Issuer getIssuer();

    protected final String title;
}
