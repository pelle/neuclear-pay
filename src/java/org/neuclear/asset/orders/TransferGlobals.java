package org.neuclear.asset.orders;

import org.dom4j.*;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.commons.Utility;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.id.verifier.VerifyingReader;

import java.sql.Timestamp;
import java.text.ParseException;

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

$Id: TransferGlobals.java,v 1.11 2004/04/06 16:24:34 pelle Exp $
$Log: TransferGlobals.java,v $
Revision 1.11  2004/04/06 16:24:34  pelle
Added two new Data Objects IssuerOrder and IssueReceipt for managing the issuance process.
Added Issuance support to the Asset and Audit Controllers.
Implemented access control for complete and cancel exchange orders.

Revision 1.10  2004/04/05 16:31:41  pelle
Created new ServiceBuilder class for creating services. A service is an identity that has a seperate service URL and Service Public Key.

Revision 1.9  2004/04/02 23:04:36  pelle
Got TransferOrder and Builder working with their test cases.
Working on TransferReceipt which is the first embedded receipt. This is causing some problems at the moment.

Revision 1.8  2004/04/01 23:18:32  pelle
Split Identity into Signatory and Identity class.
Identity remains a signed named object and will in the future just be used for self declared information.
Signatory now contains the PublicKey etc and is NOT a signed object.

Revision 1.7  2004/03/03 23:28:14  pelle
Updated tests to use AbstractObjectCreationTest

Revision 1.6  2004/01/13 15:11:17  pelle
Now builds.
Now need to do unit tests

Revision 1.5  2004/01/12 22:39:14  pelle
Completed all the builders and contracts.
Added a new abstract Value class to contain either an amount or a list of serial numbers.
Now ready to finish off the AssetControllers.

Revision 1.4  2004/01/11 00:39:06  pelle
Cleaned up the schemas even more they now all verifiy.
The Order/Receipt pairs for neuclear pay, should now work. They all have Readers using the latest
Schema.
The TransferBuilders are done and the ExchangeBuilders are nearly there.
The new EmbeddedSignedNamedObject builder is useful for creating new Receipts. The new ReceiptBuilder uses
this to create the embedded transaction.
ExchangeOrders now have the concept of BidItem's, you could create an ExchangeOrder bidding on various items at the same time, to be exchanged as one atomic multiparty exchange.
Still doesnt build yet, but very close now ;-)

Revision 1.3  2004/01/10 00:00:45  pelle
Implemented new Schema for Transfer*
Working on it for Exchange*, so far all Receipts are implemented.
Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
Changed SignedNamedObject.getDigest() from byte array to String.
The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.

Revision 1.2  2004/01/06 23:26:48  pelle
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
public final class TransferGlobals {
    private TransferGlobals() {
        // Instantiation is not allowed
    }

