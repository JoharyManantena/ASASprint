# ASASprint
FrontController Annotation Scanner
Ce projet consiste en un FrontController pour une application web Java, conçu pour scanner et répertorier les contrôleurs annotés dans un package spécifié. Le FrontController gère les requêtes HTTP en utilisant des annotations pour mapper les URL aux méthodes de contrôleur appropriées.

#Comment ça marche ?
1. Initialisation
Dans le fichier web.xml de votre application web, configurez le paramètre d'initialisation namePath pour indiquer le chemin du package où se trouvent vos contrôleurs. Par exemple :

xml

<context-param>
    <param-name>namePath</param-name>
    <param-value>prom16.annotation.controllers</param-value>
</context-param>
2. Annotation des contrôleurs
Annotez vos classes de contrôleurs avec l'annotation @Annoter et les méthodes de contrôleurs avec l'annotation @GET. Par exemple :

java

package prom16.annotation.controllers;

import prom16.annotation.Annoter;
import prom16.annotation.GET;

@Annoter
public class MyController {

    @GET("/hello")
    public void sayHello() {
        
    }
}
3. Lancement du serveur
Démarrez votre serveur web (par exemple, Apache Tomcat) pour déployer l'application.

4. Accès au FrontController
Accédez au FrontController à l'URL de votre application, par exemple http://localhost:8080/mon-application/front.

5. Liste des contrôleurs
Le FrontController va scanner le package spécifié pour trouver toutes les classes annotées avec @Annoter et les méthodes annotées avec @GET, puis afficher la liste des contrôleurs disponibles et leurs méthodes.

Exemple de Fichier web.xml
Voici un exemple de fichier web.xml configurant le FrontController :

xml

<web-app>
    <servlet>
        <servlet-name>FrontController</servlet-name>
        <servlet-class>prom16.controller.FrontController</servlet-class>
        <init-param>
            <param-name>namePath</param-name>
            <param-value>prom16.annotation.controllers</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>FrontController</servlet-name>
        <url-pattern>/front/*</url-pattern>
    </servlet-mapping>
</web-app>

Remarques
Assurez-vous que votre application est correctement configurée et déployée sur votre serveur web avant d'accéder au FrontController.
Veillez à annoter correctement vos classes de contrôleurs avec @Annoter et leurs méthodes avec @GET pour qu'elles soient détectées par le scanner.



Structure des Fichiers


src/
├── prom16/
│   ├── annotation/
│   │   ├── Annoter.java
│   │   ├── GET.java
│   │   
│   └── controller/
│       └── FrontController.java
|       ├── Mapping.java
|
└── compile.bat
└── web.xml 