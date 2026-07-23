package com.savbill.radius.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;

public class RadiusUtils {

	static Javers javers = JaversBuilder.javers().build();

	public static String getUpdatedDiff(Object o1, Object o2) {
		String updated = "";
		try {
			Diff diff = javers.compare(o1, o2);
			if (diff.hasChanges()) {
				List<Change> changes = diff.getChanges();
				for (Change change : changes) {
//	            	log.info(changes.);
//	                if (change instanceof ValueChange) {
					ValueChange valChange = (ValueChange) change;
					if(!(valChange.getPropertyName().equals("createdOn") || valChange.getPropertyName().equals("lastModifiedOn") ||
							valChange.getPropertyName().equals("createdBy") || valChange.getPropertyName().equals("lastModifiedBy")
							|| valChange.getPropertyName().equals("lastmodifiedon"))) {
						updated = updated + "property: "+valChange.getPropertyName()+" from "+valChange.getLeft()+" to "+valChange.getRight()+" ,";
					}
//					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updated;
	}

	public static boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            return false;
        }
        return date != null;
    }

	public static String readValueFromProperties(String key) {
		String value = "";
		try {
//			Properties prop = new Properties();
//			//load a properties file from class path, inside static method
//			prop.load(RadiusUtils.class.getClassLoader().getResourceAsStream("application.properties"));
//			value = prop.getProperty(key);
			value=System.getenv(key);
			return value;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return value;
	}

	/**
	 * This method is responsible to check null or empty string or string with null value and in-case if returns true it will send empty string
	 *
	 * @param inputString
	 * @return
	 */
	public static boolean notNullNotEmpty(String inputString) {
		boolean validate = true;
		if (inputString == null || inputString.trim().isEmpty() || "null".equalsIgnoreCase(inputString)) {
			validate = false;
		}
		return  validate;
	}

}
