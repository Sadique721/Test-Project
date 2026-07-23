import { Component, Input, OnInit, Output, ViewChild, EventEmitter, TemplateRef, ChangeDetectorRef } from "@angular/core";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService, TreeNode } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomerInventoryManagementService } from "src/app/service/customer-inventory-management.service";
import { BehaviorSubject, Observable, Observer } from "rxjs";
import { CustomerInventoryDetailsService } from "src/app/service/customer-inventory-details.service";
import { CustomerService } from "src/app/service/customer.service";
import { Table } from "primeng/table";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { formatDate } from "@angular/common";
import { Regex } from "src/app/constants/regex";
import { element } from "protractor";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import { ServiceAreaService } from "src/app/service/service-area.service";
import { LoginService } from "src/app/service/login.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { InwardService } from "src/app/service/inward.service";
import { saveAs as importedSaveAs } from "file-saver";
import { DomSanitizer } from "@angular/platform-browser";
import { IntegrationConfigurationService } from "src/app/service/integration-configuration.service";
import { NavMasterService } from "../../nav-master/nav-master.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import * as FileSaver from "file-saver";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { SelectStaffComponent } from "../../common/select-staff/select-staff.component";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { CustomerInventorySpecificationParamsComponent } from "../../customer-inventory-specification-params/customer-inventory-specification-params.component";
import { Toast } from "primeng/toast";
import { ToastrService } from "ngx-toastr";

import { MatRadioChange } from '@angular/material/radio';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';


declare var $: any;

export interface Country {
    name?: string;
    code?: string;
}

export interface Representative {
    name?: string;
    image?: string;
}

export interface Customer {
    id?: number;
    name?: string;
    country?: Country;
    company?: string;
    date?: string;
    status?: string;
    representative?: Representative;
    activity?: any;
    itemAssemblyName?: any;
    itemAssemblyId?: any;
    custInventoryListId?: any;
}

@Component({
    selector: "app-cust-inventory-management",
    templateUrl: "./cust-inventory-management.component.html",
    styleUrls: ["./cust-inventory-management.component.css"],
    standalone: false
})
export class CustInventoryManagementComponent implements OnInit {
    @ViewChild('assignExternalInventoryDialog') assignExternalInventoryDialog!: TemplateRef<any>;
    @ViewChild('attachDeviceDialogTemplate') attachDeviceDialogTemplate!: TemplateRef<any>;
    dialogRefAttachDevice: any;
    @ViewChild('refundDialogTemplate') refundDialogTemplate!: TemplateRef<any>;
    refundAmountModal: boolean = false;

    @ViewChild('assignInventoryWithPlanDialog') assignInventoryWithPlanDialog!: TemplateRef<any>;
    @ViewChild('assignOtherInventoryDialog') assignOtherInventoryDialog!: TemplateRef<any>;
    assignOtherInventoryDialogRef: any;
    macTableColumns = ['select', 'itemId', 'condition', 'macAddress', 'serialNumber', 'action'];
    @ViewChild('selectCustomerDialog') selectCustomerDialog!: TemplateRef<any>;
    selectCustomerDialogRef: any;
    customerTableColumns = ['select', 'name', 'username'];
    @ViewChild('selectStaffDialog') selectStaffDialog!: TemplateRef<any>;
    selectStaffDialogRef: any;

    @ViewChild('approveAssignInventoryDialog') approveAssignInventoryDialog!: TemplateRef<any>;
    approveAssignInventoryDialogRef!: MatDialogRef<any>;
    @ViewChild('rejectAssignInventoryDialog') rejectAssignInventoryDialog!: TemplateRef<any>;
    rejectAssignInventoryDialogRef!: MatDialogRef<any>;
    @ViewChild('uploadDocumentDialog') uploadDocumentDialog!: TemplateRef<any>;
    uploadDocumentDialogRef!: MatDialogRef<any>;

    @ViewChild('replaceInventoryDialog') replaceInventoryDialog!: TemplateRef<any>;
    replaceInventoryDialogRef!: MatDialogRef<any>;
    @ViewChild('inventoryWorkflowDialog') inventoryWorkflowDialog!: TemplateRef<any>;
    inventoryWorkflowDialogRef: any;

    inventoryWorkflowColumns: string[] = ['action', 'staffName', 'remark', 'actionDate'];
    replaceInventoryColumns: string[] = [];
    @ViewChild('approveRemoveInventoryDialog') approveRemoveInventoryDialog!: TemplateRef<any>;
    approveRemoveInventoryDialogRef: any;
    @ViewChild('rejectRemoveInventoryDialog') rejectRemoveInventoryDialog!: TemplateRef<any>;
    rejectRemoveInventoryDialogRef: any;
    @ViewChild('wifiDialog') wifiDialog!: TemplateRef<any>;
    wifiDialogRef: any;

    displayedColumns: string[] = [
        'oldSerialNumber',
        'oldMacAddress',
        'newSerialNumber',
        'newMacAddress'
    ];

    @ViewChild('networkHierarchyDialog') networkHierarchyDialog!: TemplateRef<any>;
    networkHierarchyDialogRef!: MatDialogRef<any>;
    hierarchyDisplayedColumns: string[] = [
        'parentDeviceName', 'parentDevicePortType', 'parentDevicePortNumber',
        'parentDeviceType', 'parentDeviceOwnerType', 'parentOwnerName',
        'parentDeviceMacAddress', 'parentDeviceSerialNumber',
        'childDeviceName', 'childDevicePortType', 'childDevicePortNumber',
        'childDeviceType', 'childDeviceOwnerType', 'childOwnerName',
        'childDeviceMacAddress', 'childDeviceSerialNumber'
    ];

    @ViewChild('parentDeviceDialog') parentDeviceDialog!: TemplateRef<any>;
    parentDeviceDialogRef!: MatDialogRef<any>;

    @ViewChild('portDetailDialog') portDetailDialog!: TemplateRef<any>;
    portDetailDialogRef!: MatDialogRef<any>;
    @ViewChild('inventorySpecificationDialog') inventorySpecificationDialog!: TemplateRef<any>;
    inventorySpecificationDialogRef: any;
    custData: any = {};
    customerId: number = 0;
    custType: String = "";
    // @Output() backButton = new EventEmitter();
    customerInventoryListItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    customerInventoryListDataCurrentPage = 1;
    customerInventoryListDataTotalRecords: number;
    assignedInventoryList: any = [];
    staffUserId: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    pageOptions = [5, 10, 20, 50, 100];
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    inventoryData = new BehaviorSubject({
        inventoryData: ""
    });
    macAddressList: any = [];
    macAddressList1: any = [];
    macAddressList2: any = [];
    macAddressListSTB: any = [];
    macAddressListCard: any = [];
    replaceProducts: any = [];

    // @ViewChild(MatPaginator) dt1!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    // @ViewChild('dt1') dt1!: MatPaginator;


    // getAllInventoryofCust: MatTableDataSource<any> = new MatTableDataSource<any>([]);

    displayedColumns1: string[] = [
        'startDate',
        'condition',
        'serviceName',
        'connectionNo',
        'macAddress',
        'serialNumber',
        'postPaidPlanName',
        'externalItemGroupNumber',
        'event',
        'approvalRemark'
    ];
    // @ViewChild(MatPaginator) paginator!: MatPaginator;
    // @ViewChild(MatSort) sort!: MatSort;
    // @ViewChild('dt1') dt1!: MatPaginator;


    // getAllInventoryofCust: MatTableDataSource<any> = new MatTableDataSource<any>([]);

    // displayedColumns1: string[] = [
    //     'startDate',
    //     'condition',
    //     'serviceName',
    //     'connectionNo',
    //     'macAddress',
    //     'serialNumber',
    //     'postPaidPlanName',
    //     'externalItemGroupNumber',
    //     'event',
    //     'approvalRemark'
    // ];
    inventoryJobTypeList: any[] = [];
    natureList: any[] = [];

    selectedCardOption: any;
    selectedStbOption: any;
    replaceInventoryForm: UntypedFormGroup;
    removeRemarkForm: UntypedFormGroup;
    replaceSumitted: boolean = false;
    displaySelectParentCustomer: boolean = false;
    replacementType = [
        { label: "Permanant Replacement", value: "Permanant Replacement" },
        { label: "Temporary Replacement", value: "Temporary Replacement" }
    ];

    replacementReasonType = [
        { label: "Defective", value: "Defective" },
        { label: "Upgrade", value: "Upgrade" },
        { label: "Surrender", value: "Surrender" },
        { label: "Others", value: "Others" }
    ];
    totalRecords: number;
    pageSize: number = 5;
    currentPage = 1;
    getAllInventoryofCustFull: any[] = [];
    getAllInventoryofCust = new MatTableDataSource<any>();
    selectedNature: any;
    selectedInventoryJobType: any;
    dialogRef: any;
    productPlanMappingId: any;
    selPlanId: any;
    selReplacePlanId: any;
    selOtherItemId: any;
    selectedMACAddress: any = "";
    selectedPlanMACAddress: any = "";
    selectedSTBPlanMACAddress: any = "";
    selectedCardPlanMACAddress: any = "";
    selectedExternalMACAddress: any = "";
    selectedReplaceMACAddress: any = "";
    selectedReplaceMACAddress2: any = "";
    // selectedMACAddress1: any ="";
    selectedMACAddress2: any = "";
    replaceOldMappingId: any = "";
    @ViewChild("btnClose") btnClose;
    inventoryApproveProgressPerPage = RadiusConstants.ITEMS_PER_PAGE;
    inventoryApproveProgresstotalRecords: String;
    currentPageInventoryApproveProgress = 1;
    inventoryApproveProgressDetail: any;
    inventoryWorkflowAuditData: any;
    editInventory: boolean = false;
    editSTBCradInventory: boolean = false;
    removeRemarkSubmitted: boolean = false;
    inOutMacMapping = {
        oldId: "",
        oldMac: "",
        oldSerial: "",
        newMac: "",
        newSerial: "",
        newId: "",
        oldStatus: "",
        newStatus: ""
    };
    inventoryAssignForm: UntypedFormGroup;
    wifiForm: UntypedFormGroup;
    refundAmountForm: UntypedFormGroup;
    inventoryAssignSumitted: boolean = false;
    macPlanListFlag: boolean = false;
    macReplaceListFlag: boolean = false;
    macExternalListFlag: boolean = false;
    serviceList = [];
    planList: any = [];
    billToPlanName: any = "";
    billToPlan: any;
    billToPlanFlag: boolean = false;
    isInvoiceDataFlag: boolean = false;
    isInvoiceDataSingleFlag: boolean = false;
    isInvoiceDataSingleReplaceFlag: boolean = false;
    isInvoiceDataPairFlag: boolean = false;
    requiredApprovalSingleFlag: boolean = false;
    requiredApprovalPairFlag: boolean = false;
    requiredApprovalNonSerialFlag: boolean = false;
    requiredApprovalPlanFlag: boolean = false;
    connectionNoList: any = [];
    connectionDetailData: any = [];
    custPlanCategory: any = "";
    custDiscount: any = "";
    custDiscountType: any = "";
    selectedCustDiscount: any = "";
    selectedPairDiscount: any = "";
    planGroupName: any = "";
    planGroupId: any = "";
    planGroupPlanMappingFlag: boolean = false;
    individualPlanMappingFlag: boolean = false;
    getPlanSingleSplitterFlag: boolean = false;
    getPlanPairSplitterFlag: boolean = false;
    allActiveProducts: any = [];
    custServiceMappingData: any = [];
    getAllCustomerInvetoryDetailshistoryData: any = [];
    allSTBProducts: any = [];
    allCardProducts: any = [];
    allActiveNonTrackableProducts: any = [];
    externalInventoryAssignForm: UntypedFormGroup;
    externalInventoryAssignSumitted: boolean = false;
    planInventoryAssignForm: UntypedFormGroup;
    approveAssignInventoryForm: UntypedFormGroup;
    rejectAssignInventoryForm: UntypedFormGroup;
    approveRemoveInventoryForm: UntypedFormGroup;
    rejectRemoveInventoryForm: UntypedFormGroup;
    itemDetailData: any;
    actualProductPrice: Number;
    newProductPrice: Number;
    ownershipForm: UntypedFormGroup;
    approved = false;
    approveRemove = false;
    macMappingId: any;
    custInventoryId: any;
    selectAssignInventoryApproveStaff: any;
    selectAssignInventoryRejectStaff: any;
    selectRemoveInventoryApproveStaff: any;
    selectRemoveInventoryRejectStaff: any;
    approveAssignInventoryData = [];
    rejectAssignInventoryData = [];
    approveRemoveInventoryData = [];
    rejectRemoveInventoryData = [];
    reject = false;
    ownershipFlag: any = "";
    rejectRemove = false;
    assignInventoryId: any;
    customerInventoryId: any;
    assignInventoryName: any;
    assignRemoveInventoryId: any;
    nextApproverId: any;
    assignAssignInventorysubmitted: boolean = false;
    rejectAssignInventorySubmitted: boolean = false;
    assignRemoveInventorysubmitted: boolean = false;
    rejectRemoveInventorySubmitted: boolean = false;
    assignReplaceInventorySubmitted: boolean = false;
    planInventoryAssignSumitted: boolean = false;



    inventoryStatus = [
        { label: "Active", value: "ACTIVE" },
        { label: "Inactive", value: "INACTIVE" }
    ];
    isInvoiceData = [
        { label: "YES", value: true },
        { label: "NO", value: false }
    ];
    isRequiredApprovalData = [
        { label: "YES", value: true },
        { label: "NO", value: false }
    ];
    isInventoryFreeData = [
        { label: "YES", value: true },
        { label: "NO", value: false }
    ];
    billToData = [
        { label: "ORGANIZATION", value: "ORGANIZATION" },
        { label: "CUSTOMER", value: "CUSTOMER" }
    ];
    itemConditionData = [
        { label: "New", value: "New" },
        { label: "Refurbished", value: "Refurbished" }
    ];
    workingFrequencyType = [
        { label: "2.4G", value: "0" },
        { label: "5G", value: "1" }
    ];
    productByPlanList: any;
    productByPlanListReplace: any;
    getAllPlanIvnetoryIdOnPlanIdList: any;
    otherInventoryReplaceFlag: boolean = false;
    planInventoryReplaceFlag: boolean = false;
    planName: any = "";
    productCategoryName: any = "";
    productCategoryId: any = "";
    inventoryDataByProductCateId: any = [];
    mappingList: any = [];
    oldOfferPricePlan: Number;
    newPriceValue: Number;
    newOfferPricePlan: Number;
    oldOfferOtherSigle: Number;
    perUOMCharge: Number;
    newUOMAmount: Number;
    oldOfferSTB: Number;
    oldOfferCard: Number;
    newOfferSTB: Number;
    newOfferCard: Number;
    oldOfferOtherSigleReplace: Number;
    newOfferSingleFlag: boolean = false;
    newOfferSTBFlag: boolean = false;
    newOfferCardFlag: boolean = false;
    newOfferOtherSigle: Number;
    newOfferOtherSigleReplace: Number;
    oldOfferPricePlanFlag: boolean = false;
    newOfferPriceFlag: boolean = false;
    invoiceDataReadOnly: boolean = false;
    oldOfferBasedDiscountSingleFlag: boolean = false;
    oldOfferBasedDiscountSingleReplaceFlag: boolean = false;
    oldOfferBasedDiscountSTBPairFlag: boolean = false;
    oldOfferBasedDiscountCardPairFlag: boolean = false;
    discountPairFlag: boolean = false;
    oldOfferPriceSingleReplaceFlag: boolean = false;
    oldOfferPriceSingleFlag: boolean = false;
    oldOfferPriceSTBFlag: boolean = false;
    oldOfferBasedDiscountPairFlag: boolean = false;
    oldOfferPriceCardFlag: boolean = false;
    oldOfferBasedDiscountNonSerialFlag: boolean = false;
    oldOfferPriceNonSerialFlag: boolean = false;
    isInvoiceDataNonSerialFlag: boolean = false;
    newOfferNonSerialFlag: boolean = false;
    approveRemoveFlag: boolean = false;
    rejectRemoveFlag: boolean = false;
    billableCusList: any;
    newFirst = 0;
    parentCustomerDialogType: any = "";
    selectedParentCust: any = [];
    currentPageParentCustomerListdata = 1;
    showItemPerPage = 5;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    prepaidParentCustomerList: any;
    editCustomerId: any;
    customerListData: any = [];
    parentCustomerListdatatotalRecords: any;
    oldOfferBasedDiscountPlanFlag: boolean = false;
    selItemCondition: any = "";
    selItemConditionReplace: any = "";
    selPlanItemCondition: any = "";
    customers: Customer[] = [];
    rowGroupMetadata: any = {};
    searchOptionSelect = this.commondropdownService.customerInventorySearchOption;
    productSelectionType = [
        { label: "Single Item", value: false },
        { label: "Pair Item", value: true }
        // { label: "Non Serialized Item", value: "Non Serialized Item" },
    ];
    ItemSelectionType = [
        { label: "Serialized Item", value: "Serialized Item" },
        { label: "Non Serialized Item", value: "Non Serialized Item" }
    ];
    externalItemList: any = [];
    getNonTrackableProductQtyList: any = [];
    getAllSerializedProductFlag: boolean = false;
    itemConditionSingleFlag: boolean = false;
    itemConditionPlanSeriFlag = false;
    itemConditionPlanPairFlag = false;
    selAssemblyTypePlanFlag = false;
    selAssemblyTypePlanGroupFlag = false;
    getAssemblyNameflag: boolean = false;
    itemConditionPairFlag: boolean = false;
    itemConditionSingleReplaceFlag: boolean = false;
    getAllAssemblyTypeFlag: boolean = false;
    getAllAssemblyNameFlag: boolean = false;
    getAllPairProductFlag: boolean = false;
    getSplitterFlag: boolean = false;
    getAllSingleItemMacFlag: boolean = false;
    getAllPairItemMacFlag: boolean = false;
    getAllPairItemMacReplaceFlag: boolean = false;
    billToSigleFlag: boolean = false;
    billToSigleReplaceFlag: boolean = false;
    billToPairFlag: boolean = false;
    parentCustList: any;
    searchParentCustValue = "";
    searchParentCustOption = "";
    parentFieldEnable = false;
    getAllConnectionNumberFlag: boolean = false;
    getExternalProductFlag: boolean = false;
    getExternalItemListFlag: boolean = false;
    getAllPlanFlag: boolean = false;
    getPlanInventoryIdFlag: boolean = false;
    getProductCategoryFlag: boolean = false;
    getProductForPlanInventoryAssignFlag: boolean = false;
    getAllPairPlanProductSTBFlag: boolean = false;
    getAllPairProductCardFlag: boolean = false;
    getAllNonSerializedProductFlag: boolean = false;
    serializedItemAssignFlag: boolean = false;
    nonSerializedItemAssignFlag: boolean = false;
    availableQty = 0;
    showQtyError: boolean;
    priceErrorMsg = "";
    submitted: boolean = false;
    showError: boolean = false;
    negativeAssignQtyError: boolean;
    availableQtyFlag: boolean = false;
    UOM: any = "";
    filterProductData: any = [];
    hasMac: boolean;
    hasSerial: boolean;
    enterMacSerial: any = "";
    editMacSerialBtn: any = "";
    enterSTBSerial: any = "";
    editSTBSerialBtn: any = "";
    enterCardSerial: any = "";
    editCardSerialBtn: any = "";
    enterPlanLevelMacSerial: any = "";
    editPlanLevelMacSerialBtn: any = "";
    editReplacementLevelMacSerialBtn: any = "";
    enterReplacementLevelMacSerial: any = "";
    removeId: any = "";
    removeCustinventoryid: any = "";
    removeItemId: any = "";
    fileterGlobalSingleItem: any = "";
    stbFileterGlobal: any = "";
    cardFileterGlobal: any = "";
    filterGlobalReplaceSingle: any = "";
    fileterGlobalPlanlevel: any = "";
    stbFileterGlobalReplace: any = "";
    cardFileterGlobalReplace: any = "";
    externalItemsFilterGlobal: any = "";
    // getAllInventoryofCust: any = [];
    // getAllInventoryofCust: MatTableDataSource<any> = new MatTableDataSource<any>([]);
    getAllInventoryofCustFilterGlobal: any = "";
    planInventoryId: any = [];
    removeRemark: any = "";
    selectedReplacementType: any = "";
    replaceAssignForm: UntypedFormGroup;
    isApproveRequest: boolean;
    replaceInventoryIdInOutMacMapping: string;
    currentDate = new Date();
    refundAmountSubmitted: boolean = false;
    serviceCustomerId;
    replaceInventoryCustId;
    isShowConnection = true;
    serviceSerialNumbers = [];
    showSelectStaffModel: boolean = false;
    displayAssignPlanInventoryModal: boolean = false;
    displayDialogAssignOtherInventory: boolean = false;
    displayAssignInventoryWithExternalItemGroup: boolean = false;
    displayCustomerInventoryHistory: boolean = false;
    displayDTVHistory: boolean = false;
    wifiModel: boolean = false;
    displaySwapInventoryPlan: boolean = false;
    replaceInventoryModal: boolean = false;
    DTVHistoryAccess: boolean = false;
    replaceAccess: boolean = false;
    editAccess: boolean = false;
    removeAccess: boolean = false;
    otherInventoryAccess: boolean = false;
    planInventoryAccess: boolean = false;
    externalInventoryAccess: boolean = false;
    InventoryHistoryAccess: boolean = false;
    swapInventoryAccess: boolean = false;
    productData: any;
    inventorySpecificationParamModal: boolean = false;
    inventorySpecificationDetailModal: boolean = false;
    inventorySpecificationDetails: any[] = [];
    specDetailsShow: boolean = false;
    editedRowIndex: number = -1;
    selectedService: any;
    selectedSerialNumber: any;
    inventoryId: any;
    isEditEnable: boolean = false;
    inventoryIdData: any;
    // uploadDocForm: FormGroup;
    // selectedFileUploadPreview: any[];
    uploadDocumentId: boolean;
    selectedFile: any;
    inventoryFileData: any = "";
    downloadDocumentId: boolean;
    multiFiles: FileList;
    filenameList: any;
    previewUrl: any;
    documentPreview: boolean = false;
    wifiDetailsList: any;
    wifiSubmitted: boolean = false;
    wifiInventory: any;
    isUpStreamDetailView: boolean = false;
    nms_enable: boolean = false;
    fiber_home_enable: boolean = false;
    editWifi: boolean = false;
    uploadDocumentAccess: boolean = false;
    downloadDocumentsAccess: boolean = false;
    downloadDocumentAccess: boolean = false;
    viewDocumentAccess: boolean = false;
    deleteDocumentAccess: boolean = false;
    currentMacAddressCardListdata = 1;
    macAddressListCarditemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    macAddressListCardtotalRecords: any;
    newFirstMacAddressCard: number = 0;
    productMacAddressCardId: any;
    currentMacAddressListdata = 1;
    macAddressListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    macAddressListtotalRecords: any;
    newFirstMacAddress: number = 0;
    productMacAddressId: any;
    currentMacAddress1Listdata = 1;
    macAddress1ListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    macAddress1ListtotalRecords: any;
    newFirstMacAddress1: number = 0;
    productMacAddress1Id: any;
    currentMacAddrRep1Listdata = 1;
    macAddrRep1ListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    macAddrRep1ListtotalRecords: any;
    newFirstMacAddrRep1: number = 0;
    productMacAddrRep1Id: any;
    macAddress2ListtotalRecords: any;
    currentMacAddress2Listdata = 1;
    macAddress2ListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    newFirstMacAddress2: number = 0;
    productMacAddress2Id: any;
    macAddrRep2ListtotalRecords: any;
    currentMacAddrRep2Listdata = 1;
    macAddrRep2ListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    newFirstMacAddrRep2: number = 0;
    productMacAddrRep2Id: any;
    uploadedFiles: any[] = [];
    uploadDocForm: UntypedFormGroup[] = [];
    selectedFileUploadPreview: any[] = [];
    // multiFiles: File[] = [];
    // selectedFile: File;
    // submitted: boolean = false;
    tabs = [
        "FAT Optical Power Picture",
        "FAT Inside Picture",
        "FAT Outside Picture",
        "ONU Optical Power Picture",
        "Optical Power Range",
        "Installation Picture",
        "Speedtest Picture",
        "Smart Gadget"
    ];
    inventoryFileDocData: any;
    opticalRangeData: any[] = [
        { label: "-15", value: "-15" },
        { label: "-16", value: "-16" },
        { label: "-17", value: "-17" },
        { label: "-18", value: "-18" },
        { label: "-19", value: "-19" },
        { label: "-20", value: "-20" },
        { label: "-21", value: "-21" },
        { label: "-22", value: "-22" },
        { label: "-23", value: "-23" }
    ];
    activeTabIndex: number = 0;
    activeTabViewIndex: number = 0;
    inventoryDocType: any;
    tabsMandatory: any[];
    searchStaffDeatil: any;
    approveAssignData: any[];
    optionValue: any;
    searchMacData: any;
    macOptionSelect = this.commondropdownService.searchInventoryOption;
    searchOption: any;
    searchMacDeatil: string;
    staffId: string;
    searchForm: UntypedFormGroup;

    //   START HERE
    popList = [];
    oltList = [];
    dnSplitterList = [];
    snSplitterList = [];
    masterDbList = [];
    customerBindDataList: any[] = [];
    itemIdForHierarchy: any[] = [];
    //   macOptionSelect = this.commondropdownService.searchInventoryOption;
    oltVisible = false;
    dnSplitterVisible = false;
    snSplitterVisible = false;
    attachDeviceInventoryModal: boolean = false;
    attachDeviceInventoryAssignForm: UntypedFormGroup;
    //   optionValue: any;
    //   searchOption: any;
    //   searchMacDeatil: string;
    //   searchMacData: any;
    //   searchForm: FormGroup;
    networkBindId: number | null = null;
    attachDeviceButtonLabel = "Save Attach Device";

    data1: TreeNode[];
    custInvenId: any;
    netWorkHierarchyName: any = "";
    isHierarchyDiagramVisible = false;
    ifPersonalPerentDeviceShow = false;
    showDiagram = true;
    IfPersonalNetworkDataShow = true;
    ifServiceAreaListShow = false;

    show = true;
    mappingLoaded = false;
    hierarchyMappingList: any[] = [];
    inForm: UntypedFormGroup;
    selecetedData: any;
    selectedDeviceId: number;
    inFlag: boolean = false;
    submittedIn: boolean = false;
    deviceName: string;
    deviceId: any;
    totalInPorts: UntypedFormArray;
    totalOutPorts: UntypedFormArray;
    basicInPorts: any = [];
    updateParentInPortdata: any[] = [];
    basicOutPorts: any = [];
    updateChildInPortdata: any[] = [];
    availableParentList: any = [];