    public static Namespace createNameSpace() {
        return DocumentHelper.createNamespace(XFER_NSPREFIX, XFER_NSURI);
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

    public static String getElementValue(final Element element, final String name) throws InvalidNamedObjectException {
        return getElementValue(element, createQName(name));
    }

    public static String getElementValue(final Element element, final QName name) throws InvalidNamedObjectException {
        final Element value = element.element(name);
        if (value == null)
            throw new InvalidNamedObjectException("Missing required element: " + name);
        final String text = value.getTextTrim();
        if (Utility.isEmpty(text))
            throw new InvalidNamedObjectException("Required element: " + name + " is empty");
        return text;
    }

    public static String parseCommentElement(final Element element) {
        final Element value = element.element(createQName(COMMENT_TAG));
        if (value == null)
            return "";
        final String text = value.getTextTrim();
        return Utility.denullString(text);
    }

    public static Element createElement(final String name) {
        return DocumentHelper.createElement(createQName(name));
    }

    public static void registerReaders() {
        VerifyingReader.getInstance().registerReader(TransferGlobals.XFER_TAGNAME, new TransferOrder.Reader());
        VerifyingReader.getInstance().registerReader(TransferGlobals.XFER_RCPT_TAGNAME, new TransferReceipt.Reader());
        VerifyingReader.getInstance().registerReader(TransferGlobals.ISSUE_TAGNAME, new IssueOrder.Reader());
        VerifyingReader.getInstance().registerReader(TransferGlobals.ISSUE_RCPT_TAGNAME, new IssueReceipt.Reader());
    }

    public static final Timestamp parseValueTimeElement(final Element elem) throws InvalidNamedObjectException {
        return parseTimeStampElement(elem, createQName(VALUE_TIME_TAG));
    }

    public static final Timestamp parseTimeStampElement(final Element elem, final String name) throws InvalidNamedObjectException {
        return parseTimeStampElement(elem, createQName(name));
    }

    public static final Timestamp parseTimeStampElement(final Element elem, final QName qn) throws InvalidNamedObjectException {
        try {
            final Element telem = elem.element(qn);
            if (telem == null)
                throw new InvalidNamedObjectException("missing time stamp element");
            final String value = telem.getTextTrim();
            if (Utility.isEmpty(value))
                throw new InvalidNamedObjectException("missing time stamp");

            return TimeTools.parseTimeStamp(value);
        } catch (ParseException e) {
            throw new InvalidNamedObjectException("missing or invalid time stamp");
        }

    }

    public static final Asset parseAssetTag(Element elem) throws InvalidNamedObjectException {
        final String name = getElementValue(elem, ASSET_TAG);
        try {
            return (Asset) Resolver.resolveIdentity(name);
        } catch (ClassCastException e) {
            throw new InvalidNamedObjectException(name, e);
        } catch (NameResolutionException e) {
            throw new InvalidNamedObjectException(name, e);
        }

    }

    public static final String parseRecipientTag(Element elem) throws InvalidNamedObjectException {
        final String name = getElementValue(elem, RECIPIENT_TAG);
        return name;

    }

    public static final Value parseValueTag(Element elem) throws InvalidNamedObjectException {
        if (elem.elements(createQName(AMOUNT_TAG)) != null)
            return parseAmountTag(elem);
        else
            return parseSerialNumbers(elem);

    }

    private static SerialNumbers parseSerialNumbers(Element elem) throws InvalidNamedObjectException {
        final String numbers = getElementValue(elem, SERIAL_NOS_TAG);
        return new SerialNumbers(numbers.split("\\s*"));
    }

    private static Amount parseAmountTag(Element elem) throws InvalidNamedObjectException {
        final String amount = getElementValue(elem, AMOUNT_TAG);

        try {
            return new Amount(Double.parseDouble(amount));
        } catch (NumberFormatException e) {
            throw new InvalidNamedObjectException("Badly formatted number", e);
        }
    }

    public static Element createValueTag(Value value) throws InvalidTransferException {
        if (value instanceof Amount) {
            return createElement(TransferGlobals.AMOUNT_TAG, Double.toString(value.getAmount()));
        }
        SerialNumbers nos = ((SerialNumbers) value);
        if (value.getAmount() > 0) {
            final StringBuffer buf = new StringBuffer((int) (nos.getAmount() * (nos.getNumber(0).length()) + 1));
            for (int i = 0; i < nos.getAmount(); i++)
                buf.append(nos.getNumber(i));
            buf.append("\n");
            return createElement(TransferGlobals.SERIAL_NOS_TAG, buf.toString());
        }
        throw new InvalidTransferException("Cant have an empty list");
    }

    public static final SignedNamedObject parseEmbedded(Element elem, QName name) throws InvalidNamedObjectException {
        final Element embedded = elem.element(name).createCopy();
        DocumentHelper.createDocument(embedded);
        if (embedded == null)
            throw new InvalidNamedObjectException("Element: " + elem.getName() + " doesnt contain a " + name.getQualifiedName());
        return VerifyingReader.getInstance().read(embedded);
    }

    static {
        registerReaders();
    }

    public static final String XFER_TAGNAME = "TransferOrder";
    public static final String XFER_RCPT_TAGNAME = "TransferReceipt";
    public static final String ISSUE_TAGNAME = "IssueOrder";
    public static final String ISSUE_RCPT_TAGNAME = "IssueReceipt";
    public static final String XFER_NSPREFIX = "xfer";
    public static final String XFER_NSURI = "http://neuclear.org/neu/xfer.xsd";
    public static final String VALUE_TIME_TAG = "ValueTime";
    public static final String COMMENT_TAG = "Comment";
    public static final String ASSET_TAG = "Asset";
    public static final String AMOUNT_TAG = "Amount";
    public static final String SERIAL_NOS_TAG = "SerialNumbers";
    public static final String RECIPIENT_TAG = "Recipient";
}
