import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray, FormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { ChargeManagementService } from "src/app/service/charge-management.service";
import { Regex } from "src/app/constants/regex";
import { ChargeManagement } from "src/app/components/model/charge-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { Observable, Observer } from "rxjs";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { PRODUCTS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
import { error } from "console";


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
    selector: "app-charge-management",
    templateUrl: "./charge-management.component.html",
    styleUrls: ["./charge-management.component.css"],
    standalone: false
})
export class ChargeManagementComponent implements OnInit {
    displayedColumns: string[] = [
        'id',
        'name',
        'type',
        'chargeAmount',
        'tax',
        'totalChargeAmount',
        'status',
        'ispName', 'action'
    ];
    chargeGroupForm: UntypedFormGroup;
    chargeCategoryList: any;
    submitted: boolean = false;
    taxListData: any = [];
    createChargeData: ChargeManagement;
    currentPageChargeListdata = 1;
    ChargeListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    ChargeListdatatotalRecords: any;
    chargeListData: any = [];
    viewChargeListData: any = [];
    isChargeEdit: boolean = false;
    searchChargeUrl: any;
    detailView: boolean = false;
    listView: boolean = true;
    createView: boolean = false;
    searchdataview: boolean = false;
    chargeDetailData: any = {
        name: "",
        chargetype: "",
        chargecategory: "",
        price: "",
        taxName: "",
        taxamount: "",
        saccode: "",
        desc: "",
        ledgerId: "",
        currency: ""
    };
    chargeType = [];
    chargeTaxDetails: boolean = false;
    searchOptionSelect = [
        { label: "Any", value: "any" },
        { label: "Name", value: "name" },
        { label: "Category", value: "chargecategory" },
        { label: "Type", value: "chargetype" }
    ];

    royaltyPayableData = [
        { label: "Yes", value: true },
        { label: "No", value: false }
    ];
    isChargeName = false;
    isChargeCatogorey = false;
    isChargeType = false;

    searchData: any = [];
    chargetype = "";
    chargecategory = "";
    chargename = "";
    searchOption: string = "";
    chargeOptionname: string = "";

    statusOptions = RadiusConstants.status;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    searchkey: any = [];
    totalDataListLength = 0;
    serviceListFlag: boolean = false;
    chargeTaxData: any = [];
    chargeTypeGetDataData: any = [];

    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    chargeTypeText: any;
    currency: string;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    constructor(
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private chargeManagementService: ChargeManagementService,
        public commondropdownService: CommondropdownService,
        loginService: LoginService, private dialog: MatDialog,
        private systemService: SystemconfigService
    ) {
        this.createAccess = loginService.hasPermission(PRODUCTS.CHARGE_CREATE);
        this.deleteAccess = loginService.hasPermission(PRODUCTS.CHARGE_DELETE);
        this.editAccess = loginService.hasPermission(PRODUCTS.CHARGE_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        // this.isChargeEdit = !this.createAccess && this.editAccess ? true : false;
        this.getChargeType();
        this.getChargeCategory();
        this.getTaxDataList();
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(PRODUCTS.CHARGE_DELETE) || this.loginService.hasPermission(PRODUCTS.CHARGE_EDIT)) {
            return [
                'id',
                'name',
                'type',
                'chargeAmount',
                'tax',
                'totalChargeAmount',
                'status',
                'ispName', 'action'
            ];
        } else {
            return [
                'id',
                'name',
                'type',
                'chargeAmount',
                'tax',
                'totalChargeAmount',
                'status',
                'ispName'
            ];
        }
    }

    ngOnInit(): void {
        this.chargeGroupForm = this.fb.group({
            actualprice: ["", Validators.required],
            chargecategory: ["", Validators.required],
            chargetype: ["", Validators.required],
            desc: ["", [Validators.required, Validators.pattern(Regex.characterlength225)]],
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            saccode: [""],
            taxid: ["", Validators.required],
            serviceid: ["", Validators.required],
            status: ["", Validators.required],
            currency: ["", Validators.required],
            ledgerId: [""],
            serviceNameList: [""],
            royalty_payable: [""],
            pushableLedgerId: [""]
        });
        this.commondropdownService.getTaxAllListAll();
        this.commondropdownService.getAllCurrencyData();

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

        this.getChargeList("");
        this.searchOption = "";
        this.selchargeOption({ value: "any" });

        const serviceArea = localStorage.getItem("serviceArea");
        let serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.filterserviceAreaList();
        } else {
            this.commondropdownService.getserviceAreaList();
        }

