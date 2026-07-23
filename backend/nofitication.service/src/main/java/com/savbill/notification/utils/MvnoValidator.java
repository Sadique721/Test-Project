package com.savbill.notification.utils;

public class MvnoValidator 
{

	private MvnoValidator()
	{
		
	}
	
	/**The mvnoId argument refers to the mvnoId of that record  chosen to update,delete,change password,etc., **/
	
	public static boolean validateMvno(long userMvnoId,long mvnoId)
	{
		boolean validator=false;
		
	     	if(userMvnoId == 1)  /** means User is SuperAdmin , allow him to proceed**/
		     {
		        validator=false;
		     }
		
		    else  /**  means User is Admin **/
		     {
		          	/** check if Admin is modifying only data created by him **/
			      if(mvnoId==1) /** this mvnoId is of Record to modify, 1 means it is record created by SuperAdmin which is being modified by Admin user,DONT ALLOW this **/
			      {
			         validator=true;
			      }
			     else 
			      {
				    validator= false;
			      }	  	
		     }
	 
	  return validator;  	
	}

}
