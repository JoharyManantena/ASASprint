package prom16.fonction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import prom16.annotation.Rest;
import prom16.annotation.FileValidator; // Ensure this matches your package structure
import jakarta.servlet.http.Part; // For the Part class


public class Reflect {
    
    public static String getClassName(Object obj) throws Exception {
        return obj.getClass().getName();
    }

    public static String[] getAttributes(Object obj) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        String[] attributeNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            attributeNames[i] = fields[i].getName();
        }
        return attributeNames;
    }

    public static Object execMethod(Object obj, String methodName, Object[] params) throws Exception {
        Class<?>[] paramTypes = getParameterTypes(params);
        Method method = obj.getClass().getDeclaredMethod(methodName, paramTypes);
        return method.invoke(obj, params);
    }

    private static Class<?>[] getParameterTypes(Object[] params) {
        if (params == null) return new Class<?>[0];

        Class<?>[] types = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            types[i] = params[i] == null ? Object.class : params[i].getClass();
        }
        return types;
    }

    public static boolean findParam(Object obj, String methodName) throws Exception {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method.getParameterCount() > 0;
            }
        }
        return false;
    }

    public static Parameter[] getParam(Object obj, String methodName) throws Exception {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method.getParameters();
            }
        }
        return new Parameter[0];
    }

    public static <T> T process(T object, Object[] values) throws Exception {
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length && i < values.length; i++) {
            fields[i].setAccessible(true);
            fields[i].set(object, values[i] != null ? values[i] : getDefaultValue(fields[i].getType()));
        }
        return object;
    }

    public static Object getDefaultValue(Class<?> type) throws Exception {
        if (type.isPrimitive()) {
            if (type == int.class) return 0;
            if (type == boolean.class) return false;
            return type.isAssignableFrom(char.class) ? '\u0000' : null;
        }
        return null;
    }

    public static Class<?> getClassForName(String typeName) throws Exception {
        return switch (typeName) {
            case "int" -> int.class;
            case "boolean" -> boolean.class;
            case "byte" -> byte.class;
            case "char" -> char.class;
            case "short" -> short.class;
            case "long" -> long.class;
            case "float" -> float.class;
            case "double" -> double.class;
            default -> Class.forName(typeName);
        };
    }

    public static Object castParameter(String value, String type) throws Exception {
        Class<?> clazz = getClassForName(type);
        if (value == null) return getDefaultValue(clazz);

        return switch (clazz.getSimpleName()) {
            case "String" -> value;
            case "int" -> Integer.parseInt(value);
            case "double" -> Double.parseDouble(value);
            case "boolean" -> Boolean.parseBoolean(value);
            case "long" -> Long.parseLong(value);
            case "float" -> Float.parseFloat(value);
            case "short" -> Short.parseShort(value);
            case "byte" -> Byte.parseByte(value);
            default -> throw new Exception("Invalid type: " + type);
        };
    }

    public static String execMethodController(Object obj, String methodName, Object[] params) throws Exception {
        Class<?>[] paramTypes = getParameterTypes(params);
        Method method = obj.getClass().getDeclaredMethod(methodName, paramTypes);
        Object result = method.invoke(obj, params);
        
        if (result instanceof String) return (String) result;
        if (result.getClass().getTypeName().equals("prom16.fonction.ModelView")) {
            return result.getClass().getTypeName();
        }
        throw new Exception("Invalid return type: must be String or ModelView");
    }

    public static Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException("No method named " + methodName + " found in class " + clazz.getName());
    }

    public static boolean isRestAPI(Object obj,String methodName){
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (method.isAnnotationPresent(Rest.class)) {
                    return true;
                }
                break;
            }
        }
        return false;
    }


    public static String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 2, token.length() - 1);
            }
        }
        return null;
    }

    public static void validateFileWithAnnotation(Part part, String fileName, Method method) throws FileValidationException {
        if (method.isAnnotationPresent(FileValidator.class)) {
            FileValidator fileValidator = method.getAnnotation(FileValidator.class);
            long maxSize = fileValidator.maxSize();
            String[] allowedTypes = fileValidator.allowedTypes();

            // Validate file size
            if (part.getSize() > maxSize) {
                throw new FileValidationException("File " + fileName + " is too large. Max allowed size is " + (maxSize / 1024 / 1024) + " MB.");
            }

            // Validate MIME type
            String fileType = part.getContentType();
            boolean isValidType = false;
            for (String type : allowedTypes) {
                if (fileType.equals(type)) {
                    isValidType = true;
                    break;
                }
            }
            if (!isValidType) {
                throw new FileValidationException("Invalid file type: " + fileName + ". Allowed types: " + String.join(", ", allowedTypes));
            }
        }
    }

    // Define custom exceptions for better error handling
    public static class FileValidationException extends Exception {
        public FileValidationException(String message) {
            super(message);
        }
    }

}
