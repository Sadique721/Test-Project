package temp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateSheet {

	private static Map<String, String> rowList = new ConcurrentHashMap<String, String>();
	private static String activeSheetName = null;
	
	public String getActiveSheetName() {
		return activeSheetName;
	}

	public void setActiveSheetName(String activeSheetName) {
		UpdateSheet.activeSheetName = activeSheetName;
	}

	public void setRowList(String row) {
		rowList.put(row, row);
	}
	
	public void setRowList(String row,String value) {
		rowList.put(row, value);
		
	}
	
	public Map<String, String> getRowList(){
		return rowList;
		
	}
	
	public void removeRowFromList(String row) {
		rowList.remove(row);
	}
	
	
}



/*
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateSheet {

    private Map<String, String> rowList = new ConcurrentHashMap<>(); // Instance-specific row list
    private ThreadLocal<String> activeSheetName = new ThreadLocal<>(); // Thread-local sheet name

    // Getter for activeSheetName
    public String getActiveSheetName() {
        return activeSheetName.get();
    }

    // Setter for activeSheetName
    public void setActiveSheetName(String activeSheetName) {
        this.activeSheetName.set(activeSheetName);
    }

    // Adds a row with the specified row identifier and value to the map
    public void setRowList(String row, String value) {
        rowList.put(row, value);
    }

    // Retrieves the row list (current state of the rows map)
    public Map<String, String> getRowList() {
        return rowList;
    }

    // Removes a specific row from the map
    public void removeRowFromList(String row) {
        rowList.remove(row);
    }
}  */
