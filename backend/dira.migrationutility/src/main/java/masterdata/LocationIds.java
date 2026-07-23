package masterdata;

public class LocationIds {
    private int countryId;
    private int stateId;
    private int cityId;

    public LocationIds(int countryId, int stateId, int cityId) {
        this.countryId = countryId;
        this.stateId = stateId;
        this.cityId = cityId;
    }

    public int getCountryId() { return countryId; }
    public int getStateId() { return stateId; }
    public int getCityId() { return cityId; }

    @Override
    public String toString() {
        return "CountryID: " + countryId + ", StateID: " + stateId + ", CityID: " + cityId;
    }
}
