package org.neuclear.asset.servlet;

import org.neuclear.asset.AssetController;
import org.neuclear.asset.InvalidTransferException;
import org.neuclear.asset.contracts.Asset;
import org.neuclear.asset.orders.Amount;
import org.neuclear.asset.orders.builders.TransferOrderBuilder;
import org.neuclear.commons.NeuClearException;
import org.neuclear.commons.Utility;
import org.neuclear.commons.servlets.ServletMessages;
import org.neuclear.commons.servlets.ServletTools;
import org.neuclear.id.Identity;
import org.neuclear.id.Signatory;
import org.neuclear.id.builders.Builder;
import org.neuclear.id.signers.SignatureRequestServlet;
import org.neuclear.ledger.Book;
import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownBookException;
import org.neuclear.ledger.servlets.ServletLedgerFactory;
import org.pkyp.client.PKYP;
import org.pkyp.client.SearchException;
import org.pkyp.recent.Page;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.ResourceBundle;

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

$Id: TransferOrderServlet.java,v 1.1 2004/06/19 21:20:03 pelle Exp $
$Log: TransferOrderServlet.java,v $
Revision 1.1  2004/06/19 21:20:03  pelle
Added TransferOrderServlet which is fully localized to Spanish and English
Asset now has a getFormatter() method which returns a localized currency formatter for amounts of Asset.

Revision 1.5  2004/05/24 18:31:30  pelle
Changed asset id in ledger to be asset.getSignatory().getName().
Made SigningRequestServlet and SigningServlet a bit clearer.

Revision 1.4  2004/04/02 23:04:36  pelle
Got TransferOrder and Builder working with their test cases.
Working on TransferReceipt which is the first embedded receipt. This is causing some problems at the moment.

Revision 1.3  2004/04/01 23:18:33  pelle
Split Identity into Signatory and Identity class.
Identity remains a signed named object and will in the future just be used for self declared information.
Signatory now contains the PublicKey etc and is NOT a signed object.

Revision 1.2  2004/03/02 18:58:35  pelle
Further cleanups in neuclear-id. Moved everything under id.

Revision 1.1  2004/01/13 15:11:17  pelle
Now builds.
Now need to do unit tests

Revision 1.5  2004/01/12 22:39:14  pelle
Completed all the builders and contracts.
Added a new abstract Value class to contain either an amount or a list of serial numbers.
Now ready to finish off the AssetControllers.

Revision 1.4  2004/01/11 00:39:06  pelle
Cleaned up the schemas even more they now all verifiy.
The Order/Receipt pairs for neuclear pay, should now work. They all have Readers using the latest
Schema.
The TransferBuilders are done and the ExchangeBuilders are nearly there.
The new EmbeddedSignedNamedObject builder is useful for creating new Receipts. The new ReceiptBuilder uses
this to create the embedded transaction.
ExchangeOrders now have the concept of BidItem's, you could create an ExchangeOrder bidding on various items at the same time, to be exchanged as one atomic multiparty exchange.
Still doesnt build yet, but very close now ;-)

Revision 1.3  2004/01/10 00:00:45  pelle
Implemented new Schema for Transfer*
Working on it for Exchange*, so far all Receipts are implemented.
Added SignedNamedDocument which is a generic SignedNamedObject that works with all Signed XML.
Changed SignedNamedObject.getDigest() from byte array to String.
The whole malarchy in neuclear-pay does not build yet. The refactoring is a big job, but getting there.

Revision 1.2  2004/01/05 23:47:09  pelle
Create new Document classification "order", which is really just inherint in the new
package layout.
Got rid of much of the inheritance that was lying around and thought a bit further about the format of the exchange orders.

Revision 1.1  2003/12/20 00:17:41  pelle
overwrote the standard Object.toString(), hashCode() and equals() methods for SignedNamedObject/Core
fixed cactus tests
Added TransferRequestServlet
Added cactus tests to pay

*/

/**
 * User: pelleb
 * Date: Dec 19, 2003
 * Time: 6:37:19 PM
 */
