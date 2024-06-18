package prom16.fonction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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
                if (parametre[i] instanceof Integer) {
                    parameterTypes[i] = int.class;
                } else if (parametre[i] instanceof Double) {
                    parameterTypes[i] = double.class;
                } else if (parametre[i] instanceof Boolean) {
                    parameterTypes[i] = boolean.class;
                } else if (parametre[i] instanceof Long) {
                    parameterTypes[i] = long.class;
                } else if (parametre[i] instanceof Float) {
                    parameterTypes[i] = float.class;
                } else if (parametre[i] instanceof Short) {
                    parameterTypes[i] = short.class;
                } else if (parametre[i] instanceof Byte) {
                    parameterTypes[i] = byte.class;
                } else {
                    parameterTypes[i] = parametre[i].getClass();
                }
            }
        }
        Method method = obj.getClass().getDeclaredMethod(methodeName, parameterTypes);
        Object valiny = method.invoke(obj, parametre);
        return valiny;
    }

    public static boolean findParam(Object obj,String methodeName)throws Exception{
        Boolean valiny = false;
        Method[] methods = obj.getClass().getDeclaredMethods();
        
        for (Method method : methods) {
            if (method.getName().equals(methodeName)) {
                return method.getParameterCount() > 0;
            }
        }
        return valiny;
    }

    public static Parameter[] getParam(Object obj, String methodName) throws Exception {
        Parameter[] valiny = new Parameter[0];
        Method[] methods = obj.getClass().getDeclaredMethods();
        
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                valiny = method.getParameters();
                break;
            }
        }
        return valiny;
    }

    public static Class<?> getClassForName(String typeName) throws Exception {
        switch (typeName) {
            case "int":
                return int.class;
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "char":
                return char.class;
            case "short":
                return short.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            default:
                return Class.forName(typeName);
        }
    }

    public static Object castParameter(String value, String type) throws Exception {
        Class<?> clazz = getClassForName(type);
        if (value == null) {
            if (clazz == String.class) {
                return "";
            } else if (clazz == Integer.class || clazz == int.class) {
                return 0;
            } else if (clazz == Double.class || clazz == double.class) {
                return 0.0;
            }
            return null;
        }
                
        if (clazz == String.class) {
            return value;
        } else if (clazz == Integer.class || clazz == int.class) {
            return Integer.parseInt(value);
        } else if (clazz == Double.class || clazz == double.class) {
            return Double.parseDouble(value);
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (clazz == Long.class || clazz == long.class) {
            return Long.parseLong(value);
        } else if (clazz == Float.class || clazz == float.class) {
            return Float.parseFloat(value);
        } else if (clazz == Short.class || clazz == short.class) {
            return Short.parseShort(value);
        } else if (clazz == Byte.class || clazz == byte.class) {
            return Byte.parseByte(value);
        } else {
            throw new Exception("Erreur de caste sur cette type " + type);
        }
    }

    public static String execMethodeController(Object obj, String methodeName, Object[] parametre) throws Exception {
        Class<?>[] parameterTypes;
        if (parametre == null) {
            parameterTypes = new Class<?>[0];
        } else {
            parameterTypes = new Class<?>[parametre.length];
            for (int i = 0; i < parametre.length; i++) {
                if (parametre[i] instanceof Integer) {
                    parameterTypes[i] = int.class;
                } else if (parametre[i] instanceof Double) {
                    parameterTypes[i] = double.class;
                } else if (parametre[i] instanceof Boolean) {
                    parameterTypes[i] = boolean.class;
                } else if (parametre[i] instanceof Long) {
                    parameterTypes[i] = long.class;
                } else if (parametre[i] instanceof Float) {
                    parameterTypes[i] = float.class;
                } else if (parametre[i] instanceof Short) {
                    parameterTypes[i] = short.class;
                } else if (parametre[i] instanceof Byte) {
                    parameterTypes[i] = byte.class;
                } else {
                    parameterTypes[i] = parametre[i].getClass();
                }
            }
        }
        Method method = obj.getClass().getDeclaredMethod(methodeName, parameterTypes);
        Object result = method.invoke(obj, parametre);
        if (result instanceof String) {
            return (String) result;
        } else {
            String valiny = "";
            if (result.getClass().getTypeName().compareTo("prom16.fonction.ModelView") == 0) {
                valiny = result.getClass().getTypeName();
            }
            else{
                throw new Exception("Erreur: Type de retour que l'on connait pas. La methode droit etre une String ou une ModelView ");
            } 
            return valiny;
        }
    }
}
