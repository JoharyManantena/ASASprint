<%
    String baseUrl = (String) request.getAttribute("baseUrl");
    if (baseUrl == null) {
        baseUrl = request.getContextPath();
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <title>Formulaire</title>
</head>

<body class="container mt-5">
    <div class="card mb-4">
        <div class="card-header">
            <h2>Formulaire d'insertion d'employer (Type de retour String)</h2>
        </div>
        <div class="card-body">
            <form action="<%=baseUrl %>/insertion" method="post">
                <div class="form-group">
                    <label for="nom">Nom :</label>
                    <input type="text" class="form-control" name="nom" id="nom">
                </div>
                <div class="form-group">
                    <label for="mail">Email :</label>
                    <input type="email" class="form-control" name="mail" id="mail">
                </div>
                <div class="form-group">
                    <label for="age">Age :</label>
                    <input type="number" class="form-control" name="age" id="age">
                </div>
                <button type="submit" class="btn btn-primary">Valider</button>
            </form>
        </div>
    </div>

    <div class="card mb-4">
        <div class="card-header">
            <h2>Formulaire d'insertion d'employer par Object (Avec Type de retour String)</h2>
        </div>
        <div class="card-body">
            <form action="<%=baseUrl %>/insertionObjet" method="post">
                <div class="form-group">
                    <label for="emp.nom">Nom :</label>
                    <input type="text" class="form-control" name="emp.nom">
                </div>
                <div class="form-group">
                    <label for="emp.email">Email :</label>
                    <input type="email" class="form-control" name="emp.email">
                </div>
                <div class="form-group">
                    <label for="emp.argent">Argent :</label>
                    <input type="text" class="form-control" name="emp.argent">
                </div>
                <div class="form-group">
                    <label for="poste">Poste :</label>
                    <input type="text" class="form-control" name="poste">
                </div>
                <div class="form-group">
                    <label for="num">Numero telephone :</label>
                    <input type="text" class="form-control" name="num">
                </div>
                <button type="submit" class="btn btn-primary">Valider</button>
            </form>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>
