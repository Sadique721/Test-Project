import { element } from "protractor";
import { type } from "os";
import { url } from "inspector";
import { DatePipe } from "@angular/common";
import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { ITEMS_PER_PAGE, pageLimitOptions } from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { LoginService } from "src/app/service/login.service";
import { PartnerService } from "src/app/service/partner.service";
import { formatDate } from "@angular/common";
import { ExternalItemManagementService } from "src/app/service/external-item-management.service";
import { PopManagementsService } from "src/app/service/pop-managements.service";
import { Regex } from "src/app/constants/regex";
import { Observable, Observer } from "rxjs";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { LiveUserService } from "src/app/service/live-user.service";
import { INVENTORYS } from "src/app/constants/aclConstants";
import { ServiceAreaService } from "src/app/service/service-area.service";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";
declare var $: any;
@Component({
    selector: "app-external-item-management",
    templateUrl: "./external-item-management.component.html",
    styleUrls: ["./external-item-management.component.css"],
    standalone: false
})
export class ExternalItemManagementComponent implements OnInit {
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    displayedColumns: string[] = [
        'externalItemNumber',
        'productName',
        'type',
        'qty',
        'availableQty',
        'inTransitQty',
        'status',
        'approvalStatus',
        'action'
    ];
    // Extracted values for template
    pageSizeOptionsValues: number[] = [];
    @ViewChild('externalItemDialog') externalItemDialog!: TemplateRef<any>;
    @ViewChild('partnerOwnerDialog') partnerOwnerDialog!: TemplateRef<any>;
    @ViewChild('customerOwnerDialog') customerOwnerDialog!: TemplateRef<any>;
    @ViewChild('macDialog') macDialog!: TemplateRef<any>;
    macDialogRef!: MatDialogRef<any>;
    partnerOwnerDialogRef!: MatDialogRef<any>;
    customerOwnerDialogRef!: MatDialogRef<any>;
    @ViewChild('externalItemDetailDialog') externalItemDetailDialog: TemplateRef<any>;
    externalItemDetailDialogRef!: MatDialogRef<any>;

    @ViewChild('addmacDialog') addMacDialog!: TemplateRef<any>;
    addMacDialogRef!: MatDialogRef<any>;
    // Data & state variables
    //   searchParentCustOption: string = '';
    //   searchParentCustValue: string = '';
    //   parentFieldEnable: boolean = false;

    dialogRef!: MatDialogRef<any>;
    externalItemManagementFormGroup: UntypedFormGroup;
    // countryFormArray: FormArray;
    submitted = false;
    showSearchBar: boolean = false;
    loggedInStaffId = localStorage.getItem("userId");
    stateData: any = {};
    countryListData: any;
    currentPageProductListdata = 1;
    productListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    productListdatatotalRecords: any;
    countryPojo: any = {};
    externalItemListData: any[] = [];
    externalItemDetails: any = [];
    ifExternalItemDetails = false;
    currentPagepartnerListdata = 1;
    partnerListdataitemsPerPage = ITEMS_PER_PAGE;
    partnerListdatatotalRecords: any;
    customerListdatatotalRecords: any;
    selectServiceAreaFlag: boolean = false;
    selectOwnerTypeFlag: boolean = false;
    getOwnerFlag: boolean = false;
    ownerShow: boolean = false;
    totalPartnerDataListLength = 0;
    partnerListData: any = [];
    currentPagecustomerListdata = 1;
    customerListdataitemsPerPage = ITEMS_PER_PAGE;
    cusotmerListdatatotalRecords: any;
    newFirst = 0;
    totalCustomerDataListLength = 0;
    customerListData: any = [];
    IfPersonalExternalItemDataShow = true;
    viewCountryListData: any;
    viewStateListData: any;
    isStateEdit = false;
    searchData: any;
    searchExternalItem: any = "";
    // searchKey: string;
    AclClassConstants: any;
    AclConstants: any;
    assignExternalItemSubmitted: boolean = false;
    partnerOwnerModelFlag: boolean = false;
    customerOwnerModelFlag: boolean = false;
    MACShowModal: boolean = false;
    MACAssignModal: boolean = false;
    rejectChangeStatusModal: boolean = false;
    approveChangeStatusModal: boolean = false;
    pageLimitOptions = pageLimitOptions;
    showItemPerPage = 5;
    searchkey: string;

    public loginService: LoginService;
    editMode: boolean;

    selectWareHouseView: boolean;
    pincodeDeatils: any;

    status = [
        { label: "Active", value: "ACTIVE" },
        { label: "Inactive", value: "INACTIVE" }
    ];
    createView = false;
    listView = true;

    @ViewChild("closebutton") closebutton;
    countryList = [];
    stateList = [];
    cityList = [];
    pincodeList = [];
    selectOwner = [];
    allpincodeNumber: any = [];
    unit = "";
    products: any[] = [];
    warehouses: any[] = [];
    ownershipType = [
        { label: "Partner Owned", value: "Partner Owned" },
        { label: "Customer Owned", value: "Customer Owned" }
    ];

