import { debounceTime } from "rxjs/operators";
import { DatePipe } from "@angular/common";
import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { ITEMS_PER_PAGE, PER_PAGE_ITEMS, pageLimitOptions } from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { InwardService } from "src/app/service/inward.service";
import { LoginService } from "src/app/service/login.service";
import { OutwardService } from "src/app/service/outward.service";
import { InventoryRequestService } from "src/app/service/inventory-request.service";
import { PopManagementsService } from "src/app/service/pop-managements.service";
import moment from "moment";

import { Table } from "primeng/table";
import { Observable, Observer, Subject } from "rxjs";
import { INVENTORYS } from "src/app/constants/aclConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";
import { ViewMappingDetailsComponent } from "src/app/shared/components/mapping-details/view-mapping-details/view-mapping-details.component";

@Component({
    selector: "app-outwards",
    templateUrl: "./outwards.component.html",
    styleUrls: ["./outwards.component.css"],
    standalone: false
})
export class OutwardsComponent implements OnInit {
    editInwardId: any = "";
    outwardFormGroup: UntypedFormGroup;
    // countryFormArray: FormArray;
    submitted = false;
    currentPageProductListdata = 1;
    productListdataitemsPerPage = PER_PAGE_ITEMS;
    productListdatatotalRecords: any;
    countryPojo: any = {};
    outwardListData: any[] = [];
    searchData: any;
    viewRequestInventoryMappingData: any = [];
    viewSelectedReqInvenData: any = [];
    viewReqInventoryData: any = [];
    searchOutward: any = "";
    fileterGlobal: any = "";
    fileterGlobal1: any = "";
    // searchkey: string;
    AclClassConstants: any;
    AclConstants: any;
    ifRedirectInventoryModule = false;
    pageLimitOptions = pageLimitOptions;
    showItemPerPage: number = 5;
    searchkey: string;

    public loginService: LoginService;
    editMode: boolean;
    sourceTypeAsStaffFlag: boolean = false;
    MACShowModal: boolean = false;
    MACAssignModalOutward: boolean = false;
    selectWareHouseView: boolean;
    pincodeDeatils: any;
    ViewMappingListDialogRef!: MatDialogRef<any>;
    viewMappingDialogRef: MatDialogRef<any>;
    status = [
        { label: "Active", value: "ACTIVE" },
        { label: "Inactive", value: "INACTIVE" }
    ];
    sourceType = [
        { label: "Warehouse" },
        { label: "Staff" }
        // { label: "Partner" }
    ];
    destinationType = [
        { label: "Warehouse" },
        { label: "Staff" },
        { label: "Partner" }
        // { label: "POP" },
        // { label: "SA" },
    ];
    userTypes = [{ label: "Staff", value: "STAFF" }];
    @ViewChild("closebutton") closebutton;
    @ViewChild("btnClose") btnClose;
    @ViewChild("btnClose1") btnClose1;
    @ViewChild("dt") table: Table;
    @ViewChild("checkbox") checkbox;
    countryList = [];
    stateList = [];
    cityList = [];
    unit = "";
    products: any[] = [];
    warehouses: any[] = [];
    types = [
        { label: "New", value: "New" },
        { label: "Refurbished", value: "Refurbished" },
        { label: "Damage", value: "Damage" }
    ];

    pipe = new DatePipe("en-US");
    // optionUserType: boolean;
    staffList = [];
    destinationStaffList = [];
    inwardList = [];
    sources = [];
    destinations = [];
    availableQty = 0;
    showQtyError: boolean;
    outwardEdit: any = {};
    // initialValue: number = 0;
    qtyErroMsg = "";
    addMACaddress: boolean;
    inwardMacList: any[];
    selectInventryData: any = [];
    inwardIdForMac: any;
    macDetailsArray: UntypedFormArray;
    outwardIdForMac: any;
    alreadySelectedCheckBoxes: number[] = [];
    selectedCheckBoxes: number[] = [];
    selectedInwardMACAddress = [];

    listView = true;
    createView: boolean;
    searchOptionSelect = this.commondropdownService.customerSearchOption2;
    hasMac: boolean;
    hasSerial: boolean;
    inwardlength: any;
    viewOutwardDetails: any;
    detailView: boolean = false;
    searchDeatil: string;
    searchOutwardOption: any = "";
    custId: any;
    disableButton: boolean;
    isOutwardView: boolean = false;
    isOutwardEdit: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    createAccess: boolean = false;
    showMacAddressAccess: boolean = false;
    addMacAddressAccess: boolean = false;
    checkBoxCount: any[];
    specDetailsShow: boolean;
    inventoryDetailData: any;
    inventorySpecificationDetails: any = [];
    inventoryDetailModal: boolean = false;
    selectedProduct: any;

    currentPageOutwardMapMapping = 1;
    outwardMappingListitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    outwardMappingListdatatotalRecords: any;
    newFirst = 0;
    currentPageOutwardMacMapping = 1;
    outwardMappingMacListitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    outwardMappingMacListdatatotalRecords: any;
    newFirstMac = 0;
    outwardData: any;
    macOptionSelect = this.commondropdownService.macSearchOption;
    searchOption: any;
    searchMacDeatil: string;
    searchMacData: any;
    optionValue: any;
    outwardId: any;
    private customRowsSubject = new Subject<number>();
    //**********
    title = 'Outwards';
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    displayedColumns = ['id', 'outwordNumber', "productName", 'quantity', 'inTransatQuantity', 'createdBy', 'Status', 'approvalStatus', 'Action'];
    @ViewChild('addEDitDialogOutward') addEDitDialogOutward!: TemplateRef<any>;
    @ViewChild('viewOutwardDetail') viewOutwardDetail!: TemplateRef<any>;
    @ViewChild('MACAssignModalOutwardDialog') MACAssignModalOutwardDialog!: TemplateRef<any>;
    @ViewChild('macAddressDialog') macAddressDialog!: TemplateRef<any>;
    @ViewChild('inventoryDetailDialog') inventoryDetailDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    @ViewChild('productDetailsDialog') productDetailsDialog!: TemplateRef<any>;
    dialogRefView!: MatDialogRef<any>;
    dialogRefMacAssign!: MatDialogRef<any>;
    dialogRefMacAddress!: MatDialogRef<any>;
    dialogRefInventoryDetail!: MatDialogRef<any>;
    productDialogRef!: MatDialogRef<any>;
    selectedProductForDetails: any = null;
    productDetailsList: any[] = [];
    isProductDetailsLoading: boolean = false;
    columnsDisplay = ['c-box', 'id', 'macAddress', "serialNumber", 'assetId', 'itemType'];
    page = 1;
    pageSize = 5;
    totalRecords: any = 0;
    inWardAllMacList: any[] = [];
    productIdForMac: any;
    ownerIdForMac: any;
    sourceTypeForMac: any;
    deviceType: string = '';
    private readonly COLUMN_CONFIG: Record<string, any[]> = {
        SIM: [
            { field: 'imsi', header: 'IMSI', width: '150px' },
            { field: 'iccid', header: 'ICCID', width: '160px' },
            { field: 'pin1', header: 'PIN1', width: '160px' },
            { field: 'pin2', header: 'PIN2', width: '160px' },
            { field: 'puk1', header: 'PUK1', width: '160px' },
            { field: 'puk2', header: 'PUK2', width: '160px' },
            { field: 'kiEncrypted', header: 'KI ENCRYPTED', width: '160px' },
            { field: 'acc', header: 'ACC', width: '160px' },
            { field: 'adm', header: 'ADM', width: '160px' },
            { field: 'kic', header: 'KIC', width: '160px' },
            { field: 'kid', header: 'KID', width: '160px' },
            { field: 'kik', header: 'KIK', width: '120px' },
            { field: 'reason', header: 'REASON', width: '120px' }
        ],
        MSISDN: [
            { field: 'msisdn', header: 'MSISDN', width: '150px' },
            { field: 'reason', header: 'REASON', width: '120px' }
        ],
        DEFAULT: [
            { field: 'mac', header: 'MAC ADDRESS', width: '200px' },
            { field: 'serial', header: 'SERIAL NUMBER', width: '200px' },
            { field: 'reason', header: 'REASON', width: '120px' }
        ]
    };

    displayedColumnsOut: string[] = [
        'productName',
        'quantity',
        'status',
        'outwardNumber',
        'action'
    ];
    getData = new MatTableDataSource<any>([]);
    currentPageSkipList = 1;
    skiptemsPerPage = ITEMS_PER_PAGE;
    skippedTotalRecords
    displayedMacShowColumns2: any[] = [];
    displayedMacShowColumnsFields: string[] = [];
    hasImsi: boolean = false;
    hasIccid: boolean = false;
    hasMsisdn: boolean = false;
    macAddMode: 'manual' | 'bulk' = 'manual'; // Switch between manual and bulk MAC address entry
    getDisplayedMacTableColumns(): Array<string> {
        const baseColumns = ['c-box', 'id'];

        if (this.deviceType === 'SIM') {
            if (this.hasImsi) {
                baseColumns.push('imsi');
            }
            if (this.hasIccid) {
                baseColumns.push('iccid');
            }
            baseColumns.push('assetId', 'itemType');
            return baseColumns;
        }

        else if (this.hasMsisdn) {
            baseColumns.push('msisdn');
            baseColumns.push('assetId', 'itemType');
            return baseColumns;
        }

        else {
            const columns = [...baseColumns];
            if (this.hasMac) {
                columns.push('macAddress');
            }
            if (this.hasSerial) {
                columns.push('serialNumber');
            }
            columns.push('assetId', 'itemType');
            return columns;
        }

    }
    bulkMacFile: File | null = null;
    showSkippedTable = false;
    outwardCreationMode: 'manual' | 'bulk' = 'manual';
    bulkOutwardsList: any[] = [];
    bulkOutwardFile: File | null = null;
    displayedMacShowColumns: string[] = [];
    constructor(
        private router: Router,
        private fb: UntypedFormBuilder,
        private activetedroute: ActivatedRoute,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private outwardService: OutwardService,
        private inventoryRequestService: InventoryRequestService,
        private popService: PopManagementsService,
        loginService: LoginService,
        public commondropdownService: CommondropdownService,
        private inwardService: InwardService,
        // 
        private dialog: MatDialog,
        private toastr: ToastrService
    ) {
        this.createAccess = loginService.hasPermission(INVENTORYS.INVEN_OUTWARDS_CREATE);
        this.deleteAccess = loginService.hasPermission(INVENTORYS.INVEN_OUTWARDS_DELETE);
        this.editAccess = loginService.hasPermission(INVENTORYS.INVEN_OUTWARDS_EDIT);
        this.showMacAddressAccess = loginService.hasPermission(INVENTORYS.INVEN_OUTWARDS_SHOW_MAC);
        this.addMacAddressAccess = loginService.hasPermission(INVENTORYS.INVEN_OUTWARDS_ADD_MAC);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.isOutwardEdit = this.editAccess;
        this.editMode = !this.createAccess && this.editAccess ? true : false;
    }

