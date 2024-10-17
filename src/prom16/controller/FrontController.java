package prom16.controller;

import java.io.*;
import java.lang.reflect.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import prom16.annotation.*;
import prom16.fonction.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;


public class FrontController extends HttpServlet {
    private String controllerPackage;
    private Gson gson = new Gson();
    private Map<String, Mapping> mappings = new HashMap<>();
    private Exception packageError = new Exception("null");
    private Exception urlError = new Exception("null");

    @Override
    public void init() throws ServletException {
        controllerPackage = getServletConfig().getInitParameter("namePath");
        scanControllers(getServletContext());
        super.init();
    }

    public void scanControllers(ServletContext context) {
        String packageName = controllerPackage;
        try {
            String classesPath = context.getRealPath("/WEB-INF/classes");
            String decodedPath = URLDecoder.decode(classesPath, "UTF-8");
            String packagePath = decodedPath + "/" + packageName.replace('.', '/');
            File packageDirectory = new File(packagePath);

            if (packageDirectory.exists() && packageDirectory.isDirectory()) {
                for (File file : packageDirectory.listFiles((dir, name) -> name.endsWith(".class"))) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Annoter.class)) {
                        registerMethods(clazz);
                    }
                }
            } else {
                packageError = new Exception("Package not found: " + packageName);
            }
        } catch (Exception e) {
            urlError = e;
        }
    }

    private void registerMethods(Class<?> clazz) throws Exception {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Url.class)) {
                String url = method.getAnnotation(Url.class).value();
                if (mappings.containsKey(url)) {
                    throw new Exception("Duplicate URL: " + url);
                }
                Mapping map = new Mapping();
                map.setClassName(clazz.getName());
                map.setMethodName(method.getName());
                map.setVerb(method.isAnnotationPresent(Post.class) ? "POST" : "GET");
                mappings.put(url, map);
            }
        }
    }

    private String handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String requestUrl = req.getRequestURI().substring(req.getContextPath().length());
        Mapping mapping = mappings.get(requestUrl);
        if (mapping == null) {
            throw new Exception("No method mapped to " + req.getRequestURL());
        }

        if (!mapping.getVerb().equals(req.getMethod())) {
            throw new Exception("Method mismatch: Expected " + mapping.getVerb() + " but got " + req.getMethod());
        }

        Class<?> controllerClass = Class.forName(mapping.getClassName());
        Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
        injectSession(controllerClass, controllerInstance, req);
        
        Method method = Reflect.getMethod(controllerClass, mapping.getMethodName());
        Object[] params = buildMethodParams(req, method);
        Object result = method.invoke(controllerInstance, params);

        return prepareResponse(req, res, controllerInstance, method, result);
    }

    private void injectSession(Class<?> controllerClass, Object controllerInstance, HttpServletRequest req) throws Exception {
        for (Field field : controllerClass.getDeclaredFields()) {
            if (field.getType().equals(CustomSession.class)) {
                CustomSession customSession = new CustomSession();
                customSession.setSession(req.getSession());
                field.setAccessible(true);
                field.set(controllerInstance, customSession);
            }
        }
    }

    private Object[] buildMethodParams(HttpServletRequest req, Method method) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (param.isAnnotationPresent(Param.class)) {
                String paramName = param.getAnnotation(Param.class).value();
                paramValues[i] = Reflect.castParameter(req.getParameter(paramName), param.getType().getName());
            } else {
                throw new Exception("Missing @Param annotation on method parameter.");
                // throw new Exception("ETU002404 Missing @Param.");
            }
        }
        return paramValues;
    }

    private String prepareResponse(HttpServletRequest req, HttpServletResponse res, Object controllerInstance, Method method, Object result) throws Exception {
        if (Reflect.isRestAPI(controllerInstance, method.getName())) {
            res.setContentType("application/json");
            return gson.toJson(result);
        } else {
            if (result instanceof ModelView) {
                ModelView modelView = (ModelView) result;
                modelView.getData().forEach(req::setAttribute);
                req.setAttribute("baseUrl", req.getContextPath());
                req.getRequestDispatcher(modelView.getUrl()).forward(req, res);
            }
            return String.valueOf(result);
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        try (PrintWriter out = res.getWriter()) {
            if (!packageError.getMessage().equals("null")) {
                throw new ServletException(packageError);
            }
    
            if (!urlError.getMessage().equals("null")) {
                throw new ServletException(urlError);
            }
    
            out.println(handleRequest(req, res));
        } catch (ServletException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.getRequestDispatcher("/WEB-INF/errorPages/500.jsp").forward(req, res);
        } catch (Exception e) {
            if (e.getMessage().contains("No method mapped") || e.getMessage().contains("Method mismatch")) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                req.getRequestDispatcher("/WEB-INF/errorPages/400.jsp").forward(req, res);
            } else {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                req.getRequestDispatcher("/WEB-INF/errorPages/500.jsp").forward(req, res);
            }
        }
    } 
    // ajout et modification

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
