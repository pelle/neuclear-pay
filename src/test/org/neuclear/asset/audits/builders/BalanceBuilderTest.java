package org.neuclear.asset.audits.builders;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.audits.Balance;
import org.neuclear.asset.audits.BalanceRequest;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.NonExistingSignerException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.id.resolver.Resolver;
import org.neuclear.tests.AbstractObjectCreationTest;
import org.neuclear.xml.XMLException;

import java.security.GeneralSecurityException;
import java.util.Date;

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

$Id: BalanceBuilderTest.java,v 1.1 2004/07/21 16:00:54 pelle Exp $
$Log: BalanceBuilderTest.java,v $
Revision 1.1  2004/07/21 16:00:54  pelle
Added Balance and History related classes.

Revision 1.5  2004/05/12 18:07:53  pelle
Fixed lotsof problems found in the unit tests.

Revision 1.4  2004/04/28 00:22:29  pelle
Fixed the strange verification error
Added bunch of new unit tests to support this.
Updated Signer's dependencies and version number to be a 0.9 release.
Implemented ThreadLocalSession session management for Hibernate ledger.
Various other minor changes.

Revision 1.3  2004/04/23 23:33:15  pelle
Major update. Added an original url and nickname to Identity and friends.

Revision 1.2  2004/04/17 19:28:00  pelle
Identity is now fully html based as is the ServiceBuilder.
VerifyingReader correctly identifies html files and parses them as such.
Targets and Target now parse html link tags
AssetBuilder and ExchangeAgentBuilder have been updated to support it and provide html formatted contracts.
The Asset.Reader and ExchangeAgent.Reader still need to be updated.

Revision 1.1  2004/04/06 16:24:35  pelle
Added two new Data Objects IssuerOrder and IssueReceipt for managing the issuance process.
Added Issuance support to the Asset and Audit Controllers.
Implemented access control for complete and cancel exchange orders.

Revision 1.7  2004/04/05 16:31:43  pelle
Created new ServiceBuilder class for creating services. A service is an identity that has a seperate service URL and Service Public Key.

Revision 1.6  2004/04/02 23:04:36  pelle
Got TransferOrder and Builder working with their test cases.
Working on TransferReceipt which is the first embedded receipt. This is causing some problems at the moment.

Revision 1.5  2004/04/02 17:56:16  pelle
Added new createTestAsset() method.

Revision 1.4  2004/04/01 23:18:33  pelle
Split Identity into Signatory and Identity class.
Identity remains a signed named object and will in the future just be used for self declared information.
Signatory now contains the PublicKey etc and is NOT a signed object.

Revision 1.3  2004/03/03 23:28:14  pelle
Updated tests to use AbstractObjectCreationTest

Revision 1.2  2004/02/18 00:13:31  pelle
Many, many clean ups. I've readded Targets in a new method.
Gotten rid of NamedObjectBuilder and revamped Identity and Resolvers

Revision 1.1  2004/01/21 23:41:02  pelle
Started the unit tests for the new payment message format.

*/

/**
 * User: pelleb
 * Date: Jan 21, 2004
 * Time: 9:11:44 PM
 */
public class BalanceBuilderTest extends AbstractObjectCreationTest {
    public BalanceBuilderTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
        AssetGlobals.registerReaders();
        TransferGlobals.registerReaders();

        asset = createTestAsset();
    }

    protected void verifyObject(SignedNamedObject obj) throws NonExistingSignerException {
        assertNotNull(obj);
        assertTrue(obj instanceof Balance);
        Balance balance = (Balance) obj;
        assertEquals(100.0, balance.getBalance(), 0);
        assertEquals(90.0, balance.getAvailable(), 0);
        assertNotNull(balance.getTime());
        assertNotNull(balance.getReq());
        BalanceRequest req = balance.getReq();
        assertEquals(asset.getDigest(), req.getAsset().getDigest());
        assertEquals(getSigner().getPublicKey("alice").getEncoded(), req.getSignatory().getPublicKey().getEncoded());
    }

    protected Class getRequiredClass() {
        return Balance.class;
    }

    protected String getSignersAlias() {
        return "carol";    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected Builder createBuilder() throws NeuClearException, InvalidTransferException, XMLException {
        Builder builder = new BalanceBuilder((BalanceRequest) new BalanceRequestBuilder(asset).convert("alice", getSigner()), 100.0, 90.0, new Date());
//        System.out.println(builder.asXML());
        return builder;
    }

    public Asset createTestAsset() throws NeuClearException {
//        Builder builder = new AssetBuilder("bux", "http://bux.neuclear.org/bux.html", "http://bux.neuclear.org/Asset",
//                getSigner().getPublicKey("bux"),
//                getAlice().getPublicKey(),
//                2, 0, "bux");
//        return (Service) builder.convert(NAME, getSigner());
        return (Asset) Resolver.resolveIdentity("http://bux.neuclear.org/bux.html");
    }

    private Asset asset;
}
