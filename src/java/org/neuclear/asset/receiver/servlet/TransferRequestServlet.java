package org.neuclear.asset.receiver.servlet;

import org.neuclear.signers.servlet.SignatureRequestServlet;
import org.neuclear.id.builders.NamedObjectBuilder;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.id.Identity;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.asset.orders.builders.TransferRequestBuilder;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.InvalidTransferException;

import javax.servlet.http.HttpServletRequest;

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

$Id: TransferRequestServlet.java,v 1.2 2004/01/05 23:47:09 pelle Exp $
$Log: TransferRequestServlet.java,v $
Revision 1.2  2004/01/05 23:47:09  pelle
Create new Document classification "order", which is really just inherint in the new
package layout.
Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.

Revision 1.1  2003/12/20 00:17:41  pelle
overwrote the standard Object.toString(), hashCode() and equals() methods for SignedNamedObject/Core
fixed cactus tests
Added TransferRequestServlet
Added cactus tests to pay

*/

/**
 * User: pelleb
 * Date: Dec 19, 2003
 * Time: 6:37:19 PM
 */
public class TransferRequestServlet extends SignatureRequestServlet{
    protected NamedObjectBuilder createBuilder(HttpServletRequest request) throws NeuClearException {
        Asset asset=(Asset) NSResolver.resolveIdentity(getServiceid());
        Identity user=(Identity) request.getUserPrincipal();
        if (user==null)
            user=NSResolver.resolveIdentity(request.getParameter("sender"));
        Identity to=NSResolver.resolveIdentity(request.getParameter("recipient"));
        double amount=Double.parseDouble(Utility.denullString(request.getParameter("amount"),"0"));
        String comment=Utility.denullString(request.getParameter("comment"));
        try {
            return new TransferRequestBuilder(asset,user,to,amount,TimeTools.now(),comment);
        } catch (InvalidTransferException e) {
            throw new InvalidNamedObjectException(user.getName(),e);
        }
    }
}
