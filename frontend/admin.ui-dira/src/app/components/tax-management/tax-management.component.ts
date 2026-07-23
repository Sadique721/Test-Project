import { ChangeDetectorRef, Component, OnInit, ViewChild } from "@angular/core";
import {
    UntypedFormBuilder,
    Validators,
    UntypedFormGroup,
    FormControl,
    UntypedFormArray
} from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { TaxManagementService } from "src/app/service/tax-management.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { TaxManagement } from "src/app/components/model/tax-management";
import { Regex } from "src/app/constants/regex";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { PRODUCTS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from 'ngx-toastr';
import { error } from "console";

@Component({
    selector: "app-tax-management",
    templateUrl: "./tax-management.component.html",
    styleUrls: ["./tax-management.component.css"],
    standalone: false
})
export class TaxManagementComponent implements OnInit {
    charecter150 = "^.{0,150}$";

    taxGroupForm: UntypedFormGroup;
    submitted = false;
    taxTypeAll: any = [];
    taxTypeProperty: any = [];
    taxTypeSlab: UntypedFormArray;
    taxTypeTiered: UntypedFormArray;
    taxTypeCompund: UntypedFormArray;
    currentPageTaxSlab = 1;
    TaxSlabitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    TaxSlabtotalRecords: String;
    currentPageTaxTiered = 0;
    TaxTiereditemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    TaxTieredtotalRecords: number;
    currentPageTaxListdata = 1;
    TaxListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    TaxListdatatotalRecords: any;
    createTaxData: TaxManagement;
    taxListData: any = [];
    viewTaxListData: any = [];
    isTaxEdit: boolean = false;
    searchTaxName: any = "";
    searchTaxType: any = "";
    searchData: any;

    listView: boolean = true;
    createView: boolean = false;
    taxALLDeatilsShow: boolean = false;
    taxAllData: any;

    //type Tiered list
    typeTieredItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    typeTieredtotalRecords: String;
    currentPagetypeTieredList = 1;

    //slab
    typeslabItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    typeslabtotalRecords: String;
    currentPagetypeslabList = 1;

    typeslebForm: UntypedFormGroup;
    typeTieredForm: UntypedFormGroup;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;

    typeTieredSubmitted = false;
    typeSlebSubmitted = false;

    taxGroup = [
        { label: "Tier-1", value: "TIER1" },
        { label: "Tier-2", value: "TIER2" },
        { label: "Tier-3", value: "TIER3" }
    ];
    taxGroupCompound = [{ label: "Compound", value: "Compound" }];

    infoMessage: string = "";
    infoMessageDisplayed: boolean = false;

    statusOptions = RadiusConstants.status;
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    beforeTax = [
        { label: "True", value: true },
        { label: "False", value: false },
        { label: "NA", value: 'NA' }
    ];
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    taxTired: { label: string; value: string }[];
    taxCompound: { label: string; value: string }[];
    taxGroupOptions = [
        { label: "Tier-1", value: "TIER1" },
        { label: "Tier-2", value: "TIER2" },
        { label: "Tier-3", value: "TIER3" }
    ];
    isCompound: boolean = false;
    constructor(
        private toastr: ToastrService,

        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private taxManagementService: TaxManagementService,
        loginService: LoginService,
        private dialog: MatDialog,
        private cdr: ChangeDetectorRef
    ) {
        this.createAccess = loginService.hasPermission(PRODUCTS.TAX_CREATE);
        this.deleteAccess = loginService.hasPermission(PRODUCTS.TAX_DELETE);
        this.editAccess = loginService.hasPermission(PRODUCTS.TAX_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        // this.isTaxEdit = !this.createAccess && this.editAccess ? true : false;
    }
    taxTypeTieredDataSource = new MatTableDataSource<any>([]);
    taxTierListDataSource = new MatTableDataSource<any>([]);
    taxTierListCurrentPage = 0;
    taxTierListTotalRecords = 0;
    taxTierListPageSize = RadiusConstants.ITEMS_PER_PAGE;
    taxTierListDisplayedData = [];

    @ViewChild("createUpdateModal") createUpdateModal;
    @ViewChild("allDetailsShow") allDetailsShow;
    ngOnInit(): void {
        this.taxGroupForm = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            taxtype: ["TIER", Validators.required],
            status: ["", Validators.required],
            desc: ["", [Validators.required, Validators.pattern(this.charecter150)]]
            //ledgerId: [""],
        });

        this.typeslebForm = this.fb.group({
            name: ["", Validators.required],
            rangeFrom: ["", [Validators.required, Validators.pattern(Regex.numeric)]],
            rangeUpTo: ["", [Validators.required, Validators.pattern(Regex.numeric)]],
            rate: [
                "",
                [
                    Validators.required,
                    Validators.pattern(Regex.decimalNumber),
                    Validators.max(99),
                    Validators.min(0)
                ]
            ],
            beforeDiscount: [false]
        });

        this.typeTieredForm = this.fb.group({
            name: ["", Validators.required],
            rate: ["", [Validators.required, Validators.max(99), Validators.min(0)]],
            taxGroup: ["", Validators.required],
            beforeDiscount: [false],
            ledgerId: [""]
        });

        this.taxTypeSlab = this.fb.array([]);
        this.taxTypeTiered = this.fb.array([]);
        this.taxTypeCompund = this.fb.array([]);

        // this.onAddTaxTypeSlabField();
        // this.onAddTaxTypeTieredField();
        this.getTaxType();
        this.getTaxDataList("");
        this.taxTypeSlab.disable();
        this.taxTypeTiered.enable();
        this.taxTypeCompund.enable();
        this.createTaxTypeSlabFormGroup();

        this.searchData = {
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
            pageSize: ""
        };
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(PRODUCTS.TAX_DELETE) || this.loginService.hasPermission(PRODUCTS.TAX_EDIT)) {
            return ['name', 'type', 'status', 'ispName', 'action'];
        } else {
            return ['name', 'type', 'status', 'ispName'];
        }
    }

    createTax() {
        this.taxALLDeatilsShow = false;
        this.submitted = false;
        this.isTaxEdit = false;

        // Reset pagination
        this.currentPageTaxTiered = 0;
        this.TaxTieredtotalRecords = 0;

        this.taxTypeSlab.disable();
        this.taxTypeTiered.enable();
        this.taxGroupForm.reset();
        this.taxTypeSlab.reset();
        this.taxTypeTiered.reset();
        this.typeTieredForm.reset();

        if (this.taxTypeSlab.controls) {
            this.taxTypeSlab.controls = [];
        }
        if (this.taxTypeTiered.controls) {
            this.taxTypeTiered.controls = [];
        }

        // Reset data source
        this.taxTypeTieredDataSource.data = [];

        this.taxGroupForm.controls.taxtype.setValue("TIER");
        this.taxGroupForm.controls.status.setValue("");
        this.typeTieredForm.get("beforeDiscount").enable();

        this.dialog.open(this.createUpdateModal, {
            width: "80%"
        });
    }

    listTax() {
        this.listView = true;
        this.createView = false;
        this.taxALLDeatilsShow = false;

        this.getTaxDataList("");
        this.searchTaxName = "";
        this.searchTaxType = "";
    }
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageTaxListdata > 1) {
            this.currentPageTaxListdata = 1;
        }
        if (!this.searchkey) {
            this.getTaxDataList(this.showItemPerPage);
        } else {
            this.searchTax();
        }
    }

    getTaxDataList(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPageTaxListdata;
        if (list) {
            size = list;
            this.TaxListdataitemsPerPage = list;
        } else {
            size = this.TaxListdataitemsPerPage;
        }

        let plandata = {
            page: page,
            pageSize: size
        };
        this.taxManagementService.TaxAllData(plandata).subscribe(
            (response: any) => {
                this.taxListData = response;
                this.TaxListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    createTaxTypeSlabFormGroup(): UntypedFormGroup {
        return this.fb.group({
            name: [this.typeslebForm.value.name],
            rangeFrom: [this.typeslebForm.value.rangeFrom, [Validators.pattern(Regex.numeric)]],
            rangeUpTo: [this.typeslebForm.value.rangeUpTo, [Validators.pattern(Regex.numeric)]],
            rate: [this.typeslebForm.value.rate, [Validators.pattern(Regex.decimalNumber)]],
            id: [""],
            beforeDiscount: [false]
        });
    }

    createTaxTypeTieredFormGroup(): UntypedFormGroup {
        return this.fb.group({
            name: [this.typeTieredForm.get("name")?.value || "", Validators.required],
            rate: [
                this.typeTieredForm.get("rate")?.value || "",
                [
                    Validators.required,
                    Validators.pattern(Regex.decimalNumber),
                    Validators.max(99),
                    Validators.min(0)
                ]
            ],
            taxGroup: [this.typeTieredForm.get("taxGroup")?.value || "", Validators.required],
            id: [""],
            ledgerId: [this.typeTieredForm.get("ledgerId")?.value || ""],
            beforeDiscount: [this.typeTieredForm.get("beforeDiscount")?.value || false]
        });
    }

    onAddTaxTypeSlabField() {
        this.typeSlebSubmitted = true;
        if (this.typeslebForm.valid) {
            this.taxTypeSlab.push(this.createTaxTypeSlabFormGroup());
            this.typeslebForm.reset();
            this.typeSlebSubmitted = false;
        } else {
            // console.log("I am not valid");
        }
    }

    onAddTaxTypeTieredField() {
        this.typeTieredSubmitted = true;
        if (this.typeTieredForm.valid) {
            const form = this.createTaxTypeTieredFormGroup();
            let filtered = this.taxTypeTiered.value.filter(record => record.taxGroup === "Compound");
            let updatedValues;

            const hasCompound = this.taxTypeTiered.value.some(
                record => record.taxGroup === "Compound"
            );

            if (hasCompound) {
                form.get("beforeDiscount")?.setValue("NA");
                form.get("beforeDiscount")?.disable({ emitEvent: false });
            }

            if (filtered.length >= 1) {
                form.get("beforeDiscount")?.setValue("NA");
                form.get("beforeDiscount")?.disable({ emitEvent: false });
            }
            form.patchValue(updatedValues);
            this.taxTypeTiered.push(form);

            // Update total records
            this.TaxTieredtotalRecords = this.taxTypeTiered.length;

            // Update paginated data source
            this.updateTaxTieredDataSource();

            this.typeTieredForm.reset();
            let filteredRecords = this.taxTypeTiered.value.filter(
                record => record.taxGroup === "Compound"
            );
            if (filteredRecords.length >= 1) {
                this.typeTieredForm.get("taxGroup").setValue(filteredRecords[0].taxGroup);
                this.typeTieredForm.get("beforeDiscount").setValue('NA');
                this.typeTieredForm.get("beforeDiscount").disable();
                this.typeTieredForm.get("beforeDiscount")?.disable({ emitEvent: false });
            }

            this.typeTieredSubmitted = false;
            this.isCompound = false;
        } else {
            this.typeTieredForm.markAllAsTouched();
        }
    }

    updateTaxTieredDataSource() {
        const startIndex = this.currentPageTaxTiered * this.TaxTiereditemsPerPage;
        const endIndex = startIndex + this.TaxTiereditemsPerPage;

        // Get paginated data from all controls
        const paginatedData = this.taxTypeTiered.controls.slice(startIndex, endIndex);

        // Update the data source
        this.taxTypeTieredDataSource.data = paginatedData;

    }

    trackByFn(index: number, item: any): any {
        return index;
    }

    searchTax() {
        if (!this.searchkey || this.searchkey !== this.searchData) {
            this.currentPageTaxListdata = 1;
        }
        this.searchkey = this.searchData;
        if (this.showItemPerPage) {
            this.TaxListdataitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchTaxName.trim();
        this.searchData.filters[0].filterDataType = this.searchTaxType;

        this.searchData.page = this.currentPageTaxListdata;
        this.searchData.pageSize = this.TaxListdataitemsPerPage;
        // console.log("this.searchData", this.searchData)
        this.taxManagementService.searchTax(this.searchData).subscribe(
            (response: any) => {
                if (response?.taxlist?.length > 0) {
                    this.taxListData = response;
                    this.TaxListdatatotalRecords = response.pageDetails.totalRecords;
                } else {
                    this.taxListData.taxlist = [];
                    this.TaxListdatatotalRecords = 0;
                    this.toastr.info('No Record Found!', 'Info!');


                }
            },
            (error: any) => {
                this.TaxListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');


                    this.taxListData.taxlist = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            }
        );
    }

    clearSearchTax() {
        this.searchTaxName = "";
        this.searchTaxType = "";
        this.getTaxDataList("");
    }

    deleteConfirmonTaxTypeSlabField(TaxTypeSlabFieldIndex: number, TaxTypeSlabFieldId: number) {
        if (TaxTypeSlabFieldIndex || TaxTypeSlabFieldIndex == 0) {
            this.confirmationService.confirm({
                message: "Do you want to delete this Tax Type Mapping Attribute?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.onRemoveTaxTypeSlab(TaxTypeSlabFieldIndex, TaxTypeSlabFieldId);
                },
                reject: () => {

                    error: (error) => {
                        this.toastr.info(`${error.error.ERROR}`, 'You have rejected!');
                    }


                }
            });
        }
    }

    async onRemoveTaxTypeSlab(TaxTypeSlabFieldIndex: number, TaxTypeSlabFieldId: number) {
        this.taxTypeSlab.removeAt(TaxTypeSlabFieldIndex);
    }

    deleteConfirmonTaxTypeTieredField(TaxTypeTieredFieldIndex: number, TaxTypeTieredFieldId: number) {
        // console.log("TaxTypeFieldIndex", TaxTypeTieredFieldIndex);
        if (TaxTypeTieredFieldIndex || TaxTypeTieredFieldIndex == 0) {
            const taxType = this.taxTypeTiered.controls[TaxTypeTieredFieldIndex].get("taxGroup").value;

            if (taxType === "TIER2" && this.isTier1Added()) {
                error: (error) => {
                    this.toastr.info(`${error.error.ERROR}`, 'Please Remove Tier-1 first!');
                }

                this.infoMessageDisplayed = true;
                return;
            } else if (taxType === "TIER3" && (this.isTier1Added() || this.isTier2Added())) {
                error: (error) => {
                    this.toastr.info(`${error.error.ERROR}`, 'Please Remove Tier-1 and Tier-2 first!');
                }

                this.infoMessageDisplayed = true;
                return;
            } else {
                this.infoMessage = "";
                this.infoMessageDisplayed = false;
            }
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                data: {
                    title: "Delete Confirmation",
                    description: "Do you want to delete this Tax Type Mapping Attribute?",
                    yesLabel: "Delete",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.onRemoveTaxTypeTiered(TaxTypeTieredFieldIndex, TaxTypeTieredFieldId);
                } else {
                    error: (error) => {
                        this.toastr.info(`You have rejected`, 'Rejected!');
                    }

                }
            });
        }
    }

    async onRemoveTaxTypeTiered(TaxTypeTieredFieldIndex: number, TaxTypeTieredFieldId: number) {
        const actualIndex =
            this.currentPageTaxTiered * this.TaxTiereditemsPerPage + TaxTypeTieredFieldIndex;

        this.taxTypeTiered.removeAt(actualIndex);
        this.TaxTieredtotalRecords = this.taxTypeTiered.length;
        const maxPage = Math.ceil(this.TaxTieredtotalRecords / this.TaxTiereditemsPerPage) - 1;
        if (this.currentPageTaxTiered > maxPage && maxPage >= 0) {
            this.currentPageTaxTiered = maxPage;
        }
        const stillHasCompound = this.taxTypeTiered.value.some(
            record => record.taxGroup === "Compound"
        );
        if (!stillHasCompound || this.taxTypeTiered.length === 0) {
            const beforeDiscountCtrl = this.typeTieredForm.get("beforeDiscount");
            if (beforeDiscountCtrl) {
                beforeDiscountCtrl.enable({ emitEvent: false });
                beforeDiscountCtrl.reset(null, { emitEvent: false });
            }
        }

        this.updateTaxTieredDataSource();
    }


    addTax(id) {
        this.submitted = true;
        if (this.taxGroupForm.valid) {
            if (id) {
                this.taxTypeTiered.value.forEach(item => {
                    if (item.taxGroup === "Compound" && item.beforeDiscount === null) {
                        item.beforeDiscount = false;
                    }
                });
                this.createTaxData = this.taxGroupForm.value;
                if (this.taxGroupForm.controls.taxtype.value == "SLAB") {
                    this.createTaxData.slabList = this.taxTypeSlab.value;
                } else {
                    this.createTaxData.tieredList = this.taxTypeTiered.value;
                }
                this.taxManagementService.updateTaxMethod(id, this.createTaxData).subscribe(
                    (response: any) => {
                        this.toastr.success(`${response.msg}`, 'Success!');

                        this.closeDialog();
                        if (!this.searchkey) {
                            this.getTaxDataList("");
                        } else {
                            this.searchTax();
                        }
                        this.cleartaxData();
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        if (error.error.status == 406 || error.error.status == 417) {
                            this.toastr.info(`${error.error.ERROR}`, 'Info!');

                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                        }
                    }
                );
            } else {
                this.taxTypeTiered.value.forEach(item => {
                    if (item.taxGroup === "Compound" && item.beforeDiscount === null) {
                        item.beforeDiscount = false;
                    }
                });
                this.createTaxData = this.taxGroupForm.value;
                if (this.taxGroupForm.controls.taxtype.value == "SLAB") {
                    this.createTaxData.slabList = this.taxTypeSlab.value;
                } else {
                    this.createTaxData.tieredList = this.taxTypeTiered.value;
                }

                // console.log(" this.createTaxData", this.createTaxData);
                this.taxManagementService.addTaxMethod(this.createTaxData).subscribe(
                    (response: any) => {
                        this.toastr.success(`${response.msg}`, 'Success!');


                        this.closeDialog();
                        this.cleartaxData();
                        if (!this.searchkey) {
                            this.getTaxDataList("");
                        } else {
                            this.searchTax();
                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            }
        } else {
            this.taxGroupForm.markAllAsTouched();
        }
    }

    cleartaxData() {
        this.listView = true;
        this.createView = false;
        this.taxALLDeatilsShow = false;
        this.taxGroupForm.reset();
        this.submitted = false;
        this.isTaxEdit = false;

        // Reset pagination
        this.currentPageTaxTiered = 0;
        this.TaxTieredtotalRecords = 0;

        this.taxTypeSlab.disable();
        this.taxTypeTiered.enable();
        this.taxALLDeatilsShow = false;
        this.taxTypeSlab.controls = [];
        this.taxTypeTiered.controls = [];

        // Reset data source
        this.taxTypeTieredDataSource.data = [];

        this.taxGroupForm.controls.taxtype.setValue("TIER");
        this.taxGroupForm.controls.status.setValue("");
    }
    getTaxType() {
        this.taxManagementService.getAllTaxType().subscribe(
            (response: any) => {
                var obj = response.data;

                for (var i in obj) this.taxTypeAll.push({ label: obj[i], value: i });

                for (var property in obj) this.taxTypeProperty.push({ label: property });

                this.taxTypeAll = this.taxTypeAll.filter(data => data.value !== "SLAB");
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    getSelTaxType(event) {
        const selTaxType = event;
        if (selTaxType == "SLAB") {
            this.taxTypeSlab.enable();
            this.taxTypeCompund.disable();
            this.isCompound = false;
        } else if (selTaxType == "TIER") {
            this.taxTypeSlab.disable();
            this.taxTypeTiered.enable();
            this.taxGroupOptions = this.taxGroup;
            this.isCompound = false;
            this.typeTieredForm.get("taxGroup").setValue(this.taxGroupOptions[0].value);
            this.taxTypeTiered.controls.forEach(row => {
                const taxGroupControl = row.get("taxGroup");
                taxGroupControl.patchValue(this.taxGroupOptions[0].value);
            });
        } else if (selTaxType == "Compound") {
            this.taxTypeSlab.disable();
            this.taxTypeTiered.enable();
            this.taxGroupOptions = this.taxGroupCompound;
            this.isCompound = true;
            let filteredRecords = this.taxGroupOptions.filter(record => record.value === "Compound");
            this.typeTieredForm.get("taxGroup").setValue(filteredRecords[0].value);
            this.taxTypeTiered.controls.forEach(row => {
                const taxGroupControl = row.get("taxGroup");
                taxGroupControl.patchValue("Compound");
            });
            this.taxTypeTiered.controls.forEach((row, index) => {
                const taxGroupControl = row.get("beforeDiscount");
                if (index > 0) {
                    taxGroupControl.disable();
                }
            });
        } else {
            this.taxTypeSlab.disable();
            this.taxTypeTiered.disable();
            this.isCompound = false;
        }
    }

    canExit() {
        if (!this.taxGroupForm.dirty && !this.typeslebForm.dirty) return true;
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
    deleteConfirmonTax(tax: any): void {
        if (!tax?.id) return;

        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: `Delete Tax`,
                description: `Are you sure you want to delete "${tax.name || tax.taxName || 'this tax'}"?`,
                yesLabel: "Delete",
                noLabel: "Cancel"

            }

        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteTax(tax.id);
            }
            else {
            this.toastr.info(`You have rejected`, 'Rejected!');
            }
        });
    }


    editTax(id) {
        // this.listView = false;
        this.createView = true;
        this.taxALLDeatilsShow = false;
        this.taxGroupForm.reset();
        this.taxTypeSlab.reset();
        this.taxTypeTiered.reset();
        this.isTaxEdit = true;

        // Reset pagination
        this.currentPageTaxTiered = 0;

        if (this.taxTypeSlab.controls) {
            this.taxTypeSlab.controls = [];
        }
        if (this.taxTypeTiered.controls) {
            this.taxTypeTiered.controls = [];
        }
        this.dialog.open(this.createUpdateModal, {
            width: "1000px"
        });
        this.taxManagementService.getTaxDetailById(id).subscribe(
            (response: any) => {
                this.viewTaxListData = response.taxData;
                this.getSelTaxType(this.viewTaxListData.taxtype);

                this.taxGroupForm.patchValue({
                    name: this.viewTaxListData.name,
                    status: this.viewTaxListData.status,
                    taxtype: this.viewTaxListData.taxtype,
                    desc: this.viewTaxListData.desc,
                    ledgerId: this.viewTaxListData.ledgerId,
                    beforeDiscount: this.viewTaxListData.beforeDiscount
                });

                if (this.viewTaxListData.taxtype == "SLAB") {
                    this.taxTypeSlab = this.fb.array([]);
                    this.viewTaxListData.slabList.forEach(element => {
                        this.taxTypeSlab.push(this.fb.group(element));
                    });
                    this.taxTypeSlab.patchValue(this.viewTaxListData.slabList);
                } else if (this.viewTaxListData.taxtype == "TIER") {
                    this.taxTypeTiered = this.fb.array([]);
                    this.viewTaxListData.tieredList.forEach(element => {
                        this.taxTypeTiered.push(this.fb.group(element));
                    });
                    this.taxTypeTiered.patchValue(this.viewTaxListData.tieredList);

                    // Update total records and data source for edit mode
                    this.TaxTieredtotalRecords = this.taxTypeTiered.length;
                    this.updateTaxTieredDataSource();
                } else if (this.viewTaxListData.taxtype == "Compound") {
                    this.taxTypeTiered = this.fb.array([]);
                    this.viewTaxListData.tieredList.forEach((element, index) => {
                        const group = this.fb.group(element);
                        if (index >= 1) {
                            group.get("beforeDiscount").setValue(null);
                        }
                        this.taxTypeTiered.push(group);
                    });

                    // Update total records and data source for compound mode
                    this.TaxTieredtotalRecords = this.taxTypeTiered.length;
                    this.updateTaxTieredDataSource();
                } else {
                    this.taxTypeSlab = this.fb.array([]);
                    this.viewTaxListData.slabList.forEach(element => {
                        this.taxTypeSlab.push(this.fb.group(element));
                    });
                    this.taxTypeSlab.patchValue(this.viewTaxListData.slabList);
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    deleteTax(TaxId) {
        this.taxManagementService.deleteTaxMethod(TaxId).subscribe(
            (response: any) => {
                if (this.currentPageTaxListdata != 1 && this.taxListData.length == 1) {
                    this.currentPageTaxListdata = this.currentPageTaxListdata - 1;
                }
                if (response.responseCode == 405) {
                    this.toastr.info(`${response.responseMessage}`, 'Failed!');


                } else if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    this.toastr.success(`Successfully Deleted`, 'Success!');


                }
                if (!this.searchkey) {
                    this.getTaxDataList("");
                } else {
                    this.searchTax();
                }
            },
            (error: any) => {
                if (error.error.responseCode == 405) {
                    this.toastr.info(`${error.error.ERROR}`, 'Info!');

                } else {

                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }

    pageChangedTaxSlab(pageNumber) {
        this.currentPageTaxSlab = pageNumber;
    }

    pageChangedTaxTiered(pageNumber) {
        this.currentPageTaxTiered = pageNumber.pageIndex;
        this.TaxTiereditemsPerPage = pageNumber.pageSize;
        this.updateTaxTieredDataSource();
    }

    closeDialog() {
        this.dialog.closeAll();
    }

    pageChangedTaxList(pageNumber) {
        this.currentPageTaxListdata = pageNumber.pageIndex + 1;
        this.TaxListdataitemsPerPage = pageNumber.pageSize;
        if (!this.searchkey) {
            this.getTaxDataList("");
        } else {
            this.searchTax();
        }
    }

    taxAllDetails(data: any) {
        this.taxAllData = data;
        this.taxALLDeatilsShow = true;
        this.taxTierListCurrentPage = 0;
        this.taxTierListPageSize = 5;
        this.updateTaxTierListPagination();

        this.dialog.open(this.allDetailsShow, {
            width: "1000px"
        });
        // this.listView = false;
        this.createView = false;
    }

    pageChangedtypeTieredList(pageNumber) {
        this.taxTierListCurrentPage = pageNumber.pageIndex;
        this.taxTierListPageSize = pageNumber.pageSize;
        this.updateTaxTierListDataSource();
    }

    pageChangedtypeslabList(pageNumber) {
        this.currentPagetypeslabList = pageNumber;
    }

    updateTaxTierListPagination() {
        if (this.taxAllData && this.taxAllData.tieredList) {
            this.taxTierListTotalRecords = this.taxAllData.tieredList.length;
            this.updateTaxTierListDataSource();
        }
    }

    updateTaxTierListDataSource() {
        const startIndex = this.taxTierListCurrentPage * this.taxTierListPageSize;
        const endIndex = startIndex + this.taxTierListPageSize;

        this.taxTierListDisplayedData = this.taxAllData.tieredList.slice(startIndex, endIndex);
        this.taxTierListDataSource.data = this.taxTierListDisplayedData;
    }

    getRefresh() {
        this.getTaxDataList("");
    }
    handleTaxGroupChange(event) {
        const selectedTier = event.value;

        if (selectedTier === "TIER2" && !this.isTier1Added()) {
            error: (error) => {
                this.toastr.info(`${error.error.ERROR}`, 'Please Add Tier-1 first!');
            }

            this.infoMessageDisplayed = true;
        } else if (selectedTier === "TIER3" && (!this.isTier1Added() || !this.isTier2Added())) {
            error: (error) => {
                this.toastr.info(`${error.error.ERROR}`, 'Please Add Tier-1 first!');
            }

            this.infoMessageDisplayed = true;
        } else {
            this.infoMessage = "";
            this.infoMessageDisplayed = false;
        }
    }

    isTier1Added(): boolean {
        return this.taxTypeTiered.controls.some(control => control.get("taxGroup").value === "TIER1");
    }

    isTier2Added(): boolean {
        return this.taxTypeTiered.controls.some(control => control.get("taxGroup").value === "TIER2");
    }
}
