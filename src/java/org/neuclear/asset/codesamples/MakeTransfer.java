package org.neuclear.asset.codesamples;

import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.TransferReceipt;
import org.neuclear.asset.orders.builders.TransferOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.passphraseagents.swing.SwingAgent;
import org.neuclear.commons.crypto.signers.DefaultSigner;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.id.resolver.Resolver;

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
 * User: pelleb
 * Date: Apr 29, 2004
 * Time: 10:00:15 AM
 */
public class MakeTransfer {

    public static void main(String args[]) {
        if (args.length < 2) {
            System.err.println("Illegal arguments.\n amount payee [asset]");
            System.exit(1);
        }
        double amount = Double.parseDouble(args[0]);
        AssetGlobals.registerReaders();
        TransferGlobals.registerReaders();
        String payee = args[1];

        String assetname = (args.length > 3) ? args[2] : "http://bux.neuclear.org/bux.html";

        try {
            Asset asset = (Asset) Resolver.resolveIdentity(assetname);
            DefaultSigner signer = new DefaultSigner(new SwingAgent());

            Builder builder = new TransferOrderBuilder(asset.getName(), payee, new Amount(amount), "transfer via commandline");
            SignedNamedObject obj = builder.convert(signer);
            TransferReceipt receipt = (TransferReceipt) asset.service(obj);
            System.out.println("Transfer Made, ID: " + receipt.getDigest());
            System.out.println("Transfer Time: " + receipt.getValueTime());
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
}
