package temp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utility.Constant;
import utility.Utility;

public class SimpleThreadPool {
	
	
	public void executeThreadsBatch(List<Map<String, String>> list) {
		
		int total_threads = Constant.TOTAL_THREADS;
		System.out.println("Total_Threads =" + total_threads);
		List<Map<String, String>> batchList = new ArrayList<Map<String, String>>();
		ExecutorService executor = Executors.newFixedThreadPool(total_threads);// creating a pool of 5 threads
		
		int totalRows = list.size();
		int batchSize = 0;
		
		if(totalRows > 10000) { // here 10000 but intile that 21 accrding to sheet
			batchSize = 10;  // here is 10 i intilize that in 2
		} else if (totalRows > 1000) {
			batchSize = 50;
		} else {
			batchSize = 21;
		}
		
		System.out.println("Total Rows = " + totalRows);
		System.out.println("Row BatchSize = " + batchSize);
		
		int size = (totalRows) / batchSize;
		int modulo = (totalRows) % batchSize;
		
		if(modulo>0) {
			size = size + 1;
		}
		
		for (int i = 0; i < size; i++) {
			batchList = batchwiseList(i,size,modulo,batchSize,list);
			Runnable worker = new WorkerThread("" + i, batchList);
			executor.execute(worker);// calling execute method of ExecutorService
		}
		
		//System.out.println("*********Generated all threads*********");
		Tasks2 t2 = new Tasks2("task 1");
		t2.setDaemon(true);
		t2.start();
		
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		//Utility.printLog("Multithread.log", "processmessage", "Last", "*********");
		t2.updateSheet1(); // Keep last call here
		System.out.println("Finished all threads");
		
	}
	
	private List<Map<String, String>> batchwiseList(int i,int size,int modulo, int batchSize,List<Map<String, String>> list) {
		
		List<Map<String, String>> batchList = new ArrayList<Map<String, String>>();
		
		int start = i*batchSize;
		int end = start + batchSize;
		
		if(i < (size-1)) {
			batchList = createBatchList(list,start,end);
		} else {
			end = start + modulo;
			if(start==0 && modulo==0) {
				end = batchSize;
			}
			batchList = createBatchList(list,start,end);
		}
		//System.out.println("batchList=" + batchList.toString());
		return batchList;
	}
	
	private List<Map<String, String>> createBatchList(List<Map<String, String>> list,int start,int end) {
		
		List<Map<String, String>> batchList = new ArrayList<Map<String, String>>();
		for (int i = start; i < end; i++) {

			Map<String, String> map = new HashMap<String, String>();
			map = list.get(i);
			batchList.add(map);
		}
		return batchList;
	}
	
	
}