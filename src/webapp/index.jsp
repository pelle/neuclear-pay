<%@ page import="org.neuclear.commons.Utility,
                 org.neuclear.id.SignedNamedObject,
                 org.neuclear.id.auth.AuthenticationTicket,
                 org.neuclear.id.NSTools,
                 org.neuclear.commons.servlets.ServletTools        ,
                 org.neuclear.id.Identity,
                 org.neuclear.id.resolver.Resolver,
                 org.neuclear.id.Signatory,
                 org.neuclear.ledger.LedgerController,
                 org.neuclear.ledger.servlets.ServletLedgerFactory,
                 org.neuclear.asset.AssetController,
                 org.neuclear.asset.servlet.ServletAssetControllerFactory,
                 org.neuclear.asset.AssetStatistics,
                 org.neuclear.ledger.Book,
                 org.neuclear.asset.contracts.Asset,
                 java.text.NumberFormat"%>
 <%
    response.setHeader("Pragma","no-cache");
    response.setDateHeader("Expires",0);
    Signatory userns=(Signatory) request.getUserPrincipal();
    boolean loggedin=userns!=null;
    AssetController controller=ServletAssetControllerFactory.getInstance().createAssetController(config);
    AssetStatistics stats=controller.getStats();
    Asset asset=controller.getAsset();

    LedgerController ledger=ServletLedgerFactory.getInstance().createLedger(config);
    if (loggedin){
        Book book=(Book) session.getAttribute("book");
        if (book==null){
            book=ledger.getBook(userns.getName());
            session.setAttribute("book",book);
        }
    }   else {
        session.removeAttribute("book");
    }
    NumberFormat format=asset.getFormatter(request.getLocale());
 %>
<html>
<head><title>
NeuClear Bux
</title>
<link rel="stylesheet" type="text/css" href="styles.css"/>
</head>
<body>
<div id="banner">
<img src="images/logo.png"  alt=" "/>NeuClear Bux</div>
<div id="subtitle">Electronic Currency</div>


<div id="content">

<%
    if(!loggedin){
%>
<a href="http://dist.neuclear.org/app/neuclear-signer.jnlp"><img src="images/neucleartrader_tasks_small.jpg" border="0"/></a>
<p>You need to be running the NeuClear Personal Trader to log in.
<a href="http://dist.neuclear.org/app/neuclear-signer.jnlp">Start with Java Web Start</a>. Note on first run this
will download about 4MB.</p>
<p>Click here for more information about using the <a href="http://neuclear.org/display/neu/Personal+Trader">Personal Trader</a></p>
<%
    } else {
        %>
        <p>If this is your first time here. Now would be a good idea to <a href="SECURE/issue.jsp">request some bux</a>.</p>
        <ul>
            <li><a href="SECURE/transfer.jsp">Transfer Funds</a></li>
            <li><a href="SECURE/browse/">Browse Transactions</a></li>
            <li><a href="SECURE/issue.jsp">Request Funds</a></li>
            <li><a href="register.jsp">Register Account Contact Page</a></li>

        </ul>
        <p>If you closed your Personal Trader you can restart it below:</p>
        <ul><li><a href="http://dist.neuclear.org/app/neuclear-signer.jnlp">Start NeuClear Personal Trader</a></li>
        </ul>
        <%
    }
%>
<hr/>
<p>NeuClear Bux is a NeuClear Asset governed by the following rules:</p>
<ul><li><a href="bux.html">NeuClear Bux Rules</a></li></ul>
<p>You can tell your Personal Trader about a new asset by clicking "Add asset.." and entering the URL (the web address) of the rules.
The URL to add for NeuClear bux is <tt>http://bux.neuclear.org/bux.html</tt>.
</p>
<hr>
<p>For more information about the goals of the NeuClear project visit <a href="http://neuclear.org">NeuClear.org</a></p>
</div>
<table style="float:right">
<tr><th colspan=2>Asset Statistics</th></tr>
<tr class="even"><td>Amount in Circulation</td><td align="right"><%=format.format(stats.getCirculation())%></td></tr>
<tr class="odd"><td>Amount of Accounts</td><td align="right"><%=stats.getAmountOfAccounts()%></td></tr>
<tr class="even"><td>Amount of Transactions</td><td align="right"><%=stats.getTransactionCount()%></td></tr>
<%if(!loggedin){ %>
<tr><td colspan="2">
<a href="Authorize"><img src="images/neuclearlogin.jpg" border="0"/></a>
</td></tr>

<%}else{
        Book book=(Book) session.getAttribute("book");
        double balance=ledger.getBalance(asset.getSignatory().getName(), userns.getName());
        double available=ledger.getAvailableBalance(asset.getSignatory().getName(), userns.getName());
%>
    <tr><th>Account</th><th  title="<%=book.getId()%>" ><%=book.getNickname()%></th></tr>
<tr class="even"><td>Balance</td><td align="right"><%=format.format(balance)%></td></tr>
<tr class="odd"><td>Available</td><td align="right"><%=format.format(available)%></td></tr>
<tr class="even"><td>Held in Exchange</td><td align="right"><%=format.format(balance-available)%>%></td></tr>
<tr><td colspan="2"><form method="POST" action="<%=ServletTools.getAbsoluteURL(request,"/")%>"><input type="submit" value="Log Out" name="logout"/></form></th></tr>
<%}%>
</table>

</body>
</html>