package org.neuclear.exchange.contracts.builders;

import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.JCESigner;
import org.neuclear.commons.crypto.signers.TestCaseSigner;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.exchange.contracts.ExchangeAgentGlobals;
import org.neuclear.id.builders.ServiceBuilder;
import org.neuclear.id.verifier.VerifyingReader;
import org.neuclear.xml.XMLTools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PublicKey;

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

$Id: ExchangeAgentBuilder.java,v 1.4 2004/04/28 00:22:28 pelle Exp $
$Log: ExchangeAgentBuilder.java,v $
Revision 1.4  2004/04/28 00:22:28  pelle
Fixed the strange verification error
Added bunch of new unit tests to support this.
Updated Signer's dependencies and version number to be a 0.9 release.
Implemented ThreadLocalSession session management for Hibernate ledger.
Various other minor changes.

Revision 1.3  2004/04/23 23:33:13  pelle
Major update. Added an original url and nickname to Identity and friends.

Revision 1.2  2004/04/17 19:28:00  pelle
Identity is now fully html based as is the ServiceBuilder.
VerifyingReader correctly identifies html files and parses them as such.
Targets and Target now parse html link tags
AssetBuilder and ExchangeAgentBuilder have been updated to support it and provide html formatted contracts.
The Asset.Reader and ExchangeAgent.Reader still need to be updated.

Revision 1.1  2004/04/05 16:31:42  pelle
Created new ServiceBuilder class for creating services. A service is an identity that has a seperate service URL and Service Public Key.

Revision 1.10  2004/04/02 16:58:53  pelle
Updated Asset and Asset Builder with semi fully featured functionality.
It now has Issuer, Service etc.

Revision 1.9  2004/04/01 23:18:31  pelle
Split Identity into Signatory and Identity class.
Identity remains a signed named object and will in the future just be used for self declared information.
Signatory now contains the PublicKey etc and is NOT a signed object.

Revision 1.8  2004/03/02 18:39:29  pelle
Done some more minor fixes within xmlsig, but mainly I've removed the old Source and Store patterns and sub packages. This is because
they really are no longer necessary with the new non naming naming system.

Revision 1.7  2004/02/18 00:13:29  pelle
Many, many clean ups. I've readded Targets in a new method.
Gotten rid of NamedObjectBuilder and revamped Identity and Resolvers

Revision 1.6  2004/01/10 00:00:44  pelle
Implemented new Schema for Transfer*
Working on it for Exchange*, so far all Receipts are implemented.
Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
Changed SignedNamedObject.getDigest() from byte array to String.
The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.

Revision 1.5  2003/12/19 00:30:49  pelle
Lots of usability changes through out all the passphrase agents and end user tools.

Revision 1.4  2003/11/21 04:43:03  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.3  2003/11/20 23:40:50  pelle
Getting all the tests to work in id
Removing usage of BC in CryptoTools as it was causing issues.
First version of EntityLedger that will use OFB's EntityEngine. This will allow us to support a vast amount databases without
writing SQL. (Yipee)

Revision 1.2  2003/11/11 21:17:19  pelle
Further vital reshuffling.
org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
Did a bit of work on the Canonicalizer and changed a few other minor bits.

Revision 1.1  2003/11/10 21:08:41  pelle
More JavaDoc

Revision 1.3  2003/11/10 17:42:07  pelle
The AssetController interface has been more or less finalized.
CurrencyController fully implemented
AssetControlClient implementes a remote client for communicating with AssetControllers

Revision 1.2  2003/11/09 03:26:47  pelle
More house keeping and shuffling about mainly pay

Revision 1.1  2003/11/09 03:10:13  pelle
Major changes that apparently didnt get properly checked in earlier.

Revision 1.2  2003/11/06 23:47:43  pelle
Major Refactoring of CurrencyController.
Factored out AssetController to be new abstract parent class together with most of its support classes.
Created (Half way) AssetControlClient, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the AssetControlClient.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

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
 * Time: 3:13:27 PM
 */
public final class ExchangeAgentBuilder extends ServiceBuilder {
    /**
     * @param serviceUrl
     * @param serviceKey
     * @throws org.neuclear.commons.NeuClearException
     *
     */
    public ExchangeAgentBuilder(final String title, final String original, final String serviceUrl, final PublicKey serviceKey) throws NeuClearException {
        super(ExchangeAgentGlobals.EXCHANGEAGENT_TAGNAME, title, original, serviceUrl, serviceKey);
    }

    public static void main(final String[] args) {
        try {
            final JCESigner signer = new TestCaseSigner();

            ExchangeAgentGlobals.registerReaders();
            final ExchangeAgentBuilder earaw = new ExchangeAgentBuilder("Tradex",
                    "http://tradex.neuclear.org/rules.html",
                    "http://bux.neuclear.org/Exchange",
                    signer.getPublicKey("exchange"));
            earaw.getDescription().setText("NeuClear Test ExchangeAgent which does nothing.");
            earaw.getRules().setText("You know the rules, there are no rules!!! No, really this is for testing purposes only. There" +
                    "are no implied rights or promises involved by this Exchange Agent.");

            earaw.sign("exchange", signer);
            File out = new File("src/webapp/rules.html");
            out.getParentFile().mkdirs();
            XMLTools.writeFile(out, earaw.getElement().getDocument());
            final InputStream is = new BufferedInputStream(new FileInputStream(out));
            final ExchangeAgent asset = (ExchangeAgent) VerifyingReader.getInstance().read(is);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
