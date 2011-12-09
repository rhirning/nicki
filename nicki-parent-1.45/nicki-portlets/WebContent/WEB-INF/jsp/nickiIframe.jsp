<%@ page import="javax.portlet.RenderRequest" %>
<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page session="false" %>
<%
  RenderRequest pReq = (RenderRequest)request.getAttribute("javax.portlet.request");
  PortletPreferences pref = pReq.getPreferences();
  String frameborder = pref.getValue("frameborder", "1");
  String height = pref.getValue("height", "400");
  String scrolling = pref.getValue("scrolling", "yes");
  String url = (String) pReq.getAttribute("nickiUrl");
  String width = pref.getValue("width", "100%");
%>
<iframe src="<%=url%>" 
        frameborder="<%=frameborder%>" 
        height="<%=height%>"
        scrolling="<%=scrolling%>" 
        width="<%=width%>">
</iframe>

