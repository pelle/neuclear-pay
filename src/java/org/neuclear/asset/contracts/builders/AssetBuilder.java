package org.neuclear.asset.contracts.builders;

import org.dom4j.Element;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.JCESigner;
import org.neuclear.commons.crypto.signers.TestCaseSigner;
import org.neuclear.id.builders.IdentityBuilder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.store.FileStore;
import org.neuclear.store.Store;
import org.neuclear.xml.XMLException;

import java.security.GeneralSecurityException;
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

$Id: AssetBuilder.java,v 1.4 2003/11/21 04:43:03 pelle Exp $
$Log: AssetBuilder.java,v $
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
public final class AssetBuilder extends IdentityBuilder {
    /**
     * Used to create new Assets
     * 
     * @param name       The Name of Identity
     * @param allow      PublicKey allowed to sign in here
     * @param repository URL of Default Store for NameSpace. (Note. A NameSpace object is stored in the default repository of it's parent namespace)
     * @param signer     URL of default interactive signing service for namespace. If null it doesnt allow interactive signing
     * @param receiver   URL of default receiver for namespace
     * @param controller URL of AssetController This should be a http web url
     * @param decimal    The amount of decimal points.
     * @param minimum    Minimum transaction size
     */
    public AssetBuilder(final String name, final PublicKey allow, final String repository, final String signer, final String logger, final String receiver, final String controller, final int decimal, final double minimum) throws NeuClearException {
        super(AssetGlobals.createQName(AssetGlobals.ASSET_TAGNAME), name, allow, repository, signer, logger, receiver);
        final Element elem = getElement();
        AssetGlobals.createAttribute(elem, "controller", controller);
        AssetGlobals.createAttribute(elem, "decimalpoints", Integer.toString(decimal));
        AssetGlobals.createAttribute(elem, "minimumxact", Double.toString(minimum));

    }

    public static void main(final String[] args) {
        try {
            final JCESigner signer = new TestCaseSigner();
            String assetname = "neu://test/bux";
            if (args.length > 0)
                assetname = args[0];

            final AssetBuilder assetraw = new AssetBuilder(assetname,
                    signer.getPublicKey(assetname),
                    "http://repository.neuclear.org/",
                    "http://bux.neuclear.org:8080",
                    "http://logger.neuclear.org",
                    "http://bux.neuclear.org:8080",
                    "http://bux.neuclear.org:8080",
                    2,
                    0.01
            );
            final Asset asset= (Asset) assetraw.sign(signer);
            final Store store = new FileStore("target/testdata/repository");
            store.receive(asset);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (NeuClearException e) {
            e.printStackTrace();
        } catch (XMLException e) {
            e.printStackTrace();
        }
    }
}
