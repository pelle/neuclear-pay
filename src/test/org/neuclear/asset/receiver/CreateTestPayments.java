package org.neuclear.asset.receiver;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.builders.TransferRequestBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.configuration.ConfigurationException;
import org.neuclear.id.builders.NamedObjectBuilder;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.id.tools.commandline.CommandLineSigner;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
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

$Id: CreateTestPayments.java,v 1.6 2003/12/10 23:52:39 pelle Exp $
$Log: CreateTestPayments.java,v $
Revision 1.6  2003/12/10 23:52:39  pelle
Did some cleaning up in the builders
Fixed some stuff in IdentityCreator
New maven goal to create executable jarapp
We are close to 0.8 final of ID, 0.11 final of XMLSIG and 0.5 of commons.
Will release shortly.

Revision 1.5  2003/12/09 18:11:30  pelle
Moved Command Line tools to org.neuclear.id.tools.commandline

Revision 1.4  2003/11/21 04:43:04  pelle
EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
Otherwise You will Finaliate.
Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
This should hopefully make everything more stable (and secure).

Revision 1.3  2003/11/12 23:47:05  pelle
Much work done in creating good test environment.
PaymentReceiverTest works, but needs a abit more work in its environment to succeed testing.

Revision 1.2  2003/11/10 17:42:08  pelle
The AssetController interface has been more or less finalized.
CurrencyController fully implemented
AssetControlClient implementes a remote client for communicating with AssetControllers

Revision 1.1  2003/11/09 03:26:48  pelle
More house keeping and shuffling about mainly pay

Revision 1.3  2003/11/08 01:39:58  pelle
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

Revision 1.2  2003/10/29 21:14:44  pelle
Refactored the whole signing process. Now we have an interface called Signer which is the old SignerStore.
To use it you pass a byte array and an alias. The sign method then returns the signature.
If a Signer needs a passphrase it uses a PassPhraseAgent to present a dialogue box, read it from a command line etc.
This new Signer pattern allows us to use secure signing hardware such as N-Cipher in the future for server applications as well
as SmartCards for end user applications.

Revision 1.1  2003/10/25 00:46:29  pelle
Added tests to test the AssetControllerReceiver.
CreateTestPayments is a command line utility to create signed payment requests

*/

/**
 * User: pelleb
 * Date: Oct 24, 2003
 * Time: 11:50:47 AM
 */
public final class CreateTestPayments extends CommandLineSigner {
    public CreateTestPayments(final String[] args) throws ParseException, GeneralSecurityException, CertificateException, IOException, KeyStoreException, ConfigurationException, NeuClearException {
        super(args);

    }

    public final NamedObjectBuilder build() throws Exception {
        final String to = cmd.getOptionValue("r");
        final String asset = cmd.getOptionValue("c");
        final double amount = Double.parseDouble(cmd.getOptionValue("x"));

        return new TransferRequestBuilder(
                (Asset) NSResolver.resolveIdentity(asset),
                NSResolver.resolveIdentity(alias),
                NSResolver.resolveIdentity(to),
                amount,
                new Date(),
                "Test Request"
        );
    }

    public static void main(final String[] args) {
        try {
            final CreateTestPayments signer = new CreateTestPayments(args);
            signer.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected final String getExtraHelp() {
        return "[--assetName neu://verax/pay --payee neu://bob@verax --amount 20.00]";
    }

    protected final boolean hasArguments() {
        return (cmd.hasOption("a") && cmd.hasOption("c") && cmd.hasOption("r") && cmd.hasOption("x"));
    }

    protected final void getLocalOptions(final Options options) {
        options.addOption("c", "assetName", true, "specify id of assetName");
        options.addOption("r", "payee", true, "specify id of payee");
        options.addOption("x", "amount", true, "specify amount");
    }

}
