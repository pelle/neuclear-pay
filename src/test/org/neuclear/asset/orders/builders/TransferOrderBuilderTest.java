package org.neuclear.asset.orders.builders;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.NonExistingSignerException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.tests.AbstractObjectCreationTest;
import org.neuclear.xml.XMLException;

import java.security.GeneralSecurityException;

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

$Id: TransferOrderBuilderTest.java,v 1.2 2004/02/18 00:13:31 pelle Exp $
$Log: TransferOrderBuilderTest.java,v $
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
public class TransferOrderBuilderTest extends AbstractObjectCreationTest{
    public TransferOrderBuilderTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
        asset=createTestAsset();
    }

    protected void verifyObject(SignedNamedObject obj) throws NonExistingSignerException {
        assertNotNull(obj);
        assertTrue(obj instanceof TransferOrder);
        TransferOrder order=(TransferOrder) obj;
        assertEquals(asset.getDigest(),order.getAsset().getDigest());
        assertEquals(getSigner().getPublicKey("neu://test").getEncoded(),order.getSignatory().getPublicKey().getEncoded());
        assertEquals(getBob().getPublicKey().getEncoded(),order.getRecipient().getPublicKey().getEncoded());
        assertEquals("Test",order.getComment());
        assertEquals(20.0,order.getAmount().getAmount(),0);
    }

    protected Builder createBuilder() throws NeuClearException, InvalidTransferException, XMLException {
        Builder builder=new TransferOrderBuilder("neu://test/bux","neu://bob@test",new Amount(20),"Test");
        System.out.println(builder.asXML());
        return builder;
    }
    public  Asset createTestAsset() throws NonExistingSignerException {
//        return new Asset("http://localhost",getSigner().getPublicKey("neu://test/bux"),1,1);
        return null;//TODO FIX
    }

    private Asset asset;
}
