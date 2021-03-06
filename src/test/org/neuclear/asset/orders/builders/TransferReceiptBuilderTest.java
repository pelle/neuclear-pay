package org.neuclear.asset.orders.builders;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.NonExistingSignerException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
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

$Id: TransferReceiptBuilderTest.java,v 1.6 2004/09/08 17:27:40 pelle Exp $
$Log: TransferReceiptBuilderTest.java,v $
Revision 1.6  2004/09/08 17:27:40  pelle
Some unit tests were not registrering their readers.

Revision 1.5  2004/05/24 18:31:32  pelle
Changed asset id in ledger to be asset.getSignatory().getName().
Made SigningRequestServlet and SigningServlet a bit clearer.

Revision 1.4  2004/04/23 23:33:15  pelle
Major update. Added an original url and nickname to Identity and friends.

Revision 1.3  2004/04/17 19:28:00  pelle
Identity is now fully html based as is the ServiceBuilder.
VerifyingReader correctly identifies html files and parses them as such.
Targets and Target now parse html link tags
AssetBuilder and ExchangeAgentBuilder have been updated to support it and provide html formatted contracts.
The Asset.Reader and ExchangeAgent.Reader still need to be updated.

Revision 1.2  2004/04/05 16:31:43  pelle
Created new ServiceBuilder class for creating services. A service is an identity that has a seperate service URL and Service Public Key.

Revision 1.1  2004/04/02 23:04:36  pelle
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
public class TransferReceiptBuilderTest extends AbstractObjectCreationTest {
    public TransferReceiptBuilderTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
        AssetGlobals.registerReaders();
        TransferGlobals.registerReaders();
        asset = createTestAsset();
    }

    protected void verifyObject(SignedNamedObject obj) throws NonExistingSignerException {
        assertNotNull(obj);
        assertTrue(obj instanceof TransferReceipt);
        TransferReceipt receipt = (TransferReceipt) obj;
        assertNotNull(receipt.getValueTime());
        assertEquals(asset.getDigest(), receipt.getAsset().getDigest());
        assertEquals(getSigner().getPublicKey("neu://test").getEncoded(), receipt.getSignatory().getPublicKey().getEncoded());

        TransferOrder order = receipt.getOrder();
        assertNotNull(order);
        assertEquals(asset.getDigest(), order.getAsset().getDigest());
        assertEquals(getSigner().getPublicKey("neu://test").getEncoded(), order.getSignatory().getPublicKey().getEncoded());
//        assertEquals(getBob().getPublicKey().getEncoded(), order.getRecipient().getSignatory().getPublicKey().getEncoded());
        assertEquals("Test", order.getComment());
        assertEquals(20.0, order.getAmount().getAmount(), 0);
    }

    protected Class getRequiredClass() {
        return TransferReceipt.class;
    }

    protected Builder createBuilder() throws NeuClearException, InvalidTransferException, XMLException {
        Builder builder = new TransferOrderBuilder(asset, getAlice(), new Amount(20), "Test");
//        System.out.println(builder.asXML());
        return new TransferReceiptBuilder((TransferOrder) builder.convert(NAME, getSigner()), new Date());
    }

    public Asset createTestAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("bux", "http://bux.neuclear.org", "http://bux.neuclear.org",
                getSigner().getPublicKey("neu://test/bux"),
                getAlice().getPublicKey(),
                2, 0, "bux");
        return (Asset) builder.convert(NAME, getSigner());

    }

    private Asset asset;
}
