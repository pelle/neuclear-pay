package org.neuclear.asset.codesamples;

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
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.IssueReceipt;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.asset.orders.builders.IssueOrderBuilder;
import org.neuclear.asset.orders.builders.TransferOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.signers.TestCaseSigner;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.Signatory;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.id.resolver.Resolver;

/**
 * User: pelleb
 * Date: Apr 29, 2004
 * Time: 10:00:15 AM
 */
public class MassTransfer {

    public static void main(String args[]) {
        AssetGlobals.registerReaders();
        TransferGlobals.registerReaders();

        try {
            Asset asset = (Asset) Resolver.resolveIdentity("http://bux.neuclear.org/bux.html");
            TestCaseSigner signer = new TestCaseSigner();
            final Signatory bob = new Signatory(signer.getPublicKey("bob"));
            Builder builder = new IssueOrderBuilder(asset, bob, new Amount(10000), "Issue from commandline");
            IssueReceipt ir = (IssueReceipt) asset.service(builder.convert("carol", signer));
            System.out.println("Issuance performed, ID: " + ir.getDigest());
            System.out.println("Issuance Time: " + ir.getValueTime());

            for (int i = 0; i < 100; i++)
                performTransfer(signer, asset);
            System.exit(0);

        } catch (NameResolutionException e) {
            e.printStackTrace();
        } catch (InvalidNamedObjectException e) {
            e.printStackTrace();
        } catch (UserCancellationException e) {
            e.printStackTrace();
        } catch (NegativeTransferException e) {
            e.printStackTrace();
        } catch (InvalidTransferException e) {
            e.printStackTrace();
        } catch (NeuClearException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    private static void performTransfer(TestCaseSigner signer, Asset asset) throws InvalidTransferException, NeuClearException {
        final Signatory alice = new Signatory(signer.getPublicKey("alice"));
        Builder builder = new TransferOrderBuilder(asset, alice, new Amount(100), "transfer via commandline 'MassTransfer'");
        SignedNamedObject obj = builder.convert("bob", signer);
        TransferReceipt receipt = (TransferReceipt) asset.service(obj);
        System.out.println("Transfer Made, ID: " + receipt.getDigest());
        System.out.println("Transfer Time: " + receipt.getValueTime());
    }
}
