package org.neuclear.asset.audits.builders;

import org.neuclear.asset.audits.Balance;
import org.neuclear.asset.audits.BalanceRequest;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.asset.orders.builders.ReceiptBuilder;

import java.util.Date;

/*
$Id: BalanceBuilder.java,v 1.1 2004/07/21 16:00:50 pelle Exp $
$Log: BalanceBuilder.java,v $
Revision 1.1  2004/07/21 16:00:50  pelle
Added Balance and History related classes.

*/

/**
 * User: pelleb
 * Date: Jul 9, 2004
 * Time: 4:07:22 PM
 */
public class BalanceBuilder extends ReceiptBuilder {
    public BalanceBuilder(BalanceRequest req, double balance, double available, Date time) {
        super(TransferGlobals.createQName(Balance.BALANCE_TAGNAME), req, time);
        addElement(TransferGlobals.createElement(Balance.TRUE_BALANCE_TAGNAME, Double.toString(balance)));
        addElement(TransferGlobals.createElement(Balance.AVAILABLE_BALANCE_TAGNAME, Double.toString(available)));
    }
}
