package org.neuclear.asset.receiver.servlet;

import com.meterware.httpunit.WebForm;
import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.commons.crypto.Base32;
import org.neuclear.commons.crypto.Base64;
import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.commons.crypto.signers.JCESigner;
import org.neuclear.commons.crypto.signers.NonExistingSignerException;
import org.neuclear.commons.crypto.signers.TestCaseSigner;
import org.neuclear.id.SignatureRequest;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.auth.AuthenticationServlet;
import org.neuclear.id.verifier.VerifyingReader;
import org.neuclear.xml.XMLException;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

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

$Id: AssetControllerServletTest.java,v 1.5 2004/04/14 23:51:13 pelle Exp $
$Log: AssetControllerServletTest.java,v $
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
        return Base32.encode(CryptoTools.digest(signer.getPublicKey(alias).getEncoded()));
    }

    public void beginAuthReq(WebRequest theRequest) throws GeneralSecurityException, NeuClearException, XMLException {
        theRequest.setContentType("application/x-www-form-urlencoded");
        theRequest.addParameter("identity", "neu://bob@test", "POST");
        theRequest.setURL("http://users.neuclear.org", "/test", "/Receiver",
                null, null);
    }

    public void testAuthReq() throws ServletException, IOException {
        assertEquals(request.getContentType(), "application/x-www-form-urlencoded");
        assertEquals(request.getMethod(), "POST");
        config.setInitParameter("serviceid", "neu://test");
        config.setInitParameter("title", "cactustest");
        AuthenticationServlet servlet = new AuthenticationServlet();
        servlet.init(config);
        servlet.service(request, response);

    }

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

    JCESigner signer;
}
