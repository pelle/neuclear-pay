package org.neuclear.asset.contracts.builders;

import org.dom4j.Element;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.contracts.HeldTransferRequest;
import org.neuclear.asset.contracts.TransferGlobals;
import org.neuclear.time.TimeTools;

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

$Id: HeldTransferReceiptBuilder.java,v 1.1 2003/11/08 01:39:57 pelle Exp $
$Log: HeldTransferReceiptBuilder.java,v $
Revision 1.1  2003/11/08 01:39:57  pelle
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

*/

/**
 * User: pelleb
 * Date: Nov 7, 2003
 * Time: 8:05:15 PM
 */
public class HeldTransferReceiptBuilder extends TransferReceiptBuilder {
    public HeldTransferReceiptBuilder(HeldTransferRequest req, String id) throws InvalidTransferException, NegativeTransferException {
        super(TransferGlobals.HELD_XFER_RCPT_TAGNAME, req, id);
        Element element = getElement();
        element.add(TransferGlobals.createAttribute(element, "helduntil", TimeTools.formatTimeStamp(req.getHeldUntil())));

    }
}
