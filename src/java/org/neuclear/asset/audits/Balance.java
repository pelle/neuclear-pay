package org.neuclear.asset.audits;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.verifier.VerifyingReader;

import java.util.Date;

/*
$Id: Balance.java,v 1.1 2004/07/21 16:00:51 pelle Exp $
$Log: Balance.java,v $
Revision 1.1  2004/07/21 16:00:51  pelle
Added Balance and History related classes.

*/

/**
 * User: pelleb
 * Date: Jul 9, 2004
 * Time: 2:37:52 PM
 */
public class Balance extends SignedNamedObject {
    private Balance(SignedNamedCore core, BalanceRequest req, double balance, double available, Date time) {
        super(core);
        this.req = req;
        this.balance = balance;
        this.available = available;
        this.time = time.getTime();
    }

    public BalanceRequest getReq() {
        return req;
    }

    public double getBalance() {
        return balance;
    }

    public double getAvailable() {
        return available;
    }

    public Date getTime() {
        return new Date(time);
    }

    private final BalanceRequest req;
    private final double balance;
    private final double available;
    private final long time;

    public static final String BALANCE_TAGNAME = "Balance";
    public static final String TRUE_BALANCE_TAGNAME = "TrueBalance";
    public static final String AVAILABLE_BALANCE_TAGNAME = "AvailableBalance";
    public static final String VALUETIME_TAG = "ValueTime";

    private static double parseAmount(SignedNamedCore core, Element elem, String tagname) throws InvalidNamedObjectException {
        Element value = elem.element(tagname);
        if (value == null)
            throw new InvalidNamedObjectException(core.getName(), "No " + tagname + " element found.");
        String text = value.getTextTrim();
        if (text == null || text.length() == 0)
            throw new InvalidNamedObjectException(core.getName(), tagname + " doesn't contain an amount");
        return Double.parseDouble(text);
    }

    public static final class Reader implements NamedObjectReader {

        public SignedNamedObject read(SignedNamedCore core, Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().getURI().equals(TransferGlobals.XFER_NSURI))
                throw new InvalidNamedObjectException(core.getName(), "Not in XML NameSpace: " + AssetGlobals.NS_ASSET.getURI());
            if (!elem.getName().equals(BALANCE_TAGNAME))
                throw new InvalidNamedObjectException(core.getName(), "Incorrect XML Tagname for reader: " + BALANCE_TAGNAME);
            Element reqElem = elem.element(BalanceRequest.BALANCE_REQUEST_TAGNAME);
            if (reqElem == null)
                throw new InvalidNamedObjectException(core.getName(), "Balance does not contain BalanceRequest");
            BalanceRequest req = (BalanceRequest) VerifyingReader.getInstance().read(DocumentHelper.createDocument(reqElem.createCopy()).getRootElement());
            return new Balance(core, req, parseAmount(core, elem, TRUE_BALANCE_TAGNAME), parseAmount(core, elem, AVAILABLE_BALANCE_TAGNAME), TransferGlobals.parseTimeStampElement(elem, VALUETIME_TAG));
        }
    }

}
