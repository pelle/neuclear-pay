package org.neuclear.asset.receiver.servlet;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.controllers.currency.CurrencyController;
import org.neuclear.asset.orders.transfers.TransferGlobals;
import org.neuclear.asset.receiver.AssetControllerReceiver;
import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.commons.sql.JNDIConnectionSource;
import org.neuclear.commons.sql.statements.SimpleStatementFactory;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.ledger.implementations.SQLLedger;
import org.neuclear.receiver.ReceiverServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

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

$Id: AssetControllerServlet.java,v 1.10 2004/01/10 00:00:45 pelle Exp $
$Log: AssetControllerServlet.java,v $
Revision 1.10  2004/01/10 00:00:45  pelle
Implemented new Schema for Transfer*
Working on it for Exchange*, so far all Receipts are implemented.
Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
Changed SignedNamedObject.getDigest() from byte array to String.
The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.

Revision 1.9  2004/01/05 23:47:09  pelle
Create new Document classification "order", which is really just inherint in the new
package layout.
Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.

Revision 1.8  2004/01/02 23:18:06  pelle
Added StatementFactory pattern and refactored the ledger to use it.

Revision 1.7  2003/12/17 23:52:57  pelle
Added SignatureRequestServlet which is abstract and can be used for building SignatureRequests for various applications.

Revision 1.6  2003/12/15 23:31:54  pelle
added ServletTools.getInitParam() which first tries the ServletConfig, then the context config.
All the web.xml's have been updated to support this. Also various further generalizations have been done throughout
for getServiceid(), getTitle(), getSigner()

Revision 1.5  2003/12/10 23:52:39  pelle
Did some cleaning up in the builders
Fixed some stuff in IdentityCreator
New maven goal to create executable jarapp
We are close to 0.8 final of ID, 0.11 final of XMLSIG and 0.5 of commons.
Will release shortly.

Revision 1.4  2003/11/22 00:22:28  pelle
All unit tests in commons, id and xmlsec now work.
AssetController now successfully processes payments in the unit test.
Payment Web App has working form that creates a TransferOrder presents it to the signer
and forwards it to AssetControlServlet. (Which throws an XML Parser Exception) I think the XMLReaderServlet is bust.

Revision 1.3  2003/11/21 04:43:04  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.2  2003/11/19 23:32:20  pelle
Signers now can generatekeys via the generateKey() method.
Refactored the relationship between SignedNamedObject and NamedObjectBuilder a bit.
SignedNamedObject now contains the full xml which is returned with getEncoded()
This means that it is now possible to further receive on or process a SignedNamedObject, leaving
NamedObjectBuilder for its original purposes of purely generating new Contracts.
NamedObjectBuilder.sign() now returns a SignedNamedObject which is the prefered way of processing it.
Updated all major interfaces that used the old model to use the new model.

Revision 1.1  2003/11/18 23:34:29  pelle
Payment Web Application is getting there.

*/

/**
 * User: pelleb
 * Date: Nov 18, 2003
 * Time: 6:18:18 PM
 */
public final class AssetControllerServlet extends ReceiverServlet {
    public final void init(final ServletConfig config) throws ServletException {
        super.init(config);
        datasource = ServletTools.getInitParam("datasource",config);
        AssetGlobals.registerReaders();
        TransferGlobals.registerReaders();
        try {
            asset = (Asset) NSResolver.resolveIdentity(getServiceid());
            final AssetControllerReceiver receiver = new AssetControllerReceiver(
                    new CurrencyController(
                            new SQLLedger(
                                    new SimpleStatementFactory(new JNDIConnectionSource(datasource)),
                                    getServiceid()
                            ),
                            getServiceid()
                    ),
                    getSigner()

            );
            setReceiver(receiver);

        } catch (Exception e) {
            ctx.log("AssetControllerServer: "+e.getLocalizedMessage());
            throw new ServletException(e);
        }
    }

    public final Asset getAsset() {
        return asset;
    }


    public final String getDatasource() {
        return datasource;
    }

    private Asset asset;
    private String datasource;
}
