package temp;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utility.Constant;
import utility.Utility;
public class ListOfFiles {
   public static void main(String args[]) {
     
	   //listFileDirectory();
	   //S3-20210111-333637
	   //S3-20190527-082758
	   //S3-20190527-082759
	  // String path = "C:\\Users\\AmitPrajapati\\OneDrive - RAH Infotech Pvt. Ltd\\Documents\\Arhant_Delievery\\Document\\aryan";
	 //  List<String> fileNames = listFiles("S3-20190527-082758");
	 //  System.out.println("files = " + fileNames.toString());
	
	/*   String billTo = "CUSTOMER";
	   String tempDiscountPercentage = "";
	   if ((billTo.equalsIgnoreCase("CUSTOMER")) && (!"".equals(tempDiscountPercentage))) {
		   System.out.print("It is not empty");
	   }else  if ((billTo.equalsIgnoreCase("CUSTOMER")) && ("".equals(tempDiscountPercentage))) {
		   System.out.print("It is empty");
	   }
	  */
	   
	  
   }
   
   
   private static List<String> listFiles(String accountNumber) {
	   
	   List<String> fileNames = new ArrayList<String>();
	   try {
		   String basePath =  Constant.BASE_PATH + "\\TestData\\input\\uploads\\document\\";
		   String newPath = basePath + "\\"+ accountNumber;
	       File directoryPath1 = new File(newPath);
	       
	       if(directoryPath1.exists()) {
	    	   File filesList[] = directoryPath1.listFiles();
	 	      // System.out.println("List of files and directories in the specified directory:");
	 	       System.out.println("\tNumber of files : "+filesList.length);
	 	       for(File file : filesList) {
	 	    	   String fileName = file.getName();	 	    	   
	 	    	   fileNames.add(fileName);
	 	    	  System.out.println("\tFile name: "+fileName);
	 	       }
	       }else {
	    	   System.out.println("Specified directory does not exist");
	       }
	       
	      
	   } catch(NullPointerException ne) {
		   System.out.println("catch Specified directory does not exist");
	   }
	   return fileNames;
   }
   
   
   private static void listFileDirectory() {
	   
	   //Creating a File object for directory
	   String path = "C:\\Users\\AmitPrajapati\\OneDrive - RAH Infotech Pvt. Ltd\\Documents\\Arhant_Delievery\\Document\\aryan";
	   File directoryPath = new File(path);
      //List of all files and directories
      String contents[] = directoryPath.list();
      System.out.println("List of files and directories in the specified directory:");
      for(int i=0; i<contents.length; i++) {
         System.out.println(contents[i]);
         String newPath = path + "\\"+ contents[i];
         File directoryPath1 = new File(newPath);
         
         File filesList[] = directoryPath1.listFiles();
        // System.out.println("List of files and directories in the specified directory:");
         for(File file : filesList) {
            System.out.println("\tFile name: "+file.getName());
            //System.out.println("File path: "+file.getAbsolutePath());
            //System.out.println("Size :"+file.getTotalSpace());
            //System.out.println(" ");
         }
      }
	   
   }
}