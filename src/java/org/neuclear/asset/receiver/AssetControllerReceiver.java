package org.neuclear.asset.receiver;

import org.neuclear.asset.AssetController;
import org.neuclear.asset.InsufficientFundsException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.contracts.*;
import org.neuclear.asset.contracts.builders.TransferReceiptBuilder;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.verifier.VerifyingReader;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnBalancedTransactionException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.receiver.Receiver;
import org.neuclear.receiver.UnsupportedTransaction;
import org.neudist.crypto.Signer;
import org.neudist.xml.ElementProxy;
import org.neudist.xml.xmlsec.XMLSecurityException;

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

*/

/**
 * User: pelleb
 * Date: Sep 23, 2003
 * Time: 3:03:03 PM
 */
public class AssetControllerReceiver implements Receiver {

    public AssetControllerReceiver(AssetController proc, Signer signer, String asset) {
        this.proc = proc;
        this.asset = asset;
        this.signer = signer;
    }

    /**
     * Add your main transaction processing logic within this method.
     * 
     * @param obj 
     * @throws UnsupportedTransaction 
     */
    public final ElementProxy receive(SignedNamedObject obj) throws UnsupportedTransaction {
        if (obj instanceof TransferContract) {
            TransferContract transfer = (TransferContract) obj;
            if (!transfer.getAsset().equals(asset))
                throw new UnsupportedTransaction(obj);

            try {
                Account from = proc.getAccount(transfer.getName());
                Account to = proc.getAccount(transfer.getName());
                TransferReceipt receipt = from.pay(to, transfer.getAmount(), transfer.getTimeStamp(), "asset");
                TransferReceiptBuilder sigReceipt = new TransferReceiptBuilder(receipt);
                sigReceipt.sign(asset, signer);
                return sigReceipt;
                //TODO do something with receipt
            } catch (UnknownBookException e) {
                throw new UnsupportedTransaction(obj);
            } catch (LowlevelLedgerException e) {
                e.printStackTrace();
            } catch (InsufficientFundsException e) {
                e.printStackTrace();
            } catch (InvalidTransactionException e) {
                e.printStackTrace();
            } catch (UnBalancedTransactionException e) {
                e.printStackTrace();
            } catch (NegativeTransferException e) {
                e.printStackTrace();
            } catch (XMLSecurityException e) {
                e.printStackTrace();
            } catch (AssetMismatchException e) {
                e.printStackTrace();
            }

        } else
            throw new UnsupportedTransaction(obj);
        return null;
    }

    private final AssetController proc;
    private final String asset;
    private Signer signer;

    {
        // Registers the readers for transfers
        VerifyingReader.getInstance().registerReader(TransferGlobals.XFER_TAGNAME, new TransferContract.Reader());
        VerifyingReader.getInstance().registerReader(TransferGlobals.XFER_RCPT_TAGNAME, new TransferContract.Reader());
        VerifyingReader.getInstance().registerReader(TransferGlobals.HELD_XFER_TAGNAME, new TransferContract.Reader());
        VerifyingReader.getInstance().registerReader(TransferGlobals.HELD_XFER_RCPT_TAGNAME, new TransferContract.Reader());
        VerifyingReader.getInstance().registerReader(AssetGlobals.ASSET_TAGNAME, new Asset.Reader());
    }
}
