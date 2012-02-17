package co.cdev.agave.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities that pertain to reflection and class introspection.
 * 
 * @author ddc
 */
public class ReflectionUtils {
    
    private static final Pattern ACCESSOR_PATTERN = Pattern.compile("^(get|is|has|can)([A-Z])(.*)");
    private static final Pattern MUTATOR_PATTERN  = Pattern.compile("^(set)([A-Z])(.*)");
    
    /**
     * Walks up the object hierarchy to get a declared field.  This will
     * work for public, protected, package, and privately scoped fields.
     * 
     * @param type most specific type in the object hierarchy
     * @param fieldName the name of the field to get
     * @return the {@code Field} if it was found; null if it was not 
     */
    public static Field getField(Class<?> type, String fieldName) {
        if (type != null && fieldName != null) {
            Class<?> currentType = type;
            
            while (!Object.class.equals(currentType)) {
                for (Field currentField : currentType.getDeclaredFields()) {
                    if (fieldName.equals(currentField.getName())) {
                        currentField.setAccessible(true);
                        return currentField;
                    }
                }
                
                currentType = currentType.getSuperclass();
            }
        }
        
        return null;
    }
    
    /**
     * Gets a method and manually performs autoboxing/unboxing (there is no way to do it reflectively).
     * 
     * @param type the most specific type in the class hierarchy to get the method from
     * @param name the method name
     * @param parameterTypes the types of its parameters
     * @return the method if found, null if not
     */
    public static Method getMethod(Class<?> type, String name, Type... parameterTypes) {
        if (type != null && name != null) {
            Class<?> currentType = type;
            
            while (!Object.class.equals(currentType)) {
                for (Method currentMethod : currentType.getDeclaredMethods()) {
                    if (name.equals(currentMethod.getName()) && parameterTypes.length == currentMethod.getParameterTypes().length) {
                        boolean allParametersMatch = true;
                        
                        for (int i = 0; i < parameterTypes.length; i++) {
                            Type a = parameterTypes[i];
                            Type b = currentMethod.getParameterTypes()[i];
                            
                            if (!canBeSubstituted(a, b)) {
                                break;
                            }
                        }
                        
                        if (allParametersMatch) {
                            return currentMethod;
                        }
                    }
                }
                
                currentType = currentType.getSuperclass();
            }
        }
        
        return null;
    }
    
    public static Method getMutator(Class<?> type, String name, Type parameterTypes) {
        return ReflectionUtils.getMethod(type, "set" + name.substring(0, 1).toUpperCase() + name.substring(1), parameterTypes);
    }
    
    public static Method getAccessor(Class<?> type, String name) {
        return ReflectionUtils.getMethod(type, "get" + name.substring(0, 1).toUpperCase() + name.substring(1));
    }
    
    /**
     * Indicates whether two {@code Type}s can be substituted.  This is useful for manually
     * doing autoboxing/unboxing.
     * 
     * @param a Type a
     * @param b Type b
     * @return whether or not the two types can be substituted
     */
    public static boolean canBeSubstituted(Type a, Type b) {
        if (a == int.class && b == Integer.class || a == Integer.class && b == int.class)
            return true;
        
        if (a == boolean.class && b == Integer.class || a == Integer.class && b == boolean.class)
            return true;
        
        if (a == double.class && b == Double.class || a == Double.class && b == double.class)
            return true;
        
        if (a == long.class && b == Long.class || a == Long.class && b == long.class)
            return true;
        
        if (a == float.class && b == Float.class || a == Float.class && b == float.class)
            return true;
        
        if (a == short.class && b == Short.class || a == Short.class && b == short.class)
            return true;
        
        if (a == char.class && b == Character.class || a == Character.class && b == char.class)
            return true;
        
        if (a == byte.class && b == Byte.class || a == Byte.class && b == byte.class)
            return true;
        
        return false;
    }
    
    /**
     * Retrieves all accessors by walking up the inheritance tree and aggregating all methods
     * whose name mathches one of the following:
     * 
     *   - getSomething()
     *   - isSomething()
     *   - hasSomething()
     *   - canSomethingBeDone()
     *   
     * The algorithm stops before it inspects {@code Object.class}, so methods such as 
     * {@code getClass()} will not be included. 
     * 
     * @param type the most specific type in the inheritance tree to inspect
     * @return all accessors that were found.
     */
    public static List<Method> getAccessors(Class<?> type) {
        List<Method> accessors = null;
        
        if (type == null) {
            accessors = Collections.emptyList();
        } else {
            accessors = new ArrayList<Method>();
            
            Class<?> currentType = type;
            
            while (!Object.class.equals(currentType)) {
                for (Method currentMethod : currentType.getDeclaredMethods()) {
                    Matcher matcher = ACCESSOR_PATTERN.matcher(currentMethod.getName());
                    if (matcher.matches() && matcher.groupCount() == 3) {
                        accessors.add(currentMethod);
                    }
                }
                
                currentType = currentType.getSuperclass();
            }
        }
        
        return accessors;
    }
    
