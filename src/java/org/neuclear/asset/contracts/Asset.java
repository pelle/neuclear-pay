package org.neuclear.asset.contracts;

import org.dom4j.Element;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.id.*;
import org.neuclear.senders.Sender;
import org.neuclear.xml.xmlsec.KeyInfo;
import org.neuclear.xml.xmlsec.XMLSecTools;
import org.neuclear.xml.xmlsec.XMLSecurityException;

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

$Id: Asset.java,v 1.13 2004/01/21 23:41:02 pelle Exp $
$Log: Asset.java,v $
Revision 1.13  2004/01/21 23:41:02  pelle
Started the unit tests for the new payment message format.

Revision 1.12  2004/01/10 00:00:44  pelle
Implemented new Schema for Transfer*
Working on it for Exchange*, so far all Receipts are implemented.
Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
Changed SignedNamedObject.getDigest() from byte array to String.
The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.

Revision 1.11  2003/12/19 18:02:35  pelle
Revamped a lot of exception handling throughout the framework, it has been simplified in most places:
- For most cases the main exception to worry about now is InvalidNamedObjectException.
- Most lowerlevel exception that cant be handled meaningful are now wrapped in the LowLevelException, a
  runtime exception.
- Source and Store patterns each now have their own exceptions that generalizes the various physical
  exceptions that can happen in that area.

Revision 1.10  2003/12/10 23:52:39  pelle
Did some cleaning up in the builders
Fixed some stuff in IdentityCreator
New maven goal to create executable jarapp
We are close to 0.8 final of ID, 0.11 final of XMLSIG and 0.5 of commons.
Will release shortly.

Revision 1.9  2003/11/21 04:43:04  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.8  2003/11/20 16:01:59  pelle
Updated all the Contracts to use the new security model.

Revision 1.7  2003/11/19 23:32:19  pelle
Signers now can generatekeys via the generateKey() method.
Refactored the relationship between SignedNamedObject and NamedObjectBuilder a bit.
SignedNamedObject now contains the full xml which is returned with getEncoded()
This means that it is now possible to further receive on or process a SignedNamedObject, leaving
NamedObjectBuilder for its original purposes of purely generating new Contracts.
NamedObjectBuilder.sign() now returns a SignedNamedObject which is the prefered way of processing it.
Updated all major interfaces that used the old model to use the new model.

Revision 1.6  2003/11/12 23:47:04  pelle
Much work done in creating good test environment.
PaymentReceiverTest works, but needs a abit more work in its environment to succeed testing.

Revision 1.5  2003/11/11 21:17:19  pelle
Further vital reshuffling.
org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
Did a bit of work on the Canonicalizer and changed a few other minor bits.

Revision 1.4  2003/11/10 21:08:41  pelle
More JavaDoc

Revision 1.3  2003/11/10 19:27:52  pelle
Mainly documentation.

Revision 1.2  2003/11/10 17:42:07  pelle
The AssetController interface has been more or less finalized.
CurrencyController fully implemented
AssetControlClient implementes a remote client for communicating with AssetControllers

Revision 1.1  2003/11/09 03:32:56  pelle
More missing files from earlier commits. IDEA is acting strangly.

Revision 1.1  2003/11/06 23:47:43  pelle
Major Refactoring of CurrencyController.
Factored out AssetController to be new abstract parent class together with most of its support classes.
Created (Half way) AssetControlClient, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the AssetControlClient.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

*/

/**
 * The assetName contains information about a tradeable Asset. The Asset is in itself a subclass of Identity, which
 * means that any transactions signed by it must be in the format of <tt>neu://assetname/1231q145452452345</tt>
 * where assetname is the unique NeuClear wide AssetCertificate.<p>
 * As a subclass of Identity you cant instantiate these classes your self, but must be gotten in this form:<p>
 * <tt>(Asset)NSResolver.resolveIdentity("neu://assetname");</tt><p>
 * To create your own assets use AssetBuilder sign it using a valid NeuClear signer and post its description
 * to your default online repository. Then anyone can access your Assets through the above interface.
 * 
 * @see org.neuclear.asset.contracts.builders.AssetBuilder
 */
public final class Asset extends Identity {
    protected Asset(final SignedNamedCore core, final String serviceurl,final PublicKey pub, final int decimal, final double minimumTransaction) {
        super(core, pub);
        this.serviceurl=serviceurl;
        this.decimal = decimal;
        this.multiplier = (int) Math.round(Math.pow(10, -decimal));
        this.minimumTransaction = minimumTransaction;
    }

    //TODO drop. This is for testing purposes only
    public Asset(final String serviceurl,final PublicKey pub, final int decimal, final double minimumTransaction) {
        super(pub);
        this.serviceurl=serviceurl;
        this.decimal = decimal;
        this.multiplier = (int) Math.round(Math.pow(10, -decimal));
        this.minimumTransaction = minimumTransaction;
    }

    /**
     * Checks if an amount is valid within the boundaries of the assetName.
     * 
     * @param amount 
     * @return 
     */
    public final boolean isValidAmount(final double amount) {
        return amount >= minimumTransaction;
    }

    /**
     * Rounds a given value to fit within the valid numbers of the
     * 
     * @param amount 
     * @return 
     */
    public final double round(final double amount) {
        if (amount < minimumTransaction)
            return minimumTransaction;
        if (decimal == 0)
            return amount;
        return Math.round(amount * multiplier) / multiplier;
    }

    public final String getServiceurl() {
        return serviceurl;
    }

    public final SignedNamedObject send(SignedNamedObject object) throws NeuClearException {
        return Sender.quickSend(serviceurl,object);
    }

    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         * 
         * @param elem 
         * @return 
         */
        public final SignedNamedObject read(final SignedNamedCore core, final Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().equals(AssetGlobals.NS_ASSET))
                throw new InvalidNamedObjectException(core.getName(),"Not in XML NameSpace: "+AssetGlobals.NS_ASSET.getURI());
            final String serviceurl = elem.attributeValue(createNEUIDQName("serviceurl"));

            final Element allowElement = InvalidNamedObjectException.assertContainsElementQName(core,elem,createNEUIDQName("allow"));
            try {
                final KeyInfo ki = new KeyInfo(InvalidNamedObjectException.assertContainsElementQName(allowElement, XMLSecTools.createQName("KeyInfo")));
                final PublicKey pub = ki.getPublicKey();
                final String dec = elem.attributeValue("decimalpoints");
                final int decimal = (!Utility.isEmpty(dec)) ? Integer.parseInt(dec) : 0;
                final String min = elem.attributeValue("minimumxact");
                final double minimum = (!Utility.isEmpty(min)) ? Double.parseDouble(min) : 0;
                return new Asset(core, serviceurl, pub, decimal, minimum);
            } catch (XMLSecurityException e) {
                throw new InvalidNamedObjectException(core.getName(),e);
            }
        }


    }

    private final String serviceurl;
    private final int decimal;
    private final int multiplier;
    private final double minimumTransaction;
}