    availableInPorts: any;
    outFlag: boolean = false;
    availableOutPorts: any;
    isparentChildDeviceModelOpen: boolean = false;
    deviceType: string = "";
    parentChildPortModal: boolean = false;
    isParent: boolean = false;
    parentMappingAccess: boolean = false;
    submittedOut: boolean = false;
    selectedDevice: any = [];
    outForm: UntypedFormGroup;
    deviceDetail: any;
    allNetworkDeviceData: any;
    updateParentOutPortdata: any[] = [];
    updateChildOutPortdata: any[] = [];
    selectedParentOutPortdata: any;
    selectedChildOutPortdata: any;
    currentPagenetworkDeviceListdata = 1;
    searchkey: string;
    networkDeviceListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    networkDeviceListData: any = [];
    networkDeviceListDataselector: any;
    networkDeviceListdatatotalRecords: any;
    childPortData: any[] = [];
    parentPortData: any[] = [];
    selectedParentInPortdata: any;
    selectedChildInPortdata: any;
    @ViewChild('downloadDocumentDailog') downloadDocumentDailog!: TemplateRef<any>;
    @ViewChild('viewDocumentDailog') viewDocumentDailog!: TemplateRef<any>;
    assignInventoryWithPlanDialogRef: MatDialogRef<any, any>;
    dialogRefAssignExternalInventory: MatDialogRef<any, any>;
    constructor(
        private toastr: ToastrService,
        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private route: ActivatedRoute,
        private router: Router,
        private confirmationService: ConfirmationService,
        private customerInventoryManagementService: CustomerInventoryManagementService,
        public commondropdownService: CommondropdownService,
        public serviceAreaService: ServiceAreaService,
        public CustomerInventoryDetailsService: CustomerInventoryDetailsService,
        private customerManagementService: CustomermanagementService,
        private navService: NavMasterService,
        private customerService: CustomerService,
        loginService: LoginService,
        private inwardService: InwardService,
        private systemService: SystemconfigService,
        private sanitizer: DomSanitizer,
        private dialog: MatDialog,
        private toast: ToastrService,
        private cdr: ChangeDetectorRef
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.otherInventoryAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVEN_OTHER
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_OTHER
        );
        this.planInventoryAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVEN_PLAN
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_PLAN
        );
        this.externalInventoryAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVEN_EXTERNAL
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_EXTERNAL
        );
        this.InventoryHistoryAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVEN_HISTORY
                : POST_CUST_CONSTANTS.POST_CUST_CAF_INVEN_HISTORY
        );
        this.swapInventoryAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVEN_SWAP
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_SWAP
        );
        this.removeAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVEN_REMOVE
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_REMOVE
        );
        this.editAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVEN_EDIT
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_EDIT
        );
        this.replaceAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVEN_REPLACE
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_REPLACE
        );
        this.DTVHistoryAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVEN_DTV
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_DTV
        );
        this.uploadDocumentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVENTORY_UPLOAD_DOCUMENT
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_DTV
        );
        this.downloadDocumentsAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVENTORY_DOWNLOAD_DOCUMENTS
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_DTV
        );
        this.downloadDocumentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVENTORY_DOWNLOAD_DOCUMENT
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_DTV
        );
        this.viewDocumentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVENTORY_VIEW_DOCUMENT
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_DTV
        );
        this.deleteDocumentAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_INVENTORY_DELETE_DOCUMENT
                : POST_CUST_CONSTANTS.POST_CUST_INVEN_DTV
        );
    }

    ngOnInit(): void {
        this.staffUserId = localStorage.getItem("userId");
        this.getCustomersDetail(this.customerId);
        this.getService("");
        this.commondropdownService
            .getMethodFromCommon(`/commonList/generic/inventoryDocType`)
            .subscribe((response: any) => {
                this.inventoryDocType = response.dataList;
                this.inventoryDocType.sort((a, b) => this.tabs.indexOf(a.text) - this.tabs.indexOf(b.text));

                // Create mapping: tab name -> mandatory flag
                this.tabs = this.inventoryDocType.map(item => item.text);
                this.tabsMandatory = this.inventoryDocType.map(item => item.hasMandatory);
            });
        // this.getCustomerAssignedList();

        //this.getAllProduct();
        this.replaceInventoryForm = this.fb.group({
            productId: ["", Validators.required],
            customerId: [this.custData.id],
            inventoryType: [""],
            assignedDateTime: ["", Validators.required],
            replacementReason: ["", Validators.required],
            remark: ["", Validators.required],
            isInvoiceToOrg: [false],
            billTo: ["CUSTOMER"],
            discount: [""],
            offerPrice: [""],
            newAmount: [""],
            chargeId: [""],
            isRequiredApproval: [false],
            isFree: [false],
            itemType: [""],
            billabecustId: [""],
            parentCustomerId: [""]
        });
        this.removeRemarkForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.refundAmountForm = this.fb.group({
            actualRefundPrice: ["", Validators.required],
            newRefundAmount: ["", Validators.required]
        });
        this.inventoryAssignForm = this.fb.group({
            id: [""],
            qty: ["1"],
            productId: ["", Validators.required],
            customerId: [this.custData.id],
            serviceId: ["", Validators.required],
            inventoryType: [""],
            staffId: [""],
            inwardId: [""],
            assignedDateTime: ["", Validators.required],
            status: [""],
            paymentOwnerId: ["", Validators.required],
            mvnoId: [""],
            externalItemId: [""],
            itemId: [""],
            itemAssemblyId: [""],
            itemAssemblyName: ["", Validators.required],
            itemAssemblyflag: ["", Validators.required],
            itemTypeFlag: ["", Validators.required],
            nonSerializedQty: [""],
            nonSerializedItemRemark: [
                "",
                [Validators.pattern(Regex.characterlength255), Validators.required]
            ],
            connectionNo: ["", Validators.required],
            isInvoiceToOrg: [false],
            billTo: ["CUSTOMER"],
            discount: [""],
            inventoryJobType: ["", Validators.required],
            nature: ["", Validators.required],
            offerPrice: [""],
            newAmount: [""],
            chargeId: [""],
            isRequiredApproval: [false],
            isFree: [false],
            itemType: ["", Validators.required],
            billabecustId: [""],
            parentCustomerId: [""]
        });
        this.wifiForm = this.fb.group({
            username: ["", Validators.required],
            password: ["", Validators.required],
            workingFrequency: ["", Validators.required]
        });
        this.externalInventoryAssignForm = this.fb.group({
            id: [""],
            qty: ["1"],
            productId: ["", Validators.required],
            customerId: [this.custData.id],
            serviceId: ["", Validators.required],
            inventoryType: [""],
            staffId: [""],
            inwardId: [""],
            assignedDateTime: ["", Validators.required],
            status: [""],
            mvnoId: [""],
            externalItemId: ["", Validators.required],
            itemId: [""],
            connectionNo: ["", Validators.required]
        });

        this.planInventoryAssignForm = this.fb.group({
            productPlanMappingId: [""],
            qty: [""],
            productId: [[], Validators.required],
            customerId: [this.custData.id],
            serviceId: ["", Validators.required],
            inventoryType: [""],
            staffId: [""],
            inwardId: [""],
            assignedDateTime: ["", Validators.required],
            paymentOwnerId: ["", Validators.required],
            status: [""],
            mvnoId: [""],
            externalItemId: [""],
            itemId: [""],
            itemType: [""],
            itemAssemblyId: [""],
            itemAssemblyName: ["", Validators.required],
            itemAssemblyflag: ["", Validators.required],
            // itemAssemblyId: [""],
            // itemAssemblyName: [""],
            // itemAssemblyflag: [""],
            connectionNo: ["", Validators.required],
            planId: ["", Validators.required],
            isInvoiceToOrg: [false],
            billTo: [""],
            discount: [""],
            offerPrice: [""],
            newAmount: [""],
            chargeId: [""],
            planGroupId: [""],
            planGroupName: [""],
            isRequiredApproval: [false],
            isFree: [false],
            billabecustId: [""],
            parentCustomerId: [""]
        });
        this.approveAssignInventoryForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.rejectAssignInventoryForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.approveRemoveInventoryForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.rejectRemoveInventoryForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.ownershipForm = this.fb.group({
            ownership: [""]
        });
        this.attachDeviceInventoryAssignForm = this.fb.group({
            customerid: [this.custData.id],
            popid: ["", Validators.required],
            oltid: [""],
            dnsplitterid: [""],
            snsplitterid: [""],
            masterdbid: [""]
        });
        this.inForm = this.fb.group({
            inBind: ["", Validators.required],
            outBind: ["", Validators.required],
            parentDeviceId: ["", Validators.required]
        });
        this.outForm = this.fb.group({
            inBind: ["", Validators.required],
            outBind: ["", Validators.required],
            parentDeviceId: ["", Validators.required]
        });
        this.inventoryAssignForm.controls.itemAssemblyName.disable();
        this.inventoryAssignForm.controls.itemAssemblyflag.disable();
        // this.planInventoryAssignForm.controls.itemAssemblyName.disable();
        setTimeout(() => {
            this.updateRowGroupMetaData();
        }, 1000);
        this.inventoryAssignForm.get("nonSerializedQty").valueChanges.subscribe(val => {
            const total = val;
            if (total > this.availableQty) {
                this.showQtyError = true;
            } else {
                this.showQtyError = false;
            }
            if (total < 0 || total == 0) {
                this.negativeAssignQtyError = true;
            } else {
                this.negativeAssignQtyError = false;
            }
        });
        this.replaceAssignForm = this.fb.group({
            remark: ["", Validators.required]
        });

        this.inventoryAssignForm.get("newAmount").valueChanges.subscribe(val => {
            const newPriceValue = val;
            this.showError = false;
            this.priceErrorMsg = "";
            if (newPriceValue > this.oldOfferOtherSigle) {
                this.showError = true;
                this.priceErrorMsg =
                    "Please enter a new offer price less than or equal to the old offerprice.";
            }
            if (newPriceValue > this.perUOMCharge) {
                this.showError = true;
                this.priceErrorMsg =
                    "Please enter a new uom price less than or equal to the per uom price.";
            }
            if (newPriceValue > this.oldOfferSTB) {
                this.showError = true;
                this.priceErrorMsg =
                    "Please enter a new offer price less than or equal to the old offerprice.";
            }
        });
        this.planInventoryAssignForm.get("newAmount").valueChanges.subscribe(val => {
            const newPriceValue = val;
            this.showError = false;
            this.priceErrorMsg = "";
            if (Number(newPriceValue) > Number(this.oldOfferPricePlan)) {
                this.showError = true;
                this.priceErrorMsg =
                    "Please enter a new offer price less than or equal to the old offerprice.";
            }
        });
        // this.uploadDocForm = this.fb.group({
        //     file: ["", Validators.required]
        // });

        this.systemService.getConfigurationByName("NMS_ENABLE").subscribe((res: any) => {
            if (res.data) {
                this.nms_enable = res.data.value;
            }
        });

        this.systemService.getConfigurationByName("FiberHome").subscribe((res: any) => {
            if (res.data) {
                this.fiber_home_enable = res.data.FiberHome ? true : false;
            }
        });
        this.tabs.forEach(() => {
            this.uploadDocForm.push(this.createForm());
        });

        this.searchForm = this.fb.group({
            searchOption: [""],
            searchMacDeatil: [""]
        });
        this.searchMacData = {
            filterBy: "",
            paginationRequestDTO: {
                page: "",
                pageSize: "",
                filters: [
                    {
                        filterDataType: "",
                        filterValue: "",
                        filterColumn: "any",
                        filterOperator: "equalto",
                        filterCondition: "and"
                    }
                ]
            },
            productId: "",
            ownerId: "",
            ownerType: ""
        };
        this.replaceInventoryColumns = [
            'select',
            'itemId',
            'condition',
            'macAddress',
            'serialNumber',
            'action'
        ];
    }
    getInventoryJobTypeList() {
        const url = "/commonList/inventoryJobType";
        this.commondropdownService.getForInventoryAndNature(url).subscribe(
            (res: any) => {
                if (res && res.dataList) {
                    this.inventoryJobTypeList = res.dataList;
                    const defaultOption = this.inventoryJobTypeList.find(
                        (item: any) => item.text === "New Installation"
                    );
                    if (defaultOption) {
                        this.inventoryAssignForm.get("inventoryJobType")?.setValue(defaultOption.value);
                    }
                } else {
                    this.inventoryJobTypeList = [];
                }
            },
            (error: any) => {
                this.inventoryJobTypeList = [];
            }
        );
    }

    getNatureList() {
        const url = "/commonList/nature";
        this.commondropdownService.getForInventoryAndNature(url).subscribe(
            (res: any) => {
                if (res && res.dataList) {
                    this.natureList = res.dataList;
                    const defaultOption = this.natureList.find(
                        (item: any) => item.text === "Sales Conversion"
                    );
                    if (defaultOption) {
                        this.inventoryAssignForm.get("nature")?.setValue(defaultOption.value);
                    }
                } else {
                    this.natureList = [];
                }
            },
            (error: any) => {
                console.error('Error fetching nature list', error);
                this.natureList = [];
            }
        );
    }

    // ngAfterViewInit() {
    //     this.getAllInventoryofCust.paginator = this.dt1;
    // }

    selMacSearchOption(event) {
        this.searchForm.value.searchMacDeatil = "";
        this.optionValue = event;
    }
    searchMac() {
        const staffId = localStorage.getItem("userId");
        this.searchMacData.paginationRequestDTO.filters[0].filterValue =
            this.searchForm.value.searchMacDeatil;
        this.searchMacData.paginationRequestDTO.filters[0].filterColumn =
            this.searchForm.value.searchOption;
        this.searchMacData.productId = this.productMacAddressId;
        this.searchMacData.ownerId = Number(staffId);
        this.searchMacData.ownerType = "staff";
        this.searchMacData.paginationRequestDTO.page = this.currentMacAddressListdata;
        this.searchMacData.paginationRequestDTO.pageSize = this.macAddressListdataitemsPerPage;
        const url = "/outwards/searchItemHistoryByProduct";
        this.inwardService.postMethod(url, this.searchMacData).subscribe(
            (response: any) => {
                if (response.dataList.length > 0) {
                    this.macAddressList = response.dataList;
                    this.macAddressListtotalRecords = response.totalRecords;
                } else {
                    this.macAddressList = [];
                    this.macAddressListtotalRecords = 0;
                    this.toastr.info(`No Search Record Found`, 'Info!');
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    clearMac() {
        this.searchForm.reset();
        this.newFirstMacAddress = 0;
        this.currentMacAddressListdata = 1;
        this.macAddressListdataitemsPerPage = 20;
        let obj = {
            value: this.productMacAddressId
        };
        this.getMacAddressList(obj);
    }

    createForm(): UntypedFormGroup {
        return this.fb.group({
            sectionName: [""],
            latitude: [""],
            longitude: [""],
            opticalRange: [null],
            file: [null, Validators.required]
        });
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custData = response.customers;
            this.getStaffDetailById(this.custData.serviceareaid);
        });
    }
    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }
    canSubmit(): boolean {
        return this.selectedCardOption && this.selectedStbOption;
    }
    // Update Row Group Meta Data
    updateRowGroupMetaData() {
        this.rowGroupMetadata = {};

        if (this.assignedInventoryList) {
            for (let i = 0; i < this.assignedInventoryList.length; i++) {
                let rowData = this.assignedInventoryList[i];
                let representativeName = rowData.custInventoryListId ? rowData.custInventoryListId : null;

                if (i == 0) {
                    this.rowGroupMetadata[representativeName] = { index: 0, size: 1 };
                } else {
                    let previousRowData = this.assignedInventoryList[i - 1];
                    let previousRowGroup = previousRowData.custInventoryListId
                        ? previousRowData.custInventoryListId
                        : null;
                    if (representativeName === previousRowGroup) {
                        this.rowGroupMetadata[representativeName].size++;
                    } else {
                        this.rowGroupMetadata[representativeName] = { index: i, size: 1 };
                    }
                }
            }
        }
    }

    // customer assigned inventory list
    getCustomerAssignedList(): void {
        this.getCustomerNetworkDeviceBindData();
        // const data = {
        //   filters: [
        //     {
        //       filterValue: this.custData.id,
        //       filterColumn: "customerId",
        //     },
        //   ],
        //   page: this.customerInventoryListDataCurrentPage,
        //   pageSize: this.customerInventoryListItemsPerPage,
        //   sortBy: "createdate",
        //   sortOrder: 0,
        // };

        // const url = "/inwards/getByCustomerId";
        const url = "/inwards/getAllCustomerInventoryList?custId=" + this.customerId;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.assignedInventoryList = res.dataList;
                this.customerInventoryListDataTotalRecords = res.totalRecords;
                //this.customerInventoryListDataTotalRecords = res.totalRecords;
                // setTimeout(() => {
                this.updateRowGroupMetaData();
                // }, 1000);
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    pageChangedEventCustomerAssignInventory(pageNumber): void {
        this.customerInventoryListDataCurrentPage = pageNumber;
        this.getCustomerAssignedList();
    }

    totalItemsEventCustomerAssignInventory(event): void {
        this.customerInventoryListDataCurrentPage = 1;
        this.customerInventoryListItemsPerPage = Number(event.value);
        this.getCustomerAssignedList();
    }

    openInventoryDetailModal(modalId, data) {
        this.CustomerInventoryDetailsService.show(data);
        this.inventoryData.next({
            inventoryData: data
        });
    }

    replaceInventoryModalOpen(inventory): void {
        if (inventory.itemAssemblyId == undefined) {
            this.replaceInventoryCustId = inventory.customerId;
            let id = inventory.inOutWardMACMapping[0].id;
            this.replaceInventoryModal = true;
            this.replaceInventoryDialogRef = this.dialog.open(this.replaceInventoryDialog, {
                width: '75%',
                disableClose: true,
            });
            this.replaceInventoryForm.reset();
            if (inventory.planId != null) {
                this.replaceOldMappingId = id;
                this.itemConditionSingleReplaceFlag = false;
                this.getAllPlanIvnetoryIdAtReplace(inventory.planId);
            }
            if (inventory.planId == null) {
                this.replaceOldMappingId = id;
                this.itemConditionSingleReplaceFlag = true;
                this.getProductsToReplace(id);
            }
        } else {
            this.getAllPairItemMacReplaceFlag = false;
            let id = inventory.inOutWardMACMapping[0].id;
            this.getProductsByAssemblyId(inventory.itemAssemblyId);
            $("#replaceAssemblyInventoryModal").modal("show");
            // this.replaceInventoryForm.reset();
            // this.replaceOldMappingId = id;
            //this.getProductsToReplace(id);
        }
        this.selOtherItemId = inventory.inOutWardMACMapping[0].itemId;
    }
    // STB and Card Replace Individually
    replaceInventorySTBCARDModalOpen(inventory): void {
        if (inventory.itemAssemblyId != undefined) {
            this.replaceInventoryCustId = inventory.customerId;
            let id = inventory.inOutWardMACMapping[0].id;
            // this.replaceInventoryModal = true;
            this.replaceInventoryDialogRef = this.dialog.open(this.replaceInventoryDialog, {
                width: '75%',
                disableClose: true,
            });
            this.replaceInventoryForm.reset();
            if (inventory.planId != null) {
                this.replaceOldMappingId = id;
                this.itemConditionSingleReplaceFlag = false;
                this.getAllPlanIvnetoryIdAtReplace(inventory.planId);
            }
            if (inventory.planId == null) {
                this.replaceOldMappingId = id;
                this.itemConditionSingleReplaceFlag = true;
                this.getProductsToReplace(id);
            }
        }
        this.selOtherItemId = inventory.inOutWardMACMapping[0].itemId;
    }

    replaceInventoryModalClose(): void {
        this.filterGlobalReplaceSingle = "";
        this.otherInventoryReplaceFlag = false;
        this.planInventoryReplaceFlag = false;
        this.macReplaceListFlag = false;
        this.replaceSumitted = false;
        this.selectedReplaceMACAddress = "";
        this.replaceInventoryForm.reset();
        this.macAddressList = [];
        this.billToSigleFlag = false;
        this.billToPairFlag = false;
        this.discountPairFlag = false;
        this.itemConditionPairFlag = false;
        this.isInvoiceDataSingleFlag = false;
        this.getPlanSingleSplitterFlag = false;
        this.getPlanPairSplitterFlag = false;
        this.getAllPairPlanProductSTBFlag = false;
        this.getAssemblyNameflag = false;
        this.getAllPairProductCardFlag = false;
        this.itemConditionSingleFlag = false;
        this.itemConditionPlanSeriFlag = false;
        this.itemConditionPlanPairFlag = false;
        this.selAssemblyTypePlanFlag = false;
        this.selAssemblyTypePlanGroupFlag = false;
        this.oldOfferPriceSingleFlag = false;
        this.newOfferSingleFlag = false;
        this.newOfferSTBFlag = false;
        this.newOfferCardFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountCardPairFlag = false;
        this.oldOfferBasedDiscountNonSerialFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferBasedDiscountSTBPairFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountSingleReplaceFlag = false;
        this.oldOfferPriceCardFlag = false;
        this.oldOfferPriceNonSerialFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPriceSTBFlag = false;
        this.oldOfferPriceSingleReplaceFlag = false;
        this.newOfferNonSerialFlag = false;
        this.replaceInventoryModal = false;
    }

    replaceAssemblyInventoryModalClose(): void {
        this.replaceSumitted = false;
        this.selectedReplaceMACAddress = "";
        this.selectedReplaceMACAddress2 = "";
        this.cardFileterGlobalReplace = "";
        this.stbFileterGlobalReplace = "";
        this.replaceInventoryForm.reset();
        this.itemConditionPairFlag = false;
        this.macAddressList1 = [];
        this.macAddressList2 = [];
        this.billToSigleFlag = false;
        this.billToPairFlag = false;
        this.discountPairFlag = false;
        this.isInvoiceDataSingleFlag = false;
        this.itemConditionSingleFlag = false;
        this.itemConditionPlanSeriFlag = false;
        this.itemConditionPlanPairFlag = false;
        this.getAssemblyNameflag = false;
        this.selAssemblyTypePlanFlag = false;
        this.selAssemblyTypePlanGroupFlag = false;
        this.oldOfferPriceSingleFlag = false;
        this.newOfferSingleFlag = false;
        this.newOfferSTBFlag = false;
        this.newOfferCardFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountCardPairFlag = false;
        this.oldOfferBasedDiscountNonSerialFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferBasedDiscountSTBPairFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountSingleReplaceFlag = false;
        this.oldOfferPriceCardFlag = false;
        this.getAllPairItemMacReplaceFlag = false;
        this.oldOfferPriceNonSerialFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPriceSTBFlag = false;
        this.oldOfferPriceSingleReplaceFlag = false;
        this.newOfferNonSerialFlag = false;
        $("#replaceAssemblyInventoryModal").modal("hide");
    }

    getProductsToReplace(id) {
        const url = `/product/getAllProductsByMacSerial?macMappingId=${id}`;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.replaceProducts = response.dataList;
                this.otherInventoryReplaceFlag = true;
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    stbProductsOld: any = [];
    cardProductsOld: any = [];
    stbProductsToReplace: any = [];
    cardProductsToReplace: any = [];

    getAssemlyProductsToReplace(data) {
        const url = `/product/getAllProductsByMacSerial?macMappingId=${data.inOutWardMACMapping[0].id}`;

        this.getAllPairItemMacReplaceFlag = false;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (data.dtvCategory == "STB") {
                    this.stbProductsOld = data;
                    this.stbProductsToReplace = response.dataList;
                } else if (data.dtvCategory == "Card") {
                    this.cardProductsOld = data;
                    this.cardProductsToReplace = response.dataList;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    getProductsByAssemblyId(id) {
        const url = `/inoutWardMacMapping/getAllAssemblyInventory?assemblyId=${id}`;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                response.dataList.forEach(data => {
                    this.getAssemlyProductsToReplace(data);
                });
                // this.pairedProductsToReplace=response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }
    // Get Mac Address List for Single Item
    getMacAddressList(event) {
        this.macAddressList = [];
        if (event.value !== this.productMacAddressId) {
            this.newFirstMacAddress = 0;
            this.currentMacAddressListdata = 1;
        }

        const staffId = localStorage.getItem("userId");
        this.productMacAddressId = event.value;
        let product = this.allActiveProducts.find(element => element.id == this.productMacAddressId);
        this.hasMac = product.productCategory.hasMac;
        this.hasSerial = product.productCategory.hasSerial;

        const requestData = {
            productId: this.productMacAddressId,
            ownerId: staffId,
            ownerType: "staff",
            paginationRequestDTO: {
                page: this.currentMacAddressListdata,
                pageSize: this.macAddressListdataitemsPerPage
            }
        };

        const url = "/outwards/getItemHistoryByProduct";

        this.customerInventoryManagementService.postMethod(url, requestData).subscribe(
            (res: any) => {
                this.getAllPairItemMacFlag = false;
                this.macAddressList = res.dataList.filter(
                    element => element.condition == this.selItemCondition
                );
                this.macAddressListtotalRecords = res.totalRecords;
                if (this.macAddressList.length == 0 || this.macAddressList == null) {
                    this.toastr.info(`Assignee does not have a product`, 'Info!');

                    this.macAddressListtotalRecords = 0;
                    this.newFirstMacAddress = 0;
                    this.billToSigleFlag = false;
                    this.billToPairFlag = false;
                    this.discountPairFlag = false;
                    this.isInvoiceDataSingleFlag = false;
                    this.oldOfferPriceSingleFlag = false;
                    this.oldOfferBasedDiscountSingleFlag = false;
                } else {
                    this.getAllSingleItemMacFlag = true;
                    this.billToSigleFlag = true;
                    this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
                    this.isInvoiceDataSingleFlag = false;
                    this.oldOfferPriceSingleFlag = false;
                    this.oldOfferBasedDiscountSingleFlag = true;
                    this.getProductDetails(product);
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }
    paginateMacAddressData(event) {
        this.newFirstMacAddress = event.first;
        this.macAddressListdataitemsPerPage = event.rows;
        this.currentMacAddressListdata = event.page + 1;
        let obj = {
            value: this.productMacAddressId
        };
        this.getMacAddressList(obj);
    }
    // Single Item Plan Level Macc Serial Address List
    getPlanLevelMacAddressList(event) {
        this.macAddressList = [];

        const staffId = localStorage.getItem("userId");
        const productId = event.value;
        let product = this.productByPlanList.find(element => element.id == productId);
        this.hasMac = product.productCategory.hasMac;
        this.hasSerial = product.productCategory.hasSerial;
        let planId = this.selPlanId;
        const url =
            "/outwards/getItemBasedOnProductType?ownerType=Staff" +
            "&ownerid=" +
            staffId +
            "&planId=" +
            planId +
            "&productId=" +
            productId +
            "&planGroupId=" +
            this.planGroupId +
            "&productCategoryId=" +
            this.productCategoryId;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                // this.macAddressList = res.dataList;
                this.macAddressList = res.dataList.filter(
                    element => element.condition == this.selPlanItemCondition
                );

                if (this.macAddressList.length == 0 || this.macAddressList == null) {
                    this.macPlanListFlag = false;
                    this.billToPlanFlag = false;
                    this.oldOfferBasedDiscountPlanFlag = false;
                    this.oldOfferPricePlanFlag = false;
                    this.toastr.info(`Assignee does not have a product`, 'Info!');

                } else {
                    this.macPlanListFlag = true;
                    this.billToPlanFlag = true;
                    this.oldOfferBasedDiscountPlanFlag = false;
                    this.oldOfferPricePlanFlag = false;
                    if (this.billToPlanName == "ORGANIZATION") {
                        this.isInvoiceDataFlag = true;
                        this.oldOfferPricePlanFlag = true;
                        this.oldOfferBasedDiscountPlanFlag = false;
                    } else {
                        this.isInvoiceDataFlag = false;
                        this.oldOfferPricePlanFlag = false;
                        this.oldOfferBasedDiscountPlanFlag = true;
                    }
                    this.getMappingDetails(
                        this.planGroupId,
                        planId,
                        this.productCategoryId,
                        productId,
                        this.billToPlanName
                    );
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }
    // Pair Item STB Plan Level Mac Address List
    getPlanLevelSTBMacAddressList(event) {
        this.macAddressList = [];

        const staffId = localStorage.getItem("userId");
        const productId = event.value;
        let product = this.productByPlanList.find(element => element.id == productId);
        this.hasMac = product.productCategory.hasMac;
        this.hasSerial = product.productCategory.hasSerial;
        let planId = this.selPlanId;
        const url =
            "/outwards/getItemBasedOnProductType?ownerType=Staff" +
            "&ownerid=" +
            staffId +
            "&planId=" +
            planId +
            "&productId=" +
            productId +
            "&planGroupId=" +
            this.planGroupId +
            "&productCategoryId=" +
            this.productCategoryId;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                // this.macAddressList = res.dataList;
                this.macAddressListSTB = res.dataList.filter(
                    element => element.condition == this.selPlanItemCondition
                );

                if (this.macAddressListSTB.length == 0 || this.macAddressListSTB == null) {
                    this.macPlanListFlag = false;
                    this.billToPlanFlag = false;
                    this.oldOfferBasedDiscountPlanFlag = false;
                    this.oldOfferPricePlanFlag = false;
                    this.toastr.info(`Assignee does not have a product`, 'Info!');

                } else {
                    this.macPlanListFlag = true;
                    this.billToPlanFlag = true;
                    this.oldOfferBasedDiscountPlanFlag = false;
                    this.oldOfferPricePlanFlag = false;
                    if (this.billToPlanName == "ORGANIZATION") {
                        this.isInvoiceDataFlag = true;
                        this.oldOfferPricePlanFlag = true;
                        this.oldOfferBasedDiscountPlanFlag = false;
                    } else {
                        this.isInvoiceDataFlag = false;
                        this.oldOfferPricePlanFlag = false;
                        this.oldOfferBasedDiscountPlanFlag = true;
                    }
                    this.getMappingDetails(
                        this.planGroupId,
                        planId,
                        this.productCategoryId,
                        productId,
                        this.billToPlanName
                    );
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    // Pair Item Card Plan Level Mac Address List
    getPlanLevelCardMacAddressList(event) {
        if (event.value !== this.productMacAddressCardId) {
            this.newFirstMacAddressCard = 0;
            this.currentMacAddressCardListdata = 1;
        }
        const staffId = localStorage.getItem("userId");
        let macAddressListCardcurrentPage;
        macAddressListCardcurrentPage = this.currentMacAddressCardListdata;

        this.productMacAddressCardId = event.value;
        let product = this.allActiveProducts.find(
            element => element.id == this.productMacAddressCardId
        );
        const requestData = {
            productId: this.productMacAddressCardId,
            ownerId: staffId,
            ownerType: "staff",
            paginationRequestDTO: {
                page: macAddressListCardcurrentPage,
                pageSize: this.macAddressListdataitemsPerPage
            }
        };

        const url = "/outwards/getItemHistoryByProduct";

        this.customerInventoryManagementService.postMethod(url, requestData).subscribe(
            (res: any) => {
                this.macAddressListCard = res.dataList.filter(
                    element => element.condition == this.selPlanItemCondition
                );
                this.macAddressListCardtotalRecords = res.totalRecords;
                // this.macAddressList2 = res.dataList;
                if (this.macAddressListCard.length == 0 || this.macAddressListCard == null) {
                    this.toastr.info(`Assignee does not have a product`, 'Info!');

                    this.newFirstMacAddressCard = 0;
                    this.macAddressListCardtotalRecords = 0;
                    this.oldOfferBasedDiscountSTBPairFlag = false;
                    this.oldOfferBasedDiscountCardPairFlag = false;
                } else {
                    this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
                    this.isInvoiceDataPairFlag = false;
                    this.oldOfferPriceSTBFlag = false;
                    this.oldOfferPriceCardFlag = false;
                    this.oldOfferBasedDiscountCardPairFlag = true;
                    // this.oldOfferBasedDiscountSTBPairFlag = false;
                    this.getCardProductDetails(product);
                    // this.billToPairFlag = true;
                    this.getAllSingleItemMacFlag = false;
                    this.getAllPairItemMacFlag = true;
                    this.getAllPairItemMacReplaceFlag = false;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }
    paginateMacAddressCardData(event) {
        this.newFirstMacAddressCard = event.first;
        this.macAddressListCarditemsPerPage = event.rows;
        this.currentMacAddressCardListdata = event.page + 1;
        let obj = {
            value: this.productMacAddressCardId
        };
        this.getPlanLevelCardMacAddressList(obj);
    }

    selReplacementType(event) {
        this.selectedReplacementType = event.value;
    }

    getReplaceLevelMacAddressList(event) {
        this.macAddressList = [];

        if (event.value !== this.productMacAddressId) {
            this.newFirstMacAddress = 0;
            this.currentMacAddressListdata = 1;
        }

        const staffId = localStorage.getItem("userId");
        this.productMacAddressId = event.value;

        let product = this.replaceProducts.find(element => element.id == this.productMacAddressId);
        this.hasMac = product.productCategory.hasMac;
        this.hasSerial = product.productCategory.hasSerial;

        const requestBody = {
            page: this.currentMacAddressListdata,
            pageSize: this.macAddressListdataitemsPerPage
        };

        // Construct full URL with required query parameters
        const url = `/outwards/getItemBasedOnCondtion?productId=${this.productMacAddressId}&itemId=${this.selOtherItemId}&ownerId=${staffId}&ownerShipType=Staff&replacementReason=${encodeURIComponent(this.selectedReplacementType)}`;

        this.customerInventoryManagementService.postMethod(url, requestBody).subscribe(
            (res: any) => {
                this.macAddressList = res.dataList || [];
                this.macAddressListtotalRecords = res.totalRecords || 0;

                if (!this.macAddressList || this.macAddressList.length === 0) {
                    // this.toastr.info( ||`Assignee does not have a product`, 'Info!');
                    this.toastr.info(res.responseMessage || `Assignee does not have a product`, 'Info!')

                    this.macReplaceListFlag = false;
                } else {
                    this.macReplaceListFlag = true;
                    // Add additional actions here like this.getProductDetailsReplace(product) if needed
                }
            },
            (error: any) => {
                this.toastr.info(error?.error?.msg || "Something went wrong", 'Info!');

            }
        );
    }

    getReplacePlanLevelMacAddressList(event) {
        this.macAddressList = [];

        const staffId = localStorage.getItem("userId");
        const productId = event.value;
        let product = this.productByPlanListReplace.find(element => element.id == productId);
        this.hasMac = product.productCategory.hasMac;
        this.hasSerial = product.productCategory.hasSerial;
        let planId = this.selReplacePlanId;
        // const url =
        //   "/outwards/getItemBasedOnProductType?ownerType=Staff" +
        //   "&ownerid=" +
        //   staffId +
        //   "&planId=" +
        //   planId +
        //   "&productId=" +
        //   productId;
        const url =
            "/outwards/getItemBasedOnProductType?ownerType=Staff" +
            "&ownerid=" +
            staffId +
            "&planId=" +
            planId +
            "&productId=" +
            productId +
            "&planGroupId=" +
            this.planGroupId +
            "&productCategoryId=" +
            this.productCategoryId;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.macAddressList = res.dataList;

                if (this.macAddressList.length == 0 || this.macAddressList == null) {
                    this.toastr.info(`Assignee does not have a product`, 'Info!');

                } else {
                    this.macReplaceListFlag = true;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    getMultipleMacAddressList(event) {
        const staffId = localStorage.getItem("userId");
        const productId = event.value;
        const url = "/product/getAllItemBasedOnProduct";
        this.customerInventoryManagementService.postMethod(url, productId).subscribe(
            (res: any) => {
                this.macAddressList = res;

                if (this.macAddressList.length == 0 || this.macAddressList == null) {
                    this.toastr.info(`Assignee does not have a product`, 'Info!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }
    //Get All Mac Address List for Pair STB At Inventory Assign
    getMacAddressList1(event) {
        if (event.value !== this.productMacAddress1Id) {
            this.newFirstMacAddress1 = 0;
            this.currentMacAddress1Listdata = 1;
        }
        const staffId = localStorage.getItem("userId");
        this.productMacAddress1Id = event.value;
        let macAddress1ListcurrentPage;
        macAddress1ListcurrentPage = this.currentMacAddress1Listdata;
        let product = this.allActiveProducts.find(element => element.id == this.productMacAddress1Id);
        const requestData = {
            productId: this.productMacAddress1Id,
            ownerId: staffId,
            ownerType: "staff",
            paginationRequestDTO: {
                page: macAddress1ListcurrentPage,
                pageSize: this.macAddress1ListdataitemsPerPage
            }
        };
        const url = "/outwards/getItemHistoryByProduct";
        this.customerInventoryManagementService.postMethod(url, requestData).subscribe(
            (res: any) => {
                this.getAllSingleItemMacFlag = false;
                this.getAllPairItemMacFlag = true;
                this.getAllPairItemMacReplaceFlag = false;
                this.macAddressList1 = res.dataList.filter(
                    element => element.condition == this.selItemCondition
                );
                this.macAddress1ListtotalRecords = res.totalRecords;
                // this.macAddressList1 = res.dataList;
                if (this.macAddressList1.length == 0 || this.macAddressList1 == null) {
                    this.toastr.info(`Assignee does not have a product`, 'Info!');

                    this.newFirstMacAddress1 = 0;
                    this.macAddress1ListtotalRecords = 0;
                    this.oldOfferBasedDiscountSTBPairFlag = false;
                    this.oldOfferBasedDiscountCardPairFlag = false;
                } else {
                    this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
                    this.isInvoiceDataPairFlag = false;
                    this.oldOfferPriceSTBFlag = false;
                    this.oldOfferPriceCardFlag = false;
                    // this.oldOfferBasedDiscountCardPairFlag = false;
                    this.oldOfferBasedDiscountSTBPairFlag = true;
                    this.getSTBProductDetails(product);
                    this.billToPairFlag = true;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }
    paginateMacAddress1Data(event) {
        this.newFirstMacAddress1 = event.first;
        this.macAddress1ListdataitemsPerPage = event.rows;
        this.currentMacAddress1Listdata = event.page + 1;
        let obj = {
            value: this.productMacAddress1Id
        };
        this.getMacAddressList1(obj);
    }

    //Get All Mac Address List for Pair STB At Inventory Replace
    getMacAddressList1Replace(event) {
        if (event.value !== this.productMacAddrRep1Id) {
            this.newFirstMacAddrRep1 = 0;
            this.currentMacAddrRep1Listdata = 1;
        }
        const staffId = localStorage.getItem("userId");
        this.productMacAddrRep1Id = event.value;
        let macAddrRepList1currentPage;
        macAddrRepList1currentPage = this.currentMacAddrRep1Listdata;
        let product = this.allActiveProducts.find(element => element.id == this.productMacAddrRep1Id);
        const requestData = {
            productId: this.productMacAddrRep1Id,
            ownerId: staffId,
            ownerType: "staff",
            paginationRequestDTO: {
                page: macAddrRepList1currentPage,
                pageSize: this.macAddrRep1ListdataitemsPerPage
            }
        };
        const url = "/outwards/getItemHistoryByProduct";
        this.customerInventoryManagementService.postMethod(url, requestData).subscribe(
            (res: any) => {
                this.getAllSingleItemMacFlag = false;
                this.getAllPairItemMacFlag = false;
                this.getAllPairItemMacReplaceFlag = true;
                // this.macAddressList1 = res.dataList.filter(
                //   element => element.condition == this.selItemCondition
                // );
                this.macAddressList1 = res.dataList;
                this.macAddrRep1ListtotalRecords = res.totalRecords;
                if (this.macAddressList1.length == 0 || this.macAddressList1 == null) {
                    this.toastr.info(`Assignee does not have a product`, 'Info!');

                    this.newFirstMacAddrRep1 = 0;
                    this.macAddrRep1ListtotalRecords = 0;
                    this.oldOfferBasedDiscountSTBPairFlag = false;
                    this.oldOfferBasedDiscountCardPairFlag = false;
                } else {
                    this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
                    this.isInvoiceDataPairFlag = false;
                    this.oldOfferPriceSTBFlag = false;
                    this.oldOfferPriceCardFlag = false;
                    // this.oldOfferBasedDiscountCardPairFlag = false;
                    this.oldOfferBasedDiscountSTBPairFlag = true;
                    this.getSTBProductDetails(product);
                    this.billToPairFlag = true;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }
    paginateMacAddrRep1data(event) {
        this.newFirstMacAddrRep1 = event.first;
        this.macAddrRep1ListdataitemsPerPage = event.rows;
        this.currentMacAddrRep1Listdata = event.page + 1;
        let obj = {
            value: this.productMacAddrRep1Id
        };
        this.getMacAddressList1Replace(obj);
    }
    // Get All Mac Address List for Pair Card Inventory Assign
    getMacAddressList2(event) {
        if (event.value !== this.productMacAddress2Id) {
            this.newFirstMacAddress2 = 0;
            this.currentMacAddress2Listdata = 1;
        }
        const staffId = localStorage.getItem("userId");
        this.productMacAddress2Id = event.value;
        let product = this.allActiveProducts.find(element => element.id == this.productMacAddress2Id);
        let currentMacAddress2Listdata;
        currentMacAddress2Listdata = this.currentMacAddress2Listdata;

        const requestData = {
            productId: this.productMacAddress2Id,
            ownerId: staffId,
            ownerType: "staff",
            paginationRequestDTO: {
                page: currentMacAddress2Listdata,
                pageSize: this.macAddress2ListdataitemsPerPage
            }
        };

        const url = "/outwards/getItemHistoryByProduct";
        this.customerInventoryManagementService.postMethod(url, requestData).subscribe(
            (res: any) => {
                this.macAddressList2 = res.dataList.filter(
                    element => element.condition == this.selItemCondition
                );
                this.macAddress2ListtotalRecords = res.totalRecords;
                if (this.macAddressList2.length == 0 || this.macAddressList2 == null) {
                    this.toastr.info(`Assignee does not have a product`, 'Info!');

                    this.newFirstMacAddress2 = 0;
                    this.macAddress2ListtotalRecords = 0;
                    this.oldOfferBasedDiscountSTBPairFlag = false;
                    this.oldOfferBasedDiscountCardPairFlag = false;
                } else {
                    this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
                    this.isInvoiceDataPairFlag = false;
                    this.oldOfferPriceSTBFlag = false;
                    this.oldOfferPriceCardFlag = false;
                    this.oldOfferBasedDiscountCardPairFlag = true;
                    // this.oldOfferBasedDiscountSTBPairFlag = false;
                    this.getCardProductDetails(product);
                    // this.billToPairFlag = true;
                    this.getAllSingleItemMacFlag = false;
                    this.getAllPairItemMacFlag = true;
                    this.getAllPairItemMacReplaceFlag = false;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }
    paginateMacAddress2Data(event) {
        this.newFirstMacAddress2 = event.first;
        this.macAddress2ListdataitemsPerPage = event.rows;
        this.currentMacAddress2Listdata = event.page + 1;
        let obj = {
            value: this.productMacAddress2Id
        };
        this.getMacAddressList2(obj);
    }
    // Get All Mac Address List for Pair Card Inventory Replace
    getMacAddressList2Replace(event) {
        if (event.value !== this.productMacAddrRep2Id) {
            this.newFirstMacAddrRep2 = 0;
            this.currentMacAddrRep2Listdata = 1;
        }
        const staffId = localStorage.getItem("userId");
        this.productMacAddrRep2Id = event.value;
        let product = this.allActiveProducts.find(element => element.id == this.productMacAddrRep2Id);
        let currentMacAddrRep2Listdata;
        currentMacAddrRep2Listdata = this.currentMacAddrRep2Listdata;
        const requestData = {
            productId: this.productMacAddrRep2Id,
            ownerId: staffId,
            ownerType: "staff",
            paginationRequestDTO: {
                page: currentMacAddrRep2Listdata,
                pageSize: this.macAddrRep2ListdataitemsPerPage
            }
        };

        const url = "/outwards/getItemHistoryByProduct";
        this.customerInventoryManagementService.postMethod(url, requestData).subscribe(
            (res: any) => {
                // this.macAddressList2 = res.dataList.filter(
                //   element => element.condition == this.selItemCondition
                // );
                this.macAddressList2 = res.dataList;
                this.macAddrRep2ListtotalRecords = res.totalRecords;
                if (this.macAddressList2.length == 0 || this.macAddressList2 == null) {
                    this.toastr.info(`Assignee does not have a product`, 'Info!');

                    this.oldOfferBasedDiscountSTBPairFlag = false;
                    this.oldOfferBasedDiscountCardPairFlag = false;
                    this.newFirstMacAddrRep2 = 0;
                    this.macAddrRep2ListtotalRecords = 0;
                } else {
                    this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
                    this.isInvoiceDataPairFlag = false;
                    this.oldOfferPriceSTBFlag = false;
                    this.oldOfferPriceCardFlag = false;
                    this.oldOfferBasedDiscountCardPairFlag = true;
                    // this.oldOfferBasedDiscountSTBPairFlag = false;
                    this.getCardProductDetails(product);
                    // this.billToPairFlag = true;
                    this.getAllSingleItemMacFlag = false;
                    this.getAllPairItemMacFlag = false;
                    this.getAllPairItemMacReplaceFlag = true;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }
    paginateMacAddrRep2Data(event) {
        this.newFirstMacAddrRep2 = event.first;
        this.macAddrRep2ListdataitemsPerPage = event.rows;
        this.currentMacAddrRep2Listdata = event.page + 1;
        let obj = {
            value: this.productMacAddrRep2Id
        };
        this.getMacAddressList2Replace(obj);
    }
    replaceInventorySubmit(): void {
        this.replaceSumitted = true;
        var reason: any = this.replaceInventoryForm.value.replacementReason;
        const remark: any = this.replaceInventoryForm.value.remark;
        //console.log("selectedReplaceMACAddress", this.selectedReplaceMACAddress);
        var payload = [
            {
                oldMacMappingId: this.replaceOldMappingId,
                newMacMappingId: this.selectedReplaceMACAddress.id
            }
        ];

        if (this.replaceInventoryForm.valid) {
            if (this.selectedReplaceMACAddress != "") {
                // const url = `/inwards/replaceInventory?oldMacMappingId=${this.replaceOldMappingId}&newMacMappingId=${this.selectedReplaceMACAddress.id}&customerId=${this.custData.id}&inventoryType=${this.replaceInventoryForm.value.inventoryType}&replacementReason=${reason}&approvalRemark=${remark}`;
                const url = `/inwards/replaceInventory?customerId=${this.replaceInventoryCustId}&inventoryType=${this.replaceInventoryForm.value.inventoryType}&replacementReason=${reason}&approvalRemark=${remark}`;
                this.customerInventoryManagementService.postMethod(url, payload).subscribe(
                    (res: any) => {
                        if (res.responseCode == 200) {
                            this.replaceInventoryModalClose();
                            this.getCustomerAssignedList();
                            this.toastr.success(`Assigned inventory successfully.`, 'Success!');

                            this.replaceInventoryDialogRef?.close();
                        } else {
                            this.toastr.error(`${res.responseMessage}`, 'Failed!');

                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.responseMessage}`, 'Failed!');

                    }
                );
            } else {
                this.toastr.info(`Please select a product to replace`, 'Info!');

            }
        }
    }

    replaceAssemblyInventorySubmit(): void {
        this.replaceSumitted = true;
        var reason: any = this.replaceInventoryForm.value.replacementReason;
        const remark: any = this.replaceInventoryForm.value.remark;
        //console.log("selectedReplaceMACAddress", this.selectedReplaceMACAddress);
        var payload = [
            {
                oldMacMappingId: this.stbProductsOld.inOutWardMACMapping[0].id,
                newMacMappingId: this.selectedReplaceMACAddress.id
            },
            {
                oldMacMappingId: this.cardProductsOld.inOutWardMACMapping[0].id,
                newMacMappingId: this.selectedReplaceMACAddress2.id
            }
        ];

        // var payload=[{
        //  oldMacMappingId:this.stbProductsOld,
        //   newMacMappingId:this.selectedReplaceMACAddress.id
        // }];

        if (this.replaceInventoryForm.valid) {
            if (this.selectedReplaceMACAddress != "") {
                // const url = `/inwards/replaceInventory?oldMacMappingId=${this.replaceOldMappingId}&newMacMappingId=${this.selectedReplaceMACAddress.id}&customerId=${this.custData.id}&inventoryType=${this.replaceInventoryForm.value.inventoryType}&replacementReason=${reason}&approvalRemark=${remark}`;
                const url = `/inwards/replaceInventory?customerId=${this.custData.id}&inventoryType=${this.replaceInventoryForm.value.inventoryType}&replacementReason=${reason}&approvalRemark=${remark}`;
                this.customerInventoryManagementService.postMethod(url, payload).subscribe(
                    (res: any) => {
                        if (res.responseCode == 200) {
                            this.replaceAssemblyInventoryModalClose();
                            this.getCustomerAssignedList();
                            this.toastr.success(`Assigned inventory successfully.`, 'Success!');

                        } else {
                            this.toastr.error(`${res.responseMessage}`, 'Failed!');

                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.responseMessage}`, 'Failed!');

                    }
                );
            } else {
                this.toastr.info(`Please select a product to replace`, 'Info!');

            }
        }
    }

    // removeInvantryFunction(id, custinventoryid, ItemId, refundAmount) {
    //   const url = `/item/` + ItemId;
    //
    //   this.customerInventoryManagementService.getMethod(url).subscribe(
    //     (respose: any) => {
    //       if (
    //         respose.data.ownershipType == "Customer Owned" ||
    //         respose.data.ownershipType == "Partner Owned"
    //       ) {
    //         this.closeApproveInventoryModal();
    //         this.removeInventory(id, custinventoryid, "false", refundAmount);
    //       }
    //       // else {
    //       //   this.closeApproveInventoryModal();
    //       //   this.removeConfirmationInventory(id, custinventoryid);
    //       // }
    //     },
    //     (error: any) => {
    //       this.messageService.add({
    //         severity: "error",
    //         summary: "Error",
    //         detail: error.error.msg,
    //         icon: "far fa-times-circle",
    //       });
    //
    //     }
    //   );
    // }

    removeInventory(id, custinventoryid, ownershipFlag, refundAmount): void {
        // const remark = removeRemark;
        const url = `/inoutWardMacMapping/generateRemoveInventoryRequest?&macMappingId=${id}&customerInventoryId=${custinventoryid}&customerId=${this.custData.id}&isflag=${ownershipFlag}&revisedcharge=${refundAmount}`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                if (res.responseCode == 200) {
                    this.approveRemoveFlag = true;
                    this.rejectRemoveFlag = true;
                    this.getCustomerAssignedList();
                    this.toastr.success(`${res.responseMessage}`, 'Success!');

                } else {
                    this.approveRemoveFlag = false;
                    this.rejectRemoveFlag = false;
                    this.toastr.info(`${res.responseMessage}`, 'Info!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    removeConfirmationInventory(assignedInventoryId: number, cusiINventoryID, refundAmount) {
        if (assignedInventoryId) {
            this.confirmationService.confirm({
                message: "Do you want to change Ownership From Sold to Organization Owned " + "?",
                header: "Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.removeInventory(assignedInventoryId, cusiINventoryID, "true", refundAmount);
                },
                reject: () => {
                    this.removeInventory(assignedInventoryId, cusiINventoryID, "false", refundAmount);
                }
            });
        }
        // this.confirmationService.confirm({
        //   message: "Do you want to change Ownership From Sold to Organization Owned " + "?",
        //   header: "Confirmation",
        //   icon: "pi pi-info-circle",
        //   accept: () => {
        //
        //     this.removeInventory(assignedInventoryId, cusiINventoryID, "true");
        //     // this.ownershipForm.controls.ownership.setValue("true");
        //   },
        //   reject: () => {
        //     this.removeInventory(assignedInventoryId, cusiINventoryID, "false");
        //     // this.ownershipForm.controls.ownership.setValue("false");
        //     // this.messageService.add({
        //     //   severity: "info",
        //     //   summary: "Rejected",
        //     //   detail: "You have rejected",
        //     // });
        //   },
        // });
    }

    //  inventory Workflow list

    inventoryWorkFlowList(id): void {
        this.inventoryId = id;
        // $("#workflowInventoryModal").modal("show");
        this.inventoryWorkflowDialogRef = this.dialog.open(this.inventoryWorkflowDialog, {
            width: '70%',
            disableClose: true,
            panelClass: 'custom-dialog-container'
        });
        const url = `/teamHierarchy/getApprovalProgress?entityId=${id}&eventName=CUSTOMER_INVENTORY_ASSIGN`;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.inventoryApproveProgressDetail = res.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
        let page = this.currentPageInventoryApproveProgress;
        let page_list;

        this.inventoryWorkflowAuditData = [];

        let data = {
            page: page,
            pageSize: this.inventoryApproveProgressPerPage
        };

        let url1 = "/workflowaudit/list?entityId=" + id + "&eventName=" + "CUSTOMER_INVENTORY_ASSIGN";

        this.customerInventoryManagementService.postMethod(url1, data).subscribe(
            (response: any) => {
                this.inventoryWorkflowAuditData = response.dataList;
                this.inventoryApproveProgresstotalRecords = response.totalRecords;
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.ERROR}`, 'Failed!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
                console.log(error, "error");
            }
        );
    }

    pageChangedInventoryProgress(pageNumber) {
        this.currentPageInventoryApproveProgress = pageNumber;
        this.inventoryWorkFlowList(this.inventoryId);
    }

    approveInventory(): void {
        let itemAssemblyId = this.assignInventoryId;
        const approveId = [];
        this.assignAssignInventorysubmitted = true;
        const selInventory = this.assignedInventoryList.filter(
            inventory => inventory.custInventoryListId === itemAssemblyId
        );
        selInventory.forEach(inOutWardMACMapping => approveId.push(inOutWardMACMapping.id));
        const remarkAssign = this.approveAssignInventoryForm.value;
        let staffId = localStorage.getItem("userId");
        // const url = `/inwards/approveInventory?isApproveRequest=true&customerInventoryMappingId=${id}`;
        const url =
            "/inwards/approveInventory?isApproveRequest=true&nextstaff=" +
            staffId +
            "&remark=" +
            remarkAssign.remark;

        this.customerInventoryManagementService.postMethod(url, approveId).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    this.assignAssignInventorysubmitted = false;
                    this.approveAssignInventoryForm.reset();
                    if (response.dataList != null) {
                        this.approveAssignInventoryData = response.dataList;
                        this.approveAssignData = this.approveAssignInventoryData;
                        this.approved = true;
                    } else {
                        // $("#assignApproveOtherInventoryOpen").modal("hide");
                        this.getCustomerAssignedList();
                    }
                    this.approveAssignInventoryDialogRef.close();
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    rejectInventory(): void {
        const rejectId = [];
        let itemAssemblyId = this.assignInventoryId;
        const selInventory = this.assignedInventoryList.filter(
            inventory => inventory.custInventoryListId === itemAssemblyId
        );
        selInventory.forEach(inOutWardMACMapping => rejectId.push(inOutWardMACMapping.id));
        const remarkReject = this.rejectAssignInventoryForm.value;
        let staffId = localStorage.getItem("userId");
        //const url = `/inwards/approveInventory?isApproveRequest=false&customerInventoryMappingId=${id}`;
        const url =
            "/inwards/approveInventory?isApproveRequest=false&nextstaff=" +
            staffId +
            "&remark=" +
            remarkReject.remark;

        this.customerInventoryManagementService.postMethod(url, rejectId).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    this.rejectAssignInventorySubmitted = false;
                    this.rejectAssignInventoryForm.reset();
                    if (response.dataList != null) {
                        this.rejectAssignInventoryData = response.dataList;
                        this.reject = true;
                    } else {
                        // $("#assignRejectOtherInventoryOpen").modal("hide");
                        this.rejectAssignInventoryDialogRef.close();
                        this.getCustomerAssignedList();
                    }
                    this.rejectAssignInventoryDialogRef.close();
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    reactivateBoxResponse(custInventoryListId): void {
        const custInventoryId = [];
        const selInventory = this.assignedInventoryList.filter(
            inventory => inventory.custInventoryListId === custInventoryListId
        );
        selInventory.forEach(inOutWardMACMapping => custInventoryId.push(inOutWardMACMapping.id));
        const url = "/inwards/reactivateBoxResponse";

        this.customerInventoryManagementService.postMethod(url, custInventoryId).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.getCustomerAssignedList();
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    pairBox(custInventoryListId): void {
        const custInventoryId = [];
        const selInventory = this.assignedInventoryList.filter(
            inventory => inventory.custInventoryListId === custInventoryListId
        );
        selInventory.forEach(inOutWardMACMapping => custInventoryId.push(inOutWardMACMapping.id));
        const url = "/inwards/pairBox";

        this.customerInventoryManagementService.postMethod(url, custInventoryId).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.getCustomerAssignedList();
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    unpairBox(custInventoryListId): void {
        const custInventoryId = [];
        const selInventory = this.assignedInventoryList.filter(
            inventory => inventory.custInventoryListId === custInventoryListId
        );
        selInventory.forEach(inOutWardMACMapping => custInventoryId.push(inOutWardMACMapping.id));
        const url = "/inwards/unPairBox";

        this.customerInventoryManagementService.postMethod(url, custInventoryId).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.getCustomerAssignedList();
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    // edit inventory
    invenoryDetails: {
        oldId: string;
        oldSerialNumber: string;
        oldMacAddress: string;
        newId: string;
        newSerialNumber: string;
        newMacAddress: string;
        currentApproveId: string;
    }[] = [];

    editInventoryold: boolean = false;

    editCustomerInventory(item): void {
        this.invenoryDetails = [];
        const mappingitemAssemblyName = item.custInventoryListId;
        // console.log("mappingid",mappingId)

        var invenoryDetailsMappings: any = this.assignedInventoryList.filter(
            inventory => inventory.custInventoryListId === mappingitemAssemblyName
        );
        const invenoryDetailsMapping = invenoryDetailsMappings[0].inOutWardMACMapping;

        invenoryDetailsMappings.forEach(invenoryDetail => {
            // console.log("invenoryDetail",invenoryDetail);
            const oldId1 = invenoryDetail.inOutWardMACMapping[0].id;
            const oldSerialNumber1 = invenoryDetail.inOutWardMACMapping[0].serialNumber;
            const oldMac1 = invenoryDetail.inOutWardMACMapping[0].macAddress;
            const newid1 = invenoryDetail.inOutWardMACMapping[1]
                ? invenoryDetail.inOutWardMACMapping[1].id
                : "";
            const newSerialNumber1 = invenoryDetail.inOutWardMACMapping[1]
                ? invenoryDetail.inOutWardMACMapping[1].serialNumber
                : "";
            const newMac1 = invenoryDetail.inOutWardMACMapping[1]
                ? invenoryDetail.inOutWardMACMapping[1].macAddress
                : "";
            const currentApprovalId1 = invenoryDetail.inOutWardMACMapping[1]?.currentApproveId
                ? invenoryDetail.inOutWardMACMapping[1]?.currentApproveId
                : "";
            this.invenoryDetails.push({
                oldId: oldId1,
                oldSerialNumber: oldSerialNumber1,
                oldMacAddress: oldMac1,
                newId: newid1,
                newSerialNumber: newSerialNumber1,
                newMacAddress: newMac1,
                currentApproveId: currentApprovalId1
            });
        });

        this.editInventory = true;
        this.editSTBCradInventory = false;
    }
    // STB Card Individually Replacement Edit
    editSTBCARDCustomerInventory(item): void {
        this.invenoryDetails = [];
        //const mappingitemAssemblyName = item.itemAssemblyName;
        // console.log("mappingid",mappingId)

        var invenoryDetailsMappings: any = this.assignedInventoryList.filter(
            inventory => inventory.id === item.id
        );
        const invenoryDetailsMapping = invenoryDetailsMappings[0].inOutWardMACMapping;

        invenoryDetailsMappings.forEach(invenoryDetail => {
            // console.log("invenoryDetail",invenoryDetail);
            const oldId1 = invenoryDetail.inOutWardMACMapping[0].id;
            const oldSerialNumber1 = invenoryDetail.inOutWardMACMapping[0].serialNumber;
            const oldMac1 = invenoryDetail.inOutWardMACMapping[0].macAddress;
            const newid1 = invenoryDetail.inOutWardMACMapping[1]
                ? invenoryDetail.inOutWardMACMapping[1].id
                : "";
            const newSerialNumber1 = invenoryDetail.inOutWardMACMapping[1]
                ? invenoryDetail.inOutWardMACMapping[1].serialNumber
                : "";
            const newMac1 = invenoryDetail[1] ? invenoryDetail.inOutWardMACMapping[1].macAddress : "";
            const currentApprovalId1 = invenoryDetail.inOutWardMACMapping[1]
                ? invenoryDetail.inOutWardMACMapping[1].currentApproveId
                : "";
            this.invenoryDetails.push({
                oldId: oldId1,
                oldSerialNumber: oldSerialNumber1,
                oldMacAddress: oldMac1,
                newId: newid1,
                newSerialNumber: newSerialNumber1,
                newMacAddress: newMac1,
                currentApproveId: currentApprovalId1
            });
        });
        this.editInventory = false;
        this.editSTBCradInventory = true;
    }

    approveReplaceInventoryInventory(isApproveRequest: boolean, isPairToSingle: boolean): void {
        let bool: boolean = false;
        if (isApproveRequest) {
            bool = true;
        }
        let payload = [];
        this.assignReplaceInventorySubmitted = true;
        this.invenoryDetails.forEach(invenoryDetail => {
            this.replaceInventoryIdInOutMacMapping = invenoryDetail.newId;
            payload.push({
                oldMacMappingId: invenoryDetail.oldId,
                newMacMappingId: invenoryDetail.newId
            });
        });

        const url = `/inwards/approveReplaceInventory?isApproveRequest=${isApproveRequest}&billAble=${bool}`;

        this.customerInventoryManagementService.postMethod(url, payload).subscribe(
            (response: any) => {
                if (response.dataList != null) {
                    this.assignReplaceInventorySubmitted = false;
                    if (response.dataList[0].dataList) {
                        if (response.dataList[0].dataList.length > 0) {
                            this.isApproveRequest = isApproveRequest;
                            this.rejectPlanData = response.dataList[0].dataList;

                            $("#approvalReplaceInventory").modal("show");
                        }
                    } else if (response.responseCode == 200) {
                        this.toastr.success(`Approve replace inventory.`, 'Success!');

                        this.getCustomerAssignedList();
                        this.editInventory = false;
                        this.editSTBCradInventory = false;
                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    }
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    checkStatusForRepalce(id): void {
        $("#EditinventoryStatusView").modal("show");
        // const url = `/teamHierarchy/getApprovalProgress?entityId=${id}&eventName=CUSTOMER_INVENTORY_ASSIGN`;
        //
        // this.customerInventoryManagementService.getMethod(url).subscribe(
        //   (res: any) => {
        //     if (res.responseCode == 200) {
        //       $("#EditinventoryStatusView").modal("show");
        //       this.inventoryApproveProgressDetail = res.dataList;
        //
        //     } else {
        //
        //       this.messageService.add({
        //         severity: "error",
        //         summary: "Error",
        //         detail: res.responseMessage,
        //         icon: "far fa-times-circle",
        //       });
        //     }
        //   },
        //   (error: any) => {
        //     this.messageService.add({
        //       severity: "error",
        //       summary: "Error",
        //       detail: error.error.msg,
        //       icon: "far fa-times-circle",
        //     });
        //
        //   }
        // );
    }

    assignOtherInventoryModalOpen() {
        this.getInventoryJobTypeList();
        this.getNatureList();
        this.staffSelectList = [];
        this.displayDialogAssignOtherInventory = true;
        this.assignOtherInventoryDialogRef = this.dialog.open(this.assignOtherInventoryDialog, {
            width: '1500px',
        });
        this.newFirst = 1;
        this.inventoryAssignForm.get("assignedDateTime").setValue(this.currentDate);
        const loginUserName = localStorage.getItem('loginUserName')
        const userId = localStorage.getItem('userId');
        if (!Array.isArray(this.staffSelectList) || this.staffSelectList.length === 0) {
            this.staffSelectList = [{
                id: Number(userId),
                name: loginUserName
            }];
        }
        this.inventoryAssignForm.patchValue({
            paymentOwnerId: Number(userId)
        });


    }

    assignOtherInventoryModalClose() {
        this.inventoryAssignSumitted = false;
        this.getAllSerializedProductFlag = false;
        this.getAllConnectionNumberFlag = false;
        this.getAllAssemblyNameFlag = false;
        this.getAllAssemblyTypeFlag = false;
        this.getAllPairProductFlag = false;
        this.getSplitterFlag = false;
        this.getAllSingleItemMacFlag = false;
        this.getAllPairItemMacReplaceFlag = false;
        this.itemConditionPairFlag = false;
        this.getAllPairItemMacFlag = false;
        this.getAllNonSerializedProductFlag = false;
        this.serializedItemAssignFlag = false;
        this.nonSerializedItemAssignFlag = false;
        this.showQtyError = false;
        this.negativeAssignQtyError = false;
        this.availableQtyFlag = false;
        this.showError = false;
        this.inventoryAssignForm.reset();
        this.selectedMACAddress = "";
        this.cardFileterGlobal = "";
        this.stbFileterGlobal = "";
        this.fileterGlobalSingleItem = "";
        this.macAddressList = [];
        this.macAddressList1 = [];
        this.macAddressList2 = [];
        this.inventoryAssignForm.controls.itemAssemblyName.disable();
        this.inventoryAssignForm.controls.itemAssemblyflag.disable();
        this.billToSigleFlag = false;
        this.billToPairFlag = false;
        this.discountPairFlag = false;
        this.isInvoiceDataSingleFlag = false;
        this.itemConditionSingleFlag = false;
        this.itemConditionPlanSeriFlag = false;
        this.itemConditionPlanPairFlag = false;
        this.selAssemblyTypePlanFlag = false;
        this.selAssemblyTypePlanGroupFlag = false;
        this.getAssemblyNameflag = false;
        this.oldOfferPriceSingleFlag = false;
        this.newOfferSingleFlag = false;
        this.newOfferSTBFlag = false;
        this.newOfferCardFlag = false;
        this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
        this.inventoryAssignForm.controls.isInvoiceToOrg.setValue(false);
        this.inventoryAssignForm.controls.isRequiredApproval.setValue(false);
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountCardPairFlag = false;
        this.oldOfferBasedDiscountNonSerialFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferBasedDiscountSTBPairFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountSingleReplaceFlag = false;
        this.oldOfferPriceCardFlag = false;
        this.oldOfferPriceNonSerialFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPriceSTBFlag = false;
        this.oldOfferPriceSingleReplaceFlag = false;
        this.newOfferNonSerialFlag = false;
        this.displayDialogAssignOtherInventory = false;
        this.assignOtherInventoryDialogRef?.close();
        this.assignInventoryWithPlanDialogRef?.close();
    }

    assigneOtherInventory(): void {
        this.inventoryAssignSumitted = true;
        let data: any = "";
        data = this.inventoryAssignForm.value;
        if (this.inventoryAssignForm.invalid) {
            this.inventoryAssignForm.markAllAsTouched();
            this.toastr.info('Please fill all required fields correctly.', 'Info!');
            return;  // Prevent submission
        }

        data.inOutWardMACMapping = [];
        if (data.itemAssemblyflag == false) {
            if (this.selectedMACAddress != "" && this.selectedMACAddress != null) {
                data.inOutWardMACMapping.push(this.selectedMACAddress);
            }
        } else {
            // data.productId = "";
            if (this.selectedMACAddress != "" && this.selectedMACAddress != null) {
                //data.inOutWardMACMapping = this.selectedMACAddress;
                data.inOutWardMACMapping.push(this.selectedMACAddress);
                data.inOutWardMACMapping.push(this.selectedMACAddress2);
            }
        }
        data.productId = this.selectedMACAddress.productId;
        data.itemId = this.selectedMACAddress?.itemId;
        data.customerId = this.serviceCustomerId;
        data.staffId = this.staffUserId;
        data.itemAssemblyStatus = "Pending";
        data.itemType = this.selectedMACAddress?.condition;
        if (this.selectedMACAddress?.macAddress == "") {
            this.toastr.info(`Please Enter at mac address in selected item`, 'Info!');

        } else if (this.selectedMACAddress?.serialNumber == "") {
            this.toastr.info(`Please Enter at serial number in selected item`, 'Info!');

        } else {
            if (this.inventoryAssignForm.valid) {
                if (data.inOutWardMACMapping.length > 0) {
                    data.qty = data.inOutWardMACMapping.length;

                    let custInvParams = this.inventorySpecificationDetails.map(item => ({
                        paramName: item.paramName,
                        paramValue: item.paramValue
                    }));
                    custInvParams.push({
                        paramName: "ONT SN",
                        paramValue: this.selectedSerialNumber
                    });

                    data.custInvParams = custInvParams;
                    data.custServiceMapId = this.selectedService.customerServiceMappingId;

                    const url = "/inwards/assignToCustomer";
                    this.customerInventoryManagementService.postMethod(url, data).subscribe(
                        (res: any) => {
                            if (res.responseCode == 200) {
                                this.assignOtherInventoryModalClose();
                                this.getCustomerAssignedList();
                                this.toastr.success(`Assigned inventory successfully.`, 'Success!');

                                this.assignOtherInventoryDialogRef.close();

                            } else if (res.responseCode == 406) {
                                this.toastr.info(`${res.responseMessage}`, 'Info!');

                            } else {
                                this.toastr.error(`${res.responseMessage}`, 'Failed!');

                            }
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.msg}`, 'Failed!');

                        }
                    );
                } else {
                    this.toastr.info(`Please Select at least one item`, 'Info!');

                }
            }
        }
        // console.log("assigneOtherInventory ::::: ", data);
    }
    uniqueServices;

    getService(event) {
        const url =
            "/subscriber/getPlanByCustService/" +
            this.customerId +
            "?isAllRequired=true&isNotChangePlan=true";
        this.customerService.getMethod(url).subscribe(
            (response: any) => {
                this.serviceList = response.dataList;
                this.uniqueServices = this.serviceList.filter(obj => {
                    if (obj.custPlanStatus == "ACTIVE" || obj.custPlanStatus == "Active") {
                        return (
                            this.serviceList.findIndex(o => o.serviceId === obj.serviceId) ===
                            this.serviceList.indexOf(obj)
                        );
                    }
                });
                let selServiceId = event.value;
                this.connectionNoList = response.dataList.filter(stb => stb.serviceId == selServiceId);
                this.custPlanCategory = this.serviceList[0].custPlanCategory;
                this.custDiscount = this.serviceList[0].discount;
                this.custDiscountType = this.serviceList[0].discountType;
                if (this.serviceList[0].plangroupid != null) {
                    this.planGroupName = this.serviceList[0].planGroupName;
                    this.planGroupId = this.serviceList[0].plangroupid;
                    this.planInventoryAssignForm.controls.planGroupName.setValue(
                        this.serviceList[0].planGroupName
                    );
                    this.planInventoryAssignForm.controls.planGroupId.setValue(
                        this.serviceList[0].plangroupid
                    );
                    this.planGroupPlanMappingFlag = true;
                    this.individualPlanMappingFlag = false;
                } else {
                    this.planGroupPlanMappingFlag = false;
                    this.individualPlanMappingFlag = true;
                }
                this.getCustomerAssignedList();
                var keepGping = false;
                this.serviceList.forEach(item => {
                    if (!keepGping) {
                        var filteredItem = item.customerInventorySerialnumberDtos.filter(item => item.primary);
                        if (filteredItem.length > 0) {
                            this.isShowConnection = false;
                            this.serviceSerialNumbers.push({
                                serialNumber: filteredItem[0].serialNumber,
                                custPlanMapppingId: item.custPlanMapppingId,
                                connectionNo: item.connection_no
                            });
                        } else {
                            this.isShowConnection = true;
                            this.serviceSerialNumbers = [];
                            keepGping = true;
                        }
                    }
                });
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getSerialNumber(inventory) {
        return this.serviceSerialNumbers.filter(item => item.connectionNo === inventory.connectionNo)
            .length > 0
            ? this.serviceSerialNumbers.filter(item => item.connectionNo === inventory.connectionNo)[0]
                .serialNumber
            : "";
    }

    getConnectionNoDetails(event) {
        this.connectionDetailData = this.connectionNoList.filter(
            element => element.connection_no == event.value
        );
        this.serviceCustomerId = this.connectionDetailData[0].custId;
    }
    onChangeConnection(event) {
        this.serviceCustomerId = this.connectionNoList.filter(
            element => element.connection_no == event.value
        )[0].custId;
    }

    getAllProduct(event) {
        const url = "/product/getAllProductByServiceId?serviceId=" + event.value;
        //this.getService(event);
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                //this.getAllConnectionNumberFlag = true;
                this.allActiveProducts = response.dataList;
                this.allSTBProducts = response.dataList.filter(
                    stb => stb.productCategory.dtvCategory == "STB"
                );
                this.allCardProducts = response.dataList.filter(
                    stb => stb.productCategory.dtvCategory == "Card"
                );
                //this.pincodeListData = response.dataList.filter(pincode => pincode.status == "Active");
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getAllProductExternalItems() {
        const url = "/product/getAllProductsByCustomerOwned?custId=" + this.custData.id;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.allActiveProducts = response.dataList;
                } else {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    swapInventoryPlanModalOpen() {
        this.getChildAndParentInventoryDetails();
    }
    swapInventoryPlanModalClose() {
        this.displaySwapInventoryPlan = false;
        this.childAndParentInventoryDetailsForm.reset();
        this.uniqueparentService = [{ serviceId: "", serviceName: "" }];
        this.childAndParentInventoryDetails = {
            childcustomerServiceMappings: [
                {
                    connectionNo: "",
                    serviceId: "",
                    serviceName: ""
                }
            ],
            parentcustomerServiceMappings: [
                {
                    connectionNo: "",
                    serviceId: "",
                    serviceName: ""
                }
            ]
        };
    }

    childAndParentInventoryDetails = {
        childcustomerServiceMappings: [
            {
                connectionNo: "",
                serviceId: "",
                serviceName: ""
            }
        ],
        parentcustomerServiceMappings: [
            {
                connectionNo: "",
                serviceId: "",
                serviceName: ""
            }
        ]
    };
    swapOptions: any = [
        {
            label: "Parent & Child",
            value: "parent_child"
        },
        {
            label: "Child & Child",
            value: "child_child"
        }
    ];
    childAndParentInventoryDetailsForm = this.fb.group({
        serviceName: "",
        serviceId: "",
        parentConnectionNo: "",
        childConnectionNo: "",
        swapOption: "parent_child"
    });
    uniqueparentService: any = [];

    getChildAndParentInventoryDetails() {
        const url = "/inwards/getChildAndParentCustomer?customerId=" + this.custData.id;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.displaySwapInventoryPlan = true;
                    this.childAndParentInventoryDetails = response.data;
                    var parentServices = [];
                    this.childAndParentInventoryDetails.childcustomerServiceMappings.forEach(
                        childcustomerServiceMapping => {
                            parentServices.push(childcustomerServiceMapping);
                        }
                    );
                    // this.uniqueparentService = parentServices;
                    this.uniqueparentService = parentServices.filter(obj => {
                        return (
                            parentServices.findIndex(o => o.serviceId === obj.serviceId) ===
                            parentServices.indexOf(obj)
                        );
                    });
                } else if (response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    filtteredParentConnection;
    masterChildConnections;
    filtteredChildConnection;
    filtteredChildConnection1;
    setServiceId(event) {
        var service = this.uniqueparentService.find(service => {
            return (
                service.serviceName == this.childAndParentInventoryDetailsForm.controls.serviceName.value
            );
        });
        this.childAndParentInventoryDetailsForm.controls.parentConnectionNo.reset();
        this.childAndParentInventoryDetailsForm.controls.childConnectionNo.reset();
        this.childAndParentInventoryDetailsForm.controls.serviceId.setValue(service.serviceId);
        this.filtteredParentConnection =
            this.childAndParentInventoryDetails.parentcustomerServiceMappings.filter(
                item => item.serviceName == event.value
            );
        this.masterChildConnections =
            this.childAndParentInventoryDetails.childcustomerServiceMappings.filter(
                item => item.serviceName == event.value
            );
        this.filtteredChildConnection = this.masterChildConnections;
        this.filtteredChildConnection1 = this.filtteredChildConnection;
    }
    swapInventoryPlan() {
        let data: any = "";
        data = this.childAndParentInventoryDetailsForm.value;

        const url =
            "/inwards/swapServicesFromParantCustomerToChildCustomer?childconnectionNumber=" +
            data.childConnectionNo +
            "&parentconnectionNumber=" +
            data.parentConnectionNo +
            "&serviceId=" +
            data.serviceId +
            "&serviceName=" +
            data.serviceName;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.replaceInventoryModalClose();
                    this.getCustomerAssignedList();
                    this.toastr.success(`Successfully Swapped.`, 'Success!');

                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    assignExternalInventoryModalOpen() {
        this.displayAssignInventoryWithExternalItemGroup = true;
        this.externalInventoryAssignForm.get("assignedDateTime").setValue(this.currentDate);
        this.getAllProductExternalItems();
        this.dialogRefAssignExternalInventory = this.dialog.open(this.assignExternalInventoryDialog, {
            width: '60%',
            disableClose: true
        });
    }

    assignExternalInventoryModalClose() {
        this.externalInventoryAssignSumitted = false;
        this.getExternalProductFlag = false;
        this.getExternalItemListFlag = false;
        this.getAllConnectionNumberFlag = false;
        this.externalInventoryAssignForm.reset();
        this.selectedExternalMACAddress = "";
        this.externalItemsFilterGlobal = "";
        this.macAddressList = [];
        this.macExternalListFlag = false;
        this.billToSigleFlag = false;
        this.itemConditionPairFlag = false;
        this.billToPairFlag = false;
        this.getPlanSingleSplitterFlag = false;
        this.getPlanPairSplitterFlag = false;
        this.getAllPairPlanProductSTBFlag = false;
        this.getAllPairProductCardFlag = false;
        this.selAssemblyTypePlanFlag = false;
        this.selAssemblyTypePlanGroupFlag = false;
        this.getAssemblyNameflag = false;
        this.discountPairFlag = false;
        this.isInvoiceDataSingleFlag = false;
        this.itemConditionSingleFlag = false;
        this.itemConditionPlanSeriFlag = false;
        this.itemConditionPlanPairFlag = false;
        this.oldOfferPriceSingleFlag = false;
        this.newOfferSingleFlag = false;
        this.newOfferSTBFlag = false;
        this.newOfferCardFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountCardPairFlag = false;
        this.oldOfferBasedDiscountNonSerialFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferBasedDiscountSTBPairFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountSingleReplaceFlag = false;
        this.oldOfferPriceCardFlag = false;
        this.oldOfferPriceNonSerialFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPriceSTBFlag = false;
        this.oldOfferPriceSingleReplaceFlag = false;
        this.newOfferNonSerialFlag = false;
        this.displayAssignInventoryWithExternalItemGroup = false;
        this.dialogRefAssignExternalInventory.close();
    }

    assigneExternalInventory(): void {
        this.externalInventoryAssignSumitted = true;
        let data: any = "";
        data = this.externalInventoryAssignForm.value;
        data.qty = "1";
        data.itemId = this.selectedExternalMACAddress?.itemId;
        data.customerId = this.serviceCustomerId;
        data.staffId = this.staffUserId;
        data.inOutWardMACMapping = [];
        if (this.selectedExternalMACAddress != "") {
            data.inOutWardMACMapping.push(this.selectedExternalMACAddress);
        }
        if (this.externalInventoryAssignForm.valid) {
            if (data.inOutWardMACMapping.length > 0) {
                const url = "/inwards/assignToCustomer";
                this.customerInventoryManagementService.postMethod(url, data).subscribe(
                    (res: any) => {
                        if (res.responseCode == 200) {
                            this.assignExternalInventoryModalClose();
                            this.getCustomerAssignedList();
                            this.dialogRefAssignExternalInventory.close();
                            this.toastr.success(`Assigned inventory successfully.`, 'Success!');

                        } else if (res.responseCode == 406) {
                            this.toastr.info(`${res.responseMessage}`, 'Info!');

                        } else {
                            this.toastr.error(`${res.responseMessage}`, 'Failed!');

                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            } else {
                this.toastr.info(`Please select a product to assign`, 'Info!');

            }
        }
    }

    assignPlanInventoryModalOpen() {
        this.displayAssignPlanInventoryModal = true;
        this.newFirst = 1;
        this.planInventoryAssignForm.get("assignedDateTime").setValue(this.currentDate);
        const loginUserName = localStorage.getItem('loginUserName');
        const userId = localStorage.getItem('userId');
        if (!Array.isArray(this.staffSelectList) || this.staffSelectList.length === 0) {
            this.staffSelectList = [{
                id: Number(userId),
                name: loginUserName
            }];
        }
        this.planInventoryAssignForm.patchValue({
            paymentOwnerId: Number(userId)
        });
        this.assignInventoryWithPlanDialogRef = this.dialog.open(this.assignInventoryWithPlanDialog, {
            width: '1000px',
        });
    }

    assignPlanInventoryModalClose() {
        this.newFirst = 0;
        this.macPlanListFlag = false;
        this.billToPlanFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.planInventoryAssignSumitted = false;
        this.planInventoryAssignForm.reset();
        this.selectedPlanMACAddress = "";
        this.macAddressList = [];
        this.getAllPlanFlag = false;
        this.planGroupPlanMappingFlag = false;
        this.individualPlanMappingFlag = false;
        this.getPlanInventoryIdFlag = false;
        this.getProductCategoryFlag = false;
        this.getProductForPlanInventoryAssignFlag = false;
        this.getAllConnectionNumberFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.fileterGlobalPlanlevel = "";
        this.planInventoryAssignForm.controls.isInvoiceToOrg.setValue(false);
        this.planInventoryAssignForm.controls.isRequiredApproval.setValue(false);
        this.billToSigleFlag = false;
        this.billToPairFlag = false;
        this.itemConditionPairFlag = false;
        this.discountPairFlag = false;
        this.isInvoiceDataSingleFlag = false;
        this.itemConditionSingleFlag = false;
        this.itemConditionPlanSeriFlag = false;
        this.itemConditionPlanPairFlag = false;
        this.getAssemblyNameflag = false;
        this.oldOfferPriceSingleFlag = false;
        this.newOfferSingleFlag = false;
        this.newOfferSTBFlag = false;
        this.newOfferCardFlag = false;
        this.getPlanSingleSplitterFlag = false;
        this.getPlanPairSplitterFlag = false;
        this.getAllPairPlanProductSTBFlag = false;
        this.getAllPairProductCardFlag = false;
        this.selAssemblyTypePlanFlag = false;
        this.selAssemblyTypePlanGroupFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountCardPairFlag = false;
        this.oldOfferBasedDiscountNonSerialFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferBasedDiscountSTBPairFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountSingleReplaceFlag = false;
        this.oldOfferPriceCardFlag = false;
        this.oldOfferPriceNonSerialFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPriceSTBFlag = false;
        this.oldOfferPriceSingleReplaceFlag = false;
        this.newOfferNonSerialFlag = false;
        this.displayAssignPlanInventoryModal = false;
        this.assignInventoryWithPlanDialogRef.close();
    }

    getPlanByCustAndService(event) {
        if (event != null) {
            this.getAllPlanFlag = true;
            this.getActivePlan(event.value);
        } else {
            this.getAllPlanFlag = false;
        }
    }

    getServiceAtPlanInventory(event, dd: any) {
        this.selectedService = dd.selectedOption;
        if (event != null) {
            this.getAllConnectionNumberFlag = true;
            this.getService(event);
            this.getAllProduct(event);
            this.getPlanByCustAndService(event);
        } else {
            this.getAllConnectionNumberFlag = false;
        }
    }

    getServiceAtOtherInventory(event, dd: any) {
        // this.selectedService = dd.selectedOption;
        const selectedId = event.value;
        const selectedServiceObj = this.uniqueServices.find(s => s.serviceId === selectedId);

        this.selectedService = selectedServiceObj;
        if (event != null) {
            this.getAllConnectionNumberFlag = true;
            this.getService(event);
            this.getAllProduct(event);
        } else {
            this.getAllConnectionNumberFlag = false;
        }
    }
    onInventoryJobTypeChange(event: any) {
        this.selectedInventoryJobType = event.value;
    }

    onNatureChange(event: any) {
        this.selectedNature = event.value;
    }

    getServiceAtExternalInventory(event) {
        if (event != null) {
            this.getAllConnectionNumberFlag = true;
            this.getExternalProductFlag = true;
            this.getService(event);
            this.getAllProductExternalItems();
        } else {
            this.getAllConnectionNumberFlag = false;
            this.getExternalProductFlag = false;
        }
    }

    getProductCatAndProduct(event) {
        this.macPlanListFlag = false;
        this.billToPlanFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.itemConditionPlanSeriFlag = false;
        this.itemConditionPlanPairFlag = false;
        this.selAssemblyTypePlanFlag = true;
        this.selAssemblyTypePlanGroupFlag = false;
        this.planInventoryAssignForm.controls.isInvoiceToOrg.setValue(false);
        this.planInventoryAssignForm.controls.isRequiredApproval.setValue(false);
        this.requiredApprovalPlanFlag = false;
        this.productPlanMappingId = event.value;
        //this.custPackageUnit = this.serviceList.find(element => element.id == event.value);
        this.getProductcategory(event.value);
        this.getProductForPlanInventoryAssign(event.value);
    }

    getProductCatAndProductByPlanGroup(event) {
        this.macPlanListFlag = false;
        this.billToPlanFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.planInventoryAssignForm.get("itemAssemblyflag").reset();
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.itemConditionPlanSeriFlag = false;
        this.itemConditionPlanPairFlag = false;
        this.selAssemblyTypePlanFlag = false;
        this.selAssemblyTypePlanGroupFlag = true;
        this.getProductcategoryPlanGroup(event.value);
        this.getProductForPlanInventoryAssignPlanGrupId(event.value);
    }

    getProductCatAndProductReplace(id) {
        //this.custPackageUnit = this.serviceList.find(element => element.id == event.value);
        this.getProductcategoryReplace(id);
        this.getProductForPlanInventoryAssignReplace(id);
    }

    getProductcategory(mappingId) {
        const url = "/product_plan_mapping/getProductCategoryByPlanId?mappingId=" + mappingId;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getProductCategoryFlag = true;
                let data: any;
                data = response.dataList;
                this.productCategoryName = data[0].name;
                this.productCategoryId = data[0].id;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getProductcategoryPlanGroup(mappingId) {
        const url = "/product/getProductCategoryByProductPlanGroupMappingId?mappingId=" + mappingId;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getProductCategoryFlag = true;
                let data: any;
                data = response.dataList;
                this.productCategoryName = data[0].name;
                this.productCategoryId = data[0].id;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getProductcategoryReplace(id) {
        const url = "/product_plan_mapping/getProductCategoryByPlanId?mappingId=" + id;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getProductCategoryFlag = true;
                let data: any;
                data = response.dataList;
                this.productCategoryName = data[0].name;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getActivePlan(serviceId) {
        const url =
            "/subscriber/getActivePlanList/" +
            this.custData.id +
            "?serviceId=" +
            serviceId +
            "&isNotChangePlan=true";
        //----Need to confirm that we need to retrieve parent and childs all plan in single experience if yes than need to send isNotChangePlan=true in above api url
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.planList = response.dataList;
                if (this.planList.length != 0) {
                    this.billToPlanName = this.planList[0].billTo;
                    if (this.billToPlanName == "ORGANIZATION") {
                        this.billToPlan = "ORGANIZATION";
                    } else {
                        this.billToPlan = this.billToPlanName;
                    }
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getAllPlanIvnetoryId(planId) {
        const url = "/product/getAllPlanIvnetoryIdOnPlanId/planId?planId=" + planId.value;
        this.selPlanId = planId.value;
        this.getProductForPlanInventoryAssignFlag = false;
        this.getProductCategoryFlag = false;
        this.macPlanListFlag = false;
        this.billToPlanFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getPlanInventoryIdFlag = true;
                this.getAllPlanIvnetoryIdOnPlanIdList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getAllPlanGroupPlanIvnetoryId(planId) {
        const url =
            "/product/getAllInventoryIdOnPlanIdAndPlanGroupId?planId=" +
            planId.value +
            "&planGroupId=" +
            this.planGroupId;
        this.selPlanId = planId.value;
        this.getProductForPlanInventoryAssignFlag = false;
        this.selAssemblyTypePlanGroupFlag = false;
        this.planInventoryAssignForm.get("itemAssemblyflag").reset();
        this.itemConditionPlanSeriFlag = false;
        this.itemConditionPlanPairFlag = false;
        this.getProductCategoryFlag = false;
        this.macPlanListFlag = false;
        this.billToPlanFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getPlanInventoryIdFlag = true;
                this.getAllPlanIvnetoryIdOnPlanIdList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getAllPlanIvnetoryIdAtReplace(planId) {
        const url = "/product/getAllPlanIvnetoryIdOnPlanId/planId?planId=" + planId;
        this.selPlanId = planId.value;
        this.selReplacePlanId = planId;
        this.getProductForPlanInventoryAssignFlag = false;
        this.getProductCategoryFlag = false;
        this.macPlanListFlag = false;
        this.billToPlanFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPricePlanFlag = false;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                //this.getPlanInventoryIdFlag = true;
                //this.getAllPlanIvnetoryIdOnPlanIdList = response;
                this.planInventoryId = response.dataList;
                this.getProductCatAndProductReplace(this.planInventoryId[0].id);
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getProductForPlanInventoryAssign(mappingId) {
        const url = "/product_plan_mapping/getProductByPlanId?mappingId=" + mappingId;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.getProductForPlanInventoryAssignFlag = true;
                this.productByPlanList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getProductForPlanInventoryAssignPlanGrupId(mappingId) {
        const url = "/product/getProductByProductPlanGroupMappingId?mappingId=" + mappingId;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.getProductForPlanInventoryAssignFlag = true;
                this.productByPlanList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getProductForPlanInventoryAssignReplace(mappingId) {
        const url = "/product_plan_mapping/getProductByPlanId?mappingId=" + mappingId;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                //this.getProductForPlanInventoryAssignFlag = true;
                this.productByPlanListReplace = response.dataList;
                this.planInventoryReplaceFlag = true;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    assignPlanInventory(): void {
        this.planInventoryAssignSumitted = true;
        let data: any = "";
        data = this.planInventoryAssignForm.value;
        data.inOutWardMACMapping = [];
        //data.productId = "";
        if (data.itemAssemblyflag == false) {
            if (this.selectedPlanMACAddress != "" && this.selectedPlanMACAddress != null) {
                data.inOutWardMACMapping.push(this.selectedPlanMACAddress);
            }
        } else {
            if (
                this.selectedSTBPlanMACAddress != "" &&
                this.selectedSTBPlanMACAddress != null &&
                this.selectedCardPlanMACAddress != "" &&
                this.selectedCardPlanMACAddress != null
            ) {
                data.inOutWardMACMapping.push(this.selectedSTBPlanMACAddress);
                data.inOutWardMACMapping.push(this.selectedCardPlanMACAddress);
            }
        }
        data.productPlanMappingId = this.productPlanMappingId;
        data.itemId = this.selectedPlanMACAddress?.itemId;
        data.customerId = this.serviceCustomerId;
        data.staffId = this.staffUserId;
        data.itemAssemblyStatus = "Pending";
        data.itemType = this.selectedPlanMACAddress?.condition;
        if (this.selectedPlanMACAddress?.macAddress == "") {
            this.toastr.info(`Please Enter at mac address in selected item`, 'Info!');

        } else if (this.selectedPlanMACAddress?.serialNumber == "") {
            this.toastr.info(`Please Enter at serial number in selected item`, 'Info!');

        } else {
            if (this.planInventoryAssignForm.valid) {
                if (data.inOutWardMACMapping.length > 0) {
                    data.qty = data.inOutWardMACMapping.length;

                    let custInvParams = this.inventorySpecificationDetails.map(item => ({
                        paramName: item.paramName,
                        paramValue: item.paramValue
                    }));
                    custInvParams.push({
                        paramName: "ONT SN",
                        paramValue: this.selectedSerialNumber
                    });

                    data.custInvParams = custInvParams;
                    data.custServiceMapId = this.selectedService.customerServiceMappingId;

                    const url = "/inwards/assignToCustomer";
                    this.customerInventoryManagementService.postMethod(url, data).subscribe(
                        (res: any) => {
                            if (res.responseCode == 200) {
                                this.assignPlanInventoryModalClose();
                                this.getCustomerAssignedList();
                                this.planName = "";
                                this.productCategoryName = "";
                                this.assignInventoryWithPlanDialogRef.close();
                                this.toastr.success(`Assigned inventory successfully.`, 'Success!');

                            } else if (res.responseCode == 406) {
                                this.toastr.info(`${res.responseMessage}`, 'Info!');

                            } else {
                                this.toastr.error(`${res.responseMessage}`, 'Failed!');

                            }
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.msg}`, 'Failed!');

                        }
                    );
                } else {
                    this.toastr.info(`Please Select at least one item`, 'Info!');

                }
            }
        }
    }

    getSelAssemblyType(event) {
        if (event.value == true) {
            this.inventoryAssignForm.controls.itemAssemblyName.enable();
            this.getAllAssemblyNameFlag = true;
            this.oldOfferPriceSTBFlag = false;
            this.oldOfferBasedDiscountSTBPairFlag = false;
            // this.getSplitterFlag = true;
            // this.getAllPairProductFlag = true;
            this.getSplitterFlag = false;
            this.getAllPairProductFlag = false;
            this.getAllPairItemMacFlag = false;
            this.selectedMACAddress = [];
            //this.getAllProduct();
            this.itemConditionSingleFlag = false;
            this.itemConditionPlanSeriFlag = false;
            this.getAllSerializedProductFlag = false;
            this.getAllNonSerializedProductFlag = false;
            this.serializedItemAssignFlag = true;
            this.nonSerializedItemAssignFlag = false;
            this.itemConditionPairFlag = true;
            this.itemConditionPlanPairFlag = false;
            this.billToPairFlag = false;
            this.billToSigleFlag = false;
            this.discountPairFlag = false;
            this.oldOfferBasedDiscountSingleFlag = false;
        } else {
            this.inventoryAssignForm.controls.itemAssemblyName.disable();
            this.getAllAssemblyNameFlag = false;
            this.getAllPairProductFlag = false;
            this.getAllPairItemMacFlag = false;
            this.getSplitterFlag = false;
            this.oldOfferPriceSTBFlag = false;
            this.oldOfferBasedDiscountSTBPairFlag = false;
            this.selectedMACAddress = "";
            this.itemConditionSingleFlag = true;
            this.itemConditionPairFlag = false;
            this.itemConditionPlanSeriFlag = false;
            this.itemConditionPlanPairFlag = false;
            this.getAllSerializedProductFlag = false;
            this.getAllNonSerializedProductFlag = false;
            this.serializedItemAssignFlag = true;
            this.nonSerializedItemAssignFlag = false;
            this.billToPairFlag = false;
            this.billToSigleFlag = false;
            this.oldOfferBasedDiscountSingleFlag = false;
            this.discountPairFlag = false;
            //this.getAllProduct();
        }

        this.macAddressList = [];
        this.inventoryAssignForm.controls.productId.setValue("");
    }


    getSelItemType(event) {
        this.inventoryAssignForm.get("itemAssemblyflag").reset();
        if (event.value == "Non Serialized Item") {
            this.getAllSingleItemMacFlag = false;
            this.inventoryAssignForm.controls.itemAssemblyflag.disable();
            this.inventoryAssignForm.controls.itemType.disable();
            this.inventoryAssignForm.controls.nonSerializedItemRemark.enable();
            this.getAllAssemblyNameFlag = false;
            this.oldOfferPriceSTBFlag = false;
            this.oldOfferBasedDiscountSTBPairFlag = false;
            this.itemConditionPairFlag = false;
            this.billToPairFlag = false;
            this.discountPairFlag = false;
            this.billToSigleFlag = false;
            this.getAllAssemblyTypeFlag = false;
            this.getAllPairProductFlag = false;
            this.getAllPairItemMacFlag = false;
            this.getSplitterFlag = false;
            this.getAllNonSerializedProductFlag = true;
            this.getAllSerializedProductFlag = false;
            this.itemConditionSingleFlag = false;
            this.itemConditionPlanSeriFlag = false;
            this.itemConditionPlanPairFlag = false;
            this.serializedItemAssignFlag = false;
            this.nonSerializedItemAssignFlag = true;
            this.availableQtyFlag = false;
            this.oldOfferBasedDiscountNonSerialFlag = false;
            this.inventoryAssignForm.get("productId").reset();
            this.inventoryAssignForm.get("itemType").reset();
            this.getProductSelection();
        } else {
            this.inventoryAssignForm.controls.itemAssemblyflag.enable();
            this.inventoryAssignForm.controls.itemType.enable();
            this.inventoryAssignForm.controls.nonSerializedItemRemark.disable();
            this.getAllAssemblyNameFlag = false;
            this.itemConditionPairFlag = false;
            this.getAllAssemblyTypeFlag = true;
            this.oldOfferPriceSTBFlag = false;
            this.oldOfferBasedDiscountSTBPairFlag = false;
            this.getAllPairProductFlag = false;
            this.getAllPairItemMacFlag = false;
            this.getSplitterFlag = false;
            this.getAllNonSerializedProductFlag = false;
            this.getAllSerializedProductFlag = false;
            this.itemConditionSingleFlag = false;
            this.itemConditionPlanSeriFlag = false;
            this.itemConditionPlanPairFlag = false;
            this.serializedItemAssignFlag = false;
            this.nonSerializedItemAssignFlag = false;
            this.inventoryAssignForm.controls.itemAssemblyName.disable();
            this.availableQtyFlag = false;
            this.oldOfferBasedDiscountNonSerialFlag = false;
        }
    }
    // Selection of Assembly Type for Plan Inventory Assign
    getSelPlanAssemblyType(event) {
        if (event.value) {
            this.planInventoryAssignForm.controls.itemAssemblyName.enable();
            this.selectedMACAddress = [];
            this.getPlanSingleSplitterFlag = false;
            this.getPlanPairSplitterFlag = true;
            this.itemConditionPlanSeriFlag = false;
            this.itemConditionPlanPairFlag = true;
            this.getAssemblyNameflag = true;
        } else {
            this.itemConditionPlanSeriFlag = true;
            this.getPlanSingleSplitterFlag = true;
            this.getPlanPairSplitterFlag = false;
            this.itemConditionPlanPairFlag = false;
            this.getAssemblyNameflag = false;
            this.planInventoryAssignForm.controls.itemAssemblyName.disable();
            this.selectedMACAddress = "";
        }
        this.macAddressList = [];
        this.planInventoryAssignForm.controls.productId.setValue("");
    }

    getProductSelection(): void {
        const url = "/product/getAllProductForNonTrackableProductCategory";
        this.customerInventoryManagementService.getMethod(url).subscribe((response: any) => {
            this.allActiveNonTrackableProducts = response.dataList;
        });
    }

    getExternalItemList(event) {
        const ownerId = this.custData.id;
        const url =
            "/externalitemmanagement/getAllExternalItemGroupByProductAndStaff?productId=" +
            event.value +
            "&ownerId=" +
            ownerId;
        const productId = event.value;
        let product = this.allActiveProducts.find(element => element.id == productId);
        this.hasMac = product.productCategory.hasMac;
        this.hasSerial = product.productCategory.hasSerial;
        this.getExternalItemListFlag = true;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.externalItemList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getAllMappingByExternal(event) {
        this.macExternalListFlag = true;
        this.macAddressList = [];
        const url = "/inoutWardMacMapping/getAllMACMappingByExternalId?external_id=" + event.value;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.macAddressList = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    assigneOtherInventoryForNonSerializedItem(): void {
        this.inventoryAssignSumitted = true;
        if (this.inventoryAssignForm.invalid) {
            this.inventoryAssignForm.markAllAsTouched();
            this.toastr.info('Please fill all required fields correctly.', 'Info!');
            return;  // Prevent submission
        }
        let data: any = "";
        data = this.inventoryAssignForm.value;
        data.itemId = data.productId;
        data.customerId = this.serviceCustomerId;
        data.staffId = this.staffUserId;
        data.itemAssemblyStatus = "Pending";
        // console.log("assigneOtherInventoryForNonSerializedItem ::::: ", data);

        // return;


        if (this.inventoryAssignForm.valid && !this.showQtyError && !this.negativeAssignQtyError) {

            data.qty = data.nonSerializedQty;
            this.submitted = true;
            if (data.qty == "" || data.qty == null) {
                this.toastr.info(`Please Enter Assign Quantity`, 'Info!');

            } else if (data.nonSerializedItemRemark == "" || data.nonSerializedItemRemark == null) {
                this.showError = true;
            } else {
                this.showError = false;
                const url = "/inwards/assignNonSerializedItemToCustomer";
                this.customerInventoryManagementService.postMethod(url, data).subscribe(
                    (res: any) => {
                        if (res.responseCode == 200) {
                            this.assignOtherInventoryModalClose();
                            this.getCustomerAssignedList();
                            this.toastr.success(`Assigned inventory successfully.`, 'Success!');

                            this.assignOtherInventoryDialogRef.close();
                        } else {
                            this.toastr.error(`${res.responseMessage}`, 'Failed!');

                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.msg}`, 'Failed!');

                    }
                );
            }
        }
    }

    getNonTrackableProductQty(event) {
        const staffId = localStorage.getItem("userId");
        const productId = event.value;
        let product = this.allActiveNonTrackableProducts.find(element => element.id == productId);
        const url =
            "/outwards/getNonTrackableProductQty?productId=" +
            productId +
            "&ownerId=" +
            staffId +
            "&ownerType=Staff";
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.inventoryAssignForm.get('nonSerializedQty').setValue('');
                this.availableQtyFlag = true;
                this.isInvoiceDataNonSerialFlag = false;
                this.oldOfferBasedDiscountNonSerialFlag = true;
                this.getNonTrackableProductQtyList = res.dataList;
                this.UOM = this.allActiveNonTrackableProducts.find(
                    element => element.id == productId
                ).productCategory.unit;
                if (
                    this.getNonTrackableProductQtyList.length == 0 ||
                    this.getNonTrackableProductQtyList == null
                ) {
                    this.availableQty = 0;
                    this.toastr.info(`Assignee does not have sufficient product quantity`, 'Info!');

                } else {
                    if (this.UOM == "kilometer" || this.UOM == "meter") {
                        this.UOM = "meter";
                    } else {
                        this.UOM = this.UOM;
                    }
                    if (res.dataList.length == 0 || res.dataList == null) {
                        this.availableQty = 0;
                    } else {
                        this.availableQty = res.dataList.find(element => element).unusedQty;
                    }
                    this.getNonProductDetails(product);
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    saveMacidMapping(id, data) {
        let url = `/item/updateItemMacAndSerial?itemId=${id}&macAddress=${data.macAddress}&serialNumber=${data.serialNumber}`;
        this.customerInventoryManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.enterReplacementLevelMacSerial = "";
                    this.enterPlanLevelMacSerial = "";
                    this.enterMacSerial = "";
                    this.isEditEnable = true;
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                }
                if (response.responseCode == 417 || response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                }

                // this.workflowAuditData1 = response.dataList;
                // this.MastertotalRecords1 = response.totalRecords;
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.ERROR}`, 'Failed!');


                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
                console.log(error, "error");
            }
        );
    }
    // cust-inventory-management.component.ts
    // replacement for editMacMapping
    editMacMapping(id: any, product: any, event: MouseEvent) {
        if (product?.customerId != null) return;
        const isCurrentlySelected = !!(this.selectedMACAddress && this.selectedMACAddress.id === product.id);
        if (isCurrentlySelected) {
            this.isEditEnable = false;
            this.editMacSerialBtn = "";
            this.selectedSerialNumber = "";
            this.enterMacSerial = "";
            this.enterPlanLevelMacSerial = "";
            this.enterReplacementLevelMacSerial = "";
        } else {
            this.selectedMACAddress = product;
            this.isEditEnable = true;
            this.editMacSerialBtn = id;
            this.selectedSerialNumber = product.serialNumber;
            this.addSpecificationParamDetails(product);
            this.enterMacSerial = "";
            this.enterPlanLevelMacSerial = "";
            this.enterReplacementLevelMacSerial = "";
        }
        this.cdr.detectChanges();
    }


    editMac(id) {
        this.enterMacSerial = id;
        this.isEditEnable = false;
    }
    // STB
    editSTBSerialMapping(id) {
        this.editSTBSerialBtn = id;
    }

    editSTBSerial(id) {
        this.enterSTBSerial = id;
    }

    // Card
    editCardSerialMapping(id) {
        this.editCardSerialBtn = id;
        this.isEditEnable = true;
    }

    editCardSerial(id) {
        this.enterCardSerial = id;
        this.isEditEnable = false;
    }
    // Plan STB
    editPlanSTBSerialMapping(id) {
        this.editSTBSerialBtn = id;
        this.isEditEnable = true;
    }

    editPlanSTBSerial(id) {
        this.enterSTBSerial = id;
        this.isEditEnable = false;
    }
    // Plan
    editPlanLevelMacMapping(id, product) {
        this.editPlanLevelMacSerialBtn = id;
        this.selectedSerialNumber = product.serialNumber;
        this.isEditEnable = true;
        this.addSpecificationParamDetails(product);
    }

    editPlanLevelMac(id) {
        this.enterPlanLevelMacSerial = id;
        this.isEditEnable = false;
    }

    selectedCard: any;

    editReplacementLevelMacMapping(id, product) {
        this.editReplacementLevelMacSerialBtn = id;
        this.selectedReplaceMACAddress = product;
        this.selectedSerialNumber = product.serialNumber;
        this.isEditEnable = true;
        this.addSpecificationParamDetails(product);
    }

    onSelectionChange() {
    }

    editReplacementLevelMac(id) {
        this.enterReplacementLevelMacSerial = id;
        this.isEditEnable = false;
    }

    assignQuantityValidation(event) {
        var num = String.fromCharCode(event.which);
        if (!/[0-9]/.test(num)) {
            event.preventDefault();
        }
    }

    generateRemoveInventoryRequest(inventory) {
        let id = inventory.inOutWardMACMapping[0].id;
        let custinventoryid = inventory.inOutWardMACMapping[0].custInventoryMappingId;
        let ItemId = inventory.inOutWardMACMapping[0].itemId;
        this.editInventory = false;
        this.editSTBCradInventory = false;
        this.removeId = id;
        this.removeCustinventoryid = custinventoryid;
        this.removeItemId = ItemId;
        if (id) {
            // this.confirmationService.confirm({
            //     message: "Do you want to remove inventory " + "?",
            //     header: "Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.getCustomerInventoryMappingDetails(ItemId, custinventoryid);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: 'Confirmation',
                    description: 'Do you want to remove inventory ? ',
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.getCustomerInventoryMappingDetails(ItemId, custinventoryid);
                } else {
                }
            });
        }
    }

    onPageChange(event: PageEvent): void {
        // this.customerInventoryListDataCurrentPage = event.pageIndex + 1; // API is 1-based
        // this.customerInventoryListItemsPerPage = event.pageSize;
        // this.getCustomerAssignedList();
        this.pageSize = event.pageSize;
        this.currentPage = event.pageIndex + 1; // MatPaginator uses 0-based index

        // Slice full data for current page
        const startIndex = (this.currentPage - 1) * this.pageSize;
        const endIndex = startIndex + this.pageSize;
        this.getAllInventoryofCust.data = this.getAllInventoryofCustFull.slice(startIndex, endIndex);
    }

    acceptRemoveItem() {
        this.editInventory = false;
        this.editSTBCradInventory = false;
        this.removeRemarkSubmitted = true;
        let refundAmount = "";
        this.removeInventory(this.removeId, this.removeCustinventoryid, "false", refundAmount);
        //this.removeRemark = this.removeRemarkForm.value;
        // if (this.removeRemarkForm.valid) {
        // this.removeInvantryFunction(this.removeId, this.removeCustinventoryid, this.removeItemId);
        // }
    }
    finalRemoveWithRefund() {
        let refundAmount = this.refundAmountForm.get("newRefundAmount").value;
        this.refundAmountSubmitted = true;
        this.removeInventory(this.removeId, this.removeCustinventoryid, "false", refundAmount);
        this.closeRefundAmountModal();
    }
    closeApproveInventoryModal() {
        this.editInventory = false;
        this.editSTBCradInventory = false;
        this.removeRemarkSubmitted = false;
        this.removeRemarkForm.reset();
        // $("#approveChangeStatusModal").modal("hide");
    }

    closeRefundAmountModal() {
        this.refundAmountForm.reset();
        this.refundAmountModal = false;
        this.dialog.closeAll();
    }

    getAllCustomerInvetoryDetailshistory() {
        const url = "/item/getAllCustomerInvetoryDetailshistory?custId=" + this.custData.id;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getAllCustomerInvetoryDetailshistoryData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    clearFileterGlobalSingleItem(table: Table) {
        this.fileterGlobalSingleItem = "";
        table.clear();
    }

    clearstbFileterGlobal(table: Table) {
        this.stbFileterGlobal = "";
        table.clear();
    }

    clearcardFileterGlobal(table: Table) {
        this.cardFileterGlobal = "";
        table.clear();
    }

    clearFilterGlobalReplaceSingle(table: Table) {
        this.filterGlobalReplaceSingle = "";
        table.clear();
    }

    clearfileterGlobalPlanlevel(table: Table) {
        this.fileterGlobalPlanlevel = "";
        table.clear();
    }

    clearstbFileterGlobalReplace(table: Table) {
        this.stbFileterGlobalReplace = "";
        table.clear();
    }

    clearcardFileterGlobalReplace(table: Table) {
        this.cardFileterGlobalReplace = "";
        table.clear();
    }

    clearexternalItemsFilterGlobal(table: Table) {
        this.externalItemsFilterGlobal = "";
        table.clear();
    }

    getAllInventoryHistoryModalOpen() {
        this.displayCustomerInventoryHistory = true;
        this.openCustInventoryHistory();
        this.getAllInventoryHistory();
    }

    getAllInventoryHistoryModalClose() {
        this.displayCustomerInventoryHistory = false;
    }

    inventoryLogDetailsList: any;

    inventoryLogModalOpen() {
        const url = "/inwards/getCustomerbasedOnDtvHistory?customerId=" + this.custData.id;
        this.customerInventoryManagementService.getMethod(url).subscribe((res: any) => {
            if (res.responseCode == 200) {
                this.displayDTVHistory = true;
                this.inventoryLogDetailsList = res.dataList;
            }
        });
    }

    closeInventoryLogModal() {
        this.displayDTVHistory = false;
    }
    getAllInventoryHistory() {
        // this.getAllInventoryofCust = [];

        const url = "/item/getAllCustomerInvetoryDetailshistory?custId=" + this.custData.id;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.getAllInventoryofCustFull = res.dataList; // Store FULL list
                //   this.totalRecords = res.totalRecords;
                this.totalRecords = this.getAllInventoryofCustFull.length;

                // Slice data for current page (0-based indexing)
                const startIndex = (this.currentPage - 1) * this.pageSize;
                const endIndex = startIndex + this.pageSize;
                this.getAllInventoryofCust.data = this.getAllInventoryofCustFull.slice(startIndex, endIndex);

                if (res.dataList?.length === 0) {
                    setTimeout(() => this.btnClose.nativeElement.click(), 100);
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    cleargetAllInventoryofCustFilterGlobal(table: Table) {
        this.getAllInventoryofCustFilterGlobal = "";
        table.clear();
    }

    onclosed() {
        this.getAllInventoryofCustFilterGlobal = "";
        this.displayCustomerInventoryHistory = false;
    }

    // Bill To Plan Inventory Assign
    getMappingDetails(planGroupId, planId, productCategoryId, productId, billToPlan) {
        const url =
            "/product/getMappingDetails?planGroupId=" +
            planGroupId +
            "&planId=" +
            planId +
            "&productCategoryId=" +
            productCategoryId +
            "&productId=" +
            productId;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.mappingList = response.dataList;
                this.oldOfferPricePlan = this.mappingList[0].revisedCharge;
                this.newOfferPricePlan = this.mappingList[0].revisedCharge;
                let offerPrice = Number(this.oldOfferPricePlan);
                let newOfferPrice = Number(this.newOfferPricePlan);
                if (billToPlan == "ORGANIZATION") {
                    if (offerPrice != 0) {
                        this.newOfferPriceFlag = false;
                        this.invoiceDataReadOnly = false;
                    } else {
                        this.newOfferPriceFlag = true;
                        this.invoiceDataReadOnly = true;
                    }
                } else {
                    if (offerPrice != 0) {
                        this.newOfferPriceFlag = false;
                        this.invoiceDataReadOnly = false;
                    } else {
                        this.newOfferPriceFlag = true;
                        this.invoiceDataReadOnly = false;
                    }
                }
                // let discount = Number(this.connectionDetailData[0].discount);
                this.selectedCustDiscount = this.custDiscount;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    staffDataList: any = [];

    getStaffDetailById(serviceAreaId) {
        const url = "/getstaffuserbyserviceareaid/" + serviceAreaId;
        this.serviceAreaService.getMethod(url).subscribe((response: any) => {
            this.staffDataList = response.dataList;
            //console.log("staffDataList", this.data);
            this.staffDataList.forEach((element, i) => {
                element.displayLabel = element.fullName + " (Ph: " + element.phone + ")";
            });
        });
    }

    // Bill to Serialized Single Item Other Inventory
    getProductDetails(product) {
        if (this.selItemCondition == "New") {
            this.oldOfferOtherSigle = product.newProductAmount;
            this.newOfferOtherSigle = product.newProductAmount;
            if (this.oldOfferOtherSigle == null) {
                this.newOfferSingleFlag = false;
            } else {
                this.newOfferSingleFlag = true;
            }
            // let offerPrice = Number(this.oldOfferOtherSigle);
            // let newOfferPrice = Number(this.newOfferOtherSigle);
            // let discount = Number(this.connectionDetailData[0].discount);
            this.selectedCustDiscount = this.custDiscount;
            let date;
            date = this.currentDate.toISOString();
            const format = "yyyy-MM-dd";
            const locale = "en-US";
            const myDate = date;
            const formattedDate = formatDate(myDate, format, locale);
        }
        if (this.selItemCondition == "Refurbished") {
            this.oldOfferOtherSigle = product.refurburshiedProductAmount;
            this.newOfferOtherSigle = product.refurburshiedProductAmount;
            if (this.oldOfferOtherSigle == null) {
                this.newOfferSingleFlag = false;
            } else {
                this.newOfferSingleFlag = true;
            }
            // let offerPrice = Number(this.oldOfferOtherSigle);
            // let newOfferPrice = Number(this.newOfferOtherSigle);
            // let discount = Number(this.connectionDetailData[0].discount);
            this.selectedCustDiscount = this.custDiscount;
        }
    }

    // Bill to Non Serialized Product Detais
    getNonProductDetails(product) {
        this.perUOMCharge = product.newProductAmount;
        this.newUOMAmount = product.newProductAmount;
        if (this.perUOMCharge == null) {
            this.newOfferNonSerialFlag = false;
        } else {
            this.newOfferNonSerialFlag = true;
        }
        // let offerPrice = Number(this.perUOMCharge);
        // let newOfferPrice = Number(this.newUOMAmount);
        // let discount = Number(this.connectionDetailData[0].discount);
        this.selectedCustDiscount = this.custDiscount;
    }

    getCardProductDetails(product) {
        if (this.selItemCondition == "New") {
            this.oldOfferCard = 0;
            this.newOfferCard = 0;
            // let offerPrice = Number(this.oldOfferCard);
            // let newOfferPrice = Number(this.newOfferCard);
            // let discount = Number(this.connectionDetailData[0].discount);
            if (this.oldOfferCard == null) {
                this.newOfferCardFlag = false;
            } else {
                this.newOfferCardFlag = true;
            }
        }
        if (this.selItemCondition == "Refurbished") {
            this.oldOfferCard = 0;
            this.newOfferCard = 0;
            // let offerPrice = Number(this.oldOfferCard);
            // let newOfferPrice = Number(this.newOfferCard);
            // let discount = Number(this.connectionDetailData[0].discount);
            if (this.oldOfferCard == null) {
                this.newOfferCardFlag = false;
            } else {
                this.newOfferCardFlag = true;
            }
        }
    }
    // Bill to STB Product Details
    getSTBProductDetails(product) {
        if (this.selItemCondition == "New") {
            this.oldOfferSTB = product.newProductAmount;
            this.newOfferSTB = product.newProductAmount;
            // let offerPrice = Number(this.oldOfferSTB);
            // let newOfferPrice = Number(this.newOfferSTB);
            // let discount = Number();
            // if (this.connectionDetailData[0].discount != 0) {
            //   discount = Number(this.connectionDetailData[0].discount);
            // } else {
            //   discount = 0;
            // }
            if (this.oldOfferSTB == null) {
                this.newOfferSTBFlag = false;
            } else {
                this.newOfferSTBFlag = true;
            }
            this.selectedPairDiscount = this.custDiscount;
        }
        if (this.selItemCondition == "Refurbished") {
            this.oldOfferSTB = product.refurburshiedProductAmount;
            this.newOfferSTB = product.refurburshiedProductAmount;
            // let offerPrice = Number(this.oldOfferSTB);
            // let newOfferPrice = Number(this.newOfferSTB);
            // let discount = Number(this.connectionDetailData[0].discount);
            this.selectedPairDiscount = this.custDiscount;
            if (this.oldOfferSTB == null) {
                this.newOfferSTBFlag = false;
            } else {
                this.newOfferSTBFlag = true;
            }
        }
    }

    selectBillToSingle(event) {
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferPriceSingleFlag = false;
        if (event.value == "ORGANIZATION") {
            this.isInvoiceDataSingleFlag = true;
            this.oldOfferPriceSingleFlag = true;
            this.oldOfferBasedDiscountSingleFlag = false;
            if (this.oldOfferOtherSigle == null) {
                this.newOfferSingleFlag = false;
            } else {
                this.newOfferSingleFlag = true;
            }
        } else {
            this.isInvoiceDataSingleFlag = false;
            this.oldOfferPriceSingleFlag = false;
            this.oldOfferBasedDiscountSingleFlag = true;
            if (this.oldOfferOtherSigle == null) {
                this.newOfferSingleFlag = false;
            } else {
                this.newOfferSingleFlag = true;
            }
        }
    }

    // Bill To Support For Non Serialized Item
    selectBillToNonSerialize(event) {
        this.oldOfferBasedDiscountNonSerialFlag = false;
        this.oldOfferPriceNonSerialFlag = false;
        if (event.value == "ORGANIZATION") {
            this.isInvoiceDataNonSerialFlag = true;
            this.oldOfferPriceNonSerialFlag = true;
            this.oldOfferBasedDiscountNonSerialFlag = false;
            if (this.perUOMCharge == null) {
                this.newOfferNonSerialFlag = false;
            } else {
                this.newOfferNonSerialFlag = true;
            }
        } else {
            this.isInvoiceDataNonSerialFlag = false;
            this.oldOfferPriceNonSerialFlag = false;
            this.oldOfferBasedDiscountNonSerialFlag = true;
            if (this.perUOMCharge == null) {
                this.newOfferNonSerialFlag = false;
            } else {
                this.newOfferNonSerialFlag = true;
            }
        }
    }

    selectItemCondition(event) {
        if (event.value == "New") {
            this.selItemCondition = event.value;
            this.inventoryAssignForm.get("productId").reset();
            this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
            this.getAllSerializedProductFlag = true;
            this.getAllSingleItemMacFlag = false;
            this.billToSigleFlag = false;
            this.billToPairFlag = false;
            this.discountPairFlag = false;
            this.isInvoiceDataSingleFlag = false;
            this.oldOfferPriceSingleFlag = false;
            this.oldOfferBasedDiscountSingleFlag = false;
        } else {
            this.selItemCondition = event.value;
            this.inventoryAssignForm.get("productId").reset();
            this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
            this.getAllSerializedProductFlag = true;
            this.getAllSingleItemMacFlag = false;
            this.billToSigleFlag = false;
            this.billToPairFlag = false;
            this.discountPairFlag = false;
            this.isInvoiceDataSingleFlag = false;
            this.oldOfferPriceSingleFlag = false;
            this.oldOfferBasedDiscountSingleFlag = false;
        }
    }
    selectItemConditionPlan(event) {
        if (event.value == "New") {
            this.selPlanItemCondition = event.value;
            this.planInventoryAssignForm.get("productId").reset();
            this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
            // this.getAllSerializedProductFlag = true;
            this.getProductForPlanInventoryAssignFlag = true;
            this.getAllPairPlanProductSTBFlag = true;
            this.getAllPairProductCardFlag = true;
            this.getAllSingleItemMacFlag = false;
            this.billToPlanFlag = false;
            this.isInvoiceDataFlag = false;
            this.oldOfferPricePlanFlag = false;
            this.oldOfferBasedDiscountPlanFlag = false;
        } else {
            this.selPlanItemCondition = event.value;
            this.planInventoryAssignForm.get("productId").reset();
            this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
            // this.getAllSerializedProductFlag = true;
            this.getProductForPlanInventoryAssignFlag = true;
            this.getAllPairPlanProductSTBFlag = true;
            this.getAllPairProductCardFlag = true;
            this.getAllSingleItemMacFlag = false;
            this.billToPlanFlag = false;
            this.isInvoiceDataFlag = false;
            this.oldOfferPricePlanFlag = false;
            this.oldOfferBasedDiscountPlanFlag = false;
        }
    }
    // Bill to Serialized Pair Other
    selectBillToPair(event) {
        //this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountSTBPairFlag = false;
        this.oldOfferBasedDiscountCardPairFlag = false;
        this.discountPairFlag = false;
        //this.oldOfferBasedDiscountCardFlag = false;
        //this.oldOfferPriceSingleFlag = false;
        // this.oldOfferPriceSTBFlag = false;
        // this.oldOfferPriceCardFlag = false;
        if (event.value == "ORGANIZATION") {
            this.isInvoiceDataPairFlag = true;
            this.oldOfferPriceSTBFlag = true;
            this.oldOfferPriceCardFlag = true;
            this.oldOfferBasedDiscountSTBPairFlag = false;
            this.oldOfferBasedDiscountCardPairFlag = false;
            this.discountPairFlag = false;
            if (this.oldOfferSTB == null) {
                this.newOfferSTBFlag = false;
            } else {
                this.newOfferSTBFlag = true;
            }
            if (this.oldOfferCard == null) {
                this.newOfferCardFlag = false;
            } else {
                this.newOfferCardFlag = true;
            }
        } else {
            this.isInvoiceDataPairFlag = false;
            this.oldOfferPriceSTBFlag = false;
            this.oldOfferPriceCardFlag = false;
            this.oldOfferBasedDiscountSTBPairFlag = true;
            this.oldOfferBasedDiscountCardPairFlag = true;
            this.discountPairFlag = true;
            if (this.oldOfferSTB == null) {
                this.newOfferSTBFlag = false;
            } else {
                this.newOfferSTBFlag = true;
            }
            if (this.oldOfferCard == null) {
                this.newOfferCardFlag = false;
            } else {
                this.newOfferCardFlag = true;
            }
        }
    }

    // selectBillToCardPair(event) {
    //   //this.oldOfferBasedDiscountSingleFlag = false;
    //   // this.oldOfferBasedDiscountSTBFlag = false;
    //   this.oldOfferBasedDiscountPairFlag = false;
    //   //this.oldOfferPriceSingleFlag = false;
    //   // this.oldOfferPriceSTBFlag = false;
    //   this.oldOfferPriceCardFlag = false;
    //     if (event.value == "ORGANIZATION") {
    //       this.isInvoiceDataPairFlag = true;
    //       this.oldOfferPriceSTBFlag = true;
    //       this.oldOfferPriceCardFlag = true;
    //       this.oldOfferBasedDiscountPairFlag = false;
    //       if (this.oldOfferOtherSigle == null) {
    //         this.newOfferSingleFlag = false;
    //       } else {
    //         this.newOfferSingleFlag = true;
    //       }
    //     } else {
    //       this.isInvoiceDataPairFlag = false;
    //       this.oldOfferPriceSTBFlag = false;
    //       this.oldOfferPriceCardFlag = false;
    //       this.oldOfferBasedDiscountPairFlag = true;
    //     }
    // }
    modalOpenParentCustomer(type) {
        this.displaySelectParentCustomer = true;
        this.selectCustomerDialogRef = this.dialog.open(this.selectCustomerDialog, {
            width: '800px',
        });
        this.showItemPerPage = 5;
        this.newFirst = 1;
        this.parentCustomerListdataitemsPerPage = 5;
        this.parentCustomerDialogType = type;
        this.getParentCustomerData("");
        this.selectedParentCust = [];
    }

    getParentCustomerData(list) {
        let size: number;

        let currentPage;
        currentPage = this.currentPageParentCustomerListdata;
        if (list) {
            size = list;
            this.parentCustomerListdataitemsPerPage = list;
        } else {
            size = this.parentCustomerListdataitemsPerPage;
        }
        const data = {
            page: currentPage,
            pageSize: this.parentCustomerListdataitemsPerPage
        };
        const url = "/parentCustomers/list/" + this.custType;
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.prepaidParentCustomerList = response.parentCustomerList;
                const list = this.prepaidParentCustomerList;
                const filterList = list.filter(cust => cust.id !== this.editCustomerId);

                this.prepaidParentCustomerList = filterList;

                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    removeSelParentCust(type) {
        this.selectedParentCust = [];
        if (type === "billable") {
            this.billableCusList = [];
            this.inventoryAssignForm.patchValue({
                billabecustId: null
            });
            this.planInventoryAssignForm.patchValue({
                billabecustId: null
            });
            this.replaceInventoryForm.patchValue({
                billabecustId: null
            });
        }
    }

    async saveSelCustomer() {
        if (this.parentCustomerDialogType === "billable") {
            this.billableCusList = [
                {
                    id: this.selectedParentCust.id,
                    name: this.selectedParentCust.name
                }
            ];
            this.inventoryAssignForm.patchValue({
                billabecustId: this.selectedParentCust.id
            });
            this.planInventoryAssignForm.patchValue({
                billabecustId: this.selectedParentCust.id
            });
            this.replaceInventoryForm.patchValue({
                billabecustId: this.selectedParentCust.id
            });
        } else {
            this.parentCustList = [
                {
                    id: this.selectedParentCust.id,
                    name: this.selectedParentCust.name
                }
            ];
            this.inventoryAssignForm.patchValue({
                parentCustomerId: this.selectedParentCust.id
            });
            this.planInventoryAssignForm.patchValue({
                parentCustomerId: this.selectedParentCust.id
            });
            this.replaceInventoryForm.patchValue({
                parentCustomerId: this.selectedParentCust.id
            });
            const url = "/customers/" + this.selectedParentCust.id;
            let parentCustServiceAreaId: any;

            await this.customerInventoryManagementService.getMethod(url).subscribe((response: any) => {
                parentCustServiceAreaId = response.customers.serviceareaid;
            });
        }
        this.modalCloseParentCustomer();
    }
    modalCloseParentCustomer() {
        this.displaySelectParentCustomer = false;
        this.selectCustomerDialogRef.close();
        this.currentPageParentCustomerListdata = 1;
        this.newFirst = 0;
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
    }

    searchParentCustomer() {
        this.newFirst = 1;
        if (this.showItemPerPage) {
            this.parentCustomerListdataitemsPerPage = this.showItemPerPage;
        }
        const searchParentData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage
        };
        searchParentData.filters[0].filterValue = this.searchParentCustValue;
        searchParentData.filters[0].filterColumn = this.searchParentCustOption.trim();
        const url = "/parentCustomers/search/" + RadiusConstants.CUSTOMER_TYPE.PREPAID;
        // console.log("this.searchData", this.searchData)
        this.customerManagementService.postMethod(url, searchParentData).subscribe(
            (response: any) => {
                if (response.status == 204) {
                    this.toastr.info(`${response.msg}`, 'Info!');

                    // this.customerListData = [];
                    this.parentCustomerListdatatotalRecords = 0;
                } else {
                    this.prepaidParentCustomerList = response.parentCustomerList;
                    const list = this.prepaidParentCustomerList;
                    const filterList = list.filter(cust => cust.id !== this.editCustomerId);
                    this.prepaidParentCustomerList = filterList;
                    this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                }
            },
            (error: any) => {
                this.parentCustomerListdatatotalRecords = 0;
                this.prepaidParentCustomerList = [];
                if (error.error.status == 404) {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');

                    this.customerListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }

    clearSearchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
        this.newFirst = 0;
        this.getParentCustomerData("");
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
    }

    selParentSearchOption(event) {
        this.currentPageParentCustomerListdata = 1;
        if (event.value) {
            this.parentFieldEnable = true;
        } else {
            this.parentFieldEnable = false;
        }
    }

    // Bill to Serialized Single Item Replace Inventory
    getProductDetailsReplace(product) {
        if (this.selItemCondition == "New") {
            this.oldOfferOtherSigleReplace = product.newProductAmount;
            this.newOfferOtherSigleReplace = product.newProductAmount;
            // let offerPrice = Number(this.oldOfferOtherSigle);
            // let newOfferPrice = Number(this.newOfferOtherSigleReplace);
            // let discount = Number(this.connectionDetailData[0].discount);
            this.selectedCustDiscount = this.custDiscount;
        }
        if (this.selItemCondition == "Refurbished") {
            this.oldOfferOtherSigleReplace = product.refurburshiedProductAmount;
            this.newOfferOtherSigleReplace = product.refurburshiedProductAmount;
            // let offerPrice = Number(this.oldOfferOtherSigle);
            // let newOfferPrice = Number(this.newOfferOtherSigleReplace);
            // let discount = Number(this.connectionDetailData[0].discount);
            this.selectedCustDiscount = this.custDiscount;
        }
    }

    selectBillToSingleReplace(event) {
        // this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountSingleReplaceFlag = false;
        this.oldOfferPriceSingleReplaceFlag = false;
        // this.oldOfferPriceSingleFlag = false;
        if (event.value == "ORGANIZATION") {
            this.isInvoiceDataSingleReplaceFlag = true;
            this.oldOfferPriceSingleReplaceFlag = true;
            // this.oldOfferBasedDiscountSingleFlag = false;
            this.oldOfferBasedDiscountSingleReplaceFlag = false;
            if (this.oldOfferOtherSigle == null) {
                this.newOfferSingleFlag = false;
            } else {
                this.newOfferSingleFlag = true;
            }
        } else {
            this.isInvoiceDataSingleReplaceFlag = false;
            this.oldOfferPriceSingleReplaceFlag = false;
            // this.oldOfferBasedDiscountSingleFlag = true;
            this.oldOfferBasedDiscountSingleReplaceFlag = false;
            if (this.oldOfferOtherSigle == null) {
                this.newOfferSingleFlag = false;
            } else {
                this.newOfferSingleFlag = true;
            }
        }
    }

    selectItemConditionReplace(event) {
        if (event.value == "New") {
            // this.selItemCondition = event.value;
            this.selItemConditionReplace = event.value;
            this.replaceInventoryForm.get("productId").reset();
            this.getAllSerializedProductFlag = true;
            this.macReplaceListFlag = false;
            // this.billToSigleFlag = false;
            // this.isInvoiceDataSingleFlag = false;
            // this.oldOfferPriceSingleFlag = false;
            // this.oldOfferBasedDiscountSingleFlag = false;
            this.billToSigleReplaceFlag = false;
            this.isInvoiceDataSingleReplaceFlag = false;
            this.oldOfferPriceSingleReplaceFlag = false;
            this.oldOfferBasedDiscountSingleReplaceFlag = false;
        } else {
            this.selItemConditionReplace = event.value;
            this.replaceInventoryForm.get("productId").reset();
            this.getAllSerializedProductFlag = true;
            this.macReplaceListFlag = false;
            // this.billToSigleFlag = false;
            // this.isInvoiceDataSingleFlag = false;
            // this.oldOfferPriceSingleFlag = false;
            // this.oldOfferBasedDiscountSingleFlag = false;
            this.billToSigleReplaceFlag = false;
            this.isInvoiceDataSingleReplaceFlag = false;
            this.oldOfferPriceSingleReplaceFlag = false;
            this.oldOfferBasedDiscountSingleReplaceFlag = false;
        }
    }

    selectItemConditionPair(event) {
        this.getSplitterFlag = true;
        this.billToPairFlag = false;
        this.discountPairFlag = true;
        if (event.value == "New") {
            this.selItemCondition = event.value;
            this.inventoryAssignForm.get("productId").reset();
            this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
            // this.getAllSerializedProductFlag = true;
            this.getAllPairProductFlag = true;
            this.getAllPairItemMacFlag = false;
            this.getAllSingleItemMacFlag = false;
            // this.billToPairFlag = false;
            this.isInvoiceDataPairFlag = false;
            this.oldOfferPriceSTBFlag = false;
            this.oldOfferPriceCardFlag = false;
            this.oldOfferBasedDiscountSingleFlag = false;
            this.oldOfferBasedDiscountSTBPairFlag = false;
            this.oldOfferBasedDiscountCardPairFlag = false;
        } else {
            this.selItemCondition = event.value;
            this.inventoryAssignForm.get("productId").reset();
            this.inventoryAssignForm.controls.billTo.setValue("CUSTOMER");
            // this.getAllSerializedProductFlag = true;
            this.getAllPairProductFlag = true;
            this.getAllPairItemMacFlag = false;
            this.getAllSingleItemMacFlag = false;
            // this.billToPairFlag = false;
            this.isInvoiceDataPairFlag = false;
            this.oldOfferPriceSTBFlag = false;
            this.oldOfferPriceCardFlag = false;
            this.oldOfferBasedDiscountSingleFlag = false;
            this.oldOfferBasedDiscountSTBPairFlag = false;
            this.oldOfferBasedDiscountCardPairFlag = false;
        }
    }

    //Workflow for approve assign inventory
    approveAssignInventoryOpen(mappingId, nextApproverId, id) {
        this.approved = false;
        this.selectAssignInventoryApproveStaff = null;
        this.approveAssignInventoryData = [];
        // $("#assignApproveOtherInventoryOpen").modal("show");
        this.approveAssignInventoryDialogRef = this.dialog.open(this.approveAssignInventoryDialog, {
            width: '55%',
            disableClose: true
        });
        this.approveAssignInventoryForm.reset();
        this.assignInventoryId = mappingId;
        this.customerInventoryId = id;
        this.nextApproverId = nextApproverId;
        this.rejectAssignInventoryForm.reset();
        this.rejectAssignInventorySubmitted = false;
    }

    clearapproveInventory() {
        this.approveAssignInventoryForm.reset();
    }
    clearassignToStaff() {
        this.rejectAssignInventoryForm.reset();
    }

    //Workflow for reject assign inventory
    seletedStaffReplace: any;
    rejectPlanData: any;
    rejectAssignInventoryOpen(mappingId, nextApproverId, id) {
        this.reject = false;
        this.selectAssignInventoryRejectStaff = null;
        this.rejectAssignInventoryData = [];
        // $("#assignRejectOtherInventoryOpen").modal("show");
        this.rejectAssignInventoryForm.reset();
        this.assignInventoryId = mappingId;
        this.nextApproverId = nextApproverId;
        this.customerInventoryId = id;
        // this.rejectAssignInventoryForm.reset();
        // this.rejectAssignInventorySubmitted = false;
        this.rejectAssignInventoryDialogRef = this.dialog.open(this.rejectAssignInventoryDialog, {
            width: '60%',
            disableClose: true,
        });
    }


    assignToStaffReplace() {
        let url: any;

        if (this.seletedStaffReplace) {
            url = `/inwards/assignFromStaffList?entityId=${this.replaceInventoryIdInOutMacMapping
                }&eventName=${"CUSTOMER_INVENTORY_ASSIGN_REPLACE"}&nextAssignStaff=${this.seletedStaffReplace
                }&isApproveRequest=${this.isApproveRequest}&isAssignPairItem=false`;
        } else {
            url = `/teamHierarchy/assignEveryStaff?entityId=${this.replaceInventoryIdInOutMacMapping
                }&eventName=${"CUSTOMER_INVENTORY_ASSIGN_REPLACE"}&isApproveRequest=${this.isApproveRequest}`;
        }

        this.customerInventoryManagementService.getMethod(url).subscribe(
            response => {
                this.replaceAssignForm.get("remark").reset();
                $("#approvalReplaceInventory").modal("hide");
                // $("#assignRejectOtherInventoryOpen").modal("hide");
                this.editInventory = false;
                this.editSTBCradInventory = false;
                this.getCustomerAssignedList();
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    paginate(event) {
        this.newFirst = event.first;
        this.macAddressListdataitemsPerPage = event.rows;
        this.currentMacAddressListdata = event.page + 1;

        const obj = {
            value: this.productMacAddressId
        };
        this.getReplaceLevelMacAddressList(obj);
    }

    assignToStaff(flag) {
        let url: any;
        if (flag == true) {
            if (this.selectAssignInventoryApproveStaff) {
                if (this.assignInventoryId != this.customerInventoryId) {
                    url = `/inwards/assignFromStaffList?entityId=${this.assignInventoryId
                        }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectAssignInventoryApproveStaff
                        }&isApproveRequest=${flag}&isAssignPairItem=true`;
                } else {
                    url = `/inwards/assignFromStaffList?entityId=${this.assignInventoryId
                        }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectAssignInventoryApproveStaff
                        }&isApproveRequest=${flag}&isAssignPairItem=false`;
                }
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignInventoryId
                    }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&isApproveRequest=${flag}`;
            }
        } else {
            if (this.selectAssignInventoryRejectStaff) {
                if (this.assignInventoryId != this.customerInventoryId) {
                    url = `/inwards/assignFromStaffList?entityId=${this.assignInventoryId
                        }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectAssignInventoryRejectStaff
                        }&isApproveRequest=${flag}&isAssignPairItem=true`;
                } else {
                    url = `/inwards/assignFromStaffList?entityId=${this.assignInventoryId
                        }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectAssignInventoryRejectStaff
                        }&isApproveRequest=${flag}&isAssignPairItem=false`;
                }
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignInventoryId
                    }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&isApproveRequest=${flag}`;
            }
        }

        this.customerInventoryManagementService.getMethod(url).subscribe(
            response => {
                // $("#assignApproveOtherInventoryOpen").modal("hide");
                this.approveAssignInventoryDialogRef.close();
                // $("#assignRejectOtherInventoryOpen").modal("hide");
                this.rejectAssignInventoryDialogRef.close();
                this.getCustomerAssignedList();
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    // offerprice validation
    newOfferPriceValidation(input) {
        var num = String.fromCharCode(input.which);
        if (!/[0-9]/.test(num)) {
            input.preventDefault();
        }
    }
    //Disable Required Approval for Single Item
    selInvoiceToOrgSingle(event) {
        if (event.value == true) {
            this.requiredApprovalSingleFlag = true;
        } else {
            this.requiredApprovalSingleFlag = false;
        }
    }
    //Disable Required Approval for Pair Item
    selInvoiceToPair(event) {
        if (event.value == true) {
            this.requiredApprovalPairFlag = true;
        } else {
            this.requiredApprovalPairFlag = false;
        }
    }
    //Disable Required Approval for Non Serial Item
    selInvoiceToOrgNonSerial(event) {
        if (event.value == true) {
            this.requiredApprovalNonSerialFlag = true;
        } else {
            this.requiredApprovalNonSerialFlag = false;
        }
    }
    //Disable Required Approval for Plan Item
    selInvoiceToOrgPlan(event) {
        if (event.value == true) {
            this.requiredApprovalPlanFlag = true;
        } else {
            this.requiredApprovalPlanFlag = false;
        }
    }

    //Workflow Function for Approve Remove Inventory
    approveRemoveInventoryOpen(inventory, nextApproverId) {
        this.approveRemove = false;
        this.selectRemoveInventoryApproveStaff = null;
        this.approveRemoveInventoryData = [];
        // $("#approveRemoveInventoryOpenModel").modal("show");
        this.approveRemoveInventoryDialogRef = this.dialog.open(this.approveRemoveInventoryDialog, {
            width: '600px',
            disableClose: true
        });
        this.approveRemoveInventoryForm.reset();
        this.assignRemoveInventoryId = inventory.id;
        this.macMappingId = inventory.inOutWardMACMapping[0].id;
        this.custInventoryId = inventory.id;
        this.nextApproverId = nextApproverId;
        this.rejectRemoveInventoryForm.reset();
        this.rejectRemoveInventorySubmitted = false;
    }

    clearapproveremoveInventory() {
        this.approveRemoveInventoryForm.reset();
    }

    approveRemoveInventory(): void {
        this.assignRemoveInventorysubmitted = true;
        let mappingId = this.macMappingId;
        let custInventoryId = this.custInventoryId;
        // const ownershipFlag = this.ownershipForm.value;
        const removeRemark = this.approveRemoveInventoryForm.value;
        let staffId = localStorage.getItem("userId");
        // const url = `/inwards/approveInventory?isApproveRequest=true&customerInventoryMappingId=${id}`;
        const url = `/inoutWardMacMapping/removeInventory?&macMappingId=${mappingId}&customerInventoryId=${custInventoryId}&customerId=${this.custData.id}&isApprove=true&nextstaff=${staffId}&remark=${removeRemark.remark}`;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200 || response.responseCode == 0) {
                    this.assignRemoveInventorysubmitted = false;
                    this.approveRemoveInventoryForm.reset();
                    if (response.dataList != null) {
                        this.approveRemoveInventoryData = response.dataList;
                        this.approveRemove = true;
                    } else {
                        // $("#approveRemoveInventoryOpenModel").modal("hide");
                        this.approveRemoveInventoryDialogRef.close();

                    }
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.getCustomerAssignedList();

                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    //Workflow Function for Reject Remove Inventory
    rejectRemoveInventoryOpen(inventory, nextApproverId) {
        this.rejectRemove = false;
        this.selectRemoveInventoryRejectStaff = null;
        this.rejectRemoveInventoryData = [];
        // $("#rejectRemoveInventoryOpenModel").modal("show");
        this.rejectRemoveInventoryDialogRef = this.dialog.open(this.rejectRemoveInventoryDialog, {
            width: '600px',
            disableClose: true,
        });
        this.rejectRemoveInventoryForm.reset();
        // this.assignRemoveInventoryId = mappingId;
        this.assignRemoveInventoryId = inventory.id;
        this.macMappingId = inventory.inOutWardMACMapping[0].id;
        this.custInventoryId = inventory.id;
        this.nextApproverId = nextApproverId;
        // this.rejectAssignInventoryForm.reset();
        this.rejectRemoveInventorySubmitted = false;
    }
    clearassignRemoveToStaff() {
        this.rejectRemoveInventoryForm.reset();
    }

    // Get Customer Inventory Mapping Details
    getCustomerInventoryMappingDetails(itemId, custinventoryid) {
        const url = `/item/getItemDetails?&itemId=${itemId}&custinventoryid=${custinventoryid}`;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.itemDetailData = response.data;
                    if (this.itemDetailData.refundFlag == true) {
                        if (
                            this.itemDetailData.warranty == "InWarranty" ||
                            this.itemDetailData.warranty == "Expired"
                        ) {
                            this.refundAmountForm.patchValue({
                                actualRefundPrice: this.itemDetailData.productRefundAmount,
                                newRefundAmount: this.itemDetailData.productRefundAmount
                            });
                            this.refundAmountForm.get('actualRefundPrice')?.disable();
                            this.dialog.open(this.refundDialogTemplate, {
                                width: '600px'
                            });
                        } else {
                            this.acceptRemoveItem();
                        }
                    } else {
                        this.acceptRemoveItem();
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    rejectRemoveInventory(): void {
        this.rejectRemoveInventorySubmitted = true;
        let mappingId = this.macMappingId;
        let custInventoryId = this.custInventoryId;
        // const ownershipFlag = this.ownershipForm.value;
        const removeRemark = this.rejectRemoveInventoryForm.value;
        let staffId = localStorage.getItem("userId");
        // const url = `/inwards/approveInventory?isApproveRequest=true&customerInventoryMappingId=${id}`;
        const url = `/inoutWardMacMapping/removeInventory?&macMappingId=${mappingId}&customerInventoryId=${custInventoryId}&customerId=${this.custData.id}&isApprove=false&nextstaff=${staffId}&remark=${removeRemark.remark}`;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200 || response.responseCode == 0) {
                    this.rejectRemoveInventorySubmitted = false;
                    this.rejectRemoveInventoryForm.reset();
                    if (response.dataList != null) {
                        this.rejectRemoveInventoryData = response.dataList;
                        this.rejectRemove = true;
                    } else {
                        // $("#rejectRemoveInventoryOpenModel").modal("hide");
                        this.rejectRemoveInventoryDialogRef.close();

                    }
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.getCustomerAssignedList();
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    // rejectRemoveInventory(): void {
    //   let mappingId = this.macMappingId;
    //   let custInventoryId = this.custInventoryId;
    //   // let ownershipFlag = this.ownershipFlag;
    //   const removeRemark = this.rejectRemoveInventoryForm.value;
    //   let staffId = localStorage.getItem("userId");
    //   //const url = `/inwards/approveInventory?isApproveRequest=false&customerInventoryMappingId=${id}`;
    //   const url = `/inoutWardMacMapping/removeInventory?&macMappingId=${mappingId}&customerInventoryId=${custInventoryId}&customerId=${this.custData.id}&isApprove=false&nextstaff=${staffId}&remark=${removeRemark.remark}`;
    //
    //   this.customerInventoryManagementService.getMethod(url).subscribe(
    //     (response: any) => {
    //       if (response.responseCode == 200 || response.responseCode == 0) {
    //         this.rejectRemoveInventorySubmitted = false;
    //         this.rejectRemoveInventoryForm.reset();
    //         if (response.dataList != null) {
    //           this.rejectRemoveInventoryData = response.dataList;
    //           this.rejectRemove = true;
    //         } else {
    //           $("#rejectRemoveInventoryOpenModel").modal("hide");
    //           this.getCustomerAssignedList();
    //         }
    //         this.messageService.add({
    //           severity: "success",
    //           summary: "success",
    //           detail: response.responseMessage,
    //           icon: "far fa-times-circle",
    //         });
    //
    //       } else {
    //
    //         this.messageService.add({
    //           severity: "error",
    //           summary: "Error",
    //           detail: response.responseMessage,
    //           icon: "far fa-times-circle",
    //         });
    //       }
    //     },
    //     (error: any) => {
    //       this.messageService.add({
    //         severity: "error",
    //         summary: "Error",
    //         detail: error.error.msg,
    //         icon: "far fa-times-circle",
    //       });
    //
    //     }
    //   );
    // }

    assignRemoveInventoryToStaff(flag) {
        let url: any;
        if (flag == true) {
            if (this.selectRemoveInventoryApproveStaff) {
                url = `/inwards/assignFromStaffList?entityId=${this.assignRemoveInventoryId
                    }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectRemoveInventoryApproveStaff
                    }&isApproveRequest=${flag}&isAssignPairItem=false`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignRemoveInventoryId
                    }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&isApproveRequest=${flag}`;
            }
        } else {
            if (this.selectRemoveInventoryRejectStaff) {
                url = `/inwards/assignFromStaffList?entityId=${this.assignRemoveInventoryId
                    }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&nextAssignStaff=${this.selectRemoveInventoryRejectStaff
                    }&isApproveRequest=${flag}&isAssignPairItem=false`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.assignRemoveInventoryId
                    }&eventName=${"CUSTOMER_INVENTORY_ASSIGN"}&isApproveRequest=${flag}`;
            }
        }

        this.customerInventoryManagementService.getMethod(url).subscribe(
            response => {
                // $("#approveRemoveInventoryOpenModel").modal("hide");
                this.approveRemoveInventoryDialogRef.close();

                // $("#rejectRemoveInventoryOpenModel").modal("hide");
                this.rejectRemoveInventoryDialogRef.close();
                this.getCustomerAssignedList();
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    isDisableRemove(inventory) {
        var service = this.serviceList.find(item => item.connection_no == inventory.connectionNo);
        if (
            service != null &&
            (service.custPlanStatus.toLowerCase() === "inactive" ||
                service.custPlanStatus.toLowerCase() === "disable" ||
                service.custPlanStatus.toLowerCase() === "suspend" ||
                service.custPlanStatus.toLowerCase() === "stop" ||
                service.custPlanStatus.toLowerCase() === "terminate")
        ) {
            return false;
        } else return true;
    }

    isDisableReplace(inventory) {
        var service = this.serviceList.find(item => item.connection_no == inventory.connectionNo);
        if (
            service != null &&
            (service.custPlanStatus.toLowerCase() === "active" ||
                service.custPlanStatus.toLowerCase() === "ingrace")
        ) {
            return false;
        } else return true;
    }

    isDisableReactiveBox(inventory) {
        var service = this.serviceList.find(item => item.connection_no == inventory.connectionNo);
        if (
            service != null &&
            (service.custPlanStatus.toLowerCase() === "active" ||
                service.custPlanStatus.toLowerCase() === "ingrace")
        ) {
            return false;
        } else return true;
    }
    onChildSerChange(value) {
        this.filtteredChildConnection1 = this.masterChildConnections.filter(
            item => item.connectionNo != value
        );
    }

    onChildSerChange1(value) {
        this.filtteredChildConnection = this.masterChildConnections.filter(
            item => item.connectionNo != value
        );
    }

    selectedStaff: any = [];
    selectStaffType = "";
    staffSelectList: any = [];

    modalOpenSelectStaff(type) {
        this.showSelectStaffModel = true;
        this.parentCustomerDialogType = type;
        this.selectedStaff = [];
        this.selectStaffType = type;
        const dialogRef = this.dialog.open(SelectStaffComponent, {
            width: '75%',
            disableClose: true,
        });
        dialogRef.afterClosed().subscribe((result) => {

            if (result) {
                const selectedStaffs = result;
                this.staffSelectList.push({
                    id: Number(selectedStaffs.id),
                    name: selectedStaffs.firstname
                });

                if (this.selectStaffType == "inventoryAssign") {
                    this.inventoryAssignForm.patchValue({
                        paymentOwnerId: selectedStaffs.id
                    });
                } else if (this.selectStaffType == "planInventoryAssign") {
                    this.planInventoryAssignForm.patchValue({
                        paymentOwnerId: selectedStaffs.id
                    });
                }
            } else {
            }
        });
    }

    closeSelectStaff() {
        this.showSelectStaffModel = false;
    }

    selectedStaffChange(event) {
        this.showSelectStaffModel = false;
        let data = event;
        this.staffSelectList.push({
            id: Number(data.id),
            name: data.firstname
        });

        if (this.selectStaffType == "inventoryAssign") {
            this.inventoryAssignForm.patchValue({
                paymentOwnerId: data.id
            });
        } else if (this.selectStaffType == "planInventoryAssign") {
            this.planInventoryAssignForm.patchValue({
                paymentOwnerId: data.id
            });
        }
    }

    removePlanSelectStaff() {
        this.staffSelectList = [];
        this.planInventoryAssignForm.get("paymentOwnerId").reset();
    }
    removeOtherSelectStaff() {
        this.staffSelectList = [];
        this.inventoryAssignForm.get("paymentOwnerId").reset();
    }

    editProductParams(inventory) {
        this.inventorySpecificationParamModal = true;
        this.productData = inventory;
        const dialogRef = this.dialog.open(CustomerInventorySpecificationParamsComponent, {
            width: '75%',
            disableClose: true,
            data: { productData: inventory }
        });
        dialogRef.afterClosed().subscribe(() => {
            // You can refresh list or perform additional actions here
        });
    }

    closeInventorySpecModel() {
        this.inventorySpecificationParamModal = false;
    }

    reActivate(inventory) {
        const url = `/reactivateService?custId=${inventory.customerId}&custServiceId=${inventory.custServiceMapId}`;
        let data = {};
        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (response.data) {
                        this.toastr.success(`Re-activate Sucessfully`, 'Success!');

                    } else {
                        this.toastr.error(`Something went wrong!!!`, 'Failed!');

                    }
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    viewSpecificationParameters(id, data) {
        this.inventorySpecificationDetailModal = true;
        this.inventorySpecificationDialogRef = this.dialog.open(this.inventorySpecificationDialog, {
            width: '75%',
            disableClose: true,
            panelClass: 'custom-mat-dialog',
        });
    }

    closeInventorySpecificationDetailModal() {
        this.inventorySpecificationDetailModal = false;
        this.inventorySpecificationDialogRef.close();
    }

    isEditing(rowIndex: number): boolean {
        return rowIndex === this.editedRowIndex;
    }

    //   editValue(rowIndex: number) {
    //     this.editedRowIndex = rowIndex;
    //   }

    addOrEditValue(rowIndex: number, id: any, newValue: string, param: any) {
        if (this.editedRowIndex !== -1) {
            this.editedRowIndex = -1;
        } else {
            this.inventorySpecificationDetails.push({
                paramName: "",
                isMandatory: false,
                paramValue: newValue,
                isMultiValueParam: param.isMultiValueParam,
                multiValue: param.multiValue
            });
        }
    }

    addSpecificationParamDetails(product: any) {
        this.inventorySpecificationDetails = [];
        this.inwardService.getByItemId(product.itemId).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.specDetailsShow = true;
                    this.inventorySpecificationDetails = response.dataList;

                    this.inventorySpecificationDetails.map(item => {
                        if (item.isMultiValueParam) {
                            item.multiValue = item.paramMultiValues.map(value => ({
                                label: value,
                                value: value
                            }));
                        }
                        return item;
                    });
                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    handleKeyDown(event: KeyboardEvent) {
        let maxValue: number = Number(this.actualProductPrice);

        const inputElement = event.target as HTMLInputElement;
        if (
            event.keyCode === 8 ||
            (event.key >= "0" && event.key <= "9") ||
            (event.key === "." && inputElement.value.indexOf(".") === -1) // Allow only one decimal point
        ) {
            if (parseFloat(inputElement.value + event.key) <= maxValue) {
                return true; // Allow the input
            } else {
                return false; // Prevent the input
            }
        } else {
            return false; // Prevent the input for other keys
        }
    }

    closeInventoryWorkflowModel() {
        // $("#workflowInventoryModal").modal("hide");
        this.inventoryWorkflowDialogRef.close()
        this.inventoryId = "";
        this.currentPageInventoryApproveProgress = 1;
    }

    uploadDocument(inventory) {
        this.inventoryIdData = inventory.id;
        this.inventoryFileData = inventory;
        this.activeTabIndex = 0;
        this.uploadDocForm.forEach((formGroup, tabIndex) => {
            formGroup.patchValue({
                sectionName: this.tabs[tabIndex]
            });
        });
        this.selectedFileUploadPreview = [];
        this.uploadDocumentId = true;
        this.uploadDocumentDialogRef = this.dialog.open(this.uploadDocumentDialog, {
            width: '75%',

            disableClose: true,
        });



    }
    // onFileChangeUpload(event: any): void {
    //     this.selectedFileUploadPreview = [];
    //     const inputElement = event.target as HTMLInputElement;
    //     if (inputElement.files && inputElement.files.length > 0) {
    //         const files: FileList = inputElement.files;

    //         // Validate all files
    //         for (let i = 0; i < files.length; i++) {
    //             const file = files.item(i);
    //             if (
    //                 file &&
    //                 (file.type === "image/png" ||
    //                     file.type === "image/jpg" ||
    //                     file.type === "image/jpeg" ||
    //                     file.type === "application/pdf")
    //             ) {
    //                 this.selectedFileUploadPreview.push(file);
    //             } else {
    //                 this.messageService.add({
    //                     severity: "info",
    //                     summary: "Info",
    //                     detail: `Invalid file type: ${file?.name}. Must be png, jpg, jpeg, or pdf.`,
    //                     icon: "far fa-check-circle"
    //                 });
    //             }
    //         }

    //         if (this.selectedFileUploadPreview.length > 0) {
    //             // If valid files exist, patch the first file to the form
    //             this.multiFiles = this.createFileList(this.selectedFileUploadPreview);
    //             this.selectedFile = this.selectedFileUploadPreview[0];
    //             this.uploadDocForm.patchValue({
    //                 file: this.multiFiles[0]
    //             });
    //         } else {
    //             // Reset form control and input if no valid files
    //             this.uploadDocForm.controls.file.reset();
    //             inputElement.value = "";
    //         }
    //     }
    // }

    // deletUploadedFile(event: any) {
    //     var temp: File[] = this.selectedFileUploadPreview?.filter((item: File) => item?.name != event);
    //     this.selectedFileUploadPreview = temp;
    //     this.uploadDocForm.patchValue({
    //         file: temp
    //     });
    // }

    // closeUploadDocumentId() {
    //     this.uploadDocumentId = false;
    //     this.uploadDocForm.patchValue({
    //         file: ""
    //     });
    //     this.selectedFileUploadPreview = [];
    // }

    // uploadDocuments() {
    //     this.submitted = true;
    //     if (this.uploadDocForm.valid) {
    //         const formData = new FormData();
    //         let fileArray: FileList;
    //         if (this.uploadDocForm.controls.file) {
    //             if (
    //                 this.selectedFile.type != "image/png" &&
    //                 this.selectedFile.type != "image/jpg" &&
    //                 this.selectedFile.type != "image/jpeg" &&
    //                 this.selectedFile.type != "application/pdf"
    //             ) {
    //                 this.uploadDocForm.controls.file.reset();
    //                 // alert("File type must be png, jpg, jpeg or pdf");
    //                 this.messageService.add({
    //                     severity: "info",
    //                     summary: "Info",
    //                     detail: "File type must be png, jpg, jpeg or pdf",
    //                     icon: "far fa-check-circle"
    //                 });
    //             } else {
    //                 fileArray = this.multiFiles;
    //                 Array.from(fileArray).forEach(file => {
    //                     formData.append("file", file);
    //                 });
    //                 // const file = this.uploadDocForm.controls.file;
    //                 // this.uploadDocForm.patchValue({
    //                 //     file: file
    //                 // });
    //             }
    //         }
    //         let newFormData = Object.assign({}, this.inventoryFileData);
    //         formData.append("customerInventoryMappingList", JSON.stringify(newFormData));
    //         const url = `/inwards/inventory/document/upload/`;
    //         this.customerInventoryManagementService.postMethod(url, formData).subscribe(
    //             (response: any) => {
    //                 if (response.responseCode === 406) {
    //                     this.messageService.add({
    //                         severity: "error",
    //                         summary: "Error",
    //                         detail: response.responseMessage,
    //                         icon: "far fa-times-circle"
    //                     });
    //                 } else if (response.responseCode === 417) {
    //                     this.messageService.add({
    //                         severity: "error",
    //                         summary: "Error",
    //                         detail: response.responseMessage,
    //                         icon: "far fa-times-circle"
    //                     });
    //                 } else {
    //                     // this.openTicketDetail(this.uploadDataTicketId);
    //                     this.getCustomerAssignedList();
    //                     this.submitted = false;
    //                     this.messageService.add({
    //                         severity: "success",
    //                         summary: "Successfully",
    //                         detail: response.message,
    //                         icon: "far fa-check-circle"
    //                     });
    //                     this.uploadDocumentId = false;
    //                 }
    //             },
    //             (error: any) => {
    //                 console.log(error, "error");
    //                 this.messageService.add({
    //                     severity: "error",
    //                     summary: "Error",
    //                     detail: error.error.ERROR,
    //                     icon: "far fa-times-circle"
    //                 });
    //             }
    //         );
    //     }
    // }

    downloadDocument(inventory) {
        this.inventoryIdData = inventory.id;
        // this.inventoryFileData = inventory;
        // this.inventoryFileData.filenameList = this.inventoryFileData.filename.split(",");
        // this.inventoryFileData.uniqueNamesList = this.inventoryFileData.uniquename.split(",");
        // if (
        //     this.inventoryFileData.filenameList.length !== this.inventoryFileData.uniqueNamesList.length
        // ) {
        //     console.error("The number of filenames and unique names do not match!");
        //     return;
        // }
        // this.inventoryFileData.fileDetails = [];

        // this.inventoryFileData.filenameList.forEach((filename, index) => {
        //     const fileDetail = {
        //         filename: filename.trim(),
        //         uniquename: this.inventoryFileData.uniqueNamesList[index].trim()
        //     };

        //     this.inventoryFileData.fileDetails.push(fileDetail);
        // });
        let url = "/inwards/inventory/documentList/" + this.inventoryIdData;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.inventoryFileDocData = response.dataList;
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.toast.success(`${response.responseMessage}`, 'Success');
                    this.downloadDocumentId = true;
                    this.dialog.open(this.downloadDocumentDailog, {
                        width: '75%',
                    });
                    this.activeTabViewIndex = 0;
                } else if (response.responseCode == 404) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    closeDownloadDocumentId() {
        this.downloadDocumentId = false;
        this.activeTabViewIndex = 0;
        this.getCustomerAssignedList();
    }

    downloadDoc(fileName, section, sectionName) {
        let inventoryId = section.customerInventoryId;
        let uniqueName = section.uniqueName;
        this.customerInventoryManagementService
            .downloadFile(inventoryId, uniqueName, sectionName)
            .subscribe(
                blob => {
                    if (blob.status == 200) {
                        this.toast.success(`Download Successfully`, 'Success');
                        importedSaveAs(blob.body, fileName);
                    } else if (blob.status == 404) {
                        this.toastr.error(`File Not Found`, 'Failed!');

                    } else {
                        this.toastr.error(`Something went wrong!`, 'Failed!');


                    }
                    this.getCustomerAssignedList();
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
    }

    deleteDoc(fileName, section, sectionName) {
        let inventoryId = section.customerInventoryId;
        let uniqueName = section.uniqueName;
        let urldoc =
            "/inwards/inventory/document/delete/" +
            inventoryId +
            "/" +
            fileName +
            "/" +
            uniqueName +
            "/" +
            sectionName +
            "/";
        this.customerInventoryManagementService.deleteMethod(urldoc).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`Successfully`, 'Success!');

                    this.closeDownloadDocumentId();
                } else if (response.responseCode == 404) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                }
                this.getCustomerAssignedList();
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    showticketDocData(fileName, section, sectionName) {
        // console.log("data ", data?.filename.split(".")[data?.filename.split(".")?.length - 1]);
        // const url = `/case/document/download/${data.ticketId}/${data.docId}`;
        const fileType = fileName.split(".");
        let inventoryId = section.customerInventoryId;
        let uniqueName = section.uniqueName;
        this.customerInventoryManagementService
            .downloadFile(inventoryId, uniqueName, sectionName)
            .subscribe(
                data => {
                    if (data.status == 200) {
                        // let type = "application/octet-stream"; // default type
                        // const uint = new Uint8Array(data.body);
                        // this.closeDownloadDocumentId();
                        // const magic = uint.subarray(0, 4);
                        // if (magic.every(b => b === 0xff)) {
                        //     type = "image/jpeg";
                        // } else if (magic[0] === 0x89 && magic[1] === 0x50 && magic[2] === 0x4e && magic[3] === 0x47) {
                        //     type = "image/png";
                        // } else if (magic[0] === 0x47 && magic[1] === 0x49 && magic[2] === 0x46 && magic[3] === 0x38) {
                        //     type = "image/gif";
                        // } else if (magic[0] === 0xd0 && magic[1] === 0xcf && magic[2] === 0x11 && magic[3] === 0xe0) {
                        //     type = "application/vnd.ms-excel";
                        // } else if (magic[0] === 0x25 && magic[1] === 0x50 && magic[2] === 0x44 && magic[3] === 0x46) {
                        //     type = "application/pdf";
                        // } else if (magic[0] === 0xd0 && magic[1] === 0xcf && magic[2] === 0x11 && magic[3] === 0xe0) {
                        //     type = "application/msword";
                        // }

                        // if (fileType[fileType?.length - 1] == "pdf") {
                        //     const blob = new Blob([data], { type: "application/pdf" });
                        //     const blobUrl = URL.createObjectURL(blob);
                        //     window.open(blobUrl, "_blank");
                        // } else {
                        //     const blob = new Blob([data], { type });
                        //     const blobUrl = URL.createObjectURL(blob);
                        //     this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
                        //     this.documentPreview = true;
                        // }
                        let type = "application/octet-stream"; // Default type
                        const uint = new Uint8Array(data.body);

                        const magic = uint.subarray(0, 4); // Check the magic bytes to identify the file type

                        if (magic.every(b => b === 0xff)) {
                            type = "image/jpeg";
                        } else if (
                            magic[0] === 0x89 &&
                            magic[1] === 0x50 &&
                            magic[2] === 0x4e &&
                            magic[3] === 0x47
                        ) {
                            type = "image/png";
                        } else if (
                            magic[0] === 0x47 &&
                            magic[1] === 0x49 &&
                            magic[2] === 0x46 &&
                            magic[3] === 0x38
                        ) {
                            type = "image/gif";
                        } else if (
                            magic[0] === 0xd0 &&
                            magic[1] === 0xcf &&
                            magic[2] === 0x11 &&
                            magic[3] === 0xe0
                        ) {
                            type = "application/vnd.ms-excel";
                        } else if (
                            magic[0] === 0x25 &&
                            magic[1] === 0x50 &&
                            magic[2] === 0x44 &&
                            magic[3] === 0x46
                        ) {
                            type = "application/pdf";
                        } else if (
                            magic[0] === 0xd0 &&
                            magic[1] === 0xcf &&
                            magic[2] === 0x11 &&
                            magic[3] === 0xe0
                        ) {
                            type = "application/msword";
                        }

                        if (fileType[fileType?.length - 1] === "pdf") {
                            // If it's a PDF file, create a blob and open it in a new tab
                            const blob = new Blob([data.body], { type: "application/pdf" });
                            const blobUrl = URL.createObjectURL(blob);
                            window.open(blobUrl, "_blank"); // Open PDF in a new tab
                        } else if (fileType[fileType?.length - 1] === "png") {
                            // If it's a PNG image, create a blob URL and display it in an <img> tag
                            const blob = new Blob([data.body], { type: "image/png" });
                            const blobUrl = URL.createObjectURL(blob);
                            this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl); // Trust the blob URL
                            this.documentPreview = true; // Set flag to show the image preview
                            this.dialog.open(this.viewDocumentDailog, {
                                width: '75%',
                            });
                        } else {
                            // For other types (e.g., JPEG, GIF), display as image preview
                            const blob = new Blob([data.body], { type });
                            const blobUrl = URL.createObjectURL(blob);
                            this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl); // Trust the blob URL
                            this.documentPreview = true; // Set flag to show the image preview

                            this.dialog.open(this.viewDocumentDailog, {
                                width: '75%',
                            });
                        }
                    } else if (data.status == 404) {
                        this.toastr.error(`File Not Found`, 'Failed!');


                    } else {
                        this.toastr.error(`Something went wrong!`, 'Failed!');

                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
    }

    closeDocumentPreview() {
        this.documentPreview = false;
    }
    createFileList(files: File[]): FileList {
        const dataTransfer = new DataTransfer();
        files.forEach(file => dataTransfer.items.add(file));
        return dataTransfer.files;
    }

    wifiModalOpen(inventory) {
        this.wifiInventory = inventory;
        this.wifiDialogRef = this.dialog.open(this.wifiDialog, {
            width: '600px',
            disableClose: true,
        });

        let obj = {
            custInvenId: this.wifiInventory.id,
            customerId: this.custData.id,
            itemId: this.wifiInventory.itemId,
            serialNumber: this.wifiInventory.inOutWardMACMapping[0].serialNumber
        };
        let urldoc = "/nmsIntegration/getWifiConfig";
        this.navService.postMethod(urldoc, obj).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.wifiModel = true;
                    if (
                        response.data.ssidUsername !== null &&
                        response.data.ssidPassword !== null &&
                        response.data.workingFrequency !== null
                    ) {
                        this.wifiForm.patchValue({
                            username: response.data.ssidUsername,
                            password: response.data.ssidPassword,
                            workingFrequency: response.data.workingFrequency
                        });
                        this.editWifi = true;
                    } else {
                        this.editWifi = false;
                    }
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    closeWifiModal() {
        this.wifiModel = false;
    }

    editWifiModel() {
        // this.wifiForm.reset();
        this.editWifi = !this.editWifi;
    }

    saveWifi() {
        this.wifiSubmitted = true;
        if (this.wifiForm.valid) {
            let obj = {
                custInvenId: this.wifiInventory.id,
                customerId: this.custData.id,
                itemId: this.wifiInventory.itemId,
                serialNumber: this.wifiInventory.inOutWardMACMapping[0]?.serialNumber,
                ssidPassword: this.wifiForm.value.password,
                ssidUsername: this.wifiForm.value.username,
                workingFrequency: this.wifiForm.value.workingFrequency
            };
            let data = obj;
            let urldoc = "/nmsIntegration/NMSWifiConfig";
            this.navService.postMethod(urldoc, data).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.toastr.success(`${response.responseMessage}`, 'Success!');


                        this.wifiSubmitted = false;
                        this.wifiModel = false;
                        this.wifiForm.reset();
                        // this.wifiDialogRef.close();
                        // this.dialog.closeAll();
                    } else {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');

                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`Failed`, 'Failed!');

                }
            );
        }
    }

    onFileChangeUpload(event: any, tabIndex: number): void {
        this.selectedFileUploadPreview[tabIndex] = [];
        const inputElement = event.target as HTMLInputElement;
        if (inputElement.files && inputElement.files.length > 0) {
            const files: FileList = inputElement.files;

            // Validate all files
            for (let i = 0; i < files.length; i++) {
                const file = files.item(i);
                if (
                    file &&
                    (file.type === "image/png" ||
                        file.type === "image/jpg" ||
                        file.type === "image/jpeg" ||
                        file.type === "application/pdf")
                ) {
                    this.selectedFileUploadPreview[tabIndex].push(file);
                } else {
                    this.toastr.info(`Invalid file type: ${file?.name}. Must be png, jpg, jpeg, or pdf.`, 'Info!');

                }
            }

            if (this.selectedFileUploadPreview[tabIndex].length > 0) {
                // If valid files exist, patch the first file to the form
                this.multiFiles = this.createFileList(this.selectedFileUploadPreview[tabIndex]);
                this.selectedFile = this.selectedFileUploadPreview[tabIndex][0];
                this.uploadDocForm[tabIndex].patchValue({
                    file: this.multiFiles[0]
                });
            } else {
                // Reset form control and input if no valid files
                this.uploadDocForm[tabIndex].controls.file.reset();
                inputElement.value = "";
            }
        }
    }

    deletUploadedFile(fileName: string, tabIndex: number): void {
        const temp: File[] = this.selectedFileUploadPreview[tabIndex]?.filter(
            (item: File) => item?.name !== fileName
        );
        this.selectedFileUploadPreview[tabIndex] = temp;
        this.uploadDocForm[tabIndex].patchValue({
            file: temp
        });
    }

    // closeUploadDocumentId(): void {
    //     this.uploadDocumentId = false;
    //     this.createForm().reset();
    //     this.submitted = false;
    //     this.uploadDocForm.forEach((formGroup) => {
    //         formGroup.reset();
    //     });
    //     // this.uploadDocForm.forEach((formGroup) => {
    //     //     console.log(
    //     //         formGroup
    //     //     );
    //     // })
    //     this.selectedFileUploadPreview = [];
    //      const fileInput = document.getElementById('txtSelectDocument') as HTMLInputElement;
    //      if (fileInput)
    //     {
    //        fileInput.value = '';
    //     }
    // }
    closeUploadDocumentId(): void {
        this.uploadDocumentId = false;
        this.submitted = false;

        // Reset the main form
        this.createForm().reset();

        // Reset all form groups inside uploadDocForm array
        this.uploadDocForm.forEach(formGroup => {
            formGroup.reset();
        });

        // Clear file previews
        this.selectedFileUploadPreview = [];

        // Clear the file input
        const fileInput = document.getElementById("txtSelectDocument") as HTMLInputElement;
        if (fileInput) {
            fileInput.value = ""; // This clears the file input
        }

        // Optional: If your component supports multiple tabIndexes and each has its own preview
        this.tabs.forEach((_, index) => {
            this.selectedFileUploadPreview[index] = [];
            const dynamicFileInput = document.getElementById(
                `txtSelectDocument_${index}`
            ) as HTMLInputElement;
            if (dynamicFileInput) {
                dynamicFileInput.value = "";
            }
        });
    }

    // uploadAllDocuments(): void {
    //     this.submitted = true;
    //     let allSectionsData: any[] = [];
    //     let allFiles: File[] = [];

    //     this.uploadDocForm.forEach((formGroup, tabIndex) => {
    //         formGroup.patchValue({
    //             sectionName: this.tabs[tabIndex]
    //         });

    //         if (formGroup.valid || this.tabs[tabIndex] === 'Optical Power Range') {
    //             if (this.tabs[tabIndex] !== 'Optical Power Range'){
    //                 const sectionData = this.collectSectionData(formGroup, tabIndex);
    //                 if (sectionData) {
    //                     allSectionsData.push(sectionData.section);
    //                     allFiles = [...allFiles, ...sectionData.files];
    //                 }
    //             }

    //         }
    //         // else {
    //         //     this.messageService.add({
    //         //         severity: "error",
    //         //         summary: "Form Invalid",
    //         //         detail: `Please fill out all required fields in ${this.tabs[tabIndex]}`,
    //         //         icon: "far fa-times-circle"
    //         //     });
    //         // }
    //     });

    //     if (allSectionsData.length > 0) {
    //         this.uploadDocuments(allSectionsData, allFiles);
    //     }
    //     // else
    //     // {
    //     //     this.messageService.add({
    //     //         severity: "error",
    //     //         summary: "No Valid Sections",
    //     //         detail: "Please fill out at least one section before submitting.",
    //     //         icon: "far fa-times-circle"
    //     //     });
    //     // }
    // }
    uploadAllDocuments(): void {
        this.submitted = true;
        let allSectionsData: any[] = [];
        let allFiles: File[] = [];
        let invalidMandatoryTabs: string[] = [];

        this.uploadDocForm.forEach((formGroup, tabIndex) => {
            formGroup.patchValue({
                sectionName: this.tabs[tabIndex]
            });

            const isOpticalPowerRange = this.tabs[tabIndex] === "Optical Power Range";
            const isMandatory = this.tabsMandatory[tabIndex];
            const sectionData = this.collectSectionData(formGroup, tabIndex);
            const hasFiles = sectionData && sectionData.files.length > 0;

            let isValid = true;

            if (isMandatory) {
                isValid =
                    formGroup.valid ||
                    (isOpticalPowerRange && (hasFiles || formGroup.get("opticalRange")?.value != null));
            }

            if (isValid) {
                if (sectionData) {
                    allSectionsData.push(sectionData.section);
                    if (sectionData.files.length > 0) {
                        allFiles = [...allFiles, ...sectionData.files];
                    }
                }
            } else if (isMandatory) {
                invalidMandatoryTabs.push(this.tabs[tabIndex]);
            }
        });

        // If there are invalid mandatory tabs, show the message
        if (invalidMandatoryTabs.length > 0) {
            this.toastr.info(`Fields are mandatory in these tabs: ${invalidMandatoryTabs.join(", ")}`, 'Info!');

            return;
        }

        if (allSectionsData.length > 0) {
            this.uploadDocuments(allSectionsData, allFiles);
        }
        this.createForm().reset();
        const fileInput = document.getElementById("txtSelectDocument") as HTMLInputElement;
        if (fileInput) {
            fileInput.value = "";
        }
    }

    collectSectionData(
        formGroup: UntypedFormGroup,
        tabIndex: number
    ): { section: any; files: File[] } | null {
        const section = {
            name: formGroup.value.sectionName,
            latitude: formGroup.value.latitude,
            longitude: formGroup.value.longitude,
            opticalRange: formGroup.value.opticalRange,
            files: [] as File[]
        };

        if (this.selectedFileUploadPreview[tabIndex]) {
            this.selectedFileUploadPreview[tabIndex].forEach((file: File) => {
                section.files.push(file);
            });
        }

        return { section, files: section.files };
    }

    uploadDocuments(sectionsData: any[], allFiles: File[]): void {
        const formData = new FormData();
        formData.append("customerInventoryId", this.inventoryIdData.toString());

        sectionsData.forEach((section, i) => {
            formData.append(`sections[${i}].name`, section.name);
            formData.append(`sections[${i}].latitude`, section.latitude);
            formData.append(`sections[${i}].longitude`, section.longitude);
            formData.append(`sections[${i}].opticalRange`, section.opticalRange);
            section.files.forEach((file: File) => {
                formData.append(`sections[${i}].files`, file);
            });
        });


        const url = `/inwards/inventory/document/upload/`;
        this.customerInventoryManagementService.postMethod(url, formData).subscribe(
            (response: any) => {
                if (response.responseCode === 406 || response.responseCode === 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    this.getCustomerAssignedList();
                    this.closeUploadDocumentId();
                    this.createForm().reset();
                    this.submitted = false;
                    this.toastr.success(`Successfully Upload `, 'Success!');

                    this.uploadDocumentId = false;
                    this.uploadDocumentDialogRef.close();
                    this.uploadDocForm.forEach(form => form.reset());
                }
            },
            (error: any) => {
                console.error(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    deleteConfirm(file, section, sectionName) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Confirmation',
                description: 'Do you want to delete this file?',
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteDoc(file, section, sectionName);
                this.dialog?.closeAll();
            } else {
                this.toastr.info(`You have rejected`, 'Info!');
            }
        });
    }

    hasFilesForTab(tab: string): boolean {
        return this.inventoryFileDocData?.some(section => section?.sectionName === tab) ?? false;
    }

    mylocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                if (position) {
                    // this.iflocationFill = true;
                    this.uploadDocForm.forEach((formGroup, tabIndex) => {
                        if (this.activeTabIndex === tabIndex) {
                            formGroup.patchValue({
                                latitude: position.coords.latitude,
                                longitude: position.coords.longitude
                            });
                        }
                    });
                }
            });
        } else {
            this.toastr.error(`Geolocation is not supported by this browser.`, 'Failed!');

        }
    }

    onTabChange(event: any): void {
        this.activeTabIndex = event.index;
    }

    searchStaffByName() {
        if (this.searchStaffDeatil) {
            this.approveAssignInventoryData = this.approveAssignData.filter(
                staff =>
                    staff.fullName.toLowerCase().includes(this.searchStaffDeatil.toLowerCase()) ||
                    staff.username.toLowerCase().includes(this.searchStaffDeatil.toLowerCase())
            );
        } else {
            this.approveAssignInventoryData = this.approveAssignData;
        }
    }

    clearSearchForm() {
        this.searchStaffDeatil = "";
        this.approveAssignInventoryData = this.approveAssignData;
    }


    attachDeviceInventoryModalOpen() {


        this.attachDeviceInventoryModal = true;
        this.getCustomerNetworkDeviceBindData();
        this.getPOPList();
        this.getMasterDBList();

        this.dialogRefAttachDevice = this.dialog.open(this.attachDeviceDialogTemplate, {
            width: '60%',
            disableClose: true
        });
    }


    attachDeviceInventoryModalClose() {
        this.externalInventoryAssignSumitted = false;
        this.getExternalProductFlag = false;
        this.getExternalItemListFlag = false;
        this.getAllConnectionNumberFlag = false;
        this.attachDeviceInventoryAssignForm.reset();
        this.selectedExternalMACAddress = "";
        this.externalItemsFilterGlobal = "";
        this.macAddressList = [];
        this.macExternalListFlag = false;
        this.billToSigleFlag = false;
        this.itemConditionPairFlag = false;
        this.billToPairFlag = false;
        this.getPlanSingleSplitterFlag = false;
        this.getPlanPairSplitterFlag = false;
        this.getAllPairPlanProductSTBFlag = false;
        this.getAllPairProductCardFlag = false;
        this.selAssemblyTypePlanFlag = false;
        this.selAssemblyTypePlanGroupFlag = false;
        this.getAssemblyNameflag = false;
        this.discountPairFlag = false;
        this.isInvoiceDataSingleFlag = false;
        this.itemConditionSingleFlag = false;
        this.itemConditionPlanSeriFlag = false;
        this.itemConditionPlanPairFlag = false;
        this.oldOfferPriceSingleFlag = false;
        this.newOfferSingleFlag = false;
        this.newOfferSTBFlag = false;
        this.newOfferCardFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountCardPairFlag = false;
        this.oldOfferBasedDiscountNonSerialFlag = false;
        this.oldOfferBasedDiscountPlanFlag = false;
        this.oldOfferBasedDiscountSTBPairFlag = false;
        this.oldOfferBasedDiscountSingleFlag = false;
        this.oldOfferBasedDiscountSingleReplaceFlag = false;
        this.oldOfferPriceCardFlag = false;
        this.oldOfferPriceNonSerialFlag = false;
        this.oldOfferPricePlanFlag = false;
        this.oldOfferPriceSTBFlag = false;
        this.oldOfferPriceSingleReplaceFlag = false;
        this.newOfferNonSerialFlag = false;
        // this.assignExternalInventoryModal = false;
        this.attachDeviceInventoryModal = false;
        // this.popList = [];
        this.oltList = [];
        this.dnSplitterList = [];
        this.snSplitterList = [];
        this.masterDbList = [];
        this.getCustomerNetworkDeviceBindData();
        this.dialogRefAttachDevice.close();
    }

    onPopChange(popId: number, isPreFill = false) {
        if (!isPreFill) {
            this.attachDeviceInventoryAssignForm.patchValue({
                oltid: null,
                dnsplitterid: null,
                snsplitterid: null
            });
        }
        this.oltList = [];
        this.dnSplitterList = [];
        this.snSplitterList = [];
        this.getOLTByPOP(popId);
    }

    onOltChange(oltId: number, isPreFill = false) {
        if (!isPreFill) {
            this.attachDeviceInventoryAssignForm.patchValue({
                dnsplitterid: null,
                snsplitterid: null
            });
        }
        this.dnSplitterList = [];
        this.snSplitterList = [];
        this.getDNSplitterByOLT(oltId);
    }

    onDnSplitterChange(dnSplitterId: number, isPreFill = false) {
        if (!isPreFill) {
            this.attachDeviceInventoryAssignForm.patchValue({
                snsplitterid: null
            });
        }
        this.snSplitterList = [];
        this.getSNSplitterByDNSplitter(dnSplitterId);
    }

    getPOPList() {
        const url = "/popmanagement/all?from_cache=true";
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                if (res.responseCode === 200) {
                    this.popList = res.dataList;


                } else {
                    this.toastr.info(`${res.responseMessage}`, 'Info!');

                }
            },
            err => {
                this.toastr.info(err?.error?.ERROR || "Something went wrong", 'Info!');

            }
        );
    }

    getMasterDBList() {
        const url = "/NetworkDevice/getNetworkDevicesByDeviceType?deviceType=" + "Master DB/DB";
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                if (res.responseCode === 200) {
                    this.masterDbList = res.dataList;
                } else {
                    this.toastr.info(`${res.responseMessage}`, 'Info!');

                }
            },
            err => {
                this.toastr.info(err?.error?.ERROR || "Something went wrong", 'Info!');

            }
        );
    }

    getCustomerNetworkDeviceBindData() {
        const bindUrl = `/customer/getCustomerNetworkBindByCustId?custId=${this.customerId}`;
        this.customerInventoryManagementService.getMethod(bindUrl).subscribe(
            (bindRes: any) => {
                if (bindRes.responseCode === 200 && bindRes.data) {
                    const data = bindRes.data;

                    this.attachDeviceInventoryAssignForm.patchValue({
                        popid: data.popid,
                        oltid: data.oltid,
                        dnsplitterid: data.dnsplitterid,
                        snsplitterid: data.snsplitterid,
                        masterdbid: data.masterdbid
                    });

                    this.onPopChange(data.popid, true);
                    this.onOltChange(data.oltid, true);
                    this.onDnSplitterChange(data.dnsplitterid, true);

                    this.attachDeviceButtonLabel = "Update Attach Device";

                    this.networkBindId = data.id;
                } else {
                    // Form should be empty if no existing data
                    this.attachDeviceInventoryAssignForm.reset();
                    this.attachDeviceButtonLabel = "Save Attach Device";
                    this.networkBindId = null;
                }
            },
            err => {
                this.toastr.info(err?.error?.ERROR || "Something went wrong", 'Info!');

            }
        );
    }

    getOLTByPOP(popId: number) {
        const url = `/NetworkDevice/getOLTDeviceByPopId?id=${popId}`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                if (res.responseCode === 200) {
                    this.oltList = res.dataList;
                } else {
                    this.toastr.info(`${res.responseMessage}`, 'Info!');

                }
            },
            err => {
                this.toastr.info(err?.error?.ERROR || "Something went wrong", 'Info!');

            }
        );
    }

    getDNSplitterByOLT(oltId: number) {
        const url = `/NetworkDevice/getDNSplitterByOltId?id=${oltId}`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                if (res.responseCode === 200) {
                    this.dnSplitterList = res.dataList;
                } else {
                    this.toastr.info(`${res.responseMessage}`, 'Info!');

                }
            },
            err => {
                this.toastr.info(err?.error?.ERROR || "Something went wrong", 'Info!');

            }
        );
    }

    getSNSplitterByDNSplitter(dnSplitterId: number) {
        const url = `/NetworkDevice/getSNSplitterByDNSplitterId?id=${dnSplitterId}`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                if (res.responseCode === 200) {
                    this.snSplitterList = res.dataList;
                } else {
                    this.toastr.info(`${res.responseMessage}`, 'Info!');

                }
            },
            err => {
                this.toastr.info(err?.error?.ERROR || "Something went wrong", 'Info!');

            }
        );
    }

    loadInitialData() {
        this.getPOPList();
        this.getMasterDBList();
    }
    confirmAttachDeviceUpdate() {
        if (this.networkBindId) {
            this.confirmationService.confirm({
                message: `If the SN Splitter is changed, all associated device mappings—such as downgrade hierarchy bindings between the SN Splitter and Customer Inventory—linked to the previous SN Splitter will be automatically removed.
        <br><br>
        <strong>Confirmation Required:</strong> Are you sure you want to proceed with updating the attached configuration?`,
                acceptLabel: "Yes",
                rejectLabel: "No",
                icon: "pi pi-question-circle",
                accept: () => {
                    this.attachDeviceInventory();
                },
                reject: () => { }
            });
        } else {
            this.attachDeviceInventory();
        }
    }

    attachDeviceInventory() {
        const formData = this.attachDeviceInventoryAssignForm.value;
        if (
            !formData.popid ||
            !formData.oltid ||
            !formData.dnsplitterid ||
            !formData.snsplitterid ||
            !formData.masterdbid
        ) {
            this.toastr.info(`Please fill all required fields before submitting.`, 'Info!');

            return;
        }

        formData.customerid = this.custData.id;

        let url = "";
        let apiCall;

        if (this.networkBindId) {
            formData.id = this.networkBindId;
            url = "/customer/updateCustomerNetworkBind";
            apiCall = this.customerInventoryManagementService.updateMethod(url, formData);
        } else {
            url = "/customer/saveCustomerNetworkBind";
            apiCall = this.customerInventoryManagementService.postMethod(url, formData);
        }

        apiCall.subscribe(
            (res: any) => {
                if (res.responseCode === 200) {
                    this.toastr.success(res.responseMessage || "Data saved successfully!", 'Success!');
                    this.dialogRefAttachDevice.close();
                    this.attachDeviceInventoryModal = false;
                    this.attachDeviceInventoryAssignForm.reset();
                    this.networkBindId = null;
                    this.getCustomerNetworkDeviceBindData();
                } else {
                    this.toastr.info(res.responseMessage || "Something went wrong!", 'Info!');

                }
            },
            (err: any) => {
                this.toastr.info(err?.error?.ERROR || "Something went wrong!", 'Info!');

            }
        );
    }
    //   selMacSearchOption(event) {
    //     this.searchForm.value.searchMacDeatil = "";
    //     this.optionValue = event;
    //   }
    //   searchMac() {
    //     const staffId = localStorage.getItem("userId");
    //     this.searchMacData.paginationRequestDTO.filters[0].filterValue =
    //       this.searchForm.value.searchMacDeatil;
    //     this.searchMacData.paginationRequestDTO.filters[0].filterColumn =
    //       this.searchForm.value.searchOption;
    //     this.searchMacData.productId = this.productMacAddressId;
    //     this.searchMacData.ownerId = Number(staffId);
    //     this.searchMacData.ownerType = "staff";
    //     this.searchMacData.paginationRequestDTO.page = this.currentMacAddressListdata;
    //     this.searchMacData.paginationRequestDTO.pageSize = this.macAddressListdataitemsPerPage;
    //     const url = "/outwards/searchItemHistoryByProduct";
    //     this.inwardService.postMethod(url, this.searchMacData).subscribe(
    //       (response: any) => {
    //         if (response.dataList.length > 0) {
    //           this.macAddressList = response.dataList;
    //           this.macAddressListtotalRecords = response.totalRecords;
    //         } else {
    //           this.macAddressList = [];
    //           this.macAddressListtotalRecords = 0;
    //           this.messageService.add({
    //             severity: "info",
    //             summary: "Info",
    //             detail: "No Search Record Found",
    //             icon: "far fa-times-circle"
    //           });
    //         }
    //       },
    //       (error: any) => {
    //         console.log(error, "error");
    //         this.messageService.add({
    //           severity: "error",
    //           summary: "Error",
    //           detail: error.error.ERROR,
    //           icon: "far fa-times-circle"
    //         });
    //       }
    //     );
    //   }
    //   clearMac() {
    //     this.searchForm.reset();
    //     this.newFirstMacAddress = 0;
    //     this.currentMacAddressListdata = 1;
    //     this.macAddressListdataitemsPerPage = 20;
    //     let obj = {
    //       value: this.productMacAddressId
    //     };
    //     this.getMacAddressList(obj);
    //   }

    networkHierarchyDevice(data) {
        if (!data || !data.id) {
            console.error("Invalid data object for hierarchy device", data);
            return;
        }
        this.data1 = [];
        this.custInvenId = data.id;
        this.itemIdForHierarchy = data.itemId;
        // this.netWorkHierarchyName = data.name;

        this.isHierarchyDiagramVisible = true;
        this.showDiagram = true;
        this.show = true;
        this.ifPersonalPerentDeviceShow = true;
        this.IfPersonalNetworkDataShow = true;
        this.ifServiceAreaListShow = false;
        this.networkHierarchyDialogRef = this.dialog.open(this.networkHierarchyDialog, {
            width: '80%',
            disableClose: true
        });

        const itemId = data.itemId;
        const custId = this.custData?.id || localStorage.getItem("custId"); // fallback if needed
        const hierarchyUrl = `/NetworkDevice/getCustDeviceHierarchy?itemId=${itemId}&custId=${custId}&custInvenId=${this.custInvenId}`;

        this.customerInventoryManagementService.getMethod(hierarchyUrl).subscribe(
            (response: any) => {
                if (response.responseCode === 200) {
                    this.data1 = response.data;
                    this.netWorkHierarchyName = response.data?.[0]?.data?.name || "Hierarchy";
                } else {
                    this.show = false;
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                }
            },
            (error: any) => {
                this.show = false;
                this.toastr.info(error?.error?.ERROR || "Something went wrong", 'Info!');

            }
        );
    }
    closeNetworkDiagramModel() {
        this.isHierarchyDiagramVisible = false;
    }
    showHierarchyMappingList(deviceId: number) {
        const custId = this.custData?.id || localStorage.getItem("custId");
        const url = `/NetworkDevice/getCustDeviceHierarchyList?itemId=${this.itemIdForHierarchy}&custId=${custId}&custInvenId=${this.custInvenId}`;

        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.hierarchyMappingList = response.data || [];
                this.showDiagram = false;
                this.mappingLoaded = true;
            },
            (error: any) => {
                this.toastr.info(error?.error?.ERROR || "Something went wrong", 'Info!');

            }
        );
    }
    exportNetMapping() {
        import("xlsx").then(xlsxModule => {
            const xlsx = xlsxModule.default || xlsxModule;
            const parentHeaders = [
                "Name",
                "Port Number",
                "Device Type",
                "Owner Type",
                "MAC Address",
                "Serial Number"
            ];
            const childHeaders = [
                "Name",
                "Port Number",
                "Device Type",
                "Owner Type",
                "MAC Address",
                "Serial Number"
            ];

            // Header row 1 with group titles
            const headerRow1 = [
                ...Array(parentHeaders.length).fill("Parent Device Details"),
                ...Array(childHeaders.length).fill("Child Device Details")
            ];

            // Header row 2 with actual column names
            const headerRow2 = [...parentHeaders, ...childHeaders];

            // Data rows
            const dataRows = this.hierarchyMappingList.map((ele: any) => [
                ele?.parentDeviceName || "-",
                ele?.parentDevicePortNumber || "-",
                ele?.parentDeviceType || "-",
                ele?.parentDeviceOwnerType || "-",
                ele?.parentDeviceMacAddress || "-",
                ele?.parentDeviceSerialNumber || "-",

                ele?.childDeviceName || "-",
                ele?.childDevicePortNumber || "-",
                ele?.childDeviceType || "-",
                ele?.childDeviceOwnerType || "-",
                ele?.childDeviceMacAddress || "-",
                ele?.childDeviceSerialNumber || "-"
            ]);

            const allRows = [headerRow1, headerRow2, ...dataRows];
            const worksheet = xlsx.utils.aoa_to_sheet(allRows);

            // Merge cells for grouped headers
            worksheet["!merges"] = [
                { s: { r: 0, c: 0 }, e: { r: 0, c: 5 } }, // Parent Device Details (6 columns)
                { s: { r: 0, c: 6 }, e: { r: 0, c: 11 } } // Child Device Details (6 columns)
            ];

            const workbook = {
                Sheets: { "Device Hierarchy": worksheet },
                SheetNames: ["Device Hierarchy"]
            };
            const excelBuffer: any = xlsx.write(workbook, { bookType: "xlsx", type: "array" });
            this.saveAsExcelFile(excelBuffer, "Device_Hierarchy");
        });
    }
    saveAsExcelFile(buffer: any, fileName: string): void {
        let EXCEL_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        let EXCEL_EXTENSION = ".xlsx";
        const data: Blob = new Blob([buffer], {
            type: EXCEL_TYPE
        });
        FileSaver.saveAs(data, fileName + "_Export_" + new Date().getTime() + EXCEL_EXTENSION);
    }

    getAllParent() {
        const url = `/NetworkDevice/all`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.allNetworkDeviceData = response.dataList;
                // this.availableParentList = this.networkDeviceListData;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getAvailableParent() {
        const snSplitterId = this.attachDeviceInventoryAssignForm.get("snsplitterid")?.value;

        if (!snSplitterId) {
            console.warn("SN Splitter ID is missing. Skipping API call.");
            return;
        }

        const url = `/NetworkDevice/${snSplitterId}`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.data) {
                    // ✅ Convert object to array for dropdown
                    this.availableParentList = [response.data];
                } else {
                    this.availableParentList = [];
                }
            },
            (error: any) => {
                this.toastr.info(error.error?.ERROR || "Something went wrong", 'Info!');

            }
        );
    }

    parentDeviceMapping(data: any) {
        this.inForm.reset();
        this.selecetedData = data;
        this.inFlag = false;
        this.outFlag = false;
        this.submittedIn = false;
        this.submittedOut = false;
        this.deviceId = data.id;

        this.totalInPorts = this.fb.array([]);
        this.totalOutPorts = this.fb.array([]);

        // ✅ Step 1: Get Device Details First
        const apiUrl = `/NetworkDevice/getDeviceDetailsByItemIdAndCustInvenId?itemId=${data.itemId}&custInventoryId=${data.id}`;
        this.customerInventoryManagementService.getMethod(apiUrl).subscribe(
            (res: any) => {
                if (res && res.responseCode === 200 && res.data) {
                    this.deviceDetail = res.data;
                    this.selectedDeviceId = this.deviceDetail.id;
                    this.deviceName = this.deviceDetail.name;
                    // ✅ Step 2: Call other dependent APIs only after deviceDetail is set
                    this.currentParentPorts(this.selectedDeviceId, "IN");
                    this.currentParentPorts(this.selectedDeviceId, "OUT");
                    this.getChildParentPortDetails(this.selectedDeviceId);

                    const url = `/NetworkDevice/boundParents?id=${this.selectedDeviceId}`;
                    this.customerInventoryManagementService.getMethod(url).subscribe(
                        (response: any) => {
                            if (response.dataList?.length > 0) {
                                for (let index = 0; index < data.totalPorts + 1; index++) {
                                    response.dataList.forEach(element => {
                                        if (element.portType === "IN") {
                                            if (element.inBind === `I${index}`) {
                                                this.totalInPorts.push(
                                                    this.fb.group({
                                                        inBind: `I${index}`,
                                                        outBind: element.outBind,
                                                        parentDeviceId: element.parentDeviceId,
                                                        flag: true
                                                    })
                                                );
                                            }
                                            if (element.outBind === `I${index}`) {
                                                this.totalInPorts.push(
                                                    this.fb.group({
                                                        inBind: element.outBind,
                                                        outBind: element.inBind,
                                                        parentDeviceId: element.parentDeviceId,
                                                        flag: true
                                                    })
                                                );
                                            }
                                        }
                                    });
                                }

                                for (let index = 0; index < data.totalPorts + 1; index++) {
                                    response.dataList.forEach(element => {
                                        if (element.portType === "OUT") {
                                            if (element.outBind === `O${index}`) {
                                                this.totalOutPorts.push(
                                                    this.fb.group({
                                                        inBind: element.inBind,
                                                        outBind: `O${index}`,
                                                        parentDeviceId: element.parentDeviceId,
                                                        flag: true
                                                    })
                                                );
                                            }
                                            if (element.inBind === `O${index}`) {
                                                this.totalOutPorts.push(
                                                    this.fb.group({
                                                        inBind: element.outBind,
                                                        outBind: element.inBind,
                                                        parentDeviceId: element.parentDeviceId,
                                                        flag: true
                                                    })
                                                );
                                            }
                                        }
                                    });
                                }
                            }

                            // ✅ Call these after data binding
                            this.getAllParent();
                            this.getAvailableParent();

                            // ✅ Finally open the modal
                            // $("#selectParent").modal("show");
                            this.parentDeviceDialogRef = this.dialog.open(this.parentDeviceDialog, {
                                width: '70%',
                                disableClose: true,
                                data,
                            });
                        },
                        error => {
                            this.toastr.info(error.error?.ERROR || "Something went wrong", 'Info!');

                        }
                    );
                } else {
                    console.warn("No device details found");
                }
            },
            err => {
                console.error("Error fetching device details", err);
            }
        );
    }

    currentParentPorts(e, type) {
        const url = `/NetworkDevice/checkPortAvailability?parentDeviceId=${e}&parentPortType=${type}`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (type == "IN") {
                    this.basicInPorts =
                        response.dataList != null
                            ? response.dataList.filter((item: string) => item.includes("IN-Port"))
                            : response.dataList;

                    this.updateParentInPortdata =
                        this.basicInPorts.length < 0
                            ? [...this.basicInPorts]
                            : this.basicInPorts.map(ele => ({
                                id: ele,
                                name: ele
                            }));
                    //   this.updateChildOutPortdata = [...this.updateParentInPortdata];
                } else {
                    this.basicOutPorts =
                        response.dataList != null
                            ? response.dataList.filter((item: string) => item.includes("OUT-Port"))
                            : response.dataList;
                    this.updateChildInPortdata =
                        this.basicOutPorts.length < 0
                            ? [...this.basicOutPorts]
                            : this.basicOutPorts.map(ele => ({
                                id: ele,
                                name: ele
                            }));
                    //   this.updateParentOutPortdata = [...this.updateChildInPortdata];
                }
            },
            (error: any) => this.spinner.hide()
        );
    }

    getParentPort(data, isParent) {
        let filter;
        if (isParent) {
            filter = "OUT-Port";
        } else {
            filter = "IN-Port";
        }
        const url = `/NetworkDevice/checkPortAvailability?parentDeviceId=${data.otherDeviceId}&parentPortType=IN`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.updateParentOutPortdata =
                    response.dataList != null
                        ? response.dataList
                            .filter((item: string) => item.includes(filter))
                            .map(ele => ({
                                id: ele,
                                name: ele
                            }))
                        : response.dataList;

                this.updateChildOutPortdata = [...this.updateParentOutPortdata];

                let Outdata = {
                    id: data.otherDevicePort,
                    name: data.otherDevicePort
                };
                this.updateParentOutPortdata.push(Outdata);
                this.selectedParentOutPortdata = Outdata.id;

                this.updateChildOutPortdata.push(Outdata);
                this.selectedChildOutPortdata = Outdata.id;
            },
            (error: any) => { }
        );
    }
    selectParent(e, type) {

        const url = `/NetworkDevice/checkPortAvailability?parentDeviceId=${e.value}&parentPortType=${type}`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (type == "IN") {
                    this.availableInPorts =
                        response.dataList != null
                            ? response.dataList.filter((item: string) => item.includes("IN-Port"))
                            : response.dataList;
                    this.outFlag = true;
                } else {
                    this.availableOutPorts =
                        response.dataList != null
                            ? response.dataList.filter((item: string) => item.includes("OUT-Port"))
                            : response.dataList;
                    this.inFlag = true;
                }
            },
            (error: any) => this.spinner.hide()
        );
    }
    modalOpenParentDevice(deviceType) {
        this.isparentChildDeviceModelOpen = true;
        this.deviceType = deviceType;
    }
    removeSelectedparentDevice(deviceType) {
        if (this.deviceType === "Parent") {
            this.inForm.patchValue({
                parentDeviceId: ""
            });
            this.availableOutPorts = "";
            // else {
            //   this.outForm.patchValue({
            //     parentDeviceId: ""
            //   });
            //   this.availableInPorts = "";
            // }
        }
    }
    connectParent(event) {
        // this.networkDeviceGroupForm.controls.devicetype.setValue("");
        let data = {
            currentDeviceId: this.selectedDeviceId,
            currentDevicePort: this.inForm.controls.inBind.value,
            otherDeviceId: this.inForm.controls.parentDeviceId.value,
            otherDevicePort: this.inForm.controls.outBind.value,
            portType: "IN"
        };

        if (this.inForm.valid) {
            const url = `/NetworkDevice/saveMappingData`;
            this.customerInventoryManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.toastr.success(`${response.responseMessage}`, 'Success!');

                        this.inForm.reset();
                        this.parentDeviceMapping(this.selecetedData);
                        this.parentDeviceDialogRef.close();
                        // this.outForm.reset();
                    } else {
                        this.inForm.reset();
                        // this.outForm.reset();
                        this.parentDeviceMapping(this.selecetedData);
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    updateNameAndPort(event: any, isParent) {
        this.parentChildPortModal = true;
        this.isParent = isParent;
        this.portDetailDialogRef = this.dialog.open(this.portDetailDialog, {
            width: '65%',
            disableClose: true,
        });
    }

    getChildParentPortDetails(id) {
        this.childPortData = [];
        this.parentPortData = [];
        let url = `/NetworkDevice/getNetworkDeviceBindByCurrentDeviceId?id=${id}`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.childPortData = response.data.filter(
                    deviceDetail => deviceDetail.portType.toLowerCase() === "out"
                );
                this.parentPortData = response.data.filter(
                    deviceDetail => deviceDetail.portType.toLowerCase() === "in"
                );
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    selectedDeviceChange(selectedDevice) {
        if (this.deviceType === "Parent") {
            this.inForm.patchValue({
                parentDeviceId: selectedDevice.id
            });

            var event = {
                value: selectedDevice.id
            };
            this.selectParent(event, "OUT");
        } else {
            this.outForm.patchValue({
                parentDeviceId: selectedDevice.id
            });

            var event = {
                value: selectedDevice.id
            };
            this.selectParent(event, "IN");
        }
    }
    modalCloseParentDevice() {
        this.isparentChildDeviceModelOpen = false;
    }

    closeParentMappingModel() {
        $("#selectParent").modal("hide");
    }
    clearParentDeviceMapping() {
        this.inForm.reset();
        this.outForm.reset();
    }

    getnetworkDeviceList(list) {
        let size;
        let page_list = this.currentPagenetworkDeviceListdata;
        this.searchkey = "";
        if (list) {
            size = list;
            this.networkDeviceListdataitemsPerPage = list;
        } else {
            size = this.networkDeviceListdataitemsPerPage;
        }

        const url = "/NetworkDevice/list";
        let networkdevicedata = {
            page: page_list,
            pageSize: size
        };
        this.customerInventoryManagementService.postMethod(url, networkdevicedata).subscribe(
            (response: any) => {
                this.networkDeviceListData = response.dataList;
                this.networkDeviceListDataselector = response.dataList;
                this.networkDeviceListdatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    closePortDetailsModel() {
        this.editedRowIndex = -1;
        this.parentChildPortModal = false;
    }
    deletePort(id) {
        let url = `/NetworkDevice/deleteNetworkDeviceBindById?id=${id}`;
        this.customerInventoryManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                this.getChildParentPortDetails(this.deviceId);
                this.currentParentPorts(this.deviceId, "IN");
                this.currentParentPorts(this.deviceId, "OUT");
                this.toastr.success(`${response.message}`, 'Success!');

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    editValue(rowIndex: number, parameter, isParent: boolean) {
        this.editedRowIndex = rowIndex;
        if (isParent) {
            this.getParentPort(parameter, isParent);

            let parentIndata = {
                id: parameter.currentDevicePort,
                name: parameter.currentDevicePort
            };
            this.updateParentInPortdata.push(parentIndata);
            this.selectedParentInPortdata = parentIndata.id;

            //   let parentOutdata = {
            //     id: parameter.otherDevicePort,
            //     name: parameter.otherDevicePort,
            //   };
            //   this.updateParentOutPortdata.push(parentOutdata);
            //   this.selectedParentOutPortdata = parentOutdata.id;
        } else {
            this.getParentPort(parameter, isParent);

            let childIndata = {
                id: parameter.currentDevicePort,
                name: parameter.currentDevicePort
            };
            this.updateChildInPortdata.push(childIndata);
            this.selectedChildInPortdata = childIndata.id;

            //   let childOutdata = {
            //     id: parameter.otherDevicePort,
            //     name: parameter.otherDevicePort,
            //   };
            //   this.updateChildOutPortdata.push(childOutdata);
            //   this.selectedChildOutPortdata = childOutdata.id;
        }
    }
    updateValue(rowIndex: number, inventorydata, isParent: boolean) {
        let request;
        if (isParent) {
            request = {
                id: inventorydata.id,
                currentDeviceId: inventorydata.currentDeviceId,
                currentDevicePort: this.selectedParentInPortdata,
                otherDeviceId: inventorydata.otherDeviceId,
                otherDevicePort: this.selectedParentOutPortdata
            };
        } else {
            request = {
                id: inventorydata.id,
                currentDeviceId: inventorydata.currentDeviceId,
                currentDevicePort: this.selectedChildInPortdata,
                otherDeviceId: inventorydata.otherDeviceId,
                otherDevicePort: this.selectedChildOutPortdata
            };
        }

        let url = `/NetworkDevice/changeNetworkDeviceBinding`;
        this.customerInventoryManagementService.updateMethod(url, request).subscribe(
            (response: any) => {
                this.editedRowIndex = -1;
                this.getChildParentPortDetails(this.deviceId);
                this.currentParentPorts(this.deviceId, "IN");
                this.currentParentPorts(this.deviceId, "OUT");
                this.toastr.success(`${response.message}`, 'Success!');

                this.portDetailDialogRef.close();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    selectParentCustomerChange(event) {
        this.selectedParentCust = event;
    }
    paginateReplaceInventory(event) {
        this.newFirst = event.first;
        this.macAddressListdataitemsPerPage = event.rows;
        this.currentMacAddressListdata = event.page + 1;

        const obj = {
            value: this.productMacAddressId
        };
        this.getReplaceLevelMacAddressList(obj);
    }
    onCancel(): void {
        // this.dialog.close();
    }
    onCloseHierarchyDialog() {
        this.isHierarchyDiagramVisible = false;
        this.networkHierarchyDialogRef.close()
    }
    @ViewChild("CustInventoryHistory") CustInventoryHistory!: TemplateRef<any>;

    openCustInventoryHistory() {
        this.dialogRef = this.dialog.open(this.CustInventoryHistory, {
            width: "1500px",
            disableClose: true,
            panelClass: 'cust-inventory-history-dialog'
        });
        this.dialogRef.afterClosed().subscribe(result => {

        });
    }
}
