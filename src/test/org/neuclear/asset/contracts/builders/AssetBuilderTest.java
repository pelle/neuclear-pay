package org.neuclear.asset.contracts.builders;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.tests.AbstractObjectCreationTest;

import java.security.GeneralSecurityException;

/*
$Id: AssetBuilderTest.java,v 1.2 2004/04/17 19:28:00 pelle Exp $
$Log: AssetBuilderTest.java,v $
Revision 1.2  2004/04/17 19:28:00  pelle
Identity is now fully html based as is the ServiceBuilder.
VerifyingReader correctly identifies html files and parses them as such.
Targets and Target now parse html link tags
AssetBuilder and ExchangeAgentBuilder have been updated to support it and provide html formatted contracts.
The Asset.Reader and ExchangeAgent.Reader still need to be updated.

Revision 1.1  2004/04/02 16:58:55  pelle
Updated Asset and Asset Builder with semi fully featured functionality.
It now has Issuer, Service etc.

*/

/**
 * User: pelleb
 * Date: Apr 2, 2004
 * Time: 10:40:20 AM
 */
public class AssetBuilderTest extends AbstractObjectCreationTest {
    public AssetBuilderTest(String string) throws NeuClearException, GeneralSecurityException {
        super(string);
    }

    protected void verifyObject(SignedNamedObject obj) throws Exception {
        Asset asset = (Asset) obj;
        assertNotNull(asset.getServiceKey());
        assertEqualPublicKeys(getSigner().getPublicKey("neu://test/bux"), asset.getServiceKey());
        assertNotNull(asset.getServiceUrl());
        assertEquals(URL, asset.getServiceUrl());
        assertNotNull(asset.getIssuerKey());
        assertEqualPublicKeys(getAlice().getPublicKey(), asset.getIssuerKey());
        assertEquals(DECIMAL, asset.getDecimal());
        assertEquals(MINIMUM, asset.getMinimumTransaction(), 0);
    }

    protected Class getRequiredClass() {
        return Asset.class;
    }

    protected Builder createBuilder() throws Exception {
        return new AssetBuilder(URL, "bux",
                getSigner().getPublicKey("bux"),
                getAlice().getPublicKey(),
                DECIMAL, MINIMUM);
    }

    private static final String URL = "http://bux.neuclear.org";
    private static final int DECIMAL = 2;
    private static final int MINIMUM = 0;


}
