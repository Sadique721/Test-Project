import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { BilltemplateService } from "src/app/service/billtemplate.service";
import { Regex } from "src/app/constants/regex";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { BillTemplate } from "src/app/components/model/billTemplate";
import { Data } from "@angular/router";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { RevenueManagementService } from "src/app/service/RevenueManagement.service";
import { INVOICE_SYSTEMS } from "src/app/constants/aclConstants";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';


@Component({
    selector: "app-billtemplate",
    templateUrl: "./billtemplate.component.html",
    styleUrls: ["./billtemplate.component.css"],
    standalone: false
})
export class BilltemplateComponent implements OnInit {
    billTemplatesGroupForm: UntypedFormGroup;
    billTemplatesCategoryList: any;
    submitted: boolean = false;
    taxListData: any;
    createbillTemplatesData: BillTemplate;
    currentPagebillTemplatesListdata = 1;
    billTemplatesListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    billTemplatesListdatatotalRecords: any;
    billTemplatesListData: any = [];
    viewbillTemplatesListData: any = [];
    isbillTemplatesEdit: boolean = false;
    billTemplatestype = "";
    billTemplatescategory = "";
    searchbillTemplatesUrl: any;

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    totalAreaListLength = 0;
    objs: any = {};
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    statusOptions = RadiusConstants.status;
    billData: any = [];
    expoerteddata: any = [];
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    constructor(
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private billTemplatesService: BilltemplateService,
        loginService: LoginService, private dialog: MatDialog,
        private revenueManagementService: RevenueManagementService,
        public commondropdownService: CommondropdownService
    ) {
        this.createAccess = loginService.hasPermission(INVOICE_SYSTEMS.CREATE_BILL_TEMPLATE);
        this.deleteAccess = loginService.hasPermission(INVOICE_SYSTEMS.DELETE_BILL_TEMPLATE);
        this.editAccess = loginService.hasPermission(INVOICE_SYSTEMS.EDIT_BILL_TEMPLATE);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.isbillTemplatesEdit = !this.createAccess && this.editAccess ? true : false;
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INVOICE_SYSTEMS.EDIT_BILL_TEMPLATE) || this.loginService.hasPermission(INVOICE_SYSTEMS.DELETE_BILL_TEMPLATE)) {
            return ['templatename', 'templatetype', 'status', 'mvnoName', 'action'];
        } else {
            return ['templatename', 'templatetype', 'status', 'mvnoName'];
        }
    }
    ngOnInit(): void {
        window.scroll(0, 0);
        this.billTemplatesGroupForm = this.fb.group({
            id: [""],
            templatename: ["", Validators.required],
            templatetype: ["", Validators.required],
            jrxmlfile: ["", Validators.required],
            status: ["", Validators.required],
            isDelete: [0],
        });

        this.getbillTemplatesList("");
        this.getChargeType();
    }
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPagebillTemplatesListdata > 1) {
            this.currentPagebillTemplatesListdata = 1;
        }
        if (!this.searchkey) {
            this.getbillTemplatesList(this.showItemPerPage);
        }
    }

    getbillTemplatesList(list) {
        let size;
        this.searchkey = "";
        let page_list = this.currentPagebillTemplatesListdata;
        if (list) {
            size = list;
            this.billTemplatesListdataitemsPerPage = list;
        } else {
            // if (this.showItemPerPage == 0) {
            //   this.billTemplatesListdataitemsPerPage = this.pageITEM
            // } else {
            //   this.billTemplatesListdataitemsPerPage = this.showItemPerPage
            // }
            size = this.billTemplatesListdataitemsPerPage;
        }

        const url = "/billTemplete/list";
        let billtemplatedata = {
            page: page_list,
            pageSize: size,
        };
        this.revenueManagementService.postMethod(url, billtemplatedata).subscribe(
            (response: any) => {
                this.billTemplatesListData = response.billRunlist;
                this.billTemplatesListdatatotalRecords = response.pageDetails.totalRecords;
                // if (this.showItemPerPage > this.billTemplatesListdataitemsPerPage) {
                //   this.totalAreaListLength =
                //     this.billTemplatesListData.length % this.showItemPerPage
                // } else {
                //   this.totalAreaListLength =
                //     this.billTemplatesListData.length %
                //     this.billTemplatesListdataitemsPerPage
                // }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    // onFileChange(event) {

    //   if (event.target.files.length > 0) {
    //     const file = event.target.files[0];
    //     this.billTemplatesGroupForm.patchValue({
    //       jrxmlfile: file
    //     });
    //   }

    // }

    addEditbillTemplates(billTemplatesId, dialogRef) {
        this.submitted = true;

        if (this.billTemplatesGroupForm.valid) {
            if (billTemplatesId) {
                const url = "/billTemplete/" + billTemplatesId;
                this.createbillTemplatesData = this.billTemplatesGroupForm.value;
                this.createbillTemplatesData.isDelete = false;
                this.revenueManagementService.updateMethod(url, this.createbillTemplatesData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.billTemplatesGroupForm.reset();
                        this.isbillTemplatesEdit = false;
                        this.viewbillTemplatesListData = [];
                        dialogRef.close()
                        this.toastr.success(`Successfully`, 'Success!');
                        this.getbillTemplatesList("");
                    },
                    (error: any) => {
                        console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            } else {
                const url = "/billTemplete";

                const formData = new FormData();
                // formData.append('jrxmlfile', this.billTemplatesGroupForm.get('jrxmlfile').value);

                this.createbillTemplatesData = this.billTemplatesGroupForm.value;
                this.createbillTemplatesData.isDelete = false;
                // this.createbillTemplatesData.jrxmlfile = this.billTemplatesGroupForm.value.jrxmlfile.name;

                this.revenueManagementService.postMethod(url, this.createbillTemplatesData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        dialogRef.close()
                        this.billTemplatesGroupForm.reset();
                        this.toastr.success(`Successfully`, 'Success!');


                        this.getbillTemplatesList("");
                    },
                    (error: any) => {
                        console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            }
        }
    }

    editbillTemplates(billTemplatesId: any) {
        if (billTemplatesId) {
            this.dialog.open(this.AddEditDialog, {
                width: '50%',
                disableClose: true // same as data-backdrop="static" data-keyboard="false"
            });
            let file;
            let file1;
            const url = "/billTemplete/" + billTemplatesId;
            this.revenueManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isbillTemplatesEdit = true;
                    this.viewbillTemplatesListData = response.billRunlist;
                    this.billTemplatesGroupForm.patchValue(this.viewbillTemplatesListData);
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    deleteConfirmonbillTemplates(billTemplates: number) {
        if (billTemplates) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this billTemplates?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deletebillTemplates(billTemplates);
                } else {
                    error: (error) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                    }

                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this billTemplates?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deletebillTemplates(billTemplates);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected",
            //         });
            //     },
            // });
        }
    }

    deletebillTemplates(id) {
        const url = "/billTemplete/" + id;
        this.revenueManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPagebillTemplatesListdata != 1 && this.totalAreaListLength == 1) {
                    this.currentPagebillTemplatesListdata = this.currentPagebillTemplatesListdata - 1;
                }

                this.submitted = false;
                this.billTemplatesGroupForm.reset();
                this.isbillTemplatesEdit = false;
                this.viewbillTemplatesListData = [];
                this.toastr.success(`${response.message}`, 'Success!');


                this.getbillTemplatesList("");
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedbillTemplatesList(pageNumber) {
        this.currentPagebillTemplatesListdata = pageNumber.pageIndex + 1;
        this.billTemplatesListdataitemsPerPage = pageNumber.pageSize;
        if (!this.searchkey) {
            this.getbillTemplatesList("");
        } else {
            this.searchbillTemplates();
        }
    }

    searchbillTemplates() {
        // const url = "/billTemplates/all"
        // this.billTemplatesService.getMethod(url).subscribe((response: any) => {
        //   this.billTemplatesListData1 = response.dataList;
        // })
        // this.billTemplatesGroupForm = this.billTemplatesListData1;
        // this.temp = [... this.billTemplatesListData1];
        // let valueobj = {};
        // if (this.searchName) {
        //   valueobj["name"] = this.searchName;
        // }
        // let filterdata = _.filter(this.billTemplatesGroupForm, valueobj);
        // this.billTemplatesListData = filterdata;
        // this.temp = filterdata;
    }

    clearSearchbillTemplates() {
        this.getbillTemplatesList("");
        // this.searchName = "";
    }

    templatetypeData: any = [];
    getChargeType() {
        let url = "/commonList/generic/billingtemplatetype";
        this.commondropdownService.getMethodWithCache(url).subscribe((response: any) => {
            this.templatetypeData = response.dataList;
        });
    }

    canExit() {
        if (!this.billTemplatesGroupForm.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
                const dialogRef = this.dialog.open(this.confirmDialog, {
                    width: '400px',
                    data: {
                        title: 'Alert',
                        description: `The filled data will be lost. Do you want to continue? (Yes/No)`,
                        yesLabel: 'Yas',
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

    cancelBillTemplates() {
        this.billTemplatesGroupForm.reset();
        this.isbillTemplatesEdit = false;
        this.viewbillTemplatesListData = [];
    }
    addBillTemplates() {
        this.submitted = false;
        this.billTemplatesGroupForm.reset();
        this.isbillTemplatesEdit = false;
        this.dialog.open(this.AddEditDialog, {
            width: '50%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
    }

    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    @ViewChild('AddEditDialog') AddEditDialog!: TemplateRef<any>;
    displayedColumns: string[] = ['templatename', 'templatetype', 'status', 'mvnoName', 'action'];
}
