<%@ page import="java.util.*" %>
<%
    String perso = String.valueOf(request.getAttribute("anarana"));
    String mail = String.valueOf(request.getAttribute("mail"));
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bienvenue dans le testView</title>
</head>
<body>
    <p>Information d'insertion avec nom <%=perso %> et mail <%=mail %></p>
</body>
</html>