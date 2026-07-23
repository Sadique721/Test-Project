package masterdata;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Class to store City, and associated Pincodes.
 */
public class Location6Ids {
    private int cityId;
    private List<Integer> pincodeIds = new ArrayList<>();
    private List<String> pincodeNames = new ArrayList<>();

    public Location6Ids(int cityId) {
        this.cityId = cityId;
    }

    public void addPincode(int id, String name) {
        if (!pincodeIds.contains(id)) { // prevent duplicates
            pincodeIds.add(id);
            pincodeNames.add(name);
        }
    }

    public int getCityId() { return cityId; }

    public List<Integer> getPincodeIds() { return pincodeIds; }
    public List<String> getPincodeNames() { return pincodeNames; }

    public Map<Integer, String> getPincodeMap() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < pincodeIds.size(); i++) {
            map.put(pincodeIds.get(i), pincodeNames.get(i));
        }
        return map;
    }


}


