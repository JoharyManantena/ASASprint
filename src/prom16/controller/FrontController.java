package prom16.controller;

import java.io.*;
import java.lang.reflect.*;
import java.net.URLDecoder;
import java.util.*;

import prom16.annotation.*;
import prom16.fonction.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.google.gson.Gson;

public class FrontController extends HttpServlet {
    private String controllerPackage;
    private HashMap<String, Mapping> liste = new HashMap<>();
    private Exception errorPackage = null;
    private Exception errorLien = null;
    private Gson gson = new Gson(); // Initialize Gson

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
                            Method[] methods = clazz.getDeclaredMethods();
                            for (Method method : methods) {
                                if (method.isAnnotationPresent(Get.class)) {
                                    String key = method.getAnnotation(Get.class).value();
                                    if (boite.containsKey(key)) {
                                        throw new Exception("Erreur : Deux URL sont identiques pour le lien " + key);
                                    }
                                    Mapping map = new Mapping(className, method.getName());
                                    boite.put(key, map);
                                }
                            }
                        }
                    }
                }
            } else {
                this.setErrorPackage(new Exception("Erreur : Package non existant " + packageName));
            }
        } catch (Exception e) {
            this.setErrorLien(e);
        }
        this.setListe(boite);
    }

    private static boolean isController(Class<?> clazz) {
        return clazz.isAnnotationPresent(Annoter.class);
    }

    private String traitement(String description, HttpServletRequest req, HttpServletResponse res) throws Exception {
        String url = req.getRequestURI();
        String nameProjet = req.getContextPath();
        int test = 0;

        for (Map.Entry<String, Mapping> entry : this.getListe().entrySet()) {
            String key = nameProjet + entry.getKey();
            Mapping value = entry.getValue();
            if (key.equals(url)) {
                test++;
                try {
                    Class<?> clazz = Class.forName(value.getClassName());
                    Object objInstance = clazz.getDeclaredConstructor().newInstance();

                    if (Reflect.findParam(objInstance, value.getMethodName())) {
                        // Retrieve method parameters and match with request parameters
                        Parameter[] methodParams = Reflect.getParam(objInstance, value.getMethodName());
                        Object[] methodValues = new Object[methodParams.length];
                        Enumeration<String> reqParams = req.getParameterNames();
                        List<String> paramNames = Collections.list(reqParams);

                        for (int i = 0; i < methodParams.length; i++) {
                            String paramName = methodParams[i].getName();
                            if (paramNames.contains(paramName)) {
                                methodValues[i] = Reflect.castParameter(req.getParameter(paramName),
                                        methodParams[i].getParameterizedType().getTypeName());
                            } else {
                                throw new Exception("Erreur : Paramètre requis non trouve : " + paramName);
                            }
                        }

                        String response = Reflect.execMethodeController(objInstance, value.getMethodName(),
                                methodValues);
                        if (Reflect.isRestAPI(objInstance, value.getMethodName())){
                            ModelView mv = (ModelView) Reflect.execMethode(objInstance, value.getMethodName(), null);
                            String jsonResponse = gson.toJson(mv.getData());
                            req.setAttribute("baseUrl", nameProjet);
                            description = jsonResponse;
                            res.setContentType("application/json");
                        }
                        else{
                            if (response.equals("prom16.fonction.ModelView")) {
                                ModelView mv = (ModelView) Reflect.execMethode(objInstance, value.getMethodName(),
                                        methodValues);
                                // Handle ModelView response ...
                            } else {
                                description += response;
                            }
                        }
                            
                    } else {
                        String response = Reflect.execMethodeController(objInstance, value.getMethodName(), null);
                        // Handle method without @Param ...
                    }
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
            }
        }

        if (test == 0) {
            throw new Exception("Lien inexistant : Aucune methode associee à ce chemin " + req.getRequestURL());
        }
        return description;
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String response = "";

        try {
            if (errorPackage == null && errorLien == null) {
                response = traitement(response, req, res);
            } else {
                response = (errorPackage != null) ? errorPackage.getMessage() : errorLien.getMessage();
            }
        } catch (Exception e) {
            response = e.getMessage();
        }

        out.println(response);
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
