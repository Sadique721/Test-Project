import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService } from "primeng/api";
import { ToastrService } from "ngx-toastr"; // Added ToastrService import
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { ResolutionMasterService } from "src/app/service/resolution-master.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { TICKETING_SYSTEMS } from "src/app/constants/aclConstants";
import { saveAs as importedSaveAs } from "file-saver";
import { DomSanitizer } from "@angular/platform-browser";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";

@Component({
    selector: "app-resolution-master",
    templateUrl: "./resolution-master.component.html",
    styleUrls: ["./resolution-master.component.css"],
    standalone: false
})
export class ResolutionMasterComponent implements OnInit {

    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;

    resolutionMasterGroupForm: UntypedFormGroup;
    submitted = false;
    createResolutionMasterData: any;
    resolutionMasterDataList: any;
    currentPageResoluionMasterListdata = 1;
    resoluionMasterListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    resoluionMasterListdatatotalRecords: any;
    viewResolutionData: any;
    deletedata: any = {
        id: "",
        name: "",
        status: "",
    };

    statusOptions = RadiusConstants.status;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    totalAreaListLength = 0;

    isResolutionEdit = false;
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    rootCauseReasonMappingForm: UntypedFormGroup;

    rootCauseReasonMappingSubmitted: boolean;

    rootCauseReasonMapping: UntypedFormArray;
    currentPageReasonMapping = 0;
    currentPageSubReasonMapping = 0;
    reasonMappingItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    subReasonMappingItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    reasonMappingTotalRecords: string;
    subReasonMappingTotalRecords: string;
    listView: Boolean = false;
    currentPageRootCauseListdata = 1;
    searchData: any;
    rootCauseitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    searchRootCauseName: any = "";
    rootCauseListDatatotalRecords: any;
    rootCauseListData: any;
    ticketReasonSubCategoryListData: any;
    createView: boolean;
    detailView: boolean;

    rootCauseSubReasonMappingForm: UntypedFormGroup;
    rootCauseSubReasonMapping: UntypedFormArray;

