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

$Id: CancelExchangeReceiptBuilderTest.java,v 1.2 2004/04/05 22:08:24 pelle Exp $
$Log: CancelExchangeReceiptBuilderTest.java,v $
Revision 1.2  2004/04/05 22:08:24  pelle
CurrencyController and AuditController now now pass all unit tests in CurrencyTests.

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
public class CancelExchangeReceiptBuilderTest extends AbstractExchangeOrderTest {
    public CancelExchangeReceiptBuilderTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
    }

    protected void verifyObject(SignedNamedObject obj) throws NonExistingSignerException {
        assertNotNull(obj);
        assertTrue(obj instanceof CancelExchangeReceipt);
        CancelExchangeReceipt cr = (CancelExchangeReceipt) obj;
        assertEquals(getSigner().getPublicKey("neu://test").getEncoded(), cr.getSignatory().getPublicKey().getEncoded());
        assertNotNull(cr.getCancellationTime());
        assertNotNull(cr.getOrder());

        CancelExchangeOrder cancel = (CancelExchangeOrder) cr.getOrder();
        assertEquals(getSigner().getPublicKey("neu://test/bux").getEncoded(), cancel.getSignatory().getPublicKey().getEncoded());
        assertNotNull(cancel.getReceipt());
        ExchangeOrderReceipt receipt = cancel.getReceipt();
        assertEquals(getSigner().getPublicKey("neu://test/bux").getEncoded(), receipt.getSignatory().getPublicKey().getEncoded());
        assertNotNull(receipt.getValuetime());
        assertNotNull(receipt.getOrder());
        ExchangeOrder order = receipt.getOrder();
        assertEquals(bux.getDigest(), order.getAsset().getDigest());
        assertEquals(getSigner().getPublicKey("neu://test/bux").getEncoded(), order.getSignatory().getPublicKey().getEncoded());
//        assertEquals(getBob().getPublicKey().getEncoded(), order.getRecipient().getSignatory().getPublicKey().getEncoded());
        assertEquals("Test", order.getComment());
        assertEquals(20.0, order.getAmount().getAmount(), 0);
    }

    protected Class getRequiredClass() {
        return CancelExchangeReceipt.class;
    }

    protected Builder createBuilder() throws NeuClearException, InvalidTransferException, XMLException {
        BidItem bids[] = new BidItem[]{new BidItem(shoes, new Amount(5))};
        Builder ob = new ExchangeOrderBuilder(bux, agent, new Amount(20), new Date(System.currentTimeMillis() + 10000), bids, "Test");
        ExchangeOrderReceiptBuilder rb = new ExchangeOrderReceiptBuilder((ExchangeOrder) ob.convert("neu://test/bux", getSigner()), new Date());
        CancelExchangeOrderBuilder cb = new CancelExchangeOrderBuilder((ExchangeOrderReceipt) rb.convert("neu://test/bux", getSigner()));
//        System.out.println(cb.asXML());

        final CancelExchangeOrder req = (CancelExchangeOrder) cb.convert("neu://test/bux", getSigner());
        CancelExchangeReceiptBuilder builder = new CancelExchangeReceiptBuilder(req, new Date());
        return builder;
    }

}
