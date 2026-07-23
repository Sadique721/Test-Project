package masterdata;

import java.util.List;
import java.util.Map;

import utility.Constant;
import utility.ModuleControlConstant;
import utility.ReadWriteExcelFile;
import utility.Utility;

public class CreateMasterData {

    private void createCountry() {
        if (ModuleControlConstant.COUNTRY) {
            Country country = new Country();
            List<Map<String, String>> countryMapList = country.readCountryList();
            country.createCountry(countryMapList);
        }
    }

    private void createProvince() {
        if (ModuleControlConstant.PROVINCE) {
            Province province = new Province();
            List<Map<String, String>> provinceMapList = province.readProvinceList();
            province.createProvince(provinceMapList);
        }
    }

    private void createDistrict() {
        if (ModuleControlConstant.DISTRICT) {
            District district = new District();
            List<Map<String, String>> districtMapList = district.readDistrictList();
            district.createDistrict(districtMapList);
        }
    }

    private void createMuncipility() {
        if (ModuleControlConstant.MUNCIPILITY) {
            Municipality municipality = new Municipality();
            List<Map<String, String>> municipalitiesMapList = municipality.readMunicipalityList();
            municipality.createMunicipality(municipalitiesMapList);
        }
    }

    private void createServiceArea() {
        if (ModuleControlConstant.SERVICEAREA) {
            ServiceArea serviceArea = new ServiceArea();
            List<Map<String, String>> serviceAreaMapList = serviceArea.readServiceAreaList();
            serviceArea.createServiceArea(serviceAreaMapList);
        }
    }

    private void createServiceAreaClass() {
        if (ModuleControlConstant.SERVICEAREACLASS) {
            ServiceAreaClass serviceArea = new ServiceAreaClass();
            List<Map<String, String>> serviceAreaMapList = serviceArea.readServiceAreaList();
            serviceArea.createServiceAreaClass(serviceAreaMapList);
        }
    }

    private void createWard() {

        if (ModuleControlConstant.WARD) {
            //Ward ward = new Ward();
            NewWard ward = new NewWard();
            List<Map<String, String>> wardMapList = ward.readWardList();
            ward.createWard(wardMapList);
        }
    }

    private void createWardClass() {

        if (ModuleControlConstant.WARDCLASS) {
            //Ward ward = new Ward();
            WardClass ward = new WardClass();
            List<Map<String, String>> wardMapList = ward.readWardList();
            ward.createWardClass(wardMapList);
        }
    }

    private void createInvestmentCode() {
        if (ModuleControlConstant.INVESTMENTCODE) {
            InvestmentCode investmentCode = new InvestmentCode();
            List<Map<String, String>> investmentCodeMapList = investmentCode.readInvestmentCodeList();
            investmentCode.createInvestmentCode(investmentCodeMapList);
        }
    }

    private void createBranch() {
        if (ModuleControlConstant.BRANCH) {
            Branch branch = new Branch();
            List<Map<String, String>> branchMapList = branch.readBranchList();
            branch.createBranch(branchMapList);
        }
    }

    private void createBusinessUnit() {
        if (ModuleControlConstant.BUSINESSUNIT) {
            BusinessUnit businessUnit = new BusinessUnit();
            List<Map<String, String>> businessUnitMapList = businessUnit.readBusinessUnitList();
            businessUnit.createBusinessUnit(businessUnitMapList);
        }
    }


    private void createSubBusinessUnit() {
        if (ModuleControlConstant.SUBBUSINESSUNIT) {
            SubBusinessUnit subBusinessUnit = new SubBusinessUnit();
            List<Map<String, String>> SubBUMapList = subBusinessUnit.readSubBusinessUnitList();
            subBusinessUnit.createSubBusinessUnit(SubBUMapList);
        }
    }

    private void createRegion() {
        if (ModuleControlConstant.REGION) {
            Region region = new Region();
            List<Map<String, String>> regionMapList = region.readRegionList();
            region.createRegion(regionMapList);
        }
    }

    private void createBusinessVertical() {
        if (ModuleControlConstant.BUSINESSVERTICAL) {
            BusinessVertical businessVertical = new BusinessVertical();
            List<Map<String, String>> businessVerticalMapList = businessVertical.readBusinessVerticalList();
            businessVertical.createBusinessVertical(businessVerticalMapList);
        }
    }

