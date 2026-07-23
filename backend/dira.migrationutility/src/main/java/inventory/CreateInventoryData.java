package inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import temp.SimpleThreadPool;
import utility.DBConnect;
import utility.ModuleControlConstant;
import utility.Utility;

public class CreateInventoryData {
	private static String logFileName = "inventory.log";
	private static String logModuleName = "CreateInward";
	private void createVendor() {
		if(ModuleControlConstant.MANUFACTURER) {
			Vendor vendor = new Vendor();
			List<Map<String, String>> vendorMapList = vendor.readVendorList();
			vendor.createVendor(vendorMapList);
			
		//	SimpleThreadPool stp = new SimpleThreadPool();
		//	stp.executeThreadsBatch(vendorMapList);
		}
	}
	
	private void createProductCategory() {
		if(ModuleControlConstant.PRODUCTCATEGORY) {
			ProductCategory productCategory = new ProductCategory();
			List<Map<String, String>> productCategoryMapList = productCategory.readUniqueProductCategoryList();
			productCategory.createProductCategory(productCategoryMapList);
		}
	}

	private void createProduct() {
		if(ModuleControlConstant.PRODUCT) {
			Product product = new Product();
			List<Map<String, String>> productMapList = product.readUniqueProductList();
			product.createProduct(productMapList);
		}
	}
	
	private void createPop() {
		if(ModuleControlConstant.POP) {
			POP pop = new POP();
			List<Map<String, String>> popMapList = pop.readUniquePopList();
			pop.createPop(popMapList);
		}
	}
	
	private void assignInventoryToPop() {
		if(ModuleControlConstant.ASSIGN_INVENTORY_POP) {
			AssignInventoryToPOP assignInventoryToPOP = new AssignInventoryToPOP();
			List<Map<String, String>> assigningInventoryMapList = assignInventoryToPOP.readAssignInventoryToPopList();
			assignInventoryToPOP.assignInventory(assigningInventoryMapList);
		}
	}
	
	private void assignInventoryToServiceArea() {
		if(ModuleControlConstant.ASSIGN_INVENTORY_SERVICEAREA) {
			AssignInventoryToServiceArea assignInventoryToServiceArea = new AssignInventoryToServiceArea();
			List<Map<String, String>> assigningInventoryMapList = assignInventoryToServiceArea.readAssignInventoryToSAList();
			assignInventoryToServiceArea.assignInventory(assigningInventoryMapList);
		}
	}

	private void createWarehouse() {
		if(ModuleControlConstant.WAREHOUSE) {
			Warehouse warehouse = new Warehouse();
			List<Map<String, String>> warehouseMapList = warehouse.readUniqueWarehouseList();
			warehouse.createWarehouse(warehouseMapList);
		}
	}

	private void createInward() {
		if(ModuleControlConstant.INWARD) {
			Inward inward = new Inward();
			List<Map<String, String>> inwardMapList = inward.readUniqueInwardList();
			Utility.printLog(logFileName, logModuleName, "Sheet Data - macSerialMapping", inwardMapList.toString());
			inward.createInward(inwardMapList);
		}
	}

	private void createOutward() {
		if(ModuleControlConstant.OUTWARD) {
			Outward outward = new Outward();
			List<Map<String, String>> outwardMapList = outward.readUniqueOutwardList();
			outward.createOutward(outwardMapList);
		}
	}

	
	public void generateInventoryData() {
		System.out.println("Started to generate Inventory Data ...!");
		Utility.printLog("execution.log", "InventoryData", "Started Generting Inventory Data...!","");
		
		String query = "delete from status where entitytype='outward'";
	//	DBConnect db = new DBConnect();
	//	db.executeQuery(query);
		
		createVendor();
		createProductCategory();
		createProduct();
		createPop();
		createWarehouse();
		createInward();
		createOutward();
		
		assignInventoryToPop();
		assignInventoryToServiceArea();
		
		
		System.out.println("Ended to generate Inventory Data ...!");
		Utility.printLog("execution.log", "InventoryData", "Ended Generting Inventory Data...!","");
	}
}
