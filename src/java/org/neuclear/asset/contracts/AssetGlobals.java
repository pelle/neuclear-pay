package org.neuclear.asset.contracts;

import org.dom4j.*;

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

$Id: AssetGlobals.java,v 1.2 2003/11/09 03:26:47 pelle Exp $
$Log: AssetGlobals.java,v $
Revision 1.2  2003/11/09 03:26:47  pelle
More house keeping and shuffling about mainly pay

Revision 1.1  2003/11/08 01:39:58  pelle
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

Revision 1.3  2003/11/06 23:47:43  pelle
Major Refactoring of CurrencyController.
Factored out AssetController to be new abstract parent class together with most of its support classes.
Created (Half way) RemoteAssetController, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the RemoteAssetController.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

Revision 1.2  2003/10/25 00:38:43  pelle
Fixed SmtpSender it now sends the messages.
Refactored CommandLineSigner. Now it simply signs files read from command line. However new class IdentityCreator
is subclassed and creates new Identities. You can subclass CommandLineSigner to create your own variants.
Several problems with configuration. Trying to solve at the moment. Updated PicoContainer to beta-2

Revision 1.1  2003/10/03 23:48:29  pelle
Did various security related updates in the pay package with regards to immutability of fields etc.
AssetControllerReceiver should now be operational. Real testing needs to be done including in particular setting the
private key of the Receiver.
A new class TransferGlobals contains usefull settings for making life easier in the other contract based classes.
TransferContract the signed contract is functional and has a matching TransferRequestBuilder class for programmatically creating
TransferRequests for signing.
TransferReceiptBuilder has been created for use by Transfer processors. It is used in the AssetControllerReceiver.

*/

/**
 * User: pelleb
 * Date: Oct 3, 2003
 * Time: 3:55:06 PM
 */
public final class AssetGlobals {
    private AssetGlobals() {
        // Instantiation is not allowed
    }

    public static Namespace createNameSpace() {
        return DocumentHelper.createNamespace(ASSET_NSPREFIX, XFER_ASSETS);
    }

    public static QName createQName(String name) {
        return DocumentHelper.createQName(name, createNameSpace());
    }

    public static Attribute createAttribute(Element elem, String name, String value) {
        return DocumentHelper.createAttribute(elem, createQName(name), value);
    }

    public static Element createElement(String name, String value) {
        return DocumentHelper.createElement(createQName(name));
    }

    public static final String ASSET_TAGNAME = "Asset";
    public static final String XFER_ASSETS = "http://neuclear.org/neu/assets";
    public static final String ASSET_NSPREFIX = "asset";
}
