import { ChangeDetectorRef, Component, ElementRef, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { FormArray, FormGroup, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { TimebasepolicyService } from "src/app/service/timebasepolicy.service";
import { TimeBasePolicy } from "../model/time-base-policy";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { QosPolicyService } from "src/app/service/qos-policy.service";
import { PRODUCTS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialog } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-time-base-policy",
    templateUrl: "./time-base-policy.component.html",
    styleUrls: ["./time-base-policy.component.css"],
    standalone: false
})
export class TimeBasePolicyComponent implements OnInit {
    displayedColumns1: string[] = ['policyName', 'status', 'ispName', 'action'];
    dataSource = new MatTableDataSource<any>([]);
    tableDataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild('timePolicyDialog') timePolicyDialog!: TemplateRef<any>;
    displayedColumns: string[] = ['fromDay', 'fromTime', 'toDay', 'toTime', 'qqsid', 'access', 'isFreeQuota', 'action']
    tableDataRefresh: any;
    @ViewChild('policyDetailsDialog') policyDetailsDialog!: TemplateRef<any>;
    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }
    basic = true;
    submitted = false;
    searchSubmitted = false;
    policyForm: UntypedFormGroup;
    policyName: String;
    policyId: number;
    //Used and required for pagination
    totalRecords: number;
    currentPage: number = 1;
    itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    createPolicyData: TimeBasePolicy;
    editMode: boolean = false;
    status = [
        { label: "Active", value: "Active" },
        { label: "Inactive", value: "Inactive" },
    ];
    access = [
        { label: "Allow", value: true },
        { label: "Not Allow", value: false },
    ];
    showProfile: boolean = false;
    mvnoData: any;
    loggedInUser: any;
    mvnoId: any;
    // new changes
    allTimeBasePolicyList: any[] = [];
    filteredPolicyList: any[] = [];
    showDialogue: boolean = false;
    accessData: any = JSON.parse(localStorage.getItem("accessData"));
    weekDaysList = [
        { label: "Sunday", value: "Sunday" },
        { label: "Monday", value: "Monday" },
        { label: "Tuesday", value: "Tuesday" },
        { label: "Wednesday", value: "Wednesday" },
        { label: "Thursday", value: "Thursday" },
        { label: "Friday", value: "Friday" },
        { label: "Saturday", value: "Saturday" },
    ];
    // editPolicyData: TimeBasePolicy
    editPolicyData: any = [];
    fromTime: string;
    toTime: string;
    policyDetailsArray: UntypedFormArray;
    showSearch: any;
    showTable: boolean;
    showForm: boolean;

    createtimePolicyFlag = false;
    timePolicyGridFlag = false;
    filteredLocationList: any[];
    searchData: any = [];
    policyDetails: any = [];
    searchName: any = "";
    PolicyMappingDetails: any = [];
    qosPolicyData: any;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private timeBasePolicyService: TimebasepolicyService,
        private radiusUtility: RadiusUtility,
        loginService: LoginService,
        private commondropdownService: CommondropdownService,
        private qospolicyservice: QosPolicyService,
        private dialog: MatDialog,
        private cdRef: ChangeDetectorRef,
        private toastr: ToastrService
    ) {
        this.createAccess = loginService.hasPermission(PRODUCTS.TIME_POLICY_CREATE);
        this.deleteAccess = loginService.hasPermission(PRODUCTS.TIME_POLICY_DELETE);
        this.editAccess = loginService.hasPermission(PRODUCTS.TIME_POLICY_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.policyDetailsArray = this.fb.array([]);
        // this.editMode = !this.createAccess && this.editAccess ? true : false;
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(PRODUCTS.TIME_POLICY_DELETE) || this.loginService.hasPermission(PRODUCTS.TIME_POLICY_EDIT)) {
            return ['policyName', 'status', 'ispName', 'action'];
        } else {
            return ['policyName', 'status', 'ispName'];
        }
    }


    ngOnInit(): void {
        this.mvnoData = JSON.parse(localStorage.getItem("mvnoData"));
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.mvnoId = localStorage.getItem("mvnoId");
        this.policyForm = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            mvnoId: [this.mvnoId],
            createdByName: [""],
            lastModifiedByName: [""],
            id: [""],
        });

        this.searchData = {
            filter: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and",
                },
            ],
        };
        this.onAddAttribute();
        this.findAllTimeBasedPolicy("");
        this.getQosPolicy();
    }

    CreateUpdatetimePolicy1() {
        // this.createtimePolicyFlag = true;
        this.timePolicyGridFlag = false;
        this.editMode = false;
        this.searchName = "";
        this.clearFormData();
        // this.radiusUtility.getFocus(this.policyForm, this.el);
        // Open the Time Policy Dialog
        // this.openTimePolicyDialog('create');
        const dialogRef = this.dialog.open(this.timePolicyDialog, {
            width: '1200px',
            maxHeight: '90vh',
            disableClose: false,
        });
    }
    // private openTimePolicyDialog(mode: 'create' | 'edit' = 'create'): void {
    //     const dialogRef = this.dialog.open(this.timePolicyDialog, {
    //         width: '1000px',
    //         maxHeight: '90vh',
    //         disableClose: false,
    //     });

    //     // Handle dialog close/response
    //     dialogRef.afterClosed().subscribe((result: any) => {
    //         if (result && result.success) {
    //             console.log('Policy saved successfully:', result.data);

    //             if (mode === 'create') {
    //                 console.log('New policy created');
    //                 // Add to your grid or refresh data
    //                 // this.refreshPolicyGrid();
    //             } else {
    //                 console.log('Policy updated');
    //                 // Update your grid
    //                 // this.refreshPolicyGrid();
    //             }

    //             // Reset flags
    //             this.createtimePolicyFlag = false;
    //             this.timePolicyGridFlag = true;
    //             this.editMode = false;
    //             this.searchName = '';
    //         } else {
    //             // Dialog closed without saving
    //             this.createtimePolicyFlag = false;
    //             this.timePolicyGridFlag = true;
    //             this.editMode = false;
    //         }
    //     });
    // }
    timePolicyListData() {
        this.createtimePolicyFlag = false;
        this.timePolicyGridFlag = true;
        this.editMode = false;
        this.currentPage = 1;
        this.searchName = "";
        this.findAllTimeBasedPolicy("");
    }

    // deleteConfirmArray(index) {
    //     // console.log(product);
    //     this.policyDetailsArray.removeAt(index);
    // }

    onAddAttribute() {
        this.policyDetailsArray.push(this.createPolicyDetailsForm());
        this.refreshTableData();
    }

    refreshTableData(): void {
        this.tableDataRefresh = [...this.policyDetailsArray.controls];
        // this.tableDataSource = [...this.policyDetailsArray.controls];
    }
    addNewRow(): void {
        this.policyDetailsArray.push(this.createPolicyDetailsForm());
        this.refreshTableData();
    }
    deleteConfirmArray(index: number): void {
        // if (confirm('Are you sure you want to delete this row?')) {
        this.policyDetailsArray.removeAt(index);
        this.refreshTableData();
        // }
    }

    createPolicyDetailsForm(): UntypedFormGroup {
        return this.fb.group({
            identityKey: [""],
            detailsid: [""],
            fromDay: ["", Validators.required],
            toDay: ["", Validators.required],
            fromTime: ["", Validators.required],
            toTime: ["", Validators.required],
            // speed: ['', Validators.required],
            qqsid: ["", Validators.required],
            access: [""],
            isFreeQuota: [""],
        });
    }

    async findAllTimeBasedPolicy(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }

        let data = {
            page: page,
            pageSize: size,
        };
        this.filteredPolicyList = [];
        this.timeBasePolicyService.getAlltimebasepolicywithpagination(data).subscribe(
            (response: any) => {
                if (response.responseCode == 204) {
                    this.toastr.info(`${response.responseMessage}`);

                } else {
                    this.filteredPolicyList = response.dataList;
                    this.totalRecords = response.totalRecords;
                    this.dataSource = new MatTableDataSource(this.filteredPolicyList);
                }
            },
            (error: any) => {
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`);

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                }
                this.totalRecords = 0;
                this.filteredPolicyList = [];
            }
        );
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAllTimeBasedPolicy(this.showItemPerPage);
        } else {
            this.searchPolicyByName();
        }
    }

    searchPolicyByName() {
        if (!this.searchkey || this.searchkey !== this.searchName) {
            this.currentPage = 1;
        }

        this.searchSubmitted = true;
        this.createtimePolicyFlag = false;
        this.timePolicyGridFlag = true;
        this.filteredPolicyList = [];
        let name = this.searchName ? this.searchName.trim() : "";

        this.searchkey = name;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = name;

        this.timeBasePolicyService
            .searchbasepolicy(this.currentPage, this.itemsPerPage, this.searchData)
            .subscribe(
                (response: any) => {
                    if (response.responseCode == 404) {
                        this.toastr.info(`${response.responseMessage}`);
                        this.filteredPolicyList = [];
                    } else {
                        this.filteredPolicyList = response.dataList;
                        this.totalRecords = response.totalRecords = 0;
                        this.dataSource = new MatTableDataSource<any>(this.filteredPolicyList);

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.response.error}`, 'Failed!');
                }
            );
    }
    clearSearchForm() {
        this.clearFormData();
        this.searchSubmitted = false;
        this.searchName = "";
        this.currentPage = 1;
        this.findAllTimeBasedPolicy("");
        this.createtimePolicyFlag = false;
        this.timePolicyGridFlag = false;
    }

    clearFormData() {
        this.editMode = false;
        this.submitted = false;
        this.policyForm.reset();
        this.policyDetailsArray.reset();
        this.policyDetailsArray = this.fb.array([]);
        this.onAddAttribute();
        this.searchName = "";
    }

    canExit() {
        if (!this.policyForm.dirty) return true;
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
                    },
                });
                return false;
            });
        }
    }
    // deleteConfirm(data, selectedMvnoId, index) {
    //     this.confirmationService.confirm({
    //         message: "Do you want to delete this Time Base Policy ?",
    //         header: "Delete Confirmation",
    //         icon: "pi pi-info-circle",
    //         accept: () => {
    //             this.deletePolicy(data, index);
    //         },
    //         reject: () => {
    //             this.messageService.add({
    //                 severity: "info",
    //                 summary: "Rejected",
    //                 detail: "You have rejected",
    //             });
    //         },
    //     });
    // }
    deleteConfirm(data, selectedMvnoId, index) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Confirmation ',
                description: `Are you sure you want to delete "${data.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deletePolicy(data, index);
            } else {
            }
        });
    }
    async deletePolicy(data, index) {
        this.timeBasePolicyService.deletePolicy(data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (this.currentPage != 1 && index == 0 && this.filteredPolicyList.length == 1) {
                        this.currentPage = this.currentPage - 1;
                    }
                    if (!this.searchkey) {
                        this.findAllTimeBasedPolicy("");
                        this.toastr.success(`Successfully Deleted`, 'Success!');

                    } else {
                        this.searchPolicyByName();
                    }

                } else if (response.responseCode == 405 || response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`);
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }
    onCancel() {
        this.dialog.closeAll();
    }
    async editPolicyDetailById(id) {
        this.editMode = true;
        this.policyDetailsArray = this.fb.array([]);
        this.policyForm.setControl('policyDetailsArray', this.policyDetailsArray);
        // this.policyForm.setControl('policyDetailsArray', this.fb.array([]));
        // this.createtimePolicyFlag = true;
        this.timePolicyGridFlag = false;
        const dialogRef = this.dialog.open(this.timePolicyDialog, {
            width: '1200px',
            maxHeight: '90vh',
            disableClose: false,
        });

        this.timeBasePolicyService.getPolicyById(id).subscribe(
            (response: any) => {
                let policyData = response.data;
                let policyMappingData = response.data.timeBasePolicyDetailsList;

                this.policyForm.patchValue({
                    id: policyData.id,
                    name: policyData.name,
                    mvnoId: policyData.mvnoId,
                    status: policyData.status,
                    createdByName: policyData.createdByName,
                    lastModifiedByName: policyData.lastModifiedByName,
                });

                // policyMappingData.forEach(details => {
                //     this.policyDetailsArray.push(
                //         this.fb.group({
                //             fromDay: details.fromDay,
                //             toDay: details.toDay,
                //             fromTime: this.formatTime(details.fromTime),
                //             toTime: this.formatTime(details.toTime),
                //             // speed: details.speed,
                //             qqsid: details.qqsid,
                //             access: details.access,
                //             detailsid: details.detailsid,
                //             isFreeQuota: details.isFreeQuota,
                //         })
                //     );
                // });
                // this.policyDetailsArray.clear();
                if (policyMappingData && policyMappingData.length > 0) {
                    policyMappingData.forEach((detail: any) => {
                        this.policyDetailsArray.push(
                            this.fb.group({
                                // identityKey: [detail.identityKey],
                                detailsid: [detail.detailsid],
                                fromDay: [detail.fromDay, Validators.required],
                                toDay: [detail.toDay, Validators.required],
                                fromTime: [detail.fromTime, Validators.required],
                                toTime: [detail.toTime, Validators.required],
                                qqsid: [detail.qqsid, Validators.required],
                                access: [detail.access],
                                isFreeQuota: [detail.isFreeQuota],
                            })
                        );
                    });
                }
                // Refresh table
                // this.tableDataSource = [...this.policyDetailsArray.controls];
                this.refreshTableData();
                this.cdRef.detectChanges();

            },
            error => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }

    pageChanged(event) {
        this.currentPage = event.pageIndex + 1;
        // this.currentPageProductListdata = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;
        if (!this.searchkey) {
            this.findAllTimeBasedPolicy("");
        } else {
            this.searchPolicyByName();
        }
    }
    closeModal() {
        this.showDialogue = false;
    }
    async getPolicyDetails(policyId) {
        this.timeBasePolicyService.getPolicyById(policyId).subscribe(
            (response: any) => {
                this.policyDetails = response.data;
                this.showDialogue = true;
                this.dialog.open(this.policyDetailsDialog, {
                    width: '60%',
                    disableClose: true,
                    autoFocus: false
                });
                this.PolicyMappingDetails = response.data.timeBasePolicyDetailsList;
            },
            error => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }
    markFormGroupTouched(formGroup: FormGroup): void {
        Object.keys(formGroup.controls).forEach(key => {
            const control = formGroup.get(key);
            control?.markAsTouched();

            if (control instanceof FormArray) {
                control.controls.forEach(group => {
                    if (group instanceof FormGroup) {
                        this.markFormGroupTouched(group);
                    }
                });
            }
        });
    }
    cannotContainSpace(control: any): { [key: string]: boolean } | null {
        if (control.value && control.value.includes(' ')) {
            return { 'cannotContainSpace': true };
        }
        return null;
    }

    savePolicy() {
        this.submitted = true;
        if (this.policyForm.invalid && this.policyDetailsArray.invalid) {
            this.markFormGroupTouched(this.policyForm);
            // alert('Please fill all required fields correctly');
            return;
        }

        if (this.policyForm.valid && this.policyDetailsArray.valid) {
            this.policyDetailsArray.value.forEach(details => {
                details.fromTime = this.formatTime(details.fromTime);
                details.toTime = this.formatTime(details.toTime);
            });

            if (this.editMode) {
                this.editPolicyData.lastModifiedBy = this.loggedInUser;
                this.updatePolicy();
            } else {
                this.addNewPolicy();
            }
        }
    }

    updatePolicy() {
        // this.createPolicyData = this.policyForm.value;
        // this.createPolicyData.mvnoId = this.mvnoId;
        // this.createPolicyData.lastModifiedByName = this.loggedInUser;
        // this.createPolicyData.timeBasePolicyDetailsList = this.policyDetailsArray.value;
        const createPolicyData = { ...this.policyForm.value };
        createPolicyData.mvnoId = this.mvnoId;
        createPolicyData.lastModifiedByName = this.loggedInUser;
        createPolicyData.timeBasePolicyDetailsList = this.policyDetailsArray.value;
        delete createPolicyData.policyDetailsArray;
        this.timeBasePolicyService.updatePolicyDetails(createPolicyData).subscribe(
            (res: any) => {
                if (!this.searchkey) {
                    this.findAllTimeBasedPolicy("");
                } else {
                    this.searchPolicyByName();
                }
                this.clearFormData();
                this.createtimePolicyFlag = false;
                this.timePolicyGridFlag = true;
                if (res.responseCode == 406 || res.responseCode == 417) {
                    this.toastr.info(`${res.responseMessage}`);
                } else {
                    this.toastr.success(`Successfully Updated`, 'Success!');
                    this.dialog.closeAll();
                }
            },
            error => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }

    addNewPolicy() {
        this.createPolicyData = this.policyForm.value;
        this.createPolicyData.createdByName = this.loggedInUser;

        this.createPolicyData.timeBasePolicyDetailsList = this.policyDetailsArray.value;

        this.timeBasePolicyService.addNewPolicyDetails(this.createPolicyData).subscribe(
            (res: any) => {
                if (res.responseCode == 200) {
                    this.clearFormData();
                    this.toastr.success(`Successfully Created`, 'Success!');
                    this.createtimePolicyFlag = false;
                    this.timePolicyGridFlag = true;
                    this.findAllTimeBasedPolicy("");
                    this.dialog.closeAll();
                } else {
                    this.toastr.error(`${res.responseMessage}`, 'Failed!');
                }
            },
            error => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }
    formatTime(fromTime) {
        if (typeof fromTime != "string") {
            let hour = new Date(fromTime).getHours();
            let min = new Date(fromTime).getMinutes();
            if (hour < 10) {
                if (min < 10) {
                    fromTime = `0${hour}:0${min}`;
                } else {
                    fromTime = `0${hour}:${min}`;
                }
            } else {
                if (min < 10) {
                    fromTime = `${hour}:0${min}`;
                } else {
                    fromTime = `${hour}:${min}`;
                }
            }
            return fromTime;
        } else {
            return fromTime;
        }
    }

    // updateValue() {
    //   let formValues = this.policyForm.getRawValue()

    //   let detailsFormValue: TimeBasePolicyDetails[] = this.policyDetailsArray.getRawValue()
    //   let finalDetailsMapping: TimeBasePolicyDetails[] = []
    //   let newtimebaseLocation = []
    //   if (!this.editMode) {
    //     detailsFormValue.forEach((details) => {
    //       details.fromTime = this.formatTime(details.fromTime)
    //       details.toTime = this.formatTime(details.toTime)
    //       finalDetailsMapping.push(details)
    //     })
    //   } else {
    //     detailsFormValue.forEach((details) => {
    //       details.policyId = this.editPolicyData.policyId
    //       details.fromTime = this.formatTime(details.fromTime)
    //       details.toTime = this.formatTime(details.toTime)
    //       finalDetailsMapping.push(details)
    //     })
    //   }

    //   if (!this.editMode) {
    //     this.editPolicyData = {
    //       policyId: null,
    //       policyName: formValues.policyName,
    //       policyDetailsMapping: finalDetailsMapping,
    //       status: formValues.status,
    //       mvnoId: formValues.mvnoName ? formValues.mvnoName : this.mvnoId,
    //       lastModifiedBy: '',
    //       createdBy: this.loggedInUser,
    //     }
    //   } else {
    //     this.editPolicyData.policyName = formValues.policyName
    //     this.editPolicyData.status = formValues.status
    //     this.editPolicyData.mvnoId = formValues.mvnoName
    //       ? formValues.mvnoName
    //       : this.mvnoId

    //     this.editPolicyData.policyDetailsMapping = finalDetailsMapping
    //     this.editPolicyData.lastModifiedBy = ''
    //     this.editPolicyData.createdBy = this.loggedInUser
    //   }
    // }

    getQosPolicy() {
        const url = "/qosPolicy/all";
        this.qospolicyservice.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.qosPolicyData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }
}
