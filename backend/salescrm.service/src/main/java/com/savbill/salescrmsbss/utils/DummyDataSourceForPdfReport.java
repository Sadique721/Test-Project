package com.savbill.salescrmsbss.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class DummyDataSourceForPdfReport {

	public static List<JSONObject> getListOfObjectForTable1() {
		JSONObject deep = new JSONObject();
		List<JSONObject> list = new ArrayList<>();
		deep.put("Emp Id", "1L");
		deep.put("Emp Name", "Deep");
		deep.put("Emp Age", "23L");
		list.add(deep);
		JSONObject suresh = new JSONObject();
		suresh.put("Emp Id", "2L");
		suresh.put("Emp Name", "Suresh");
		suresh.put("Emp Age", "24L");
		list.add(suresh);
		JSONObject hardik = new JSONObject();
		hardik.put("Emp Id", "3L");
		hardik.put("Emp Name", "Hardik");
		hardik.put("Emp Age", "25L");
		list.add(hardik);
		JSONObject mahesh = new JSONObject();
		mahesh.put("Emp Id", "4L");
		mahesh.put("Emp Name", "Mahesh");
		mahesh.put("Emp Age", "26L");
		list.add(mahesh);
		return list;
	}

	public static List<JSONObject> getListOfObjectForTable2() {
		JSONObject deep = new JSONObject();
		List<JSONObject> list = new ArrayList<>();
		deep.put("Emp Id", "1L");
		deep.put("Emp Name", "Deep");
		deep.put("Emp Age", "23L");
		deep.put("Emp Salary", "20000.0");
		list.add(deep);
		JSONObject suresh = new JSONObject();
		suresh.put("Emp Id", "2L");
		suresh.put("Emp Name", "Suresh");
		suresh.put("Emp Age", "24L");
		suresh.put("Emp Salary", "30000.0");
		list.add(suresh);
		JSONObject hardik = new JSONObject();
		hardik.put("Emp Id", "3L");
		hardik.put("Emp Name", "Hardik");
		hardik.put("Emp Age", "25L");
		hardik.put("Emp Salary", "40000.0");
		list.add(hardik);
		JSONObject mahesh = new JSONObject();
		mahesh.put("Emp Id", "4L");
		mahesh.put("Emp Name", "Mahesh");
		mahesh.put("Emp Age", "26L");
		mahesh.put("Emp Salary", "50000.0");
		list.add(mahesh);
		return list;
	}

}
