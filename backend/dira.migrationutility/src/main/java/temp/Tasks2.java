package temp;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import utility.ReadWriteExcelFile;
import utility.Utility;

class Tasks2 extends Thread {
	private String taskName;

//constructor of the class Tasks  
	public Tasks2(String str) {
//initializing the field taskName   
		taskName = str;
	}

//Printing the task name and then sleeps for 1 sec  
//The complete process is getting repeated five times  
	public void run() {
		callUpdateShee();
		
	}
	
	
	private void callUpdateShee() {
		while(true){
			updateSheet();
		}
	}
	
	
	public void updateSheet() {
		try {
			
			Thread.sleep(5000);
			ReadWriteExcelFile rw = new ReadWriteExcelFile();
			//rw.setMigrationStatus1("Customer");
			rw.setMultipleColumnInActiveSheet();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void updateSheet1() {
		try {
			
			Thread.sleep(1000);
			ReadWriteExcelFile rw = new ReadWriteExcelFile();
			//rw.setMigrationStatus1("Customer");  // --> i have comment this on 30 dec
			rw.setMultipleColumnInActiveSheet();
			
	/*		Set<String> keys = map.keySet();
			Iterator<String> keyIter = keys.iterator();

			while (keyIter.hasNext()) {
				String row = keyIter.next();
				Utility.printLog("Multithread.log", "processmessage", "Start", row);
				us.removeRowFromList(row);
			}
	*/				
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}