package org.neuclear.asset.contracts;

import org.dom4j.Element;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.id.Identity;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.receiver.UnsupportedTransaction;

import java.sql.Timestamp;
import java.util.Date;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 11:06:37 AM
 * $Id: AssetTransactionContract.java,v 1.3 2003/11/12 23:47:04 pelle Exp $
 * $Log: AssetTransactionContract.java,v $
 * Revision 1.3  2003/11/12 23:47:04  pelle
 * Much work done in creating good test environment.
 * PaymentReceiverTest works, but needs a abit more work in its environment to succeed testing.
 *
 * Revision 1.2  2003/11/11 21:17:19  pelle
 * Further vital reshuffling.
 * org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
 * org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
 * Did a bit of work on the Canonicalizer and changed a few other minor bits.
 * <p/>
 * Revision 1.1  2003/11/10 17:42:07  pelle
 * The AssetController interface has been more or less finalized.
 * CurrencyController fully implemented
 * AssetControlClient implementes a remote client for communicating with AssetControllers
 */
public class AssetTransactionContract extends SignedNamedObject {
    private final Asset asset;

    public AssetTransactionContract(String name, Identity signer, Timestamp timestamp, String digest, Asset asset) throws NeuClearException {
        super(name, signer, timestamp, digest);
        this.asset = asset;
    }

    public final Asset getAsset() {
        return asset;
    }

    public static class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         * 
         * @param elem 
         * @return 
         */
        public SignedNamedObject read(Element elem, String name, Identity signatory, String digest, Timestamp timestamp) throws NeuClearException {
            if (elem.getNamespaceURI().equals(TransferGlobals.XFER_NSURI))
                throw new UnsupportedTransaction(null);

            Asset asset = (Asset) NSResolver.resolveIdentity(elem.attributeValue("assetName"));
            String holdid = elem.attributeValue("holdid");
            if (elem.getName().equals(TransferGlobals.CANCEL_TAGNAME))
                return new CancelHeldTransferRequest(name, signatory, timestamp, digest, asset, holdid);
            if (elem.getName().equals(TransferGlobals.CANCEL_RCPT_TAGNAME))
                return new CancelHeldTransferReceipt(name, signatory, timestamp, digest, asset, holdid);

            double amount = Double.parseDouble(elem.attributeValue("amount"));
            Date valuetime = TimeTools.parseTimeStamp(elem.attributeValue("valuetime"));
            Identity to = NSResolver.resolveIdentity(elem.attributeValue("recipient"));
            String comment = elem.attributeValue("comment");
            if (elem.getName().equals(TransferGlobals.XFER_TAGNAME))
                return new TransferRequest(name, signatory, timestamp, digest, asset, to, amount, valuetime, comment);

            Date helduntil = null;
            if (!Utility.isEmpty(elem.attributeValue("valuetime")))
                helduntil = TimeTools.parseTimeStamp(elem.attributeValue("valuetime"));
            if (elem.getName().equals(TransferGlobals.HELD_XFER_TAGNAME))
                return new HeldTransferRequest(name, signatory, timestamp, digest, asset, to, amount, valuetime, comment, helduntil);

            Identity from = NSResolver.resolveIdentity(elem.attributeValue("sender"));
            String reqid = elem.attributeValue("reqid");
            if (elem.getName().equals(TransferGlobals.XFER_RCPT_TAGNAME))
                return new TransferReceipt(name, signatory, timestamp, digest, asset, from, to, reqid, amount, valuetime, comment);

            if (elem.getName().equals(TransferGlobals.HELD_XFER_RCPT_TAGNAME))
                return new HeldTransferReceipt(name, signatory, timestamp, digest, asset, from, to, reqid, amount, valuetime, comment, helduntil);

            if (elem.getName().equals(TransferGlobals.COMPLETE_TAGNAME))
                return new CompleteHeldTransferRequest(name, signatory, timestamp, digest, asset, from, to, amount, valuetime, comment, holdid);

            throw new UnsupportedTransaction(null);
        }

    }
}
