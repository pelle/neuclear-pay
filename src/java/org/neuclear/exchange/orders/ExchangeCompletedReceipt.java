package org.neuclear.exchange.orders;

import org.dom4j.Element;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;

import java.sql.Timestamp;
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

$Id: ExchangeCompletedReceipt.java,v 1.2 2004/01/10 00:00:46 pelle Exp $
$Log: ExchangeCompletedReceipt.java,v $
Revision 1.2  2004/01/10 00:00:46  pelle
Implemented new Schema for Transfer*
Working on it for Exchange*, so far all Receipts are implemented.
Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
Changed SignedNamedObject.getDigest() from byte array to String.
The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.

Revision 1.1  2004/01/06 23:26:48  pelle
Started restructuring the original xml schemas.
Updated the Exchange and transfer orders.

*/

/**
 * User: pelleb
 * Date: Jan 6, 2004
 * Time: 7:38:36 PM
 */
public final class ExchangeCompletedReceipt extends ExchangeTransactionContract{
    private ExchangeCompletedReceipt(SignedNamedCore core, ExchangeCompletionOrder order, Date valuetime) {
        super(core, order.getAsset(), order.getAgent());
        this.valuetime = valuetime.getTime();
        this.order=order;
    }

    public ExchangeCompletionOrder getOrder() {
        return order;
    }

    public final Timestamp getValueTime(){
        return new Timestamp(valuetime);
    }

    private final long valuetime;
    private final ExchangeCompletionOrder order;

    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         *
         * @param elem
         * @return
         */
        public final SignedNamedObject read(final SignedNamedCore core, final Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().equals(AssetGlobals.NS_ASSET))
                throw new InvalidNamedObjectException(core.getName(),"Not in XML NameSpace: "+AssetGlobals.NS_ASSET.getURI());

            if (elem.getName().equals(ExchangeGlobals.COMPLETE_RCPT_TAGNAME)){
                return new ExchangeCompletedReceipt(core,
                        (ExchangeCompletionOrder) TransferGlobals.parseEmbedded(elem,ExchangeGlobals.createQName(ExchangeGlobals.COMPLETE_TAGNAME)),
                        TransferGlobals.parseValueTimeElement(elem));
            }
            throw new InvalidNamedObjectException(core.getName(),"Not Matched");
        }
    }

}