    onCustomRowsChange(value: number) {
        this.customRowsSubject.next(value);
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INVENTORYS.INVEN_OUTWARDS_SHOW_MAC) || this.loginService.hasPermission(INVENTORYS.INVEN_OUTWARDS_ADD_MAC) || this.loginService.hasPermission(INVENTORYS.INVEN_OUTWARDS_DELETE) || this.loginService.hasPermission(INVENTORYS.INVEN_OUTWARDS_EDIT)) {
            return ['id', 'outwordNumber', "productName", 'quantity', 'inTransatQuantity', 'createdBy', 'Status', 'approvalStatus', 'Action'];
        } else {
            return ['id', 'outwordNumber', "productName", 'quantity', 'inTransatQuantity', 'createdBy', 'Status', 'approvalStatus'];
        }
    }

    ngOnInit(): void {
        // this.outwardService.getAllStaff().subscribe((res: any) => {
        //     const staffId = localStorage.getItem("userId");
        //     this.staffList = res.dataList.filter(element => element.id == staffId);
        //     this.destinationStaffList = res.dataList;
        // });

        this.outwardFormGroup = this.fb.group({
            id: [""],
            outwardNumber: [""],
            qty: [""],
            status: ["", Validators.required],
            product: ["", Validators.required],
            sourceType: ["", Validators.required],
            source: ["", Validators.required],
            description: ["", Validators.required],
            destinationType: ["", Validators.required],
            destination: ["", Validators.required],
            outwardDateTime: [new Date(), Validators.required],
            mvnoId: [""],
            //inwardId: ["", Validators.required],
            usedQty: [0],
            unusedQty: [""],
            inTransitQty: ["", Validators.min(1)],
            outTransitQty: [""],
            rejectedQty: [""],
            requestInventoryId: [""],
            requestInventoryName: [""],
            requestInventoryProductId: [""],
            selectedItems: [0],
            unit: ["-"]
        });
        this.outwardFormGroup?.get("unit")?.setValue("-");
        this.outwardFormGroup?.get("unit")?.disable();
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
        this.searchMacData = {
            filterBy: "",
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: "",
            pageSize: "",
            entityId: null,
            productId: "",
            ownerId: "",
            ownerType: "",
            entityType: "outward"
        };

        this.getOutwardList("");
        if (this.activetedroute.snapshot.queryParamMap.get("mapId")) {
            this.ifRedirectInventoryModule = true;
            this.outwardFormGroup.patchValue({
                id: Number(this.selectInventryData.value)
            });
            setTimeout(() => {
                this.getSources("Warehouse");
            }, 1000);
            this.listView = false;
            this.createView = true;
            // this.getInventoryRequestDetails(value, mapId);
        }
        this.outwardFormGroup.get("inTransitQty").valueChanges.subscribe(val => {
            const qty: number = val;
            this.showQtyError = false;
            this.qtyErroMsg = "";
            if (!this.editMode) {
                if (val !== null && val !== "") {
                    if (typeof qty === "number") {
                        if (qty < 0 || qty === 0) {
                            this.showQtyError = true;
                            this.qtyErroMsg = "Quantity must be greater than 0.";
                            this.disableButton = true;
                        } else if (qty > this.availableQty) {
                            this.showQtyError = true;
                            this.qtyErroMsg =
                                "Please enter a quantity less than or equal to the available quantity.";
                            this.disableButton = true;
                        } else {
                            this.disableButton = false;
                        }
                    } else {
                        this.showQtyError = true;
                        this.qtyErroMsg = "Quantity must be a number.";
                        this.disableButton = true;
                    }
                } else {
                    this.disableButton = true;
                }
            }
        });
    }
    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    getInventoryRequestDetails(id: any, mapId: any) {
        const url = "/requestinventory/getById?id=" + id;
        this.inventoryRequestService.getMethod(url).subscribe((response: any) => {
            this.viewReqInventoryData = response.data;
            this.viewRequestInventoryMappingData =
                this.viewReqInventoryData.requestInvenotryProductMappings.find(
                    element => element.id == mapId
                );
            setTimeout(() => {
                this.outwardFormGroup.patchValue({
                    //product: this.viewReqInventoryData.requestInvenotryProductMappings[0].productId,
                    product: this.viewRequestInventoryMappingData.productId,
                    description: this.viewRequestInventoryMappingData.description,
                    status: "Active",
                    sourceType: "Warehouse",
                    source: this.viewReqInventoryData.requestToWarehouseId,
                    destinationType: this.viewReqInventoryData.requestNameId,
                    destination: this.viewReqInventoryData.requestNameId,
                    //inTransitQty: this.viewReqInventoryData.requestInvenotryProductMappings[0].quantity,
                    inTransitQty: this.viewRequestInventoryMappingData.quantity,
                    requestInventoryName: this.viewReqInventoryData.requestInventoryName,
                    requestInventoryId: this.viewReqInventoryData.id,
                    requestInventoryProductId: this.viewRequestInventoryMappingData.id
                });
                this.getAvailableQtyByProductAndSourceInventoryRequestDetails(
                    this.viewRequestInventoryMappingData.productId,
                    this.viewReqInventoryData.requestToWarehouseId,
                    "Warehouse",
                    this.viewRequestInventoryMappingData.quantity
                );
            }, 1500);
        });
    }
    // selectedRow(event: any, ind: number, row: any, inde: any) {
    //     const foundIndex = this.selectedCheckBoxes.findIndex(val => val === ind);
    //     if (event == true) {
    //         this.selectedCheckBoxes.push(1);
    //     } else {
    //         this.selectedCheckBoxes.splice(foundIndex, 1);
    //     }
    // }

    // toggleSelectAll(inwardLength: number, sel: number) {
    //     if (inwardLength !== sel) {
    //         this.selectedCheckBoxes.length = this.inwardlength;
    //     } else {
    //         this.selectedCheckBoxes = [];
    //     }
    // }

    // saveSelectedCheckBoxes() {
    //   // Store the currently selected checkboxes in the alreadySelectedCheckBoxes array
    //   this.alreadySelectedCheckBoxes = [...this.selectedCheckBoxes];
    //   this.selectedCheckBoxes = [];
    // }

    getAvailableQtyByProductAndSourceInventoryRequestDetails(
        productId,
        sourceId,
        sourceType,
        requestQty
    ): void {
        this.getSources(sourceType);
        this.getDestinations(this.destType);
        if (productId && sourceId) {
            this.inwardList = [];
            this.outwardService.getProductAvailableQTY(productId, sourceId, sourceType).subscribe(
                (res: any) => {
                    this.inwardList = res.dataList;
                    if (res.dataList.length == 0) {
                        this.availableQty = 0;
                    } else {
                        const totalQty = res.dataList.find(element => element).unusedQty;
                        const reservedQty = this.bulkOutwardsList
                            .filter(item => item.productId === productId && item.sourceId === sourceId)
                            .reduce((sum, item) => sum + (Number(item.qty) || Number(item.inTransitQty) || 0), 0);
                        this.availableQty = totalQty - reservedQty < 0 ? 0 : totalQty - reservedQty;
                    }
                    if (requestQty > this.availableQty) {
                        this.showQtyError = true;
                        this.qtyErroMsg = "The requested quantity is greater than available quantity";
                    } else {
                        this.showQtyError = false;
                    }
                    // this.getAvailableQty(this.inwardList);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
        } else {
        }
    }
    TotalItemPerPage(event): void {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageProductListdata > 1) {
            this.currentPageProductListdata = 1;
        }
        if (!this.searchkey) {
            this.getOutwardList(this.showItemPerPage);
        } else {
            this.searchOutwardData();
        }
    }
    currentPageStateSlab = 1;

    getOutwardList(list): void {
        this.outwardListData = [];
        let size;
        this.searchkey = "";
        const List = this.currentPageProductListdata;
        if (list) {
            size = list;
            this.productListdataitemsPerPage = list;
        } else {
            size = this.productListdataitemsPerPage;
        }
        const plandata = {
            page: List,
            pageSize: size
        };
        this.outwardService.getAll(plandata).subscribe(
            (response: any) => {
                this.outwardListData = response.dataList;
                this.dataSource = new MatTableDataSource<any>(this.outwardListData);

                this.productListdatatotalRecords = response.totalRecords;

                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    submit(): void {
        this.submitted = true;
        if (this.outwardFormGroup.valid && !this.showQtyError) {
            if (this.editMode) {
                this.outwardService.update(this.mapObject()).subscribe(
                    (res: any) => {
                        this.dialogRef.close();
                        if (res.responseCode === 406) {
                            this.toastr.error(`${res.responseMessage}`, 'Error!');
                        } else {
                            this.toastr.success(`Successfully Updated`, 'Success!');
                            this.clearSearchOutward();
                            // this.availableQty = 0;
                            // this.outwardFormGroup.patchValue({
                            //   outwarddDateTime: new Date()
                            // });
                            // this.editMode = false;
                            // this.submitted = false;
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Error!');
                    }
                );
            } else {
                let data = this.mapObject();
                this.outwardService.save(data).subscribe(
                    (res: any) => {
                        this.dialogRef.close();
                        if (res.responseCode == 406) {
                            this.toastr.error(`${res.responseMessage}`, 'Error!');
                        } else {
                            this.toastr.success(`Successfully Created`, 'Success!');
                            this.submitted = false;
                            this.clearSearchOutward();
                            // this.outwardFormGroup.patchValue({
                            //   outwardDateTime: new Date(),
                            // });
                            this.availableQty = 0;
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Error!');
                    }
                );
            }
        }
    }

    mapObject(): {} {
        const outwardValues = this.outwardFormGroup.getRawValue();
        const outward = {
            id: "",
            productId: "",
            qty: 0,
            outwardDateTime: "",
            sourceId: "",
            sourceType: "",
            status: "",
            description: "",
            outwardNumber: "",
            destinationId: null,
            destinationType: "",
            mvnoId: "",
            //inwardId: "",
            // isQtyChanged: false,
            usedQty: "",
            unusedQty: "",
            inTransitQty: "",
            outTransitQty: "",
            rejectedQty: "",
            requestInventoryId: "",
            requestInventoryProductId: "",
            selectedItems: ""
        };
        outward.id = outwardValues.id ? outwardValues.id : null;
        outward.productId = outwardValues.product;
        outward.qty = outwardValues.qty;
        // outward.isQtyChanged = (outwardValues.qty != this.initialValue);
        outward.status = outwardValues.status;
        outward.outwardDateTime = outwardValues.outwardDateTime;
        outward.sourceId = outwardValues.source;
        outward.description = outwardValues.description;
        outward.sourceType = outwardValues.sourceType;
        outward.outwardNumber = outwardValues.outwardNumber ? outwardValues.outwardNumber : "";
        outward.destinationId = outwardValues.destination;
        outward.destinationType = outwardValues.destinationType;
        outward.mvnoId = null;
        //outward.inwardId = outwardValues.inwardId;
        outward.usedQty = outwardValues.usedQty;
        outward.unusedQty = outwardValues.unusedQty;
        outward.inTransitQty = outwardValues.inTransitQty;
        outward.outTransitQty = outwardValues.outTransitQty;
        outward.rejectedQty = outwardValues.rejectedQty;
        outward.requestInventoryId = outwardValues.requestInventoryId;
        outward.requestInventoryProductId = outwardValues.requestInventoryProductId;
        outward.selectedItems = outwardValues.selectedItems;
        return outward;
    }

    addToBulkList(): void {
        this.submitted = true;
        if (this.outwardFormGroup.valid && !this.showQtyError) {
            const newData = this.mapObject();
            const currentSourceType = this.outwardFormGroup.get('sourceType')?.value;
            const currentSource = this.outwardFormGroup.get('source')?.value;
            const currentDestinationType = this.outwardFormGroup.get('destinationType')?.value;
            const currentDestination = this.outwardFormGroup.get('destination')?.value;

            this.bulkOutwardsList.unshift(newData);
            this.outwardFormGroup.reset();
            this.outwardFormGroup.patchValue({
                outwardDateTime: new Date(),
                unit: "-",
                sourceType: currentSourceType,
                source: currentSource,
                destinationType: currentDestinationType,
                destination: currentDestination
            });

            // Explicitly disable via reactive forms
            this.outwardFormGroup.get('sourceType')?.disable();
            this.outwardFormGroup.get('source')?.disable();
            this.outwardFormGroup.get('destinationType')?.disable();
            this.outwardFormGroup.get('destination')?.disable();

            this.availableQty = 0;
            this.submitted = false;
            this.disableButton = true;
        }
    }

    removeBulkItem(index: number): void {
        this.bulkOutwardsList.splice(index, 1);

        // Re-enable if list goes empty and not in restricted modes
        if (this.bulkOutwardsList.length === 0) {
            if (!this.editMode && !this.ifRedirectInventoryModule) {
                this.outwardFormGroup.get('sourceType')?.enable();
                this.outwardFormGroup.get('source')?.enable();
            }
            if (!this.editMode) {
                this.outwardFormGroup.get('destinationType')?.enable();
                this.outwardFormGroup.get('destination')?.enable();
            }
        }

        // Re-calculate the available qty for the currently selected product
        const currentProduct = this.outwardFormGroup.get('product')?.value;
        const currentSource = this.outwardFormGroup.get('source')?.value;
        const currentSourceType = this.outwardFormGroup.get('sourceType')?.value;
        if (currentProduct && currentSource && currentSourceType) {
            this.getAvailableQtyByProductAndSource(currentProduct, currentSource, currentSourceType);
        }
    }

    // submitBulkOutwards(): void {
    //     if (this.bulkOutwardsList.length === 0) {
    //         this.toastr.error('Please add at least one outward to the list.', 'Error!');
    //         return;
    //     }

    //     let successCount = 0;
    //     let completeCount = 0;
    //     this.bulkOutwardsList.forEach((outwardData) => {
    //         this.outwardService.save(outwardData).subscribe(
    //             (res: any) => {
    //                 completeCount++;
    //                 if (res.responseCode != 406) {
    //                     successCount++;
    //                 }
    //                 this.checkBulkComplete(completeCount, successCount);
    //             },
    //             (error: any) => {
    //                 completeCount++;
    //                 this.toastr.error(`${error.error.ERROR}`, 'Error!');
    //                 this.checkBulkComplete(completeCount, successCount);
    //             }
    //         );
    //     });
    // 



    submitBulkOutwards(): void {
        if (this.bulkOutwardsList.length === 0) {
            this.toastr.error('Please add at least one outward to the list.', 'Error!');
            return;
        }

        this.outwardService.saveBulk(this.bulkOutwardsList).subscribe(
            (res: any) => {
                if (res.responseCode === 406 || res.responseCode === 417) {
                    this.toastr.error(res.responseMessage, 'Error!');
                }
                else if (res.responseCode === 200 || res.responseCode === 201) {
                    this.dialogRef.close();
                    this.toastr.success(`Bulk Outwards Created Successfully`, 'Success!');
                    this.bulkOutwardsList = [];
                    this.clearSearchOutward();
                    this.getOutwardList("");
                }
                else {
                    this.dialogRef.close();
                    this.toastr.success(res.responseMessage || 'Bulk Outwards Processed', 'Success!');
                    this.bulkOutwardsList = [];

                    this.clearSearchOutward();
                    this.getOutwardList("");
                }
            },
            (error: any) => {
                this.dialogRef.close();
                this.toastr.error(error.error?.responseMessage || error.error?.ERROR || 'Failed to create bulk outwards', 'Failed!');
            }
        );

    }

    checkBulkComplete(completeCount: number, successCount: number): void {
        if (completeCount === this.bulkOutwardsList.length) {
            this.dialogRef.close();
            if (successCount > 0) {
                this.toastr.success(`Successfully created ${successCount} Outwards`, 'Success!');
            }
            this.bulkOutwardsList = [];
            this.clearSearchOutward();
            this.getOutwardList("");
        }
    }

    getProductName(id: any): string {
        const prod = this.products.find(p => p.id === id);
        return prod ? prod.name : id;
    }

    getSourceName(id: any, type: string): string {
        if (type === 'Warehouse') {
            const w = this.warehouses.find(x => x.id === id);
            return w ? w.name : id;
        } else if (type === 'Staff' || type === 'Customer') {
            const s = this.destinationStaffList.find(x => x.id === id);
            return s ? s.username : id;
        }
        const src = this.sources.find(x => x.id === id);
        return src ? (src.name || src.username || id) : id;
    }

    getDestName(id: any, type: string): string {
        if (type === 'Warehouse') {
            const w = this.warehouses.find(x => x.id === id);
            return w ? w.name : id;
        } else if (type === 'Staff' || type === 'Customer') {
            const s = this.destinationStaffList.find(x => x.id === id);
            return s ? s.username : id;
        }
        const d = this.destinations.find(x => x.id === id);
        return d ? (d.name || d.username || id) : id;
    }

    destType: any;
    editOutward(id): void {
        this.sourceType = [{ label: "Warehouse" }, { label: "Staff" }, { label: "Partner" }];

        this.editMode = true;
        this.createView = true;
        // this.listView = false;
        this.detailView = false;
        this.sourceType = [{ label: "Warehouse" }, { label: "Staff" }];
        this.outwardService.getAllProducts().subscribe((res: any) => {
            this.products = res.dataList;
        });
        this.inwardService.getAllWareHouse().subscribe((res: any) => {
            this.warehouses = res.dataList;
        });
        this.outwardService.getAllStaff().subscribe((res: any) => {
            const staffId = localStorage.getItem("userId");
            this.staffList = res.dataList.filter(element => element.id == staffId);
            this.destinationStaffList = res.dataList;
        });
        let outwardEdit = this.outwardListData.find(element => element.id === id);
        const url = "/outwards/" + id;
        this.outwardService.getMethod(url).subscribe((res: any) => {
            this.outwardEdit = res.data;
            this.destType = this.outwardEdit.destinationType;
            // this.getInwardList(
            //   this.outwardEdit.productId.id,
            //   this.outwardEdit.sourceId,
            //   this.outwardEdit.sourceType
            // );
            this.getSources(this.outwardEdit.sourceType);
            this.getDestinations(this.outwardEdit.destinationType);
            if (this.outwardEdit.sourceType == "Staff" || this.outwardEdit.sourceType == "Customer")
                // this.isSourceAStaffOrCustomer = true;
                this.sourceTypeAsStaffFlag = true;
            else this.sourceTypeAsStaffFlag = false;
            if (
                this.outwardEdit.destinationType == "Staff" ||
                this.outwardEdit.destinationType == "Customer"
            )
                this.isDestAStaffOrCustomer = true;
            else this.isDestAStaffOrCustomer = false;
            this.outwardFormGroup.patchValue({
                id: this.outwardEdit.id,
                product: this.outwardEdit.productId.id,
                description: this.outwardEdit.description,
                qty: this.outwardEdit.qty,
                status: this.outwardEdit.status,
                unit: this.outwardEdit?.productId?.productCategory?.unit,
                outwardDateTime: new Date(this.outwardEdit.outwardDateTime),
                sourceType: this.outwardEdit.sourceType,
                source: this.outwardEdit.sourceId,
                destinationType: this.outwardEdit.destinationType,
                destination: this.outwardEdit.destinationId,
                // type: this.outwardEdit.userType,
                outwardNumber: this.outwardEdit.outwardNumber,
                mvnoId: [""],
                unusedQty: this.outwardEdit.unusedQty,
                usedQty: this.outwardEdit.usedQty,
                selectedItems: this.outwardEdit.selectedItems,
                //inwardId: this.outwardEdit.inwardId.id,
                inTransitQty: this.outwardEdit.inTransitQty,
                requestInventoryId: this.outwardEdit.requestInventoryId
            });
            this.addEditOutwardDialogOpen();
            // if (this.outwardEdit.staffId != null) {
            //   this.optionUserType = true;
            // }
            // this.outwardFormGroup.controls.inwardId.setValue(this.outwardEdit.inwardId.id);
        });
    }

    selSearchOption(event: any) {
        this.searchDeatil = "";
    }

    searchOutwardData() {
        if (!this.searchkey || this.searchkey !== this.searchOutward) {
            this.currentPageProductListdata = 1;
        }
        this.searchkey = this.searchOutward;
        if (this.showItemPerPage) {
            this.productListdataitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchOutward;
        this.searchData.filter[0].filterColumn = this.searchOutwardOption.trim();
        const page = {
            page: this.currentPageProductListdata,
            pageSize: this.productListdataitemsPerPage,
            sortBy: "id",
            sortOrder: 0
        };
        const url =
            "/outwards/search?page=" +
            page.page +
            "&pageSize=" +
            page.pageSize +
            "&sortBy=" +
            page.sortBy +
            "&sortOrder=" +
            page.sortOrder;
        this.outwardService.postMethod(url, this.searchData).subscribe(
            (res: any) => {
                if (res.responseCode === 200) {
                    this.outwardListData = res.dataList;
                    this.dataSource = new MatTableDataSource<any>(this.outwardListData);
                    if (this.paginator) {
                        this.dataSource.paginator = this.paginator;
                    }
                    const list = this.outwardListData;
                    const filterList = list.filter(cust => cust.id !== this.custId);
                    this.outwardListData = filterList;
                    this.productListdatatotalRecords = res.totalRecords;

                } else {
                    this.productListdatatotalRecords = 0;
                    this.toastr.info(`${res.responseMessage}`, 'Info!');
                    this.outwardListData = [];
                }
            },
            (error: any) => {
                this.productListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.outwardListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }

    clearSearchOutward(): void {
        {
        }
        this.listView = true;
        this.createView = false;
        this.editMode = false;
        this.submitted = false;
        this.showQtyError = false;
        this.disableButton = false;
        this.detailView = false;
        this.searchDeatil = "";
        this.availableQty = 0;
        this.searchOutward = "";
        this.searchOutwardOption = "";
        this.searchkey = "";
        this.getOutwardList("");
        this.router.navigate(["/home/outwards"], {});
        this.ifRedirectInventoryModule = false;
        this.outwardFormGroup.reset();
        this.outwardFormGroup.patchValue({
            outwardDateTime: new Date()
        });
    }
    ResetField() {
        this.outwardFormGroup.controls.destinationType.reset();
        this.outwardFormGroup.controls.destination.reset();
        this.outwardFormGroup.controls.inTransitQty.reset();
        // this.refreshTimeValue(this.outwardFormGroup.controls.outwardDateTime);
        // this.outwardFormGroup.controls.status.reset();
    }
    ResetFieldDestination() {
        this.outwardFormGroup.controls.destination.reset();
        this.outwardFormGroup.controls.inTransitQty.reset();
    }
    deleteConfirmOutward(id: number): void {
        if (id) {
            this.confirmationService.confirm({
                message: "Do you want to delete this outward?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteOutward(id);
                },
                reject: () => {
                    this.toastr.info(`You have rejected`, 'Info!');
                }
            });
        }
    }

    deleteOutward(productId): void {
        // const productEditData = this.outwardListData.find(element => element.id === productId);
        this.outwardService.delete(productId).subscribe(
            (response: any) => {
                if (response.responseCode === 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Error!');
                } else {
                    this.toastr.success(`Successfully Deleted`, 'Success!');
                    this.getOutwardList("");
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'error!');
            }
        );
    }
    // getUserType(event) {
    //   this.outwardFormGroup.controls.userId.setValue('');
    //   if (event.value == "STAFF") {
    //     this.optionUserType = true;
    //     // this.outwardFormGroup.controls.staffId.enable();
    //   } else {
    //     this.optionUserType = false;
    //     // this.outwardFormGroup.controls.custId.enable();
    //   }
    // }
    getUnit(event, dd: any): void {
        this.selectedProduct = dd.selectedOption;
        this.unit = this.products.find(element => element.id === event.value)?.productCategory?.unit;
        this.outwardFormGroup?.get("unit")?.setValue(this.unit);

        if (this.bulkOutwardsList.length === 0) {
            this.outwardFormGroup.controls.sourceType.setValue("");
            this.outwardFormGroup.controls.source.setValue("");
            this.outwardFormGroup.controls.destinationType.setValue("");
            this.outwardFormGroup.controls.destination.setValue("");
        }
        this.outwardFormGroup.controls.description.setValue("");
        this.outwardFormGroup.controls.inTransitQty.setValue("");
        this.availableQty = 0;

        if (this.bulkOutwardsList.length > 0) {
            const currentSourceType = this.outwardFormGroup.get('sourceType')?.value;
            const currentSource = this.outwardFormGroup.get('source')?.value;
            if (currentSourceType && currentSource) {
                this.getAvailableQtyByProductAndSource(event.value, currentSource, currentSourceType);
            }
        }
    }
    selectSourceEvent(e) {
        let sourceID = e.value;
        this.getAvailableQtyByProductAndSource(
            this.outwardFormGroup.controls.productId.value,
            sourceID,
            this.outwardFormGroup.controls.sourceType.value
        );
    }

    getAvailableQtyByProductAndSource(productId, sourceId, sourceType): void {
        if (this.bulkOutwardsList.length === 0) {
            this.outwardFormGroup.get("destinationType").reset();
            this.outwardFormGroup.get("destination").reset();
        }
        this.getSources(sourceType);
        this.getDestinations(this.destType);
        if (productId && sourceId) {
            this.inwardList = [];
            this.outwardService.getProductAvailableQTY(productId, sourceId, sourceType).subscribe(
                (res: any) => {
                    this.inwardList = res.dataList;
                    if (res.dataList.length == 0) {
                        this.availableQty = 0;
                    } else {
                        const totalQty = res.dataList.find(element => element).unusedQty;
                        const reservedQty = this.bulkOutwardsList
                            .filter(item => item.productId === productId && item.sourceId === sourceId)
                            .reduce((sum, item) => sum + (Number(item.qty) || Number(item.inTransitQty) || 0), 0);
                        this.availableQty = totalQty - reservedQty < 0 ? 0 : totalQty - reservedQty;
                    }

                    const ctrl = this.outwardFormGroup.get('inTransitQty');
                    if (ctrl && ctrl.value !== null && ctrl.value !== "") {
                        ctrl.setValue(ctrl.value, { emitEvent: true });
                    }
                    // this.getAvailableQty(this.inwardList);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'error!');
                }
            );
        } else {
        }
    }

    isSourceAStaffOrCustomer = false;
    getSources(sourceType): void {
        if (this.bulkOutwardsList.length === 0) {
            this.outwardFormGroup.controls.destinationType.setValue("");
            this.outwardFormGroup.controls.destination.setValue("");
        }
        this.outwardFormGroup.controls.inTransitQty.setValue("");
        this.availableQty = 0;
        if (sourceType == "Warehouse") {
            this.isSourceAStaffOrCustomer = false;
            this.sourceTypeAsStaffFlag = false;
            this.sources = this.warehouses;
            this.destinationType = [{ label: "Warehouse" }, { label: "Staff" }, { label: "Partner" }];
        } else if (sourceType == "Staff") {
            this.isSourceAStaffOrCustomer = true;
            this.sourceTypeAsStaffFlag = true;
            if (this.selectedProduct && this.selectedProduct.hasAssetConsider) {
                this.sources = this.destinationStaffList.filter(element => element.partnerid == 1);
            } else {
                this.sources = this.staffList;
            }
            this.destinationType = [{ label: "Warehouse" }];
        } else if (sourceType == "Partner") {
            const url = "/partner/allActive";
            this.isSourceAStaffOrCustomer = false;
            this.destinationType = [{ label: "POP" }, { label: "Service Area" }];
            this.outwardService.getMethod(url).subscribe(
                (res: any) => {
                    this.sources = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'error!');
                }
            );
        } else if (sourceType == "Service Area") {
            const url = "/serviceArea/all";
            this.isSourceAStaffOrCustomer = false;
            this.commondropdownService.getMethodWithCache(url).subscribe(
                (res: any) => {
                    this.sources = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'error!');
                }
            );
        } else if (sourceType == "POP") {
            const url = "/popmanagement/all";
            this.isSourceAStaffOrCustomer = false;
            this.popService.getMethodWithCache(url).subscribe(
                (res: any) => {
                    this.sources = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'error!');
                }
            );
        }
    }

    isDestAStaffOrCustomer = false;
    getDestinations(destinationType): void {
        if (this.bulkOutwardsList.length === 0) {
            this.outwardFormGroup.controls.destination.setValue("");
        }
        this.outwardFormGroup.controls.inTransitQty.setValue("");
        const destinationTypeVal = destinationType;
        const sourceTypeVal = this.outwardFormGroup.controls.sourceType.value;
        const sourceVal = this.outwardFormGroup.controls.source.value;
        if (destinationType == "Warehouse") {
            this.isDestAStaffOrCustomer = false;
            this.destinations = this.warehouses;
            const destinationData = this.destinations;
            if (sourceTypeVal != "" && destinationTypeVal != "") {
                if (sourceTypeVal == destinationTypeVal) {
                    if (sourceVal != "") {
                        this.destinations = destinationData.filter(item => item.id != sourceVal);
                    }
                }
            }
        } else if (destinationType == "Staff") {
            this.isDestAStaffOrCustomer = true;
            this.destinations = this.destinationStaffList.filter(element => element.partnerid == 1);
            const destinationData = this.destinations;

            if (sourceTypeVal != "" && destinationTypeVal != "") {
                if (sourceTypeVal == destinationTypeVal) {
                    if (sourceVal != "") {
                        this.destinations = destinationData.filter(item => item.id != sourceVal);
                    }
                }
            }
        } else if (destinationType == "Partner") {
            const url = "/partner/getAllTypePartner";
            this.isDestAStaffOrCustomer = false;
            this.outwardService.getMethod(url).subscribe(
                (res: any) => {
                    this.destinations = res.dataList;

                    const destinationData = this.destinations;
                    if (sourceTypeVal != "" && destinationTypeVal != "") {
                        if (sourceTypeVal == destinationTypeVal) {
                            if (sourceVal != "") {
                                this.destinations = destinationData.filter(item => item.id != sourceVal);
                            }
                        }
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'error!');
                }
            );
        } else if (destinationType == "Service Area") {
            const url = "/serviceArea/all";
            this.isDestAStaffOrCustomer = false;
            this.commondropdownService.getMethodWithCache(url).subscribe(
                (res: any) => {
                    this.destinations = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'error!');
                }
            );
        } else if (destinationType == "POP") {
            const url = "/popmanagement/all";
            this.isDestAStaffOrCustomer = false;
            this.popService.getMethodWithCache(url).subscribe(
                (res: any) => {
                    this.destinations = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'error!');
                }
            );
        } else if (destinationType == "Customer") {
            const url = `/customers/getActiveCustomersList`;
            this.isDestAStaffOrCustomer = true;
            this.popService.getMethod(url).subscribe(
                (res: any) => {
                    this.destinations = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'error!');
                }
            );
        }
    }

    // getAvailableQty(): void {
    //   this.availableQty = this.inwardList.find(element => element).unusedQty;
    // }

    createOutward(): void {
        this.createView = true;
        // this.listView = false;
        this.detailView = false;
        this.editMode = false;
        this.submitted = false;
        this.showQtyError = false;
        this.availableQty = 0;
        this.searchOutward = "";
        this.searchkey = "";
        this.outwardFormGroup.reset();
        this.ifRedirectInventoryModule = false;
        this.outwardFormGroup.patchValue({
            outwardDateTime: new Date()
        });
        this.outwardEdit = null;
        this.sourceType = [{ label: "Warehouse" }, { label: "Staff" }];

        this.addEditOutwardDialogOpen();
        this.outwardService.getAllProducts().subscribe((res: any) => {
            this.products = res.dataList;
        });
        this.inwardService.getAllWareHouse().subscribe((res: any) => {
            this.warehouses = res.dataList;
        });
        this.outwardService.getAllStaff().subscribe((res: any) => {
            const staffId = localStorage.getItem("userId");
            this.staffList = res.dataList.filter(element => element.id == staffId);
            this.destinationStaffList = res.dataList;
        });
    }
    inTransQty: number;
    selectedItemsCount: number;

    addMAC(outward) {
        this.searchOption = "";
        this.searchMacDeatil = "";
        this.selectedInwardMACAddress = [];
        this.inwardIdForMac = outward?.id;
        this.hasMac = outward.productId.productCategory.hasMac;
        this.hasSerial = outward.productId.productCategory.hasSerial;
        this.productIdForMac = outward?.productId.id;
        this.ownerIdForMac = outward?.sourceId;
        this.sourceTypeForMac = outward?.sourceType;

        this.inTransQty = outward.inTransitQty;
        this.selectedItemsCount = outward.selectedItems;
        this.outwardIdForMac = outward.id;
        this.deviceType = outward.productId.productCategory.deviceType;
        this.displayedMacShowColumns2 =
            this.COLUMN_CONFIG[this.deviceType] ||
            this.COLUMN_CONFIG['DEFAULT'];

        this.displayedMacShowColumnsFields =
            this.displayedMacShowColumns2.map(col => col.field);

        if (outward.productId.productCategory.deviceType === 'SIM') {
            this.hasImsi = outward.productId.productCategory.hasImsi;
            this.hasIccid = outward.productId.productCategory.hasIccid;
            this.macOptionSelect = [{ label: "IMSI", value: "imsi" },
            { label: "ICCID", value: "iccid" },]
        }

        if (outward.productId.productCategory.deviceType === 'MSISDN') {
            this.hasMsisdn = outward.productId.productCategory.hasMsisdn;
            this.macOptionSelect = [{ label: "MSISDN", value: "msisdn" }]
            this.searchOption = "msisdn";
        }

        if ((outward.productId.productCategory.deviceType === "ONU") && !this.hasMac && !this.hasSerial) {
            this.toastr.info(`Product type does not allow to add Mac/Serial Number..`, 'Info!');
            this.addMACaddress = false;
        } else {
            this.MACAssignModalOutward = true;
            this.outwardData = outward;
            this.getItems(outward.productId.id, outward.sourceId, outward.sourceType).subscribe(
                (res: any) => {
                    this.inwardlength = res?.dataList?.length;
                    this.inwardMacList = res.dataList;
                    this.inwardMacList.forEach(element => {
                        if (this.inWardAllMacList.findIndex((item) => item.id === element.id) === -1) {
                            this.inWardAllMacList.push(element);
                        }
                    });
                    this.outwardMappingMacListdatatotalRecords = res.totalRecords;
                    this.macAssignModalOutward();
                    this.addMACaddress = true;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
        }
    }

    macAssignModalOutward() {
        this.dialogRefMacAssign = this.dialog.open(this.MACAssignModalOutwardDialog, {
            width: '1500px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRefMacAssign.afterClosed().subscribe(result => {
            this.dialogRefMacAssign = null;
        });
    }

    getItems(productId, sourceId, sourceType) {
        const body = {
            page: this.currentPageOutwardMacMapping,
            pageSize: this.outwardMappingMacListitemsPerPage
        };
        this.inwardMacList = [];

        this.macDetailsArray = this.fb.array([]);
        return this.outwardService.postItems(productId, sourceId, sourceType, body);
    }

    // mapMACOutward(): void {
    //     const selectedMAC = this.inwardMacList.filter(val =>
    //         this.selectedInwardMACAddress.includes(val)
    //     );
    //     if (this.selectedInwardMACAddress.length === 0) {
    //         this.toastr.info(`Please select atleast one item.`, 'Info!');
    //         this.MACAssignModalOutward = true;
    //         this.selectedInwardMACAddress = [];
    //         return;
    //     }
    //     // this.checkBoxCount=[]
    //     this.selectedInwardMACAddress.forEach(element => {
    //         element.outwardId = this.outwardIdForMac;
    //     });

    //     this.saveMACMapping(this.selectedInwardMACAddress);
    // }
    // mapMACOutward(): void {
    //     // ✅ Get selected rows based on selected IDs

    //     if (this.macAddMode == "bulk") {
    //         this.uploadOutwardFile();
    //         return;
    //     }

    //     const selectedMAC = this.inWardAllMacList.filter(item =>
    //         this.selectedCheckBoxes.includes(item.id)
    //     );

    //     if (selectedMAC.length === 0) {
    //         this.toastr.info('Please select at least one item.', 'Info!');
    //         return;
    //     }

    //     // ✅ Add outwardId to each selected item
    //     const payload = selectedMAC.map(item => ({
    //         ...item,
    //         outwardId: this.outwardIdForMac
    //     }));
    //     // ✅ Send to API
    //     this.saveMACMapping(payload);
    // }

    mapMACOutward(): void {

        if (this.macAddMode == "bulk") {
            this.uploadOutwardFile();
            return;
        }

        const selectedMAC = this.inWardAllMacList.filter(item =>
            this.selectedCheckBoxes.includes(item.id)
        );

        if (selectedMAC.length === 0) {
            this.toastr.info('Please select at least one item.', 'Info!');
            return;
        }

        const payload = selectedMAC.map(item => ({
            ...item,
            outwardId: this.outwardIdForMac
        }));

        this.saveMACMapping(payload);
    }
    saveMACMapping(selectedMAC: any[]): void {
        this.outwardService.updateMACMappingList(selectedMAC).subscribe(
            (res: any) => {
                // this.selectedInwardMACAddress = null;
                // this.addMACaddress = false;
                if (res.responseCode == 200) {
                    this.toastr.success(`Items are added successfully.`, 'Success!');
                    this.checkBoxCount = [];
                    this.MACAssignModalOutward = false;
                    this.selectedCheckBoxes = [];
                    // this.saveSelectedCheckBoxes();
                    this.getOutwardList("");
                    this.closeDialog();

                }
                if (res.responseCode == 406) {
                    this.toastr.info(`${res.responseMessage}`, 'Info!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }
    pageChangedProductList(event: PageEvent): void {
        this.currentPageProductListdata = event.pageIndex + 1;
        this.productListdataitemsPerPage = event.pageSize;
        if (!this.searchkey) {
            this.getOutwardList("");
        } else {
            this.searchOutwardData();
        }
    }

    getFilterDestioan(event) {
        if (this.outwardFormGroup.controls.destinationType.value) {
            this.getDestinations(this.outwardFormGroup.controls.destinationType.value);
            const destinationData = this.destinations;
            const destinationTypeVal = this.outwardFormGroup.controls.destinationType.value;
            const sourceTypeVal = this.outwardFormGroup.controls.sourceType.value;
            const sourceVal = event.value;
            if (sourceTypeVal != "" && destinationTypeVal != "") {
                if (sourceTypeVal == destinationTypeVal) {
                    if (sourceVal != "") {
                        this.destinations = destinationData.filter(item => item.id != sourceVal);
                    }
                }
            }
        }
    }

    canExit() {
        if (!this.outwardFormGroup.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
                this.confirmationService.confirm({
                    header: "Alert",
                    message: "The filled data will be lost. Do you want to continue? (Yes/No)",
                    icon: "pi pi-info-circle",
                    accept: () => {
                        observer.next(true);
                        observer.complete();
                    },
                    reject: () => {
                        observer.next(false);
                        observer.complete();
                    }
                });
                return false;
            });
        }
    }
    getOutwardDetails(id) {
        const url = "/outwards/" + id;
        this.outwardService.getMethod(url).subscribe(
            (res: any) => {
                this.viewOutwardDetails = res.data;
                console.log(this.viewOutwardDetails, "this.viewOutwardDetails");
                // this.listView = false;
                this.createView = false;
                this.detailView = true;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
        this.viewDetailDialogOutward();
    }

    deleteShowMACMapping(product: any) {
        if (product) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this inward item?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });
        }
    }

    outwardList() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
    }

    // showMac(outward): void {
    //     this.selectedInwardMACAddress = [];
    //     const product = outward.productId;
    //     const deviceType = product.productCategory.deviceType;
    //     this.hasMac = product.productCategory.hasMac;
    //     this.hasSerial = product.productCategory.hasSerial;
    //     this.outwardIdForMac = outward.id;

    //     if ((deviceType === 'ONU' || !deviceType) && !this.hasMac && !this.hasSerial) {
    //         this.toastr.info('Product type does not allow to add Mac/Serial Number..', 'Info!');
    //         this.addMACaddress = false;
    //         return;
    //     }
    //     this.MACShowModal = true;
    //     this.outwardData = outward;

    //     this.displayedMacShowColumns = ['id'];

    //     if (deviceType === 'SIM') {
    //         this.displayedMacShowColumns.push('IMSI', 'ICCID', 'KIK');
    //     } else if (deviceType === 'MSISDN') {
    //         this.displayedMacShowColumns.push('msisdn');
    //     } else {
    //         if (this.hasMac) this.displayedMacShowColumns.push('macAddress');
    //         if (this.hasSerial) this.displayedMacShowColumns.push('serialNumber');
    //     }

    //     this.displayedMacShowColumns.push('assetId', 'ItemType');

    //     this.showItem(
    //         outward.id,
    //         outward.productId.id,
    //         outward.destinationId,
    //         outward.destinationType,
    //         true
    //     );
    //     this.addMACaddress = true;
    // }
    showMac(outward): void {
        this.selectedInwardMACAddress = [];
        const product = outward.productId;
        const deviceType = product.productCategory.deviceType;
        this.hasMac = product.productCategory.hasMac;
        this.hasSerial = product.productCategory.hasSerial;
        this.outwardIdForMac = outward.id;

        if ((deviceType === 'ONU' || !deviceType) && !this.hasMac && !this.hasSerial) {
            this.toastr.info('Product type does not allow to add Mac/Serial Number..', 'Info!');
            this.addMACaddress = false;
            return;
        }
        this.MACShowModal = true;
        this.outwardData = outward;

        this.displayedMacShowColumns = ['id'];

        if (deviceType === 'SIM') {
            this.displayedMacShowColumns.push('IMSI', 'ICCID', 'KIK');
        } else if (deviceType === 'MSISDN') {
            this.displayedMacShowColumns.push('msisdn');
        } else {
            if (this.hasMac) this.displayedMacShowColumns.push('macAddress');
            if (this.hasSerial) this.displayedMacShowColumns.push('serialNumber');
        }

        this.displayedMacShowColumns.push('assetId', 'ItemType');

        this.showItem(
            outward.id,
            outward.productId.id,
            outward.destinationId,
            outward.destinationType,
            true
        );
        this.addMACaddress = true;
    }

    showItem(outwardId, productId, destinationId, destinationType, viewMappingDialog = false) {
        const currentPage = this.currentPageOutwardMapMapping;
        let body = {
            page: currentPage,
            pageSize: this.outwardMappingListitemsPerPage
        };
        this.inwardMacList = [];
        this.outwardId = outwardId;
        this.macDetailsArray = this.fb.array([]);
        this.outwardService
            .showItems(outwardId, destinationId, destinationType, productId, body, viewMappingDialog)
            .subscribe((res: any) => {
                this.inwardlength = res.dataList.length;
                this.inwardMacList = res.dataList;
                this.outwardMappingListdatatotalRecords = res.totalRecords;
                if (this.viewMappingDialogRef?.componentInstance) {
                    this.viewMappingDialogRef.componentInstance.updateList(this.inwardMacList, this.outwardMappingListdatatotalRecords);
                }
                if (viewMappingDialog) {
                    this.ViewMappingListDialogRef = this.dialog.open(ViewMappingDetailsComponent, {
                        width: '80%',
                        autoFocus: false,
                        disableClose: true,
                        data: {
                            mappingList: this.inwardMacList,
                            mappingListdatatotalRecords: this.outwardMappingListdatatotalRecords,
                            mappingListitemsPerPage: this.outwardMappingListitemsPerPage,
                            macOptionSelect: this.macOptionSelect,
                            searchOption: this.searchOption,
                            searchMappingDeatil: this.searchMacDeatil,
                            displayedMacShowColumns: this.displayedMacShowColumns,
                            headerTitle: `Outward MAC Mapping`,
                            outwardIdForMac: this.outwardIdForMac
                        }
                    });


                    // this.ViewMappingListDialogRef.componentInstance.searchMacEvent.subscribe((searchData: { searchOption: string, searchMacDeatil: string }) => {
                    //     this.searchOption = searchData.searchOption;
                    //     this.searchMacDeatil = searchData.searchMacDeatil;
                    //     this.searchMac();
                    // });

                    // this.ViewMappingListDialogRef.componentInstance.clearMacEvent.subscribe(() => {
                    //     this.clearMac();
                    // });
                    // this.ViewMappingListDialogRef.componentInstance.paginateEvent.subscribe((event: any) => {
                    //     this.paginate(event);
                    // }
                    // );

                    // this.ViewMappingListDialogRef.componentInstance.inventoryDetailsEvent.subscribe((itemId: number) => {
                    //     this.InventoryDetails(itemId);
                    // }
                    // );
                    // this.ViewMappingListDialogRef.componentInstance.deleteShowMACMappingEvent.subscribe((item: any) => {
                    //     this.deleteShowMACMapping(item);
                    // }
                    // );
                    this.ViewMappingListEvents(this.ViewMappingListDialogRef);
                    this.viewMappingDialogRef = this.ViewMappingListDialogRef;
                }
                return this.inwardMacList;;
            });
    }

    ViewMappingListEvents(ViewMappingListDialogRef: any) {
        const mappingList = ViewMappingListDialogRef.componentInstance;

        mappingList.searchMacEvent.subscribe((data: { searchOption: string; searchMacDeatil: string }) => {
            this.searchOption = data.searchOption;
            this.searchMacDeatil = data.searchMacDeatil;
            this.searchMac();
        });

        mappingList.clearMacEvent.subscribe(() => this.clearMac());

        mappingList.paginateEvent.subscribe((event: any) => this.paginate(event));

        mappingList.inventoryDetailsEvent.subscribe((itemId: number) => this.InventoryDetails(itemId));

        mappingList.deleteShowMACMappingEvent.subscribe((item: any) => this.deleteShowMACMapping(item));
    }
    showMacAddressDialog() {
        this.dialogRefMacAddress = this.dialog.open(this.macAddressDialog, {
            width: '1000px',
            maxWidth: '80vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRefMacAddress.afterClosed().subscribe(result => {
            this.dialogRefMacAddress = null;
        });
    }
    // showItem(outwardId, productId, destinationId, destinationType) {
    // showItem(outwardId, productId, destinationId, destinationType, viewMappingDialog = false) {
    //     let currentPage;
    //     currentPage = this.currentPageOutwardMapMapping;
    //     let body = {
    //         page: currentPage,
    //         pageSize: this.outwardMappingListitemsPerPage
    //     };
    //     this.inwardMacList = [];
    //     this.outwardId = outwardId;
    //     this.macDetailsArray = this.fb.array([]);
    //     this.outwardService
    //         .showItems(outwardId, destinationId, destinationType, productId, body)
    //         .subscribe(
    //             (res: any) => {
    //                 this.inwardlength = res.dataList.length;
    //                 this.inwardMacList = res.dataList;
    //                 this.outwardMappingListdatatotalRecords = res.totalRecords;
    //             },
    //             (error: any) => {
    //                 this.toastr.error(`${error.error.ERROR}`, 'Error!');
    //             }
    //         );
    // }

    quantityOutValidation(event: any) {
        var num = String.fromCharCode(event.which);
        if (!/[0-9]/.test(num)) {
            event.preventDefault();
        }
    }

    clearFilterGlobal(table: Table) {
        this.fileterGlobal = "";
        table.clear();
    }
    clearFilterGlobal1(table: Table) {
        this.fileterGlobal1 = "";
        table.clear();
    }
    onclosed() {
        this.currentPageOutwardMapMapping = 1;
        this.outwardMappingListitemsPerPage = 20;
        this.currentPageOutwardMacMapping = 1;
        this.outwardMappingMacListitemsPerPage = 20;
        this.newFirst = 0;
        this.customRows = 20;
        this.newFirstMac = 0;
        this.fileterGlobal1 = "";
        this.fileterGlobal = "";
        this.checkBoxCount = [];
        this.selectedCheckBoxes = [];
        this.MACShowModal = false;
        this.MACAssignModalOutward = false;
        this.searchOption = "";
        this.searchMacDeatil = "";
    }
    InventoryDetails(itemId) {
        this.inwardService.getByItemId(itemId).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.specDetailsShow = true;
                    this.inventorySpecificationDetails = response.dataList;
                    this.inventoryDetailModal = true;
                    this.inventoryDetailDialogModel();
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    inventoryDetailDialogModel() {
        this.dialogRefInventoryDetail = this.dialog.open(this.inventoryDetailDialog, {
            width: '1500px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRefInventoryDetail.afterClosed().subscribe(result => {
            this.dialogRefInventoryDetail = null;
        });
    }
    closeInventoryDetailModal() {
        this.inventoryDetailModal = false;
        this.specDetailsShow = false;
        this.searchOption = "";
        this.searchMacDeatil = "";
    }

    paginate(event: PageEvent) {
        this.newFirst = event.pageIndex * event.pageSize;
        this.outwardMappingListitemsPerPage = event.pageSize;
        this.currentPageOutwardMapMapping = event.pageIndex + 1;
        this.searchMacDeatil
            ? this.searchMac(true)
            : this.showItem(
                this.outwardData.id,
                this.outwardData.productId.id,
                this.outwardData.destinationId,
                this.outwardData.destinationType
            );
    }

    paginateOutwardMapping(event: PageEvent) {
        this.newFirstMac = event.pageIndex * event.pageSize;
        this.outwardMappingMacListitemsPerPage = event.pageSize;
        this.currentPageOutwardMacMapping = event.pageIndex + 1;
        if (this.searchMacDeatil) {
            this.searchAddMac(true);
        } else {
            this.getItems(
                this.outwardData.productId.id,
                this.outwardData.sourceId,
                this.outwardData.sourceType
            ).subscribe(
                (res: any) => {
                    this.inwardlength = res?.dataList?.length;
                    this.inwardMacList = res.dataList;
                    this.inwardMacList.forEach(element => {
                        if (this.inWardAllMacList.findIndex((item) => item.id === element.id) === -1) {
                            this.inWardAllMacList.push(element);
                        }
                    });

                    this.outwardMappingMacListdatatotalRecords = res.totalRecords;
                },
                (error: any) => {
                    this.toastr.error(`${error.error?.ERROR || 'Failed to load data'}`, 'Error!');
                }
            );
        }
    }

    customRows = 20;
    rowsPerPageOptions = [5, 10, 20, 50, 100, 1000, 5000];

    updateRowsPerPage(value: number) {
        if (value === null || value < 1) {
            value = 1;
        }
        if (value > 5000) {
            this.toastr.warning(`Maximum allowed rows per page is 5000`, 'Warn!');
            value = 5000;
        }
        this.customRows = value;
        if (!this.rowsPerPageOptions.includes(value)) {
            this.rowsPerPageOptions = [...this.rowsPerPageOptions, value].sort((a, b) => a - b);
        }
        this.outwardMappingMacListitemsPerPage = value;
        const totalRecords = this.outwardMappingMacListdatatotalRecords;
        const previousFirst = this.newFirstMac;
        const newPage = Math.floor(previousFirst / value);
        const totalPages = Math.ceil(totalRecords / value);
        const validPage = Math.min(newPage, totalPages - 1);
        this.newFirstMac = validPage * value;
        this.currentPageOutwardMacMapping = validPage + 1;
        this.getItems(
            this.outwardData.productId.id,
            this.outwardData.sourceId,
            this.outwardData.sourceType
        );
    }

    searchMac(isPaginating: boolean = false) {
        if (!isPaginating) {
            this.currentPageOutwardMapMapping = 1;
        }
        this.searchMacData.filters[0].filterValue = this.searchMacDeatil;
        this.searchMacData.filters[0].filterColumn = this.searchOption;
        this.searchMacData.productId = this.outwardData.productId.id;
        this.searchMacData.ownerId = this.outwardData.destinationId;
        this.searchMacData.ownerType = this.outwardData.destinationType;
        this.searchMacData.entityId = this.outwardId;
        this.searchMacData.page = this.currentPageOutwardMapMapping;
        this.searchMacData.pageSize = this.outwardMappingListitemsPerPage;
        const url = "/inwards/searchInwardOutwardItem";
        this.inwardService.postMethod(url, this.searchMacData).subscribe(
            (response: any) => {
                this.inwardMacList = response.dataList || [];
                this.inwardMacList.forEach(element => {
                    if (this.inWardAllMacList.findIndex((item) => item.id === element.id) === -1) {
                        this.inWardAllMacList.push(element);
                    }
                });
                this.outwardMappingListdatatotalRecords = response.totalRecords || 0;
            },
            (error: any) => {
                this.toastr.error(`${error.error?.ERROR || 'Search failed'}`, 'Error!');
            }
        );
    }

    clearMac() {
        this.searchOption = "";
        this.searchMacDeatil = "";
        this.currentPageOutwardMapMapping = 1;
        this.outwardMappingListitemsPerPage = 20;
        this.showItem(
            this.outwardData.id,
            this.outwardData.productId.id,
            this.outwardData.destinationId,
            this.outwardData.destinationType
        );
    }
    searchAddMac(isPaginating: boolean = false) {
        if (!isPaginating) {
            this.currentPageOutwardMacMapping = 1;
        }

        this.searchMacData.filters[0].filterValue = this.searchMacDeatil;
        this.searchMacData.filters[0].filterColumn = this.searchOption;
        this.searchMacData.productId = this.outwardData.productId.id;
        this.searchMacData.ownerId = this.outwardData.sourceId;
        this.searchMacData.ownerType = this.outwardData.sourceType;

        this.searchMacData.page = this.currentPageOutwardMacMapping;
        this.searchMacData.pageSize = this.outwardMappingMacListitemsPerPage;

        const url = "/inwards/searchInwardOutwardItem";
        this.inwardService.postMethod(url, this.searchMacData).subscribe(
            (response: any) => {
                this.inwardMacList = response.dataList || [];

                this.inwardMacList.forEach(element => {
                    if (this.inWardAllMacList.findIndex((item) => item.id === element.id) === -1) {
                        this.inWardAllMacList.push(element);
                    }
                });

                this.outwardMappingMacListdatatotalRecords = response.totalRecords || 0;
            },
            (error: any) => {
                this.toastr.error(`${error.error?.ERROR || 'Search failed'}`, 'Error!');
            }
        );
    }

    clearAddMac() {
        this.searchOption = "";
        this.searchMacDeatil = "";
        this.currentPageOutwardMapMapping = 1;
        this.outwardMappingListitemsPerPage = 20;
        this.outwardMappingMacListitemsPerPage = 20;
        this.getItems(
            this.outwardData.productId.id,
            this.outwardData.sourceId,
            this.outwardData.sourceType
        ).subscribe(
            (res: any) => {
                this.inwardMacList = res.dataList || [];
                this.inwardMacList.forEach(element => {
                    if (this.inWardAllMacList.findIndex((item) => item.id === element.id) === -1) {
                        this.inWardAllMacList.push(element);
                    }
                });

                this.outwardMappingMacListdatatotalRecords = res.totalRecords || 0;
            },
            (error: any) => {
                this.toastr.error(`${error.error?.ERROR || 'Failed to load data'}`, 'Error!');
            }
        );
    }

    selMacSearchOption(event) {
        this.searchMacDeatil = "";
        this.optionValue = event;
    }

    deleteConfirmonOutwardDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: `Delete ${this.title}`,
                description: `Are you sure you want to delete "${item.outwardNumber}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteOutward(item.id);
            } else {
            }
        });
    }
    addEditOutwardDialogOpen() {
        this.createView = true;
        this.dialogRef = this.dialog.open(this.addEDitDialogOutward, {
            width: '1500px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.dialogRef = null;
            this.outwardFormGroup.reset();
            this.bulkOutwardsList = [];
            this.outwardCreationMode = 'manual';
            this.outwardFormGroup.get('sourceType')?.enable();
            this.outwardFormGroup.get('source')?.enable();
            this.outwardFormGroup.get('destinationType')?.enable();
            this.outwardFormGroup.get('destination')?.enable();
        });
    }
    viewDetailDialogOutward() {
        this.dialogRefView = this.dialog.open(this.viewOutwardDetail, {
            width: '1000px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRefView.afterClosed().subscribe(result => {
            this.dialogRefView = null;
            this.outwardFormGroup.reset();
        });
    }
    closeDialogView() {
        if (this.dialogRefView) {
            this.dialogRefView.close();
            this.dialogRefView = null;
            this.outwardFormGroup.reset();
        }
    }
    closeDialog() {
        if (this.dialogRef) {
            this.dialogRef.close();
            this.bulkMacFile = null;
            this.showSkippedTable = false;
        }
        if (this.dialogRefMacAssign) {
            this.dialogRefMacAssign.close();
            this.dialogRefMacAssign = null;
        }
        if (this.dialogRefMacAddress) {
            this.dialogRefMacAddress.close();
            this.dialogRefMacAddress = null;
        }
        if (this.dialogRefInventoryDetail) {
            this.dialogRefInventoryDetail.close();
            this.dialogRefInventoryDetail = null;
        }
        this.bulkMacFile = null;
        this.showSkippedTable = false;
        this.getData.data = [];
        this.skippedTotalRecords = 0;
        this.currentPageOutwardMapMapping = 1;
        this.currentPageOutwardMacMapping = 1;
        this.selectedCheckBoxes = [];
        this.inWardAllMacList = [];
        this.inwardMacList = [];
        this.outwardMappingMacListitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    }
    // ---------------------------
    /** Select or deselect all checkboxes */
    // toggleSelectAll(inwardlength: number, selectedLength: number): void {
    //     // If not all selected, select all
    //     if (selectedLength < inwardlength) {
    //         this.selectedCheckBoxes = Array.from({ length: inwardlength }, (_, i) => i);
    //     }
    //     // Otherwise, clear all
    //     else {
    //         this.selectedCheckBoxes = [];
    //     }
    // }

    /** Toggle individual row selection */
    // selectedRow(checked: boolean, inwardlength: number, rowIndex: number, selectedLength: number): void {
    //     if (checked) {
    //         // If currently checked, uncheck
    //         this.selectedCheckBoxes = this.selectedCheckBoxes.filter(i => i !== rowIndex);
    //     } else {
    //         // Otherwise, check it
    //         this.selectedCheckBoxes.push(rowIndex);
    //     }
    // }


    /** Optional: Utility to check if all rows are selected */
    // isAllSelected(): boolean {
    //     return this.selectedCheckBoxes.length === this.inwardlength;
    // }

    // /** Optional: Utility to check if partially selected */
    // isIndeterminate(): boolean {
    //     return (
    //         this.selectedCheckBoxes.length > 0 &&
    //         this.selectedCheckBoxes.length < this.inwardlength
    //     );
    // }



    /** Toggle single checkbox */
    selectedRow(productId: number, checked: boolean): void {
        const index = this.selectedCheckBoxes.indexOf(productId);

        if (checked && index === -1) {
            // Add if checked and not already selected
            this.selectedCheckBoxes.push(productId);
        } else if (!checked && index > -1) {
            // Remove if unchecked
            this.selectedCheckBoxes.splice(index, 1);
        }
    }

    /** Select All / Deselect All */
    toggleSelectAll(checked: boolean): void {
        if (checked) {
            // Select all IDs
            this.selectedCheckBoxes = this.inwardMacList.map(item => item.id);
        } else {
            // Clear all selections
            this.selectedCheckBoxes = [];
        }
    }

    /** Check if all rows are selected */
    isAllSelected(): boolean {
        return (
            this.inwardMacList?.length > 0 &&
            this.selectedCheckBoxes?.length === this.inwardMacList?.length
        );
    }

    /** Check if partially selected */
    isIndeterminate(): boolean {
        return (
            this.selectedCheckBoxes.length > 0 &&
            this.selectedCheckBoxes.length < this.inwardMacList.length
        );
    }
    uploadOutwardFile() {
        const url = `/inoutWardMacMapping/saveManualOutward/upload/${this.outwardIdForMac}?productId=${this.productIdForMac}&ownerId=${this.ownerIdForMac}&ownerType=${this.sourceTypeForMac}`;
        const formData = new FormData();
        formData.append('file', this.bulkMacFile);
        console.log("file for upload file size", this.bulkMacFile.size);
        this.outwardService.uploadFile(url, formData).subscribe({
            next: (res: any) => {
                if (res.responseCode === 200) {
                    this.toastr.success('File uploaded successfully.', 'Success!');
                    if (this.dialogRef) {
                        this.dialogRef.close();
                    }
                    this.dialogRefMacAssign.close();
                    this.clearBulkFile();
                    this.checkBoxCount = [];
                    this.showSkippedTable = false;
                } else if (res.responseCode === 202) {

                    this.showSkippedTable = false;
                }
                else {
                    this.toastr.error(`${res.responseMessage}`, 'Failed!');
                    this.clearBulkFile();
                }
            }
            ,
            error: (err) => {
                console.error('Upload Error', err);
                if (err.status === 400 && err.error?.responseMessage) {
                    this.toastr.error(err.error.responseMessage, 'Failed!');
                    this.clearBulkFile();
                } else {
                    this.toastr.error('Something went wrong while uploading file.', 'Failed!');
                }
            }
        });
    }
    clearBulkFile() {
        this.bulkMacFile = null;
    }
    onDragOver(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        const element = event.currentTarget as HTMLElement;
        element.style.backgroundColor = '#e3f2fd';
        element.style.borderColor = '#1976d2';
    }

    onDragLeave(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        const element = event.currentTarget as HTMLElement;
        element.style.backgroundColor = '#fafafa';
        element.style.borderColor = '#e0e0e0';
    }

    onDrop(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        const element = event.currentTarget as HTMLElement;
        element.style.backgroundColor = '#fafafa';
        element.style.borderColor = '#e0e0e0';

        const files = event.dataTransfer?.files;
        if (files && files.length > 0) {
            const file = files[0];
            const validTypes = ['text/csv', 'application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'];
            if (validTypes.includes(file.type) || file.name.endsWith('.csv') || file.name.endsWith('.xlsx') || file.name.endsWith('.xls')) {
                this.bulkMacFile = file;
            } else {
                this.toastr.error('Please select a valid CSV or Excel file', 'Error!');
            }
        }
    }

    removeBulkFile(): void {
        this.bulkMacFile = null;
    }
    file: any;
    onBulkFileSelect(event: any): void {
        this.file = event.target.files[0];
        if (this.file) {
            const validTypes = ['text/csv', 'application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'];
            if (validTypes.includes(this.file.type) || this.file.name.endsWith('.csv') || this.file.name.endsWith('.xlsx') || this.file.name.endsWith('.xls')) {
                this.bulkMacFile = this.file;
            } else {
                this.toastr.error('Please select a valid CSV or Excel file', 'Error!');
                event.target.value = '';
            }
        }
    }

    getSkipRecord(inwardIdForMac) {
        const body = {
            page: this.currentPageSkipList,
            pageSize: this.skiptemsPerPage
        };
        const url = `/outwards/getAllRemarks/${this.inwardIdForMac}`;
        this.inwardService.postMethod(url, body)
            .subscribe(
                (res: any) => {
                    if (res.responseCode === 200) {


                        // this.dataSource.data = res?.data || [];
                        this.getData.data = res?.dataList || [];
                        this.skippedTotalRecords = res?.totalRecords || 0;
                        this.toastr.success(`${res.responseMessage}`, 'Success');
                    } else if (res.responseCode === 400) {
                        this.toastr.error(`${res.responseMessage}`, 'Failed!');
                    } else {
                        this.toastr.error(`${res.responseMessage}`, 'Failed!');
                    }
                },
                (error: any) => {
                    if (error.error.responseCode == '400') {
                        this.toastr.success(`${error.error.responseMessage}`, 'Success');
                    } else {
                        this.toastr.error(`${error.error.responseMessage}`, 'Failed!');
                    }
                }
            );
    }

    pageChangedSkipList(event: any) {
        this.currentPageSkipList = event.pageIndex + 1;
        this.skiptemsPerPage = event.pageSize;
        this.getSkipRecord(this.inwardIdForMac);
    }

    formatFileSize(bytes: number): string {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    openProductDialog(outward: any) {
        this.selectedProductForDetails = outward;
        this.productDetailsList = [];
        this.isProductDetailsLoading = true;

        const url = `/outwards/findByGroupId/${outward.id}`;
        this.outwardService.getMethod(url).subscribe(
            (res: any) => {
                this.isProductDetailsLoading = false;
                if (res && res.dataList) {
                    this.productDetailsList = res.dataList;
                } else if (res && res.data) {
                    this.productDetailsList = Array.isArray(res.data) ? res.data : [res.data];
                }
            },
            (error: any) => {
                this.isProductDetailsLoading = false;
                this.toastr.error('Failed to fetch product details', 'Error!');
            }
        );

        this.productDialogRef = this.dialog.open(this.productDetailsDialog, {
            width: '900px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });
    }

    closeProductDialog() {
        if (this.productDialogRef) {
            this.productDialogRef.close();
        }
        this.productDialogRef = null as any;
        this.selectedProductForDetails = null;
        this.productDetailsList = [];
    }
}

