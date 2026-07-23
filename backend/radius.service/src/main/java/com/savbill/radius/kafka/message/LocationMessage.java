package com.savbill.radius.kafka.message;

import com.savbill.radius.entity.LocationMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationMessage {
	private Map<String, Object> locationMasterData;
	private boolean isUpdate;
	private boolean isDelete;

	public LocationMessage(LocationMaster locationMaster, boolean isUpdate, boolean isDelete) {
		Map<String,Object> map = new HashMap<>();
		map.put("locationMasterId",locationMaster.getLocationMasterId());
		map.put("name", locationMaster.getName());
		map.put("checkItem", locationMaster.getCheckItem());
		map.put("status", locationMaster.getStatus());
		map.put("mvnoId",locationMaster.getMvnoId());
		map.put("locationIdentifyAttribute",locationMaster.getLocationIdentifyAttribute());
		this.setLocationMasterData(map);
		this.isUpdate = isUpdate;
		this.isDelete=isDelete;
	}

}
