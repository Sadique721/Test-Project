package com.diameter.util;

public final class CsvUtils {
	
	private CsvUtils() {
	}

	public static String escape(Object value) {
		if (value == null) {
			return "";
		}
		String str = value.toString();
		if (str.contains("\"")) {
			str = str.replace("\"", "\"\"");
		}
		if (str.contains(",") || str.contains("\n") || str.contains("\r") || str.contains("\"")) {
			return "\"" + str + "\"";
		}
		return str;
	}
	
}