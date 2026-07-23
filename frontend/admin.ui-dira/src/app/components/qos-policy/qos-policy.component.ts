import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { FormGroup, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { QosPolicyManagement } from "src/app/components/model/qos-policy";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Regex } from "src/app/constants/regex";
import { LoginService } from "src/app/service/login.service";
import { QosPolicyService } from "src/app/service/qos-policy.service";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { PRODUCTS } from "src/app/constants/aclConstants";
import { NMSService } from "src/app/service/nms.service";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { AddEditQosPolicyManagmentComponent } from "./add-edit-qos-policy-managment/add-edit-qos-policy-managment.component";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";
declare var $: any;
@Component({
    selector: "app-qos-policy",
    templateUrl: "./qos-policy.component.html",
    styleUrls: ["./qos-policy.component.css"],
    standalone: false
})
export class QosPolicyComponent implements OnInit {
    title = "Qos Policy";
    qosPolicyGroupForm: UntypedFormGroup;
    submitted: boolean = false;
    searchSubmitted = false;
    createQosPolicyData: QosPolicyManagement;
    qosPolicyListData: any;
    currentPageQosPolicyListdata = 1;
    qosPolicyListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    qosPolicyListdatatotalRecords: any;
    isQosPolicyEdit: boolean = false;
    viewQosPolicyListData: any;
    filteredUpstreamProfile: any[] = [];
    filteredDownstreamProfile: any[] = [];
    qosPolicyData: any = {
        name: "",
        thpolicyname: "",
        basepolicyname: "",
        description: "",
        gatewayName: "",
        downloadSpeed: "",
        uploadSpeed: "",
        baseDownloadSpeed: "",
        baseUploadSpeed: "",
        throttleDownloadSpeed: "",
        throttleUploadSpeed: "",
        thparam1: "",
        thparam2: "",
        thparam3: "",
        baseparam1: "",
        baseparam2: "",
        baseparam3: "",
        type: "",
        qosspeed: "",
    };

    listView: boolean = true;
    createView: boolean = false;
    detailView: boolean = false;
    isUpStreamDetailView: boolean = false;

    deletedata: any = {
        id: "",
        name: "",
        thpolicyname: "",
        thparam1: "",
        thparam2: "",
        thparam3: "",
        description: "",
        gatewayName: "",
        downloadSpeed: "",
        uploadSpeed: "",
        baseDownloadSpeed: "",
        baseUploadSpeed: "",
        throttleDownloadSpeed: "",
        throttleUploadSpeed: "",
        basepolicyname: "",
        baseparam1: "",
        baseparam2: "",
        baseparam3: "",
    };
    AclClassConstants;
    AclConstants;
    searchName: any = "";
    searchData: any;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    inputMobile: string;
    totalAreaListLength = 0;
    gatewayAtrribute: UntypedFormArray;
    allowedGateway: number;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    upStreamType: any;
    upStreamprofilename: any;
    downStreamProfilename: any;
    assuredbandwidth: any;
    bandwidthUnit: any;
    bandwidthValue: any;
    commistedBustsize: any;
    peakBustsize: any;
    upStreamProfileData: any[] = [];
    downStreamProfileData: any[] = [];
    upStreamProfileDataList: any[] = [];

