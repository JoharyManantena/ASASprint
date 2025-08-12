<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Upload de fichier</title>
</head>
<body>
    <h2>Formulaire d'upload</h2>

    <%-- Affichage du message --%>
    <c:if test="${not empty message}">
        <p>${message}</p>
    </c:if>

    <form action="uploadFile" method="post" enctype="multipart/form-data">
        <label for="nom">Nom:</label><br/>
        <input type="text" id="nom" name="nom" required><br/>

        <label for="prenom">Prénom:</label><br/>
        <input type="text" id="prenom" name="prenom" required><br/>

        <label for="image">Image:</label><br/>
        <input type="file" id="image" name="image" accept="image/*" required><br/>

        <label for="fichier">Fichier:</label><br/>
        <input type="file" id="fichier" name="fichier" required><br/><br/>

        <button type="submit">Envoyer</button>
    </form>
</body>
</html>
