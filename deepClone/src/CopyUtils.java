import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class CopyUtils {

    public static Object deepCopy(Object original) {
        try {
            Map<Object, Object> visited = new IdentityHashMap<>();
            return deepCopyInternal(original, visited);
        } catch (Exception e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }

    private static Object deepCopyInternal(Object original, Map<Object, Object> visited) throws Exception {
        if (original == null) {
            return null;
        }

        if (visited.containsKey(original)) {
            return visited.get(original);
        }

        Class<?> clazz = original.getClass();

        if (clazz.isArray()) {
            int length = Array.getLength(original);
            Object copy = Array.newInstance(clazz.getComponentType(), length);
            visited.put(original, copy);
            for (int i = 0; i < length; i++) {
                Array.set(copy, i, deepCopyInternal(Array.get(original, i), visited));
            }
            return copy;
        }

        if (original instanceof Collection<?> originalCollection) {
            Collection<Object> copy = original instanceof List ? new ArrayList<>() : new HashSet<>();
            visited.put(original, copy);
            for (Object item : originalCollection) {
                copy.add(deepCopyInternal(item, visited));
            }
            return copy;
        }

        if (original instanceof Map<?, ?> originalMap) {
            Map<Object, Object> copy = new HashMap<>();
            visited.put(original, copy);
            for (Map.Entry<?, ?> entry : originalMap.entrySet()) {
                Object keyCopy = deepCopyInternal(entry.getKey(), visited);
                Object valueCopy = deepCopyInternal(entry.getValue(), visited);
                copy.put(keyCopy, valueCopy);
            }
            return copy;
        }

        if (clazz.isPrimitive() || original instanceof String || original instanceof Number || original instanceof Boolean || original instanceof Character) {
            return original;
        }

        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object copy = constructor.newInstance();
        visited.put(original, copy);

        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.set(copy, deepCopyInternal(field.get(original), visited));
                }
            }
            clazz = clazz.getSuperclass();
        }

        return copy;
    }

    public static void main(String[] args) {
        Man originalMan = new Man("John Doe", 30, List.of("Book1", "Book2"));
        Man copiedMan = (Man) deepCopy(originalMan);

        System.out.println("Original: " + originalMan);
        System.out.println("Copy: " + copiedMan);
    }
}
