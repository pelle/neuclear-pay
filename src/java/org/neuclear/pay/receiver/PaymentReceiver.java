package org.neuclear.pay.receiver;

import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.verifier.VerifyingReader;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnBalancedTransactionException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.pay.*;
import org.neuclear.pay.contracts.TransferContract;
import org.neuclear.pay.contracts.TransferGlobals;
import org.neuclear.pay.contracts.builders.TransferReceiptBuilder;
import org.neuclear.receiver.Receiver;
import org.neuclear.receiver.UnsupportedTransaction;
import org.neudist.xml.ElementProxy;
import org.neudist.xml.xmlsec.XMLSecurityException;

import java.security.PrivateKey;

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
public class PaymentReceiver implements Receiver {

    public PaymentReceiver(PaymentProcessor proc, String asset) {
        this.proc = proc;
        this.asset = asset;
        this.signer = null;
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
                PaymentReceipt receipt = from.pay(to, transfer.getAmount(), transfer.getTimeStamp(), "transfer");
                TransferReceiptBuilder sigReceipt = new TransferReceiptBuilder(receipt);
                sigReceipt.sign(signer);
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
            } catch (NegativePaymentException e) {
                e.printStackTrace();
            } catch (XMLSecurityException e) {
                e.printStackTrace();
            }

        } else
            throw new UnsupportedTransaction(obj);
        return null;
    }

    private final PaymentProcessor proc;
    private final String asset;
    private PrivateKey signer;

    {
        // Registers the readers for transfers
        VerifyingReader.getInstance().registerReader(TransferGlobals.XFER_TAGNAME, new TransferContract.Reader());
    }
}