    pipe = new DatePipe("en-US");
    usedQty: number;
    inTransitQty: number;
    totalMacSerial: number;
    showQtyError: boolean;
    showIntransitQtyError: boolean;
    addMACaddress: boolean;
    externalItemIdForMac: number;
    externalItemMacList: any[] = [];
    serviceAreaList: any = [];
    macAdderessInput = "";
    macForm: UntypedFormGroup;
    assignExternalItemForm: UntypedFormGroup;
    hasMac: boolean;
    hasSerial: boolean;
    detailView: boolean = false;
    viewExternalItemData: any;
    ownerData: any;
    searchkey2: string;
    searchOptionSelect = this.commondropdownService.customerInventorySearchOption;
    parentFieldEnable: boolean = false;
    partnerFieldEnable: boolean = false;
    searchParentCustValue = "";
    searchPartnerValue = "";
    searchParentCustOption = "";
    searchPartnerOption = "";
    prepaidParentCustomerList: any;
    custid: any;
    custId: any;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    createAccess: boolean = false;
    showMacAddressAccess: boolean = false;
    addMacAddressAccess: boolean = false;
    constructor(
        private fb: UntypedFormBuilder,
        private customerManagementService: CustomermanagementService,
        private liveUserService: LiveUserService,
        private datePipe: DatePipe,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private toastr: ToastrService,
        private partnerService: PartnerService,
        private externalItemManagementService: ExternalItemManagementService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private popService: PopManagementsService,
        loginService: LoginService,
        private serviceAreaService: ServiceAreaService,
        public commondropdownService: CommondropdownService,
        private dialog: MatDialog
    ) {
        this.loginService = loginService;
        this.createAccess = loginService.hasPermission(INVENTORYS.EXT_ITEM_CREATE);
        this.deleteAccess = loginService.hasPermission(INVENTORYS.EXT_ITEM_DELETE);
        this.editAccess = loginService.hasPermission(INVENTORYS.EXT_ITEM_EDIT);
        this.showMacAddressAccess = loginService.hasPermission(INVENTORYS.EXT_ITEM_SHOW_MAC_ADDRESS);
        this.addMacAddressAccess = loginService.hasPermission(INVENTORYS.EXT_ITEM_ADD_MAC_ADDRESS);
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        // this.editMode = !createAccess && editAccess ? true : false;

        // this.externalItemManagementService.getAllProducts().subscribe((res: any) => {
        //   this.products = res.dataList;
        // });
        // this.getServiceArea();
    }

    ngOnInit(): void {
        this.pageSizeOptionsValues = this.pageLimitOptions.map(o => o.value);
        this.externalItemManagementFormGroup = this.fb.group({
            id: [""],
            productId: ["", Validators.required],
            qty: [""],
            // destinationId: ["", Validators.required],
            // destinationType: [""],
            status: ["", Validators.required],
            ownershipType: ["", Validators.required],
            ownerId: ["", Validators.required],
            externalItemGroupNumber: [""],
            mvnoId: [""],
            unusedQty: [""],
            usedQty: [""],
            inTransitQty: [
                "",
                [Validators.required, Validators.pattern(Regex.numeric), Validators.min(1)]
            ],
            rejectedQty: [""],
            serviceAreaId: ["", Validators.required],
            totalMacSerial: [""]
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
        this.assignExternalItemForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.getExternalItemList("");
        this.externalItemManagementFormGroup.get("qty").valueChanges.subscribe(val => {
            const total = val - this.usedQty;
            if (total < 0) {
                this.showQtyError = true;
            } else {
                this.showQtyError = false;
            }
        });
        this.externalItemManagementFormGroup.get("inTransitQty").valueChanges.subscribe(val => {
            const total = val - this.totalMacSerial;
            if (total < 0) {
                this.showIntransitQtyError = true;
            } else {
                this.showIntransitQtyError = false;
            }
        });
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INVENTORYS.EXT_ITEM_SHOW_MAC_ADDRESS) || this.loginService.hasPermission(INVENTORYS.EXT_ITEM_ADD_MAC_ADDRESS) || this.loginService.hasPermission(INVENTORYS.EXT_ITEM_DELETE) || this.loginService.hasPermission(INVENTORYS.EXT_ITEM_EDIT)) {
            return [
                'externalItemNumber',
                'productName',
                'type',
                'qty',
                'availableQty',
                'inTransitQty',
                'status',
                'approvalStatus',
                'action'
            ];
        } else {
            return [
                'externalItemNumber',
                'productName',
                'type',
                'qty',
                'availableQty',
                'inTransitQty',
                'status',
                'approvalStatus'
            ];
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageProductListdata > 1) {
            this.currentPageProductListdata = 1;
        }
        if (!this.searchkey) {
            this.getExternalItemList(this.showItemPerPage);
        } else {
            this.searchExternalItemData();
        }
    }

