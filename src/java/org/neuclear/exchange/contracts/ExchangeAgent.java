package org.neuclear.exchange.contracts;

import org.dom4j.Element;
import org.neuclear.id.*;
import org.neuclear.id.targets.Targets;
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

$Id: ExchangeAgent.java,v 1.5 2004/04/05 16:31:42 pelle Exp $
$Log: ExchangeAgent.java,v $
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
    public ExchangeAgent(SignedNamedCore core, String serviceUrl, PublicKey serviceKey, Targets targets) {
        super(core, serviceUrl, serviceKey, targets);
    }

    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         *
         * @param elem
         * @return
         */
        public final SignedNamedObject read(final SignedNamedCore core, final Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().equals(ExchangeAgentGlobals.NS_EXAGENT))
                throw new InvalidNamedObjectException(core.getName(), "Not in XML NameSpace: " + ExchangeAgentGlobals.NS_EXAGENT.getURI());
            final Element serviceElement = InvalidNamedObjectException.assertContainsElementQName(core, elem, SignedNamedObject.createNEUIDQName("Service"));
            final Element serviceKeyElement = InvalidNamedObjectException.assertContainsElementQName(core, serviceElement, XMLSecTools.createQName("KeyInfo"));
            final Element serviceUrlElement = InvalidNamedObjectException.assertContainsElementQName(core, serviceElement, SignedNamedObject.createNEUIDQName("Url"));
            try {
                final PublicKey sPub = extractPublicKey(serviceKeyElement);
                final String serviceurl = serviceUrlElement.getTextTrim();
                final Targets targets = Targets.parseList(elem);
                return new ExchangeAgent(core, serviceurl, sPub, targets);
            } catch (XMLSecurityException e) {
                throw new InvalidNamedObjectException("invalid exchange agent xml");
            }
        }


    }

}
