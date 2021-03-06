package org.neuclear.exchange.controllers.receivers;

import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.contracts.builders.AssetBuilder;
import org.neuclear.asset.fees.FeeStructureBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.exchange.contracts.ExchangeAgent;
import org.neuclear.exchange.contracts.builders.ExchangeAgentBuilder;
import org.neuclear.exchange.controllers.DelegatingExchangeAssetController;
import org.neuclear.id.Signatory;
import org.neuclear.id.receiver.Receiver;
import org.neuclear.ledger.LedgerController;
import org.neuclear.tests.AbstractSigningTest;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

/*
$Id: AbstractExchangeReceiverTest.java,v 1.1 2004/09/10 19:48:02 pelle Exp $
$Log: AbstractExchangeReceiverTest.java,v $
Revision 1.1  2004/09/10 19:48:02  pelle
Refactored all the Exchange related receivers into a new package under org.neuclear.exchange.
Refactored the way the Receivers handle embedded objects. Now they pass them on to the parent receiver for processing before they do their own thing.

Revision 1.3  2004/09/08 23:17:39  pelle
Fees now work for everything but Exchange Completion.

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

    protected void generatePrimaryTestAssetWithFees() throws NeuClearException {
        PublicKey pub = signer.generateKey();
        AssetBuilder assetBuilder = new AssetBuilder("test", "http://localhost:8080/rules.html", "http://localhost:8080/Asset", pub, signer.getPublicKey("carol"), 2, 0, "t");
        assetBuilder.addFeeStructure(new FeeStructureBuilder(0.01));
        assetBuilder.addFeeAccount(signer.getPublicKey("eve"));
        asset = (Asset) assetBuilder.convert("bux", signer);
        assetalias = new Signatory(pub).getName();
    }

    public Receiver createReceiver() {
        return new DelegatingExchangeAssetController(signer, ledger);
    }
}
