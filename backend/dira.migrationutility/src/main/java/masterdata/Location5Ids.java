package masterdata;

public class Location5Ids {

    private int cityId;
    private int pincodeId;
    private int wardId;
    private int stateId;
    private int countryId;


    public Location5Ids(int cityId, int pincodeId, int wardId, int stateId, int countryId) {

        this.cityId = cityId;
        this.pincodeId = pincodeId;
        this.wardId = wardId;
        this.stateId = stateId;
        this.countryId = countryId;

    }

    // getters

    public int getCityId() {
        return cityId;
    }

    public int getPincodeId() {
        return pincodeId;
    }

    public int getWardId() { return wardId; }

    public int getStateId() { return stateId; }

    public int getCountryId() { return countryId; }

    @Override
    public String toString() {
        return " CityID: " + cityId + ", PincodeId: " + pincodeId + ", WardId: " + wardId + ", StateId: " + stateId + ", CountryId: " + countryId;
    }
}

