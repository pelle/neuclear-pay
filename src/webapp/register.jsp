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
       In NeuClear your account id is the same across all Asset types. Therefore it makes more sense if the control
       of your account details are in your hands and not with some central authority. Your account id
       is a 32 character id, which frankly does look ugly. Therefore you have the option of creating an
       Account web page on the internet, where you can tell people about yourself. Once you have that page up on your web site,
       you can tell people to pay you at for example <tt>http://yoursite.com/myaccountpage.html</tt>. Your friends,
       family or business partners can also add that page as a contact from within the NeuClear Personal Trader.
       </p><p>
        Use this screen to register your or some one elses NeuClear Account Page with us. This is absolutely optional on the Beta Bux site.
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
       <ol>
       <li>Create a simple html page writing as much or as little about your self as you want.
        If you wish you may <a href="sampleaccountpage.html">download this sample page to use as a template</a></li>
       <li>Decide on your nick name. This doesnt have to be unique. Just a short name that will replace your
       account id on the screens of this site.</li>
       <li>Add it to your html and mark it with the <tt>id</tt> named <tt>nickname</tt>. One way to do this is like this:
        <tt><span style="color:blue">&lt;div id="nickname">Bobbie&lt/div></span></tt> or in a headline <tt><span style="color:blue">&lt;h1 id="nickname">Bobbie&lt/h1></span></tt>.
    </li>
        <li>Decide on where you want to put it on your website. Say for example <tt>http://yoursite.com/myname.html</tt></li>
        <li>Put that url in a link tag in your html header: eg. <tt><span style="color:grey">&lt;/title><span style="color:blue">&lt;link href="http://talk.org/pelletest.html" rel="original"/></span>&lt;/head>&lt;body></span></tt></li>
        <li>Save your html file</li>
        <li>Now Sign it using your NeuClear Personal Trader which you can start by clicking <a href="http://dist.neuclear.org/app/neuclear-signer.jnlp">here</a>.
        From the file menu pick "Sign File...". This lets you pick a file. Sign it with your selected account and save it.</li>
        <li>Upload the file to your webserver. eg. http://bobsmith.com/bob.html</li>
        <li>Enter the address of your uploaded file below</li>
        </ol>

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