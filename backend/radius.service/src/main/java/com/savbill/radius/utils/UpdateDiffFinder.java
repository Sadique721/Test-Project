package com.savbill.radius.utils;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class UpdateDiffFinder {

    // TODO: Consider making this method more flexible by allowing dynamic exclusion of properties
    public static String getUpdatedDiff(Object oldObj, Object newObj) {
        StringBuilder updated = new StringBuilder();

        try {
            Class<?> clazz = oldObj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            updated.append(" For: ").append(clazz.getSimpleName()).append(", ");

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
        } catch (Exception e) {
            // TODO: Handle exceptions more gracefully, log, or rethrow if needed
            return null;
        }

        // //		System.out.println("Custom changes updated >>>>>>>>>>>>>>>>>> " + updated.toString());
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




    // TODO: Customize the logic to dynamically exclude properties based on requirements
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
                propertyName.equals("id") ||
                propertyName.equals("lastModifiedDate");

    }

    private static boolean areEqual(Object obj1, Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }




}
