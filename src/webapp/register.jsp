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
                 org.neuclear.asset.orders.builders.IssueOrderBuilder,
                 org.neuclear.asset.AssetController,
                 org.neuclear.asset.servlet.ServletAssetControllerFactory,
                 org.neuclear.ledger.Book,
                 org.neuclear.asset.LowLevelPaymentException,
                 org.neuclear.id.*,
                 java.io.StringWriter,
                 java.io.PrintWriter"%>
<%
    AssetController controller=ServletAssetControllerFactory.getInstance().createAssetController(config);
    String url=Utility.denullString(request.getParameter("url"));
   boolean submit=(!Utility.isEmpty(request.getParameter("submit")));
    boolean success=false;
    boolean invalid=false;
    boolean resolution=false;
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
<div id="subtitle">Register</div>
<div id="content">
<%
    if (!Utility.isEmpty(url)){
        %>
<h3>Registering Identity from <%=url%></h3>
        <%
        try {
            Identity id=controller.register(url);
            success=true;
%>
    <table>
    <tr><th colspan="2">You have now successfully been registered on <%=controller.getAsset().getNickname()%></th></tr>
    <tr><th>Account Nickname</th><td><%=id.getNickname()%></td></tr>
    <tr><th>Account ID</th><td><%=id.getSignatory().getName()%></td></tr>
    <tr><th>URL</th><td><%=id.getURL()%></td></tr>
    <tr><th colspan="2"><a href="index.jsp">Go Back</a></th></tr>
    </table>

            <%
        } catch (LowLevelPaymentException e) {
%>
    <p id="invalid">A low level error occurred. Please paste the following into an email and send it to pelle@neuclear.org:</p>
    <pre><%
        StringWriter writer=new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
            %><%=writer.toString()%></pre><%
        } catch (NameResolutionException e) {
            resolution=true;
        } catch (InvalidNamedObjectException e) {
            invalid=true;
        }
    }
if (!success){
    boolean error=(submit&&Utility.isEmpty(url))||resolution||invalid;
%>
<p>
       Use this screen to register your NeuClear Identity with us. This is absolutely optional on the Beta Bux site.
       Some asset controllers may require that you register to use their assets.
</p>
<p>
         <i>For this early beta you do require a few technical skills for this. This will change very soon. You currently
         should know how to do the following:</i>
         <ul>
         <li>How to write very simple html. Using Microsoft FrontPage is fine</li>
         <li>You need to know how to upload a file to your web server.</li>
         </ul>
</p>
<p>
       If you havent already registered. Follow these simple steps:
       <ul>
       <li>Create a simple html page writing as much or as little about your self as you want.</li>
       <li>Decide on your nick name. This doesnt have to be unique. Just a short name that will replace your
       account id on the screens of this site.</li>
       <li>Add it to your html and mark it with the <tt>id</tt> named <tt>nickname</tt>. One way to do this is like this:
        <tt>&lt;div id="nickname">Bobbie&lt/div></tt> or in a headline <tt>&lt;h1 id="nickname">Bobbie&lt/h1></tt>.
    </li>
        <li>Save your html file</li>
        <li>Now Sign it using your NeuClear Signer which you can start by clicking <a href="http://dist.neuclear.org/app/neuclear-signer.jnlp">here</a>.
        From the file menu pick "Sign File...". This lets you pick a file. Sign it with your selected personality and save it.</li>
        <li>Upload the file to your webserver. eg. http://bobsmith.com/bob.html</li>
        <li>Enter the address of your uploaded file below</li>
        </ul>

</p>
<p>
<form action="register.jsp" method="POST">
    <h1>Register</h1>
    <p<%=(error)?" class=\"invalid\"":""%>>URL : (Web address of your Identity file)<br/>
    <input type="text" name="url" value="<%=url%>" size="40"/>
    <%=(submit&&Utility.isEmpty(url))?"Remember to enter your url":""%>
    <%=(invalid)?"The html page at <a href=\""+url+"\">"+url+"</a> is invalid. Did you remember to sign it?":""%>
    <%=(resolution)?"We couldn't load the page at <a href=\""+url+"\">"+url+"</a>":""%>
    </p>
     <p class="formaction">
         <input type="submit" name="submit" value="Verify"/>
         <input type="submit" name="cancel" value="Cancel" onClick="history.go(-1)"/>

     </p>

</form>
</p>
<% }
    %>
    </div>
</body>
</html>