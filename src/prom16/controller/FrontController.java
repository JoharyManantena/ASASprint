package prom16.annotation;

import java.io.*;
import java.net.*;
import java.util.*;

import prom16.annotation.Annoter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class FrontController extends HttpServlet {
    private boolean scanner = false;
    private List<String> nameControlleurs;
    private String controllerPackage;

    @Override
    public void init() throws ServletException {
        this.setControllerPackage(getServletConfig().getInitParameter("namePath"));
        super.init();
    }

    public static List<String> scanPackageForControllers(ServletContext context , String packageName) {
        List<String> controllerClassNames = new ArrayList<>();
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
                            controllerClassNames.add(className);
                        }
                    }
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return controllerClassNames;
    }

    private static boolean isController(Class<?> clazz) {
        return clazz.isAnnotationPresent(AnnotationControlleur.class);
    }


    protected void processRequest(HttpServletRequest req , HttpServletResponse res) throws ServletException , IOException {
        PrintWriter out = res.getWriter();
        String valiny = "Tous les listes des controlleurs dans votre projet sont:\n ";
        if (!isScanner()) {
            this.setNameControlleurs(scanPackageForControllers(getServletContext(),this.getControllerPackage()));
            for (int i = 0; i < getNameControlleurs().size(); i++) {
                valiny += "\t" + getNameControlleurs().get(i);
            }
            setScanner(true);
        }else{
            for (int i = 0; i < getNameControlleurs().size(); i++) {
                valiny += "\t" + getNameControlleurs().get(i);
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
