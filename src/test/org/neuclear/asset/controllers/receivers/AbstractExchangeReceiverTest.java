package org.neuclear.asset.controllers.receivers;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.exchange.contracts.builders.ExchangeAgentBuilder;
import org.neuclear.id.Signatory;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.ledger.LedgerController;
import org.neuclear.tests.AbstractSigningTest;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

/*
$Id: AbstractExchangeReceiverTest.java,v 1.2 2004/08/18 09:42:56 pelle Exp $
$Log: AbstractExchangeReceiverTest.java,v $
Revision 1.2  2004/08/18 09:42:56  pelle
Many fixes to the various Signing and SigningRequest Servlets etc.

Revision 1.1  2004/07/22 21:48:46  pelle
Further receivers and unit tests for for Exchanges etc.
I've also changed the internal asset to ledger id from being the pk of the contract signer, to being the pk of the service key.

*/

/**
 * User: pelleb
 * Date: Jul 22, 2004
 * Time: 10:39:40 AM
 */
public abstract class AbstractExchangeReceiverTest extends AbstractSigningTest {
    protected Receiver receiver;
    protected Asset asset;
    protected Asset shoes;
    protected LedgerController ledger;
    protected ExchangeAgent agent;
    public static String PROMOTER = "ivan";
    protected String assetalias;


    public AbstractExchangeReceiverTest(final String string) throws NeuClearException, GeneralSecurityException {
        super(string);
    }

    public ExchangeAgent createTestExchangeAgent() throws NeuClearException {
        ExchangeAgentBuilder builder = new ExchangeAgentBuilder("tradex", "http://tradex.neuclear.org", "http://tradex.neuclear.org",
                getSigner().getPublicKey("exchange"));
        return (ExchangeAgent) builder.convert("carol", getSigner());

    }

    public Asset createShoeAsset() throws NeuClearException {
        AssetBuilder builder = new AssetBuilder("shoes", "http://shoes.neuclear.org", "http://shoes.neuclear.org",
                getSigner().getPublicKey("shoes"),
                getSigner().getPublicKey("shoesissuer"),
                2, 0, "pairs of shoes");
        return (Asset) builder.convert(PROMOTER, getSigner());

    }

    protected void generatePrimaryTestAsset() throws NeuClearException {
        PublicKey pub = signer.generateKey();
        asset = (Asset) new AssetBuilder("test", "http://localhost:8080/rules.html", "http://localhost:8080/Asset", pub, signer.getPublicKey("carol"), 2, 0, "t").convert("bux", signer);
        assetalias = new Signatory(pub).getName();
    }

    protected abstract Receiver createReceiver();
}
