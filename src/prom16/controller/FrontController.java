package prom16.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import prom16.annotation.AnnoterAttribut;
import prom16.annotation.Annoter;
import prom16.annotation.AnnoterObject;
import prom16.annotation.Auth;
import prom16.annotation.Param;
import prom16.annotation.Post;
import prom16.annotation.Roles;
import prom16.annotation.Url;
import prom16.fonction.CustomSession;
import prom16.fonction.Mapping;
import prom16.fonction.ModelView;
import prom16.fonction.Reflect;
import prom16.fonction.VerbAction;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@MultipartConfig
public class FrontController extends HttpServlet {
    private String controllerPackage;
    private Gson gson = new Gson();
    private HashMap<String, Mapping> liste = new HashMap<String, Mapping>();
    private Exception errorPackage = new Exception("null");
    private Exception errorLien = new Exception("null");
    private String statusCode = "200";
    private String authUser;
    private String userRoles;

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }

    public String getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(String userRoles) {
        this.userRoles = userRoles;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public Exception getErrorLien() {
        return errorLien;
    }

    public void setErrorLien(Exception errorLien) {
        this.errorLien = errorLien;
    }

    public Exception getErrorPackage() {
        return errorPackage;
    }

    public void setErrorPackage(Exception errorPackage) {
        this.errorPackage = errorPackage;
    }

    public HashMap<String, Mapping> getListe() {
        return liste;
    }

    public void setListe(HashMap<String, Mapping> liste) {
        this.liste = liste;
    }

    @Override
    public void init() throws ServletException {
        this.setControllerPackage(getServletConfig().getInitParameter("namePath"));
        this.setAuthUser(getServletContext().getInitParameter("authentification"));
        this.setUserRoles(getServletContext().getInitParameter("roles"));
        this.scan(getServletContext());
        super.init();
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public void scan(ServletContext context) {
        HashMap<String, Mapping> boite = new HashMap<>();
        String packageName = this.getControllerPackage();
        try {
            String classesPath = context.getRealPath("/WEB-INF/classes");
            String decodedPath = URLDecoder.decode(classesPath, "UTF-8");
            String packagePath = decodedPath + "/" + packageName.replace('.', '/');
            File packageDirectory = new File(packagePath);
            if (packageDirectory.exists() && packageDirectory.isDirectory()) {
                File[] classFiles = packageDirectory.listFiles((dir, name) -> name.endsWith(".class"));
                if (classFiles != null) {
                    for (File file : classFiles) {
                        String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                        Class<?> clazz = Class.forName(className);
                        if (isController(clazz)) {
                            Method[] listeMethod = clazz.getDeclaredMethods();
                            for (Method method : listeMethod) {
                                Mapping map = new Mapping();
                                map.setClassName(className);
                                String verbe = "GET";
                                if (method.isAnnotationPresent(Url.class)) {
                                    String key = method.getAnnotation(Url.class).value();
                                    if (method.isAnnotationPresent(Post.class)) {
                                        verbe = "POST";
                                    }
                                    VerbAction verbeAction = new VerbAction(method.getName(), verbe);
                                    if (boite.containsKey(key)) {
                                        Mapping keyExist = boite.get(key);
                                        if (keyExist.contains(verbeAction)) {
                                            this.setStatusCode("409");
                                            throw new Exception("Erreur : Deux URL qui sont pareil sur cette lien "+ key + " avec le meme verbe " + verbe);
                                        }
                                        keyExist.addVerbAction(verbeAction);
                                    } else {
                                        map.addVerbAction(verbeAction);
                                        boite.put(key, map);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                this.setStatusCode("500");
                this.setErrorPackage(new Exception("Erreur Package non existant " + packageName));
            }
        } catch (Exception e) {
            this.setErrorLien(e);
        }
        this.setListe(boite);
    }

    private static boolean isController(Class<?> clazz) {
        return clazz.isAnnotationPresent(Annoter.class);
    }

    private void traitementAuth(Method meth , Class<?> clazz,HttpServletRequest req) throws Exception{
        CustomSession session = new CustomSession();
        session.setSession(req.getSession());
        if (meth != null && clazz == null) {
            if (meth.isAnnotationPresent(Auth.class)) {
                if ((Boolean)session.getSession().getAttribute(getAuthUser()) != true) {
                    this.setStatusCode("400");
                    throw new Exception("Vous ne pouvez pas accéder à cette méthode car vous devez être authentifié"); 
                }
            }else if (meth.isAnnotationPresent(Roles.class)) {
                String[] lesRoles =  meth.getAnnotation(Roles.class).value();
                boolean exists = true;
                String mess = String.join(" ou ", lesRoles);
                for (String ext : lesRoles) {
                    if (ext.equals((String)session.getSession().getAttribute(getUserRoles()))) {
                        exists = false;                
                    }
                }
                if (exists) {
                    this.setStatusCode("400");
                    throw new Exception("Vous ne pouvez pas accéder à cette méthode car vous devez être une "+mess);                    
                }
            }else{
                //Pour les methodes publics et qui n'ont pas d'annotations
            }
        }else if(meth == null && clazz != null){
            if (clazz.isAnnotationPresent(Auth.class)) {
                if ((Boolean)session.getSession().getAttribute(authUser) != true) {
                    this.setStatusCode("400");
                    throw new Exception("Vous ne pouvez pas accéder à cette prom16 car vous devez être authentifié"); 
                }
            }else if (clazz.isAnnotationPresent(Roles.class)) {
                String[] lesRoles =  clazz.getAnnotation(Roles.class).value();
                boolean exists = true;
                String mess = String.join(" ou ", lesRoles);
                for (String ext : lesRoles) {
                    if (ext.equals((String)session.getSession().getAttribute(getUserRoles()))) {
                        exists = false;                
                    }
                }
                if (exists) {
                    this.setStatusCode("400");
                    throw new Exception("Vous ne pouvez pas accéder à cette prom16 car vous devez être une "+mess);
                }
            }else{
                //Pour les prom16 publics et qui n'ont pas d'annotations
            }
        }
    }

    private String traitement(String description, HttpServletRequest req, HttpServletResponse res) throws Exception {
        String url = req.getRequestURI();
        String nameProjet = req.getContextPath();
        int test = 0;
        for (Map.Entry<String, Mapping> entry : this.getListe().entrySet()) {
            String key = nameProjet + entry.getKey();
            Mapping value = entry.getValue();
            String verbe = req.getMethod();
            int verifErreur = 0;
            HashMap<String, List<String>> mapErreur = new HashMap<String, List<String>>();
            if (key.equals(url)) {
                int idVerbeMethode = 0;
                for (int i = 0; i < value.getVerbeAction().size(); i++) {
                    if (value.getVerbeAction().get(i).getVerb().equals(verbe)) {
                        idVerbeMethode = i;
                    }
                }
                
                test++;

                try {
                    if (!verbe.equals(value.getVerbeAction().get(idVerbeMethode).getVerb())) {
                        this.setStatusCode("405");
                        throw new Exception("La methode " + value.getVerbeAction().get(idVerbeMethode).getMethodName()
                                + " est invoquee en " + value.getVerbeAction().get(idVerbeMethode).getVerb()
                                + " alors que ton formulaire opte pour du " + verbe
                                + " . Un petit ajustement s'impose");
                    }

                    Class<?> obj = Class.forName(value.getClassName());
                    traitementAuth(null,obj,req);
                    Object objInstance = obj.getDeclaredConstructor().newInstance();
                    Field[] fields = obj.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getType().equals(CustomSession.class)) {
                            CustomSession customSession = new CustomSession();
                            customSession.setSession(req.getSession());
                            field.setAccessible(true);
                            field.set(objInstance, customSession);
                        }
                    }

                    Method meth = Reflect.getMethode(objInstance, value.getVerbeAction().get(idVerbeMethode).getMethodName());
                    traitementAuth(meth,null,req);

                    if (Reflect.findParam(objInstance, value.getVerbeAction().get(idVerbeMethode).getMethodName())) {
                        Parameter[] objParametre = Reflect.getParam(objInstance,value.getVerbeAction().get(idVerbeMethode).getMethodName());
                        Object[] objValeur = new Object[objParametre.length];
                        Enumeration<String> reqParametre = req.getParameterNames();
                        Enumeration<String> reqParametre2 = req.getParameterNames();
                        List<String> reqParametreString = new ArrayList<String>();
                        while (reqParametre.hasMoreElements()) {
                            String paramName = reqParametre.nextElement();
                            reqParametreString.add(paramName);
                        }

                        boolean isSession = false;
                        int idParamSession = 0;
                        for (int i = 0; i < objParametre.length; i++) {
                            Class<?> objTemp = Reflect.getClassForName(objParametre[i].getParameterizedType().getTypeName());
                            Object objTempInstance = null;
                            
                            if (objParametre[i].getType() == Part.class) {
                                if (objParametre[i].isAnnotationPresent(Param.class)) {
                                    String paramPart = objParametre[i].getAnnotation(Param.class).value();
                                    Part file = req.getPart(paramPart);
                                    objValeur[i] = (Part) file;                              
                                } else {
                                    this.setStatusCode("400");
                                    throw new Exception("ETU002401 il n'y a pas de parametre sur cette methode");
                                }
                            } else {
                                if (!objTemp.isPrimitive()) {
                                    objTempInstance = objTemp.getDeclaredConstructor().newInstance();
                                }
                                if (!objTemp.isPrimitive() && objTempInstance.getClass().isAnnotationPresent(AnnoterObject.class)) {
                                    if (objParametre[i].isAnnotationPresent(Param.class)) {
                                        Field[] lesAttributs = objTempInstance.getClass().getDeclaredFields();
                                        Object[] attributsValeur = new Object[lesAttributs.length];

                                        for (int j = 0; j < lesAttributs.length; j++) {
                                            int verif = 0;

                                            List<String> valeurErreur = new ArrayList<>();
                                            String clesErreur = "error_"+objParametre[i].getAnnotation(Param.class).value() + ".";
                                            if (lesAttributs[j].isAnnotationPresent(AnnoterAttribut.class)) {
                                                clesErreur += lesAttributs[j].getAnnotation(AnnoterAttribut.class).value();
                                            }else {
                                                clesErreur += lesAttributs[j].getName();
                                            }

                                            for (int k = 0; k < reqParametreString.size(); k++) {
                                                String paramName = reqParametreString.get(k);
                                                if (paramName.startsWith(objParametre[i].getAnnotation(Param.class).value() + ".")) {
                                                    String lastPart = "";
                                                    int lastIndex = paramName.lastIndexOf(".");
                                                    if (lastIndex != -1 && lastIndex != paramName.length() - 1) {
                                                        lastPart = paramName.substring(lastIndex + 1);
                                                    }

                                                    if (lesAttributs[j].getName().compareTo(lastPart) == 0) {
                                                        if (!Reflect.validation(lesAttributs[j], req.getParameter(paramName))) {
                                                            valeurErreur = Reflect.erreurValidation(lesAttributs[j], req.getParameter(paramName));
                                                            verifErreur ++;
                                                        }else{
                                                            attributsValeur[j] = Reflect.castParameter(req.getParameter(paramName),lesAttributs[j].getType().getName());
                                                            verif++;
                                                            break;
                                                        }
                                                    }
                                                    if (lesAttributs[j].isAnnotationPresent(AnnoterAttribut.class)) {
                                                        if (lesAttributs[j].getAnnotation(AnnoterAttribut.class).value().compareTo(lastPart) == 0) {
                                                            if (!Reflect.validation(lesAttributs[j], req.getParameter(paramName))) {
                                                                valeurErreur = Reflect.erreurValidation(lesAttributs[j], req.getParameter(paramName));
                                                                verifErreur ++;
                                                            }else{
                                                                attributsValeur[j] = Reflect.castParameter(req.getParameter(paramName),lesAttributs[j].getType().getName());
                                                                verif++;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                
                                            }
                                            
                                            if (verif == 0) {
                                                attributsValeur[j] = Reflect.castParameter(null,lesAttributs[j].getType().getName());
                                            }

                                            mapErreur.put(clesErreur, valeurErreur);
                                        }

                                        objTempInstance = Reflect.process(objTempInstance, attributsValeur);
                                        objValeur[i] = objTempInstance;
                                    } else {
                                        this.setStatusCode("400");
                                        throw new Exception("ETU002401 il n'y a pas de parametre sur cette methode");
                                    }
                                } else if (objTempInstance.getClass().getTypeName().compareTo("prom16.source.CustomSession") == 0) {
                                    isSession = true;
                                    idParamSession = i;
                                    objValeur[i] = Reflect.castParameter(null,
                                            objParametre[i].getParameterizedType().getTypeName());
                                } else {
                                    int verif = 0;
                                    while (reqParametre2.hasMoreElements()) {
                                        String paramName = reqParametre2.nextElement();
                                        if (objParametre[i].isAnnotationPresent(Param.class)) {
                                            if (objParametre[i].getAnnotation(Param.class).value().compareTo(paramName) == 0) {
                                                objValeur[i] = Reflect.castParameter(req.getParameter(paramName),objParametre[i].getParameterizedType().getTypeName());
                                                verif++;
                                                break;
                                            }
                                        } else {
                                            this.setStatusCode("400");
                                            throw new Exception("ETU002401 il n'y a pas de parametre sur cette methode");
                                        }
                                    }
                                    if (verif == 0) {
                                        objValeur[i] = Reflect.castParameter(null,objParametre[i].getParameterizedType().getTypeName());
                                    }
                                }
                            }
                        }

                        if (isSession) {
                            Class<?> objTemp = Reflect.getClassForName(objParametre[idParamSession].getParameterizedType().getTypeName());
                            Object objTempInstance = objTemp.getDeclaredConstructor().newInstance();
                            CustomSession session = (CustomSession) objTempInstance;
                            session.setSession(req.getSession());
                            objValeur[idParamSession] = session;
                        }
                        
                        String reponse = Reflect.execMethodeController(objInstance,value.getVerbeAction().get(idVerbeMethode).getMethodName(), objValeur); 
                        
                        if (Reflect.isRest(objInstance,value.getVerbeAction().get(idVerbeMethode).getMethodName())) {
                            if (reponse.compareTo("prom16.fonction.ModelView") == 0) {
                                ModelView mv = (ModelView) Reflect.execMethode(objInstance,value.getVerbeAction().get(idVerbeMethode).getMethodName(), objValeur);
                                String jsonResponse = gson.toJson(mv.getData());
                                req.setAttribute("baseUrl", nameProjet);
                                description = jsonResponse;
                            } else {
                                String jsonResponse = gson.toJson(reponse);
                                description = jsonResponse;
                            }
                            res.setContentType("text/json");
                        } else {
                            if (reponse.compareTo("prom16.fonction.ModelView") == 0) {
                                ModelView mv = (ModelView) Reflect.execMethode(objInstance,value.getVerbeAction().get(idVerbeMethode).getMethodName(), objValeur);
                                String cleHash = "";
                                String referer = "";
                                Object valueHash = new Object();
                                for (String cles : mv.getData().keySet()) {
                                    cleHash = cles;
                                    valueHash = mv.getData().get(cles);
                                    if (cleHash.equals("referer")) {
                                        referer = (String)mv.getData().get(cleHash);
                                    }else{
                                        req.setAttribute(cleHash, valueHash);
                                    }
                                }
                                req.setAttribute("baseUrl", nameProjet);
                                if (verifErreur != 0) {
                                    for (int j = 0; j < reqParametreString.size(); j++) {     
                                        String paramName = reqParametreString.get(j);
                                        req.setAttribute(paramName,req.getParameter(paramName));
                                    }

                                    if (referer != null && !referer.isEmpty()) {
                                        req.setAttribute("baseUrl", nameProjet);

                                        for (Map.Entry<String, List<String>> entree : mapErreur.entrySet()) {
                                            String cle = entree.getKey();
                                            List<String> valeur = entree.getValue();
                                            req.setAttribute(cle, valeur);
                                        }

                                        req.getServletContext().getRequestDispatcher(referer).forward(req, res);
                                    } else {
                                        this.setStatusCode("405");
                                        throw new Exception("Erreur : Aucun referer trouvé dans la requête.");
                                    }
                                }else{
                                    req.getServletContext().getRequestDispatcher(mv.getUrl()).forward(req, res);
                                }
                            } else {
                                description += reponse;
                            }
                        }
                    } else {
                        String reponse = Reflect.execMethodeController(objInstance,value.getVerbeAction().get(idVerbeMethode).getMethodName(), null);
                        if (Reflect.isRest(objInstance,value.getVerbeAction().get(idVerbeMethode).getMethodName())) {
                            if (reponse.compareTo("prom16.fonction.ModelView") == 0) {
                                ModelView mv = (ModelView) Reflect.execMethode(objInstance,value.getVerbeAction().get(idVerbeMethode).getMethodName(), null);
                                String jsonResponse = gson.toJson(mv.getData());
                                req.setAttribute("baseUrl", nameProjet);
                                description = jsonResponse;
                            } else {
                                String jsonResponse = gson.toJson(reponse);
                                description = jsonResponse;
                            }
                            res.setContentType("text/json");
                        } else {
                            if (reponse.compareTo("prom16.fonction.ModelView") == 0) {
                                ModelView mv = (ModelView) Reflect.execMethode(objInstance,value.getVerbeAction().get(idVerbeMethode).getMethodName(), null);
                                String cleHash = "";
                                Object valueHash = new Object();
                                for (String cles : mv.getData().keySet()) {
                                    cleHash = cles;
                                    valueHash = mv.getData().get(cles);
                                    req.setAttribute(cleHash, valueHash);
                                }
                                req.setAttribute("baseUrl", nameProjet);
                                req.getServletContext().getRequestDispatcher(mv.getUrl()).forward(req, res);
                            } else {
                                description += reponse;
                            }
                        }
                    }
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        if (test == 0) {
            this.setStatusCode("404");
            throw new Exception("Lien inexistante : Il n'y a pas de methodes associer a cette chemin " + req.getRequestURL());
        }
        return description;
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String valiny = "";

        if (this.getErrorPackage().getMessage().compareTo("null") == 0
                && this.getErrorLien().getMessage().compareTo("null") == 0) {
            try {
                valiny = traitement(valiny, req, res);
            } catch (Exception e) {
                valiny = e.getMessage();
                res.sendError(Integer.parseInt(this.getStatusCode()), valiny);
            }
        } else {
            if (this.getErrorPackage().getMessage().compareTo("null") == 0) {
                valiny = this.getErrorLien().getMessage();
                res.sendError(Integer.parseInt(this.getStatusCode()), valiny);
            } else {
                valiny = this.getErrorPackage().getMessage();
                res.sendError(Integer.parseInt(this.getStatusCode()), valiny);
            }
        }

        out.println(valiny);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

}
