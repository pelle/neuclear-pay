package org.neuclear.asset.remote;

import org.neuclear.asset.*;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.ledger.*;
import org.neuclear.pay.contracts.builders.TransferRequestBuilder;
import org.neudist.crypto.Signer;

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

$Id: RemoteAssetController.java,v 1.1 2003/11/06 23:47:43 pelle Exp $
$Log: RemoteAssetController.java,v $
Revision 1.1  2003/11/06 23:47:43  pelle
Major Refactoring of PaymentProcessor.
Factored out AssetController to be new abstract parent class together with most of its support classes.
Created (Half way) RemoteAssetController, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the RemoteAssetController.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

*/

/**
 * Uses the built in java interface to perform payments on remote services.
 * User: pelleb
 * Date: Nov 6, 2003
 * Time: 5:59:09 PM
 */
public class RemoteAssetController extends AssetController {
    public RemoteAssetController(String asset, Signer signer) throws NeuClearException {
        super(asset);
        this.signer = signer;
        this.asset = NSResolver.resolveIdentity(asset);
    }

    public TransferReceipt processTransfer(TransferRequest req) throws UnknownBookException, LowlevelLedgerException, UnBalancedTransactionException, InvalidTransactionException, TransferDeniedException {
        try {
            if (!signer.canSignFor(req.getFrom().getID()))
                throw new TransferDeniedException(req);
            TransferRequestBuilder builder = new TransferRequestBuilder(
                    req.getFrom().getID(),
                    asset.getName(),
                    req.getTo().getID(),
                    req.getAmount()
            );
            builder.sign(req.getFrom().getID(), signer);

        } catch (org.neudist.xml.xmlsec.XMLSecurityException e) {
            throw new LowlevelLedgerException(e);
        }
        return null;
    }

    public HeldTransferReceipt processHeldTransfer(HeldTransferRequest req) throws UnknownBookException, LowlevelLedgerException, InvalidTransactionException {
        return null;
    }

    public TransferReceipt processCompleteHold(HeldTransferReceipt hold, Date valuedate, double amount, String comment) throws LowlevelLedgerException, NegativeTransferException, TransferLargerThanHeldException, TransferNotStartedException, ExpiredHeldTransferException {
        return null;
    }

    public void processCancelHold(HeldTransferReceipt hold) throws LowlevelLedgerException {

    }

    public Account getAccount(String id) throws UnknownBookException, LowlevelLedgerException {

        return null;
    }

    public Account createAccount(String id, String title) throws BookExistsException, LowlevelLedgerException {
        return null;
    }

    public Issuer getIssuer() {
        return null;
    }

    private final Signer signer;
    private final Identity asset;
}
