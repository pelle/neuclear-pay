package org.neuclear.asset.receiver;

import org.neuclear.asset.AssetController;
import org.neuclear.asset.InsufficientFundsException;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.TransferDeniedException;
import org.neuclear.asset.contracts.*;
import org.neuclear.asset.contracts.builders.TransferReceiptBuilder;
import org.neuclear.asset.contracts.builders.TransferBuilder;
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
            if (!proc.canProcess(transfer.getAsset()))
                throw new UnsupportedTransaction(obj);

            try {
                TransferBuilder sigReceipt = proc.process(transfer);
                sigReceipt.sign(asset, signer);
                return sigReceipt;
                //TODO do something with receipt
            } catch (TransferDeniedException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (InvalidTransactionException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (UnknownBookException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (UnBalancedTransactionException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (XMLSecurityException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            } catch (LowlevelLedgerException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
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
