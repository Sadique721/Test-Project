package temp;

import java.util.List;
import java.util.Map;

import Act_Migration.ActCustomerManager;
import customer.PrepaidCustomer;
import customer.PrepaidCustomerNew;
import inventory.Vendor;

class WorkerThread implements Runnable {
	private String message;
	private List<Map<String, String>> list;

//	public WorkerThread(String s) {/
//		this.message = s;
//	}

	public WorkerThread(String s, List<Map<String, String>> list) {
		this.message = s;
		this.list = list;
	}

	public void run() {
		//System.out.println(Thread.currentThread().getName() + " (Start) message = " + message);
		processmessage();// call processmessage method that sleeps the thread for 2 seconds
		//System.out.println(Thread.currentThread().getName() + " (End)");// prints thread name
	}

	private void processmessage() {
		try {
			Thread.sleep(2000);  // here i have intilize 0 from 500

			//Vendor vendor = new Vendor();
			//vendor.createVendor(list);
			
			PrepaidCustomerNew prepaidCustomerNew = new PrepaidCustomerNew();
			prepaidCustomerNew.createPrepaidCustomer(list);	
			
		/*	System.out.println("****** " + list.size() + "\n\n");
			Utility.printLog("Multithread", "processmessage", "Response", "****** " + list.size() + "\n\n");
			//System.out.println(list.toString());
			
			for (int i = 0; i < list.size(); i++) {

				Map<String, String> map = new HashMap<String, String>();
				map = list.get(i);
				System.out.println(map.toString());
				//batchList.add(map);
				Utility.printLog("Multithread", "processmessage", "Response", map.toString() + "\n");
			}
		*/	
			
		//	Utility.printLog("Multithread", "processmessage", "Response", message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
