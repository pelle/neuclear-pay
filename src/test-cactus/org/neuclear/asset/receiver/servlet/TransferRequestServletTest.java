package org.neuclear.asset.receiver.servlet;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.commons.crypto.signers.TestCaseSigner;
import org.neuclear.commons.crypto.signers.JCESigner;
import org.neuclear.commons.crypto.Base64;
import org.neuclear.xml.XMLException;
import org.neuclear.auth.AuthenticationServlet;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.SignatureRequest;
import org.neuclear.id.resolver.NSResolver;
import org.neuclear.id.verifier.VerifyingReader;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.ByteArrayInputStream;

import com.meterware.httpunit.WebForm;

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

$Id: TransferRequestServletTest.java,v 1.1 2003/12/20 00:17:41 pelle Exp $
$Log: TransferRequestServletTest.java,v $
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
public class TransferRequestServletTest extends ServletTestCase{
    public TransferRequestServletTest(String string) throws NeuClearException {
        super(string);
        signer = new TestCaseSigner();
    }

    public void beginAuthReq(WebRequest theRequest) throws GeneralSecurityException, NeuClearException, XMLException {
        theRequest.setContentType("application/x-www-form-urlencoded");
        theRequest.addParameter("recipient", "neu://alice@test", "POST");
        theRequest.addParameter("sender", "neu://alice@test", "POST");
        theRequest.addParameter("comment", "test", "POST");
        theRequest.addParameter("amount", "10", "POST");
        theRequest.setURL("http://users.neuclear.org", "/test", "/Receiver",
                null, null);
    }

    public void testAuthReq() throws ServletException, IOException {
        assertEquals(request.getContentType(), "application/x-www-form-urlencoded");
        assertEquals(request.getMethod(), "POST");

        config.setInitParameter("serviceid","neu://test/bux");
        config.setInitParameter("title","cactustest");
        TransferRequestServlet servlet = new TransferRequestServlet();
        servlet.init(config);
        servlet.service(request, response);
    }

    JCESigner signer;
}
