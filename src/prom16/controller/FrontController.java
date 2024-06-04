package prom16.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import prom16.annotation.Annoter;
import prom16.annotation.Get;
import prom16.fonction.ModelView;
import prom16.fonction.Reflect;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private String controllerPackage;
    private HashMap<String , Mapping> liste = new HashMap<String , Mapping>();

    public HashMap<String , Mapping> getListe() {
        return liste;
    }

    public void setListe(HashMap<String , Mapping> liste) {
        this.liste = liste;
    }

    @Override
    public void init() throws ServletException {
        this.setControllerPackage(getServletConfig().getInitParameter("namePath"));
        this.setListe(scan(getServletContext(), this.getControllerPackage(), this.getListe()));
        super.init();
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public static HashMap<String , Mapping> scan(ServletContext context , String packageName , HashMap<String , Mapping> boite) {
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
                            for (int i = 0; i < listeMethod.length; i++) {
                                if (listeMethod[i].isAnnotationPresent(Get.class)) {
                                    Mapping map = new Mapping(className, listeMethod[i].getName());
                                    boite.put(listeMethod[i].getAnnotation(Get.class).value(), map);
                                }
                            }
                        }
                    }
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boite;
    }

    private static boolean isController(Class<?> clazz) {
        return clazz.isAnnotationPresent(Annoter.class);
    }

    protected void processRequest(HttpServletRequest req , HttpServletResponse res) throws ServletException , IOException {
        PrintWriter out = res.getWriter();
        String valiny ="";
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
                    String reponse = Reflect.execMethodeController(objInstance, value.getMethodName(), null);
                    if (reponse.compareTo("prom16.fonction.ModelView")==0) {
                        ModelView mv = (ModelView)Reflect.execMethode(objInstance, value.getMethodName(), null);
                        String cleHash ="";
                        Object valueHash = new Object();
                        for (String cles : mv.getData().keySet()) {
                            cleHash = cles;
                            valueHash = mv.getData().get(cles);
                            break;
                        }
                        req.setAttribute(cleHash, valueHash);
                        req.getServletContext().getRequestDispatcher(mv.getUrl()).forward(req, res);
                    }else{
                        valiny += reponse;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    String stackTrace = sw.toString();
                    valiny += "\n Exception: " + e.toString() + "\n" + stackTrace;
                }
            }
        }
        if (test == 0) {
            valiny += "Il n'y a pas de methodes associer a cette chemin " + req.getRequestURL();
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