    getExternalItemList(list) {
        this.externalItemListData = [];
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
        this.externalItemManagementService.getAll(plandata).subscribe(
            (response: any) => {
                this.externalItemListData = response.dataList;
                this.dataSource = new MatTableDataSource<any>(this.externalItemListData);
                this.productListdatatotalRecords = response.totalRecords;

                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }

    selParentSearchOption(event) {
        if (event.value) {
            this.parentFieldEnable = true;
        } else {
            this.parentFieldEnable = false;
        }
    }

    searchParentCustomer() {
        const searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.currentPagepartnerListdata,
            pageSize: this.customerListdataitemsPerPage,
            sortBy: "id",
            sortOrder: 0
        };
        searchData.filters[0].filterColumn = this.searchParentCustOption;
        searchData.filters[0].filterValue = this.searchParentCustValue.trim();
        const serviceAreaId = this.externalItemManagementFormGroup.get("serviceAreaId").value;
        const url =
            "/externalitemmanagement/searchCustomerListServiceArea?serviceAreaId=" + serviceAreaId;
        this.externalItemManagementService.postMethod(url, searchData).subscribe(
            (response: any) => {
                this.customerListData = response.dataList;
                this.customerListdatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.productListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`);

                } else {
                    this.toastr.error(`${error.error.error}`, 'Failed!');
                }
            }
        );
    }

    clearSearchParentCustomer() {
        this.getCustomerList("");
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
    }

    searchPartner() {
        const searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "name",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.currentPagepartnerListdata,
            pageSize: this.customerListdataitemsPerPage,
            sortBy: "id",
            sortOrder: 0
        };
        searchData.filters[0].filterColumn = this.searchPartnerOption;
        searchData.filters[0].filterValue = this.searchPartnerValue.trim();
        const serviceAreaId = this.externalItemManagementFormGroup.get("serviceAreaId").value;
        const url =
            "/externalitemmanagement/searchPartnerListServiceArea?serviceAreaId=" + serviceAreaId;
        this.externalItemManagementService.postMethod(url, searchData).subscribe(
            (response: any) => {
                if (response != null) {
                    this.partnerListData = response.dataList;
                    this.partnerListdatatotalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                this.productListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`);

                } else {
                    this.toastr.error(`${error.error.error}`, 'Failed!');
                }
            }
        );
    }

    clearSearchPartner() {
        // this.currentPageParentCustomerListdata = 1;
        this.getpartnerList("");
        this.searchPartnerValue = "";
        this.searchPartnerOption = "";
        // this.partnerFieldEnable = false;
    }

    submit() {
        this.submitted = true;
        if (
            this.externalItemManagementFormGroup.valid &&
            !this.showQtyError &&
            !this.showIntransitQtyError
        ) {
            if (this.editMode) {
                this.externalItemManagementService.update(this.mapObject()).subscribe(
                    (res: any) => {
                        if (res.responseCode == 406) {
                            this.toastr.info(`${res.responseMessage}`);

                        } else {
                            // this.messageService.add({
                            //     severity: "success",
                            //     summary: "Successfully",
                            //     detail: res.responseMessage,
                            //     icon: "far fa-check-circle"
                            // });
                            this.toastr.success(`${res.responseMessage}`, 'Success!');
                            this.clearSearchExternalItem();
                            this.editMode = false;
                            this.submitted = false;
                            this.dialogRef.close();
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.error}`, 'Failed!');
                    }
                );
            } else {
                this.externalItemManagementService.save(this.mapObject()).subscribe(
                    (res: any) => {
                        if (res.responseCode == 406) {
                            this.toastr.info(`${res.responseMessage}`);

                        } else {
                            // this.messageService.add({
                            //     severity: "success",
                            //     summary: "Successfully",
                            //     detail: res.responseMessage,
                            //     icon: "far fa-check-circle"
                            // });
                            this.toastr.success(`${res.responseMessage}`, 'Success!');

                            this.submitted = false;
                            this.clearSearchExternalItem();
                            this.dialogRef.close();
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.error}`, 'Failed!');
                    }
                );
            }
        }
    }

    mapObject() {
        const externalItemValues = this.externalItemManagementFormGroup.getRawValue();
        const externalitem = {
            id: "",
            productId: "",
            qty: 0,
            // destinationId: "",
            // destinationType: "Warehouse",
            ownershipType: "",
            ownerId: null,
            status: "",
            externalItemGroupNumber: "",
            inTransitQty: "",
            mvnoId: "",
            usedQty: "",
            unusedQty: "",
            rejectedQty: "",
            serviceAreaId: { id: "" },
            totalMacSerial: 0
        };
        externalitem.id = externalItemValues.id ? externalItemValues.id : null;
        externalitem.productId = externalItemValues.productId;
        externalitem.qty = externalItemValues.qty;
        externalitem.status = externalItemValues.status;
        externalitem.serviceAreaId.id = externalItemValues.serviceAreaId;
        externalitem.ownershipType = externalItemValues.ownershipType;
        externalitem.externalItemGroupNumber = externalItemValues.externalItemGroupNumber
            ? externalItemValues.externalItemGroupNumber
            : "";
        externalitem.mvnoId = null;
        externalitem.usedQty = externalItemValues.usedQty;
        externalitem.unusedQty = externalItemValues.unusedQty;
        externalitem.ownerId = externalItemValues.ownerId;
        externalitem.inTransitQty = externalItemValues.inTransitQty;
        externalitem.rejectedQty = externalItemValues.rejectedQty;
        externalitem.totalMacSerial = externalItemValues.totalMacSerial;
        // const hh =myDate.toLocaleTimeString().replace(/([\d]+:[\d]{2})(:[\d]{2})(.*)/, "$1$3");

        // const formattedDate = formatDate(myDate, format, locale) +' '+ hh;
        // console.log("date time",formattedDate )
        return externalitem;
    }

    editExternalItem(id) {
        this.editMode = true;
        this.createView = true;
        this.listView = false;
        this.detailView = false;
        // ----
        this.dialogRef = this.dialog.open(this.externalItemDialog, {
            width: '1000px'
        });
        // ----

        this.externalItemManagementService.getAllProducts().subscribe((res: any) => {
            this.products = res.dataList;
        });
        this.getServiceArea();
        const externalItemEdit = this.externalItemListData.find(element => element.id == id);
        if (this.editMode) {
            this.externalItemManagementFormGroup.get('productId').disable();
            this.externalItemManagementFormGroup.get('serviceAreaId').disable();
            this.externalItemManagementFormGroup.get('ownershipType').disable();
            this.externalItemManagementFormGroup.get('ownerId').disable();
        } else {
            this.externalItemManagementFormGroup.get('productId').enable()
            this.externalItemManagementFormGroup.get('serviceAreaId').enable();
            this.externalItemManagementFormGroup.get('ownershipType').enable();
            this.externalItemManagementFormGroup.get('ownerId').enable();
        }

        this.externalItemManagementFormGroup.patchValue({
            id: externalItemEdit.id,
            productId: externalItemEdit.productId.id,
            qty: externalItemEdit.qty,
            status: externalItemEdit.status,
            ownershipType: externalItemEdit.ownershipType,
            externalItemGroupNumber: externalItemEdit.externalItemGroupNumber,
            mvnoId: [""],
            usedQty: externalItemEdit.usedQty,
            unusedQty: externalItemEdit.unusedQty,
            inTransitQty: externalItemEdit.inTransitQty,
            ownerId: externalItemEdit.ownerId,
            serviceAreaId: externalItemEdit.serviceAreaId.id,
            totalMacSerial: externalItemEdit.totalMacSerial
        });
        this.ownerShow = true;
        this.usedQty = externalItemEdit.usedQty;
        this.inTransitQty = externalItemEdit.inTransitQty;
        this.totalMacSerial = externalItemEdit.totalMacSerial;
        const url = "/externalitemmanagement/" + id;
        this.externalItemManagementService.getMethod(url).subscribe((res: any) => {
            this.ownerData = res.data;
            const serviceAreaId = this.externalItemManagementFormGroup.get("serviceAreaId").value;
            if (serviceAreaId == undefined) {
                this.externalItemManagementFormGroup.patchValue({
                    serviceAreaId: this.ownerData.serviceAreaId.id
                });
            }
            this.ownerSelectList = [];
            this.ownerSelectList.push({
                id: Number(this.ownerData.ownerId),
                name: this.ownerData.ownerName
            });
        });
    }

    searchExternalItemData() {
        if (!this.searchkey || this.searchkey !== this.searchExternalItem) {
            this.currentPageProductListdata = 1;
        }
        this.searchkey = this.searchExternalItem;
        if (this.showItemPerPage) {
            this.productListdataitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchExternalItem.trim();
        const page = {
            page: this.currentPageProductListdata,
            pageSize: this.showItemPerPage
        };
        // const url = '/state/search'
        this.externalItemManagementService.search(page, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`);

                    this.externalItemListData = [];
                    this.productListdatatotalRecords = 0;
                } else {
                    this.externalItemListData = response.dataList;
                    this.productListdatatotalRecords = response.totalRecords;
                    this.dataSource = new MatTableDataSource<any>(this.externalItemListData);
                    if (this.paginator) {
                        this.dataSource.paginator = this.paginator;
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }
    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    clearSearchExternalItem() {
        // this.dialogRef.close();
        this.showSearchBar = true;
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.editMode = false;
        this.submitted = false;
        this.searchExternalItem = "";
        this.searchkey = "";
        this.getOwnerFlag = false;
        this.ownerShow = false;
        this.getExternalItemList("");
        this.externalItemManagementFormGroup.reset();
        // this.getServiceArea();
    }

    //   deleteConfirmExternalItem(productId: number) {
    //     if (productId) {
    //       this.confirmationService.confirm({
    //         message: "Do you want to delete this externalItem ?",
    //         header: "Delete Confirmation",
    //         icon: "pi pi-info-circle",
    //         accept: () => {
    //           this.deleteProduct(productId);
    //         },
    //         reject: () => {
    //           this.messageService.add({
    //             severity: "info",
    //             summary: "Rejected",
    //             detail: "You have rejected"
    //           });
    //         }
    //       });
    //     }
    //   }
    deleteConfirmExternalItem(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Confirmation ',
                description: `Are you sure you want to delete "${item.externalItemGroupNumber}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteProduct(item.id);
            } else {
            }
        });
    }

    deleteProduct(productId) {
        //const productEditData = this.externalItemListData.find(element => element.id == productId);
        this.externalItemManagementService.delete(productId).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`);

                } else {
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: response.message,
                    //     icon: "far fa-check-circle"
                    // });
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                }
                this.getExternalItemList("");
            },
            (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }

    addEditExternalItemDialog() {
        this.showSearchBar = false;
        this.getOwnerFlag = false;
        this.ownerShow = false;
        this.listView = false;
        this.detailView = false;
        this.createView = true;
        this.editMode = false;
        this.externalItemManagementFormGroup.get('productId').enable()
        this.externalItemManagementFormGroup.get('serviceAreaId').enable();
        this.externalItemManagementFormGroup.get('ownershipType').enable();
        this.externalItemManagementFormGroup.get('ownerId').enable();
        this.externalItemManagementFormGroup.reset();
        this.dialogRef = this.dialog.open(this.externalItemDialog, {
            width: '1000px'
        });
        this.externalItemManagementService.getAllProducts().subscribe((res: any) => {
            this.products = res.dataList;
        });
        this.getServiceArea();
    }
    getUnit(event) {
        this.unit = this.products.find(element => element.id == event.value).unit;
        this.selectServiceAreaFlag = true;
        this.selectOwnerTypeFlag = false;
    }
    getOwnerType() {
        this.selectOwnerTypeFlag = true;
        this.selectedPartner = [];
        this.selectedCustomer = [];
        this.customerListData = [];
        this.partnerListData = [];
        this.externalItemManagementFormGroup.get("ownershipType").reset();
        this.getOwnerFlag = false;
        this.ownerShow = false;
        this.externalItemManagementFormGroup.get("onwerId").reset();
    }

    pageChangedProductList(event: PageEvent) {

        this.currentPageProductListdata = event.pageIndex + 1;
        this.productListdataitemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.getExternalItemList("");
        } else {
            this.searchExternalItemData();
        }
    }
    macDisplayedColumns: string[] = [];
    showMac(externalItemId) {
        this.hasMac = this.externalItemListData.find(
            element => element.id == externalItemId
        ).productId.productCategory.hasMac;
        this.hasSerial = this.externalItemListData.find(
            element => element.id == externalItemId
        ).productId.productCategory.hasSerial;

        if (!this.hasMac && !this.hasSerial) {
            // this.messageService.add({
            //     severity: "info",
            //     summary: "info",
            //     detail: "Product type does not allow to add Mac/Serial Number..",
            //     icon: "far fa-times-circle"
            // });
            this.toastr.info('Product type does not allow to add Mac/Serial Number.');
            this.addMACaddress = false;
            return;
        } else {
            this.MACShowModal = true;
            this.macDialogRef = this.dialog.open(this.macDialog, {
                width: '55%',
                disableClose: true
            });

            this.externalItemIdForMac = externalItemId;
            this.macForm = this.fb.group({
                id: [""],
                externalItemId: [this.externalItemIdForMac],
                status: ["ACTIVE"],
                macAddress: this.hasMac ? ["", Validators.required] : [null],
                serialNumber: this.hasSerial ? ["", Validators.required] : [null]
            });
            this.addMACaddress = true;
            this.getExternalItemMACMapping();
            this.macDisplayedColumns = [];
            if (this.hasMac) {
                this.macDisplayedColumns.push('macAddress');
            }
            if (this.hasSerial) {
                this.macDisplayedColumns.push('serialNumber');
            }
        }
    }

    AddDisplayedColumns: string[] = [];
    headerColumns: string[] = [];
    headerInputColumns: string[] = [];
    //   addMAC(externalItemId) {
    //     this.hasMac = this.externalItemListData.find(
    //       element => element.id == externalItemId
    //     ).productId.productCategory.hasMac;
    //     this.hasSerial = this.externalItemListData.find(
    //       element => element.id == externalItemId
    //     ).productId.productCategory.hasSerial;

    //     if (!this.hasMac && !this.hasSerial) {
    //       this.messageService.add({
    //         severity: "info",
    //         summary: "info",
    //         detail: "Product type does not allow to add Mac/Serial Number..",
    //         icon: "far fa-times-circle"
    //       });
    //       this.addMACaddress = false;
    //       return;
    //     } else {
    //       this.MACAssignModal = true;
    //       this.externalItemIdForMac = externalItemId;
    //       this.macForm = this.fb.group({
    //         id: [""],
    //         externalItemId: [this.externalItemIdForMac],
    //         status: ["ACTIVE"],
    //         macAddress: this.hasMac ? ["", Validators.required] : [null],
    //         serialNumber: this.hasSerial ? ["", Validators.required] : [null]
    //       });
    //       this.addMACaddress = true;
    //       if (this.hasMac) {
    //     this.headerInputColumns.push('macInput');
    //     this.headerColumns.push('macAddress');
    //     this.AddDisplayedColumns.push('macAddress');
    //   }
    //   if (this.hasSerial) {
    //     this.headerInputColumns.push('serialInput');
    //     this.headerColumns.push('serialNumber');
    //     this.AddDisplayedColumns.push('serialNumber');
    //   }

    //   this.headerInputColumns.push('addButton');
    //   this.AddDisplayedColumns.push('actions');

    //        this.macDialogRef = this.dialog.open(this.addMacDialog, {
    //     width: '60%',
    //     disableClose: true
    //   });
    //       this.getExternalItemMACMapping();
    //     }
    //   }
    addMAC(externalItemId: number) {
        const item = this.externalItemListData.find(element => element.id === externalItemId);

        if (!item) {
            console.error("External item not found for id:", externalItemId);
            return;
        }

        this.hasMac = item.productId.productCategory.hasMac;
        this.hasSerial = item.productId.productCategory.hasSerial;

        if (!this.hasMac && !this.hasSerial) {
            // this.messageService.add({
            //     severity: "info",
            //     summary: "Info",
            //     detail: "Product type does not allow to add Mac/Serial Number..",
            //     icon: "far fa-times-circle"
            // });
            this.toastr.info('Product type does not allow to add Mac/Serial Number.');
            this.addMACaddress = false;
            return;
        }

        // ✅ Reset column arrays before pushing new values
        this.headerInputColumns = [];
        this.headerColumns = [];
        this.AddDisplayedColumns = [];

        // Prepare form
        this.externalItemIdForMac = externalItemId;
        this.macForm = this.fb.group({
            id: [""],
            externalItemId: [this.externalItemIdForMac],
            status: ["ACTIVE"],
            macAddress: this.hasMac ? ["", Validators.required] : [null],
            serialNumber: this.hasSerial ? ["", Validators.required] : [null]
        });

        // Setup column arrays dynamically
        if (this.hasMac) {
            this.headerInputColumns.push("macInput");
            this.headerColumns.push("macAddress");
            this.AddDisplayedColumns.push("macAddress");
        }

        if (this.hasSerial) {
            this.headerInputColumns.push("serialInput");
            this.headerColumns.push("serialNumber");
            this.AddDisplayedColumns.push("serialNumber");
        }

        this.headerInputColumns.push("addButton");
        this.AddDisplayedColumns.push("actions");

        // Open Angular Material dialog
        this.macDialogRef = this.dialog.open(this.addMacDialog, {
            width: "800px",
            disableClose: true
        });

        this.addMACaddress = true;
        this.getExternalItemMACMapping();
    }

    onAddAttribute() {
        // let index;
        // if (this.hasMac) {
        //   index = this.externalItemMacList.find(
        //     element =>
        //       element.macAddress == this.macForm.controls.macAddress.value ||
        //       element.serialNumber == this.macForm.controls.serialNumber.value
        //   );
        // } else {
        //   index = this.externalItemMacList.find(
        //     element => element.serialNumber == this.macForm.controls.serialNumber.value
        //   );
        // }

        // if (index) {
        //   this.messageService.add({
        //     severity: "info",
        //     summary: "info",
        //     detail: "Mac Address Already Exists, It Should Be Unique",
        //     icon: "far fa-times-circle",
        //   });
        //   return;
        // }
        const totalQty = this.externalItemListData.find(
            element => element.id == this.externalItemIdForMac
        ).inTransitQty;
        if (this.externalItemMacList.length == totalQty) {
            // this.messageService.add({
            //     severity: "info",
            //     summary: "info",
            //     detail: "No more inventory available.",
            //     icon: "far fa-times-circle"
            // });
            this.toastr.info('No more inventory available.');
            return;
        }

        if (this.macForm.valid) {
            const macMappingValue = {
                id: null,
                externalItemId: this.externalItemIdForMac,
                macAddress: this.macForm.controls.macAddress.value,
                serialNumber: this.macForm.controls.serialNumber.value,
                status: "ACTIVE"
            };

            this.externalItemManagementService
                .postMethod("/externalitemmacserialmapping/save", macMappingValue)
                .subscribe(
                    (res: any) => {
                        if (res.responseCode == 406) {
                            this.toastr.info(`${res.responseMessage}`);

                        } else {
                            this.macForm.reset();
                        }

                        this.getExternalItemMACMapping();
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.error}`, 'Failed!');
                    }
                );
        }
    }
    //   onAddAttribute() {
    //   if (this.macForm.invalid) return;

    //   const newEntry = {
    //     macAddress: this.macForm.value.macAddress,
    //     serialNumber: this.macForm.value.serialNumber,
    //     itemId: Date.now() // temp id, replace with backend id
    //   };

    //   this.externalItemMacList = [...this.externalItemMacList, newEntry];
    //   this.macForm.reset();
    // }

    createPolicyDetailsForm(): UntypedFormGroup {
        return this.fb.group({
            id: [""],
            externalItemId: [this.externalItemIdForMac],
            status: ["ACTIVE"],
            macAddress: ["", Validators.required],
            serialNumber: ["", Validators.required]
        });
    }

    getExternalItemMACMapping() {
        this.externalItemMacList = [];
        this.externalItemManagementService
            .getExternalItemMacMapping(this.externalItemIdForMac)
            .subscribe(
                (res: any) => {
                    this.externalItemMacList = res.dataList;
                    //     this.externalItemMacList = [
                    //   { macAddress: '00:11:22:33:44:55', serialNumber: 'SN12345', externalItemNumber: 'EX123' },
                    //   { macAddress: '66:77:88:99:AA:BB', serialNumber: 'SN67890', externalItemNumber: 'EX456' },
                    // ];
                },
                (error: any) => {
                    this.toastr.error(`${error.error.error}`, 'Failed!');
                }
            );
    }

    deleteMACMapping(mapping) {
        if (mapping.outwardId != null) {
            // this.messageService.add({
            //     severity: "error",
            //     summary: "Error",
            //     detail: "This MAC address is availabel in outward.",
            //     icon: "far fa-times-circle"
            // });
            this.toastr.error('This MAC address is availabel in outward.', 'Failed!');
            return;
        }

        this.externalItemManagementService.deleteMacMapping(mapping).subscribe(
            (res: any) => {
                this.getExternalItemMACMapping();
            },
            (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }
    assignExternalItemID: any;
    externalItemIDStatus: any;
    approveExternalItemData = [];

    @ViewChild('approveStatusDialog') approveStatusDialog!: TemplateRef<any>;
    approveDialogRef!: MatDialogRef<any>;

    approveChangeStatus(id) {
        this.approveChangeStatusModal = true;
        this.approveDialogRef = this.dialog.open(this.approveStatusDialog, {
            width: '40%',
            disableClose: true
        });
        this.assignExternalItemID = id;
    }

    @ViewChild('rejectStatusDialog') rejectStatusDialog!: TemplateRef<any>;
    rejectDialogRef!: MatDialogRef<any>;
    rejectChangeStatus(id) {
        this.rejectChangeStatusModal = true;
        this.rejectDialogRef = this.dialog.open(this.rejectStatusDialog, {
            width: '40%',
            disableClose: true
        });
        this.assignExternalItemID = id;
    }

    closeStatusModal() {
        this.approveChangeStatusModal = false;
        this.rejectChangeStatusModal = false;
        this.approveDialogRef.close();
        this.assignExternalItemForm.get("remark").reset();
    }
    closeStatusModal1() {
        if (this.rejectDialogRef) {
            this.rejectDialogRef.close();
        }
        this.rejectChangeStatusModal = false;
    }

    approveExternalItemGroup() {
        this.assignExternalItemSubmitted = true;
        this.submitted = true;
        this.approveExternalItemData = [];
        this.approveChangeStatusModal = true;
        if (this.assignExternalItemForm.valid) {
            let url = `/externalitemmanagement/externalItemApproval`;
            let approvalExternalItemData = {
                id: this.assignExternalItemID,
                approvalStatus: "Approve",
                approvalRemark: this.assignExternalItemForm.controls.remark.value
            };

            this.externalItemManagementService.updateMethod(url, approvalExternalItemData).subscribe(
                (response: any) => {
                    this.submitted = false;
                    this.close();
                    this.approveExternalItemData = response.data;
                    this.assignExternalItemForm.reset();

                    this.getExternalItemList("");
                    this.approveDialogRef.close();
                    this.approveChangeStatusModal = false;
                },
                (error: any) => {
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.msg,
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.error(`${error.error.msg}`, 'Failed!');
                }
            );
        }
    }

    rejectExternalItemGroup() {
        this.assignExternalItemSubmitted = true;
        this.submitted = true;
        this.approveExternalItemData = [];
        this.rejectChangeStatusModal = true;
        if (this.assignExternalItemForm.valid) {
            let url = `/externalitemmanagement/externalItemApproval`;
            let approvalExternalItemData = {
                id: this.assignExternalItemID,
                approvalStatus: "Rejected",
                approvalRemark: this.assignExternalItemForm.controls.remark.value
            };

            this.externalItemManagementService.updateMethod(url, approvalExternalItemData).subscribe(
                (response: any) => {
                    this.approveExternalItemData = response.data;
                    this.close();

                    this.assignExternalItemForm.reset();
                    this.getExternalItemList("");
                    this.rejectDialogRef.close();
                },
                (error: any) => {
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.msg,
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.error(`${error.error.msg}`, 'Failed!');
                }
            );
        }
    }

    getExternalItemData(externalItemId: any) {
        this.externalItemDetails = [];
        this.ifExternalItemDetails = true;
        this.IfPersonalExternalItemDataShow = true;
        if (externalItemId) {
            const url = "/inwards/" + externalItemId;
            this.externalItemManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.externalItemDetails = response.data;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.error}`, 'Failed!');
                }
            );
        }
    }
    externalItemDeatilsClear() {
        this.IfPersonalExternalItemDataShow = false;
    }
    personalExternalItemData() {
        this.IfPersonalExternalItemDataShow = true;
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
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }
    onclosed() {
        this.getExternalItemList("");
        this.MACAssignModal = false;
        this.MACShowModal = false;
    }
    close() {
        this.assignExternalItemSubmitted = false;
        this.assignExternalItemForm.reset();
        this.approveChangeStatusModal = false;
        this.rejectChangeStatusModal = false;
    }
    clearMacMapping() {
        this.getExternalItemList("");
        this.MACAssignModal = false;
    }
    // clearMacMapping() {
    //   this.externalItemMacList = [];
    //   this.macForm.reset();
    // }
    isDestAStaffOrCustomer = false;
    getDestinations(ownershipType): void {
        this.externalItemManagementFormGroup.controls.ownerId.setValue("");
        if (ownershipType == "Partner Owned") {
            this.isDestAStaffOrCustomer = false;
        } else if (ownershipType == "Customer Owned") {
            this.isDestAStaffOrCustomer = true;
        }
        this.getOwnerFlag = true;
        this.ownerShow = false;
    }

    canExit() {
        if (!this.externalItemManagementFormGroup.dirty) return true;
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
    getExternalItemDetails(id) {
        const url = "/externalitemmanagement/" + id;
        this.externalItemManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.viewExternalItemData = res.data;
                this.listView = false;
                this.createView = false;
                this.detailView = true;
                // Open Angular Material dialog
                this.externalItemDetailDialogRef = this.dialog.open(this.externalItemDetailDialog, {
                    width: '900px',
                    disableClose: true
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }

    externalItemList() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
    }
    quantityInValidation(event) {
        var num = String.fromCharCode(event.which);
        if (!/[0-9]/.test(num)) {
            event.preventDefault();
        }
    }
    // showSelectStaffModel = false;
    selectedPartner: any = [];
    selectedCustomer: any = [];
    // selectStaffType = "";
    ownerSelectList: any = [];
    onPartnerSelect(event: any, partner: any) {
        if (event.checked) {
            this.selectedPartner.push(partner);
        } else {
            this.selectedPartner = this.selectedPartner.filter(p => p !== partner);
        }
    }
    onCustomerSelect(event: any, customer: any) {
        if (event.checked) {
            this.selectedCustomer.push(customer);
        } else {
            this.selectedCustomer = this.selectedCustomer.filter(p => p !== customer);
        }
    }
    modalOpenSelectOwner(type) {
        if (type == "Partner Owned") {
            this.getpartnerList("");
            // $("#selectPartnerOwner").modal("show");
        } else if (type == "Customer Owned") {
            this.getCustomerList("");
            // $("#selectCustomerOwner").modal("show");
        }
    }

    selectedOwnerChange(selectedData, selectOwnerType) {
        // this.showSelectStaffModel = false;
        let data = selectedData[0];
        if (selectOwnerType == "Partner Owned") {
            this.ownerSelectList.push({
                id: Number(data.id),
                name: data.name
            });
            this.externalItemManagementFormGroup.patchValue({
                ownerId: data.id
            });
        } else if (selectOwnerType == "Customer Owned") {
            this.ownerSelectList.push({
                id: Number(data.id),
                name: data.firstname
            });
            this.externalItemManagementFormGroup.patchValue({
                ownerId: data.id
            });
        }
        // this.dialogRef.close();
    }
    removeSelectOwner() {
        this.ownerSelectList = [];
    }
    getpartnerList(list) {
        const serviceAreaId = this.externalItemManagementFormGroup.get("serviceAreaId").value;
        this.searchkey = "";

        if (serviceAreaId == null) {
            // this.messageService.add({
            //     severity: "info",
            //     summary: "info",
            //     detail: "Please select service area",
            //     icon: "far fa-times-circle"
            // });
            this.toastr.info('Please select service area');
        } else {
            const url =
                "/externalitemmanagement/getPartnerListServiceArea?serviceAreaId=" + serviceAreaId;
            let partnerdata = {
                page: this.currentPagepartnerListdata,
                pageSize: this.partnerListdataitemsPerPage
            };
            this.partnerListData = [];
            this.externalItemManagementService.postMethod(url, partnerdata).subscribe(
                (response: any) => {
                    this.partnerListData = response.dataList;
                    this.partnerListdatatotalRecords = response.totalRecords;
                    if (!this.editMode) {

                        // $("#selectPartnerOwner").modal("show");
                        this.partnerOwnerDialogRef = this.dialog.open(this.partnerOwnerDialog, {
                            width: '1000px',
                            disableClose: true,
                            data: { type: 'Partner Owned' }
                        });

                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.error}`, 'Failed!');
                }
            );
        }
    }
    getCustomerList(list) {
        const serviceAreaId = this.externalItemManagementFormGroup.get("serviceAreaId").value;
        this.searchkey = "";

        if (serviceAreaId == null) {
            // this.messageService.add({
            //     severity: "info",
            //     summary: "info",
            //     detail: "Please select service area",
            //     icon: "far fa-times-circle"
            // });
            this.toastr.info('Please select service area');
        } else {
            const url =
                "/externalitemmanagement/getCustomerListServiceArea?serviceAreaId=" + serviceAreaId;
            let customerdata = {
                page: this.currentPagecustomerListdata,
                pageSize: this.customerListdataitemsPerPage
            };
            this.customerListData = [];
            this.externalItemManagementService.postMethod(url, customerdata).subscribe(
                (response: any) => {
                    this.customerListData = response.dataList;

                    this.customerListdatatotalRecords = response.totalRecords;
                    if (!this.editMode) {
                        // $("#selectCustomerOwner").modal("show");
                        this.customerOwnerDialogRef = this.dialog.open(this.customerOwnerDialog, {
                            width: '1000px',
                            disableClose: true,
                            autoFocus: false,
                            data: { type: 'Customer Owned' }
                        });
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.error}`, 'Failed!');
                }
            );
        }
    }
    paginatePartner(event) {
        this.currentPagepartnerListdata = event.page + 1;
        this.getpartnerList("");
    }
    paginateCustomer(event) {
        this.currentPagecustomerListdata = event.page + 1;
        this.getCustomerList("");
    }
    saveSelOwner(type) {
        if (type == "Partner Owned") {
            this.selectedOwnerChange(this.selectedPartner, type);
            this.partnerOwnerDialogRef?.close();

            // $("#selectPartnerOwner").modal("hide");
        } else if (type == "Customer Owned") {
            this.selectedOwnerChange(this.selectedCustomer, type);
            this.customerOwnerDialogRef?.close();

            // $("#selectCustomerOwner").modal("hide");
        }
    }
    //   modalCloseOwner(type) {
    //     if (type == "Partner Owned") {
    //       $("#selectPartnerOwner").modal("hide");
    //       this.searchPartnerValue = "";
    //       this.searchPartnerOption = "";
    //       // this.partnerFieldEnable = false;
    //     } else if (type == "Customer Owned") {
    //       $("#selectCustomerOwner").modal("hide");
    //       this.searchParentCustValue = "";
    //       this.searchParentCustOption = "";
    //       this.parentFieldEnable = false;
    //     }
    //   }
    //   ------------------------

    //   selectedPartner: any;
    //   searchPartnerValue = '';
    onCancel() {
        this.editMode = false;
        this.externalItemManagementFormGroup.reset();
        this.dialogRef.close();
    }
    // modalCloseOwner(type: string) {
    //     if (type === 'Customer Owned' && this.customerOwnerDialogRef) {
    //       this.customerOwnerDialogRef.close();
    //     }
    //   }

    modalCloseOwner(type) {
        if (type == "Partner Owned") {

            this.partnerOwnerDialogRef?.close();
            this.searchPartnerValue = "";
            this.searchPartnerOption = "";
            // this.partnerFieldEnable = false;
        } else if (type == "Customer Owned") {
            this.customerOwnerDialogRef.close();
            this.searchParentCustValue = "";
            this.searchParentCustOption = "";
            this.parentFieldEnable = false;
        }
    }
    closeMACDialog() {
        if (this.macDialogRef) {
            this.macDialogRef.close();
        }
    }
    onCancelpartnerOwnerDialog() {
        this.partnerOwnerDialogRef.close();
    }

    // closeStatusModal() {
    //   if (this.approveDialogRef) {
    //     this.approveDialogRef.close();
    //   }
    //   this.approveChangeStatusModal = false;
    // }
}