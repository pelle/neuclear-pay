package org.neuclear.asset.servlet;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.builders.TransferOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.id.Identity;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.builders.Builder;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.id.signers.SignatureRequestServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
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

$Id: TransferRequestServlet.java,v 1.5 2004/05/24 18:31:30 pelle Exp $
$Log: TransferRequestServlet.java,v $
Revision 1.5  2004/05/24 18:31:30  pelle
Changed asset id in ledger to be asset.getSignatory().getName().
Made SigningRequestServlet and SigningServlet a bit clearer.

Revision 1.4  2004/04/02 23:04:36  pelle
Got TransferOrder and Builder working with their test cases.
Working on TransferReceipt which is the first embedded receipt. This is causing some problems at the moment.

Revision 1.3  2004/04/01 23:18:33  pelle
Split Identity into Signatory and Identity class.
Identity remains a signed named object and will in the future just be used for self declared information.
Signatory now contains the PublicKey etc and is NOT a signed object.

Revision 1.2  2004/03/02 18:58:35  pelle
Further cleanups in neuclear-id. Moved everything under id.

Revision 1.1  2004/01/13 15:11:17  pelle
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
public class TransferRequestServlet extends SignatureRequestServlet {

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        try {
            asset = (Asset) Resolver.resolveIdentity(getServiceid());
        } catch (NameResolutionException e) {
            e.printStackTrace();
        } catch (InvalidNamedObjectException e) {
            e.printStackTrace();
        }
    }

    protected Builder createBuilder(HttpServletRequest request) throws NeuClearException {
        Identity user = (Identity) request.getUserPrincipal();
        if (user == null)
            user = Resolver.resolveIdentity(request.getParameter("sender"));
        String to = request.getParameter("recipient");
        double amount = Double.parseDouble(Utility.denullString(request.getParameter("amount"), "0"));
        String comment = Utility.denullString(request.getParameter("comment"));
        try {
            return new TransferOrderBuilder(asset, to, new Amount(amount), comment);
        } catch (InvalidTransferException e) {
            throw new InvalidNamedObjectException(user.getName(), e);
        }
    }

    protected String getRequestType() {
        return "Transfer Order";
    }

    private Asset asset;
}
