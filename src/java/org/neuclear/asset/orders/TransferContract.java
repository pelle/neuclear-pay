package org.neuclear.asset.orders;

import org.dom4j.Element;
import org.neuclear.id.Identity;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.receiver.UnsupportedTransaction;
import org.neuclear.commons.NeuClearException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.commons.Utility;
import org.neuclear.xml.XMLTools;

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

*/

/**
 * 
 * User: pelleb
 * Date: Sep 23, 2003
 * Time: 3:07:54 PM
 */
public abstract class TransferContract extends AssetTransactionContract {
    TransferContract(final SignedNamedCore core, final Asset asset, final double amount,  final String comment)  {
        super(core, asset);
        this.amount = amount;
        this.comment = (comment != null) ? comment : "";
    }



    public final double getAmount() {
            return amount;
        }

    public final String getComment() {
        return comment;
    }


    private final double amount;
    private final String comment;

}
