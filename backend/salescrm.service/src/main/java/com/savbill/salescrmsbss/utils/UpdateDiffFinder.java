package com.savbill.salescrmsbss.utils;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
public class UpdateDiffFinder {

    // TODO: Consider making this method more flexible by allowing dynamic exclusion of properties
    public static String getUpdatedDiff(Object oldObj, Object newObj) {
        StringBuilder updated = new StringBuilder();

        try {
            Class<?> clazz = oldObj.getClass();
            Field[] fields = clazz.getDeclaredFields();
//            updated.append(" For: ").append(clazz.getSimpleName()).append(", ");

            int fieldCount = fields.length;

            int changesCount = 0;

            for (int i = 0; i < fieldCount; i++) {
                Field field = fields[i];
                field.setAccessible(true);

                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);

                // TODO: Customize the exclusion logic based on your requirements
                if ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) {
                    if (!isExcludedProperty(field.getName()) && !areEqual(oldValue, newValue)) {
                        if (isListOrSetField(field)) {
                            // If the field is a List or Set, compare its elements
                            String collectionChanges = getUpdatedCollectionDiff(field.getName(), (Collection<?>) oldValue, (Collection<?>) newValue);
                            if (collectionChanges != null) {
                                updated.append(collectionChanges);
                                changesCount++;
                            }
                        } else {
                            updated.append(field.getName())
                                    .append(" changes from ").append(oldValue)
                                    .append(" to ").append(newValue);

                            changesCount++;

                            // Append " , and " if there are more changes
                            if (changesCount < countUpdatedFields(oldObj, newObj)) {
                                updated.append(" , and ");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO: Handle exceptions more gracefully, log, or rethrow if needed
            return null;
        }

        return updated.toString();
    }
    private static int countUpdatedFields(Object oldObj, Object newObj) throws IllegalAccessException {
        Class<?> clazz = oldObj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        int count = 0;

        for (Field field : fields) {
            field.setAccessible(true);

            Object oldValue = field.get(oldObj);
            Object newValue = field.get(newObj);

            if ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) {
                if (!isExcludedProperty(field.getName()) && !areEqual(oldValue, newValue)) {
                    count++;
                }
            }
        }

        return count;
    }
    private static boolean isExcludedProperty(String propertyName) {
        return propertyName.equals("createdOn") ||
                propertyName.equals("lastModifiedOn") ||
                propertyName.equals("createdBy") ||
                propertyName.equals("lastModifiedBy") ||
                propertyName.equals("lastModifiedById") ||
                propertyName.equals("lastModifiedByName") ||
                propertyName.equals("mvnoId") ||
                propertyName.equals("createdByName") ||
                propertyName.equals("createdate") ||
                propertyName.equals("isDelete") ||
                propertyName.equals("updatedate") ||
                propertyName.equals("createdById") ||
                propertyName.equals("id");
    }

    private static boolean areEqual(Object obj1, Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }

    private static boolean isListOrSetField(Field field) {
        return List.class.isAssignableFrom(field.getType()) || Set.class.isAssignableFrom(field.getType());
    }
    private static String getUpdatedCollectionDiff(String fieldName, Collection<?> oldCollection, Collection<?> newCollection) {

        StringBuilder listChanges = new StringBuilder();

        int size = Math.min(oldCollection.size(), newCollection.size());

        for (int i = 0; i < size; i++) {
            Object oldItem = oldCollection instanceof List ? ((List<?>) oldCollection).get(i) : oldCollection.toArray()[i];
            Object newItem = newCollection instanceof List ? ((List<?>) newCollection).get(i) : newCollection.toArray()[i];


            String itemChanges = getUpdatedDiff(oldItem, newItem);
            if (itemChanges != null && !itemChanges.isEmpty()) {
                listChanges.append(fieldName)
                        .append("[").append(i).append("] changes: ")
                        .append(itemChanges);

                // Append " , and " if there are more changes
                if (i < size - 1) {
                    listChanges.append(" , and ");
                }

            }
        }

        return listChanges.length() > 0 ? listChanges.toString() : null;
    }
}