    private void createSubBusinessVertical() {
        if (ModuleControlConstant.SUBBUSINESSVERTICAL) {
            SubBusinessVertical subBusinessVertical = new SubBusinessVertical();
            List<Map<String, String>> subBVMapList = subBusinessVertical.readSubBusinessVerticalList();
            subBusinessVertical.createSubBusinessVertical(subBVMapList);
        }
    }


    // new development
    private void createSubArea() {

        if (ModuleControlConstant.SUBAREA) {
            //Ward ward = new Ward();
            SubArea subArea = new SubArea();
            List<Map<String, String>> subAreaMapList = subArea.readSubAreaList();
            subArea.createSubArea(subAreaMapList);
        }
    }

//    private void createSubAreaClass() {
//
//        if (ModuleControlConstant.SUBAREACLASS) {
//            //Ward ward = new Ward();
//            SubAreaClass subArea = new SubAreaClass();
//            List<Map<String, String>> subAreaMapList = subArea.readSubAreaList();
//            subArea.createSubAreaClass(subAreaMapList);
//        }
//    }

    private void createBuildingMangement() {

        if (ModuleControlConstant.BUILDING) {
            //Ward ward = new Ward();
            //	BuildingMangament building = new BuildingMangament();
            NewBuilding building = new NewBuilding();
            List<Map<String, String>> buildingMapList = building.readBuildingList();
            building.processBuildingList(buildingMapList);
        }
    }

    private void createBuildingMangementClass() {

        if (ModuleControlConstant.BUILDINGCLASS) {
            //Ward ward = new Ward();
            //	BuildingMangament building = new BuildingMangament();
            createSubAreasWithHomePassClass homePass = new createSubAreasWithHomePassClass();
            List<Map<String, String>> buildingMapList = homePass.readSubAreaHomePassList();
            homePass.createSubAreasWithHomePass(buildingMapList);
        }
    }

    private void createSubAreaHomePassClass() {

        if (ModuleControlConstant.BUILDINGHOMEPASSCLASS) {
            //Ward ward = new Ward();
            //	BuildingMangament building = new BuildingMangament();
            createSubAreasWithHomePassClass homePass = new createSubAreasWithHomePassClass();
            List<Map<String, String>> buildingMapList = homePass.readSubAreaHomePassList();
            homePass.createSubAreasWithHomePass(buildingMapList);
        }
    }

    private void createDep_Management() {
        if (ModuleControlConstant.DEPARTMENT_MANAGEMENT) {
            Department department = new Department();
            List<Map<String, String>> departmentMapList = department.readDepartmentList();
            department.createDep_Management(departmentMapList);
        }
    }

    private void createOLT() {
        if (ModuleControlConstant.OLT) {
            OLT OLT = new OLT();
            List<Map<String, String>> OLTMapList = OLT.readOLTList();
            OLT.createOLT(OLTMapList);
        }
    }


    public void generateMasterData() {
        try {
            String fileName = Constant.MASTERDATA_FILE;
            ReadWriteExcelFile rwe = new ReadWriteExcelFile();
            rwe.isExcelFileOpen(fileName);

            System.out.println("Started to generate Master Data ...!");
            Utility.printLog("execution.log", "MasterData", "Started Generting Master Data...!", "");

            createCountry();
            createProvince();
            createDistrict();
            createMuncipility();
            createWard();
            createWardClass();
            createServiceArea(); // how to remove each call
            createServiceAreaClass();

            createInvestmentCode();
            createBranch();
            createBusinessUnit();
            createSubBusinessUnit();
            createRegion();
            createBusinessVertical();
            createSubBusinessVertical();
            //createLocation();
            // new development
            createSubArea();
//            createSubAreaClass();
            createSubAreaHomePassClass();
            createBuildingMangementClass();
            createBuildingMangement();

            createDep_Management();

            createOLT();

            System.out.println("Ended to generate MasterData ...!");
            Utility.printLog("execution.log", "MasterData", "Ended Generting Master Data...!", "");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getting error in this method (generateMasterData).... " + e.getMessage());
        }
    }
}
