package org.neuclear.exchange.orders.builders;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.orders.Amount;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.signers.NonExistingSignerException;
import org.neuclear.exchange.orders.*;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
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

$Id: ExchangeCompletedReceiptBuilderTest.java,v 1.2 2004/04/14 23:51:13 pelle Exp $
$Log: ExchangeCompletedReceiptBuilderTest.java,v $
Revision 1.2  2004/04/14 23:51:13  pelle
Fixed Exchange tests and Cactus tests working on web app.

Revision 1.1  2004/04/05 16:31:45  pelle
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
public class ExchangeCompletedReceiptBuilderTest extends AbstractExchangeOrderTest {
    public ExchangeCompletedReceiptBuilderTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
    }

    protected void verifyObject(SignedNamedObject obj) throws NonExistingSignerException {
        assertNotNull(obj);
        assertTrue(obj instanceof ExchangeCompletedReceipt);
        ExchangeCompletedReceipt cr = (ExchangeCompletedReceipt) obj;
        assertEquals(getPublicKeyName("bux"), cr.getSignatory().getName());
        assertNotNull(cr.getOrder());
        assertNotNull(cr.getValueTime());

        ExchangeCompletionOrder complete = cr.getOrder();
        assertEquals(getPublicKeyName("exchange"), complete.getSignatory().getName());
        assertEquals("done", complete.getComment());
        assertEquals(19.0, complete.getAmount().getAmount(), 0);
        assertNotNull(complete.getExchangeTime());
        assertNotNull(complete.getReceipt());

        ExchangeOrderReceipt receipt = complete.getReceipt();
        assertEquals(getPublicKeyName("bux"), receipt.getSignatory().getName());
//        assertEquals(getSigner().getPublicKey("neu://test/bux").getEncoded(), receipt.getSignatory().getPublicKey().getEncoded());
        assertNotNull(receipt.getValuetime());
        assertNotNull(receipt.getOrder());

        ExchangeOrder order = receipt.getOrder();
        assertEquals(bux.getDigest(), order.getAsset().getDigest());
        assertEquals(getPublicKeyName("bob"), order.getSignatory().getName());
//      assertEquals(getSigner().getPublicKey("neu://test/bux").getEncoded(), order.getSignatory().getPublicKey().getEncoded());
//        assertEquals(getBob().getPublicKey().getEncoded(), order.getRecipient().getSignatory().getPublicKey().getEncoded());
        assertEquals("Test", order.getComment());
        assertEquals(20.0, order.getAmount().getAmount(), 0);
    }

    protected Class getRequiredClass() {
        return ExchangeCompletedReceipt.class;
    }

    protected Builder createBuilder() throws NeuClearException, InvalidTransferException, XMLException {
        BidItem bids[] = new BidItem[]{new BidItem(shoes, new Amount(5))};
        Builder ob = new ExchangeOrderBuilder(bux, agent, new Amount(20), new Date(System.currentTimeMillis() + 10000), bids, "Test");
        ExchangeOrderReceiptBuilder rb = new ExchangeOrderReceiptBuilder((ExchangeOrder) ob.convert("bob", getSigner()), new Date());

        ExchangeCompletionOrderBuilder cb = new ExchangeCompletionOrderBuilder((ExchangeOrderReceipt) rb.convert("bux", getSigner()), new Date(), "neu://alice@test", new Amount(19), "done");
        ExchangeCompletedReceiptBuilder builder = new ExchangeCompletedReceiptBuilder((ExchangeCompletionOrder) cb.convert("exchange", getSigner()), new Date());
        return builder;
    }

    protected String getSignersAlias() {
        return "bux";
    }

}
