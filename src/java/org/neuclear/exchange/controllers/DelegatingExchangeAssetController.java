package org.neuclear.exchange.controllers;

import org.neuclear.asset.controllers.DelegatingAssetController;
import org.neuclear.commons.crypto.signers.Signer;
import org.neuclear.exchange.controllers.receivers.*;
import org.neuclear.ledger.LedgerController;

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

/**
 * An asset controller with support for exchange transactions.
 */
public class DelegatingExchangeAssetController extends DelegatingAssetController {
    public DelegatingExchangeAssetController(Signer signer, LedgerController ledger) {
        super(signer, ledger);
        register(new ExchangeOrderReceiver(this, signer, ledger));
        register(new ExchangeOrderReceiptReceiver(this, ledger));
        register(new ExchangeCompletionOrderReceiver(this, signer, ledger));
        register(new ExchangeCompletedReceiptReceiver(this, ledger));
        register(new CancelExchangeOrderReceiver(this, signer, ledger));
        register(new CancelExchangeReceiptReceiver(this, ledger));
    }
}
