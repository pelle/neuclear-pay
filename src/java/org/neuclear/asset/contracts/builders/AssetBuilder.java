package org.neuclear.asset.contracts.builders;

import org.dom4j.Element;
import org.neuclear.id.builders.NamedObjectBuilder;
import org.neuclear.id.builders.IdentityBuilder;
import org.neuclear.id.Identity;
import org.neuclear.id.NSTools;
import org.neuclear.asset.contracts.TransferGlobals;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.NegativeTransferException;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.commons.Utility;

import java.util.Date;
import java.security.PublicKey;

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

$Id: AssetBuilder.java,v 1.2 2003/11/11 21:17:19 pelle Exp $
$Log: AssetBuilder.java,v $
Revision 1.2  2003/11/11 21:17:19  pelle
Further vital reshuffling.
org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
Did a bit of work on the Canonicalizer and changed a few other minor bits.

Revision 1.1  2003/11/10 21:08:41  pelle
More JavaDoc

Revision 1.3  2003/11/10 17:42:07  pelle
The AssetController interface has been more or less finalized.
CurrencyController fully implemented
AssetControlClient implementes a remote client for communicating with AssetControllers

Revision 1.2  2003/11/09 03:26:47  pelle
More house keeping and shuffling about mainly pay

Revision 1.1  2003/11/09 03:10:13  pelle
Major changes that apparently didnt get properly checked in earlier.

Revision 1.2  2003/11/06 23:47:43  pelle
Major Refactoring of CurrencyController.
Factored out AssetController to be new abstract parent class together with most of its support classes.
Created (Half way) AssetControlClient, which can perform transactions on external AssetControllers via NeuClear.
Created the first attempt at the ExchangeAgent. This will need use of the AssetControlClient.
SOAPTools was changed to return a stream. This is required by the VerifyingReader in NeuClear.

Revision 1.1  2003/10/03 23:48:29  pelle
Did various security related updates in the pay package with regards to immutability of fields etc.
AssetControllerReceiver should now be operational. Real testing needs to be done including in particular setting the
private key of the Receiver.
A new class TransferGlobals contains usefull settings for making life easier in the other contract based classes.
TransferContract the signed contract is functional and has a matching TransferRequestBuilder class for programmatically creating
TransferRequests for signing.
TransferReceiptBuilder has been created for use by Transfer processors. It is used in the AssetControllerReceiver.

*/

/**
 * User: pelleb
 * Date: Oct 3, 2003
 * Time: 3:13:27 PM
 */
public abstract class AssetBuilder extends IdentityBuilder {
    /**
     * Used to create new Assets
     *
     * @param name       The Name of Identity
     * @param allow      PublicKey allowed to sign in here
     * @param repository URL of Default Store for NameSpace. (Note. A NameSpace object is stored in the default repository of it's parent namespace)
     * @param signer     URL of default interactive signing service for namespace. If null it doesnt allow interactive signing
     * @param receiver   URL of default receiver for namespace
     * @param controller URL of AssetController This should be a http web url
     * @param decimal The amount of decimal points.
     * @param minimum Minimum transaction size
     */
    public AssetBuilder(String name, PublicKey allow, String repository, String signer, String logger, String receiver,String controller, int decimal, double minimum) {
        super(AssetGlobals.createQName(AssetGlobals.ASSET_TAGNAME), name, allow, repository, signer, logger, receiver);
        Element elem=getElement();
        AssetGlobals.createAttribute(elem,"controller",controller);
        AssetGlobals.createAttribute(elem,"decimalpoints",Integer.toString(decimal));
        AssetGlobals.createAttribute(elem,"minimumxact",Double.toString(minimum));

    }
}
