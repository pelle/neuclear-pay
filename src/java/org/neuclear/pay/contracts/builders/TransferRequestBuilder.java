package org.neuclear.pay.contracts.builders;

import org.neuclear.pay.contracts.TransferGlobals;

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

$Id: TransferRequestBuilder.java,v 1.1 2003/10/03 23:48:29 pelle Exp $
$Log: TransferRequestBuilder.java,v $
Revision 1.1  2003/10/03 23:48:29  pelle
Did various security related updates in the pay package with regards to immutability of fields etc.
PaymentReceiver should now be operational. Real testing needs to be done including in particular setting the
private key of the Receiver.
A new class TransferGlobals contains usefull settings for making life easier in the other contract based classes.
TransferContract the signed contract is functional and has a matching TransferRequestBuilder class for programmatically creating
TransferRequests for signing.
TransferReceiptBuilder has been created for use by Payment processors. It is used in the PaymentReceiver.

*/

/**
 * 
 * User: pelleb
 * Date: Oct 3, 2003
 * Time: 6:26:13 PM
 */
public class TransferRequestBuilder extends TransferBuilder {
    public TransferRequestBuilder(String name, String asset, String toaccount, double amount) {
        super(TransferGlobals.XFER_TAGNAME, name, asset, toaccount, amount);
    }
}
