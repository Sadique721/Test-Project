package masterdata;

public class Location2Ids {

    private int cityId;
    private int pincodeId;


    public Location2Ids( int cityId, int pincodeId) {
        this.cityId = cityId;
        this.pincodeId = pincodeId;

    }

    // getters

    public int getCityId() {
        return cityId;
    }

    public int getPincodeId() {
        return pincodeId;
    }
}

