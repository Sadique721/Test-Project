import { values } from "lodash";
import { DatePipe } from "@angular/common";
import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormArray, UntypedFormGroup, Validators, UntypedFormControl } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { ToastrService } from 'ngx-toastr'; // Replace MessageService with ToastrService
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";

import {
    ITEMS_PER_PAGE,
    PER_PAGE_ITEMS,
    pageLimitOptions
} from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { InwardService } from "src/app/service/inward.service";
import { LoginService } from "src/app/service/login.service";
import { formatDate } from "@angular/common";
import { Regex } from "src/app/constants/regex";
import { Observable, Observer } from "rxjs";
import { Table } from "primeng/table";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { INVENTORYS } from "src/app/constants/aclConstants";
import { HttpClient } from "@angular/common/http";
import { element } from "protractor";
import { log } from "console";
import moment from "moment";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DialogRef } from "@angular/cdk/dialog";
import { MatPaginator } from "@angular/material/paginator";

declare var $: any;

@Component({
    selector: "app-inwards",
    templateUrl: "./inwards.component.html",
    styleUrls: ["./inwards.component.css"],
    standalone: false
})
export class InwardsComponent implements OnInit {
    inwardsForm: UntypedFormControl;
    expiryTime: "";
    expiryTimeUnit: string;
    productData: any;
    startDateTime: Date;
    inwardFormGroup: UntypedFormGroup;
    specificationParametersDTO: UntypedFormGroup;
    specificationParametersDTOList: UntypedFormArray;
    submitted = false;
    stateData: any = {};
    countryListData: any;
    currentPageProductListdata = 1;
    productListdataitemsPerPage = ITEMS_PER_PAGE;
    searchOptionSelect = this.commondropdownService.customerSearchOption1;
    macOptionSelect = this.commondropdownService.macSearchOption;
    productListdatatotalRecords: any;
    countryPojo: any = {};
    inwardListData: any[] = [];
    inwardDetails: any = [];
    ifInwardDetails = false;
    IfPersonalInwardDataShow = true;
    viewCountryListData: any;
    viewStateListData: any;
    isStateEdit = false;
    searchData: any;
    AclClassConstants: any;
    AclConstants: any;
    specificationValue = "";
    specificationValue1 = "";
    mandatory: boolean = false;
    showTable: boolean = false;
    MACShowModal: boolean = false;
    approveChangeStatusModal: boolean = false;
    rejectChangeStatusModal: boolean = false;
    parameterList: any[] = [];
    pageLimitOptions = pageLimitOptions;
    showItemPerPage = 5;
    searchkey: string;
    public loginService: LoginService;
    editMode: boolean;
    selectedProductForEdit: any;
    isEditMode: boolean = false;
    selectWareHouseView: boolean;
    assignInwardForm: UntypedFormGroup;
    rejectInwardForm: UntypedFormGroup;
    assignInwardSubmitted: boolean = false;
    rejectInwardSubmitted: boolean = false;
    pincodeDeatils: any;
    approveInwardData = [];
    macForm: UntypedFormGroup;
    macFormList: UntypedFormArray;
    externalItemIdForMac: number;
    externalItemMacList: any[] = [];
    assignInwardID: any;
    inwardIDStatus: any;
    assignInwardProductId: any;
    uniqueIdCounter = 1;
    searchOption: any;
    status = [
        { label: "Active", value: "ACTIVE" },
        { label: "Inactive", value: "INACTIVE" }
    ];
    createView = false;
    listView = true;
    @ViewChild("closebutton") closebutton;
    @ViewChild("btnClose") btnClose;
    countryList = [];
    stateList = [];
    cityList = [];
    pincodeList = [];
    allpincodeNumber: any = [];
    unit = "";
    hasOEMConsider: boolean = false;
    warrantyDays = "";
    warrantyPeriods = "";
    products: any[] = [];
    warehouses: any[] = [];
    types = [
        { label: "New", value: "New" },
        { label: "Refurbished", value: "Refurbished" },
        { label: "Old", value: "Old" }
    ];
    pipe = new DatePipe("en-US");
    usedQty: number;
    inTransitQty: number;
    showQtyError: boolean;
    addMACaddress: boolean = false;
    inwardIdForMac: number;
    totalMacSerial: number;
    showIntransitQtyError: boolean;
    inwardMacList: any[] = [];
    itemList: any[] = [];
    macAdderessInput = "";
    inwardId = "";
    hasMac: boolean = true;
    hasSerial: boolean = true;
    inwardHasMac: boolean;
    inwardHasSeial: boolean;
    viewInwardsDetails: any;
    paramValue = "";
    detailView: boolean = false;
    qtyErroMsg = "";
    fileterGlobal1: any = "";
    searchDeatil: string;
    searchMacDeatil: string;
    searchkey2: string;
    searchInward: any = "";
    searchInward1: any = "";
    custId: any;
    isInwardView: boolean = false;
    isInwardEdit: boolean = false;
    isInwardDelete: boolean = false;
    showSpecification: boolean = false;
    isMandatory: boolean = false;
    editAccess: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    showMacAddressAccess: boolean = false;
    rejectAccess: boolean = false;
    calculatedExpiryDateTime: string;
    inventoryDetailData: any;
    specDetailsShow: boolean = false;
    inventorySpecificationDetails: any[] = [];
    inventoryDetailModal: boolean = false;
    isBuldUpload: boolean = false;
    uploadDocForm: UntypedFormGroup;
    selectedFileUploadPreview: File[] = [];
    selectedFile: any;
    uploadInwardId: number;
    macSubmitted: boolean = false;
    inwardDeleteData: any;
    currentPageInwardMapMapping = 1;
    inwardMappingListitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    inwardMappingListdatatotalRecords: any;
    newFirst = 0;
    searchMacData: any;
    optionValue: any;
    bulkInwardList: any[] = [];
    isBulkLoading: boolean = false;




    dataSourceData = [{}];
    displayedColumns: string[] = [
        'id',
        'inwardNumber',
        'productName',
        'type',
        'qty',
        'inTransitQty',
        'createdBy',
        'status',
        'approvalStatus',
        'action'
    ];


