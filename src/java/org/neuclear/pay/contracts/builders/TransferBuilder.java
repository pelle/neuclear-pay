package org.neuclear.pay.contracts.builders;

import org.dom4j.Element;
import org.neuclear.id.builders.NamedObjectBuilder;
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

$Id: TransferBuilder.java,v 1.2 2003/11/06 23:47:43 pelle Exp $
$Log: TransferBuilder.java,v $
Revision 1.2  2003/11/06 23:47:43  pelle
Major Refactoring of PaymentProcessor.
Factored out AssetController to be new abstract parent class together with most of its support classes.
Created (Half way) RemoteAssetController, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the RemoteAssetController.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

Revision 1.1  2003/10/03 23:48:29  pelle
Did various security related updates in the pay package with regards to immutability of fields etc.
PaymentReceiver should now be operational. Real testing needs to be done including in particular setting the
private key of the Receiver.
A new class TransferGlobals contains usefull settings for making life easier in the other contract based classes.
TransferContract the signed contract is functional and has a matching TransferRequestBuilder class for programmatically creating
TransferRequests for signing.
TransferReceiptBuilder has been created for use by Transfer processors. It is used in the PaymentReceiver.

*/

/**
 * User: pelleb
 * Date: Oct 3, 2003
 * Time: 3:13:27 PM
 */
public class TransferBuilder extends NamedObjectBuilder {
    protected TransferBuilder(String tagname, String name, String asset, String toaccount, double amount) {
        super(name, TransferGlobals.createQName(tagname));
        Element element = getElement();
        element.add(TransferGlobals.createAttribute(element, "recipient", toaccount));
        element.add(TransferGlobals.createAttribute(element, "asset", asset));
        element.add(TransferGlobals.createAttribute(element, "amount", Double.toString(amount)));
    }

}
