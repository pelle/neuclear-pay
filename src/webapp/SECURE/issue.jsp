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
                 org.neuclear.asset.orders.Amount,
                 org.neuclear.commons.crypto.signers.TestCaseSigner,
                 org.neuclear.commons.crypto.signers.ServletSignerFactory,
                 org.neuclear.commons.crypto.signers.Signer,
                 org.neuclear.asset.orders.builders.IssueOrderBuilder,
                 org.neuclear.asset.AssetController,
                 org.neuclear.asset.servlet.ServletAssetControllerFactory"%>
<%
    AssetGlobals.registerReaders();
    TransferGlobals.registerReaders();
    final Signer signer = ServletSignerFactory.getInstance().createSigner(config);
    Signatory userns=(Signatory) request.getUserPrincipal();
    String service=ServletTools.getInitParam("serviceid",config);
//    String asseturl=ServletTools.getInitParam("asset",config);
    Asset asset=(Asset)Resolver.resolveIdentity(ServletTools.getAbsoluteURL(request,"/bux.html"));
    double amount=Double.parseDouble(Utility.denullString(request.getParameter("amount"),"0"));
    boolean submit=!Utility.isEmpty(request.getParameter("submit"));

%>
<html>
<head><title>
NeuClear Bux
</title>
<link rel="stylesheet" type="text/css" href="../styles.css"/>
</head>
<body>
<div id="banner">
<img src="../images/logo.png"  alt=" "/>NeuClear Bux</div>
<div id="subtitle">Request Funds</div>
<h3>Request Beta Bux</h3>

<div id="content">
<%
if (!submit){
%>
<p>
       Use this screen to receive free beta bux.
</p>
<p>
<form action="issue.jsp" method="POST">
    <p>Account Recipient: <%=userns.getName()%></p>
    <p>Amount:
    <input type="text" name="amount" value="<%=amount%>"/></p>
    <p><input type="submit" name="submit" value="Verify"/></p>

</form>
</p>
<% } else {
    IssueOrderBuilder transfer=new IssueOrderBuilder(
            asset.getName(),
            userns.getName(),//new Signatory(signer.getPublicKey(recipient)),
            new Amount(amount),
            "Present"
    ) ;
    SignedNamedObject sig=transfer.convert("carol",signer);
    AssetController controller=ServletAssetControllerFactory.getInstance().createAssetController(config);
    SignedNamedObject obj=controller.receive(sig);
%>
    <p>You have just received <%=amount%> neuclear beta bux.</p>
    <hr/>
    <a href="<%=ServletTools.getAbsoluteURL(request,"/")%>">Main Menu</a>
<%
}
    %>
    </div>
</body>
</html>