import { Component, OnInit, TemplateRef } from "@angular/core";
import { UntypedFormControl, UntypedFormGroup, Validators, UntypedFormBuilder } from "@angular/forms";
import { DictionaryService } from "src/app/service/dictionary.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { ViewChild } from "@angular/core";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { MessageService } from "primeng/api";
// import { NgxSpinnerService } from 'ngx-spinner';
import { ConfirmationService } from "primeng/api";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
import { rejects } from "assert";
import { error } from "console";

declare var $: any;
@Component({
    selector: "app-dictionary",
    templateUrl: "./dictionary.component.html",
    styleUrls: ["./dictionary.component.css"],
    standalone: false
})
export class DictionaryComponent implements OnInit {
    searchDictionaryForm: UntypedFormGroup;
    dictionaryForm: UntypedFormGroup;
    searchDictionaryAttributeForm: UntypedFormGroup;
    dictionaryAttributeForm: UntypedFormGroup;
    searchDictionaryValueForm: UntypedFormGroup;
    dictionaryValueForm: UntypedFormGroup;
    mvnoData: any;
    loggedInUser: any;
    mvnoId: any;
    dictionaryMvnoId: any;
    dictionaryAttributeMvnoId: any;
    accessData: any = JSON.parse(localStorage.getItem("accessData"));

    @ViewChild("closebutton") closebutton;
    @ViewChild("closeDicAttrPopupButton") closeDicAttrPopupButton;
    vendoreTypeAll: any = [];
    attributeCategoryList: any = [];

    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    createDictAccess: boolean = false;
    editDictAccess: boolean = false;
    deleteDictAccess: boolean = false;
    createDictAttrAccess: boolean = false;
    deleteDictAttrAccess: boolean = false;
    editDictAttrAccess: boolean = false;
    viewDictAttrAccess: boolean = false;
    clickViewDictAtt: boolean = false;
    viewAttrValueAccess: boolean = false;
    createAttrValueAccess: boolean = false;
    deleteAttrValueAccess: boolean = false;
    editAttrValueAccess: boolean = false;
    isSuperAdmin: boolean = false;
    userId: string;
    superAdminId: string = RadiusConstants.SUPERADMINID;

