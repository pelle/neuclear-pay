package org.neuclear.asset.controllers.currency;

import org.neuclear.commons.NeuClearException;
import org.neuclear.ledger.Ledger;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.prevalent.PrevalentLedger;

import java.io.IOException;
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

$Id: PrevalentCurrencyTests.java,v 1.1 2004/04/12 15:27:52 pelle Exp $
$Log: PrevalentCurrencyTests.java,v $
Revision 1.1  2004/04/12 15:27:52  pelle
Added Hibernate and Prevalent tests for Currency Controllers

Revision 1.1  2004/04/06 23:01:01  pelle
Create new PrevalentCurrencyTests TestCase for doing integration testing with various persistent ledgers.

*/

/**
 * User: pelleb
 * Date: Apr 6, 2004
 * Time: 8:11:02 PM
 */
public class PrevalentCurrencyTests extends CurrencyTests {

    public PrevalentCurrencyTests(String s) throws GeneralSecurityException, LowlevelLedgerException, NeuClearException, IOException, ClassNotFoundException {
        super(s);
    }

    protected Ledger createControllerLedger() throws IOException, ClassNotFoundException {
        return new PrevalentLedger("asset", "target/test-data/pl");
    }


}