    displayedColumnsdiloag: string[] = [
        'productName',
        'qty',
        'inTransitQty',
        'inwardNumber',
        'status',
        'approvalStatus'
    ];

    dataSource = this.bulkInwardList; // your API data

    displayedMacShowColumns: string[] = [
        'id',
        ...(this.hasMac ? ['macAddress'] : []),
        ...(this.hasSerial ? ['serialNumber'] : []),
        'assetId',
        'condition',
        'action'
    ];

    displayedAddMacColumns: string[] = ['macAddress', 'serialNumber', 'actions'];
    displayedinventoryColumns = ['paramName', 'isMandatory', 'paramValue'];
    displayedItemsColumns = [
        'assetId',
        'macAddress',
        'serialNumber',
        'oemWarrantyStatus',
        'oemWarrantyRemainingDays'
    ];

    @ViewChild('inwardMactable') inwardMactable!: MatTable<any>;
    @ViewChild('addMacTableTable') addMacTableTable!: MatTable<any>;

    @ViewChild('AddEditDialog') AddEditDialog!: TemplateRef<any>;
    @ViewChild('detailsDialog') detailsDialog!: TemplateRef<any>;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild('MACShowModalDialog') MACShowModalDialog!: TemplateRef<any>;
    @ViewChild('addMACaddressDialog') addMACaddressDialog!: TemplateRef<any>;
    @ViewChild('approveChangeStatusModalDialog') approveChangeStatusModalDialog!: TemplateRef<any>;
    @ViewChild('rejectChangeStatusModalDialog') rejectChangeStatusModalDialog!: TemplateRef<any>;
    @ViewChild('inventoryDetailModalDialog') inventoryDetailModalDialog!: TemplateRef<any>;
    @ViewChild('isBuldUploadDialog') isBuldUploadDialog!: TemplateRef<any>;
    @ViewChild('bulkProductDetailsDialog') bulkProductDetailsDialog!: TemplateRef<any>;

    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;

