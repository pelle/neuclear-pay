package org.neuclear.asset.receiver;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.contracts.TransferGlobals;
import org.neuclear.asset.contracts.TransferRequest;
import org.neuclear.asset.contracts.builders.TransferRequestBuilder;
import org.neuclear.asset.controllers.currency.CurrencyController;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.sql.DefaultConnectionSource;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.id.Identity;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.ledger.BookExistsException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownLedgerException;
import org.neuclear.ledger.implementations.SQLLedger;
import org.neuclear.receiver.Receiver;
import org.neuclear.tests.AbstractSigningTest;
import org.neuclear.xml.XMLException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

$Id: PaymentReceiverTest.java,v 1.7 2003/11/22 00:22:29 pelle Exp $
$Log: PaymentReceiverTest.java,v $
Revision 1.7  2003/11/22 00:22:29  pelle
All unit tests in commons, id and xmlsec now work.
AssetController now successfully processes payments in the unit test.
Payment Web App has working form that creates a TransferRequest presents it to the signer
and forwards it to AssetControlServlet. (Which throws an XML Parser Exception) I think the XMLReaderServlet is bust.

Revision 1.6  2003/11/21 04:43:04  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.5  2003/11/19 23:32:20  pelle
Signers now can generatekeys via the generateKey() method.
Refactored the relationship between SignedNamedObject and NamedObjectBuilder a bit.
SignedNamedObject now contains the full xml which is returned with getEncoded()
This means that it is now possible to further send on or process a SignedNamedObject, leaving
NamedObjectBuilder for its original purposes of purely generating new Contracts.
NamedObjectBuilder.sign() now returns a SignedNamedObject which is the prefered way of processing it.
Updated all major interfaces that used the old model to use the new model.

Revision 1.4  2003/11/12 23:47:05  pelle
Much work done in creating good test environment.
PaymentReceiverTest works, but needs a abit more work in its environment to succeed testing.

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
public final class PaymentReceiverTest extends AbstractSigningTest {
    public PaymentReceiverTest(final String string) throws NeuClearException, GeneralSecurityException, UnknownLedgerException, LowlevelLedgerException, BookExistsException, IOException, InvalidTransferException, XMLException {
        super(string);
        AssetGlobals.registerReaders();
        TransferGlobals.registerReaders();
        asset = (Asset) NSResolver.resolveIdentity(assetName);

        proc = new CurrencyController(
                new SQLLedger(
                        new DefaultConnectionSource(),
                        assetName
                )
                , assetName
        );
        receiver = new AssetControllerReceiver(proc, getSigner());
    }


    public final Receiver getReceiver() {
        return receiver;
    }

    public final String getExtension() {
        return ".xml";
    }

    public final Asset getAsset() {
        return asset;
    }

    public final void testTransactions() throws Exception, IOException, InvalidTransferException, NeuClearException {
        performTransaction(createPayments(getAlice(), getBob(), 100));
        performTransaction(createPayments(getBob(), getAlice(), 100));
    }

    public void performTransaction(SignedNamedObject obj) throws Exception {
        Object pre = getPreTransactionState(obj);
        org.neuclear.xml.ElementProxy receipt = receiver.receive(obj);
        assertTrue(verifyTransaction(obj, pre));
    }

    public final Object getPreTransactionState(final SignedNamedObject obj) throws Exception {

        if (obj instanceof TransferRequest) {
            final TransferRequest transfer = (TransferRequest) obj;
            final double fromBalance = proc.getBalance(transfer.getFrom(), transfer.getTimeStamp());
            final double toBalance = proc.getBalance(transfer.getTo(), transfer.getTimeStamp());

            return new double[]{fromBalance, toBalance};

        }
        return null; //No state to report
    }

    public final boolean verifyTransaction(final SignedNamedObject obj, final Object state) throws Exception {
        if (obj instanceof TransferRequest) {
            final TransferRequest transfer = (TransferRequest) obj;
            final double fromBalance = proc.getBalance(transfer.getFrom(), transfer.getTimeStamp());
            final double toBalance = proc.getBalance(transfer.getTo(), transfer.getTimeStamp());
            final double prebalances[] = (double[]) state;

            return (fromBalance == prebalances[0] - transfer.getAmount()) &&
                    (toBalance == prebalances[1] + transfer.getAmount());
        }
        return false;
    }

    public final SignedNamedObject createPayments(final Identity from, final Identity to, final double amount) throws InvalidTransferException, XMLException, NeuClearException, IOException, UnsupportedEncodingException {
        final TransferRequestBuilder transfer = new TransferRequestBuilder(asset, from, to, amount, TimeTools.now(), "Test One");
        return transfer.sign(getSigner());
    }

    protected final String assetName = "neu://test/bux";

    private final Asset asset;
    private final Receiver receiver;
    private final CurrencyController proc;
    private final double balance = 0.0;

    static {
        AssetGlobals.class.getClass();
        TransferGlobals.class.getClass();
    }
}
