package org.neuclear.pay.receiver;

import org.dom4j.DocumentException;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.configuration.ConfigurationException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.ledger.BookExistsException;
import org.neuclear.ledger.LedgerCreationException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.pay.Account;
import org.neuclear.pay.PaymentProcessor;
import org.neuclear.pay.contracts.TransferContract;
import org.neuclear.receiver.AbstractReceiverTest;
import org.neuclear.receiver.Receiver;
import org.neudist.xml.XMLException;

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

$Id: PaymentReceiverTest.java,v 1.2 2003/10/28 23:42:47 pelle Exp $
$Log: PaymentReceiverTest.java,v $
Revision 1.2  2003/10/28 23:42:47  pelle
The PassPhraseDialogue now works. It simply presents itself as a simple modal dialog box asking for a passphrase.
The two SignerStore implementations both use it for the passphrase.

Revision 1.1  2003/10/25 00:46:29  pelle
Added tests to test the PaymentReceiver.
CreateTestPayments is a command line utility to create signed payment requests

*/

/**
 * User: pelleb
 * Date: Oct 24, 2003
 * Time: 11:20:31 AM
 */
public class PaymentReceiverTest extends AbstractReceiverTest {
    public PaymentReceiverTest(String string) throws LowlevelLedgerException, LedgerCreationException, ConfigurationException {
        super(string);
        proc = PaymentProcessor.getInstance();
        receiver = new PaymentReceiver(proc, "neu://test/pay");
    }

    public void testSimple() throws Exception, DocumentException, NeuClearException, XMLException {
        runDirectoryTest("src/testdata/payments");
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public String getExtension() {
        return ".xml";
    }

    private Account createNewAccount(String name) throws BookExistsException, LowlevelLedgerException {
        try {
            return proc.getAccount(name);
        } catch (UnknownBookException e) {
            return proc.createAccount(name, name);
        }

    }

    public Object getPreTransactionState(SignedNamedObject obj) throws Exception {

        if (obj instanceof TransferContract) {
            TransferContract transfer = (TransferContract) obj;
            Account fromAccount = createNewAccount(transfer.getSignatory().getName());
            double fromBalance = (fromAccount != null) ? fromAccount.getBalance(transfer.getTimeStamp()) : 0;
            Account toAccount = createNewAccount(transfer.getRecipient());
            double toBalance = (toAccount != null) ? toAccount.getBalance(transfer.getTimeStamp()) : 0;
            ;
            return new double[]{fromBalance, toBalance};

        }
        return null; //No state to report
    }

    public boolean verifyTransaction(final SignedNamedObject obj, final Object state) throws Exception {
        if (obj instanceof TransferContract && state != null) {
            TransferContract transfer = (TransferContract) obj;
            final double prebalances[] = (double[]) state;
            Account fromAccount = createNewAccount(transfer.getSignatory().getName());
            Account toAccount = createNewAccount(transfer.getRecipient());
            double fromBalance = (fromAccount != null) ? fromAccount.getBalance(transfer.getTimeStamp()) : 0;
            double toBalance = (toAccount != null) ? toAccount.getBalance(transfer.getTimeStamp()) : 0;
            ;

            return (fromBalance == prebalances[0] - transfer.getAmount()) &&
                    (toBalance == prebalances[1] + transfer.getAmount());
        }
        return false;
    }

    private Receiver receiver;
    private PaymentProcessor proc;
    private double balance = 0.0;
}
