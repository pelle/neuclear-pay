package org.neuclear.asset.servlet;

import org.neuclear.asset.AssetController;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.controllers.currency.CurrencyController;
import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.commons.crypto.signers.ServletSignerFactory;
import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.id.verifier.VerifyingReader;
import org.neuclear.ledger.Ledger;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.servlets.ServletLedgerFactory;

import javax.servlet.ServletConfig;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to create Signers from signers configuration parameters. It keeps a cache of Signers with similar parameters. Thus
 * if you have several Servlets with the same keystore parameters they will use the same instance of Signer<p>
 * The Configuration parameters are as follows:
 * <table border="2"><tr><th>parameter name</th><th>parameter value</th></tr>
 * <tr><td>keystore</td><td>The location of the JCE KeyStore. Defaults to the file .keystore in the users home directory
 * If you specify <tt>test</tt> the built in Test keystore will be used.</td></tr>
 * <tr><td>serviceid</td><td>The main service ID of the service. Ie. neu://superbux.com/ecurrency. This is only required (and used)
 * if you set <tt>keeppassphrase</tt> (see below)</td></tr>
 * <tr><td>passphraseagent</td><td>The type of passphraseagent to use. Valid options are <tt>signers</tt>,
 * <tt>gui</tt>(default) and <tt>console</tt></td></tr>
 * <tr><td>keeppassphrase</td><td>This asks for the service passphrase once at startup and remembers it through the lifetime of the signers</td></tr>
 * </table>
 * <p/>
 * To use the factory. Do as follows within your servlets init() method:
 * <code>Signer signer=ServletSignerFactory.getInstance().createSigner(config);</code>
 *
 * @see org.neuclear.commons.crypto.passphraseagents.PassPhraseAgent
 * @see org.neuclear.commons.crypto.signers.Signer
 */
public final class ServletAssetControllerFactory {

    private ServletAssetControllerFactory() {
        map = Collections.synchronizedMap(new HashMap());
    }

    public synchronized AssetController createAssetController(ServletConfig config) throws LowlevelLedgerException {
        final String type = ServletTools.getInitParam("assettype", config);
        final String serviceid = ServletTools.getInitParam("serviceid", config);
        final String hash = getConfigHash(type, serviceid);
        if (map.containsKey(hash))
            return (AssetController) map.get(hash);

        System.out.println("using asset controller: " + type);
        final AssetController assetController;
        try {
            String asseturl = ServletTools.getInitParam("asset", config);
            InputStream is;
            if (asseturl.startsWith("/"))
                is = config.getServletContext().getResourceAsStream(asseturl);
            else {
                is = new URL(asseturl).openStream();
            }
            Asset asset = (Asset) VerifyingReader.getInstance().read(is);
            Ledger ledger = ServletLedgerFactory.getInstance().createLedger(config);

            assetController = new CurrencyController(ledger, asset, ServletSignerFactory.getInstance().createSigner(config), serviceid);
        } catch (Exception e) {
            config.getServletContext().log("ServletLedgerFactory: " + e.getLocalizedMessage());
            throw new LowlevelLedgerException(e);
        }
        map.put(hash, assetController);
        return assetController;
    }

/*
    private assetController c(String type, String serviceid) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        if (type.equals("hibernate"))
            return createLedger(Class.forName("org.neuclear.ledger.hibernate.HibernateLedger"),serviceid);
        if (type.equals("prevalent"))
            return createLedger(Class.forName("org.neuclear.ledger.prevalent.PrevalentLedger"),serviceid);
        if (type.equals("simple"))
            return new SimpleLedger(serviceid);
        return createLedger(Class.forName(type),serviceid);
    }

    private Ledger createLedger(Class aClass, String serviceid) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return (Ledger) aClass.getConstructor(new Class[]{String.class}).newInstance(new String[]{serviceid});
    }

*/

    private static final String getConfigHash(final String type, final String serviceid) {
        return new String(CryptoTools.digest((type + serviceid).getBytes()));
    }

    public synchronized static ServletAssetControllerFactory getInstance() {
        if (instance == null)
            instance = new ServletAssetControllerFactory();
        return instance;
    }

    private static ServletAssetControllerFactory instance;
    final private Map map;
}
