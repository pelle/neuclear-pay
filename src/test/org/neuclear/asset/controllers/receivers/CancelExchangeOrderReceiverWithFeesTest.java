package org.neuclear.asset.controllers.receivers;

/*
 *  The NeuClear Project and it's libraries are
 *  (c) 2002-2004 Antilles Software Ventures SA
 *  For more information see: http://neuclear.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.Amount;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.contracts.ExchangeAgentGlobals;
import org.neuclear.exchange.orders.BidItem;
import org.neuclear.exchange.orders.CancelExchangeReceipt;
import org.neuclear.exchange.orders.ExchangeOrderGlobals;
import org.neuclear.exchange.orders.ExchangeOrderReceipt;
import org.neuclear.exchange.orders.builders.CancelExchangeOrderBuilder;
import org.neuclear.exchange.orders.builders.ExchangeOrderBuilder;
import org.neuclear.id.Signatory;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.ledger.simple.SimpleLedgerController;

import java.security.GeneralSecurityException;
import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 21, 2004
 * Time: 1:38:45 PM
 */
public class CancelExchangeOrderReceiverWithFeesTest extends AbstractExchangeReceiverTest {

    public CancelExchangeOrderReceiverWithFeesTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
        ExchangeOrderGlobals.registerReaders();
        ExchangeAgentGlobals.registerReaders();
        AssetGlobals.registerReaders();
    }

    protected void setUp() throws Exception {
        generatePrimaryTestAssetWithFees();
        ledger = new SimpleLedgerController("test");
        receiver = createReceiver();
        agent = createTestExchangeAgent();
        shoes = createShoeAsset();
    }

    protected Receiver createReceiver() {
        return new CancelExchangeOrderReceiver(signer, ledger);
    }

    public void testCancelExchangeOrder() throws NeuClearException, InvalidTransferException, LowlevelLedgerException, UnknownBookException, InvalidTransactionException {
        Signatory sender = new Signatory(signer.getPublicKey("bob"));
        Signatory recipient = new Signatory(signer.getPublicKey("alice"));
        final double amount = 10;
        final double fee = asset.getFeeStructure().calculateFee(amount);
        final double senderstart = ledger.getBalance(asset.getServiceId(), sender.getName());
        ledger.transfer(asset.getServiceId(), "bluesky", sender.getName(), amount - senderstart, "bla");
        assertEquals(amount, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);
        assertEquals(amount, ledger.getAvailableBalance(asset.getServiceId(), sender.getName()), 0);

        SignedNamedObject order = new ExchangeOrderBuilder(asset, agent, new Amount(amount), new Date(System.currentTimeMillis() + 50000), new BidItem[]{new BidItem(shoes, new Amount(amount))}, "test").convert("bob", signer);
        ExchangeOrderReceipt receipt = (ExchangeOrderReceipt) new ExchangeOrderReceiver(signer, ledger).receive(order);
        assertEquals(amount - fee, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);
        assertEquals(0, ledger.getAvailableBalance(asset.getServiceId(), sender.getName()), 0);

        assertEquals(0, ledger.getBalance(asset.getServiceId(), recipient.getName()), 0);
        assertEquals(0, ledger.getAvailableBalance(asset.getServiceId(), recipient.getName()), 0);

        CancelExchangeReceipt cancelled = (CancelExchangeReceipt) receiver.receive(new CancelExchangeOrderBuilder(receipt).convert("exchange", signer));
        assertNotNull(cancelled);
        assertEquals(amount - fee, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);
        assertEquals(amount - fee, ledger.getAvailableBalance(asset.getServiceId(), sender.getName()), 0);
        assertEquals(0, ledger.getBalance(asset.getServiceId(), recipient.getName()), 0);
        assertEquals(0, ledger.getAvailableBalance(asset.getServiceId(), recipient.getName()), 0);

    }

}
