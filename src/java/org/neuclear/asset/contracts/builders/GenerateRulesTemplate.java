package org.neuclear.asset.contracts.builders;

import org.apache.commons.cli.Option;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.crypto.passphraseagents.UserCancellationException;
import org.neuclear.commons.crypto.signers.InvalidPassphraseException;
import org.neuclear.commons.crypto.signers.NonExistingSignerException;
import org.neuclear.id.builders.Builder;
import org.neuclear.id.tools.commandline.CommandLineSigner;
import org.neuclear.xml.XMLException;
import org.neuclear.xml.XMLTools;
import org.neuclear.xml.xmlsec.KeyInfo;

import java.security.PublicKey;

/*
 *  The NeuClear Project and it's libraries are
 *  (c) 2002-2004 Antilles Software Ventures SA
 *  For more information see: http://neuclear.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * User: pelleb
 * Date: Jun 2, 2004
 * Time: 12:54:04 PM
 */
public class GenerateRulesTemplate extends CommandLineSigner {
    public GenerateRulesTemplate(String[] args) throws UserCancellationException, InvalidPassphraseException {
        super(args);
        nickname = cmd.getOptionValue('n');
        units = cmd.getOptionValue('u');
        rulesurl = cmd.getOptionValue('r');
        issueralias = cmd.getOptionValue('y');
        servicealias = cmd.getOptionValue('s');
        serviceurl = cmd.getOptionValue('x');

    }

    public final static void main(String args[]) {
        try {
            System.out.println();
            final GenerateRulesTemplate tool = new GenerateRulesTemplate(args);
            tool.execute();
        } catch (UserCancellationException e) {
            System.out.println("Bye");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected String getExtraHelp() {
        return super.getExtraHelp();
    }

    protected boolean hasArguments() {
//        return true;
        return cmd.hasOption("r") && cmd.hasOption("y") && cmd.hasOption("s") && cmd.hasOption("x");
    }

    protected Builder build() throws NeuClearException {

        return new AssetBuilder(nickname, rulesurl, serviceurl, getPublicKey(servicealias), getPublicKey(issueralias), 2, 0, units);
    }

    protected PublicKey getPublicKey(String alias) {
        if (alias.endsWith(".xml")) {
            System.out.println("Loading Public Key from:" + alias);
            try {
                KeyInfo ki = new KeyInfo(XMLTools.loadDocument(alias).getRootElement());
                return ki.getPublicKey();
            } catch (XMLException e) {
                System.err.println(e.getLocalizedMessage());
                System.exit(1);
            }
        }
        try {
            return sig.getPublicKey(alias);
        } catch (NonExistingSignerException e) {
            System.err.println("alias " + alias + " not found");
            System.exit(1);
            return null;
        }
    }

    protected void getLocalOptions(org.apache.commons.cli.Options options) {
        options.addOption(new Option("n", "nickname", true, "Nickname of Asset"));
        options.addOption(new Option("u", "units", true, "Unit of Asset"));
        options.addOption(new Option("r", "rulesurl", true, "Rules URL (where the generated file will live)"));
        options.addOption(new Option("y", "issueralias", true, "Issuer's alias in Key Store or xml key info file"));
        options.addOption(new Option("s", "servicealias", true, "Service's alias in Key Store or xml key info file"));
        options.addOption(new Option("x", "serviceurl", true, "Service's URL"));
    }

    String nickname;
    String units;
    String rulesurl;
    String issueralias;
    String servicealias;
    String serviceurl;
}
