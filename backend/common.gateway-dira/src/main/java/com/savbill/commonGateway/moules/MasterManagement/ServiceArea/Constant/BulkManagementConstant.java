package com.savbill.commonGateway.moules.MasterManagement.ServiceArea.Constant;

public class BulkManagementConstant {

    public interface DropdownStatus {
        public final String ACTIVE = "Active";
        public final String INACTIVE = "Inactive";
        public final String PRIVATE = "private";
        public final String PUBLIC = "public";
        public final String UNDER_DEVELOPMENT = "UnderDevelopment";
    }

    public interface EntityName {
        public final String CITY = "City";
        public final String LOCATION = "Location";
        public final String PINCODE = "Pincode";
    }

    public interface MapData {
        public final String CITYID = "cityId";
        public final String CITY_NAME = "cityName";
        public final String LOCATION_ID = "locationId";
        public final String LOCATION_NAME = "locationName";
        public final String PINCODE_ID = "pincodeId";
        public final String PINCODE = "pincode";
        public final String SITE_NAME = "siteName";
    }

    public interface ColumnName {
        public final String STATUS = "status";
        public final String CITYID= "cityid";
        public final String SERVICEAREA_TYPE = "service_area_type";
        public final String SITENAME = "site_name";
        public final String LOCATION = "locationid";
        public final String PINCODE = "pincodeid";

        public final String RADIUS = "radius";

        public final String BLOCK_NUMBER = "blockno";
    }

    public interface SourceMasterColumn {
        public final String NAME = "name";
        public final String STATUS = "status";
        public final String LATITUDE = "latitude";
        public final String LONGITUDE = "longitude";
        public final String CITYID = "cityid";
        public final String RADIUS = "radius";
        public final String SITE_NAME = "site_name)";
        public final String SERVICEAREA_TYPE = "service_area_type";
        public final String PINCODE = "pincode";
        public final String PINCODE_ID = "pincodeid";
        public final String LOCATION = "location";
        public final String LOCATION_ID = "locationid";
        public final String UNIT_NUMBER = "blockno";
    }

    public interface SheetNames{
        String SERVICE_AREA_SHEET = "ServiceAreaSheet";
        String CITY_SHEET = "CitySheet";
        String PINCODE_SHEET = "PincodeSheet";
        String SITE_SHEET = "SiteSheet";
        String CITY_PINCODE_SHEET = "CityPincodeSheet";
        String LOCATION_SHEET = "LocationSheet";
    }
}
