package org.neuclear.exchange;

import org.neuclear.asset.HeldTransferReceipt;

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

$Id: ExchangeOrder.java,v 1.1 2003/11/06 23:47:43 pelle Exp $
$Log: ExchangeOrder.java,v $
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
 * Time: 5:28:20 PM
 */
public class ExchangeOrder {
    public ExchangeOrder(HeldTransferReceipt bid, String requestedAsset, double amount) {
        this.bid = bid;
        this.requestedAsset = requestedAsset;
        this.amount = amount;
    }

    public HeldTransferReceipt getBid() {
        return bid;
    }

    public String getRequestedAsset() {
        return requestedAsset;
    }

    public double getAmount() {
        return amount;
    }

    private final HeldTransferReceipt bid;
    private final String requestedAsset;
    private final double amount;
}
