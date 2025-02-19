package prom16.fonction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import prom16.annotation.Email;
import prom16.annotation.Length;
import prom16.annotation.FutureDate;
import jakarta.servlet.http.Part;

import java.util.regex.Pattern;
import prom16.annotation.Email;
import prom16.annotation.Length;
import prom16.annotation.FutureDate;
import prom16.annotation.NotNull;
import prom16.annotation.Positive;
import prom16.annotation.Rest;


public class Reflect {

    public static String getClassName(Object obj) throws ClassNotFoundException {
        return obj.getClass().getName();
    }

    public static String[] getAttributes(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                     .map(Field::getName)
                     .toArray(String[]::new);
    }

    public static Object execMethode(Object obj, String methodeName, Object[] parametre) throws Exception {
        Class<?>[] parameterTypes = getParameterTypes(parametre);
        Method method = obj.getClass().getDeclaredMethod(methodeName, parameterTypes);
        return method.invoke(obj, parametre);
    }

    private static Class<?>[] getParameterTypes(Object[] parametre) {
        if (parametre == null) {
            return new Class<?>[0];
        }
        return Arrays.stream(parametre)
                     .map(Reflect::getParameterClass)
                     .toArray(Class<?>[]::new);
    }

    private static Class<?> getParameterClass(Object param) {
        if (param == null) return Object.class;
        if (param instanceof Integer) return int.class;
        if (param instanceof Double) return double.class;
        if (param instanceof Boolean) return boolean.class;
        if (param instanceof Long) return long.class;
        if (param instanceof Float) return float.class;
        if (param instanceof Short) return short.class;
        if (param instanceof Byte) return byte.class;
        if (param instanceof Part) return Part.class;
        return param.getClass();
    }

    public static boolean findParam(Object obj, String methodeName) throws Exception {
        return Arrays.stream(obj.getClass().getDeclaredMethods())
                     .anyMatch(method -> method.getName().equals(methodeName) && method.getParameterCount() > 0);
    }

    public static Parameter[] getParam(Object obj, String methodName) throws Exception {
        return Arrays.stream(obj.getClass().getDeclaredMethods())
                     .filter(method -> method.getName().equals(methodName))
                     .findFirst()
                     .map(Method::getParameters)
                     .orElse(new Parameter[0]);
    }

    public static <T> T process(T object, Object[] lesValeurs) throws Exception {
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length && i < lesValeurs.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Object valeur = lesValeurs[i] != null ? lesValeurs[i] : getDefaultValue(field.getType());
            field.set(object, valeur);
        }
        return object;
    }

    public static Object getDefaultValue(Class<?> type) throws Exception {
        if (type.isPrimitive()) {
            if (type == int.class) return 0;
            if (type == boolean.class) return false;
            if (type == byte.class) return (byte) 0;
            if (type == short.class) return (short) 0;
            if (type == long.class) return 0L;
            if (type == float.class) return 0.0f;
            if (type == double.class) return 0.0;
            if (type == char.class) return '\u0000';
        }
        return null;
    }

    public static Class<?> getClassForName(String typeName) throws ClassNotFoundException {
        switch (typeName) {
            case "int": return int.class;
            case "boolean": return boolean.class;
            case "byte": return byte.class;
            case "char": return char.class;
            case "short": return short.class;
            case "long": return long.class;
            case "float": return float.class;
            case "double": return double.class;
            default: return Class.forName(typeName);
        }
    }

    public static Object castParameter(String value, String type) throws Exception {
        Class<?> clazz = getClassForName(type);
        if (value == null) return getDefaultValue(clazz);
        return castValue(clazz, value);
    }

    private static Object castValue(Class<?> clazz, String value) throws Exception {
        if (clazz == String.class) return value;
        if (clazz == int.class || clazz == Integer.class) return Integer.parseInt(value);
        if (clazz == double.class || clazz == Double.class) return Double.parseDouble(value);
        if (clazz == boolean.class || clazz == Boolean.class) return Boolean.parseBoolean(value);
        if (clazz == long.class || clazz == Long.class) return Long.parseLong(value);
        if (clazz == float.class || clazz == Float.class) return Float.parseFloat(value);
        if (clazz == short.class || clazz == Short.class) return Short.parseShort(value);
        if (clazz == byte.class || clazz == Byte.class) return Byte.parseByte(value);
        throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
    }

    public static Object getPartValue(Part part, Class<?> targetType) throws Exception {
        String value = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return castValue(targetType, value);
    }

    public static String execMethodeController(Object obj, String methodeName, Object[] parametre) throws Exception {
        Object result = execMethode(obj, methodeName, parametre);
        if (result instanceof String) {
            return (String) result;
        }
        if ("prom16.fonction.ModelView".equals(result.getClass().getTypeName())) {
            return result.getClass().getTypeName();
        }
        throw new Exception("Erreur: Type de retour inconnu.");
    }

    public static boolean isRestAPI(Object obj, String methodName) {
        return Arrays.stream(obj.getClass().getDeclaredMethods())
                     .filter(method -> method.getName().equals(methodName))
                     .anyMatch(method -> method.isAnnotationPresent(Rest.class));
    }


    public static List<String> validate(Object obj) throws IllegalAccessException {
        List<String> errors = new ArrayList<>();

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);

            if (field.isAnnotationPresent(Email.class)) {
                if (value != null && !isValidEmail(value.toString())) {
                    errors.add(field.getAnnotation(Email.class).message());
                }
            }
            if (field.isAnnotationPresent(NotNull.class) && value == null) {
                errors.add(field.getAnnotation(NotNull.class).message());
            }
            if (field.isAnnotationPresent(Length.class)) {
                Length length = field.getAnnotation(Length.class);
                String stringValue = value != null ? value.toString() : "";
                if (stringValue.length() < length.min() || stringValue.length() > length.max()) {
                    errors.add(length.message());
                }
            }
            if (field.isAnnotationPresent(Positive.class) && value instanceof Number && ((Number) value).doubleValue() <= 0) {
                errors.add(field.getAnnotation(Positive.class).message());
            }
            if (field.isAnnotationPresent(FutureDate.class) && value instanceof LocalDate && ((LocalDate) value).isBefore(LocalDate.now())) {
                errors.add(field.getAnnotation(FutureDate.class).message());
            }
        }

        return errors;
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
