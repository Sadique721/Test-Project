package com.savbill.notification.utils;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Component
public class NotificationUtils {

    static Javers javers= JaversBuilder.javers().withListCompareAlgorithm(ListCompareAlgorithm.AS_SET).build();

    public static String getUpdatedDiff(Object o1, Object o2) {
        String updated = "";
        try {

            Diff diff = javers.compare(o1, o2);

            if (diff.hasChanges()) {
                List<Change> changes = diff.getChanges();
                for (Change change : changes) {
                    if (change instanceof ListChange) {
                        ListChange valChange = (ListChange) change;
                        if (!(valChange.getPropertyName().equals("createdOn")
                                || valChange.getPropertyName().equals("lastModifiedOn")
                                || valChange.getPropertyName().equals("createdBy")
                                || valChange.getPropertyName().equals("lastModifiedBy"))) {
                            updated = updated + "property: " + valChange.getPropertyName()+" : "+valChange.getChanges();


                        }
                    }
                    if (change instanceof ValueChange) {
                        ValueChange valChange = (ValueChange) change;
                        if (!(valChange.getPropertyName().equals("createdOn")
                                || valChange.getPropertyName().equals("lastModifiedOn")
                                || valChange.getPropertyName().equals("createdBy")
                                || valChange.getPropertyName().equals("lastModifiedBy"))) {
                            updated = updated + "property: " + valChange.getPropertyName() + " from " + valChange.getLeft() + " to " + valChange.getRight() + " ,";

                        }
                    }
                    if (change instanceof ReferenceChange) {
                        ReferenceChange newObjecte = (ReferenceChange) change;
                        updated = updated + "property: " + newObjecte.getPropertyName() + " from " + newObjecte.getLeft() + " to " + newObjecte.getRight() + " ,";
                        }
                    }
                }

        } catch (Exception e) {
            return null;

        }
        return updated;
    }

    public static String GetError(Throwable e){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        double msgLength =(Integer) stringWriter.toString().length()*0.15;
        int endIndx = (int) msgLength;
        return stringWriter.toString().substring(0,endIndx);
    }
}