    MACShowModalDialogRef!: MatDialogRef<any>;
    showSkippedTable: boolean = false;
    skippedRecordsData: any;
    displayedMacShowColumns2: any[] = [];
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
            { field: 'msisdn', header: 'MSISDN', width: '150px' }
        ],
        DEFAULT: [
            { field: 'macAddress', header: 'MAC ADDRESS', width: '200px' },
            { field: 'serialNumber', header: 'SERIAL NUMBER', width: '200px' },
            { field: 'reason', header: 'REASON', width: '120px' }
        ]
    };
    displayedMacShowColumnsFields: string[] = [];
    getData = new MatTableDataSource<any>([]);
    skiptemsPerPage = ITEMS_PER_PAGE;
    skippedTotalRecords: any;
    constructor(
        private customerManagementService: CustomermanagementService,
        private http: HttpClient,
        private fb: UntypedFormBuilder,
        private formBuilder: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService, // Replace MessageService with ToastrService
        private inwardService: InwardService,
        loginService: LoginService, private dialog: MatDialog,
        public commondropdownService: CommondropdownService
    ) {
        this.createAccess = loginService.hasPermission(INVENTORYS.INVEN_INWARDS_CREATE);
        this.deleteAccess = loginService.hasPermission(INVENTORYS.INVEN_INWARDS_DELETE);
        this.editAccess = loginService.hasPermission(INVENTORYS.INVEN_INWARDS_EDIT);
        this.rejectAccess = loginService.hasPermission(INVENTORYS.INWARD_REJECT);
        this.showMacAddressAccess = loginService.hasPermission(INVENTORYS.INVEN_INWARDS_SHOW_MAC);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.isInwardEdit = this.editAccess;
        this.editMode = !this.createAccess && this.editAccess ? true : false;
        this.specificationParametersDTOList = this.formBuilder.array([]);
        this.macFormList = this.fb.array([]);
        this.inwardsForm = new UntypedFormControl();
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INVENTORYS.INVEN_INWARDS_DELETE) || this.loginService.hasPermission(INVENTORYS.INVEN_INWARDS_EDIT) || this.loginService.hasPermission(INVENTORYS.INWARD_REJECT) || this.loginService.hasPermission(INVENTORYS.INVEN_INWARDS_SHOW_MAC)) {
            return [
                'id',
                'inwardNumber',
                'productName',
                'type',
                'qty',
                'inTransitQty',
                'createdBy',
                'status',
                'approvalStatus',
                'action'
            ];
        } else {
            return [
                'id',
                'inwardNumber',
                'productName',
                'type',
                'qty',
                'inTransitQty',
                'createdBy',
                'status',
                'approvalStatus'
            ];
        }
    }

    ngOnInit(): void {
        this.specificationParametersDTO = this.formBuilder.group({
            defaultValue: [""],
            paramValue: ["", Validators.required],
            id: [""],
            identityKey: [""],
            isMandatory: [false],
            mvnoId: [""],
            paramName: [""],
            pcid: [""]
        });
        this.macForm = this.fb.group({
            macAddress: [""],
            serialNumber: [""]
        });
        this.inwardFormGroup = this.formBuilder.group({
            id: [""],
            productId: ["", Validators.required],
            specificationParametersDTOList: this.specificationParametersDTOList,
            macFormList: this.macFormList,
            qty: [""],
            inwardDateTime: [new Date(), Validators.required],
            destinationId: ["", Validators.required],
            destinationType: [""],
            status: ["", Validators.required],
            type: ["", Validators.required],
            description: ["", Validators.required],
            startDateTime: [""],
            expiryDateTime: [""],
            inwardNumber: [""],
            mvnoId: [""],
            unusedQty: [""],
            usedQty: [""],
            inTransitQty: [
                "",
                [Validators.required, Validators.pattern(Regex.numeric), Validators.min(1)]
            ],
            outTransitQty: [""],
            rejectedQty: [""],
            totalMacSerial: [""],
            unit: ""
        });
        this.inwardFormGroup?.get("unit")?.setValue("-");
        this.inwardFormGroup?.get("unit")?.disable();
        this.macFormList = this.macForm.get("macFormList") as UntypedFormArray;
        this.assignInwardForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.rejectInwardForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.uploadDocForm = this.fb.group({
            file: ["", Validators.required]
        });
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
            entityType: "inward"
        };

        this.calculateExpiryDateTime();
        this.getInwardList("");
        this.inwardFormGroup.get("inTransitQty").valueChanges.subscribe(val => {
            const qty: number = val;
            const totalMacSerial = this.inwardFormGroup.get("totalMacSerial").value;
            var letters = /^[A-Za-z]+$/;
            this.qtyErroMsg = "";
            this.showQtyError = false;
            if (val != null) {
                if (String(val).match(letters)) {
                    this.showQtyError = true;
                    this.qtyErroMsg = "Only Numeric value are allowed.";
                } else if (qty < 0) {
                    this.showQtyError = true;
                    this.qtyErroMsg = "Quantity must be greater than 0.";
                } else if (qty === 0) {
                    this.showQtyError = true;
                    this.qtyErroMsg = "Quantity must not be 0.";
                } else if (qty < totalMacSerial) {
                    this.showQtyError = true;
                    this.qtyErroMsg = "Quantity must be greater than total added mac serial.";
                } else {
                    this.showQtyError = false;
                }
            } else {
                if (this.editMode) {
                    this.showQtyError = true;
                    this.qtyErroMsg = "Please enter quantity.";
                }
            }
        });
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageProductListdata > 1) {
            this.currentPageProductListdata = 1;
        }
        if (!this.searchkey) {
            this.getInwardList(this.showItemPerPage);
        } else {
            this.searchInwardData();
        }
    }

    getInwardList(list) {
        this.inwardListData = [];
        let size: number;
        this.searchkey = "";
        const page = this.currentPageProductListdata;
        if (list) {
            size = list;
            this.productListdataitemsPerPage = list;
        } else {
            size = this.productListdataitemsPerPage;
        }

        const plandata = {
            page,
            pageSize: this.productListdataitemsPerPage
        };
        this.inwardService.getAll(plandata).subscribe(
            (response: any) => {
                this.inwardListData = response.dataList;
                this.productListdatatotalRecords = response.totalRecords;
                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    submit(dialogRef) {
        this.submitted = true;
        if (this.inwardFormGroup.valid && !this.showQtyError && !this.showIntransitQtyError) {
            if (this.editMode) {
                this.inwardService.update(this.mapObject()).subscribe(
                    (res: any) => {
                        if (res.responseCode == 406) {
                            this.toastr.info(`${res.responseMessage}`, 'Info!');
                        } else {
                            this.toastr.success(`Successfully Updated`, 'Success!');
                            dialogRef.close()
                            this.clearSearchInward();
                            this.inwardFormGroup.patchValue({
                                inwardDateTime: new Date()
                            });
                            this.editMode = false;
                            this.submitted = false;
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            } else {
                this.inwardService.save(this.mapObject()).subscribe(
                    (res: any) => {
                        if (res.responseCode == 406) {
                            this.toastr.info(`${res.responseMessage}`, 'Info!');
                        } else {
                            this.toastr.success(`Successfully Created`, 'Success!');
                            dialogRef.close()
                            this.submitted = false;
                            this.clearSearchInward();
                            this.inwardFormGroup.patchValue({
                                inwardDateTime: new Date()
                            });
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            }
        }
    }

    selSearchOption(event) {
        this.searchDeatil = "";
    }

    mapObject = () => {
        const inwardValues = this.inwardFormGroup.getRawValue();
        const inward = {
            id: "",
            productId: "",
            qty: 0,
            inwardDateTime: "",
            destinationId: "",
            destinationType: "Warehouse",
            type: "",
            description: "",
            startDateTime: "",
            expiryDateTime: "",
            status: "",
            inwardNumber: "",
            inTransitQty: "",
            mvnoId: "",
            usedQty: "",
            unusedQty: "",
            outTransitQty: "",
            rejectedQty: "",
            totalMacSerial: 0,
            specificationParametersDTOList: ""
        };
        inward.id = inwardValues.id ? inwardValues.id : null;
        inward.productId = inwardValues.productId;
        inward.qty = inwardValues.qty;
        inward.status = inwardValues.status;
        inward.inwardDateTime = inwardValues.inwardDateTime;
        inward.destinationId = inwardValues.destinationId;
        inward.specificationParametersDTOList = this.specificationParametersDTOList.value.map(
            ({ paramValues, ...rest }) => rest
        );
        inward.type = inwardValues.type;
        inward.description = inwardValues.description;
        inward.startDateTime = inwardValues.startDateTime;
        inward.expiryDateTime = inwardValues.expiryDateTime;
        inward.inwardNumber = inwardValues.inwardNumber ? inwardValues.inwardNumber : "";
        inward.mvnoId = null;
        inward.usedQty = inwardValues.usedQty;
        inward.unusedQty = inwardValues.unusedQty;
        inward.inTransitQty = inwardValues.inTransitQty;
        inward.outTransitQty = inwardValues.outTransitQty;
        inward.rejectedQty = inwardValues.rejectedQty;
        inward.totalMacSerial = inwardValues.totalMacSerial;
        return inward;
    };

    clearMacMapping() {
        this.addMACaddress = false;
        this.inwardMacList = [];
    }

    addMacMapping(dialogRef) {
        const macList = [];
        this.inwardMacList.forEach(item => {
            macList.push({
                macAddress: item.macAddress,
                serialNumber: item.serialNumber
            });
        });
        let data = {
            inwardId: this.inwardId,
            macSerialListDTOList: macList
        };
        this.inwardService.postMethod("/inwards/saveManualMacSerial", data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    dialogRef.close()
                    this.clearMacMapping();
                    this.macForm.reset();
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    editInward(id) {
        this.editMode = true;
        this.dialog.open(this.AddEditDialog, {
            width: '80%',
            disableClose: true
        });
        this.inwardService.getAllProducts().subscribe((res: any) => {
            this.products = res.dataList;
        });
        this.inwardService.getAllWareHouse().subscribe((res: any) => {
            this.warehouses = res.dataList;
        });

        this.inwardDetails = [];
        this.ifInwardDetails = true;
        this.IfPersonalInwardDataShow = true;
        if (id) {
            const url = "/inwards/" + id;
            this.inwardService.getMethod(url).subscribe(
                (response: any) => {
                    const inwardEdit = response.data;
                    this.inwardFormGroup.patchValue({
                        id: inwardEdit.id,
                        productId: inwardEdit.productId.id,
                        qty: inwardEdit.qty,
                        status: inwardEdit.status,
                        inwardDateTime: new Date(inwardEdit.inwardDateTime),
                        destinationId: inwardEdit.destinationId,
                        unit: inwardEdit?.productId?.productCategory?.unit || "",
                        destinationType: inwardEdit.destinationType,
                        type: inwardEdit.type,
                        description: inwardEdit.description,
                        startDateTime: inwardEdit.startDateTime,
                        expiryDateTime: inwardEdit.expiryDateTime,
                        inwardNumber: inwardEdit.inwardNumber,
                        mvnoId: [""],
                        usedQty: inwardEdit.usedQty,
                        unusedQty: inwardEdit.unusedQty,
                        inTransitQty: inwardEdit.inTransitQty,
                        totalMacSerial: inwardEdit.totalMacSerial
                    });
                    this.hasOEMConsider = this.products.find(
                        element => element.id == inwardEdit.productId.id
                    ).hasOEMConsider;
                    this.specificationParametersDTOList = this.formBuilder.array([]);
                    inwardEdit.specificationParametersDTOList?.forEach(element => {
                        let newArray;
                        let listData = this.fb.array([]);
                        if (element.paramMultiValues && element.paramMultiValues.length > 0) {
                            element.paramMultiValues.forEach(data => {
                                listData.push(
                                    this.formBuilder.group({
                                        value: data,
                                        label: data
                                    })
                                );
                            });
                        }

                        this.specificationParametersDTOList.push(
                            this.formBuilder.group({
                                defaultValue: [element.defaultValue],
                                paramValue: [element.paramValue],
                                id: [element.id],
                                identityKey: [element.identityKey],
                                isMandatory: [element.isMandatory],
                                mvnoId: [element.mvnoId],
                                paramName: [element.paramName],
                                pcid: [element.pcid],
                                isMultiValueParam: [element.isMultiValueParam],
                                paramValues: listData
                            })
                        );
                    });
                    this.showSpecification = this.specificationParametersDTOList.value.length > 0;
                    this.usedQty = inwardEdit.usedQty;
                    this.inTransitQty = inwardEdit.inTransitQty;
                    this.totalMacSerial = inwardEdit.totalMacSerial;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }
    currentPageSkipList = 1;
    pageChangedSkipList(event: any) {

        this.currentPageSkipList = event.pageIndex + 1;  // pageIndex starts from 0
        this.skiptemsPerPage = event.pageSize;

        this.getSkipRecord(this.uploadInwardId);   // common API calling method
    }
    getSkipRecord(uploadInwardId) {
        const body = {
            page: this.currentPageSkipList,
            pageSize: this.skiptemsPerPage
        };
        const url = `/inwards/getAllRemarks/${uploadInwardId}`;
        this.inwardService.postMethod(url, body)
            .subscribe(
                (res: any) => {
                    if (res.responseCode === 200) {
                        // this.dataSource.data = res?.data || [];
                        this.getData.data = res?.dataList || [];
                        this.toastr.success(`${res.responseMessage}`, 'Success');
                        this.skippedTotalRecords = res?.totalRecords || 0;
                    } else if (res.responseCode === 400) {
                        this.toastr.error(`${res.responseMessage}`, 'Failed!');
                    }
                    else {
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
    searchInwardData() {
        if (this.paginator) {
            this.paginator.pageIndex = 0;
        }
        if (!this.searchkey || this.searchkey !== this.searchInward) {
            this.currentPageProductListdata = 1;
        }
        this.searchkey = this.searchInward;
        if (this.showItemPerPage) {
            this.productListdataitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchInward;
        this.searchData.filter[0].filterColumn = this.searchInward1.trim();
        const page = {
            page: this.currentPageProductListdata,
            pageSize: this.productListdataitemsPerPage,
            sortBy: "id",
            sortOrder: 0
        };
        const url =
            "/inwards/search?page=" +
            page.page +
            "&pageSize=" +
            page.pageSize +
            "&sortBy=" +
            page.sortBy +
            "&sortOrder=" +
            page.sortOrder;

        this.inwardService.postMethod(url, this.searchData).subscribe(
            (res: any) => {
                if (res.responseCode === 200) {
                    this.inwardListData = res.dataList;
                    const list = this.inwardListData;
                    const filterList = list.filter(cust => cust.id !== this.custId);
                    this.inwardListData = filterList;
                    this.productListdatatotalRecords = res.totalRecords;
                } else {
                    this.productListdatatotalRecords = 0;
                    this.toastr.info(`${res.responseMessage}`, 'Info!');
                    this.inwardListData = [];
                }
            },
            (error: any) => {
                this.productListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.inwardListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    clearSearchInward() {
        this.editMode = false;
        this.submitted = false;
        this.searchInward1 = "";
        this.searchInward = "";
        this.searchkey = "";
        this.getInwardList("");
        this.inwardFormGroup.reset();
        this.inwardFormGroup.patchValue({
            inwardDateTime: new Date()
        });
        this.specificationParametersDTOList = this.formBuilder.array([]);
    }

    deleteConfirmInward(productId: number) {
        if (productId) {

            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this inward?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteProduct(productId);
                } else {
                    this.toastr.info('Delete operation was cancelled', 'Info!');
                }
            });
        }
    }

    deleteProduct(productId) {
        this.inwardService.delete(productId).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`Successfully Deleted`, 'Success!');
                }
                this.getInwardList("");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    createWareHouse() {
        this.specificationParametersDTOList = this.formBuilder.array([]);
        this.editMode = false;
        this.inwardFormGroup.reset();
        this.inwardFormGroup.patchValue({
            inwardDateTime: new Date()
        });
        this.hasOEMConsider = false;
        this.inwardService.getAllProducts().subscribe((res: any) => {
            this.products = res.dataList;
        });
        this.inwardService.getAllWareHouse().subscribe((res: any) => {
            this.warehouses = res.dataList;
        });
        this.dialog.open(this.AddEditDialog, {
            width: '80%',
            disableClose: true
        });
    }

    getUnit(event) {
        this.hasOEMConsider = this.products.find(element => element.id == event.value).hasOEMConsider;
        this.unit = this.products.find(element => element.id == event.value)?.productCategory?.unit;
        this.expiryTime = this.products.find(element => element.id == event.value).expiryTime;
        this.expiryTimeUnit = this.products
            .find(element => element.id == event.value)
            .expiryTimeUnit.toString();
        this.inwardFormGroup.controls.unit.setValue(this.unit || "")
        this.optionProductCategoryParameter();
    }

    optionProductCategoryParameter() {
        const productId = this.inwardFormGroup.get("productId").value;
        const url = "/specificationParameters/getSpecificParametersByid?product_id=" + productId;
        this.inwardService.getAllParameter(url).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.specificationParametersDTOList = this.formBuilder.array([]);
                    response.dataList.forEach(element => {
                        let newArray;
                        let listData = this.fb.array([]);
                        if (element.paramMultiValues && element.paramMultiValues.length > 0) {
                            element.paramMultiValues.forEach(data => {
                                listData.push(
                                    this.formBuilder.group({
                                        value: data,
                                        label: data
                                    })
                                );
                            });
                        }
                        this.specificationParametersDTOList.push(
                            this.formBuilder.group({
                                defaultValue: [element.defaultValue],
                                paramValue: [element.defaultValue],
                                id: [element.id],
                                identityKey: [element.identityKey],
                                isMandatory: [element.isMandatory],
                                mvnoId: [element.mvnoId],
                                paramName: [element.paramName],
                                pcid: [element.pcid],
                                isMultiValueParam: [element.isMultiValueParam],
                                paramValues: listData
                            })
                        );
                    });
                    this.showSpecification = this.specificationParametersDTOList.value.length > 0;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedProductList(pageNumber) {
        this.productListdataitemsPerPage = pageNumber.pageSize;
        this.currentPageProductListdata = pageNumber.pageIndex + 1;

        if (!this.searchkey) {
            this.getInwardList("");
        } else {
            this.searchInwardData();
        }
    }

    addMACC(inward) {
        this.inwardId = inward.id;
        this.hasMac = this.inwardListData.find(
            element => element.id == inward.id
        ).productId.productCategory.hasMac;
        this.hasSerial = this.inwardListData.find(
            element => element.id == inward.id
        ).productId.productCategory.hasSerial;

        if (!this.hasMac && !this.hasSerial) {
            this.toastr.info('Product type does not allow to add Mac/Serial Number..', 'Info!');
            this.addMACaddress = false;
            return;
        } else {
            this.inwardIdForMac = inward.id;
            this.macForm = this.fb.group({
                id: [""],
                inwardId: [this.inwardIdForMac],
                outwardId: [null],
                status: ["ACTIVE"],
                macAddress: this.hasMac ? ["", Validators.required] : [null],
                serialNumber: this.hasSerial ? ["", Validators.required] : [null]
            });
            this.addMACaddress = true;
            this.dialog.open(this.addMACaddressDialog, {
                width: '80%',
                disableClose: true
            });
            this.inwardMacList = [];
        }
    }
    addMAC(inward) {
        this.hasMac = this.inwardListData.find(
            element => element.id == inward.id
        ).productId.productCategory.hasMac;
        this.hasSerial = this.inwardListData.find(
            element => element.id == inward.id
        ).productId.productCategory.hasSerial;

        if (!this.hasMac && !this.hasSerial) {
            this.toastr.info('Product type does not allow to add Mac/Serial Number..', 'Info!');
            this.addMACaddress = false;
            return;
        } else {
            this.inwardIdForMac = inward.id;
            this.macForm = this.fb.group({
                id: [""],
                inwardId: [this.inwardIdForMac],
                outwardId: [null],
                status: ["ACTIVE"],
                macAddress: this.hasMac ? ["", Validators.required] : [null],
                serialNumber: this.hasSerial ? ["", Validators.required] : [null]
            });
            this.MACShowModal = true;

            this.MACShowModalDialogRef = this.dialog.open(this.MACShowModalDialog, {
                width: '80%',
                disableClose: true
            });

            this.inwardDeleteData = inward;
            this.showItem(inward.id, inward.productId.id, inward.destinationId, inward.destinationType);
        }
    }

    MACShowModalDialogClose() {
        this.MACShowModalDialogRef.close();
        this.currentPageInwardMapMapping = 1;
    }


    createPolicyDetailsForm(): UntypedFormGroup {
        return this.fb.group({
            id: [""],
            inwardId: [this.inwardIdForMac],
            outwardId: [null],
            status: ["ACTIVE"],
            macAddress: ["", Validators.required],
            serialNumber: ["", Validators.required]
        });
    }

    calculateExpiryDateTime(): void {
        const startDateTime = new Date(this.inwardFormGroup.value.startDateTime);

        if (!isNaN(startDateTime.getTime()) && this.expiryTime && this.expiryTimeUnit) {
            switch (this.expiryTimeUnit.toLowerCase()) {
                case "day":
                    startDateTime.setDate(startDateTime.getDate() + this.expiryTime);
                    break;
                case "month":
                    startDateTime.setMonth(startDateTime.getMonth() + this.expiryTime);
                    break;
                case "year":
                    startDateTime.setFullYear(startDateTime.getFullYear() + this.expiryTime);
                    break;
                default:
                    startDateTime.setDate(startDateTime.getDate() + this.expiryTime);
                    break;
            }
            this.inwardFormGroup.patchValue({
                expiryDateTime: moment(startDateTime).format("yyyy-MM-DD")
            });
        }
    }

    onStartDateChange(): void {
        this.calculateExpiryDateTime();
    }

    approved = false;
    approveChangeStatus(id, productId, outwardId, inward) {
        this.inwardHasMac = inward.productId.productCategory.hasMac;
        this.inwardHasSeial = inward.productId.productCategory.hasSerial;
        if (outwardId != null) {
            if (this.inwardHasMac || this.inwardHasSeial) {
                if (inward.inTransitQty != inward.totalMacSerial) {
                    this.toastr.info('Serial numbers are not fulfilled from outward.', 'Info!');
                } else {
                    this.approveChangeStatusModal = true;
                    this.assignInwardID = id;
                    this.assignInwardProductId = productId;
                    this.dialog.open(this.approveChangeStatusModalDialog, {
                        width: '450px',
                        disableClose: true
                    });
                }
            } else {
                this.approveChangeStatusModal = true;
                this.assignInwardID = id;
                this.assignInwardProductId = productId;
                this.dialog.open(this.approveChangeStatusModalDialog, {
                    width: '450px',
                    disableClose: true
                });
            }
        } else {
            this.approveChangeStatusModal = true;
            this.assignInwardID = id;
            this.assignInwardProductId = productId;
            this.dialog.open(this.approveChangeStatusModalDialog, {
                width: '450px',
                disableClose: true
            });
        }
    }
    rejectChangeStatus(id, productId) {
        this.rejectChangeStatusModal = true;
        this.assignInwardID = id;
        this.assignInwardProductId = productId;
        this.dialog.open(this.rejectChangeStatusModalDialog, {
            width: '450px',
            disableClose: true
        });
    }

    performAction(item: any) {
        const index = this.parameterList.indexOf(item);
        if (index !== -1) {
            this.parameterList.splice(index, 1);
            this.showTable = this.parameterList.length > 0;
        }
    }

    open() {
        this.specificationValue = "";
        this.mandatory = false;
        $("#approveOpenStatusModal").modal("show");
    }

    saveParameter() {
        this.showTable = true;
        if (!this.specificationValue) {
            return;
        }
        this.parameterList.push({
            specificationValue: this.specificationValue,
            mandatory: this.mandatory
        });
        this.specificationValue = "";
        this.mandatory = false;
        this.showTable = this.parameterList.length > 0;
    }

    saveParametersAndClose() {
        this.saveParameter();
        $("#approveOpenStatusModal").modal("hide");
    }

    cancelAndClose() {
        $("#approveOpenStatusModal").modal("hide");
    }

    onAddAttributee() {
        this.macSubmitted = true;
        const macAddress = this.macForm.get("macAddress").value;
        const serialNumber = this.macForm.get("serialNumber").value;
        if (macAddress && serialNumber) {
            const newItem = {
                itemId: this.uniqueIdCounter++,
                macAddress: macAddress,
                serialNumber: serialNumber
            };
            this.inwardMacList.push(newItem);
            this.macForm.reset();
            this.macSubmitted = false;
            this.addMacTableTable.renderRows()
        } else if (serialNumber) {
            const newItem = {
                itemId: this.uniqueIdCounter++,
                macAddress: macAddress,
                serialNumber: serialNumber
            };
            this.inwardMacList.push(newItem);
            this.macForm.reset();
            this.macSubmitted = false;
            this.addMacTableTable.renderRows()
        }
    }

    editMACMapping(product: any) {
        this.selectedProductForEdit = { ...product };
        this.isEditMode = true;
    }

    deleteMACMapping(itemId: any) {
        const index = this.inwardMacList.findIndex(item => item.itemId === itemId);
        if (index !== -1) {
            this.inwardMacList.splice(index, 1);
            this.addMacTableTable.renderRows()
        }
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

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteMACMap(product);
                } else {
                    this.toastr.info('Delete operation was cancelled', 'Info!');
                }
            });
        }
    }

    deleteMACMap(inward) {
        this.inwardService.deleteMapMac(inward.id).subscribe(
            (response: any) => {
                if (response.responseCode == 406 || response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`Successfully Deleted`, 'Success!');
                }
                this.inwardMactable.renderRows()
                this.showItem(
                    this.inwardDeleteData.id,
                    this.inwardDeleteData.productId.id,
                    this.inwardDeleteData.destinationId,
                    this.inwardDeleteData.destinationType
                );
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    saveEdit() {
        this.isEditMode = false;
        this.selectedProductForEdit = null;
    }

    approveInward(dialogRef): void {
        this.assignInwardSubmitted = true;
        this.approveInwardData = [];
        if (this.assignInwardForm.valid) {
            let url = `/inwards/inwardGroupApproval`;
            let approvalInwardData = {
                id: this.assignInwardID,
                productId: this.assignInwardProductId.id,
                approvalStatus: "Approve",
                approvalRemark: this.assignInwardForm.controls.remark.value
            };
            this.inwardService.updateMethod(url, approvalInwardData).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.closeApproveInventoryModal();
                        this.approveInwardData = response.data;
                        dialogRef.close()
                        this.toastr.success(`Approved Successfully`, 'Success!');
                        this.getInwardList("");
                    } else {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');
                }
            );
        }
    }

    rejectInward(dialogRef): void {
        this.rejectInwardSubmitted = true;
        this.approveInwardData = [];
        if (this.rejectInwardForm.valid) {
            let url = `/inwards/inwardGroupApproval`;
            let approvalInwardData = {
                id: this.assignInwardID,
                productId: this.assignInwardProductId.id,
                approvalStatus: "Rejected",
                approvalRemark: this.rejectInwardForm.controls.remark.value
            };

            this.inwardService.updateMethod(url, approvalInwardData).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.closeRejectInventoryModal();
                        this.approveInwardData = response.data;
                        this.getInwardList("");
                        dialogRef.close()
                    } else {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');
                }
            );
        }
    }

    getInwardData(inwardId: any) {
        this.inwardDetails = [];
        this.ifInwardDetails = true;
        this.IfPersonalInwardDataShow = true;
        if (inwardId) {
            const url = "/inwards/" + inwardId;
            this.inwardService.getMethod(url).subscribe(
                (response: any) => {
                    this.inwardDetails = response.data;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    inwardDeatilsClear() {
        this.IfPersonalInwardDataShow = false;
    }
    personalInwardData() {
        this.IfPersonalInwardDataShow = true;
    }

    onclosed() {
        this.fileterGlobal1 = "";
        this.searchOption = "";
        this.searchMacDeatil = "";
        this.getInwardList("");
        this.MACShowModal = false;
        this.currentPageInwardMapMapping = 1;
        this.inwardMappingListitemsPerPage = 20;
    }

    closeApproveInventoryModal() {
        this.assignInwardSubmitted = false;
        this.assignInwardForm.reset();
        this.approveChangeStatusModal = false;
    }

    closeRejectInventoryModal() {
        this.rejectInwardSubmitted = false;
        this.rejectInwardForm.reset();
        this.rejectChangeStatusModal = false;
    }

    canExit() {
        if (!this.inwardFormGroup.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
                const dialogRef = this.dialog.open(this.confirmDialog, {
                    width: '400px',
                    data: {
                        title: 'Alert',
                        description: `The filled data will be lost. Do you want to continue? (Yes/No)`,
                        yesLabel: 'Yes',
                        noLabel: 'No'
                    }
                });

                dialogRef.afterClosed().subscribe((result) => {
                    if (result === true) {
                        observer.next(true);
                        observer.complete();
                    }
                    else {
                        observer.next(false);
                        observer.complete();
                    }
                });
                return false;
            });
        }
    }
    getInwardDetails(id) {
        const url = "/inwards/" + id;
        this.inwardService.getMethod(url).subscribe(
            (res: any) => {
                this.viewInwardsDetails = res.data;
                this.dialog.open(this.detailsDialog, {
                    width: '80%',
                    disableClose: true
                });
                this.showSpecification = true;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    inwardList() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
    }


    showItem(inwardId, productId, sourceId, sourceType) {
        let currentPage;
        currentPage = this.currentPageInwardMapMapping;
        let body = {
            page: currentPage,
            pageSize: this.inwardMappingListitemsPerPage
        };
        this.inwardId = inwardId;

        //this.macDetailsArray = this.fb.array([]);
        this.inwardService.postItems(inwardId, productId, sourceId, sourceType, body).subscribe(
            (res: any) => {
                this.inwardMacList = res.dataList;
                this.inwardMappingListdatatotalRecords = res.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    quantityInValidation(event) {
        var num = String.fromCharCode(event.which);
        if (!/[0-9]/.test(num)) {
            event.preventDefault();
        }
    }
    clearFilterGlobal1(table: Table) {
        this.fileterGlobal1 = "";
        table.clear();
    }
    InventoryDetails(itemId) {
        this.inwardService.getByItemId(itemId).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.specDetailsShow = true;
                    this.inventorySpecificationDetails = response.dataList;
                    this.inventoryDetailModal = true;

                    this.dialog.open(this.inventoryDetailModalDialog, {
                        width: '50%',
                        disableClose: true
                    });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    closeInventoryDetailModal() {
        this.inventoryDetailModal = false;
        this.specDetailsShow = false;
    }
    getProductById(productId: number) {
        return this.inwardListData.find(
            element => element.id == productId
        ).productId;
    }

    openBulkProductDialog(inward: any) {
        this.isBulkLoading = true;
        this.inwardService.getInwardsByGroupId(inward.id).subscribe(
            (res: any) => {
                this.bulkInwardList = res.data || [];
                this.dataSource = this.bulkInwardList;
                console.log("bulkInwardList", this.bulkInwardList);

                this.isBulkLoading = false;
                this.dialog.open(this.bulkProductDetailsDialog, {
                    width: '60%',
                    disableClose: true
                });
            },
            (error: any) => {
                this.isBulkLoading = false;
                this.toastr.error('Failed to fetch bulk inwards', 'Failed!');
            }
        );
    }

    bulkApproveInward(dialogRef: any) {
        this.assignInwardSubmitted = true;
        if (this.assignInwardForm.valid) {
            const approvalData = this.bulkInwardList.map(item => ({
                id: item.id,
                productId: item.productId.id,
                approvalStatus: 'Approve',
                approvalRemark: this.assignInwardForm.controls.remark.value
            }));

            this.inwardService.updateMethod('/inwards/bulkInwardApproval', approvalData).subscribe(
                (res: any) => {
                    if (res.responseCode === 200) {
                        this.toastr.success('Bulk Approval Successful', 'Success!');
                        dialogRef.close();
                        this.getInwardList("");
                    } else {
                        this.toastr.info(res.responseMessage, 'Info!');
                    }
                },
                (error: any) => {
                    this.toastr.error('Bulk Approval Failed', 'Failed!');
                }
            );
        }
    }

    bulkRejectInward(dialogRef: any) {
        this.rejectInwardSubmitted = true;
        if (this.rejectInwardForm.valid) {
            const rejectionData = this.bulkInwardList.map(item => ({
                id: item.id,
                productId: item.productId.id,
                approvalStatus: 'Rejected',
                approvalRemark: this.rejectInwardForm.controls.remark.value
            }));

            this.inwardService.updateMethod('/inwards/bulkInwardApproval', rejectionData).subscribe(
                (res: any) => {
                    if (res.responseCode === 200) {
                        this.toastr.success('Bulk Rejection Successful', 'Success!');
                        dialogRef.close();
                        this.getInwardList("");
                    } else {
                        this.toastr.info(res.responseMessage, 'Info!');
                    }
                },
                (error: any) => {
                    this.toastr.error('Bulk Rejection Failed', 'Failed!');
                }
            );
        }
    }

    uploadDocument(inward) {
        const product = this.getProductById(inward?.id)
        const deviceType = product.productCategory.deviceType || 'DEFAULT';;
        this.inwardId = inward.id;
        this.hasMac = product?.productCategory?.hasMac;
        this.hasSerial = product?.productCategory?.hasSerial;

        if ((deviceType === "ONU" || !deviceType) && !this.hasMac && !this.hasSerial) {
            this.toastr.info('Product type does not allow to add Mac/Serial Number..', 'Info!');
            this.addMACaddress = false;
            return;
        } else {
            this.uploadInwardId = inward.id;
            this.uploadDocForm.patchValue({
                file: ""
            });
            this.selectedFileUploadPreview = [];
            this.isBuldUpload = true;
            this.resetUploadState();

            // Dynamic Column Selection (Scalable)
            this.displayedMacShowColumns2 =
                this.COLUMN_CONFIG[deviceType] ||
                this.COLUMN_CONFIG['DEFAULT'];

            this.displayedMacShowColumnsFields =
                this.displayedMacShowColumns2.map(col => col.field);
            this.dialog.open(this.isBuldUploadDialog, {
                width: '900px',
                disableClose: true
            });
        }
    }
    closed(dialogRef) {
        dialogRef.close();
        this.getData.data = [];
        this.skippedTotalRecords = 0;
    }
    private resetUploadState(): void {
        this.uploadDocForm.patchValue({ file: "" });
        this.selectedFileUploadPreview = [];
        this.showSkippedTable = false;
        this.skippedRecordsData = null;
        this.isBuldUpload = true;
    }

    onFileChangeUpload(event: any) {
        this.selectedFileUploadPreview = [];
        if (event.target.files.length > 0) {
            this.selectedFile = event.target.files[0];
            const files: FileList = event.target.files;
            for (let i = 0; i < files.length; i++) {
                this.selectedFileUploadPreview.push(files.item(i));
            }
            if (!this.isValidCSVFile(this.selectedFile)) {
                this.uploadDocForm.controls.file.reset();
                this.selectedFileUploadPreview = [];
                this.toastr.error("Please upload valid .csv file or .xlsx or .xls file", 'Failed!');
            } else {
                const file = event.target.files;
                this.uploadDocForm.patchValue({
                    file: file
                });
            }
        }
    }





    isValidCSVFile(file: File): boolean {
        if (!file?.name) return false;
        const allowedExtensions = ['.csv', '.xlsx', '.xls'];
        const fileName = file.name.toLowerCase();
        return allowedExtensions.some(ext => fileName.endsWith(ext));
    }

    deletUploadedFile(event: any) {
        var temp: File[] = this.selectedFileUploadPreview?.filter((item: File) => item?.name != event);
        this.selectedFileUploadPreview = temp;
        this.uploadDocForm.patchValue({
            file: temp
        });
    }

    uploadDocuments(dialogRef) {
        this.submitted = true;
        this.showSkippedTable = false;
        this.skippedRecordsData = null;
        if (this.uploadDocForm.valid) {
            const formData = new FormData();
            let fileArray: FileList;
            if (this.uploadDocForm.controls.file) {
                if (!this.isValidCSVFile(this.selectedFile)) {
                    this.uploadDocForm.controls.file.reset();
                    this.toastr.error("Please upload valid .csv file or .xlsx or .xls file", 'Failed!');
                    this.showSkippedTable = true;
                    this.skippedRecordsData = [];
                    return;
                } else {
                    fileArray = this.uploadDocForm.controls.file.value;
                    Array.from(fileArray).forEach(file => {
                        formData.append("file", file);
                    });
                }
            }

            const url = `/inwards/saveManualMacSerial/upload/${this.uploadInwardId}`;
            this.inwardService.postMethod(url, formData).subscribe(
                (response: any) => {
                    if (response.responseCode === 406) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');
                        this.skippedRecordsData = [];
                    } else if (response.responseCode === 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');
                        this.skippedRecordsData = [];
                    } else if (response.responseCode === 202) {

                        this.showSkippedTable = true;
                        this.skippedRecordsData = [];
                    }
                    else {
                        dialogRef.close()
                        this.submitted = false;
                        this.toastr.success(`Document Uploaded Successfully`, 'Success!');
                        this.isBuldUpload = false;
                        this.showSkippedTable = true;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.responseMessage}`, 'Failed!');
                    this.showSkippedTable = true;
                    this.skippedRecordsData = [];
                }
            );
        }
    }

    closeUploadDocumentId() {
        this.isBuldUpload = false;
        this.uploadDocForm.patchValue({
            file: ""
        });
        this.selectedFileUploadPreview = [];
    }

    paginate(event) {
        this.inwardMappingListitemsPerPage = event.pageSize;
        this.currentPageInwardMapMapping = event.pageIndex + 1;
        this.searchMacDeatil
            ? this.searchMac()
            : this.showItem(
                this.inwardDeleteData.id,
                this.inwardDeleteData.productId.id,
                this.inwardDeleteData.destinationId,
                this.inwardDeleteData.destinationType
            );
    }

    searchMac() {
        this.searchMacData.filters[0].filterValue = this.searchMacDeatil;
        this.searchMacData.filters[0].filterColumn = this.searchOption;
        this.searchMacData.productId = this.inwardDeleteData.productId.id;
        this.searchMacData.ownerId = this.inwardDeleteData.destinationId;
        this.searchMacData.ownerType = this.inwardDeleteData.destinationType;
        this.searchMacData.entityId = this.inwardId;
        this.searchMacData.page = this.currentPageInwardMapMapping;
        this.searchMacData.pageSize = this.inwardMappingListitemsPerPage;
        const url = "/inwards/searchInwardOutwardItem";
        this.inwardService.postMethod(url, this.searchMacData).subscribe(
            (response: any) => {
                this.inwardMacList = response.dataList;
                this.inwardMappingListdatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    clearMac() {
        this.searchOption = "";
        this.searchMacDeatil = "";
        this.currentPageInwardMapMapping = 1;
        this.inwardMappingListitemsPerPage = 20;
        this.showItem(
            this.inwardDeleteData.id,
            this.inwardDeleteData.productId.id,
            this.inwardDeleteData.destinationId,
            this.inwardDeleteData.destinationType
        );
    }

    selMacSearchOption(event) {
        this.searchMacDeatil = "";
        this.optionValue = event;
    }


    closeProductDialog() {
        this.dialog.closeAll();
    }

}
