package org.neuclear.exchange.orders.builders;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.exchange.contracts.builders.ExchangeAgentBuilder;
import org.neuclear.tests.AbstractObjectCreationTest;

import java.security.GeneralSecurityException;

/*
$Id: AbstractExchangeOrderTest.java,v 1.1 2004/04/05 16:31:44 pelle Exp $
$Log: AbstractExchangeOrderTest.java,v $
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
        bux = createTestAsset();
        agent = createTestExchangeAgent();
        shoes = createShoeAsset();
    }

    public Asset createTestAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("http://bux.neuclear.org",
                getSigner().getPublicKey("neu://test/bux"),
                getAlice().getPublicKey(),
                2, 0);
        return (Asset) builder.convert(NAME, getSigner());

    }

    public Asset createShoeAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("http://shoes.neuclear.org",
                getSigner().getPublicKey("neu://test"),
                getAlice().getPublicKey(),
                2, 0);
        return (Asset) builder.convert(NAME, getSigner());

    }

    public ExchangeAgent createTestExchangeAgent() throws NeuClearException {
        ExchangeAgentBuilder builder = new ExchangeAgentBuilder("http://tradex.neuclear.org",
                getSigner().getPublicKey("neu://bob@test"));
        return (ExchangeAgent) builder.convert(NAME, getSigner());

    }

    protected Asset bux;
    protected Asset shoes;
    protected ExchangeAgent agent;
}
