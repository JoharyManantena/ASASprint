package prom16.controller;

import java.io.*;
import java.lang.reflect.*;

import java.net.URLDecoder;
import java.util.*;

import prom16.annotation.*;
import prom16.fonction.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class FrontController extends HttpServlet {
    private String controllerPackage;
    private HashMap<String , Mapping> liste = new HashMap<String , Mapping>();
    private Exception errorPackage = new Exception("null");
    private Exception errorLien = new Exception("null");

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

    public HashMap<String , Mapping> getListe() {
        return liste;
    }

    public void setListe(HashMap<String , Mapping> liste) {
        this.liste = liste;
    }

    @Override
    public void init() throws ServletException {
        this.setControllerPackage(getServletConfig().getInitParameter("namePath"));
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
        HashMap<String , Mapping> boite = new HashMap<>();
        String packageName = this.getControllerPackage();
        try {
            String classesPath = context.getRealPath("/WEB-INF/classes");
            String decodedPath = URLDecoder.decode(classesPath, "UTF-8");
            String packagePath = decodedPath+"/"+packageName.replace('.', '/');
            File packageDirectory = new File(packagePath);
            if (packageDirectory.exists() && packageDirectory.isDirectory()) {
                File[] classFiles = packageDirectory.listFiles((dir, name) -> name.endsWith(".class"));
                if (classFiles != null) {
                    for (File file : classFiles) {
                        String className = packageName+"."+file.getName().substring(0, file.getName().length()-6);
                        Class<?> clazz = Class.forName(className);
                        if (isController(clazz)) {
                            Method[] listeMethod = clazz.getDeclaredMethods();
                            for (Method method : listeMethod) {
                                if (method.isAnnotationPresent(Get.class)) {
                                    String key = method.getAnnotation(Get.class).value();
                                    if (boite.containsKey(key)) {
                                        throw new Exception("Erreur : Deux URL qui sont pareil sur cette lien " + key);
                                    }
                                    Mapping map = new Mapping(className, method.getName());
                                    boite.put(key, map);
                                }
                            }
                        }
                    }
                }
            } 
            else{
                this.setErrorPackage(new Exception("Erreur Package non existant "+ packageName));
            }
        } catch (Exception e) {
            this.setErrorLien(e);
        }
        this.setListe(boite);
    }

    private static boolean isController(Class<?> clazz) {
        return clazz.isAnnotationPresent(Annoter.class);
    }

    private String traitement(String description,HttpServletRequest req , HttpServletResponse res)throws Exception{
        String url = req.getRequestURI();
        String nameProjet = req.getContextPath();
        int test = 0;
        for (Map.Entry<String, Mapping> entry : this.getListe().entrySet()) {
            String key = nameProjet + entry.getKey();
            Mapping value = entry.getValue();
            if (key.equals(url)) {
                test++;
                try {
                    Class<?> obj = Class.forName(value.getClassName());
                    Object objInstance = obj.getDeclaredConstructor().newInstance(); 
                    if (Reflect.findParam(objInstance, value.getMethodName())) {
                        Parameter[] objParametre = Reflect.getParam(objInstance, value.getMethodName());
                        // String[] objParametreName = Reflect.parameterNames(objInstance, value.getMethodName());
                        Object[] objValeur = new Object[objParametre.length];
                        Enumeration<String> reqParametre = req.getParameterNames();
                        Enumeration<String> reqParametre2 = req.getParameterNames();
                        for (int i = 0; i < objParametre.length; i++) {
                            Class<?> objTemp = Reflect.getClassForName(objParametre[i].getParameterizedType().getTypeName());
                            Object objTempInstance = null;
                            if (!objTemp.isPrimitive()) {
                                objTempInstance = objTemp.getDeclaredConstructor().newInstance();
                            }

                            if (!objTemp.isPrimitive() && objTempInstance.getClass().isAnnotationPresent(AnnoterObject.class)) {
                                Field[] lesAttributs = objTempInstance.getClass().getDeclaredFields();
                                Object[] attributsValeur = new Object[lesAttributs.length];
                                for (int j = 0; j < lesAttributs.length; j++) {
                                    int verif = 0;
                                    while (reqParametre.hasMoreElements()) {
                                        String paramName = reqParametre.nextElement();
                                        if (paramName.startsWith(objParametre[i].getName() + ".")) {
                                            // Obtenir la partie après le dernier "."
                                            String lastPart = "";
                                            int lastIndex = paramName.lastIndexOf(".");
                                            if (lastIndex != -1 && lastIndex != paramName.length() - 1) {
                                                lastPart = paramName.substring(lastIndex + 1);
                                            }

                                            if (lesAttributs[j].getName().compareTo(lastPart)==0) {
                                                attributsValeur[j] = Reflect.castParameter(req.getParameter(paramName), lesAttributs[j].getType().getName());
                                                verif++;
                                                break;
                                            }
                                            if (lesAttributs[j].isAnnotationPresent(AnnoterAttribut.class)) {
                                                if (lesAttributs[j].getAnnotation(AnnoterAttribut.class).value().compareTo(lastPart)==0) {
                                                    attributsValeur[j] = Reflect.castParameter(req.getParameter(paramName), lesAttributs[j].getType().getName());
                                                    verif++;
                                                    break;
                                                }
                                            } 
                                        }
                                    }
                                    if (verif == 0) {
                                        attributsValeur[j] = Reflect.castParameter(null, lesAttributs[j].getType().getName());
                                    }
                                }
                                objTempInstance = Reflect.process(objTempInstance, attributsValeur);
                                objValeur[i] = objTempInstance;
                            }else{
                                int verif = 0;
                                while (reqParametre2.hasMoreElements()) {
                                    String paramName = reqParametre2.nextElement();
                                    if (objParametre[i].getName().compareTo(paramName)==0) {
                                        objValeur[i] = Reflect.castParameter(req.getParameter(paramName), objParametre[i].getParameterizedType().getTypeName());
                                        verif++;
                                        break;
                                    }
                                    if (objParametre[i].isAnnotationPresent(Param.class)) {
                                        if (objParametre[i].getAnnotation(Param.class).value().compareTo(paramName)==0) {
                                            objValeur[i] = Reflect.castParameter(req.getParameter(paramName), objParametre[i].getParameterizedType().getTypeName());
                                            verif++;
                                            break;
                                        }
                                    }
                                }
                                if (verif == 0) {
                                    objValeur[i] = Reflect.castParameter(null, objParametre[i].getParameterizedType().getTypeName());
                                }
                            }
                        }

                        String reponse = Reflect.execMethodeController(objInstance, value.getMethodName(), objValeur);
                        if (reponse.compareTo("prom16.fonction.ModelView")==0) {
                            ModelView mv = (ModelView)Reflect.execMethode(objInstance, value.getMethodName(), objValeur);
                            String cleHash ="";
                            Object valueHash = new Object();
                            for (String cles : mv.getData().keySet()) {
                                cleHash = cles;
                                valueHash = mv.getData().get(cles);
                                req.setAttribute(cleHash, valueHash);
                            }
                            req.setAttribute("baseUrl", nameProjet);
                            req.getServletContext().getRequestDispatcher(mv.getUrl()).forward(req, res);
                        }else{
                            description += reponse;
                        }
                    }else{
                        String reponse = Reflect.execMethodeController(objInstance, value.getMethodName(), null);
                        if (reponse.compareTo("prom16.fonction.ModelView")==0) {
                            ModelView mv = (ModelView)Reflect.execMethode(objInstance, value.getMethodName(), null);
                            String cleHash ="";
                            Object valueHash = new Object();
                            for (String cles : mv.getData().keySet()) {
                                cleHash = cles;
                                valueHash = mv.getData().get(cles);
                                req.setAttribute(cleHash, valueHash);
                            }
                            req.setAttribute("baseUrl", nameProjet);
                            req.getServletContext().getRequestDispatcher(mv.getUrl()).forward(req, res);
                        }else{
                            description += reponse;
                        }
                    }
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }       
            }
        }
        if (test == 0) {
            throw new Exception("Lien inexistante : Il n'y a pas de methodes associer a cette chemin " + req.getRequestURL());
        }
        return description;
    }

    protected void processRequest(HttpServletRequest req , HttpServletResponse res) throws ServletException , IOException {
        PrintWriter out = res.getWriter();
        String valiny ="";
        if (this.getErrorPackage().getMessage().compareTo("null")==0 && this.getErrorLien().getMessage().compareTo("null")==0) {
            try {
                valiny = traitement(valiny, req, res);
            } catch (Exception e) {
                valiny = e.getMessage();
            }
        }
        else{
            if (this.getErrorPackage().getMessage().compareTo("null")==0) {
                valiny = this.getErrorLien().getMessage();
            }
            else{
                valiny = this.getErrorPackage().getMessage();
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