    rootCauseSubReasonMappingSubmitted: boolean;

    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    pageItem;
    uploadDocForm: UntypedFormGroup;
    uploadDocumentId: boolean = false;
    selectedFile: any;
    rootCauseId: number;
    selectedFileUploadPreview: File[] = [];
    resolutionIdData: any;
    resolutionBaseFileData: any;
    downloadDocumentId: boolean = false;
    previewUrl: any;
    previewType: string = "";
    documentPreview: boolean = false;

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService, // Replaced messageService with toastr
        private resolutionMasterService: ResolutionMasterService,
        loginService: LoginService, private dialog: MatDialog,
        private sanitizer: DomSanitizer
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(TICKETING_SYSTEMS.ROOT_CAUSE_CREATE);
        this.deleteAccess = loginService.hasPermission(TICKETING_SYSTEMS.ROOT_CAUSE_DELETE);
        this.editAccess = loginService.hasPermission(TICKETING_SYSTEMS.ROOT_CAUSE_EDIT);
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(TICKETING_SYSTEMS.ROOT_CAUSE_EDIT) || this.loginService.hasPermission(TICKETING_SYSTEMS.ROOT_CAUSE_DELETE)) {
            return ['id', 'name', 'status', 'action'];
        } else {
            return ['id', 'name', 'status'];
        }
    }

    ngOnInit(): void {
        this.resolutionMasterGroupForm = this.fb.group({
            name: ["", Validators.required],
            status: ["", Validators.required],
        });
        this.rootCauseReasonMappingForm = this.fb.group({
            id: [""],
            rootCauseReason: ["", Validators.required],
            resolutionId: [""],
        });
        this.rootCauseSubReasonMappingForm = this.fb.group({
            id: [""],
            subcateId: ["", Validators.required],
            resId: [""],
        });
        this.uploadDocForm = this.fb.group({
            file: ["", Validators.required]
        });

        this.getResolutionMaster("");
        this.getTicketReasonSubCategoryDataList();
        this.rootCauseReasonMapping = this.fb.array([]);
        this.rootCauseSubReasonMapping = this.fb.array([]);
        this.listView = true;
        this.searchData = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "any",
                },
            ],
            page: "",
            pageSize: "",
            sortBy: "createdate",
            sortOrder: 0,
        };
    }

    pageChangedData(number) {
        this.currentPageReasonMapping = number;
    }

    TotalItemPerPage(event): void {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageResoluionMasterListdata > 1) {
            this.currentPageResoluionMasterListdata = 1;
        }
        if (!this.searchkey) {
            this.getResolutionMaster(this.showItemPerPage);
        }
    }

    getResolutionMaster(list): void {
        let size;
        this.searchkey = "";
        const pageList = this.currentPageResoluionMasterListdata;
        if (list) {
            size = list;
            this.resoluionMasterListdataitemsPerPage = list;
        } else {
            size = this.resoluionMasterListdataitemsPerPage;
        }

        const url = "/resolutionReasons";
        const resolutionmasterdata = {
            page: pageList,
            pageSize: size,
        };
        this.resolutionMasterService.postMethod(url, resolutionmasterdata).subscribe(
            (response: any) => {
                this.resolutionMasterDataList = response.dataList;
                this.resoluionMasterListdatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    validateText() {
        this.resolutionMasterGroupForm.value.name = this.resolutionMasterGroupForm.value.name.trim();
        if (
            this.resolutionMasterGroupForm.value.name == "" ||
            this.resolutionMasterGroupForm.value.name == null
        ) {
            this.resolutionMasterGroupForm.patchValue({ name: "" });
        }
    }

    addEditResolutionMaster(resolutionMasterId): void {
        this.submitted = true;
        if (this.rootCauseReasonMapping.value.length <= 0) {
            this.toastr.info('Resolution is required please add atleast one.', 'Info!');
            return;
        }
        if (this.rootCauseSubReasonMapping.value.length <= 0) {
            this.toastr.info('Sub Problem Domain is required please add atleast one.', 'Info!');
            return;
        }
        if (this.resolutionMasterGroupForm.valid) {
            if (resolutionMasterId) {
                const url = "/resolutionReasons/update";
                this.viewResolutionData = this.resolutionMasterGroupForm.value;
                this.viewResolutionData.rootCauseResolutionMappingList = this.rootCauseReasonMapping.value;
                this.viewResolutionData.resoSubCategoryMappingList = this.rootCauseSubReasonMapping.value;
                this.viewResolutionData.rootCauseResolutionMappingList.forEach(e => {
                    e.resolutionId = resolutionMasterId;
                });
                this.viewResolutionData.id = resolutionMasterId;
                if (
                    this.rootCauseReasonMapping.value.length > 0 &&
                    this.rootCauseSubReasonMapping.value.length > 0
                ) {
                    this.resolutionMasterService.postMethod(url, this.viewResolutionData).subscribe(
                        (response: any) => {
                            if (
                                response.responseCode === 406 ||
                                response.responseCode === 417 ||
                                response.responseCode === 500
                            ) {
                                this.toastr.info(`${response.responseMessage}`, 'Info!');
                            } else {
                                this.submitted = false;
                                this.searchViewTrc();
                                this.rootCauseReasonMapping = this.fb.array([]);
                                this.rootCauseReasonMappingSubmitted = false;
                                this.resolutionMasterGroupForm.reset();
                                this.rootCauseSubReasonMapping = this.fb.array([]);
                                this.rootCauseSubReasonMappingSubmitted = false;
                                this.getResolutionMaster("");
                                this.isResolutionEdit = false;
                                this.toastr.success(`Successfully Updated`, 'Success!');
                            }
                        },
                        (error: any) => {
                            console.log(error, "error");
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        }
                    );
                } else {
                    if (this.rootCauseReasonMapping.value.length == 0) {
                        this.toastr.error('Minimum one Resolution Details need to add.', 'Failed!');
                    } else {
                        this.toastr.error('Minimum one Sub Problem Domain need to add.', 'Failed!');
                    }
                }
            } else {
                const url = "/resolutionReasons/save";
                this.createResolutionMasterData = this.resolutionMasterGroupForm.value;
                this.createResolutionMasterData.rootCauseResolutionMappingList =
                    this.rootCauseReasonMapping.value;
                this.createResolutionMasterData.resoSubCategoryMappingList =
                    this.rootCauseSubReasonMapping.value;
                this.createResolutionMasterData.isDeleted = false;
                if (
                    this.rootCauseReasonMapping.value.length > 0 &&
                    this.rootCauseSubReasonMapping.value.length > 0
                ) {
                    this.resolutionMasterService.postMethod(url, this.createResolutionMasterData).subscribe(
                        (response: any) => {
                            if (response.responseCode === 406) {
                                this.toastr.info(`${response.responseMessage}`, 'Info!');
                            } else {
                                this.submitted = false;
                                this.searchViewTrc();
                                this.resolutionMasterGroupForm.reset();
                                this.rootCauseReasonMappingSubmitted = false;
                                this.rootCauseReasonMapping = this.fb.array([]);
                                this.rootCauseSubReasonMapping = this.fb.array([]);
                                this.getResolutionMaster("");
                                this.toastr.success(`Successfully Added`, 'Success!');
                            }
                        },
                        (error: any) => {
                            console.log(error, "error");
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        }
                    );
                } else {
                    if (this.rootCauseReasonMapping.value.length == 0) {
                        this.toastr.error('Minimum one Resolution Details need to add.', 'Failed!');
                    } else {
                        this.toastr.error('Minimum one Sub Problem Domain need to add.', 'Failed!');
                    }
                }
            }
        }
    }

    editResolution(resolutionId): void {
        if (resolutionId) {
            this.isResolutionEdit = true;

            const url = "/resolutionReasons/" + resolutionId;
            this.resolutionMasterService.getMethod(url).subscribe(
                (response: any) => {
                    this.createView = true;
                    this.listView = false;
                    this.viewResolutionData = response.data;
                    this.rootCauseReasonMappingForm.reset();
                    this.rootCauseSubReasonMapping = this.fb.array([]);
                    this.rootCauseSubReasonMappingForm.reset();
                    this.rootCauseReasonMapping = this.fb.array([]);
                    this.resolutionMasterGroupForm.patchValue(response.data);
                    this.viewResolutionData.rootCauseResolutionMappingList.forEach(e => {
                        this.rootCauseReasonMapping.push(this.fb.group(e));
                    });
                    this.viewResolutionData.resoSubCategoryMappingList.forEach(e => {
                        this.rootCauseSubReasonMapping.push(this.fb.group(e));
                    });
                    this.deletedata = this.viewResolutionData;
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    async getResolutionById(resolutionId) {
        const url = "/resolutionReasons/" + resolutionId.id;
        this.resolutionMasterService.getMethod(url).subscribe(
            (response: any) => {
                this.viewResolutionData = response.data;
                this.deleteResolution(resolutionId);
                this.deletedata = {
                    id: response.data.id,
                    name: response.data.name,
                    status: response.data.status
                };
            },
            (error: any) => {
                console.log(error, "error");
            }
        );
    }

    deleteConfirmonResolution(resolutionId): void {
        if (resolutionId) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: "400px",
                data: {
                    title: "Delete Confirmation",
                    description: `Do you want to delete this Root Cause?`,
                    yesLabel: "Delete",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.getResolutionById(resolutionId);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
    }

    deleteResolution(resolutionId): void {
        const url = "/resolutionReasons/delete";
        this.resolutionMasterService.postMethod(url, this.viewResolutionData).subscribe(
            (response: any) => {
                if (response.responseCode !== 200) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                } else {
                    if (this.currentPageResoluionMasterListdata !== 1 && this.totalAreaListLength === 1) {
                        this.currentPageResoluionMasterListdata = this.currentPageResoluionMasterListdata - 1;
                    }
                    this.toastr.success(`Successfully Deleted`, 'Success!');
                    this.submitted = false;
                    this.resolutionMasterGroupForm.reset();
                    this.rootCauseReasonMappingSubmitted = false;
                    this.rootCauseReasonMapping = this.fb.array([]);
                    this.rootCauseSubReasonMappingSubmitted = false;
                    this.rootCauseSubReasonMapping = this.fb.array([]);
                    this.isResolutionEdit = false;
                    this.getResolutionMaster("");
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedResolutionMasterList(pageNumber): void {
        this.currentPageResoluionMasterListdata = pageNumber.pageIndex + 1;
        this.resoluionMasterListdataitemsPerPage = pageNumber.pageSize

        this.getResolutionMaster("");
    }

    rootCauseReasonMappingFormGroup(): UntypedFormGroup {
        return this.fb.group({
            rootCauseReason: [
                this.rootCauseReasonMappingForm.value.rootCauseReason,
                [Validators.required],
            ],
            resolutionId: [""],
        });
    }

    onAddRootCauseReasonMappingField(): void {
        this.rootCauseReasonMappingSubmitted = true;
        if (this.rootCauseReasonMappingForm.valid) {
            this.rootCauseReasonMapping.push(this.rootCauseReasonMappingFormGroup());
            this.rootCauseReasonMappingForm.reset();
            this.rootCauseReasonMappingSubmitted = false;
            this.reasonTable.renderRows()
        }
    }

    async onRemoveRootCauseReasonMapping(
        reasonMappingFieldIndex: number,
        reasonMappingFieldId: number
    ): Promise<void> {
        this.rootCauseReasonMapping.removeAt(reasonMappingFieldIndex);
        this.reasonTable.renderRows()
    }

    searchTrc() {
        if (!this.searchkey || this.searchkey !== this.searchData) {
            this.currentPageResoluionMasterListdata = 1;
        }
        this.searchkey = this.searchData;
        if (this.showItemPerPage) {
            this.resoluionMasterListdataitemsPerPage = this.showItemPerPage;
        }
        let data: any = [];
        this.searchData.filters[0].filterColumn = "any";
        this.searchData.filters[0].filterValue = this.searchRootCauseName.trim();
        this.searchData.page = this.currentPageResoluionMasterListdata;
        this.searchData.pageSize = this.resoluionMasterListdataitemsPerPage;
        data = this.searchData;

        const url = "/resolutionReasons/searchAll";
        this.resolutionMasterService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response?.dataList?.length > 0) {
                    this.resolutionMasterDataList = response.dataList;
                    this.resoluionMasterListdatatotalRecords = response.totalRecords;
                } else {
                    this.resolutionMasterDataList = [];
                    this.resoluionMasterListdatatotalRecords = 0;
                    this.toastr.info('No Record Found', 'Info!');
                }
            },
            (error: any) => {
                this.resoluionMasterListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.resolutionMasterDataList = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    clearSearchTrc() {
        this.searchRootCauseName = "";
        this.submitted = false;
        this.rootCauseReasonMapping = this.fb.array([]);
        this.rootCauseSubReasonMapping = this.fb.array([]);
        this.rootCauseReasonMappingSubmitted = false;
        this.resolutionMasterGroupForm.reset();
        this.getResolutionMaster("");
        this.isResolutionEdit = false;
    }

    canExit() {
        if (!this.resolutionMasterGroupForm.dirty) {
            return true;
        }
        {
            return Observable.create((observer: Observer<boolean>) => {
                const dialogRef = this.dialog.open(this.confirmDialog, {
                    width: "400px",
                    data: {
                        title: "Alert",
                        description: `The filled data will be lost. Do you want to continue? (Yes/No)`,
                        yesLabel: "Yes",
                        noLabel: "No"
                    }
                });

                dialogRef.afterClosed().subscribe(result => {
                    if (result) {
                        observer.next(true);
                        observer.complete();
                    } else {
                        observer.next(false);
                        observer.complete();
                    }
                });

                return false;
            });
        }
    }

    TotalItemPerPageReasonMappingTotalRecords(event: any) {
        this.reasonMappingItemsPerPage = event.value;
    }

    getTicketReasonSubCategoryDataList() {
        const pagedata = {
            page: 1,
            pageSize: 100000,
        };
        const url = "/ticketReasonSubCategory";
        this.resolutionMasterService.postMethod(url, pagedata).subscribe(
            (response: any) => {
                this.ticketReasonSubCategoryListData = response.dataList;
                this.ticketReasonSubCategoryListData = response.dataList.filter(
                    element => element.status === "Active"
                );
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    createTrc() {
        this.listView = false;
        this.createView = true;
        this.detailView = false;
        this.submitted = false;
        this.isResolutionEdit = false;
        this.resolutionMasterGroupForm.reset();
        this.rootCauseReasonMappingForm.reset();
        this.rootCauseReasonMapping = this.fb.array([]);
        this.rootCauseSubReasonMapping = this.fb.array([]);
    }

    searchViewTrc() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.pageItem = this.resoluionMasterListdataitemsPerPage;
        this.getResolutionMaster("");
        this.searchRootCauseName = "";
    }

    rootCauseSubReasonMappingFormGroup(): UntypedFormGroup {
        return this.fb.group({
            subcateId: [this.rootCauseSubReasonMappingForm.value.subcateId, [Validators.required]],
            resId: [""],
        });
    }

    onAddRootCauseSubReasonMappingField(): void {
        this.rootCauseSubReasonMappingSubmitted = true;
        if (this.rootCauseSubReasonMappingForm.valid) {
            this.rootCauseSubReasonMapping.push(this.rootCauseSubReasonMappingFormGroup());
            this.rootCauseSubReasonMappingForm.reset();
            this.rootCauseSubReasonMappingSubmitted = false;
            this.rootcaseTable.renderRows()
        }
    }

    async onRemoveRootCauseSubReasonMapping(
        reasonMappingFieldIndex: number,
        reasonMappingFieldId: number
    ): Promise<void> {
        this.rootCauseSubReasonMapping.removeAt(reasonMappingFieldIndex);
        this.rootcaseTable.renderRows()
    }

    pageChangedSubReasonData(number) {
        this.currentPageSubReasonMapping = number;
    }

    TotalItemPerPageSubReasonMappingTotalRecords(event): void {
        this.subReasonMappingItemsPerPage = Number(event.value);
        if (this.currentPageSubReasonMapping > 1) {
            this.currentPageSubReasonMapping = 1;
        }
    }

    uploadDocuments(dialogRef) {
        this.submitted = true;
        if (this.uploadDocForm.valid) {
            const formData = new FormData();
            let fileArray: FileList;
            if (this.uploadDocForm.controls.file) {
                if (
                    this.selectedFile.type != "image/png" &&
                    this.selectedFile.type != "image/jpg" &&
                    this.selectedFile.type != "image/jpeg" &&
                    this.selectedFile.type != "application/pdf"
                ) {
                    alert("File type must be png, jpg, jpeg or pdf");
                } else {
                    fileArray = this.uploadDocForm.controls.file.value;
                    Array.from(fileArray).forEach(file => {
                        formData.append("fileList", file);
                    });
                }
            }
            const url = `/resolutionReasons/uploadFile/${this.rootCauseId}`;
            this.resolutionMasterService.postMethod(url, formData).subscribe(
                (response: any) => {
                    if (response.responseCode === 406) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');
                    } else if (response.responseCode === 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');
                    } else {
                        this.submitted = false;
                        this.toastr.success(`Succesfully Uploaded`, 'Success!');
                        dialogRef.close()
                        this.uploadDocumentId = false;
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    uploadDocument(resolution) {
        this.rootCauseId = resolution.id;
        this.uploadDocForm.patchValue({
            file: ""
        });
        this.selectedFileUploadPreview = [];
        this.uploadDocumentId = true;
        this.dialog.open(this.uploadDocumentDialog, {
            width: '400px',
            disableClose: true
        });
    }

    closeUploadDocumentId() {
        this.uploadDocumentId = false;
        this.submitted = false;
        this.uploadDocForm.patchValue({
            file: ""
        });
        this.selectedFileUploadPreview = [];
    }

    onFileChangeUpload(event: any) {
        this.selectedFileUploadPreview = [];
        if (event.target.files.length > 0) {
            this.selectedFile = event.target.files[0];
            const files: FileList = event.target.files;
            for (let i = 0; i < files.length; i++) {
                this.selectedFileUploadPreview.push(files.item(i));
            }
            if (
                this.selectedFile.type != "image/png" &&
                this.selectedFile.type != "image/jpg" &&
                this.selectedFile.type != "image/jpeg" &&
                this.selectedFile.type != "application/pdf"
            ) {
                this.uploadDocForm.controls.file.reset();
                alert("File type must be png, jpg, jpeg or pdf");
            } else {
                const file = event.target.files;
                this.uploadDocForm.patchValue({
                    file: file
                });
            }
        }
    }

    deletUploadedFile(event: any) {
        var temp: File[] = this.selectedFileUploadPreview?.filter((item: File) => item?.name != event);
        this.selectedFileUploadPreview = temp;
        this.uploadDocForm.patchValue({
            file: temp
        });
    }

    downloadDocument(resolution) {
        this.resolutionIdData = resolution.id;
        const url = "/resolutionReasons/fileList/" + this.resolutionIdData;

        this.resolutionMasterService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode === 200 && response.dataList != null && response.dataList.length > 0) {
                    const fileDetails = response.dataList.map((item: any) => ({
                        id: item.id,
                        filename: item.filename,
                        uniqueName: item.uniquename,
                        latitude: item.latitiude,
                        longitude: item.longitude,
                        caseId: item.caseId,
                        staffId: item.staffId,
                        resolutionTime: item.resolutionTime,
                        remarks: item.remarks
                    }));

                    this.resolutionBaseFileData = {
                        resolutionBaseId: this.resolutionIdData,
                        fileDetails
                    };

                    // this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.downloadDocumentId = true;
                    this.dialog.open(this.viewDocumentDialog, {
                        width: '50%',
                        disableClose: true
                    });
                } else {
                    this.toastr.info('No files available for this document', 'Info!');
                }
            },
            (error: any) => {
                console.error("API Error: ", error);
                this.toastr.error(`${error?.error?.ERROR}`, 'Failed!');
            }
        );
    }

    downloadDoc(fileName, resolutionBaseId, uniquename) {
        this.resolutionMasterService.downloadFile(resolutionBaseId, uniquename).subscribe(
            blob => {
                if (blob.status == 200) {
                    this.toastr.success('Download Successfully', 'Success!');
                    importedSaveAs(blob.body, fileName);
                } else if (blob.status == 404) {
                    this.toastr.error('File Not Found', 'Failed!');
                } else if (blob.status == 204) {
                    this.toastr.info('Can\'t Download, File is Remove From The Server Directory', 'Info!');
                } else {
                    this.toastr.error('Something went wrong!', 'Failed!');
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    showDocData(fileName, resolutionBaseId, uniqueName) {
        const extension = fileName.split(".").pop().toLowerCase();
        this.resolutionMasterService.downloadFile(resolutionBaseId, uniqueName).subscribe(
            data => {
                if (data.status == 200) {
                    let mimeType = "application/octet-stream";
                    switch (extension) {
                        case "pdf":
                            mimeType = "application/pdf";
                            break;
                        case "png":
                            mimeType = "image/png";
                            break;
                        case "jpg":
                        case "jpeg":
                            mimeType = "image/jpeg";
                            break;
                        case "mp4":
                            mimeType = "video/mp4";
                            break;
                    }

                    const blob = new Blob([data.body], { type: mimeType });
                    const blobUrl = URL.createObjectURL(blob);
                    this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
                    this.previewType = extension;
                    this.documentPreview = true;

                    this.dialog.open(this.documentPreviewDialog, {
                        width: '80%',
                        disableClose: true
                    });
                } else if (data.status == 404) {
                    this.toastr.error('File Not Found', 'Failed!');
                } else if (data.status == 204) {
                    this.toastr.info('File is Remove From The Server Directory', 'Info!');
                } else {
                    this.toastr.error('Something went wrong!', 'Failed!');
                }
            },
            error => {
                console.log(error);
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    closeDocumentPreview() {
        this.documentPreview = false;
        this.previewUrl = null;
        this.previewType = "";
    }

    deleteResolveConfirm(file) {
        const dialogRef = this.dialog.open(this.confirmDialog, {
            width: "400px",
            data: {
                title: "Delete Confirmation",
                description: `Do you want to delete this File?`,
                yesLabel: "Delete",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteResolveDoc(file);
            } else {
                this.toastr.info(`Delete operation was cancelled`, 'Info!');
            }
        });
    }

    deleteResolveDoc(filedata: any) {
        const resolutionBaseId = filedata.id;
        const fileName = filedata.filename;
        const uniqueName = filedata.uniqueName;
        const url = `/resolutionReasons/deletefiles/${resolutionBaseId}`;

        this.resolutionMasterService.deleteMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`Successfully Deleted`, 'Success!');

                    this.resolutionBaseFileData.fileDetails = this.resolutionBaseFileData.fileDetails.filter(
                        (file: any) => file.uniqueName !== uniqueName
                    );
                    this.viewDucumentTable.renderRows()

                } else if (response.responseCode == 404) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error?.ERROR || 'Server error'}`, 'Failed!');
            }
        );
    }

    closeDownloadDocumentId() {
        this.downloadDocumentId = false;
    }

    onNextStep1(stepper: any) {
        this.submitted = true
        this.resolutionMasterGroupForm.markAllAsTouched();
        if (this.resolutionMasterGroupForm.valid) {
            stepper.next();
        }
    }

    onNextStep2(stepper: any) {
        if (this.rootCauseSubReasonMapping.value.length <= 0) {
            this.toastr.info('Sub Problem Domain is required please add atleast one.', 'Info!');
        } else {
            stepper.next();
        }
    }

    @ViewChild('rootcaseTable') rootcaseTable!: MatTable<any>;
    @ViewChild('reasonTable') reasonTable!: MatTable<any>;
    @ViewChild('uploadDocumentDialog') uploadDocumentDialog!: TemplateRef<any>;
    @ViewChild('viewDocumentDialog') viewDocumentDialog!: TemplateRef<any>;
    @ViewChild('documentPreviewDialog') documentPreviewDialog!: TemplateRef<any>;
    @ViewChild('viewDucumentTable') viewDucumentTable!: MatTable<any>;

    displayedColumns = ['id', 'name', 'status', 'action']
    displayeviewDocumentdColumns = ['filename', 'action']
    displayrootCase = ['subcateId', 'delete']
    displayreso = ['resolution', 'delete']

    dataSourceData = [{}];
    pageIndex = 0;
    pageSize = 5;
}
