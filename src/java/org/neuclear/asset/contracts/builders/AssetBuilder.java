package org.neuclear.asset.contracts.builders;

import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.fees.FeeStructureBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.JCESigner;
import org.neuclear.commons.crypto.signers.TestCaseSigner;
import org.neuclear.id.Service;
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

$Id: AssetBuilder.java,v 1.21 2004/09/07 18:47:33 pelle Exp $
$Log: AssetBuilder.java,v $
Revision 1.21  2004/09/07 18:47:33  pelle
Added support for dom4j 1.5 and added a new XPP3Reader

Revision 1.20  2004/09/06 22:24:23  pelle
Added a package for calculating fees. This has been integrated into the Asset contract.

Revision 1.19  2004/04/27 15:24:54  pelle
Due to a new API change in 0.5 I have changed the name of Ledger and it's implementers to LedgerController.

Revision 1.18  2004/04/26 23:57:39  pelle
Trying to find the verifying error

Revision 1.17  2004/04/25 07:27:28  pelle
Cosmetic changes to signing servlet and neuclear pay web app.
Cosmetic changes to html generated by ServiceBuilder.
Fixed some stuff in Sender and SmtpSender
Added account registration.
Bumped version numbers in project.xml files to final release versions.

Revision 1.16  2004/04/23 23:33:13  pelle
Major update. Added an original url and nickname to Identity and friends.

Revision 1.15  2004/04/21 23:22:19  pelle
Integrated Browser with the asset controller
Updated look and feel
Added ServletLedgerFactory
Added ServletAssetControllerFactory
Created issue.jsp file
Fixed many smaller issues

Revision 1.14  2004/04/20 23:30:42  pelle
All unit tests (junit and cactus) work. The AssetControllerServlet is operational.

Revision 1.13  2004/04/18 01:06:05  pelle
Asset now parses the xhtml file for its details.

Revision 1.12  2004/04/17 19:27:59  pelle
Identity is now fully html based as is the ServiceBuilder.
VerifyingReader correctly identifies html files and parses them as such.
Targets and Target now parse html link tags
AssetBuilder and ExchangeAgentBuilder have been updated to support it and provide html formatted contracts.
The Asset.Reader and ExchangeAgent.Reader still need to be updated.

Revision 1.11  2004/04/05 16:31:40  pelle
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
public final class AssetBuilder extends ServiceBuilder {
    /**
     * @param serviceUrl
     * @param serviceKey
     * @param issuerKey
     * @param decimal
     * @param minimum
     * @throws NeuClearException
     */
    public AssetBuilder(final String title, final String originalurl, final String serviceUrl, final PublicKey serviceKey, final PublicKey issuerKey, final int decimal, final double minimum, final String units) throws NeuClearException {
        super(AssetGlobals.ASSET_TAGNAME, title, originalurl, serviceUrl, serviceKey);
        addKeyInfo("asset.issuer.publickey", issuerKey, "Issuers Key");
        addFeature("asset.decimalpoints", "Decimal Points", Integer.toString(decimal), "Decimal Points");
        addFeature("asset.miminmum", "Minimum Transaction", Double.toString(minimum), "The Minumum Transaction size");
        addFeature("asset.units", "Units", units, "Asset Units");

    }

    public void addFeeAccount(final PublicKey feeAccount) {
        addKeyInfo("fee.account", feeAccount, "Fee Account");
    }

    public void addFeeStructure(FeeStructureBuilder feeBuilder) {
        fees.add(feeBuilder.getElement());
    }

    public static void main(final String[] args) {
        try {
            final JCESigner signer = new TestCaseSigner();

            AssetGlobals.registerReaders();

            final AssetBuilder assetraw = new AssetBuilder("Bux",
                    "http://bux.neuclear.org/bux.html",
                    "http://bux.neuclear.org/Asset",
                    signer.getPublicKey("bux"),
                    signer.getPublicKey("carol"),
                    2,
                    0.01, "bux");
            assetraw.getDescription().setText("NeuClear Test Currency of no value.");
            assetraw.getRules().setText("You know the rules, there are no rules!!! No, really this is for testing purposes only. There" +
                    "are no implied rights or promises involved in this asset.");
            assetraw.addFeeAccount(signer.getPublicKey("bob"));
            assetraw.addFeeStructure(new FeeStructureBuilder("Bux", 0.1, 0.01));
            assetraw.sign("ivan", signer);
            File out = new File("src/webapp/bux.html");
            out.getParentFile().mkdirs();
            XMLTools.writeFile(out, assetraw.getElement().getDocument());
            final InputStream is = new BufferedInputStream(new FileInputStream(out));
            final Service asset = (Service) VerifyingReader.getInstance().read(is);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