public class TransferOrderServlet extends SignatureRequestServlet {

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            controller = ServletAssetControllerFactory.getInstance().createAssetController(config);
            ledger = ServletLedgerFactory.getInstance().createLedger(config);
            asset = controller.getAsset();
        } catch (LowlevelLedgerException e) {
            e.printStackTrace();
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Signatory userns = (Signatory) request.getUserPrincipal();

        double amount = Double.parseDouble(Utility.denullString(request.getParameter("amount"), "0"));
        boolean submit = !Utility.isEmpty(request.getParameter("submit"));
        String comment = Utility.denullString(request.getParameter("comment"));
        String recipientfield = Utility.denullString(request.getParameter("recipient"));
        String recipient = Utility.denullString(request.getParameter("candidate"), recipientfield);

        Book recpbook = null;
        Iterator candidates = null;
        try {
            if (!Utility.isEmpty(recipient)) {
                if (recipient.startsWith("http://") || recipient.startsWith("https://")) {
                    try {
                        Identity id = controller.register(recipient);
                        recpbook = ledger.getBook(id.getSignatory().getName());
                    } catch (Exception e) {
                        submit = false;
                    }
                } else if (recipient.length() == 32) {
                    recpbook = ledger.getBook(recipient);
                } else {
                    try {
                        candidates = PKYP.search(recipientfield);
                        submit = false;
                    } catch (SearchException e) {

                    }
                }
            }
            double balance = 0;
            if (userns != null) {
                balance = ledger.getAvailableBalance(asset.getSignatory().getName(), userns.getName());
                if (submit && (amount > balance || amount < 0))
                    submit = false;
            }
            if (submit) {
                request.setAttribute("recpbook", recpbook);
                super.service(request, response);
                return;
            } else {
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();
                ResourceBundle messages = ServletMessages.getMessages("assetmessages", request);
                NumberFormat format = asset.getFormatter(request.getLocale());
                ServletTools.printHeader(out, request, getTitle(), messages.getString("transfer"));
                out.print("<form action=\"");
                out.print(ServletTools.getAbsoluteURL(request, request.getServletPath()));
                out.println("\" method=\"POST\">");
                if (userns != null) {
                    Book book = (Book) request.getSession(true).getAttribute("book");
                    if (book == null) {
                        book = ledger.getBook(userns.getName());
                        request.getSession().setAttribute("book", book);
                    }
                    out.print("<table>\n<tr><th>");
                    out.print(messages.getString("account"));
                    out.print("</th><td>");
                    out.print(book.getId());
                    out.print("</td></tr>\n<tr><th>");
                    out.print(messages.getString("account.nickname"));
                    out.print("</th><td>");
                    out.print(book.getNickname());
                    out.print("</td></tr>\n<tr><th>");
                    out.print(messages.getString("account.available"));
                    out.print("</th><td align=\"right\">");
                    out.print(format.format(balance));
                    out.println("</td></tr>\n</table>");


                }
                out.print("<p");
                out.print((!Utility.isEmpty(recipientfield) && recpbook == null && candidates == null) ? " class=\"invalid\">" : ">");
                out.print(messages.getString("transfer.recipient"));
                out.println("<br/>");
                out.print("<input type=\"text\" name=\"recipient\" value=\"");
                out.print(recipientfield);
                out.println("\" size=\"32\"/>");
                if (!Utility.isEmpty(recipientfield) && recpbook == null && candidates == null)
                    out.print(messages.getString("account.unknown"));
                out.println("</p>");
                if (candidates != null) {
                    String candidate = Utility.denullString(request.getParameter("candidate"));
                    out.print("<p><b>");
                    out.print(messages.getString("account.matches"));
                    out.print("</b> <span style=\"color:blue\">");
                    out.print(recipient);
                    out.println("</span>\n<dl>");
                    while (candidates.hasNext()) {
                        Page account = (Page) candidates.next();
                        out.print("<dt><input type=\"radio\" name=\"candidate\" value=\"");
                        out.print(account.getUrl());
                        out.print("\"");
                        if ((candidate.equals(account.getUrl()))) {
                            out.println(" selected");
                        }
                        out.print("/> <b>");
                        out.print(account.getNickname());
                        out.print("</b> (<a href=\"");
                        out.print(account.getUrl());
                        out.print("\" target=\"_blank\">");
                        out.print(account.getUrl());
                        out.println("</a></dt><dd>");
                        out.println(account.getSummary());
                        out.println("</dd>");
                    }
                    out.println("</dl></p>");
                }
                out.print("<p");
                out.print((amount < 0 || (userns != null && amount > balance)) ? " class=\"invalid\">" : ">");
                out.print(messages.getString("transfer.amount"));
                out.println("<br/>");
                out.print("<input type=\"text\" name=\"amount\" value=\"");
                out.print(amount);
                out.println("\" size=\"10\"/>");
                if (amount < 0)
                    out.print(messages.getString("transfer.amount.negative"));
                if ((userns != null && amount > balance))
                    out.print(messages.getString("transfer.amount.insufficient"));
                out.println("</p>");

                out.print("<p>");
                out.print(messages.getString("transfer.comment"));
                out.println("<br/>");
                out.print("<input type=\"text\" name=\"comment\" value=\"");
                out.print(comment);
                out.println("\" size=\"80\"/>");
                out.println("</p>");

                out.println("<hr/><p class=\"formaction\">\n" +
                        "        <input type=\"submit\" name=\"submit\" value=\"Verify\"/>\n" +
                        "    </p></form>");
                out.println("<a href=" + ServletTools.getAbsoluteURL(request, "/") + ">" + messages.getString("mainmenu") + "</a>");
            }
        } catch (LowlevelLedgerException e) {
            e.printStackTrace();
        } catch (UnknownBookException e) {
            e.printStackTrace();
        }
    }

    protected Builder createBuilder(HttpServletRequest request) throws NeuClearException {
        String comment = request.getParameter("comment");
        double amount = Double.parseDouble(Utility.denullString(request.getParameter("amount"), "0"));
        Book recpbook = (Book) request.getAttribute("recpbook");
        try {
            return new TransferOrderBuilder(asset,
                    recpbook.getId(),
                    recpbook.getSource(),
                    new Amount(amount),
                    comment);
        } catch (InvalidTransferException e) {
            throw new NeuClearException(e);
        }
    }

    protected String getRequestType() {
        return "Transfer Order";
    }

    private Asset asset;
    private AssetController controller;
    private LedgerController ledger;
}
