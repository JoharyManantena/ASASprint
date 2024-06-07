package prom16.fonction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflect {
    public static String getClassName(Object obj) throws Exception {
        return obj.getClass().getName();
    }

    public static String[] getAttributes(Object obj) throws Exception{
        Field[] attributs = obj.getClass().getDeclaredFields();
        String[] valiny = new String[attributs.length];
        for (int i = 0; i < valiny.length; i++) {
            valiny[i] = attributs[i].getName();
        }
        return valiny;
    }

    public static Object execMethode(Object obj, String methodeName, Object[] parametre) throws Exception {
        Class<?>[] parameterTypes;
        if (parametre == null) {
            parameterTypes = new Class<?>[0];
        } else {
            parameterTypes = new Class<?>[parametre.length];
            for (int i = 0; i < parametre.length; i++) {
                parameterTypes[i] = parametre[i].getClass();
            }
        }
        Method method = obj.getClass().getDeclaredMethod(methodeName, parameterTypes);
        Object valiny = method.invoke(obj, parametre);
        return valiny;
    }

    public static String execMethodeController(Object obj, String methodeName, Object[] parametre) throws Exception {
        Class<?>[] parameterTypes;
        if (parametre == null) {
            parameterTypes = new Class<?>[0];
        } else {
            parameterTypes = new Class<?>[parametre.length];
            for (int i = 0; i < parametre.length; i++) {
                parameterTypes[i] = parametre[i].getClass();
            }
        }
        Method method = obj.getClass().getDeclaredMethod(methodeName, parameterTypes);
        Object result = method.invoke(obj, parametre);
        if (result instanceof String) {
            return (String) result;
        }
         else {
            String valiny = "";
            if (result.getClass().getTypeName().compareTo("prom16.fonction.ModelView") == 0) {
                valiny = result.getClass().getTypeName();
            }
            else{
                valiny = "Errer : !!! ";
            } 
            return valiny;
        }
    }
}
