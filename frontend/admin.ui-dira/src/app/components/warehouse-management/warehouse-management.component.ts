import { Component, OnInit, ViewChild, Inject, TemplateRef } from "@angular/core";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { FormGroup, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import {
    ITEMS_PER_PAGE,
    PER_PAGE_ITEMS,
    pageLimitOptions
} from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { LoginService } from "src/app/service/login.service";
import { WarhouseManagementService } from "src/app/service/warhouse-management.service";
import { COUNTRY, CITY, STATE, PINCODE, AREA } from "src/app/RadiusUtils/RadiusConstants";
import { serviceArea } from "src/app/components/model/serviceArea";
import { element } from "protractor";
import { PincodeManagementService } from "src/app/service/pincode-management.service";
import { Observable, Observer } from "rxjs";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { INVENTORYS } from "src/app/constants/aclConstants";
import { ServiceAreaService } from "src/app/service/service-area.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ToastrService } from 'ngx-toastr';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";


declare var $: any;

declare module '@angular/forms' {
    interface FormGroup {
        pick(keys: string[]): FormGroup;
    }
}

FormGroup.prototype.pick = function (keys: string[]): FormGroup {
    const group: { [key: string]: any } = {};

    keys.forEach(k => {
        const control = this.get(k);
        if (control) {
            group[k] = new (control.constructor as any)(control.value, control.validator, control.asyncValidator);
        }
    });

    return new FormGroup(group);
};
@Component({
    selector: "app-warehouse-management",
    templateUrl: "./warehouse-management.component.html",
    styleUrls: ["./warehouse-management.component.css"],
    standalone: false
})
export class WarehouseManagementComponent implements OnInit {
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild("closebutton") closebutton;
    @ViewChild('stepper') stepper: any;
    countryTitle = COUNTRY;
    cityTitle = CITY;
    stateTitle = STATE;
    pincodeTitle = PINCODE;
    areaTitle = AREA;
    wareHouseFormGroup: UntypedFormGroup;
    // countryFormArray: FormArray;
    submitted: boolean = false;
    stateData: any = {};
    countryListData: any;
    currentPageProductListdata = 1;
    productListdataitemsPerPage = PER_PAGE_ITEMS;
    productListdatatotalRecords: any;
    countryPojo: any = {};
    warehouseListData: any[] = [];
    viewCountryListData: any;
    viewStateListData: any;
    isStateEdit: boolean = false;
    searchLocationModal: boolean = false;
    servicearealists: boolean = false;
    parentservicearealists: boolean = false;
    teamlists: boolean = false;
    searchData: any;
    searchWarehouseName: any = "";
    AclClassConstants: any;
    AclConstants: any;
    pageLimitOptions = pageLimitOptions;
    showItemPerPage = 5;
    searchkey: string;
    public loginService: LoginService;
    editData: serviceArea;
    editMode: boolean;
    selectWareHouseView: boolean;
    pincodeDeatils: any;
    status = [
        { label: "Active", value: "ACTIVE" },
        { label: "Inactive", value: "INACTIVE" }
    ];
    createView: boolean = false;
    viewWarehouseDetails: any;
    listview: boolean = true;
    detailView: boolean = false;
    iflocationFill: boolean;
    ifsearchLocationModal: boolean;
    isServiceareaBasedBranch: boolean;
    currentPagesearchLocationList: number;
    searchLocationForm: UntypedFormGroup;
    searchLocationData: any = [];
    searchLocationItemPerPage = ITEMS_PER_PAGE;
    searchLocationtotalRecords: number;
    countryList = [];
    stateList = [];
    cityList = [];
    pincodeList = [];
    pincodes: any[] = [];
    branchId: any[] = [];
    WareHouseType: any[] = [];
    allpincodeNumber: any = [];
    serviceAreaList: any = [];
    parentServiceArea: any = [];
    serviceAreaDataListByBarnchId: any = [];
    branchList: any[] = [];
    allTeanBasedonStaff: any = [];
    ifServiceAreaListShow: boolean;
    ifParentServiceAreaListShow: boolean;
    ifTeamListShow: boolean;
    editAccess: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    productWarehouseMappingDTOS: UntypedFormArray;
    productWarehouseMappingDTO: UntypedFormGroup;
    displayedColumns = [
        "name",
        "warehouseType",
        "address1",
        "address2",
        "status",
        "action"
    ];
    dataSource = new MatTableDataSource([]);
    searchOption: any = "";
    // step1Group: UntypedFormGroup;
    // step2Group: UntypedFormGroup;
    // step3Group: UntypedFormGroup;
    // step4Group: UntypedFormGroup;
    constructor(
        private toastr: ToastrService,

        private fb: UntypedFormBuilder,
        private formBuilder: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private warehouseService: WarhouseManagementService,
        private serviceAreaService: ServiceAreaService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        loginService: LoginService,
        public commondropdownService: CommondropdownService,
        private customerManagementService: CustomermanagementService,
        private pincodeService: PincodeManagementService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar
    ) {
        this.createAccess = loginService.hasPermission(INVENTORYS.WAREHOUSE_CREATE);
        this.deleteAccess = loginService.hasPermission(INVENTORYS.WAREHOUSE_DELETE);
        this.editAccess = loginService.hasPermission(INVENTORYS.WAREHOUSE_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.editMode = !this.createAccess && this.editAccess ? true : false;
        // this.getAllWarehouseType();
        // this.getAllParantServiceArea();
        // this.getServiceArea();
        // this.warehouseService.getAllBranch().subscribe((res: any) => {
        //   this.branchList = res.dataList;
        // });
    }

    ngOnInit(): void {
        // this.commondropdownService.getCountryList();
        // this.commondropdownService.getStateList();
        // this.commondropdownService.getCityList();
        // this.commondropdownService.getAllPinCodeNumber();
        // this.commondropdownService.getAllPinCodeData();
        // this.getAllTeamsBasedonStaff();
        // this.countryList = this.commondropdownService.countryListData;
        // this.stateList = this.commondropdownService.stateListData;
        // this.cityList = this.commondropdownService.cityListData;
        // this.pincodeList = this.commondropdownService.allpincodeNumber;
        this.productWarehouseMappingDTO = this.fb.group({
            productId: ["", Validators.required],
            warehouseId: [""],
            thresholdQty: ["", Validators.required],
            mvnoId: [""],
            unit: [""]
        });
        this.wareHouseFormGroup = this.fb.group({
            id: [""],
            name: ["", Validators.required],
            warehouseCode: [""],
            status: ["", Validators.required],
            description: ["", Validators.required],
            //Warehouse: ["", Validators.required],
            parentArea: [""],
            address1: ["", Validators.required],
            address2: ["", Validators.required],
            pincode: ["", Validators.required],
            state: ["", Validators.required],
            city: ["", Validators.required],
            country: ["", Validators.required],
            teamsIdsList: ["", Validators.required],
            latitude: [""],
            longitude: [""],
            mvnoId: [""],
            serviceAreaIdsList: ["", Validators.required],
            serviceAreaNameList: [""],
            parentServiceAreaIdsList: [""],
            warehouseType: ["", Validators.required],
            branchId: [""],
            productWarehouseMappingDTOS: this.fb.array([])
        });
        this.searchLocationForm = this.fb.group({
            searchLocationname: ["", Validators.required]
        });
        this.productWarehouseMappingDTOS = this.wareHouseFormGroup.get(
            "productWarehouseMappingDTOS"
        ) as UntypedFormArray;
        this.searchData = {
            filter: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ]
        };
        // this.step1Group = this.fb.group({
        //     name: this.wareHouseFormGroup.get('name'),
        //     warehouseCode: this.wareHouseFormGroup.get('warehouseCode'),
        //     warehouseType: this.wareHouseFormGroup.get('warehouseType'),
        //     status: this.wareHouseFormGroup.get('status'),
        //     description: this.wareHouseFormGroup.get('description')
        // });

        // this.step2Group = this.fb.group({
        //     serviceAreaIdsList: this.wareHouseFormGroup.get('serviceAreaIdsList'),
        //     address1: this.wareHouseFormGroup.get('address1'),
        //     address2: this.wareHouseFormGroup.get('address2'),
        //     pincode: this.wareHouseFormGroup.get('pincode'),
        //     city: this.wareHouseFormGroup.get('city'),
        //     state: this.wareHouseFormGroup.get('state'),
        //     country: this.wareHouseFormGroup.get('country'),
        //     latitude: this.wareHouseFormGroup.get('latitude'),
        //     longitude: this.wareHouseFormGroup.get('longitude')
        // });

        // this.step3Group = this.fb.group({
        //     parentServiceAreaIdsList: this.wareHouseFormGroup.get('parentServiceAreaIdsList'),
        //     branchId: this.wareHouseFormGroup.get('branchId'),
        //     teamsIdsList: this.wareHouseFormGroup.get('teamsIdsList')
        // });

        // this.step4Group = this.fb.group({
        //     productWarehouseMappingDTOS: this.wareHouseFormGroup.get('productWarehouseMappingDTOS')
        // });
        this.getWareHouseList("");
        // const serviceArea = localStorage.getItem("serviceArea");
        // let serviceAreaArray =JSON.parse(serviceArea);
        // if (serviceAreaArray.length !== 0) {
        //   this.commondropdownService.filterserviceAreaList();
        // } else {
        //   this.commondropdownService.getserviceAreaList();
        // }
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INVENTORYS.WAREHOUSE_DELETE) || this.loginService.hasPermission(INVENTORYS.WAREHOUSE_EDIT)) {
            return [
                "name",
                "warehouseType",
                "address1",
                "address2",
                "status",
                "action"
            ];
        } else {
            return [
                "name",
                "warehouseType",
                "address1",
                "address2",
                "status"];
        }
    }
    get step1Group(): FormGroup {
        return this.wareHouseFormGroup.pick(['name', 'warehouseCode', 'warehouseType', 'status', 'description']);
    }

    get step2Group(): FormGroup {
        return this.wareHouseFormGroup.pick(['serviceAreaIdsList', 'address1', 'address2', 'pincode', 'city', 'country', 'latitude', 'longitude']);
    }
    get step3Group(): FormGroup {
        return this.wareHouseFormGroup.pick(['parentServiceAreaIdsList', 'branchId', 'teamsIdsList']);
    }
    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    onPageChange(event: PageEvent): void {
        this.currentPageProductListdata = event.pageIndex + 1;
        this.productListdataitemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.getWareHouseList(this.productListdataitemsPerPage);
        } else {
            this.searchWareHouse();
        }
    }
    getProductNameById(id: string): string {
        const product = this.commondropdownService.activeProductList.find(p => p.id === id);
        return product ? product.name : "";
    }

    get productWarehouseMappingFormArray(): UntypedFormArray {
        return this.wareHouseFormGroup.get('productWarehouseMappingDTOS') as UntypedFormArray;
    }

    addProductWarehouseMapping(): void {
        if (this.productWarehouseMappingDTO.valid) {
            const newMapping = this.fb.group({
                productId: [this.productWarehouseMappingDTO.value.productId],
                thresholdQty: [this.productWarehouseMappingDTO.value.thresholdQty],
                unit: [this.selectedUnit],
                isEditing: [false]
            });

            this.productWarehouseMappingFormArray.push(newMapping);
            this.producttableMapping?.renderRows()
            // Clear the top form
            this.productWarehouseMappingDTO.reset();
            this.selectedUnit = "";
        }
    }
    editMapping(index: number): void {
        const row = this.productWarehouseMappingFormArray.at(index);
        row.get("isEditing")?.setValue(true); this.producttableMapping?.renderRows()
    }

    saveMapping(index: number): void {
        const row = this.productWarehouseMappingFormArray.at(index);
        row.get("isEditing")?.setValue(false);
        this.producttableMapping?.renderRows()
    }
    removeMapping(index: number): void {
        this.productWarehouseMappingFormArray.removeAt(index); this.producttableMapping.renderRows()
    }

    selectedUnit: string = "";

    onProductChange(productId: string): void {
        const selectedProduct = this.commondropdownService.activeProductList.find(
            p => p.id === productId
        );
        this.selectedUnit = selectedProduct?.productCategory?.unit || "";
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageProductListdata > 1) {
            this.currentPageProductListdata = 1;
        }
        if (!this.searchkey) {
            this.getWareHouseList(this.showItemPerPage);
        } else {
            this.searchWareHouse();
        }
        //this.getWareHouseList()
        // }
    }

    getWarehouseDetails(id) {
        const url = "/warehouseManagement/getWarhouseView/" + id;
        this.warehouseService.getMethod(url).subscribe(
            (res: any) => {
                this.viewWarehouseDetails = res.data;
                this.listview = false;
                this.createView = false;
                this.detailView = true;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');



            }
        );
    }

    WarehouseList() {
        this.listview = true;
        this.createView = false;
        this.detailView = false;
    }
    getWareHouseList(list): void {
        this.warehouseListData = [];
        let size: number;
        this.searchkey = "";
        let List = this.currentPageProductListdata;
        if (list) {
            size = list;
            this.productListdataitemsPerPage = list;
        } else {
            size = this.productListdataitemsPerPage;
        }

        let plandata = {
            page: List,
            pageSize: size
        };
        this.warehouseService.getAll(plandata).subscribe(
            (response: any) => {
                this.warehouseListData = response.dataList;
                this.dataSource = new MatTableDataSource(this.warehouseListData);
                this.productListdatatotalRecords = response.totalRecords;
                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.snackBar.open(error.error.ERROR, "Close", {
                //     duration: 3000,
                //     horizontalPosition: "end",
                //     verticalPosition: "top",
                //     panelClass: ["error-snackbar"]
                // });
            }
        );
    }
    submit() {
        Object.keys(this.wareHouseFormGroup.controls).forEach(field => {
            const control = this.wareHouseFormGroup.get(field);
            control?.markAsTouched({ onlySelf: true });

            if (control?.invalid) {
                console.error(` Invalid field: ${field}`, control.errors);
            } else {
            }
        });
        this.submitted = true;
        if (this.wareHouseFormGroup.valid) {
            if (this.editMode) {
                this.warehouseService.update(this.mapObject()).subscribe(
                    (res: any) => {
                        if (res.responseCode == 406) {
                            this.toastr.error(`${res.responseMessage}`, 'Failed')
                            // this.snackBar.open(res.responseMessage, "Close", {
                            //     duration: 3000,
                            //     horizontalPosition: "end",
                            //     verticalPosition: "top",
                            //     panelClass: ["error-snackbar"]
                            // });
                        } else {
                            this.clearSearchWarehouse();
                            this.editMode = false;
                            this.submitted = false;
                            this.toastr.success(`Successfully Updated`, 'Success!')
                            // this.snackBar.open(res.message, "Close", {
                            //     duration: 3000,
                            //     horizontalPosition: "end",
                            //     verticalPosition: "top",
                            //     panelClass: ["success-snackbar"]
                            // });
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                        // this.snackBar.open(error.error.ERROR, "Close", {
                        //     duration: 3000,
                        //     horizontalPosition: "end",
                        //     verticalPosition: "top",
                        //     panelClass: ["error-snackbar"]
                        // });
                    }
                );
            } else {
                this.warehouseService.save(this.mapObject()).subscribe(
                    (res: any) => {
                        if (res.responseCode == 406) {
                            this.toastr.error(`${res.responseMessage}`, 'Failed!')
                            // this.snackBar.open(res.responseMessage, "Close", {
                            //     duration: 3000,
                            //     horizontalPosition: "end",
                            //     verticalPosition: "top",
                            //     panelClass: ["error-snackbar"]
                            // });
                        } else {
                            this.submitted = false;
                            this.clearSearchWarehouse();
                            this.toastr.success(`Successfully Created`, 'Success!')
                            // this.snackBar.open(res.message, "Close", {
                            //     duration: 3000,
                            //     horizontalPosition: "end",
                            //     verticalPosition: "top",
                            //     panelClass: ["success-snackbar"]
                            // });
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                        // this.snackBar.open(error.error.ERROR, "Close", {
                        //     duration: 3000,
                        //     horizontalPosition: "end",
                        //     verticalPosition: "top",
                        //     panelClass: ["error-snackbar"]
                        // });
                    }
                );
            }
        }
    }
    mapObject() {
        let warehouseValues = this.wareHouseFormGroup.getRawValue();
        const mvnoId = JSON.parse(localStorage.getItem("mvnoId"));
        let warehouse = {
            id: "",
            name: "",
            warehouseCode: "",
            description: "",
            status: "",
            address1: "",
            address2: "",
            pincode: "",
            city: "",
            state: "",
            country: "",
            longitude: "",
            latitude: "",
            mvnoId: mvnoId,
            parentServiceAreaIdsList: "",
            serviceAreaIdsList: "",
            warehouseType: "",
            branchId: "",
            teamsIdsList: [],
            productWarehouseMappingDTOS: []
        };
        warehouse.id = warehouseValues.id ? warehouseValues.id : "";
        warehouse.name = warehouseValues.name;
        warehouse.warehouseCode = warehouseValues.warehouseCode;
        warehouse.description = warehouseValues.description;
        warehouse.status = warehouseValues.status;
        warehouse.address1 = warehouseValues.address1;
        warehouse.address2 = warehouseValues.address2;
        warehouse.pincode = warehouseValues.pincode;
        warehouse.city = warehouseValues.city;
        warehouse.state = warehouseValues.state;
        warehouse.country = warehouseValues.country;
        warehouse.longitude = warehouseValues.longitude;
        warehouse.latitude = warehouseValues.latitude;
        warehouse.serviceAreaIdsList = warehouseValues.serviceAreaIdsList;
        warehouse.mvnoId = JSON.parse(localStorage.getItem("mvnoId"));
        warehouse.warehouseType = warehouseValues.warehouseType;
        warehouse.parentServiceAreaIdsList = warehouseValues.parentServiceAreaIdsList;
        warehouse.branchId = warehouseValues.branchId;
        warehouse.teamsIdsList = warehouseValues.teamsIdsList;
        warehouse.productWarehouseMappingDTOS = this.productWarehouseMappingDTOS.controls
            .filter(mapping => mapping.valid) // Only include valid form groups
            .map(item => ({
                productId: item.get("productId").value,
                thresholdQty: item.get("thresholdQty").value,
                mvnoId: mvnoId,
                warehouseId: null,
                unit: item.get("unit").value
            }));

        return warehouse;
    }

    @ViewChild('servicedetailsDialog') servicedetailsDialog!: TemplateRef<any>;
    @ViewChild('teamDialog') teamDialog!: TemplateRef<any>;
    @ViewChild('ParentdetailsDialog') ParentdetailsDialog!: TemplateRef<any>;
    serviceareListShow() {
        this.ifServiceAreaListShow = true;
        this.servicearealists = true;
        this.dialog.open(this.servicedetailsDialog, {
            width: '450px',
        });
    }

    teamListShow() {
        this.dialog.open(this.teamDialog, {
            width: '450px',
        });
        this.ifTeamListShow = true;
        this.teamlists = true;
    }

    parentserviceareListShoww() {
        this.dialog.open(this.ParentdetailsDialog, {
            width: '450px',
        });

        this.parentservicearealists = true;
        this.ifParentServiceAreaListShow = true;
    }

    closeParentservicearealists() {
        this.parentservicearealists = false;
    }

    closeServicearealists() {
        this.servicearealists = false;
    }

    closeTeamlists() {
        this.teamlists = false;
    }

    ServiceAreaDetailClear() {
        this.ifServiceAreaListShow = false;
    }
    ParentServiceAreaDetailClear() {
        this.ifParentServiceAreaListShow = false;
    }
    TeamDetailClear() {
        this.ifTeamListShow = false;
    }

    editWareHouse(id) {
        this.branchList = [];

        this.editMode = true;
        this.createView = true;
        this.listview = false;
        this.detailView = false;
        this.commondropdownService.getCountryList();
        this.commondropdownService.getStateList();
        this.commondropdownService.getCityList();
        // this.commondropdownService.getAllPinCodeNumber();
        this.commondropdownService.getAllPinCodeData();
        this.getAllTeamsBasedonStaff();
        this.getAllParantServiceArea();
        this.getServiceArea();
        this.getAllWarehouseType();
        this.commondropdownService.getAllActiveProduct();
        // let warehouseEdit1 = this.warehouseListData.find(element => element.id == id);

        // let serviceAreaListdata:any[];
        // warehouseEdit.serviceAreaIdsList
        // console.log(warehouseEdit1, "data");

        const url = "/warehouseManagement/" + id;
        this.warehouseService.getMethod(url).subscribe(
            (res: any) => {
                let warehouseEdit = res.data;
                // console.log(warehouseEdit,"warehouseEdit");
                this.wareHouseFormGroup.patchValue({
                    id: warehouseEdit.id,
                    name: warehouseEdit.name,
                    warehouseCode: warehouseEdit.warehouseCode,
                    description: warehouseEdit.description,
                    status: warehouseEdit.status,
                    address1: warehouseEdit.address1,
                    address2: warehouseEdit.address2,
                    pincode: Number(warehouseEdit.pincode),
                    longitude: warehouseEdit.longitude,
                    latitude: warehouseEdit.latitude,
                    mvnoId: warehouseEdit.mvnoId,
                    state: Number(warehouseEdit.state),
                    city: Number(warehouseEdit.city),
                    country: Number(warehouseEdit.country),
                    // parentServiceAreaIdsList: warehouseEdit.parentServiceAreaIdsList,
                    // serviceAreaIdsList: warehouseEdit.serviceAreaIdsList,
                    warehouseType: warehouseEdit.warehouseType,
                    branchId: warehouseEdit.branchId,
                    teamsIdsList: warehouseEdit.teamsIdsList
                });
                this.getpincodeData(warehouseEdit.pincode);
                this.getAllParentServiceAreaListByWarehouseId(id);

                let servicAreaId = [];

                for (let k = 0; k < warehouseEdit.serviceAreaNameList.length; k++) {
                    servicAreaId.push(warehouseEdit.serviceAreaNameList[k].id);
                }

                this.serviceAreaList.forEach(element => {
                    warehouseEdit.serviceAreaNameList.forEach(e => {
                        if (e.id) {
                            if (element.id == e.id) {
                                element.flag = true;
                            }
                        } else {
                            if (element.id == e) {
                                element.flag = true;
                            }
                        }
                    });
                });


                this.wareHouseFormGroup.patchValue({ serviceAreaIdsList: servicAreaId });
                this.getPincodeListByServiceId();
                if (
                    warehouseEdit.productWarehouseMappingDTOS &&
                    warehouseEdit.productWarehouseMappingDTOS.length > 0
                ) {
                    while (this.productWarehouseMappingDTOS.length !== 0) {
                        this.productWarehouseMappingDTOS.removeAt(0);
                    }

                    warehouseEdit.productWarehouseMappingDTOS.forEach(mapping => {
                        const newMapping = this.fb.group({
                            productId: [mapping.productId],
                            thresholdQty: [mapping.thresholdQty],
                            mvnoId: [mapping.mvnoId],
                            warehouseId: [null],
                            isEditing: [false],
                            unit: [mapping.unit]
                        });
                        this.productWarehouseMappingDTOS.push(newMapping);
                    });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    searchWareHouse() {
        if (!this.searchkey || this.searchkey !== this.searchWarehouseName) {
            this.currentPageProductListdata = 1;
        }

        this.searchkey = this.searchWarehouseName;
        if (this.showItemPerPage) {
            this.productListdataitemsPerPage = this.showItemPerPage;
        }

        this.searchData.filter[0].filterValue = this.searchWarehouseName.trim();
        let page = {
            page: this.currentPageProductListdata,
            pageSize: this.showItemPerPage
        };

        this.warehouseService.search(page, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                    // this.snackBar.open(response.responseMessage, "Close", {
                    //     duration: 3000,
                    //     horizontalPosition: "end",
                    //     verticalPosition: "top",
                    //     panelClass: ["info-snackbar"]
                    // });
                    this.warehouseListData = [];
                    this.dataSource = new MatTableDataSource(this.warehouseListData);
                    this.productListdatatotalRecords = 0;
                } else {
                    this.warehouseListData = response.dataList;
                    this.dataSource = new MatTableDataSource(this.warehouseListData);
                    this.productListdatatotalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.snackBar.open(error.error.ERROR, "Close", {
                //     duration: 3000,
                //     horizontalPosition: "end",
                //     verticalPosition: "top",
                //     panelClass: ["error-snackbar"]
                // });
            }
        );
    }
    clearSearchWarehouse() {
        this.listview = true;
        this.detailView = false;
        this.createView = false;
        this.searchWarehouseName = "";
        this.searchkey = "";
        this.getWareHouseList("");
        this.getAllParantServiceArea();
        this.wareHouseFormGroup.reset();
        this.productWarehouseMappingDTO.reset();
        this.selectedUnit = "";
        this.productWarehouseMappingDTOS = this.fb.array([]);
        this.serviceAreaList.forEach(element => {
            if (element.id) {
                element.flag = false;
            }
        });
    }

    deleteConfirmWarehouse(productId: number) {
        if (productId) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: "Are you sure you want to delete this warehouse?",
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteProduct(productId);
                }
            });
        }
    }

    deleteProduct(productId) {
        let productEditData = this.warehouseListData.find(element => element.id == productId);
        this.warehouseService.delete(productId).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                    // this.snackBar.open(response.responseMessage, "Close", {
                    //     duration: 3000,
                    //     horizontalPosition: "end",
                    //     verticalPosition: "top",
                    //     panelClass: ["info-snackbar"]
                    // });
                } else {
                    this.toastr.success(`Successfully Deleted`, 'Success!')
                    // this.snackBar.open(response.message, "Close", {
                    //     duration: 3000,
                    //     horizontalPosition: "end",
                    //     verticalPosition: "top",
                    //     panelClass: ["success-snackbar"]
                    // });
                }
                this.getWareHouseList("");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.snackBar.open(error.error.ERROR, "Close", {
                //     duration: 3000,
                //     horizontalPosition: "end",
                //     verticalPosition: "top",
                //     panelClass: ["error-snackbar"]
                // });
            }
        );
    }

    selectPINCODEChange(_event: any) {
        this.getpincodeData(_event.value);
    }
    selectServicePin(id) {
        this.getPincodeListByServiceId();
        this.getAllBranchesByServiceAreaId();
    }

    getpincodeData(id: any) {
        this.warehouseService.getPincode(id).subscribe((response: any) => {
            this.selectWareHouseView = true;
            this.pincodeDeatils = response.data;
            this.wareHouseFormGroup.patchValue({
                city: Number(this.pincodeDeatils.cityId),
                state: Number(this.pincodeDeatils.stateId),
                country: Number(this.pincodeDeatils.countryId)
            });
        });
    }

    createWareHouse() {
        this.editMode = false;
        this.listview = false;
        this.detailView = false;
        this.createView = true;
        this.submitted = false;
        this.wareHouseFormGroup.reset();
        this.serviceAreaList.forEach(element => {
            if (element.id) {
                element.flag = false;
            }
        });
        this.commondropdownService.getCountryList();
        this.commondropdownService.getStateList();
        this.commondropdownService.getCityList();
        // this.commondropdownService.getAllPinCodeNumber();
        this.commondropdownService.getAllPinCodeData();
        this.getAllTeamsBasedonStaff();
        this.getAllParantServiceArea();
        this.getServiceArea();
        this.getAllWarehouseType();
        this.commondropdownService.getAllActiveProduct();
        this.productWarehouseMappingDTO.reset();
        this.productWarehouseMappingDTOS = this.fb.array([]);
        this.selectedUnit = "";
    }

    mylocation() {
        // this.spinner.show()
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                if (position) {
                    // console.log(
                    //   'Latitude: ' +
                    //     position.coords.latitude +
                    //     'Longitude: ' +
                    //     position.coords.longitude,
                    // )
                    this.iflocationFill = true;
                    this.wareHouseFormGroup.patchValue({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude
                    });
                }
            });
        } else {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Geolocation is not supported by this browser!');
            }


        }
    }
    @ViewChild('searchLocationDialog') searchLocationDialog!: TemplateRef<any>;
    openSearchModel() {
        this.dialog.open(this.searchLocationDialog, {
            width: '600px',
            disableClose: true,
        });
        this.searchLocationModal = true;
        this.ifsearchLocationModal = true;
        this.currentPagesearchLocationList = 1;
    }
    searchLocation() {
        if (this.searchLocationForm.valid) {
            this.customerManagementService
                .searchLocation(this.searchLocationForm.value.searchLocationname)
                .subscribe(
                    (response: any) => {
                        this.searchLocationData = response.locations;
                    },
                    (error: any) => {
                        if (error.error.code == 422) {
                            this.toastr.error(`${error.error.error}`, 'Failed!');


                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                        }
                    }
                );
        }
    }
    clearLocationForm() {
        this.searchLocationForm.reset();
        this.searchLocationData = [];
    }

    filedLocation(placeId) {
        const url = "/serviceArea/getLatitudeAndLongitude?placeId=" + placeId;
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.ifsearchLocationModal = false;
                this.wareHouseFormGroup.patchValue({
                    latitude: response.location.latitude,
                    longitude: response.location.longitude
                });

                this.iflocationFill = true;
                this.closebutton.nativeElement.click();
                this.searchLocationData = [];
                this.searchLocationForm.reset();
            },
            (error: any) => {
                // console.log(error, 'error')
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    // onElementChange(event) {
    //   this.commondropdownService.serviceAreaList = [];
    //   this.commondropdownService.serviceAreaList = event.value;
    // }

    pageChangedSearchLocationList(currentPage) {
        this.currentPagesearchLocationList = currentPage;
    }

    clearsearchLocationData() {
        this.searchLocationModal = false;
        this.searchLocationData = [];
        this.ifsearchLocationModal = false;
        this.searchLocationForm.reset();
    }
    pageChangedProductList(pageNumber) {
        this.currentPageProductListdata = pageNumber;
        if (!this.searchkey) {
            this.getWareHouseList("");
        } else {
            this.searchWareHouse();
        }
        // if (!this.searchkey) {
        //   this.getProductList();
        // } else {
        //this.getWareHouseList("")
        // }
    }
    getAllWarehouseType(): void {
        // let url = "";
        const url = "/commonList/generic/WAREHOUSE_TYPE";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.WareHouseType = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    getAllParantServiceArea(): void {
        // let url = "";
        const url = "/warehouseManagement/getAllParentServiceAreaList";
        this.warehouseService.getMethod(url).subscribe(
            (response: any) => {
                this.parentServiceArea = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getAllParentServiceAreaListByWarehouseId(id): void {
        // let url = "";
        let parentServiceAreaList = [];
        const url = "/warehouseManagement/getAllParentServiceAreaListByWarehouseId/" + id;
        this.warehouseService.getMethod(url).subscribe(
            (response: any) => {
                this.parentServiceArea = response.dataList;

                this.parentServiceArea.forEach(element => {
                    parentServiceAreaList.push(element.id);
                });

                this.wareHouseFormGroup.patchValue({
                    parentServiceAreaIdsList: parentServiceAreaList
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    getPincodeListByServiceId() {
        const url = "/pincode/getPincodeListByServiceId";
        let serviceAreaIds = this.wareHouseFormGroup.value.serviceAreaIdsList;
        this.warehouseService.postMethod(url, serviceAreaIds).subscribe((response: any) => {
            this.pincodes = response.dataList;
        });
    }
    getAllBranchesByServiceAreaId() {
        const url = "/branchManagement/getAllBranchesByServiceAreaId";
        let serviceAreaIds = this.wareHouseFormGroup.value.serviceAreaIdsList;
        this.warehouseService.postMethod(url, serviceAreaIds).subscribe((response: any) => {
            this.branchId = response.dataList;
        });
    }
    getServiceArea() {
        const url = "/serviceArea/getAllServiceAreaByStaff";
        this.serviceAreaService.getMethod(url).subscribe(
            (response: any) => {
                this.serviceAreaList = response.dataList;
                //
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    // getAllServiceAreaByBranchId() {
    //   this.warehouseService
    //     .getAllServiceAreaByBranchId(this.wareHouseFormGroup.value.branchId)
    //     .subscribe(
    //       (response: any) => {
    //         this.serviceAreaList = response.dataList;
    //
    //       },
    //       (error: any) => {
    //         // console.log(error, 'error')
    //         this.messageService.add({
    //           severity: "error",
    //           summary: "Error",
    //           detail: error.error.ERROR,
    //           icon: "far fa-times-circle",
    //         });
    //
    //       }
    //     );
    // }

    canExit() {
        if (!this.wareHouseFormGroup.dirty && !this.searchLocationForm.dirty) return true;

        return Observable.create((observer: Observer<boolean>) => {
            const dialogRef = this.dialog.open(ConfirmExitDialogComponent, {
                width: "400px",
                data: {
                    title: "Alert",
                    description: "The filled data will be lost. Do you want to continue?"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                observer.next(result);
                observer.complete();
            });
        });
    }
    getAllTeamsBasedonStaff(): void {
        // let url = "";
        const url = "/teams/getAllTeamBasedOnAttchedStaff";
        this.warehouseService.getMethod(url).subscribe(
            (response: any) => {
                this.allTeanBasedonStaff = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }



    displayedAddressColumns = ['name', 'address']


    @ViewChild('producttableMapping') producttableMapping!: MatTable<any>;
    dataSourceTable = [{}]
    displayedColumnsProduct: string[] = ['productName', 'thresholdQty', 'unit', 'actions'];

}
// Confirm Delete Dialog Component
@Component({
    selector: "app-confirm-delete-dialog",
    template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <p>{{ data.message }}</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">No</button>
      <button mat-raised-button color="warn" [mat-dialog-close]="true">Yes</button>
    </mat-dialog-actions>
  `,
    standalone: false
})
export class ConfirmDeleteDialogComponent {
    constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }
}

// Confirm Exit Dialog Component
@Component({
    selector: "app-confirm-exit-dialog",
    template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <p>{{ data.description }}</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">No</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="true">Yes</button>
    </mat-dialog-actions>
  `,
    standalone: false
})
export class ConfirmExitDialogComponent {
    constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }
}