package org.neuclear.pay.contracts;

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

$Id: TransferGlobals.java,v 1.2 2003/10/25 00:38:43 pelle Exp $
$Log: TransferGlobals.java,v $
Revision 1.2  2003/10/25 00:38:43  pelle
Fixed SmtpSender it now sends the messages.
Refactored CommandLineSigner. Now it simply signs files read from command line. However new class IdentityCreator
is subclassed and creates new Identities. You can subclass CommandLineSigner to create your own variants.
Several problems with configuration. Trying to solve at the moment. Updated PicoContainer to beta-2

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
 * User: pelleb
 * Date: Oct 3, 2003
 * Time: 3:55:06 PM
 */
public final class TransferGlobals {
    private TransferGlobals() {
        // Instantiation is not allowed
    }

    public static Namespace createXferNameSpace() {
        return DocumentHelper.createNamespace(XFER_NSPREFIX, XFER_NSURI);
    }

    public static QName createQName(String name) {
        return DocumentHelper.createQName(name, createXferNameSpace());
    }

    public static Attribute createAttribute(Element elem, String name, String value) {
        return DocumentHelper.createAttribute(elem, createQName(name), value);
    }

    public static Element createElement(String name, String value) {
        return DocumentHelper.createElement(createQName(name));
    }

    public static final String XFER_TAGNAME = "TransferRequest";
    public static final String XFER_RCPT_TAGNAME = "TransferReceipt";
    public static final String XFER_NSPREFIX = "xfer";
    public static final String XFER_NSURI = "http://neuclear.org/neu/xfer";
}
