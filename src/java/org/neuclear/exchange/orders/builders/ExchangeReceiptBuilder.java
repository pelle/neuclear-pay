package org.neuclear.exchange.orders.builders;

import org.dom4j.Element;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.orders.builders.TransferBuilder;
import org.neuclear.asset.orders.exchanges.ExchangeOrder;
import org.neuclear.asset.orders.transfers.TransferGlobals;
import org.neuclear.asset.orders.transfers.TransferGlobals;
import org.neuclear.asset.orders.builders.TransferBuilder;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.commons.NeuClearException;

import java.sql.Timestamp;

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

$Id: ExchangeReceiptBuilder.java,v 1.1 2004/01/05 23:47:10 pelle Exp $
$Log: ExchangeReceiptBuilder.java,v $
Revision 1.1  2004/01/05 23:47:10  pelle
Create new Document classification "order", which is really just inherint in the new
package layout.
Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.

Revision 1.1  2004/01/03 20:36:25  pelle
Renamed HeldTransfer to Exchange
Dropped valuetime from the request objects.
Doesnt yet compile. New commit to follow soon.

Revision 1.3  2003/11/21 04:43:03  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.2  2003/11/11 21:17:19  pelle
Further vital reshuffling.
org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
Did a bit of work on the Canonicalizer and changed a few other minor bits.

Revision 1.1  2003/11/08 01:39:57  pelle
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

*/

/**
 * User: pelleb
 * Date: Nov 7, 2003
 * Time: 8:05:15 PM
 */
public final class ExchangeReceiptBuilder extends TransferBuilder {
    public ExchangeReceiptBuilder(final ExchangeOrder req, final String id,Timestamp valuetime) throws InvalidTransferException, NegativeTransferException, NeuClearException {
        super(TransferGlobals.HELD_XFER_RCPT_TAGNAME, req.getAsset(),req.getAsset(),req.getAmount());
        final Element element = getElement();
        element.add(TransferGlobals.createAttribute(element, "helduntil", TimeTools.formatTimeStamp(req.getValidTo())));

    }
}