    /**
     * Gets the field that is being accessed by the given {@code Method}.
     * 
     * @param accessor the accessor whose field is being gotten
     * @return the accessed field
     */
    public static Field getAccessedField(Method accessor) {
        Field accessedField = null;
        
        if (accessor != null) {
            Matcher matcher = ACCESSOR_PATTERN.matcher(accessor.getName());
            
            if (matcher.matches() && matcher.groupCount() == 3) {
                String fieldName = matcher.group(2).toLowerCase() + matcher.group(3);
                accessedField = getField(accessor.getDeclaringClass(), fieldName);
            }
        }
        
        return accessedField;
    }
    
    /**
     * Retrieves all mutators by walking up the inheritance tree and aggregating all methods
     * whose name mathches {@code setSometing(anArgument)}.
     *   
     * The algorithm stops before it inspects {@code Object.class}, so methods on the  
     * {@code Object} class will not be included. 
     * 
     * @param type the most specific type in the inheritance tree to inspect
     * @return all mutators that were found.
     */
    public static List<Method> getMutators(Class<?> type) {
        List<Method> mutators = null;
        
        if (type == null) {
            mutators = Collections.emptyList();
        } else {
            mutators = new ArrayList<Method>();
            
            Class<?> currentType = type;
            
            while (!Object.class.equals(currentType)) {
                for (Method currentMethod : currentType.getDeclaredMethods()) {
                    Matcher matcher = MUTATOR_PATTERN.matcher(currentMethod.getName());
                    if (matcher.matches() && matcher.groupCount() == 3) {
                        mutators.add(currentMethod);
                    }
                }
                
                currentType = currentType.getSuperclass();
            }
        }
        
        return mutators;
    }
    
    /**
     * Gets the field that is being mutated by the given {@code Method}.
     * 
     * @param accessor the mutator whose field is being set
     * @return the accessed field
     */
    public static Field getMutatedField(Method mutator) {
        Field mutatedField = null;
        
        if (mutator != null) {
            Matcher matcher = MUTATOR_PATTERN.matcher(mutator.getName());
            
            if (matcher.matches() && matcher.groupCount() == 3) {
                String fieldName = matcher.group(2).toLowerCase() + matcher.group(3);
                mutatedField = getField(mutator.getDeclaringClass(), fieldName);
            }
        }
        
        return mutatedField;
    }
    
    public static void invokeMutator(Object object, String fieldName, Object fieldValue) 
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class<?> modelObjectClass = object.getClass();
        Class<?> actualValueClass = fieldValue.getClass();
        Class<?> expectedValueClass = actualValueClass;
        Field field = ReflectionUtils.getField(modelObjectClass, fieldName);
        
        if (field != null) {
            expectedValueClass = field.getType();
        }
        
        Method mutator = ReflectionUtils.getMutator(modelObjectClass, fieldName, expectedValueClass);
        
        if (mutator != null) {
            try {
                if ((expectedValueClass.equals(int.class) || expectedValueClass.equals(Integer.class)) && Number.class.isAssignableFrom(actualValueClass)) {
                    mutator.invoke(object, ((Number) fieldValue).intValue());
                } else if (expectedValueClass.equals(double.class) || expectedValueClass.equals(Double.class) && Number.class.isAssignableFrom(actualValueClass)) {
                    mutator.invoke(object, ((Number) fieldValue).doubleValue());
                } else if (expectedValueClass.equals(long.class) || expectedValueClass.equals(Long.class) && Number.class.isAssignableFrom(actualValueClass)) {
                    mutator.invoke(object, ((Number) fieldValue).longValue());
                } else if (expectedValueClass.equals(float.class) || expectedValueClass.equals(Float.class) && Number.class.isAssignableFrom(actualValueClass)) {
                    mutator.invoke(object, ((Number) fieldValue).floatValue());
                } else if (expectedValueClass.equals(short.class) || expectedValueClass.equals(Short.class) && Number.class.isAssignableFrom(actualValueClass)) {
                    mutator.invoke(object, ((Number) fieldValue).shortValue());
                } else if (expectedValueClass.equals(byte.class) || expectedValueClass.equals(Byte.class) && Number.class.isAssignableFrom(actualValueClass)) {
                    mutator.invoke(object, ((Number) fieldValue).byteValue());
                } else if (!expectedValueClass.equals(actualValueClass) && expectedValueClass.isAssignableFrom(actualValueClass)) {
                    mutator.invoke(object, expectedValueClass.cast(fieldValue));
                } else {
                    mutator.invoke(object, fieldValue);
                }
            } catch (IllegalArgumentException e) {
                String message = String.format("Argument type mismatch for %s - expected %s, got %s",
                        mutator,
                        actualValueClass == null ? "null" : actualValueClass.getName(), 
                        fieldValue == null ? "null" : fieldValue.getClass().getName());
                throw new IllegalArgumentException(message);
            }
        }
    }
    
}
