<%@ page import="org.neuclear.commons.Utility,
                 org.neuclear.id.SignedNamedObject,
                 org.neuclear.id.auth.AuthenticationTicket,
                 org.neuclear.id.NSTools,
                 org.neuclear.commons.servlets.ServletTools        ,
                 org.neuclear.id.Identity,
                 org.neuclear.id.resolver.Resolver,
                 org.neuclear.id.Signatory"%>
 <%
    response.setHeader("Pragma","no-cache");
    response.setDateHeader("Expires",0);
    Signatory userns=(Signatory) request.getUserPrincipal();
    boolean loggedin=userns!=null;

 %>
<html>
<head><title>
NeuClear Bux
</title></head>
<body>
<h1>NeuClear Bux Electronic Currency</h1>
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
        %>
        <%=userns.getName()%> is Logged In<br><hr>
        <ul>
            <li><a href="SECURE/transfer.jsp">Transfer Funds</a></li>

            <li><a href="<%=ServletTools.getAbsoluteURL(request,"/")%>?logout=1">Log Out</a></li>

        </ul>
        <%
    }
%>
<hr/><a href="bux.html">NeuClear Bux Rules</a><hr>
For more information about what this is visit <a href="http://neuclear.org">NeuClear.org</a>

</body>
</html>