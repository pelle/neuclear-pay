package org.neuclear.asset.audits;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.neuclear.asset.contracts.AssetGlobals;
import org.neuclear.asset.orders.TransferGlobals;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.id.InvalidNamedObjectException;
import org.neuclear.id.NamedObjectReader;
import org.neuclear.id.SignedNamedCore;
import org.neuclear.id.SignedNamedObject;
import org.neuclear.id.verifier.VerifyingReader;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/*
$Id: History.java,v 1.1 2004/07/21 16:00:51 pelle Exp $
$Log: History.java,v $
Revision 1.1  2004/07/21 16:00:51  pelle
Added Balance and History related classes.

*/

/**
 * User: pelleb
 * Date: Jul 9, 2004
 * Time: 2:37:52 PM
 */
public class History extends SignedNamedObject {
    private History(SignedNamedCore core, HistoryRequest req, Date time, SignedNamedObject history[]) {
        super(core);
        this.req = req;
        this.time = time.getTime();
        this.history = new SignedNamedObject[history.length];
        System.arraycopy(history, 0, this.history, 0, history.length);
    }

    public HistoryRequest getReq() {
        return req;
    }

    public Date getTime() {
        return new Date(time);
    }

    public Iterator iterate() {
        return new Iterator() {
            int i = 0;

            public void remove() {

            }

            public boolean hasNext() {
                return i < history.length;
            }

            public Object next() {
                return history[i++];
            }
        };
    }

    private final long time;
    private final HistoryRequest req;
    private final SignedNamedObject history[];
    public final static String HISTORY_TAGNAME = "History";

    public static final class Reader implements NamedObjectReader {

        public SignedNamedObject read(SignedNamedCore core, Element elem) throws InvalidNamedObjectException {
            if (!elem.getNamespace().getURI().equals(TransferGlobals.XFER_NSURI))
                throw new InvalidNamedObjectException(core.getName(), "Not in XML NameSpace: " + AssetGlobals.NS_ASSET.getURI());
            if (!elem.getName().equals(HISTORY_TAGNAME))
                throw new InvalidNamedObjectException(core.getName(), "Incorrect XML Tagname for reader: " + HISTORY_TAGNAME);

            List children = elem.elements();
            if (children.size() < 2)
                throw new InvalidNamedObjectException(core.getName(), "History does not contain required elements");
            Element reqElem = null;
            Date time = null;
            SignedNamedObject history[] = new SignedNamedObject[children.size() - 3];
            int y = 0;
            for (int i = 0; i < children.size(); i++) {
                Element e = (Element) children.get(i);
                if (reqElem == null && e.getName().equals(HistoryRequest.HISTORY_REQUEST_TAGNAME))
                    reqElem = e;
                else if (time == null && e.getName().equals("ValueTime"))
                    try {
                        time = TimeTools.parseTimeStamp(e.getTextTrim());
                    } catch (ParseException e1) {
                        throw new InvalidNamedObjectException(core.getName(), e1);
                    }
                else if (!e.getName().equals("Signature"))
                    history[y++] = VerifyingReader.getInstance().read(DocumentHelper.createDocument(e.createCopy()).getRootElement());
            }
            if (reqElem == null)
                throw new InvalidNamedObjectException(core.getName(), "History does not contain HistoryRequest");
            if (time == null)
                throw new InvalidNamedObjectException(core.getName(), "History does not contain ValueTime");
            HistoryRequest req = (HistoryRequest) VerifyingReader.getInstance().read(DocumentHelper.createDocument(reqElem.createCopy()).getRootElement());
            return new History(core, req, time, history);
        }
    }

}