    //****************** */
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    displayedColumns = ['id', 'Qos Policy Name', "Policy Name", 'Qos Speed', 'ISP Name', 'Action'];
    @ViewChild('addEditQosPolicyDialog') addEditQosPolicyDialog!: TemplateRef<any>;
    @ViewChild('viewTatForTaskDialog') viewTatForTaskDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    dialogRef2!: MatDialogRef<any>;
    dataSourcess = new MatTableDataSource<any>();



    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private qosPolicyService: QosPolicyService,
        public loginService: LoginService,
        private configService: SystemconfigService,
        public commondropdownService: CommondropdownService,
        private systemService: SystemconfigService,
        private nmsService: NMSService,
        // **************
        private dialog: MatDialog,
        private toastr: ToastrService

    ) {
        this.createAccess = loginService.hasPermission(PRODUCTS.QOS_POLICY_CREATE);
        this.deleteAccess = loginService.hasPermission(PRODUCTS.QOS_POLICY_DELETE);
        this.editAccess = loginService.hasPermission(PRODUCTS.QOS_POLICY_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        // this.isQosPolicyEdit = !this.createAccess && this.editAccess ? true : false;
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.gatewayAtrribute = this.fb.array([]);
        this.createGatewayMappingGroup();
        this.configService.getConfigurationByName("GATEWAY_SUPPORT_COUNT").subscribe((res: any) => {
            if (res.data) {
                this.allowedGateway = Number(res.data.value);
            }
        });
    }

    ngOnInit(): void {
        this.gatewayAtrribute = this.fb.array([]);
        this.qosPolicyGroupForm = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            thpolicyname: ["", Validators.required],
            thparam1: ["", Validators.required],
            thparam2: ["", Validators.required],
            thparam3: ["", Validators.required],
            description: ["", [Validators.required, Validators.pattern(Regex.characterlength100)]],
            basepolicyname: ["", Validators.required],
            baseparam1: ["", Validators.required],
            baseparam2: ["", Validators.required],
            baseparam3: ["", Validators.required],
            type: [""],
            qosspeed: ["", Validators.required],
            upstreamprofileuid: [""],
            downstreamprofileuid: [""],
            upstreamprofileName: [""],
            downstreamprofileName: [""],
            gatewayDetails: this.fb.array([]),
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
        this.onAddOfGatwayMapping();
        this.getQosPolicyList("");
        this.systemService.getConfigurationByName("NMS_ENABLE").subscribe((res: any) => {
            if (res.data) {
                this.isUpStreamDetailView = res.data.value === "true" ? true : false;
                if (this.isUpStreamDetailView) {
                    this.removeValidators();
                    this.qosPolicyGroupForm.controls.upstreamprofileuid.setValidators(Validators.required);
                    this.qosPolicyGroupForm.controls.downstreamprofileuid.setValidators(Validators.required);
                }
            }
        });
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }


    removeValidators() {
        this.gatewayAtrribute.controls.forEach(control => {
            const formGroup = control as UntypedFormGroup;

            Object.keys(formGroup.controls).forEach(key => {
                formGroup.get(key)?.setValidators(null);
                formGroup.get(key)?.updateValueAndValidity();
            });
        });
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(PRODUCTS.QOS_POLICY_DELETE) || this.loginService.hasPermission(PRODUCTS.QOS_POLICY_EDIT)) {
            return ['id', 'Qos Policy Name', "Policy Name", 'Qos Speed', 'ISP Name', 'Action'];
        } else {
            return ['id', 'Qos Policy Name', "Policy Name", 'Qos Speed', 'ISP Name'];

        }
    }
    createQosPolicy() {
        this.gatewayAtrribute = this.fb.array([]);
        this.createView = true;
        this.listView = false;
        this.detailView = false;
        this.submitted = false;
        this.qosPolicyGroupForm.reset();
        this.isQosPolicyEdit = false;
        this.viewQosPolicyListData = [];
        this.upStreamProfileData = [];
        this.downStreamProfileData = [];
        this.assuredbandwidth = "";
        this.bandwidthUnit = "";
        this.bandwidthValue = "";
        if (!this.isUpStreamDetailView) {
            this.onAddOfGatwayMapping();
        }
    }

    searchQosPolicy() {
        this.createView = false;
        this.listView = true;
        this.detailView = false;
    }

    qosPolicyDetail(id) {
        this.createView = false;
        this.listView = false;
        this.detailView = true;
        this.dialogRef2 = this.dialog.open(this.viewTatForTaskDialog, {
            width: '1500px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef2.afterClosed().subscribe(() => {
            this.dialogRef2.close();
        });

        this.getQosPolicyById(id);
    }

    addEditQosPolicy(qosPolicyId) {
        this.submitted = true;
        if (this.gatewayAtrribute.length > 0 || this.isUpStreamDetailView) {
            if (this.qosPolicyGroupForm.valid && this.gatewayAtrribute.valid) {
                if (qosPolicyId) {
                    const url = "/qosPolicy/update";
                    this.viewQosPolicyListData = this.qosPolicyGroupForm.value;
                    this.viewQosPolicyListData.qosPolicyGatewayMappingList = this.gatewayAtrribute.value;
                    this.viewQosPolicyListData.id = qosPolicyId;
                    this.qosPolicyService.postMethod(url, this.viewQosPolicyListData).subscribe(
                        (response: any) => {
                            this.dialogRef.close();
                            if (response.responseCode == 406 || response.responseCode == 417) {
                                this.toastr.info(`${response.responseMessage}`, 'Info!');
                            } else {
                                this.submitted = false;
                                this.qosPolicyGroupForm.reset();
                                this.isQosPolicyEdit = false;
                                this.createView = false;
                                this.commondropdownService.clearCache("/qosPolicy/all");
                                // this.listView = true;
                                this.getQosPolicyList("");
                                this.viewQosPolicyListData = [];
                                this.gatewayAtrribute = this.fb.array([]);
                                this.toastr.success(`${response.responseMessage}`, 'Success!');
                                this.getQosPolicyList("");
                            }
                        },
                        (error: any) => {
                            console.log(error, "error");
                            this.toastr.error(`${error.error.ERROR}`, 'Error!');
                        }
                    );
                } else {
                    const url = "/qosPolicy/save";
                    this.createQosPolicyData = this.qosPolicyGroupForm.value;
                    this.createQosPolicyData.qosPolicyGatewayMappingList = this.gatewayAtrribute.value;
                    this.qosPolicyService.postMethod(url, this.createQosPolicyData).subscribe(
                        (response: any) => {
                            this.isQosPolicyEdit = false;
                            this.dialogRef.close();

                            if (
                                response.responseCode == 406 ||
                                response.responseCode == 500 ||
                                response.responseCode == 417
                            ) {
                                this.toastr.error(`${response.responseMessage}`, 'Failed')
                                // this.messageService.add({
                                //     severity: "error",
                                //     summary: "Error",
                                //     detail: response.responseMessage,
                                //     icon: "far fa-times-circle",
                                // });
                            } else {
                                this.submitted = false;
                                this.qosPolicyGroupForm.reset();
                                this.gatewayAtrribute = this.fb.array([]);
                                this.isQosPolicyEdit = false;
                                this.createView = false;
                                this.listView = true;
                                this.commondropdownService.clearCache("/qosPolicy/all");
                                this.toastr.success(`${response.responseMessage}`, 'Success!');
                                this.getQosPolicyList("");
                            }
                        },
                        (error: any) => {
                            console.log(error, "error");
                            this.toastr.error(`${error.error.ERROR}`, 'Error!');
                        }
                    );
                }
            }
        } else {
            this.toastr.error(`Atlease one gateway should be added`, 'Error!');
        }
    }
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageQosPolicyListdata > 1) {
            this.currentPageQosPolicyListdata = 1;
        }
        if (!this.searchkey) {
            this.getQosPolicyList(this.showItemPerPage);
        }
    }
    getQosPolicyList(list) {
        let size;
        this.listView = true;
        this.searchkey = "";
        let page_list = this.currentPageQosPolicyListdata;
        if (list) {
            size = list;
            this.qosPolicyListdataitemsPerPage = list;
        } else {
            // if (this.showItemPerPage == 0) {
            //   this.qosPolicyListdataitemsPerPage = this.pageITEM
            // } else {
            //   this.qosPolicyListdataitemsPerPage = this.showItemPerPage
            // }
            size = this.qosPolicyListdataitemsPerPage;
        }

        const url = "/qosPolicy";
        let qospolicydata = {
            page: page_list,
            pageSize: size,
        };
        this.qosPolicyService.postMethod(url, qospolicydata).subscribe(
            (response: any) => {
                this.qosPolicyListData = response.dataList;
                this.dataSource = new MatTableDataSource<any>(this.qosPolicyListData);

                this.qosPolicyListdatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    async editQosPolicy(qosPolicyId: number) {
        this.isQosPolicyEdit = true;
        if (!qosPolicyId) return;

        // Reset form & gateway array
        this.qosPolicyGroupForm.reset();
        this.gatewayAtrribute = this.fb.array([]);
        this.createView = true;
        this.listView = false;
        this.isQosPolicyEdit = true;

        const url = `/qosPolicy/${qosPolicyId}`;
        this.qosPolicyService.getMethod(url).subscribe(
            (response: any) => {
                if (response && response.data) {
                    const data = response.data;
                    this.viewQosPolicyListData = data;
                    this.deletedata = data;
                    this.qosPolicyData = data;

                    // Patch main form fields
                    this.qosPolicyGroupForm.patchValue({
                        name: data.name || "",
                        thpolicyname: data.thpolicyname || "",
                        thparam1: data.thparam1 || "",
                        thparam2: data.thparam2 || "",
                        thparam3: data.thparam3 || "",
                        description: data.description || "",
                        basepolicyname: data.basepolicyname || "",
                        baseparam1: data.baseparam1 || "",
                        baseparam2: data.baseparam2 || "",
                        baseparam3: data.baseparam3 || "",
                        type: data.type || "",
                        qosspeed: data.qosspeed || "",
                        upstreamprofileuid: data.upstreamprofileuid || "",
                        downstreamprofileuid: data.downstreamprofileuid || "",
                    });

                    // Load upstream/downstream profiles if set
                    if (data.upstreamprofileuid) this.getUpStreamProfiles(data.upstreamprofileuid);
                    if (data.downstreamprofileuid) this.getDownStreamProfiles(data.downstreamprofileuid);

                    // Populate gateway mapping rows
                    if (data.qosPolicyGatewayMappingList && data.qosPolicyGatewayMappingList.length > 0) {
                        data.qosPolicyGatewayMappingList.forEach(mapping => {
                            this.gatewayAtrribute.push(this.fb.group({
                                gatewayName: [mapping.gatewayName || "", Validators.required],
                                downloadSpeed: [mapping.downloadSpeed || "", Validators.required],
                                uploadSpeed: [mapping.uploadSpeed || "", Validators.required],
                                baseDownloadSpeed: [mapping.baseDownloadSpeed || "", Validators.required],
                                baseUploadSpeed: [mapping.baseUploadSpeed || "", Validators.required],
                                throttleDownloadSpeed: [mapping.throttleDownloadSpeed || "", Validators.required],
                                throttleUploadSpeed: [mapping.throttleUploadSpeed || "", Validators.required],
                                qosPolicyId: [mapping.qosPolicyId || ""],
                            }));
                        });
                    } else {
                        // Agar koi gateway mapping nahi hai to ek blank row show karo
                        this.onAddOfGatwayMapping();
                    }

                    // Refresh Material table datasource
                    this.dataSourcess.data = [...this.gatewayAtrribute.controls] as any[];

                    // Open dialog
                    this.dialogRef = this.dialog.open(this.addEditQosPolicyDialog, {
                        width: '1500px',
                        maxWidth: '90vw',
                        height: 'auto',
                        autoFocus: false,
                        disableClose: true
                    });

                    this.dialogRef.afterClosed().subscribe(() => {
                        this.qosPolicyGroupForm.reset();
                    });
                }
            },
            (error: any) => {
                console.error("Error fetching QoS policy:", error);
                this.toastr.error(`${error.error.ERROR}  || Failed to load policy details`, 'Error!');
            }
        );
    }


    async getQosPolicyById(qosPolicyId) {
        const url = "/qosPolicy/" + qosPolicyId;
        this.qosPolicyService.getMethod(url).subscribe(
            (response: any) => {
                this.viewQosPolicyListData = response.data;
                this.qosPolicyData = response.data;
                this.deletedata = this.viewQosPolicyListData;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    canExit() {
        if (!this.qosPolicyGroupForm.dirty) return true;
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

    deleteConfirmonQosPolicy(item) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: `Delete Qos Policy`,
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteQosPolicy(item.id);
            } else {
                this.toastr.info(`You have rejected`, 'Rejected!');
            }
        });
    }

    deleteQosPolicy(qosPolicyId) {
        const url = "/qosPolicy/delete";
        this.deletedata.id = qosPolicyId;
        this.qosPolicyService.postMethod(url, this.deletedata).subscribe(
            (response: any) => {
                if (
                    response.responseCode == 405 ||
                    response.responseCode == 406 ||
                    response.responseCode == 417
                ) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    if (this.currentPageQosPolicyListdata != 1 && this.totalAreaListLength == 1) {
                        this.currentPageQosPolicyListdata = this.currentPageQosPolicyListdata - 1;
                    }
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    this.getQosPolicyList("");
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    pageChangedQosPolicyList(event: PageEvent) {
        this.currentPageQosPolicyListdata = event.pageIndex + 1;
        this.qosPolicyListdataitemsPerPage = event.pageSize;
        if (this.searchkey) {
            this.search();
        } else {
            this.getQosPolicyList("");
        }
    }

    onAddOfGatwayMapping() {
        if (this.allowedGateway <= this.gatewayAtrribute.length) {
            this.toastr.info(`Can not add more than ${this.allowedGateway} gateway details.`, 'Info!');
            return;
        } else {
            this.submitted = false;
            this.gatewayAtrribute.push(this.createGatewayMappingGroup());
            this.gatewayAtrribute.controls = this.gatewayAtrribute.controls;
            this.dataSourcess.data = [...this.gatewayAtrribute.controls] as any[];
        }
    }
    deleteConfirmInActiveAttribute(attributeIndex: number) {
        this.gatewayAtrribute.removeAt(attributeIndex);
        this.dataSourcess.data = [...this.gatewayAtrribute.controls] as any[];
    }
    createGatewayMappingGroup(): UntypedFormGroup {
        return this.fb.group({
            gatewayName: ["", Validators.required],
            downloadSpeed: ["", Validators.required],
            uploadSpeed: ["", Validators.required],
            baseDownloadSpeed: ["", Validators.required],
            baseUploadSpeed: ["", Validators.required],
            throttleDownloadSpeed: ["", Validators.required],
            throttleUploadSpeed: ["", Validators.required],
            qosPolicyId: [""],
        });
    }
    search() {
        if (!this.searchkey || this.searchkey !== this.searchName) {
            this.currentPageQosPolicyListdata = 1;
        }
        this.searchkey = this.searchName;
        if (this.showItemPerPage) {
            this.qosPolicyListdataitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchName.trim();

        const url = `/qosPolicy/search?page=${this.currentPageQosPolicyListdata}&pageSize=${this.qosPolicyListdataitemsPerPage}&sortBy=id&sortOrder=0`;
        this.qosPolicyService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.qosPolicyListData = response.dataList;
                this.qosPolicyListdatatotalRecords = response.totalRecords;

                this.dataSource = new MatTableDataSource<any>(this.qosPolicyListData);
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }

                this.createView = false;
                this.listView = true;
                this.detailView = false;
            },
            (error: any) => {
                this.createView = false;
                this.listView = true;
                this.detailView = false;
                this.qosPolicyListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.qosPolicyListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }
    clearSearch() {
        this.searchName = "";
        this.searchkey = "";
        this.getQosPolicyList("");
        this.submitted = false;
        this.isQosPolicyEdit = false;
        this.createView = false;
        this.listView = true;
        this.detailView = false;
        this.qosPolicyGroupForm.reset();
    }

    onKeymobilelength(event) {
        const pattern = /[0-9\.]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    upProfileLeave() {
        let profileType = this.qosPolicyGroupForm.controls.upstreamprofileName.value;
        this.getUpStreamProfiles(profileType);
    }

    downProfileLeave() {
        let profileType = this.qosPolicyGroupForm.controls.downstreamprofileName.value;
        this.getDownStreamProfiles(profileType);
    }

    getUpStreamProfiles(profileType) {
        this.upStreamProfileData = [];
        this.filteredUpstreamProfile = [];
        if (profileType) {
            this.nmsService.getUpStreamProfileByType(profileType).subscribe(
                (response: any) => {
                    if (response.responseCode == "200") {
                        this.upStreamProfileData = response.dataList;

                        this.filteredUpstreamProfile = this.upStreamProfileData.find(
                            obj => obj["profile-name"] === profileType
                        );
                        //   console.log(this.upStreamProfileData);
                        if (this.filteredUpstreamProfile) {
                            this.assuredbandwidth = this.filteredUpstreamProfile["assured-bandwidth"];
                            this.bandwidthUnit = this.filteredUpstreamProfile["bandwidth-unit"];
                        }
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
        }
    }

    getDownStreamProfiles(profileType) {
        this.downStreamProfileData = [];
        this.filteredDownstreamProfile = [];
        if (profileType) {
            this.nmsService.getDownStreamProfileByType(profileType).subscribe(
                (response: any) => {
                    if (response.responseCode == "200") {
                        this.downStreamProfileData = response.dataList;
                        this.downStreamProfilename = profileType;
                        this.filteredDownstreamProfile = this.downStreamProfileData.find(
                            obj => obj["uuid"] === profileType
                        );
                        if (this.filteredDownstreamProfile) {
                            this.bandwidthValue =
                                this.filteredDownstreamProfile["committed-information-rate"].value;
                        }
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            );
        }
    }

    onDropdownChange(event: any, downStreamProfileData) {
        const selectedValue = event.value;
        this.upStreamprofilename = event.value;
        this.filteredUpstreamProfile = downStreamProfileData.find(
            obj => obj["profile-name"] === this.upStreamprofilename
        );
        this.assuredbandwidth = this.filteredUpstreamProfile["assured-bandwidth"];
        this.bandwidthUnit = this.filteredUpstreamProfile["bandwidth-unit"];
    }

    getUpStreamProfileData(name, upStreamprofilename) {
        this.filteredUpstreamProfile = name.find(
            obj => obj["profile-name"] === this.qosPolicyGroupForm.value.upstreamprofileuid
        );
        $("#UpProfilename").modal("show");
    }

    ondownstramDropdownChange(event: any, downstream) {
        const selectedValue = event.value;
        this.downStreamProfilename = event.value;
        this.filteredDownstreamProfile = downstream.find(obj => obj["uuid"] === selectedValue);
        this.bandwidthValue = this.filteredDownstreamProfile["committed-information-rate"].value;
        // $("#DownProfilename").modal("show");
    }

    getDownStreamProfileData(name, downstream) {
        this.filteredDownstreamProfile = downstream.find(obj => obj["uuid"] === name);
        this.bandwidthValue = this.filteredDownstreamProfile["committed-information-rate"].value;
        this.commistedBustsize = this.filteredDownstreamProfile["committed-burst-size"].value;
        this.peakBustsize = this.filteredDownstreamProfile["peak-burst-size"].value;
        $("#DownProfilename").modal("show");
    }

    // 
    qosPolicyList: any[] = [];
    dataSources = new MatTableDataSource<any>(this.qosPolicyList);

    addEDitQosPolicyDialog() {
        this.isQosPolicyEdit = false;
        this.submitted = false;
        this.dialogRef = this.dialog.open(this.addEditQosPolicyDialog, {
            width: '1200px',
            data: {
                width: '1500px',
                maxWidth: '90vw',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            }
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.dialogRef.close();
        });
    }



    closeDialog() {
        this.submitted = false;
        if (this.dialogRef) {
            this.dialogRef.close();
        }
        if (this.dialogRef2) {
            this.dialogRef2.close();
        }
    }

}
