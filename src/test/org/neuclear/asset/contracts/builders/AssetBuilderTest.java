package org.neuclear.asset.contracts.builders;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.fees.FeeStructureBuilder;
import org.neuclear.asset.fees.MinimumFeeStructure;
import org.neuclear.commons.NeuClearException;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.builders.Builder;
import org.neuclear.tests.AbstractObjectCreationTest;

import java.security.GeneralSecurityException;

/*
$Id: AssetBuilderTest.java,v 1.6 2004/09/06 22:24:24 pelle Exp $
$Log: AssetBuilderTest.java,v $
Revision 1.6  2004/09/06 22:24:24  pelle
Added a package for calculating fees. This has been integrated into the Asset contract.

Revision 1.5  2004/05/01 00:23:12  pelle
Added Ledger field to Transaction as well as to getBalance() and friends.

Revision 1.4  2004/04/23 23:33:14  pelle
Major update. Added an original url and nickname to Identity and friends.

Revision 1.3  2004/04/18 01:06:06  pelle
Asset now parses the xhtml file for its details.

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
        AssetGlobals.registerReaders();
    }

    protected void verifyObject(SignedNamedObject obj) throws Exception {
        Asset asset = (Asset) obj;
        assertNotNull(asset.getServiceKey());
        assertEqualPublicKeys(getSigner().getPublicKey("bux"), asset.getServiceKey());
        assertNotNull(asset.getServiceUrl());
        assertEquals(URL, asset.getServiceUrl());
        assertNotNull(asset.getIssuer());
        assertEquals(getAlice().getName(), asset.getIssuer().getName());
        assertEquals(DECIMAL, asset.getDecimal());
        assertEquals(MINIMUM, asset.getMinimumTransaction(), 0);
        assertEquals(MinimumFeeStructure.class, asset.getFeeStructure().getClass());
    }

    protected Class getRequiredClass() {
        return Asset.class;
    }

    protected Builder createBuilder() throws Exception {
        AssetBuilder assetBuilder = new AssetBuilder("bux", "http://bux.neuclear.org/bux.html", "http://bux.neuclear.org/Asset",
                getSigner().getPublicKey("bux"),
                getAlice().getPublicKey(),
                DECIMAL, MINIMUM, "bux");
        assetBuilder.addFeeStructure(new FeeStructureBuilder("bux", 0.1, 0.01));
        return assetBuilder;
    }

    private static final String URL = "http://bux.neuclear.org/Asset";
    private static final int DECIMAL = 2;
    private static final int MINIMUM = 0;


}
