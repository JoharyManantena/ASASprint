package prom16.fonction;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import prom16.annotation.Email;
import prom16.annotation.Range;
import prom16.annotation.Required;
import prom16.annotation.Rest;
import jakarta.servlet.http.Part;

// import com.thoughtworks.paranamer.AdaptiveParanamer;
// import com.thoughtworks.paranamer.Paranamer;

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
                if (parametre[i] == null) {
                    parameterTypes[i] = Object.class;
                }else if (parametre[i] instanceof Integer) {
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
                } else if (parametre[i].getClass().getName().equals("org.apache.catalina.core.ApplicationPart")) {
                    parameterTypes[i] = jakarta.servlet.http.Part.class;
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

    // public static String[] parameterNames(Object obj, String methodName)throws Exception {
    //     Method[] methods = obj.getClass().getDeclaredMethods();
    //     String[] valiny = new String[0]; 
    //     for (Method method : methods) {
    //         if (method.getName().equals(methodName)) {
    //             try {
    //                 Paranamer paranamer = new AdaptiveParanamer();
    //                 valiny = paranamer.lookupParameterNames(method);
    //                 if (valiny.length == 0) {
    //                     throw new Exception("No parameter names found for method: " + methodName);
    //                 }
    //             } catch (Exception e) {
    //                 throw new Exception("Error fetching parameter names: " + e.getMessage()+" avec nom methode = "+methodName + " ou "+ method.getName());
    //             }
    //             break;
    //         }
    //     }
    //     return valiny;
    // }
    
    public static <T> T process(T object, Object[] lesValeurs) throws Exception {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length && i < lesValeurs.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Object valeur = lesValeurs[i];
            if (valeur != null) {
                field.set(object, valeur);
            } else {
                field.set(object, getDefaultValue(field.getType()));
            }
        }
        return object;
    }

    public static Object getDefaultValue(Class<?> type) throws Exception{
        if (type.isPrimitive()) {
            if (type == int.class) {
                return 0;
            } else if (type == boolean.class) {
                return false;
            } else if (type == byte.class) {
                return (byte) 0;
            } else if (type == short.class) {
                return (short) 0;
            } else if (type == long.class) {
                return 0L;
            } else if (type == float.class) {
                return 0.0f;
            } else if (type == double.class) {
                return 0.0;
            } else if (type == char.class) {
                return '\u0000';
            }
        } else {
            return null;
        }
        throw new Exception("Type non supporté : " + type.getName());
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

    public static Object getPartValue(Part part, Class<?> targetType) throws IOException, ParseException {
        String value = new String(part.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // Traitez la valeur en fonction du type cible
        if (targetType == String.class) {
            return value; // Retourne directement la chaîne
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value); // Convertit en entier
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value); // Convertit en double
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value); // Convertit en booléen
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value); // Convertit en long
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(value); // Convertit en float
        } else if (targetType == Short.class || targetType == short.class) {
            return Short.parseShort(value); // Convertit en short
        } else if (targetType == Byte.class || targetType == byte.class) {
            return Byte.parseByte(value); // Convertit en byte
        } else if (targetType == java.util.Date.class) {
            // Si c'est une date, utilisez un format spécifique
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Ajustez le format selon vos besoins
            return sdf.parse(value); // Convertit en Date
        } else {
            throw new IllegalArgumentException("Type non supporté: " + targetType.getName());
        }
    }

    public static String execMethodeController(Object obj, String methodeName, Object[] parametre) throws Exception {
        Class<?>[] parameterTypes;
        if (parametre == null) {
            parameterTypes = new Class<?>[0];
        } else {
            parameterTypes = new Class<?>[parametre.length];
            for (int i = 0; i < parametre.length; i++) {
                if (parametre[i] == null) {
                    parameterTypes[i] = Object.class;
                } else if (parametre[i] instanceof Integer) {
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
                } else if (parametre[i].getClass().getName().equals("org.apache.catalina.core.ApplicationPart")) {
                    parameterTypes[i] = jakarta.servlet.http.Part.class;
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
            } else {
                throw new Exception("Erreur: Type de retour inconnu. La méthode doit retourner une String ou une ModelView.");
            }
            return valiny;
        }
    }


    public static boolean isRest(Object obj,String methodName){
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


    // public static boolean validation(Field attributs, String valeur) {
    //     boolean valiny = true;

    //     if (attributs.isAnnotationPresent(Required.class)) {
    //         if (valeur == null || valeur.trim().isEmpty()) {
    //             return false;
    //         }
    //     }

    //     if (attributs.isAnnotationPresent(Email.class)) {
    //         String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    //         Pattern pattern = Pattern.compile(emailRegex);
    //         if (!pattern.matcher(valeur).matches()) {
    //             return false;
    //         }
    //     }

    //     if (attributs.isAnnotationPresent(Range.class)) {
    //         try {
    //             double valeurNumerique = Double.parseDouble(valeur);
    //             Range range = attributs.getAnnotation(Range.class);
    //             if (valeurNumerique < range.min() || valeurNumerique > range.max()) {
    //                 return false; 
    //             }
    //         } catch (NumberFormatException e) {
    //             return false;
    //         }
    //     } 
    //     return valiny;
    // }

    // public static List<String> erreurValidation(Field attributs, String valeur) {
    //     List<String> valiny = new ArrayList<String>();
    //     if (attributs.isAnnotationPresent(Required.class)) {
    //         if (valeur == null || valeur.trim().isEmpty()) {
    //             String temp = "La valeur est obligatoire, donc ne doit pas être null ou vide sur cette champ "+attributs.getName();
    //             valiny.add(temp);
    //         }
    //     }
    //     if (attributs.isAnnotationPresent(Email.class)) {
    //         String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    //         Pattern pattern = Pattern.compile(emailRegex);
    //         if (!pattern.matcher(valeur).matches()) {
    //             String temp = "La valeur doit correspondre au format email sur cette champ "+attributs.getName();
    //             valiny.add(temp);
    //         }
    //     }
    //     if (attributs.isAnnotationPresent(Range.class)) {
    //         try {
    //             double valeurNumerique = Double.parseDouble(valeur);
    //             Range range = attributs.getAnnotation(Range.class);
    //             if (valeurNumerique < range.min() || valeurNumerique > range.max()) {
    //                 String temp = "La valeur doit être entre min:"+range.min()+" et max:"+range.max()+" pour cette champ "+attributs.getName();
    //                 valiny.add(temp);
    //             }
    //         } catch (NumberFormatException e) {
    //             String temp = "La valeur doit être un nombre si @Range est présent sur cette champ "+attributs.getName();
    //             valiny.add(temp);
    //         }
    //     } 
    //     return valiny;
    // }

    public static Method getMethode(Object obj , String methodName)  throws Exception {
        Method[] methods = obj.getClass().getDeclaredMethods();
        Method valiny = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                valiny = method;
            }
        }
        return valiny;
    }

}
