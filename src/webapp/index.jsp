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
                 org.neuclear.asset.contracts.Asset"%>
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
<p>You need to be running the NeuClear Personal Signer to log in.
<a href="http://dist.neuclear.org/app/neuclear-signer.jnlp">Start with Java Web Start</a>. Note on first run this
will download about 4MB.</p>
<p>Click here for more information about the <a href="http://neuclear.org/display/neu/Personal+Signer">Personal Signer</a></p>
<%
    } else {
        %>
        <ul>
            <li><a href="SECURE/transfer.jsp">Transfer Funds</a></li>
            <li><a href="SECURE/browse/">Browse Transactions</a></li>
            <li><a href="SECURE/issue.jsp">Request Funds</a></li>
            <li><a href="register.jsp">Register Identity Page</a></li>

        </ul>
        <%
    }
%>
<hr/><a href="bux.html">NeuClear Bux Rules</a><hr>
For more information about what this is visit <a href="http://neuclear.org">NeuClear.org</a>
</div>
<table style="float:right">
<tr><th colspan=2>Asset Statistics</th></tr>
<tr class="even"><td>Amount in Circulation</td><td align="right"><%=stats.getCirculation()%> <%=asset.getUnits()%></td></tr>
<tr class="odd"><td>Amount of Accounts</td><td align="right"><%=stats.getAmountOfAccounts()%></td></tr>
<tr class="even"><td>Amount of Transactions</td><td align="right"><%=stats.getTransactionCount()%></td></tr>
<%if(!loggedin){ %>
<tr><td colspan="2">
<form action="Authorize" method="POST">
    <input type="submit" name="submit" value="Login" style="font-size:small">
</form></td></tr>

<%}else{
        Book book=(Book) session.getAttribute("book");
        double balance=ledger.getBalance(userns.getName());
        double available=ledger.getAvailableBalance(userns.getName());
%>
    <tr><th>Account</th><th  title="<%=book.getId()%>" ><%=book.getNickname()%></th></tr>
<tr class="even"><td>Balance</td><td align="right"><%=balance%> <%=asset.getUnits()%></td></tr>
<tr class="odd"><td>Available</td><td align="right"><%=available%> <%=asset.getUnits()%></td></tr>
<tr class="even"><td>Held in Exchange</td><td align="right"><%=balance-available%> <%=asset.getUnits()%></td></tr>
<tr><td colspan="2"><form method="POST" action="<%=ServletTools.getAbsoluteURL(request,"/")%>"><input type="submit" value="Log Out" name="logout"/></form></th></tr>
<%}%>
</table>

</body>
</html>