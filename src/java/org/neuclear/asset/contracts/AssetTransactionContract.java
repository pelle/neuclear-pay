package org.neuclear.asset.contracts;

import org.dom4j.Element;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.id.Identity;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.receiver.UnsupportedTransaction;

import java.util.Date;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 11:06:37 AM
 * $Id: AssetTransactionContract.java,v 1.8 2003/11/28 00:11:50 pelle Exp $
 * $Log: AssetTransactionContract.java,v $
 * Revision 1.8  2003/11/28 00:11:50  pelle
 * Getting the NeuClear web transactions working.
 *
 * Revision 1.7  2003/11/22 00:22:28  pelle
 * All unit tests in commons, id and xmlsec now work.
 * AssetController now successfully processes payments in the unit test.
 * Payment Web App has working form that creates a TransferRequest presents it to the signer
 * and forwards it to AssetControlServlet. (Which throws an XML Parser Exception) I think the XMLReaderServlet is bust.
 * <p/>
 * Revision 1.6  2003/11/21 04:43:04  pelle
 * EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
 * Otherwise You will Finaliate.
 * Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
 * This should hopefully make everything more stable (and secure).
 * <p/>
 * Revision 1.5  2003/11/20 16:01:59  pelle
 * Updated all the Contracts to use the new security model.
 * <p/>
 * Revision 1.4  2003/11/19 23:32:20  pelle
 * Signers now can generatekeys via the generateKey() method.
 * Refactored the relationship between SignedNamedObject and NamedObjectBuilder a bit.
 * SignedNamedObject now contains the full xml which is returned with getEncoded()
 * This means that it is now possible to further send on or process a SignedNamedObject, leaving
 * NamedObjectBuilder for its original purposes of purely generating new Contracts.
 * NamedObjectBuilder.sign() now returns a SignedNamedObject which is the prefered way of processing it.
 * Updated all major interfaces that used the old model to use the new model.
 * <p/>
 * Revision 1.3  2003/11/12 23:47:04  pelle
 * Much work done in creating good test environment.
 * PaymentReceiverTest works, but needs a abit more work in its environment to succeed testing.
 * <p/>
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

    AssetTransactionContract(final SignedNamedCore core, final Asset asset) throws NeuClearException {
        super(core);
        this.asset = asset;
    }


    public final Asset getAsset() {
        return asset;
    }

    public static final class Reader implements NamedObjectReader {
        /**
         * Read object from Element and fill in its details
         * 
         * @param elem 
         * @return 
         */
        public final SignedNamedObject read(final SignedNamedCore core, final Element elem) throws NeuClearException {
            if (!elem.getNamespaceURI().equals(TransferGlobals.XFER_NSURI))
                throw new UnsupportedTransaction(core);

            final Asset asset = (Asset) NSResolver.resolveIdentity(elem.attributeValue("assetName"));

            final String holdid = elem.attributeValue("holdid");
            if (elem.getName().equals(TransferGlobals.CANCEL_TAGNAME))
                return new CancelHeldTransferRequest(core, asset, holdid);
            if (elem.getName().equals(TransferGlobals.CANCEL_RCPT_TAGNAME))
                return new CancelHeldTransferReceipt(core, asset, holdid);

            final double amount = Double.parseDouble(elem.attributeValue("amount"));
            final Date valuetime = TimeTools.parseTimeStamp(elem.attributeValue("valuetime"));
            final Identity to = NSResolver.resolveIdentity(elem.attributeValue("recipient"));
            final Element commentElement = elem.element(TransferGlobals.createQName("comment"));

            final String comment = (commentElement != null) ? commentElement.getText() : "";
            if (elem.getName().equals(TransferGlobals.XFER_TAGNAME))
                return new TransferRequest(core, asset, to, amount, valuetime, comment);

            Date helduntil = null;
            if (!Utility.isEmpty(elem.attributeValue("valuetime")))
                helduntil = TimeTools.parseTimeStamp(elem.attributeValue("valuetime"));
            if (elem.getName().equals(TransferGlobals.HELD_XFER_TAGNAME))
                return new HeldTransferRequest(core, asset, to, amount, valuetime, comment, helduntil);

            final Identity from = NSResolver.resolveIdentity(elem.attributeValue("sender"));
            final String reqid = elem.attributeValue("reqid");
            if (elem.getName().equals(TransferGlobals.XFER_RCPT_TAGNAME))
                return new TransferReceipt(core, asset, from, to, reqid, amount, valuetime, comment);

            if (elem.getName().equals(TransferGlobals.HELD_XFER_RCPT_TAGNAME))
                return new HeldTransferReceipt(core, asset, from, to, reqid, amount, valuetime, comment, helduntil);

            if (elem.getName().equals(TransferGlobals.COMPLETE_TAGNAME))
                return new CompleteHeldTransferRequest(core, asset, from, to, amount, valuetime, comment, holdid);

            throw new UnsupportedTransaction(core);
        }

    }


}
