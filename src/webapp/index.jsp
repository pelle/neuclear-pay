<%@ page import="org.neuclear.commons.Utility,
                 org.neuclear.id.SignedNamedObject,
                 org.neuclear.id.auth.AuthenticationTicket,
                 org.neuclear.id.NSTools,
                 org.neuclear.commons.servlets.ServletTools        ,
                 org.neuclear.id.Identity,
                 org.neuclear.id.resolver.Resolver,
                 org.neuclear.id.Signatory,
                 org.neuclear.ledger.Ledger,
                 org.neuclear.ledger.servlets.ServletLedgerFactory"%>
 <%
    response.setHeader("Pragma","no-cache");
    response.setDateHeader("Expires",0);
    Signatory userns=(Signatory) request.getUserPrincipal();
    boolean loggedin=userns!=null;

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
<form action="Authorize" method="POST">
<table bgcolor="#FFFFE0"><tr><td valign="top">
    </td><td valign="top">
    <input type="submit" name="submit" value="Login">
    </td>
</tr>
<tr><td colspan="2" bgcolor="#F0F0FF">
You need to be running the NeuClear Personal Signer to log in.
<a href="http://old.neuclear.org/signer/jnlp/neuclear-signer.jnlp">Start with Java Web Start</a>
</td></tr></table>
</form>
<%
    } else {
        Ledger ledger=ServletLedgerFactory.getInstance().createLedger(config);
        %>
        <%=userns.getName()%> is Logged In<br><hr>
        You currently have available: <%=ledger.getAvailableBalance(userns.getName())%>
        <ul>
            <li><a href="SECURE/transfer.jsp">Transfer Funds</a></li>
            <li><a href="SECURE/browse/">Browse Transactions</a></li>
            <li><a href="SECURE/issue.jsp">Request Funds</a></li>
            <hr/>

            <li><a href="<%=ServletTools.getAbsoluteURL(request,"/")%>?logout=1">Log Out</a></li>

        </ul>
        <%
    }
%>
<hr/><a href="bux.html">NeuClear Bux Rules</a><hr>
For more information about what this is visit <a href="http://neuclear.org">NeuClear.org</a>
</div>

</body>
</html>