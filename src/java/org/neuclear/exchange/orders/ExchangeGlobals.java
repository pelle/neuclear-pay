package org.neuclear.exchange.orders;

import org.dom4j.*;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.resolver.NSResolver;

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

$Id: ExchangeGlobals.java,v 1.3 2004/01/11 00:39:06 pelle Exp $
$Log: ExchangeGlobals.java,v $
Revision 1.3  2004/01/11 00:39:06  pelle
Cleaned up the schemas even more they now all verifiy.
The Order/Receipt pairs for neuclear pay, should now work. They all have Readers using the latest
Schema.
The TransferBuilders are done and the ExchangeBuilders are nearly there.
The new EmbeddedSignedNamedObject builder is useful for creating new Receipts. The new ReceiptBuilder uses
this to create the embedded transaction.
ExchangeOrders now have the concept of BidItem's, you could create an ExchangeOrder bidding on various items at the same time, to be exchanged as one atomic multiparty exchange.
Still doesnt build yet, but very close now ;-)

Revision 1.2  2004/01/10 00:00:46  pelle
Implemented new Schema for Transfer*
Working on it for Exchange*, so far all Receipts are implemented.
Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
Changed SignedNamedObject.getDigest() from byte array to String.
The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.

Revision 1.1  2004/01/06 23:26:48  pelle
Started restructuring the original xml schemas.
Updated the Exchange and transfer orders.

Revision 1.1  2004/01/05 23:47:09  pelle
Create new Document classification "order", which is really just inherint in the new
package layout.
Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.

Revision 1.6  2004/01/03 20:36:25  pelle
Renamed HeldTransfer to Exchange
Dropped valuetime from the request objects.
Doesnt yet compile. New commit to follow soon.

Revision 1.5  2003/11/28 00:11:50  pelle
Getting the NeuClear web transactions working.

Revision 1.4  2003/11/22 00:22:28  pelle
All unit tests in commons, id and xmlsec now work.
AssetController now successfully processes payments in the unit test.
Payment Web App has working form that creates a TransferOrder presents it to the signer
and forwards it to AssetControlServlet. (Which throws an XML Parser Exception) I think the XMLReaderServlet is bust.

Revision 1.3  2003/11/21 04:43:04  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.2  2003/11/10 17:42:07  pelle
The AssetController interface has been more or less finalized.
CurrencyController fully implemented
AssetControlClient implementes a remote client for communicating with AssetControllers

Revision 1.1  2003/11/09 03:32:56  pelle
More missing files from earlier commits. IDEA is acting strangly.

Revision 1.3  2003/11/06 23:47:43  pelle
Major Refactoring of CurrencyController.
Factored out AssetController to be new abstract parent class together with most of its support classes.
Created (Half way) AssetControlClient, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the AssetControlClient.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

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
TransferReceiptBuilder has been created for use by Transfer processors. It is used in the PaymentReceiver.

*/

/**
 * User: pelleb
 * Date: Oct 3, 2003
 * Time: 3:55:06 PM
 */
public final class ExchangeGlobals {
    private ExchangeGlobals() {
        // Instantiation is not allowed
    }

    public static Namespace createNameSpace() {
        return DocumentHelper.createNamespace(EX_NSPREFIX, EX_NSURI);
    }

    public static QName createQName(final String name) {
        return DocumentHelper.createQName(name, createNameSpace());
    }

    public static Attribute createAttribute(final Element elem, final String name, final String value) {
        return DocumentHelper.createAttribute(elem, createQName(name), value);
    }

    public static Element createElement(final String name, final String value) {
        Element elem = createElement(name);
        elem.setText(value);
        return elem;
    }

    public static Element createElement(final String name) {
        return DocumentHelper.createElement(createQName(name));
    }

    public static String getElementValue(final Element element, final String name) throws InvalidNamedObjectException {
           return TransferGlobals.getElementValue(element,createQName(name));
    }
    public static String parseExchangeOrderId(final Element element) throws InvalidNamedObjectException {
        return getElementValue(element,EXCHANGE_REF_TAG);
    }

    public static final ExchangeAgent parseAgentTag(Element elem) throws InvalidNamedObjectException {
        final String name = getElementValue(elem,AGENT_TAG);
        try {
            return (ExchangeAgent) NSResolver.resolveIdentity(name);
        } catch (ClassCastException e) {
            throw new InvalidNamedObjectException(name,e);
        } catch (NameResolutionException e) {
            throw new InvalidNamedObjectException(name,e);
        }

    }

    public static final Asset parseAssetTag(final Element elem,final String tag) throws InvalidNamedObjectException {
        final String name = getElementValue(elem,tag);
        try {
            return (Asset) NSResolver.resolveIdentity(name);
        } catch (ClassCastException e) {
            throw new InvalidNamedObjectException(name,e);
        } catch (NameResolutionException e) {
            throw new InvalidNamedObjectException(name,e);
        }

    }


    
    public static void registerReaders() {
//        VerifyingReader.getInstance().registerReader(ExchangeGlobals.CANCEL_TAGNAME, new AssetTransactionContract.Reader());
//        VerifyingReader.getInstance().registerReader(ExchangeGlobals.CANCEL_RCPT_TAGNAME, new AssetTransactionContract.Reader());
//        VerifyingReader.getInstance().registerReader(ExchangeGlobals.EXCHANGE_TAGNAME, new AssetTransactionContract.Reader());
//        VerifyingReader.getInstance().registerReader(ExchangeGlobals.EXCHANGE_RCPT_TAGNAME, new AssetTransactionContract.Reader());
//        VerifyingReader.getInstance().registerReader(ExchangeGlobals.COMPLETE_TAGNAME, new AssetTransactionContract.Reader());
//        VerifyingReader.getInstance().registerReader(ExchangeGlobals.COMPLETE_RCPT_TAGNAME, new AssetTransactionContract.Reader());

    }

    static {
        registerReaders();
    }

    public static final String EXCHANGE_TAGNAME = "ExchangeOrder";
    public static final String EXCHANGE_RCPT_TAGNAME = "ExchangeOrderReceipt";
    public static final String COMPLETE_TAGNAME = "ExchangeCompletionOrder";
    public static final String COMPLETE_RCPT_TAGNAME = "ExchangeCompletedReceipt";
    public static final String CANCEL_TAGNAME = "CancelExchangeOrder";
    public static final String CANCEL_RCPT_TAGNAME = "CancelExchangeReceipt";
    public static final String EXPIRY_TAG = "ExpiryDate";
    public static final String EXCHANGE_REF_TAG = "ExchangeOrderRef";
    public static final String EXCHANGE_TIME_TAGNAME = "ExchangeTime";
    public static final String AGENT_TAG = "ExchangeAgent";
    public static final String BID_ITEM_TAG = "BidItem";

    public static final String EX_NSPREFIX = "ex";
    public static final String EX_NSURI = "http://neuclear.org/neu/exch";

}
