# ASASprint

FrontController Annotation Scanner
Ce projet consiste en un FrontController pour une application web Java, conçu pour scanner et répertorier les contrôleurs annotés dans un package spécifié.

Comment ça marche ?
    1. Initialisation
Dans le fichier web.xml de votre application web, configurez le paramètre d'initialisation namePath pour indiquer le chemin du package où se trouvent vos contrôleurs. Par exemple :

    <context-param>
        <param-name>namePath</param-name>
        <param-value>prom16.annotation.controllers</param-value>
    </context-param>


    2. Annotation des contrôleurs
Annotez vos classes de contrôleurs avec l'annotation @AnnotationControlleur. Par exemple :

java
Copy code
package prom16.annotation.controllers;

import prom16.annotation.AnnotationControlleur;

    @AnnotationControlleur
    public class MyController {
        // Votre code de contrôleur ici
    }


    3. Lancement du serveur
Démarrez votre serveur web (par exemple, Apache Tomcat) pour déployer l'application.

    4. Accès au FrontController
Accédez au FrontController à l'URL de votre application, par exemple http://localhost:8080/mon-application/front.

    5. Liste des contrôleurs
Le FrontController va scanner le package spécifié pour trouver toutes les classes annotées avec @AnnotationControlleur et afficher la liste des contrôleurs disponibles.

Remarques
Assurez-vous que votre application est correctement configurée et déployée sur votre serveur web avant d'accéder au FrontController.
Veillez à annoter correctement vos classes de contrôleurs avec @AnnotationControlleur pour qu'elles soient détectées par le scanner.