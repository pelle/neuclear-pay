package org.neuclear.asset.receiver.servlet;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.controllers.currency.CurrencyController;
import org.neuclear.asset.receiver.AssetControllerReceiver;
import org.neuclear.commons.crypto.signers.TestCaseSigner;
import org.neuclear.commons.sql.JNDIConnectionSource;
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

$Id: AssetControllerServlet.java,v 1.2 2003/11/19 23:32:20 pelle Exp $
$Log: AssetControllerServlet.java,v $
Revision 1.2  2003/11/19 23:32:20  pelle
Signers now can generatekeys via the generateKey() method.
Refactored the relationship between SignedNamedObject and NamedObjectBuilder a bit.
SignedNamedObject now contains the full xml which is returned with getEncoded()
This means that it is now possible to further send on or process a SignedNamedObject, leaving
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
public class AssetControllerServlet extends ReceiverServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        serviceid = config.getInitParameter("serviceid");
        datasource = config.getInitParameter("datasource");
        try {
            asset = (Asset) NSResolver.resolveIdentity(serviceid);
            AssetControllerReceiver receiver = new AssetControllerReceiver(
                    new CurrencyController(
                            new SQLLedger(
                                    new JNDIConnectionSource(datasource),
                                    serviceid
                            ),
                            serviceid
                    ),
                    new TestCaseSigner()
            );
            setReceiver(receiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Asset getAsset() {
        final byte test[] = "one two three".getBytes();
        test[0] = 0;

        return asset;
    }

    public String getServiceid() {
        return serviceid;
    }

    public String getDatasource() {
        return datasource;
    }

    private Asset asset;
    private String serviceid;
    private String datasource;
}
