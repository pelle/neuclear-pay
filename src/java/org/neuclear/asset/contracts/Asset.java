package org.neuclear.asset.contracts;

import org.neuclear.commons.NeuClearException;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.NSTools;
import org.neuclear.asset.contracts.builders.TransferReceiptBuilder;
import org.neuclear.senders.SoapSender;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.neudist.xml.xmlsec.XMLSecurityException;
import org.neudist.xml.xmlsec.KeyInfo;
import org.neudist.xml.xmlsec.XMLSecTools;

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

$Id: Asset.java,v 1.2 2003/11/10 17:42:07 pelle Exp $
$Log: Asset.java,v $
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
 * User: pelleb
 * Date: Nov 6, 2003
 * Time: 6:27:21 PM
 */
public class Asset extends Identity {
    private Asset(String name, Identity signatory, Timestamp timestamp, String digest, String repository, String signer, String logger, String receiver, PublicKey pub, String assetController) throws NeuClearException {
        super(name, signatory, timestamp, digest, repository, signer, logger, receiver, pub);
        this.assetController = assetController;
    }

    public String getControllerURL() {
        return assetController;
    }

    public SignedNamedObject send(TransferReceiptBuilder obj) throws NeuClearException {
        if (obj.isSigned())
            return SoapSender.quickSend(assetController, obj);
        throw null;
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

            return new Asset(name,signatory, timestamp, digest, repository,signer, logger, receiver, pub,assetController);
        }


    }
    private final String assetController;
}