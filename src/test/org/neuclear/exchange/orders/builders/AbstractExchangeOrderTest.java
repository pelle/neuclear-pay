package org.neuclear.exchange.orders.builders;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.exchange.contracts.ExchangeAgentGlobals;
import org.neuclear.exchange.contracts.builders.ExchangeAgentBuilder;
import org.neuclear.tests.AbstractObjectCreationTest;

import java.security.GeneralSecurityException;

/*
$Id: AbstractExchangeOrderTest.java,v 1.5 2004/09/08 17:27:41 pelle Exp $
$Log: AbstractExchangeOrderTest.java,v $
Revision 1.5  2004/09/08 17:27:41  pelle
Some unit tests were not registrering their readers.

Revision 1.4  2004/04/23 23:33:15  pelle
Major update. Added an original url and nickname to Identity and friends.

Revision 1.3  2004/04/17 19:28:01  pelle
Identity is now fully html based as is the ServiceBuilder.
VerifyingReader correctly identifies html files and parses them as such.
Targets and Target now parse html link tags
AssetBuilder and ExchangeAgentBuilder have been updated to support it and provide html formatted contracts.
The Asset.Reader and ExchangeAgent.Reader still need to be updated.

Revision 1.2  2004/04/14 23:51:12  pelle
Fixed Exchange tests and Cactus tests working on web app.

Revision 1.1  2004/04/05 16:31:44  pelle
Created new ServiceBuilder class for creating services. A service is an identity that has a seperate service URL and Service Public Key.

*/

/**
 * User: pelleb
 * Date: Apr 5, 2004
 * Time: 10:30:19 AM
 */
public abstract class AbstractExchangeOrderTest extends AbstractObjectCreationTest {
    public AbstractExchangeOrderTest(final String string) throws NeuClearException, GeneralSecurityException {
        super(string);
        AssetGlobals.registerReaders();
        ExchangeAgentGlobals.registerReaders();
        bux = createTestAsset();
        agent = createTestExchangeAgent();
        shoes = createShoeAsset();
    }

    public Asset createTestAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("bux", "http://bux.neuclear.org", "bux",
                getSigner().getPublicKey("bux"),
                getSigner().getPublicKey("buxissuer"),
                2, 0, "bux");
        return (Asset) builder.convert(PROMOTER, getSigner());

    }

    public Asset createShoeAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("shoes", "http://shoes.neuclear.org", "http://shoes.neuclear.org",
                getSigner().getPublicKey("shoes"),
                getSigner().getPublicKey("shoesissuer"),
                2, 0, "pairs of shoes");
        return (Asset) builder.convert(PROMOTER, getSigner());

    }

    public ExchangeAgent createTestExchangeAgent() throws NeuClearException {
        ExchangeAgentBuilder builder = new ExchangeAgentBuilder("tradex", "http://tradex.neuclear.org", "http://tradex.neuclear.org",
                getSigner().getPublicKey("exchange"));
        return (ExchangeAgent) builder.convert("carol", getSigner());

    }

    protected Asset bux;
    protected Asset shoes;
    protected ExchangeAgent agent;
    public static String PROMOTER = "ivan";
}