    constructor(
        private toastr: ToastrService,

        private dictionaryService: DictionaryService,
        private fb: UntypedFormBuilder,
        private radiusUtility: RadiusUtility,
        // private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService, private dialog: MatDialog,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.mvnoData = JSON.parse(localStorage.getItem("mvnoData"));
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.mvnoId = localStorage.getItem("mvnoId");
        this.userId = localStorage.getItem("userId");
        this.superAdminId = RadiusConstants.SUPERADMINID;
        if (this.userId == RadiusConstants.SUPERADMINID) {
            this.isSuperAdmin = true;
        } else {
            this.isSuperAdmin = false;
        }
        // dictionary
        this.createDictAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DICT_CREATE);
        this.deleteDictAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DICT_DELETE);
        this.editDictAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DICT_EDIT);

        // use for attribute values
        this.viewAttrValueAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DICT_VALUE);
        this.createAttrValueAccess = loginService.hasPermission(
            RADIUS_CONSTANTS.RADIUS_DICT_VALUE_CREATE
        );
        this.deleteAttrValueAccess = loginService.hasPermission(
            RADIUS_CONSTANTS.RADIUS_DICT_VALUE_DELETE
        );
        this.editAttrValueAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DICT_VALUE_EDIT);

        // use for attribute access values
        this.viewDictAttrAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DICT_ATTRIBUTES);
        this.createDictAttrAccess = loginService.hasPermission(
            RADIUS_CONSTANTS.RADIUS_DICT_ATTRIBUTES_CREATE
        );
        this.deleteDictAttrAccess = loginService.hasPermission(
            RADIUS_CONSTANTS.RADIUS_DICT_ATTRIBUTES_DELETE
        );
        this.editDictAttrAccess = loginService.hasPermission(
            RADIUS_CONSTANTS.RADIUS_DICT_ATTRIBUTES_EDIT
        );
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DICT_EDIT) || this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_DICT_DELETE)) {
            return ['id', 'vendor', 'vendorId', 'vendorType', 'action'];
        } else {
            return ['id', 'vendor', 'vendorId', 'vendorType'];
        }
    }

    ngOnInit(): void {
        this.searchDictionaryForm = new UntypedFormGroup({
            vendor: new UntypedFormControl(),
            vendorId: new UntypedFormControl(),
            vendorType: new UntypedFormControl()
        });

        this.dictionaryForm = new UntypedFormGroup({
            vendor: new UntypedFormControl(),
            vendorId: new UntypedFormControl(),
            vendorType: new UntypedFormControl()
        });

        this.dictionaryForm = this.fb.group({
            vendor: ["", [Validators.required]],
            vendorId: ["", Validators.required],
            vendorType: ["", Validators.required],
            mvnoName: [""]
        });

        this.searchDictionaryAttributeForm = new UntypedFormGroup({
            name: new UntypedFormControl()
        });

        this.dictionaryAttributeForm = new UntypedFormGroup({
            attributeId: new UntypedFormControl(),
            category: new UntypedFormControl(),
            name: new UntypedFormControl(),
            type: new UntypedFormControl(),
            vendor: new UntypedFormControl()
        });

        this.dictionaryAttributeForm = this.fb.group({
            attributeId: ["", [Validators.required]],
            category: ["", Validators.required],
            name: ["", Validators.required],
            type: ["", Validators.required],
            vendor: ["", Validators.required],
            mvnoName: [""]
        });

        this.searchDictionaryValueForm = new UntypedFormGroup({
            name: new UntypedFormControl(),
            value: new UntypedFormControl()
        });

        this.dictionaryValueForm = new UntypedFormGroup({
            name: new UntypedFormControl(),
            value: new UntypedFormControl()
            // type: new FormControl()
        });

        this.dictionaryValueForm = this.fb.group({
            name: ["", Validators.required],
            value: ["", Validators.required],
            mvnoName: [""]
        });

        this.getVendorTypes();
        this.getAllDictionary("");
    }

    //Used for pagination Of Dictionary
    totalRecords_Dic: string;
    currentPage_Dic = 0;
    itemsPerPage_Dic = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions_Dic = RadiusConstants.pageLimitOptions;
    showItemPerPage_Dic: any;
    searchkey_Dic: string;

    pageChangedForDictionary(pageNumber) {
        this.currentPage_Dic = pageNumber.pageIndex + 1;
        this.itemsPerPage_Dic = pageNumber.pageSize
        if (!this.searchkey_Dic) {
            this.getAllDictionary("");
        } else {
            this.searchDictionary();
        }
    }

    vendorTypes: any = [];
    dictionaryData: any = [];

    dictionaryPopupIsOpen: boolean = false;
    dictionaryValuePopupIsOpen: boolean = true;
    editMode: boolean = false;
    getVendorTypes() {
        //
        let vendorelist: [];
        this.dictionaryService.getVendorTypes().subscribe(
            (response: any) => {
                this.vendorTypes = response;
                vendorelist = response.vendorTypeList;
                for (var i in vendorelist) this.vendoreTypeAll.push({ label: vendorelist[i] });

                //
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                //
            }
        );
    }

    clearSearchForm() {
        this.searchDictionaryForm.reset();
        this.getAllDictionary("");
        this.refreshDictionaryAttributes();
        this.selectedRowVendorName = "";
    }

    refreshDictionaryAttributes() {
        this.dictionaryAttributeData.dictionaryAttributeList = [];
        if (this.dictionaryAttributeData.dictionaryAttributeList.length > 0) {
            this.showAddDicAttrButton = true;
        } else {
            this.showAddDicAttrButton = false;
        }
    }

    searchDictionary() {
        //
        this.refreshDictionaryAttributes();
        // this.currentPage_Dic = 1;

        // if (!this.searchkey_Dic || this.searchkey_Dic !== this.searchDictionaryForm.value) {
        //     this.currentPage_Dic = 1;
        // }
        this.searchkey_Dic = this.searchDictionaryForm.value;
        // if (this.showItemPerPage_Dic) {
        //     this.itemsPerPage_Dic = this.showItemPerPage_Dic;
        // }
        let vendor = this.searchDictionaryForm.value.vendor
            ? this.searchDictionaryForm.value.vendor
            : "";
        let vendorId = this.searchDictionaryForm.value.vendorId
            ? this.searchDictionaryForm.value.vendorId
            : "";
        let vendorType = this.searchDictionaryForm.value.vendorType
            ? this.searchDictionaryForm.value.vendorType
            : "";

        this.dictionaryData = [];
        this.dictionaryService.searchDictionary(vendor, vendorId, vendorType).subscribe(
            (response: any) => {
                this.dictionaryData = response.dictionaryList;
                this.totalRecords_Dic=response.dictionaryList.length;
                // this.totalRecords_Dic = response.dictionaryList.totalRecords;
                if (this.dictionaryAttributeData.dictionaryAttributeList.length > 0) {
                    this.showAddDicAttrButton = true;
                } else {
                    this.showAddDicAttrButton = false;
                }
                //
            },
            (error: any) => {
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');


                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                }
            }
        );
    }

    TotalItemPerPage_Dic(event) {
        this.showItemPerPage_Dic = Number(event.value);
        // if (this.currentPage_Dic > 1) {
        //     this.currentPage_Dic = 1;
        // }
        if (!this.searchkey_Dic) {
            this.getAllDictionary(this.showItemPerPage_Dic);
        } else {
            this.currentPage_Dic = 1
            this.searchDictionary();
        }
    }

    getAllDictionary(list) {
        //
        let size;
        this.searchkey_Dic = "";
        let page = this.currentPage_Dic;
        if (list) {
            size = list;
            this.itemsPerPage_Dic = list;
        } else {
            size = this.itemsPerPage_Dic;
        }
        this.dictionaryService.findAllDictionary(page, size, "", "", "").subscribe(
            (response: any) => {
                this.dictionaryData = response.dictionaryList;
                this.totalRecords_Dic = response.dictionaryList.length;
                //
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                //
            }
        );
    }

    showAddDicAttrButton = false;
    dictionaryAttributeData: any = [];
    dictionaryAttributeValueData: any = [];
    selectedDictionaryId = "";
    selectedVendor = "";

    selectedRowVendorName = "";
    dictionaryIdToCompare = "";

    getDictionaryAttributes(dictionaryId, vendor, selectedMvnoId, list, openDialog = true) {


        this.clickViewDictAtt = true;
        //
        this.dictionaryMvnoId = selectedMvnoId;
        this.dictionaryIdToCompare = dictionaryId;
        if (!this.dictionaryPopupIsOpen) {
            this.showAddDicAttrButton = true;
            this.getAttributeCategories();
            this.selectedRowVendorName = vendor;
            // this.currentPage_DicAttr = 1;
            let size;
            this.searchkey_DicAttr = "";
            let page = this.currentPage_DicAttr;
            if (list) {
                size = list;
                this.itemsPerPage_DicAttr = list;
            } else {
                size = this.itemsPerPage_DicAttr;
            }
            if (this.viewDictAttrAccess && this.clickViewDictAtt && openDialog) {
                this.dialog.open(this.dictionarAttributeDailog, { width: '80%' });
            }

            this.dictionaryService
                .getDictionaryAttributes(dictionaryId, selectedMvnoId, page, size, "")
                .subscribe(
                    (response: any) => {
                        this.selectedDictionaryId = dictionaryId;
                        this.selectedVendor = vendor;
                        this.dictionaryAttributeData = response.dictionaryAttributeList;
                        // this.totalRecords_DicAttr = response.dictionaryAttributeList.totalRecords;
                        if (this.dictionaryAttributeData.length > 0) {
                            //this.getAttributeCategories();
                            //this.showAddDicAttrButton = true;
                        }
                        //
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



                        //
                    }
                );
        }
    }

    newDictionaryData = {
        vendor: "",
        vendorId: "",
        vendorType: [],
        mvnoId: ""
    };

    newDictionaryDataForUpdate = {
        dictionaryId: "",
        vendor: "",
        vendorId: "",
        vendorType: [],
        mvnoId: ""
    };

    mapFormDataWithObject() {
        this.newDictionaryData.vendor = this.dictionaryForm.value.vendor;
        this.newDictionaryData.vendorId = this.dictionaryForm.value.vendorId;
        this.newDictionaryData.vendorType = this.dictionaryForm.value.vendorType;
        this.newDictionaryData.mvnoId = this.dictionaryForm.value.mvnoName;
    }

    mapFormDataWithObjectOnUpdate() {
        this.newDictionaryDataForUpdate.dictionaryId = this.selectedDictioanryId;
        this.newDictionaryDataForUpdate.vendor = this.dictionaryForm.value.vendor;
        this.newDictionaryDataForUpdate.vendorId = this.dictionaryForm.value.vendorId;
        this.newDictionaryDataForUpdate.vendorType = this.dictionaryForm.value.vendorType;
        this.newDictionaryDataForUpdate.mvnoId = this.dictionaryForm.value.mvnoName;
    }
    submitDictionary(dialogRef) {
        if (this.dictionaryForm.invalid) {
            this.dictionaryForm.markAllAsTouched();  // 👈 important
            return;
        }
        if (!this.editMode) {
            this.mapFormDataWithObject();
            this.addDictionary(dialogRef);
        } else {
            this.mapFormDataWithObjectOnUpdate();
            this.updateDictionary(dialogRef);
        }
    }

    addDictionary(dialogRef) {
        //
        this.dictionaryService.addDictionary(this.newDictionaryData).subscribe(
            (response: any) => {
                dialogRef.close()
                this.reset();
                this.getAllDictionary("");
                // this.dictionaryData = response.dictionaryList.data;
                this.closebutton.nativeElement.click();
                this.toastr.success(`${response.message}`, 'Successfully!');


                //
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                //
            }
        );
    }

    selectedDictioanryId = "";
    editDictionaryById(selectMvnoid, index, selectedDictioanryId) {
        if (this.validateUserToPerformOperations(selectMvnoid)) {
            this.selectedDictioanryId = selectedDictioanryId;

            if (this.dictionaryIdToCompare != selectedDictioanryId) {
                this.reset();
                this.refreshDictionaryAttributes();
                this.selectedRowVendorName = "";
            }
            this.dialog.open(this.dictionaryDialog, { width: '500px' });
            this.dictionaryPopupIsOpen = true;
            this.editMode = true;

            this.dictionaryService.findDictionaryById(selectedDictioanryId).subscribe((response: any) => {
                let dicnoryValue = response.dictionary;
                this.dictionaryForm.patchValue({
                    vendor: dicnoryValue.vendor,
                    vendorId: dicnoryValue.vendorId,
                    vendorType: dicnoryValue.vendorType,
                    mvnoName: dicnoryValue.mvnoId,
                    mvnoId: dicnoryValue.mvnoId
                });
            });
        }

        //
    }

    updateDictionary(dialogRef) {


        this.dictionaryService.updateDictionary(this.newDictionaryDataForUpdate).subscribe(
            (response: any) => {
                this.selectedVendor = response.dictionary.vendor;

                dialogRef.close()
                this.reset();
                if (!this.searchkey_Dic) {
                    this.getAllDictionary("");
                } else {
                    this.searchDictionary();
                }
                this.dictionaryData = response;
                this.toastr.success(`${response.message}`, 'Success!');


                this.closebutton.nativeElement.click();
                //
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                //
            }
        );
    }
    deleteConfirm(dictionaryId, vendor, selectedMvnoId) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this Dictionary?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteDictionaryById(dictionaryId, vendor, selectedMvnoId);
                } else {
                    (error: any) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejecte!');
                    }

                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this Dictionary?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deleteDictionaryById(dictionaryId, vendor, selectedMvnoId);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });
        }
    }
    deleteDictionaryById(dictionaryId, vendor, selectedMvnoId) {
        //
        if (this.selectedRowVendorName != vendor) {
            this.reset();
            this.refreshDictionaryAttributes();
            this.selectedRowVendorName = "";
        }
        this.dictionaryPopupIsOpen = false;
        this.dictionaryService.deleteDictionary(dictionaryId, selectedMvnoId).subscribe(
            (response: any) => {
                if (this.currentPage_Dic != 1 && this.dictionaryData.length == 1) {
                    this.currentPage_Dic = this.currentPage_Dic - 1;
                }
                if (!this.searchkey_Dic) {
                    this.getAllDictionary("");
                } else {
                    this.searchDictionary();
                }
                this.reset();
                this.dictionaryData = response;
                this.toastr.success(`${response.message}`, 'Success!');

                //
            },
            (error: any) => {
                this.reset();
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                //
            }
        );
    }

    reset() {
        this.dictionaryForm.reset();
    }

    openDictionaryPopup() {
        this.dictionaryPopupIsOpen = true;
        this.editMode = false;
        this.reset();
        this.refreshDictionaryAttributes();
        this.selectedRowVendorName = "";
        this.dialog.open(this.dictionaryDialog, { width: '500px' });
    }

    closeDictionaryPopup() {
        this.dictionaryPopupIsOpen = false;
    }

    preventSearchAttributeOnDelete(dictionaryId) {
        this.dictionaryPopupIsOpen = true;
    }

    cancelClickedFun() {
        this.dictionaryPopupIsOpen = false;
    }

    /////////////////////////////////// Dictionary Attribute Code //////////////////////

    attributeEditMode = false;
    attributeCategories: any = [];

    pageIndex = 0
    pageSize = RadiusConstants.ITEMS_PER_PAGE;

    //Used for pagination
    totalRecords_DicAttr: string;
    currentPage_DicAttr = 1;
    itemsPerPage_DicAttr = RadiusConstants.ITEMS_PER_PAGE;

    pageLimitOptions_DicAttr = RadiusConstants.pageLimitOptions;
    showItemPerPage_DicAttr: any;
    searchkey_DicAttr: string;

    //Used for delete confirm message
    popoverMessageForAttribute: string =
        RadiusConstants.DELETE_CONFIRM_MESSAGE("Dictionary Attribute");

    pageChangedForDictionaryAttribute(pageNumber) {
        this.currentPage_DicAttr = pageNumber.pageIndex + 1;
        this.itemsPerPage_DicAttr = pageNumber.pageSize
        if (!this.searchkey_DicAttr) {
            this.getDictionaryAttributes(
                this.selectedDictionaryId,
                this.selectedVendor,
                this.dictionaryMvnoId,
                ""
            );
        } else {
            this.searchDictionaryAttribute();
        }
    }

    TotalItemPerPage_DicAttr(event) {
        this.showItemPerPage_DicAttr = Number(event.value);
        if (this.currentPage_DicAttr > 1) {
            this.currentPage_DicAttr = 1;
        }
        if (!this.searchkey_DicAttr) {
            this.getDictionaryAttributes(
                this.selectedDictionaryId,
                this.selectedVendor,
                this.dictionaryMvnoId,
                this.showItemPerPage_DicAttr
            );
        } else {
            this.searchDictionaryAttribute();
        }
    }

    dicAttrData: any = [];
    dictionaryAttribute: any = [];
    dictionaryOfAttribute: any = [];
    myModal: boolean = false;
    showDictionaryAttributeDetail(dictionaryAttributeId) {
        this.dictionaryValuePopupIsOpen = false;
        this.dialog.open(this.dictionaryAttributeDetailDialog, { width: '600px' });
        this.dictionaryService.findDictionaryAttributeById(dictionaryAttributeId).subscribe(
            response => {
                this.dicAttrData = response;
                this.dictionaryAttribute = this.dicAttrData.dictionaryAttribute;
                this.dictionaryOfAttribute = this.dicAttrData.dictionaryAttribute.dictionary;
                //
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                //
            }
        );
    }

    closeDicAttrDetailPopup() {
        this.dictionaryValuePopupIsOpen = true;
    }

    closeDicAttrAddUpdatePopup() {
        this.dictionaryValuePopupIsOpen = true;
    }

    getAttributeCategories() {
        //
        this.dictionaryService.getAttributeCategories().subscribe(
            (response: any) => {
                this.attributeCategories = response;
                const attribute = response.attributeCategoryList;
                this.attributeCategoryList = [];
                for (const category of attribute) {
                    this.attributeCategoryList.push({ label: category });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    searchDictionaryAttribute() {
        if (
            !this.searchkey_DicAttr ||
            this.searchkey_DicAttr !== this.searchDictionaryAttributeForm.value.name
        ) {
            this.currentPage_DicAttr = 1;
        }
        if (this.showItemPerPage_DicAttr) {
            this.itemsPerPage_DicAttr = this.showItemPerPage_DicAttr;
        }

        let name = this.searchDictionaryAttributeForm.value.name
            ? this.searchDictionaryAttributeForm.value.name
            : "";
        let dictionaryId = this.dictionaryIdToCompare;
        this.searchkey_DicAttr = name;
        this.dictionaryAttributeData = [];
        this.dictionaryService.searchDictionaryAttribute(name, dictionaryId).subscribe(
            (response: any) => {
                this.dictionaryAttributeData = response.dictionaryAttributeList;
                // this.totalRecords_DicAttr = response.dictionaryAttributeList.totalRecords;
                //
            },
            (error: any) => {
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                }
            }
        );
    }

    clearAttributeSearchForm() {
        this.searchDictionaryAttributeForm.reset();
        this.getDictionaryAttributes(
            this.selectedDictionaryId,
            this.selectedVendor,
            this.dictionaryMvnoId,
            "",
            false
        );
    }

    isValidToOpenDicAttrPopup = true;
    openDictionaryAttributePopup() {
        this.attributeEditMode = false;
        this.resetDictAttr();
        this.dictionaryAttributeForm.patchValue({
            vendor: this.selectedVendor,
            mvnoName: this.mvnoId
        });
        this.dialog.open(this.dictionaryAttributeDialog, { width: '50%' });
    }

    resetDictAttr() {
        this.dictionaryAttributeForm.reset();
    }

    newDictionaryAttributeData = {
        name: "",
        attributeId: "",
        type: "",
        vendor: "",
        category: [],
        mvnoId: ""
    };

    newDictionaryAttributeDataOnUpdate = {
        dictionaryAttributeId: "",
        name: "",
        attributeId: "",
        type: "",
        vendor: "",
        category: [],
        mvnoId: ""
    };

    mapDictAttrFormDataWithObject() {
        this.newDictionaryAttributeData.name = this.dictionaryAttributeForm.value.name;
        this.newDictionaryAttributeData.attributeId = this.dictionaryAttributeForm.value.attributeId;
        this.newDictionaryAttributeData.type = this.dictionaryAttributeForm.value.type;
        this.newDictionaryAttributeData.vendor = this.dictionaryAttributeForm.value.vendor;
        this.newDictionaryAttributeData.category = this.dictionaryAttributeForm.value.category;
        this.newDictionaryAttributeData.mvnoId = this.dictionaryMvnoId;
    }

    mapDictAttrFormDataWithObjectOnUpdate() {
        this.newDictionaryAttributeDataOnUpdate.dictionaryAttributeId =
            this.selectedDictionaryAttributeIdForUpdate;
        this.newDictionaryAttributeDataOnUpdate.name = this.dictionaryAttributeForm.value.name;
        this.newDictionaryAttributeDataOnUpdate.attributeId =
            this.dictionaryAttributeForm.value.attributeId;
        this.newDictionaryAttributeDataOnUpdate.type = this.dictionaryAttributeForm.value.type;
        this.newDictionaryAttributeDataOnUpdate.vendor = this.dictionaryAttributeForm.value.vendor;
        this.newDictionaryAttributeDataOnUpdate.category = this.dictionaryAttributeForm.value.category;
        this.newDictionaryAttributeDataOnUpdate.mvnoId = this.dictionaryMvnoId;
    }

    submitDictionaryAttribute() {
        if (this.validateUserToPerformOperations(this.dictionaryAttributeForm.value.mvnoName)) {
            if (!this.attributeEditMode) {
                this.mapDictAttrFormDataWithObject();
                this.addDictionaryAttribute();
            } else {
                this.mapDictAttrFormDataWithObjectOnUpdate();
                this.updateDictionaryAttribute();
            }
        }
    }

    addDictionaryAttribute() {
        //
        this.dictionaryService.addDictionaryAttribute(this.newDictionaryAttributeData).subscribe(
            (response: any) => {
                this.resetDictAttr();
                this.getDictionaryAttributes(
                    this.selectedDictionaryId,
                    this.newDictionaryAttributeData.vendor,
                    this.dictionaryMvnoId,
                    ""
                );
                this.toastr.success(`${response.message}`, 'Success!');


                this.dictionaryAttributeData = response;
                this.closeDicAttrPopupButton.nativeElement.click();
                //
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                //
            }
        );
    }

    selectedDictionaryAttributeIdForUpdate = "";

    editDictionaryAttributeById(selectmvnoid, index, attributesID) {
        if (this.validateUserToPerformOperations(selectmvnoid)) {
            this.selectedDictionaryAttributeIdForUpdate = attributesID;
            // index = this.radiusUtility.getIndexOfSelectedRecord(
            //   index,
            //   this.currentPage_DicAttr,
            //   this.itemsPerPage_DicAttr
            // );
            this.dictionaryValuePopupIsOpen = false;
            this.attributeEditMode = true;
            // $("#dictionaryAttributeModal").modal("show");
            this.dialog.open(this.dictionaryAttributeDialog, { width: '50%' });
            this.dictionaryService
                .findDictionaryAttributeById(attributesID)
                .subscribe((response: any) => {
                    let dicnoryValue = response.dictionaryAttribute;
                    this.dictionaryAttributeForm.patchValue({
                        vendor: this.selectedVendor,
                        attributeId: dicnoryValue.attributeId,
                        category: dicnoryValue.category,
                        name: dicnoryValue.name,
                        type: dicnoryValue.type,
                        mvnoName: dicnoryValue.mvnoId
                    });
                });
        }
        //
    }

    updateDictionaryAttribute() {
        //
        this.dictionaryService
            .updateDictionaryAttribute(this.newDictionaryAttributeDataOnUpdate)
            .subscribe(
                (response: any) => {
                    this.resetDictAttr();
                    if (!this.searchkey_DicAttr) {
                        this.getDictionaryAttributes(
                            this.selectedDictionaryId,
                            this.newDictionaryAttributeDataOnUpdate.vendor,
                            this.dictionaryMvnoId,
                            ""
                        );
                    } else {
                        this.searchDictionaryAttribute();
                    }
                    this.dictionaryAttributeData = response;
                    this.closeDicAttrPopupButton.nativeElement.click();
                    this.toastr.success(`${response.message}`, 'Success!');


                    //
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                    //
                }
            );
    }
    deleteConfirmAttribute(dictionaryAttributeId, selectedMvnoId) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this Dictionary Attribute?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteDictionaryAttributeById(dictionaryAttributeId, selectedMvnoId);
                } else {
                    (error: any) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejecte!');
                    }

                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this Dictionary Attribute?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deleteDictionaryAttributeById(dictionaryAttributeId, selectedMvnoId);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });
        }
    }
    deleteDictionaryAttributeById(dictionaryAttributeId, selectedMvnoId) {
        //
        this.dictionaryValuePopupIsOpen = true;
        this.dictionaryService
            .deleteDictionaryAttribute(dictionaryAttributeId, selectedMvnoId)
            .subscribe(
                (response: any) => {
                    this.resetDictAttr();
                    if (this.currentPage_DicAttr != 1 && this.dictionaryAttributeData.length == 1) {
                        this.currentPage_DicAttr = this.currentPage_DicAttr - 1;
                    }
                    if (!this.searchkey_DicAttr) {
                        this.getDictionaryAttributes(
                            this.selectedDictionaryId,
                            this.selectedVendor,
                            selectedMvnoId,
                            ""
                        );
                    } else {
                        this.searchDictionaryAttribute();
                    }
                    this.dictionaryAttributeData = response;
                    this.toastr.success(`${response.message}`, 'Success!');

                    //
                },
                (error: any) => {
                    this.resetDictAttr();
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    //
                }
            );
    }

    preventShowValuePopupOnDelete() {
        this.dictionaryValuePopupIsOpen = false;
    }

    fromDeleteCancel: boolean = false;
    cancelDeleteAttribute() {
        this.dictionaryValuePopupIsOpen = true;
        this.removeRowSelection();
        this.fromDeleteCancel = true;
    }

    removeRowSelection() {
        this.selectedRowDicAttrName = "";
        this.myModal = false;
    }

    ///////////////////////////// Dictionary Value Code ///////////////////////////////////

    dicValueEditMode = false;
    dictionaryValueData: any = [];

    selectedDictionaryAttributeId = "";
    selectedDictionaryValueName = "";
    selectedDictionaryAttributeName = "";

    //Used for pagination
    totalRecords_DicValue: String;
    currentPage_DicValue = 1;
    itemsPerPage_DicValue = RadiusConstants.ITEMS_PER_PAGE;

    //Used for delete confirm message
    popoverMessageForDicValue: string = RadiusConstants.DELETE_CONFIRM_MESSAGE("Dictionary Value");

    pageChangedForDictionaryValue(pageNumber) {
        this.currentPage_DicAttr = pageNumber;
    }

    selectedRowDicAttrName = "";
    showItemPerPageValue = "";
    // TotalItemPerDiAtValuePage(event){
    //   this.showItemPerPageValue = Number(event.value);
    //   if (this.currentPage_DicValue > 1) {
    //     this.currentPage_DicValue = 1
    //   }
    //   if (!this.searchkeyValue) {
    //     this.getDictionaryAttributeValues(dictionaryAttributeId,
    //       dictionaryAttributeName,
    //       this.dictionaryMvnoId);
    //   }
    //   else {
    //     this.searchDictionaryValue();
    //   }
    // }

    getDictionaryAttributeValues(dictionaryAttributeId, dictionaryAttributeName, selectedMvnoId) {
        this.dictionaryMvnoId = selectedMvnoId;
        if (this.dictionaryValuePopupIsOpen) {
            this.resetDictValue();
            if (!this.fromDeleteCancel) {
                this.selectedRowDicAttrName = dictionaryAttributeName;
            } else {
                this.fromDeleteCancel = false;
            }
            this.currentPage_DicValue = 1;

            this.dictionaryService.getDictionaryValues(dictionaryAttributeId).subscribe(
                (response: any) => {
                    this.selectedDictionaryAttributeId = dictionaryAttributeId;
                    this.selectedDictionaryAttributeName = dictionaryAttributeName;
                    this.dictionaryValueData = response;
                    //
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                    //
                }
            );
        }
    }

    attributesRowClick() {
        this.myModal = true;
        this.dialog.open(this.dictionaryValueDialog, { width: '70%' });
    }

    searchDictionaryValue() {
        //
        this.resetDictValue();
        this.currentPage_DicValue = 1;
        this.dictionaryValueData = [];
        this.dictionaryService
            .searchDictionaryValues(
                this.searchDictionaryValueForm.value.name,
                this.searchDictionaryValueForm.value.value,
                this.selectedDictionaryAttributeId
            )
            .subscribe(
                (response: any) => {
                    this.dictionaryValueData = response;
                    //
                },
                (error: any) => {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    if (error.error.status == 404) {

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                    }
                }
            );
    }

    clearSearchDictionaryValueForm() {
        this.searchDictionaryValueForm.reset();
        this.getDictionaryAttributeValues(
            this.selectedDictionaryAttributeId,
            this.selectedDictionaryAttributeName,
            this.dictionaryAttributeMvnoId
        );
        this.resetDictValue();
    }

    clearDictionaryValueForm() {
        this.dictionaryValueForm.reset();
        this.dicValueEditMode = false;
    }

    resetDictValue() {
        this.clearDictionaryValueForm();
    }

    newDictionaryValueData = {
        name: "",
        value: "",
        dictionaryAttributeName: "",
        mvnoId: "",
        dictionaryAttributeId: ""
    };

    newDictionaryValueDataOnUpdate = {
        dictionaryValueId: "",
        name: "",
        value: "",
        dictionaryAttributeName: "",
        mvnoId: "",
        dictionaryAttributeId: ""
    };

    mapDictValueFormDataWithObject() {
        this.newDictionaryValueData.name = this.dictionaryValueForm.value.name;
        this.newDictionaryValueData.value = this.dictionaryValueForm.value.value;
        this.newDictionaryValueData.dictionaryAttributeName = this.selectedDictionaryAttributeName;
        this.newDictionaryValueData.mvnoId = this.dictionaryMvnoId;
        this.newDictionaryValueData.dictionaryAttributeId = this.selectedDictionaryAttributeId;
    }

    mapDictValueFormDataWithObjectOnUpdate() {
        this.newDictionaryValueDataOnUpdate.dictionaryValueId = this.selectedDictionaryValueIdForUpdate;
        this.newDictionaryValueDataOnUpdate.name = this.dictionaryValueForm.value.name;
        this.newDictionaryValueDataOnUpdate.value = this.dictionaryValueForm.value.value;
        this.newDictionaryValueDataOnUpdate.dictionaryAttributeName =
            this.selectedDictionaryAttributeName;
        this.newDictionaryValueDataOnUpdate.mvnoId = this.dictionaryMvnoId;
        this.newDictionaryValueDataOnUpdate.dictionaryAttributeId = this.selectedDictionaryAttributeId;
    }

    submitDictionaryValue(dialogRef) {
        if (this.dictionaryValueForm.invalid) {
            this.dictionaryValueForm.markAllAsTouched();  // 👈 important
            return;
        }
        if (this.dictionaryValueForm.value.mvnoName == null)
            this.dictionaryValueForm.setValue({
                mvnoName: this.mvnoId,
                name: this.dictionaryValueForm.value.name,
                value: this.dictionaryValueForm.value.value
            });
        if (this.validateUserToPerformOperations(this.dictionaryValueForm.value.mvnoName)) {
            if (!this.dicValueEditMode) {
                this.mapDictValueFormDataWithObject();
                this.addDictionaryValue(dialogRef);
            } else {
                this.mapDictValueFormDataWithObjectOnUpdate();
                this.updateDictionaryValue(dialogRef);
            }
        }
    }

    addDictionaryValue(dialogRef) {
        //
        this.dictionaryService.addDictionaryValue(this.newDictionaryValueData).subscribe(
            (response: any) => {
                dialogRef.close()
                this.resetDictValue();
                this.getDictionaryAttributeValues(
                    this.selectedDictionaryAttributeId,
                    this.selectedDictionaryAttributeName,
                    this.dictionaryAttributeMvnoId
                );
                this.dictionaryValueData = response;
                this.toastr.success(`${response.message}`, 'Success!');

                //
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                //
            }
        );
    }

    openDictonryValueDailog() {
        this.dialog.open(this.createDictionaryValueDailog, { width: '50%' });
    }
    selectedDictionaryValueIdForUpdate = "";
    editDictionaryValueById(dictionaryValueId, index, selectedMvnoId) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            this.selectedDictionaryValueIdForUpdate = dictionaryValueId;

            this.dicValueEditMode = true;
            index = this.radiusUtility.getIndexOfSelectedRecord(
                index,
                this.currentPage_DicValue,
                this.itemsPerPage_DicValue
            );

            this.dialog.open(this.createDictionaryValueDailog, { width: '50%' });
            this.dictionaryService.getDictionaryVal(dictionaryValueId).subscribe((response: any) => {
                let dicnoryValue = response.dictionaryValue;
                this.dictionaryValueForm.patchValue({
                    name: dicnoryValue.name,
                    value: dicnoryValue.value,
                    mvnoName: dicnoryValue.mvnoId
                });
            });
        }
        //
    }

    updateDictionaryValue(dialogRef) {
        //
        this.dictionaryService.updateDictionaryValue(this.newDictionaryValueDataOnUpdate).subscribe(
            (response: any) => {
                dialogRef.close()
                this.resetDictValue();
                this.getDictionaryAttributeValues(
                    this.selectedDictionaryAttributeId,
                    this.selectedDictionaryAttributeName,
                    this.dictionaryAttributeMvnoId
                );
                this.dictionaryValueData = response;
                this.toastr.success(`${response.message}`, 'Success!');


                this.dicValueEditMode = false;
                //
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                //
            }
        );
    }
    deleteConfirmValue(dictionaryValueId, selectedMvnoId) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this Dictionary value?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteDictionaryValueById(dictionaryValueId, selectedMvnoId);
                } else {
                    (error: any) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejecte!');
                    }

                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this Dictionary value?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deleteDictionaryValueById(dictionaryValueId, selectedMvnoId);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });
        }
    }
    deleteDictionaryValueById(dictionaryValueId, selectedMvnoId) {
        //
        this.dictionaryService.deleteDictionaryValue(dictionaryValueId, selectedMvnoId).subscribe(
            (response: any) => {
                this.resetDictValue();
                this.getDictionaryAttributeValues(
                    this.selectedDictionaryAttributeId,
                    this.selectedDictionaryAttributeName,
                    selectedMvnoId
                );
                this.dictionaryValueData = response;
                this.toastr.success(`${response.message}`, 'Success!');


                //
            },
            (error: any) => {
                this.resetDictValue();
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                //
            }
        );
    }

    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        this.userId = localStorage.getItem("userId");
        if (this.userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            //  this.reset();
            (error: any) => {
                this.toastr.info(`${error.responseMessage}`, 'You are not authorized to do this operation. Please contact to the administrator!');
            }

            //  this.modalToggle = false;
            return false;
        }
        return true;
    }


    @ViewChild('dictionarAttributeDailog') dictionarAttributeDailog!: TemplateRef<any>;

    @ViewChild('createDictionaryValueDailog') createDictionaryValueDailog!: TemplateRef<any>;


    @ViewChild('dictionaryDialog') dictionaryDialog!: TemplateRef<any>;
    @ViewChild('dictionaryAttributeDetailDialog') dictionaryAttributeDetailDialog!: TemplateRef<any>;
    @ViewChild('dictionaryValueDialog') dictionaryValueDialog!: TemplateRef<any>;
    @ViewChild('dictionaryAttributeDialog') dictionaryAttributeDialog!: TemplateRef<any>;
    dictionaryDisplayedColumns: string[] = ['id', 'vendor', 'vendorId', 'vendorType', 'action'];
    dictionaryAttrDisplayedColumns: string[] = ['name', 'category', 'type', 'action'];
    displayedColumnsValue: string[] = ['name', 'type', 'value', 'action'];


    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;

}
