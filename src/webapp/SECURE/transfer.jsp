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
                 org.neuclear.asset.AssetController,
                 org.neuclear.asset.servlet.ServletAssetControllerFactory,
                 org.neuclear.ledger.Ledger,
                 org.neuclear.ledger.servlets.ServletLedgerFactory"%>
<%
    AssetGlobals.registerReaders();
    TransferGlobals.registerReaders();
    final Signer signer = ServletSignerFactory.getInstance().createSigner(config);
    Signatory userns=(Signatory) request.getUserPrincipal();
    String service=ServletTools.getInitParam("serviceid",config);
//    String asseturl=ServletTools.getInitParam("asset",config);
    AssetController controller=ServletAssetControllerFactory.getInstance().createAssetController(config);
    Ledger ledger=ServletLedgerFactory.getInstance().createLedger(config);
    Asset asset=controller.getAsset();
    String recipient=Utility.denullString(request.getParameter("recipient"));
    double amount=Double.parseDouble(Utility.denullString(request.getParameter("amount"),"0"));
    boolean submit=!Utility.isEmpty(request.getParameter("submit"));
    String comment=Utility.denullString(request.getParameter("comment"));

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
<div id="subtitle">Transfer Funds</div>


<div id="content">
<%
if (!submit){
%>
<p>
       Use this screen to perform a transfer of funds to another id.
</p>
<p>
<form action="transfer.jsp" method="POST">
    <p>Account: <br/><%=userns.getName()%></p>
    <p>Available Balance: <br/><%=ledger.getAvailableBalance(userns.getName())%></p>
    <hr/>
    <p>Recipient:<br/>
    <input type="text" name="recipient" value="<%=recipient%>"/></p>
    <p>Amount:<br/>
    <input type="text" name="amount" value="<%=amount%>"/></p>
    <p>Comment:<br/>
    <input type="text" name="comment" value="<%=comment%>"/></p>
    <hr/>
    <p><input type="submit" name="submit" value="Verify"/></p>

</form>
</p>
<% } else {
    TransferOrderBuilder transfer=new TransferOrderBuilder(
            asset.getName(),
            recipient,//new Signatory(signer.getPublicKey(recipient)),
            new Amount(amount),
            comment
    ) ;
    SignatureRequestBuilder sigreq=new SignatureRequestBuilder(transfer,comment);
    SignedNamedObject sig=sigreq.convert(service,signer);

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
    </div>
</body>
</html>