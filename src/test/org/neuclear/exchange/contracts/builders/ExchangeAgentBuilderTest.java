package org.neuclear.exchange.contracts.builders;

import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.exchange.contracts.ExchangeAgentGlobals;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.tests.AbstractObjectCreationTest;

import java.security.GeneralSecurityException;

/*
$Id: ExchangeAgentBuilderTest.java,v 1.4 2004/04/23 23:33:15 pelle Exp $
$Log: ExchangeAgentBuilderTest.java,v $
Revision 1.4  2004/04/23 23:33:15  pelle
Major update. Added an original url and nickname to Identity and friends.

Revision 1.3  2004/04/20 17:47:24  pelle
Fixes to ExchangeAgent to make it work with html contracts
CurrencyTests fail.

Revision 1.2  2004/04/17 19:28:01  pelle
Identity is now fully html based as is the ServiceBuilder.
VerifyingReader correctly identifies html files and parses them as such.
Targets and Target now parse html link tags
AssetBuilder and ExchangeAgentBuilder have been updated to support it and provide html formatted contracts.
The Asset.Reader and ExchangeAgent.Reader still need to be updated.

Revision 1.1  2004/04/05 16:31:44  pelle
Created new ServiceBuilder class for creating services. A service is an identity that has a seperate service URL and Service Public Key.

Revision 1.1  2004/04/02 16:58:55  pelle
Updated Asset and Asset Builder with semi fully featured functionality.
It now has Issuer, Service etc.

*/

/**
 * User: pelleb
 * Date: Apr 2, 2004
 * Time: 10:40:20 AM
 */
public class ExchangeAgentBuilderTest extends AbstractObjectCreationTest {
    public ExchangeAgentBuilderTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
        ExchangeAgentGlobals.registerReaders();
    }

    protected void verifyObject(SignedNamedObject obj) throws Exception {
        ExchangeAgent asset = (ExchangeAgent) obj;
        assertNotNull(asset.getServiceKey());
        assertEqualPublicKeys(getSigner().getPublicKey("neu://test/bux"), asset.getServiceKey());
        assertNotNull(asset.getServiceUrl());
        assertEquals(URL, asset.getServiceUrl());
    }

    protected Class getRequiredClass() {
        return ExchangeAgent.class;
    }

    protected Builder createBuilder() throws Exception {
        return new ExchangeAgentBuilder("Exchange", URL, URL,
                getSigner().getPublicKey("neu://test/bux"));
    }

    private static final String URL = "http://tradex.neuclear.org";
}
