<%@ page import="org.neuclear.commons.Utility,
                 org.neuclear.asset.orders.builders.TransferOrderBuilder,
                 org.neuclear.id.resolver.Resolver,
                 org.neuclear.asset.contracts.Asset,
                 org.neuclear.commons.time.TimeTools,
                 org.neuclear.id.builders.SignatureRequestBuilder,
                 org.neuclear.asset.contracts.AssetGlobals,
                 org.neuclear.asset.servlet.AssetControllerServlet,
                 org.neuclear.commons.crypto.Base64,
                 org.neuclear.commons.servlets.ServletTools,
                 org.neuclear.asset.orders.TransferGlobals,
                 org.neuclear.asset.orders.Amount,
                 org.neuclear.commons.crypto.signers.TestCaseSigner,
                 org.neuclear.commons.crypto.signers.ServletSignerFactory,
                 org.neuclear.commons.crypto.signers.Signer,
                 org.neuclear.asset.AssetController,
                 org.neuclear.asset.servlet.ServletAssetControllerFactory,
                 org.neuclear.ledger.LedgerController,
                 org.neuclear.ledger.servlets.ServletLedgerFactory,
                 org.neuclear.ledger.Book,
                 org.neuclear.ledger.LowlevelLedgerException,
                 org.neuclear.ledger.UnknownBookException,
                 org.neuclear.asset.LowLevelPaymentException,
                 org.neuclear.id.*"%>
<%
//    AssetGlobals.registerReaders();
//    TransferGlobals.registerReaders();
    final Signer signer = ServletSignerFactory.getInstance().createSigner(config);
    Signatory userns=(Signatory) request.getUserPrincipal();
    if (userns==null){
        response.sendRedirect("../");
        return;
    }
    String service=ServletTools.getInitParam("serviceid",config);
//    String asseturl=ServletTools.getInitParam("asset",config);
    AssetController controller=ServletAssetControllerFactory.getInstance().createAssetController(config);
    LedgerController ledger=ServletLedgerFactory.getInstance().createLedger(config);
    Asset asset=controller.getAsset();
    String recipient=Utility.denullString(request.getParameter("recipient"));
    double amount=Double.parseDouble(Utility.denullString(request.getParameter("amount"),"0"));
    boolean submit=!Utility.isEmpty(request.getParameter("submit"));
    String comment=Utility.denullString(request.getParameter("comment"));

    Book recpbook=null;
    if (!Utility.isEmpty(recipient)){
        if (recipient.startsWith("http://")||recipient.startsWith("https://")) {
            try {
                Identity id=controller.register(recipient);
                recpbook=ledger.getBook(id.getSignatory().getName());
            } catch (Exception e) {
                submit=false;
            }
        }  else {
            try {
                recpbook=ledger.getBook(recipient);
            } catch (UnknownBookException e) {
                submit=false;
            }
        }
    }
    final double balance = ledger.getAvailableBalance(asset.getDigest(), userns.getName());
    if (submit&&(amount>balance||amount<0))
        submit=false;
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
           Book book=(Book) session.getAttribute("book");

%>
<p>
<form action="transfer.jsp" method="POST">
    <h1>Use this screen to perform a transfer of funds to another id.</h1>
    <table>
    <tr><th>Account</th><td><%=book.getNickname()%></td></tr>
    <tr><th>Account ID</th><td><%=book.getId()%></td></tr>
    <tr><th>Available Balance:</th><td><%=balance%> <%=asset.getUnits()%></td></tr>
    </table>
    <hr/>
    <p<%=(!Utility.isEmpty(recipient)&&recpbook==null)?" class=\"invalid\"":""%>>Recipient: (Use the full 32 character id, the nickname if known or the identity url)<br/>
    <input type="text" name="recipient" value="<%=recipient%>" size="32"/>
    <%=(!Utility.isEmpty(recipient)&&recpbook==null)?"Unknown Account":""%>
    </p>
    <p<%=(amount>balance)||(amount<0)?" class=\"invalid\"":""%>>Amount:<br/>
    <input type="text" name="amount" value="<%=amount%>" size="10" style="text-align:right"/> <%=asset.getUnits()%>
    <%=(amount>balance)?"Insufficient Funds":""%>
    <%=(amount<0)?"You can not transfer a negative amount":""%>
    </p>
    <p>Comment:<br/>
    <input type="text" name="comment" value="<%=comment%>" size="40"/></p>
    <hr/>
    <p class="formaction">
        <input type="submit" name="submit" value="Verify"/>
        <input type="submit" name="cancel" value="Cancel" onClick="history.go(-1)"/>

    </p>

</form>
</p>
<% } else {


    TransferOrderBuilder transfer=new TransferOrderBuilder(
            asset.getURL(),
            recpbook.getId(),//new Signatory(signer.getPublicKey(recipient)),
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