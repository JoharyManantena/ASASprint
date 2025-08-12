<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Formulaire d'Insertion</title>
</head>
<body>
    <h2>Formulaire d'Insertion</h2>
    <form action="insertion" method="post" enctype="multipart/form-data">
        <label for="nom">Nom:</label>
        <input type="text" id="nom" name="nom" required><br><br>

        <label for="prenom">Prénom:</label>
        <input type="text" id="prenom" name="prenom" required><br><br>

        <label for="image">Ajouter une image:</label>
        <input type="file" id="image" name="image" accept="image/*" required><br><br>

        <label for="fichier">Ajouter un fichier:</label>
        <input type="file" id="fichier" name="fichier" required><br><br>

        <input type="submit" value="Soumettre">
    </form>
</body>
</html>
