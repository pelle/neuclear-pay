<%@ page import="org.neuclear.id.Identity,
                 org.neuclear.commons.Utility,
                 org.neuclear.asset.contracts.builders.TransferRequestBuilder,
                 org.neuclear.id.resolver.NSResolver,
                 org.neuclear.asset.contracts.Asset,
                 org.neuclear.commons.time.TimeTools,
                 org.neuclear.id.builders.SignatureRequestBuilder"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%
    Identity userns=(Identity) request.getUserPrincipal();

    String recipient=Utility.denullString(request.getParameter("recipient"));
    double amount=Double.parseDouble(Utility.denullString(request.getParameter("amount"),"0"));
    boolean submit=!Utility.isEmpty(request.getParameter("submit"));
    String comment=Utility.denullString(request.getParameter("comment"));

%>
<html><head><title>Transfer - <%=userns.getName()%></title></head>
<body>
<h1>Transfer Funds</h1>
<%  
if (!submit){
%>
<p>
       Use this screen to perform a transfer of funds to another id.
</p>
<p>
<form action="transfer.jsp" method="POST">
    <p>Account: <%=userns.getName()%></p>
    <p>Recipient:
    <input type="text" name="recipient" value="<%=recipient%>"/></p>
    <p>Amount:
    <input type="text" name="recipient" value="<%=amount%>"/></p>
    <p>Comment:
    <input type="text" name="comment" value="<%=comment%>"/></p>
    <p><input type="submit" name="submit" value="Verify"/></p>

</form>
</p>
<% } else {
    TransferRequestBuilder transfer=new TransferRequestBuilder(
            (Asset)NSResolver.resolveIdentity("neu://test/bux"),
            userns,
            NSResolver.resolveIdentity(recipient),
            amount,
            TimeTools.now(),
            comment
    ) ;
    SignatureRequestBuilder sigreq=new SignatureRequestBuilder("neu://test/bux",userns.getName(),transfer,comment);


%>
    Transfering to Signing Server

<%
}
    %>
</body>
</html>