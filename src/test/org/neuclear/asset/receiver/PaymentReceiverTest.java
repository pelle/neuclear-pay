package org.neuclear.asset.receiver;

import org.dom4j.DocumentException;
import org.neuclear.asset.AssetController;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.configuration.Configuration;
import org.neuclear.commons.configuration.ConfigurationException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.ledger.BookExistsException;
import org.neuclear.ledger.LedgerCreationException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.asset.controllers.currency.CurrencyController;
import org.neuclear.tests.AbstractReceiverTest;
import org.neuclear.receiver.Receiver;
import org.neuclear.xml.XMLException;

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

$Id: PaymentReceiverTest.java,v 1.3 2003/11/11 21:17:20 pelle Exp $
$Log: PaymentReceiverTest.java,v $
Revision 1.3  2003/11/11 21:17:20  pelle
Further vital reshuffling.
org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
Did a bit of work on the Canonicalizer and changed a few other minor bits.

Revision 1.2  2003/11/10 17:42:08  pelle
The AssetController interface has been more or less finalized.
CurrencyController fully implemented
AssetControlClient implementes a remote client for communicating with AssetControllers

Revision 1.1  2003/11/09 03:26:48  pelle
More house keeping and shuffling about mainly pay

Revision 1.5  2003/11/08 01:39:58  pelle
WARNING this rev is majorly unstable and will almost certainly not compile.
More major refactoring in neuclear-pay.
Got rid of neuclear-ledger like features of pay such as Account and Issuer.
Accounts have been replaced by Identity from neuclear-id
Issuer is now Asset which is a subclass of Identity
AssetController supports more than one Asset. Which is important for most non ecurrency implementations.
TransferRequest/Receipt and its Held companions are now SignedNamedObjects. Thus to create them you must use
their matching TransferRequest/ReceiptBuilder classes.
PaymentProcessor has been renamed CurrencyController. I will extract a superclass later to be named AbstractLedgerController
which will handle all neuclear-ledger based AssetControllers.

Revision 1.4  2003/11/06 23:47:44  pelle
Major Refactoring of CurrencyController.
Factored out AssetController to be new abstract parent class together with most of its support classes.
Created (Half way) AssetControlClient, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the AssetControlClient.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

Revision 1.3  2003/10/29 21:14:45  pelle
Refactored the whole signing process. Now we have an interface called Signer which is the old SignerStore.
To use it you pass a byte array and an alias. The sign method then returns the signature.
If a Signer needs a passphrase it uses a PassPhraseAgent to present a dialogue box, read it from a command line etc.
This new Signer pattern allows us to use secure signing hardware such as N-Cipher in the future for server applications as well
as SmartCards for end user applications.

Revision 1.2  2003/10/28 23:42:47  pelle
The GuiDialogAgent now works. It simply presents itself as a simple modal dialog box asking for a passphrase.
The two Signer implementations both use it for the passphrase.

Revision 1.1  2003/10/25 00:46:29  pelle
Added tests to test the AssetControllerReceiver.
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
        proc = (AssetController) Configuration.getComponent(CurrencyController.class, "neuclear-pay");
        receiver = (AssetControllerReceiver) Configuration.getComponent(AssetControllerReceiver.class, "neuclear-pay");
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


    public Object getPreTransactionState(SignedNamedObject obj) throws Exception {

/*
        if (obj instanceof AssetTransactionContract) {
            AssetTransactionContract transfer = (AssetTransactionContract) obj;
            Account fromAccount = createNewAccount(transfer.getSignatory().getName());
            double fromBalance = (fromAccount != null) ? fromAccount.getBalance(transfer.getTimeStamp()) : 0;
            Account toAccount = createNewAccount(transfer.getRecipient());
            double toBalance = (toAccount != null) ? toAccount.getBalance(transfer.getTimeStamp()) : 0;
            ;
            return new double[]{fromBalance, toBalance};

        }
*/
        return null; //No state to report
    }

    public boolean verifyTransaction(final SignedNamedObject obj, final Object state) throws Exception {
/*
        if (obj instanceof AssetTransactionContract && state != null) {
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
*/
        return false;
    }

    private Receiver receiver;
    private AssetController proc;
    private double balance = 0.0;
}
