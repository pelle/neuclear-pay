package org.neuclear.asset.orders;

import org.neuclear.id.Service;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Nov 10, 2003
 * Time: 11:06:37 AM
 * $Id: AssetTransactionContract.java,v 1.3 2004/04/05 16:31:41 pelle Exp $
 * $Log: AssetTransactionContract.java,v $
 * Revision 1.3  2004/04/05 16:31:41  pelle
 * Created new ServiceBuilder class for creating services. A service is an identity that has a seperate service URL and Service Public Key.
 *
 * Revision 1.2  2004/01/10 00:00:45  pelle
 * Implemented new Schema for Transfer*
 * Working on it for Exchange*, so far all Receipts are implemented.
 * Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
 * Changed SignedNamedObject.getDigest() from byte array to String.
 * The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.
 * <p/>
 * Revision 1.1  2004/01/05 23:47:09  pelle
 * Create new Document classification "order", which is really just inherint in the new
 * package layout.
 * Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.
 * <p/>
 * Revision 1.11  2004/01/03 20:36:25  pelle
 * Renamed HeldTransfer to Exchange
 * Dropped valuetime from the request objects.
 * Doesnt yet compile. New commit to follow soon.
 * <p/>
 * Revision 1.10  2003/12/19 18:02:35  pelle
 * Revamped a lot of exception handling throughout the framework, it has been simplified in most places:
 * - For most cases the main exception to worry about now is InvalidNamedObjectException.
 * - Most lowerlevel exception that cant be handled meaningful are now wrapped in the LowLevelException, a
 * runtime exception.
 * - Source and Store patterns each now have their own exceptions that generalizes the various physical
 * exceptions that can happen in that area.
 * <p/>
 * Revision 1.9  2003/12/10 23:52:39  pelle
 * Did some cleaning up in the builders
 * Fixed some stuff in IdentityCreator
 * New maven goal to create executable jarapp
 * We are close to 0.8 final of ID, 0.11 final of XMLSIG and 0.5 of commons.
 * Will release shortly.
 * <p/>
 * Revision 1.8  2003/11/28 00:11:50  pelle
 * Getting the NeuClear web transactions working.
 * <p/>
 * Revision 1.7  2003/11/22 00:22:28  pelle
 * All unit tests in commons, id and xmlsec now work.
 * AssetController now successfully processes payments in the unit test.
 * Payment Web App has working form that creates a TransferOrder presents it to the signer
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
 * This means that it is now possible to further receive on or process a SignedNamedObject, leaving
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
public abstract class AssetTransactionContract extends SignedNamedObject {
    private final Service asset;

    protected AssetTransactionContract(final SignedNamedCore core, final Service asset) {
        super(core);
        this.asset = asset;
    }


    public final Service getAsset() {
        return asset;
    }

}
