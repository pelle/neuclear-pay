<%@ page import="org.neuclear.id.Identity,
                 org.neuclear.commons.Utility,
                 org.neuclear.asset.contracts.builders.TransferRequestBuilder,
                 org.neuclear.id.resolver.NSResolver,
                 org.neuclear.asset.contracts.Asset,
                 org.neuclear.commons.time.TimeTools,
                 org.neuclear.id.builders.SignatureRequestBuilder,
                 org.neuclear.asset.contracts.AssetGlobals,
                 org.neuclear.asset.contracts.TransferGlobals,
                 org.neuclear.asset.receiver.servlet.AssetControllerServlet,
                 org.neuclear.id.SignedNamedObject,
                 org.neuclear.commons.crypto.Base64,
                 org.neuclear.commons.servlets.ServletTools"%>
<%
    AssetGlobals.registerReaders();
    TransferGlobals.registerReaders();
    AssetControllerServlet controller=AssetControllerServlet.getInstance();
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
    Servlet servlet=config.getServletContext().getServlet("");
    TransferRequestBuilder transfer=new TransferRequestBuilder(
            controller.getAsset(),
            userns,
            NSResolver.resolveIdentity(recipient),
            amount,
            TimeTools.now(),
            comment
    ) ;
    SignatureRequestBuilder sigreq=new SignatureRequestBuilder(controller.getServiceid(),userns.getName(),transfer,comment);
    SignedNamedObject sig=sigreq.sign(controller.getSigner());

%>
<form action="<%=userns.getSigner()%>" method="POST">
<input name="base64xml" value="<%=Base64.encode(sig.getEncoded().getBytes())%>" type="hidden">
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