<%@ page import="org.neuclear.id.Identity,
                 org.neuclear.commons.Utility,
                 org.neuclear.asset.orders.builders.TransferOrderBuilder,
                 org.neuclear.id.resolver.Resolver,
                 org.neuclear.asset.contracts.Asset,
                 org.neuclear.commons.time.TimeTools,
                 org.neuclear.id.builders.SignatureRequestBuilder,
                 org.neuclear.asset.contracts.AssetGlobals,

                 org.neuclear.asset.servlet.AssetControllerServlet,
                 org.neuclear.id.SignedNamedObject,
                 org.neuclear.commons.crypto.Base64,
                 org.neuclear.commons.servlets.ServletTools,
                 org.neuclear.id.Service,
                 org.neuclear.asset.orders.TransferGlobals,
                 org.neuclear.id.Signatory,
                 org.neuclear.asset.orders.Amount"%>
<%
    AssetGlobals.registerReaders();
    TransferGlobals.registerReaders();
    Signatory userns=(Signatory) request.getUserPrincipal();
    String service=ServletTools.getInitParam("service",config);
//    Service asset=(Service)Resolver.resolveIdentity(service);
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
    <input type="text" name="amount" value="<%=amount%>"/></p>
    <p>Comment:
    <input type="text" name="comment" value="<%=comment%>"/></p>
    <p><input type="submit" name="submit" value="Verify"/></p>

</form>
</p>
<% } else {
    TransferOrderBuilder transfer=new TransferOrderBuilder(
            service,
            recipient,
            new Amount(amount),
            comment
    ) ;
    SignatureRequestBuilder sigreq=new SignatureRequestBuilder(transfer,comment);
//    SignedNamedObject sig=sigreq.sign();

%>
<form action="http://localhost:11870/Signer" method="POST">
<input name="neuclear-request" value="<%=Base64.encode(sig.getEncoded().getBytes())%>" type="hidden">
<input name="endpoint" value="<%=ServletTools.getAbsoluteURL(request, "/Asset")%>" type="hidden">
</form>
    Transfering to Signing Server
<script language="javascript">
<!--
document.forms[0].submit();
-->
</script>
<%
}
    %>
</body>
</html>