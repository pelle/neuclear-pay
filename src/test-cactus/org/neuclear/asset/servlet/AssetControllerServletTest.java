package org.neuclear.asset.servlet;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.TransferOrder;
import org.neuclear.asset.orders.builders.TransferOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.Base32;
import org.neuclear.commons.crypto.Base64;
import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.commons.crypto.signers.JCESigner;
import org.neuclear.commons.crypto.signers.NonExistingSignerException;
import org.neuclear.commons.crypto.signers.TestCaseSigner;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NameResolutionException;
import org.neuclear.id.Signatory;
import org.neuclear.id.verifier.VerifyingReader;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.xml.XMLException;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.GeneralSecurityException;
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

$Id: AssetControllerServletTest.java,v 1.3 2004/05/01 00:23:13 pelle Exp $
$Log: AssetControllerServletTest.java,v $
Revision 1.3  2004/05/01 00:23:13  pelle
Added Ledger field to Transaction as well as to getBalance() and friends.

Revision 1.2  2004/04/23 19:09:35  pelle
Lots of cleanups and improvements to the userinterface and look of the bux application.

Revision 1.1  2004/04/20 23:31:03  pelle
All unit tests (junit and cactus) work. The AssetControllerServlet is operational.

Revision 1.5  2004/04/14 23:51:13  pelle
Fixed Exchange tests and Cactus tests working on web app.

Revision 1.4  2004/04/01 23:18:34  pelle
Split Identity into Signatory and Identity class.
Identity remains a signed named object and will in the future just be used for self declared information.
Signatory now contains the PublicKey etc and is NOT a signed object.

Revision 1.3  2004/03/02 18:58:35  pelle
Further cleanups in neuclear-id. Moved everything under id.

Revision 1.2  2004/01/13 23:37:30  pelle
Refactoring parts of the core of XMLSignature. There shouldnt be any real API changes.

Revision 1.1  2003/12/20 00:17:41  pelle
overwrote the standard Object.toString(), hashCode() and equals() methods for SignedNamedObject/Core
fixed cactus tests
Added TransferRequestServlet
Added cactus tests to pay

*/

/**
 * User: pelleb
 * Date: Dec 19, 2003
 * Time: 6:52:08 PM
 */
public class AssetControllerServletTest extends ServletTestCase {
    public AssetControllerServletTest(String string) throws NeuClearException {
        super(string);
        signer = new TestCaseSigner();
    }

    protected String getPublicKeyName(String alias) throws NonExistingSignerException {
        final PublicKey key = signer.getPublicKey(alias);
        return getPublicKeyName(key);
    }

    private String getPublicKeyName(final PublicKey key) {
        return Base32.encode(CryptoTools.digest(key.getEncoded()));
    }

    public void beginAuthReq(WebRequest theRequest) throws GeneralSecurityException, NeuClearException, XMLException, InvalidTransferException {
        theRequest.setContentType("application/x-www-form-urlencoded");
        theRequest.addParameter("identity", "neu://bob@test", "POST");
        theRequest.setContentType("application/x-www-form-urlencoded");
        Asset asset = loadAsset();
        TransferOrder order = (TransferOrder) new TransferOrderBuilder(asset, new Signatory(signer.getPublicKey("alice")), new Amount(100), "test").convert("bob", signer);
        String b64 = Base64.encode(order.getEncoded().getBytes());
        theRequest.addParameter("neuclear-request", b64, "POST");
        theRequest.setURL("http://bux.neuclear.org", "/test", "/Receiver",
                null, null);


    }

    public void testAuthReq() throws ServletException, IOException, NameResolutionException, InvalidNamedObjectException, NonExistingSignerException, InvalidTransactionException, LowlevelLedgerException, UnknownBookException {
        assertEquals(request.getContentType(), "application/x-www-form-urlencoded");
        assertEquals(request.getMethod(), "POST");
        config.setInitParameter("serviceid", "bux");
        config.setInitParameter("title", "cactustest");
        config.setInitParameter("asset", "/bux.html");
        AssetControllerServlet servlet = new AssetControllerServlet();
        final Asset asset = loadAsset();
//        servlet.setAsset(asset);
        servlet.init(config);
        assertEquals(0, servlet.getLedger().getBalance(null, getPublicKeyName("bob")), 0);
        servlet.getLedger().transfer(asset.getIssuer().getName(), getPublicKeyName("bob"), 110, "fund");
        assertEquals(110, servlet.getLedger().getBalance(null, getPublicKeyName("bob")), 0);
        servlet.service(request, response);
        assertEquals(10, servlet.getLedger().getBalance(null, getPublicKeyName("bob")), 0);
        assertEquals(100, servlet.getLedger().getBalance(null, getPublicKeyName("alice")), 0);
    }
/*

    public void endAuthReq(com.meterware.httpunit.WebResponse theResponse) throws SAXException, NeuClearException, XMLException {
        assertEquals("cactustest", theResponse.getTitle());
        WebForm forms[] = theResponse.getForms();
        assertNotNull(forms);
        assertEquals(1, forms.length);
        assertTrue(forms[0].hasParameterNamed("neuclear-request"));
        String encoded = forms[0].getParameterValue("neuclear-request");
        assertTrue(!Utility.isEmpty(encoded));
        final SignedNamedObject obj = VerifyingReader.getInstance().read(new ByteArrayInputStream(Base64.decode(encoded)));
        assertNotNull(obj);
        assertTrue(obj instanceof SignatureRequest);
        SignatureRequest sigreq = (SignatureRequest) obj;
        assertEquals(getPublicKeyName("neu://test"), sigreq.getSignatory().getName());
        assertEquals(sigreq.getUnsigned().getElement().getName(), "AuthenticationTicket");
        assertEquals("http://localhost:11870/Signer", forms[0].getAction());
    }
*/
    private static Asset loadAsset() throws NameResolutionException, InvalidNamedObjectException {
        AssetGlobals.registerReaders();
        return (Asset) VerifyingReader.getInstance().read(AssetControllerServletTest.class.getClassLoader().getResourceAsStream("bux.html"));
    }

    JCESigner signer;
}
