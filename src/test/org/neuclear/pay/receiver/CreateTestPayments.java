package org.neuclear.pay.receiver;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.neuclear.id.NSTools;
import org.neuclear.id.builders.NamedObjectBuilder;
import org.neuclear.pay.contracts.builders.TransferRequestBuilder;
import org.neuclear.signers.commandline.CommandLineSigner;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

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

$Id: CreateTestPayments.java,v 1.1 2003/10/25 00:46:29 pelle Exp $
$Log: CreateTestPayments.java,v $
Revision 1.1  2003/10/25 00:46:29  pelle
Added tests to test the PaymentReceiver.
CreateTestPayments is a command line utility to create signed payment requests

*/

/**
 * User: pelleb
 * Date: Oct 24, 2003
 * Time: 11:50:47 AM
 */
public class CreateTestPayments extends CommandLineSigner {
    public CreateTestPayments(String[] args) throws ParseException, NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException {
        super(args);
    }

    public NamedObjectBuilder build() throws Exception {
        String to = cmd.getOptionValue("r");
        String asset = cmd.getOptionValue("c");
        double amount = Double.parseDouble(cmd.getOptionValue("x"));
        String id = NSTools.createUniqueNamedID(alias, to);
        return new TransferRequestBuilder(id, asset, to, amount);
    }

    public static void main(String args[]) {
        try {
            CreateTestPayments signer = new CreateTestPayments(args);
            signer.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getExtraHelp() {
        return "[--asset neu://verax/pay --payee neu://bob@verax --amount 20.00]";
    }

    protected boolean hasArguments() {
        return (cmd.hasOption("a") && cmd.hasOption("c") && cmd.hasOption("r") && cmd.hasOption("x"));
    }

    protected void getLocalOptions(Options options) {
        options.addOption("c", "asset", true, "specify id of asset");
        options.addOption("r", "payee", true, "specify id of payee");
        options.addOption("x", "amount", true, "specify amount");
    }

}
