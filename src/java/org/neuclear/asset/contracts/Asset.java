package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.NSTools;
import org.neuclear.id.builders.NamedObjectBuilder;
import org.neuclear.asset.contracts.builders.TransferReceiptBuilder;
import org.neuclear.senders.SoapSender;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.neuclear.xml.xmlsec.XMLSecurityException;
import org.neuclear.xml.xmlsec.KeyInfo;
import org.neuclear.xml.xmlsec.XMLSecTools;
import org.neuclear.commons.Utility;

import java.security.PublicKey;
import java.sql.Timestamp;

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

$Id: Asset.java,v 1.5 2003/11/11 21:17:19 pelle Exp $
$Log: Asset.java,v $
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
 * The asset contains information about a tradeable Asset. The Asset is in itself a subclass of Identity, which
 * means that any transactions signed by it must be in the format of <tt>neu://assetname/1231q145452452345</tt>
 * where assetname is the unique NeuClear wide AssetCertificate.<p>
 * As a subclass of Identity you cant instantiate these classes your self, but must be gotten in this form:<p>
 * <tt>(Asset)NSResolver.resolveIdentity("neu://assetname");</tt><p>
 * To create your own assets use AssetBuilder sign it using a valid NeuClear signer and post its description
 * to your default online repository. Then anyone can access your Assets through the above interface.
 * @see org.neuclear.asset.contracts.builders.AssetBuilder
 */
public class Asset extends Identity {
    private Asset(String name, Identity signatory, Timestamp timestamp, String digest, String repository, String signer, String logger, String receiver, PublicKey pub, String assetController,int decimalpoint,double minimumTransaction) throws NeuClearException {
        super(name, signatory, timestamp, digest, repository, signer, logger, receiver, pub);
        this.assetController = assetController;
        this.decimal=decimalpoint;
        this.multiplier=(int)Math.round(Math.pow(10,-decimalpoint));
        this.minimumTransaction=minimumTransaction;
    }

    public String getControllerURL() {
        return assetController;
    }
    /**
     * Sends a contract to the Assets controller.
     * @param obj NamedObjectBuilder
     * @return The receipt
     * @throws NeuClearException
     */
    public SignedNamedObject send(NamedObjectBuilder obj) throws NeuClearException {
        if (obj.isSigned())
            return SoapSender.quickSend(assetController, obj);
        throw new NeuClearException("Object wasnt signed");
    }

    /**
     * Checks if an amount is valid within the boundaries of the asset.
     * @param amount
     * @return
     */
    public boolean isValidAmount(double amount) {
        return amount>=minimumTransaction;
    }

    /**
     * Rounds a given value to fit within the valid numbers of the
     * @param amount
     * @return
     */
    public double round(double amount) {
        if (amount<minimumTransaction)
            return minimumTransaction;
        if (decimal==0)
            return amount;
        return Math.round(amount*multiplier)/multiplier;
    }



    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         *
         * @param elem
         * @return
         */
        public SignedNamedObject read(Element elem, String name, Identity signatory, String digest, Timestamp timestamp) throws NeuClearException, XMLSecurityException {
            if (!elem.getNamespace().equals(AssetGlobals.createNameSpace()))
                throw new UnsupportedOperationException("");
            String assetController=elem.attributeValue("controller");
            String repository = elem.attributeValue(DocumentHelper.createQName("repository", NSTools.NS_NEUID));
            String signer = elem.attributeValue(DocumentHelper.createQName("signer", NSTools.NS_NEUID));
            String logger = elem.attributeValue(DocumentHelper.createQName("logger", NSTools.NS_NEUID));
            String receiver = elem.attributeValue(DocumentHelper.createQName("receiver", NSTools.NS_NEUID));

            Element allowElement = elem.element(DocumentHelper.createQName("allow", NSTools.NS_NEUID));
            KeyInfo ki = new KeyInfo(allowElement.element(XMLSecTools.createQName("KeyInfo")));
            PublicKey pub = ki.getPublicKey();
            String dec = elem.attributeValue("decimalpoints");
            int decimal=(!Utility.isEmpty(dec))?Integer.parseInt(dec):0;
            String min=elem.attributeValue("minimumxact");
            double minimum=(!Utility.isEmpty(min))?Double.parseDouble(min):0;

            return new Asset(name,signatory, timestamp, digest, repository,signer, logger, receiver, pub,assetController,decimal,minimum);
        }


    }
    private final String assetController;
    private final int decimal;
    private final int multiplier;
    private final double minimumTransaction;
}
