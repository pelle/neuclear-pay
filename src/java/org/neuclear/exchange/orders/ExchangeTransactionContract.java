package org.neuclear.exchange.orders;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.AssetTransactionContract;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.id.SignedNamedCore;

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

$Id: ExchangeTransactionContract.java,v 1.2 2004/01/10 00:00:46 pelle Exp $
$Log: ExchangeTransactionContract.java,v $
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
 * Time: 7:31:15 PM
 */
public abstract class ExchangeTransactionContract extends AssetTransactionContract{
    ExchangeTransactionContract(SignedNamedCore core, Asset asset,ExchangeAgent agent) {
        super(core, asset);
        this.agent=agent;
    }
    public final ExchangeAgent getAgent(){
        return agent;
    }
    private final ExchangeAgent agent;
}
