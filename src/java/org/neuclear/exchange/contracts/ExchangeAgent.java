package org.neuclear.exchange.contracts;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.neuclear.id.*;
import org.neuclear.id.targets.Targets;
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

$Id: ExchangeAgent.java,v 1.7 2004/04/23 23:33:14 pelle Exp $
$Log: ExchangeAgent.java,v $
Revision 1.7  2004/04/23 23:33:14  pelle
Major update. Added an original url and nickname to Identity and friends.

Revision 1.6  2004/04/20 17:47:17  pelle
Fixes to ExchangeAgent to make it work with html contracts
CurrencyTests fail.

Revision 1.5  2004/04/05 16:31:42  pelle
Created new ServiceBuilder class for creating services. A service is an identity that has a seperate service URL and Service Public Key.

Revision 1.4  2004/04/01 23:18:33  pelle
Split Identity into Signatory and Identity class.
Identity remains a signed named object and will in the future just be used for self declared information.
Signatory now contains the PublicKey etc and is NOT a signed object.

Revision 1.3  2004/02/18 00:13:30  pelle
Many, many clean ups. I've readded Targets in a new method.
Gotten rid of NamedObjectBuilder and revamped Identity and Resolvers

Revision 1.2  2004/01/12 22:39:14  pelle
Completed all the builders and contracts.
Added a new abstract Value class to contain either an amount or a list of serial numbers.
Now ready to finish off the AssetControllers.

Revision 1.1  2004/01/05 23:47:10  pelle
Create new Document classification "order", which is really just inherint in the new
package layout.
Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.

*/

/**
 * User: pelleb
 * Date: Jan 5, 2004
 * Time: 11:04:32 PM
 */
public class ExchangeAgent extends Service {
    private ExchangeAgent(SignedNamedCore core, String name, String original, String serviceUrl, PublicKey serviceKey, Targets targets) {
        super(core, name, original, serviceUrl, serviceKey, targets);
    }

    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         *
         * @param elem
         * @return
         */
        public final SignedNamedObject read(final SignedNamedCore core, final Element elem) throws InvalidNamedObjectException {
            final Element serviceKeyElement = InvalidNamedObjectException.assertContainsElementId(core, elem, "controller.publickey");
            final Attribute url = (Attribute) elem.selectSingleNode("//html/head/link[starts-with(@rel,'neu:controller')]/@href");
            if (url == null || url.getValue() == null)
                throw new InvalidNamedObjectException(core.getName(), "Asset didnt contain a controller");
            try {
                final PublicKey sPub = extractPublicKey(serviceKeyElement);
                final Targets targets = Targets.parseList(elem);
                final String nickname = extractNickName(elem, core);
                final String original = extractOrginalUrl(elem);

                return new ExchangeAgent(core, nickname, original, url.getValue(), sPub, targets);
            } catch (XMLSecurityException e) {
                throw new InvalidNamedObjectException("invalid exchange agent xml");
            }
        }


    }

}
