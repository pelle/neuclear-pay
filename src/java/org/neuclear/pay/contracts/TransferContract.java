package org.neuclear.pay.contracts;

import org.dom4j.Element;
import org.neuclear.id.Identity;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.receiver.UnsupportedTransaction;
import org.neudist.utils.NeudistException;

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

*/

/**
 * 
 * User: pelleb
 * Date: Sep 23, 2003
 * Time: 3:07:54 PM
 */
public class TransferContract extends SignedNamedObject {
    TransferContract(String name, Identity signer, Timestamp timestamp, String digest, String asset, String toaccount, double amount) throws NeudistException {
        super(name, signer, timestamp, digest);
        this.asset = asset;
        this.amount = amount;
        this.toaccount = toaccount;
    }

    public final double getAmount() {
        return amount;
    }

    public final String getAsset() {
        return asset;
    }

    public final String getRecipient() {
        return toaccount;
    }

    private final double amount;
    private final String toaccount;
    private final String asset;

    public static class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         * @param elem
         * @return
         */
        public SignedNamedObject read(Element elem, String name, Identity signatory, String digest, Timestamp timestamp) throws NeudistException {
            if (!(elem.getName().equals(TransferGlobals.XFER_TAGNAME) &&
                    elem.getNamespaceURI().equals(TransferGlobals.XFER_NSURI)))
                throw new UnsupportedTransaction(null);

            double amount = Double.parseDouble(elem.attributeValue("amount"));
            String asset = elem.attributeValue("asset");
            String to = elem.attributeValue("recipient");
            return new TransferContract(name, signatory, timestamp, digest, asset, to, amount);
        }

    }

}
