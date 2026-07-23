package masterdata;

public class Location4Ids {
    private final int countryId;
    private final int stateId;
    private final int cityId;
    private final int pincodeId;

    public Location4Ids(int countryId, int stateId, int cityId, int pincodeId) {
        this.countryId = countryId;
        this.stateId = stateId;
        this.cityId = cityId;
        this.pincodeId = pincodeId;
    }

    public int getCountryId() { return countryId; }
    public int getStateId() { return stateId; }
    public int getCityId() { return cityId; }
    public int getPincodeId() {return pincodeId; }

    @Override
    public String toString() {
        return "CountryID: " + countryId + ", StateID: " + stateId + ", CityID: " + cityId + ", PincodeId: " + pincodeId;
    }
}
