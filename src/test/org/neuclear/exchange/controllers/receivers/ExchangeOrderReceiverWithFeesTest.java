package org.neuclear.exchange.controllers.receivers;

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
import org.neuclear.exchange.orders.ExchangeOrderGlobals;
import org.neuclear.exchange.orders.builders.ExchangeOrderBuilder;
import org.neuclear.id.Signatory;
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
public class ExchangeOrderReceiverWithFeesTest extends AbstractExchangeReceiverTest {

    public ExchangeOrderReceiverWithFeesTest(String string) throws NeuClearException, GeneralSecurityException {
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

    public void testExchangeOrder() throws NeuClearException, InvalidTransferException, LowlevelLedgerException, UnknownBookException, InvalidTransactionException {
        Signatory recipient = new Signatory(signer.getPublicKey("alice"));
        Signatory sender = new Signatory(signer.getPublicKey("bob"));
        final double senderstart = ledger.getBalance(asset.getServiceId(), sender.getName());
        double feeBalance = ledger.getBalance(asset.getServiceId(), asset.getFeeAccount().getName());
        final double amount = 10;
        ledger.transfer(asset.getServiceId(), "bluesky", sender.getName(), amount - senderstart, "bla");
        assertEquals(10, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);

        receiver.receive(new ExchangeOrderBuilder(asset, agent, new Amount(10), new Date(System.currentTimeMillis() + 100000), new BidItem[]{new BidItem(shoes, new Amount(10))}, "test").convert("bob", signer));
        double fee = asset.getFeeStructure().calculateFee(amount);
        assertEquals(feeBalance + fee, ledger.getBalance(asset.getServiceId(), asset.getFeeAccount().getName()), 0);
        assertEquals(senderstart, ledger.getAvailableBalance(asset.getServiceId(), sender.getName()), 0);
        assertEquals(10 - fee, ledger.getBalance(asset.getServiceId(), sender.getName()), 0);

    }

}