        this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
            this.currency = res.data.value;
        });
    }
    get step1Group(): FormGroup {
        return this.chargeGroupForm.pick(['name', 'chargecategory', 'chargetype', 'serviceid', 'status', 'ledgerId', 'royalty_payable', 'pushableLedgerId']);
    }
    openServiceModal() {
        this.serviceListFlag = true;
        this.dialog.open(this.serviceListFlagDailog, {
            width: '450px',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
    }

    closeModalOfService() {
        this.serviceListFlag = false;
    }

    createCharge() {
        this.listView = false;
        this.createView = true;
        this.detailView = false;
        this.searchdataview = false;

        this.submitted = false;
        this.isChargeEdit = false;
        this.viewChargeListData = [];
        this.chargeGroupForm.reset();

        this.chargeGroupForm.controls.chargecategory.setValue("");
        this.chargeGroupForm.controls.chargetype.setValue("");
        this.chargeGroupForm.controls.taxid.setValue("");
    }

    searchChargedata() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.searchdataview = true;
    }

    listCharge() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.searchdataview = false;
    }
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageChargeListdata > 1) {
            this.currentPageChargeListdata = 1;
        }
        if (!this.searchkey) {
            this.getChargeList(this.showItemPerPage);
        } else {
            this.searchCharge();
        }
    }
    closeModal() {
        this.chargeTaxDetails = false;
    }
    getChargeList(list) {
        this.chargeTypeGetDataData = [];
        let size;
        let page_list = this.currentPageChargeListdata;
        this.searchkey = "";
        if (list) {
            size = list;
            this.ChargeListdataitemsPerPage = list;
        } else {
            // if (this.showItemPerPage == 1) {
            //   this.ChargeListdataitemsPerPage = this.pageITEM
            // } else {
            //   this.ChargeListdataitemsPerPage = this.showItemPerPage
            // }
            size = this.ChargeListdataitemsPerPage;
        }

        const url = "/charge/list";
        let chargedata = {
            page: page_list,
            pageSize: size
        };
        this.chargeManagementService.postMethod(url, chargedata).subscribe(
            (response: any) => {
                this.chargeListData = response.chargelist;
                let chargeData = response.chargelist;
                // if (this.showItemPerPage > this.ChargeListdataitemsPerPage) {
                //   this.totalDataListLength = chargeData.length % this.showItemPerPage
                // } else {
                //   this.totalDataListLength =
                //     chargeData.length % this.ChargeListdataitemsPerPage
                // }
                this.ChargeListdatatotalRecords = response.pageDetails.totalRecords;

                this.chargeListData.forEach((element, index) => {
                    this.chargeType.forEach((data, j) => {
                        if (element.chargetype == data.value) {
                            this.chargeTypeGetDataData.push(data.text);
                        } else if (element.chargetype == "ADVANCE_RECURRING") {
                            this.chargeTypeGetDataData.push("Advance Recurring");
                        }
                    });
                });
            },
            (error: any) => {
                console.log(error, 'error')
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getChargeCategory() {
        const url = "/commonList/chargeCategory";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.chargeCategoryList = response.dataList;
                // console.log('this.chargeCategoryList', this.chargeCategoryList)
            },
            (error: any) => {
                console.log(error, 'error')
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getTaxDataList() {
        const url = "/taxes/all";
        this.chargeManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.taxListData = response.taxlist;
                // console.log(' this.taxListData', this.taxListData)
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                console.log(error, 'error')

            }
        );
    }

    onKey(event: any) {
        let enteredValue = event.target.value;
        if (isNaN(enteredValue) || parseFloat(enteredValue) < 0) {
            event.target.value = "";
        } else {
            if (this.taxUpRange && parseFloat(enteredValue) > parseFloat(this.taxUpRange)) {
                this.chargeValueSentence = "The charge value is not in range with added tax.";
            } else {
                this.chargeValueSentence = "";
            }
        }
    }
    addEditCharge(chargeId) {
        this.submitted = true;
        Object.keys(this.chargeGroupForm.controls).forEach(field => {
            const control = this.chargeGroupForm.get(field);
            control?.markAsTouched({ onlySelf: true });

            if (control?.invalid) {
                console.error(` Invalid field: ${field}`, control.errors);
            } else {
            }
        });
        if (this.chargeGroupForm.valid) {
            if (this.chargeValueSentence == "") {
                if (chargeId) {
                    const url = "/charge/" + chargeId;

                    if (
                        this.chargeGroupForm.value.royalty_payable == null ||
                        this.chargeGroupForm.value.royalty_payable == ""
                    ) {
                        this.chargeGroupForm.value.royalty_payable = false;
                    }
                    this.createChargeData = this.chargeGroupForm.value;
                    this.createChargeData.price = this.chargeGroupForm.controls.actualprice.value;
                    // console.log('this.createChargeData', this.createChargeData)
                    this.chargeManagementService.updateMethod(url, this.createChargeData).subscribe(
                        (response: any) => {
                            this.submitted = false;
                            this.chargeGroupForm.reset();
                            this.isChargeEdit = false;
                            this.viewChargeListData = [];
                            this.listView = true;
                            this.createView = false;
                            this.commondropdownService.clearCacheCMS("/charge/all");
                            if (!this.searchkey) {
                                this.getChargeList("");
                            } else {
                                this.searchCharge();
                            }

                            this.toastr.success(`${response.msg}`, 'Success!');
                            ;
                        },
                        (error: any) => {
                            // console.log(error, 'error')
                            if (error.error.status == 417 || error.error.status == 406) {
                                this.toastr.info(`${error.error.ERROR}`, 'Info!');

                            } else {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                            }
                        }
                    );
                } else {
                    const url = "/charge";
                    if (
                        this.chargeGroupForm.value.royalty_payable == null ||
                        this.chargeGroupForm.value.royalty_payable == ""
                    ) {
                        this.chargeGroupForm.value.royalty_payable = false;
                    }
                    this.createChargeData = this.chargeGroupForm.value;
                    this.createChargeData.price = this.chargeGroupForm.controls.actualprice.value;
                    // console.log('this.createChargeData', this.createChargeData)
                    this.chargeManagementService.postMethod(url, this.createChargeData).subscribe(
                        (response: any) => {
                            this.submitted = false;
                            this.chargeGroupForm.reset();
                            this.listView = true;
                            this.createView = false;
                            this.toastr.success(`${response.msg}`, 'Success!');


                            if (!this.searchkey) {
                                this.getChargeList("");
                            } else {
                                this.searchCharge();
                            }
                        },
                        (error: any) => {
                            // console.log(error, 'error')
                            if (error.error.status == 406) {

                                this.toastr.info(`${error.error.ERROR}`, 'Info!');

                            } else {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                            }
                        }
                    );
                }
            }
        }
    }

    editCharge(chargeId) {
        this.listView = false;
        this.createView = true;
        this.searchdataview = false;
        this.chargeGroupForm.reset();
        let taxData: any = [];
        let slabList: any = [];
        this.viewChargeListData = [];
        this.chargeValueSentence = "";
        this.taxUpRange = "";
        if (chargeId) {
            const url = "/charge/" + chargeId;
            this.chargeManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isChargeEdit = true;
                    this.viewChargeListData = response.chargebyid;

                    this.commondropdownService.getplanservice();
                    this.chargeGroupForm.patchValue(this.viewChargeListData);
                    this.chargeGroupForm.patchValue({
                        serviceid: this.viewChargeListData.servicesid
                    });
                    var event = {
                        value: this.chargeGroupForm.value.chargecategory
                    };
                    this.eventChargeCategory(event);
                    let url = "/taxes/" + this.viewChargeListData.taxid;
                    this.chargeManagementService.getMethod(url).subscribe((response: any) => {
                        taxData = response.taxData;
                        if (taxData.taxtype == "SLAB") {
                            slabList = taxData.slabList;
                            let index = slabList.length - 1;
                            this.taxUpRange = slabList[index].rangeUpTo;
                            if (this.viewChargeListData.price > this.taxUpRange) {
                                this.chargeValueSentence = "The charge value is not in range with added tax.";
                            } else {
                                this.chargeValueSentence = "";
                            }
                        }
                    });
                },
                (error: any) => {
                    console.log(error, 'error')
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    canExit() {
        if (!this.chargeGroupForm.dirty) return true;
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

    deleteConfirmonCharge(chargeId: number) {
        if (chargeId) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this Charge?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteCharge(chargeId);
                } else {
                    this.toastr.info(`You have rejected`, 'Rejected!');

                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this Charge?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deleteCharge(chargeId);
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

    deleteCharge(chargeId) {
        const url = "/charge/" + chargeId;
        this.chargeManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPageChargeListdata != 1 && this.totalDataListLength == 1) {
                    this.currentPageChargeListdata = this.currentPageChargeListdata - 1;
                }
                if (response.responseCode == 405) {

                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else if (response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.toastr.success(`${response.msg}`, 'Success!');

                }
                this.commondropdownService.clearCacheCMS("/charge/all");
                if (!this.searchkey) {
                    this.getChargeList("");
                } else {
                    this.searchCharge();
                }
            },
            (error: any) => {
                if (error.error.responseCode == 405 || error.error.responseCode == 417) {


                    this.toastr.info(`${error.error.ERROR}`, 'Info!');

                } else {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            }
        );
    }

    selchargeOption(event) {
        let selOption = event.value;
        if (selOption == "name") {
            this.isChargeName = true;
            this.isChargeCatogorey = false;
            this.isChargeType = false;
            this.chargeOptionname = "";
        } else if (selOption == "chargecategory") {
            this.isChargeName = false;
            this.isChargeCatogorey = true;
            this.isChargeType = false;
            this.chargeOptionname = "";
        } else if (selOption == "chargetype") {
            this.isChargeName = false;
            this.isChargeCatogorey = false;
            this.isChargeType = true;
            this.chargeOptionname = "";
        } else if (selOption == "any") {
            this.isChargeName = true;
            this.isChargeCatogorey = false;
            this.isChargeType = false;
            this.chargeOptionname = "";
        } else {
            this.isChargeName = false;
            this.isChargeCatogorey = false;
            this.isChargeType = false;
            this.chargeOptionname = "";
        }
    }

    searchCharge() {
        this.chargeTypeGetDataData = [];
        const trimmedInput = (this.chargeOptionname || "").trim();


        if (!trimmedInput) {

            return;
        }
        if (!this.searchkey || this.searchkey !== this.chargeOptionname.trim()) {
            this.currentPageChargeListdata = 1;
        }
        this.searchkey = this.chargeOptionname;
        if (this.showItemPerPage == 1) {
            this.ChargeListdataitemsPerPage = this.pageITEM;
        } else {
            this.ChargeListdataitemsPerPage = this.showItemPerPage;
        }

        this.searchData.filters[0].filterValue = this.chargeOptionname.trim();
        this.searchData.filters[0].filterColumn = this.searchOption.trim();
        this.searchData.filters[0].filterDataType = "";
        this.searchData.page = this.currentPageChargeListdata;
        this.searchData.pageSize = this.ChargeListdataitemsPerPage;

        const url = "/charge/search";
        this.chargeManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.chargeListData = response.chargelist;
                let chargeData = response.chargelist;
                this.ChargeListdatatotalRecords = response.pageDetails.totalRecords;

                if (this.showItemPerPage > this.ChargeListdataitemsPerPage) {
                    this.totalDataListLength = chargeData.length % this.showItemPerPage;
                } else {
                    this.totalDataListLength = chargeData.length % this.ChargeListdataitemsPerPage;
                }

                this.chargeListData.forEach((element, index) => {
                    this.chargeType.forEach((data, j) => {
                        if (element.chargetype == data.value) {
                            this.chargeTypeGetDataData.push(data.text);
                        }
                    });
                });
            },
            (error: any) => {
                if (error.error.status == 404) {
                    this.chargeListData = [];
                    this.ChargeListdatatotalRecords = 0;

                    this.toastr.info(`${error.error.msg}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');

                }
            }
        );
        // } else {
        //   this.getChargeList('')
        // }
    }

    clearSearchCharge() {
        this.getChargeList("");
        this.chargecategory = "";
        this.chargetype = "";
        this.chargeOptionname = "";
        this.searchOption = "";
        this.isChargeName = false;
        this.isChargeCatogorey = false;
        this.isChargeType = false;
        this.selchargeOption({ value: "any" });
    }

    pageChangedChargeList(pageNumber) {
        this.currentPageChargeListdata = pageNumber.pageIndex + 1;
        this.ChargeListdataitemsPerPage = pageNumber.pageSize;
        if (!this.searchkey) {
            this.getChargeList("");
        } else {
            this.searchCharge();
        }
    }

    openChargeDetail(chargeId) {
        this.dialog.open(this.chargeDetailsDialog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.detailView = false;
        this.createView = false;
        this.listView = true;
        this.searchdataview = false;
        this.getChargeDetailById(chargeId);
    }
    viewServiceName = "";
    getChargeDetailById(chargeId) {
        const url = "/charge/" + chargeId;
        this.chargeManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.chargeDetailData = response.chargebyid;

                this.chargeType.forEach((data, j) => {
                    if (this.chargeDetailData.chargetype == data.value) {
                        this.chargeTypeText = data.text;
                    }
                });

                const url = "/planservice/" + this.chargeDetailData.servicesid[0];
                this.chargeManagementService.getMethod(url).subscribe((response: any) => {
                    this.viewServiceName = response.servicebyId.name;
                });
            },
            (error: any) => {
                console.log(error, 'error')
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    chargeValueSentence = "";
    taxUpRange = "";

    taxRang(event) {
        let taxData: any = [];
        let slabList: any = [];
        this.taxUpRange = "";
        this.chargeValueSentence = "";
        let id = event.value;

        let url = "/taxes/" + id;
        this.chargeManagementService.getMethod(url).subscribe((response: any) => {
            taxData = response.taxData;
            if (taxData.taxtype == "SLAB") {
                slabList = taxData.slabList;
                let index = slabList.length - 1;
                this.taxUpRange = slabList[index].rangeUpTo;
                if (this.viewChargeListData.price > this.taxUpRange) {
                    this.chargeValueSentence = "The charge value is not in range with added tax.";
                } else {
                    this.chargeValueSentence = "";
                }
            }
        });
    }

    getChargeType() {
        let url = "/commonList/generic/chargetype";
        this.commondropdownService.getMethodWithCache(url).subscribe((response: any) => {
            this.chargeType = response.dataList;
        });
    }

    eventChargeType(e) {
        if (e.value == "ADVANCE" || e.value == "RECURRING") {
            this.chargeGroupForm.get("royalty_payable").setValidators([Validators.required]);
            this.chargeGroupForm.get("royalty_payable").updateValueAndValidity();
        } else {
            this.chargeGroupForm.get("royalty_payable").clearValidators();
            this.chargeGroupForm.get("royalty_payable").updateValueAndValidity();
        }
    }

    eventChargeCategory(e) {
        if (e.value == "IP") {
            const url = "/getAllServicesforIPCharge";
            this.commondropdownService.getMethod(url).subscribe(
                (response: any) => {
                    this.commondropdownService.planserviceData = response.dataList;
                },
                (error: any) => { }
            );
        } else {
            this.commondropdownService.getplanservice();
        }
    }

    openChargeTAxDetail(taxId) {
        this.listView = true;
        let url = "/taxes/" + taxId;
        this.chargeManagementService.getMethod(url).subscribe((response: any) => {
            this.chargeTaxData = response.taxData;
            this.chargeTaxDetails = true;
            this.dialog.open(this.chargeTaxDetailsDailog, {
                width: '550px',
                disableClose: true // same as data-backdrop="static" data-keyboard="false"
            });
        });
    }

    @ViewChild('chargeDetailsDialog') chargeDetailsDialog!: TemplateRef<any>;
    @ViewChild('serviceListFlagDailog') serviceListFlagDailog!: TemplateRef<any>;
    @ViewChild('chargeTaxDetailsDailog') chargeTaxDetailsDailog!: TemplateRef<any>;
}
