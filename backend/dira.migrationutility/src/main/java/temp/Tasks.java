package temp;

import java.text.SimpleDateFormat;
import java.util.Date;

class Tasks implements Runnable {
	private String taskName;

//constructor of the class Tasks  
	public Tasks(String str) {
//initializing the field taskName   
		taskName = str;
	}

//Printing the task name and then sleeps for 1 sec  
//The complete process is getting repeated five times  
	public void run() {
		try {
			for (int j = 0; j <= 5; j++) {
				if (j == 0) {
					Date dt = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("hh : mm : ss");

//prints the initialization time for every task  
					System.out.println("Initialization time for the task name: " + taskName + " = " + sdf.format(dt));

				} else {
					Date dt = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("hh : mm : ss");

//prints the execution time for every task  
					System.out.println("Time of execution for the task name: " + taskName + " = " + sdf.format(dt));

				}

//1000ms = 1 sec  
				Thread.sleep(1000);
			}

			System.out.println(taskName + " is complete.");
		}

		catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}