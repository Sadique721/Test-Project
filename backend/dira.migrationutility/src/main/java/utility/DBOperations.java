package utility;

public class DBOperations {
	
	
	
	public String getSingleData(String query) {
		
		DBConnect db = new DBConnect();
		String data = db.selectData(query);
		return data;
	}
	
	public void setAPIData(String entityType,String entityName,int id,int status) {
		
		String logtime = Utility.getCurrentDateTime();
		
		DBConnect db = new DBConnect();
		String query = "insert into status (entitytype,name,id,status,logtime) values ('" + entityType + "','" + entityName + "'," + id + "," + status + ",'" + logtime + "')" ;
		//System.out.println("query = " + query);
		db.executeQuery(query);
		
	}
	
	public void updateAPIData(String entityType,String entityName,int id,int status) {
		
			String logtime = Utility.getCurrentDateTime();
			
			DBConnect db = new DBConnect();
			String query = "update status set id="+id+" where entitytype='" + entityType +"' and name='"+entityName+"'";
			db.executeQuery(query);
			
		}
}
