import { Component, ElementRef, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { ConfirmationService } from "primeng/api";
import { CountryManagement } from "src/app/components/model/country-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import {
    AREA,
    CITY,
    COUNTRY,
    KNOWLEDGEBASE,
    STATE,
    SUBAREA
} from "src/app/RadiusUtils/RadiusConstants";
import { IDeactivateGuard } from "src/app/service/deactivate.service";
import { Observable, Observer } from "rxjs";
import { MASTERS, SETTINGS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { DomSanitizer } from "@angular/platform-browser";
import { saveAs as importedSaveAs } from "file-saver";
import { knowldegeBaseManagementService } from "src/app/service/knowledgebase-management.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { ToastrService } from 'ngx-toastr';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Component({
    selector: "app-knowledge-base-management",
    templateUrl: "./knowledge-base.component.html",
    styleUrls: ["./knowledge-base.component.css"],
    standalone: false
})
export class KnowledgeBaseComponent implements OnInit, IDeactivateGuard {
    @ViewChild("fileInput") fileInput: ElementRef;
    title = KNOWLEDGEBASE;

    documentPreviewDialogRef: MatDialogRef<any>;

    displayedColumns: string[] = ['id', 'eventType', 'category', 'type', 'action'];
    dataSource = new MatTableDataSource<any>();

    displayedColumns1: string[] = ['fileName', 'action'];
    knowledgeBaseFileDataSource = new MatTableDataSource<any>([]);

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('filePaginator') filePaginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    isDialogOpen: boolean = false;
    dialogRef: any;
    paginatedFileData: any[] = [];
    mvnoId: any;
    inputshowSelsctData: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    knowledgeBaseFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    knowldegeBaseManagement: any;
    isKnowledgebaseEdit: boolean = false;
    viewKnowledgeBaseListData: any;
    currentPageKnowledgeBaseSlab = 1;
    knowledgebaseitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    knowledgebaseTotalRecords: any;
    knowledgebaseName: any = "";
    searchData: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    currentPageKnowledgeListData = 1;
    showItemPerPage: any;
    searchkey: string;
    public loginService: LoginService;
    selectedFilePreview: any[];
    selectedFile: any;
    downloadDocumentId: boolean = false;
    previewUrl: any;
    documentPreview: boolean = false;
    knowledgebaseIdData: any;
    knowledgeBaseFileData: any;
    multiFiles: any;
    knowledgebaseListData: any[];
    previewType: string = "";
    selectedFilePreviewd: any[];
    currentFilePage: number;
    pageSize: number;
    constructor(
        private fb: UntypedFormBuilder,
        private toastr: ToastrService,
        private dialog: MatDialog,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private knowledgeBaseManagementService: knowldegeBaseManagementService,
        loginService: LoginService,
        private sanitizer: DomSanitizer,
        private commonDropdownService: CommondropdownService
    ) {
        this.loginService = loginService;
        this.createAccess = loginService.hasPermission(SETTINGS.KNOWLEDGE_BASE_CREATE);
        this.deleteAccess = loginService.hasPermission(SETTINGS.KNOWLEDGE_BASE_DELETE);
        this.editAccess = loginService.hasPermission(SETTINGS.KNOWLEDGE_BASE_EDIT);
    }

    ngOnInit(): void {
        this.knowledgeBaseFormGroup = this.fb.group({
            eventName: ["", Validators.required],
            documentFor: ["", Validators.required],
            docType: ["", Validators.required],
            file: ["", Validators.required],
            remarks: ["", Validators.required]
        });

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
        this.getKnowledgeBaseList("");
        this.commonDropdownService.getAllKnowledgeCategory();
        this.commonDropdownService.getAllKnowledgeType();
        this.commonDropdownService.getAllKnowledgeEvent();
        this.mvnoId = Number(localStorage.getItem("mvnoId"));
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(SETTINGS.KNOWLEDGE_BASE_DELETE) || this.loginService.hasPermission(SETTINGS.KNOWLEDGE_BASE_EDIT)) {
            return ['id', 'eventType', 'category', 'type', 'action'];
        } else {
            return ['id', 'eventType', 'category', 'type'];
        }
    }
    canExit() {
        if (!this.knowledgeBaseFormGroup.dirty) return true;
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

    addEditKnowledgebase(id) {
        this.submitted = true;
        const hasFiles =
            (this.multiFiles && this.multiFiles.length > 0) ||
            (this.selectedFilePreview && this.selectedFilePreview.length > 0) ||
            (id && this.selectedFilePreviewd && this.selectedFilePreviewd.length > 0);

        if (!hasFiles) {
            this.knowledgeBaseFormGroup.controls.file.setErrors({ required: true });
        } else {
            this.knowledgeBaseFormGroup.controls.file.setErrors(null);
        }

        if (this.knowledgeBaseFormGroup.valid) {
            const formData = new FormData();
            let fileArray: FileList = this.multiFiles;
            let fileNames: string[] = [];
            if (fileArray && fileArray.length > 0) {
                const isValidType = Array.from(fileArray).every(file =>
                    [
                        "image/png",
                        "image/jpg",
                        "image/jpeg",
                        "application/pdf",
                        "video/mp4",
                        "application/msword", // .doc
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // .docx
                    ].includes(file.type)
                );

                if (!isValidType) {
                    this.knowledgeBaseFormGroup.controls.file.reset();
                    this.toastr.info("File type must be png, jpg, jpeg, pdf or mp4", 'Info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: "File type must be png, jpg, jpeg, pdf or mp4",
                    //     icon: "far fa-check-circle"
                    // });
                    return;
                }
                const isValidSize = Array.from(fileArray).every(file => file.size <= 10 * 1024 * 1024); // 10MB

                if (!isValidSize) {
                    this.knowledgeBaseFormGroup.controls.file.reset();
                    this.toastr.error("Maximum file size is 10MB", 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: "Maximum file size is 10MB",
                    //     icon: "far fa-times-circle"
                    // });
                    return;
                }

                Array.from(fileArray).forEach(file => {
                    formData.append("file", file);
                    fileNames.push(file.name);
                });
            }

            this.knowldegeBaseManagement = this.knowledgeBaseFormGroup.value;

            //   if (fileNames.length > 0) {
            //     this.knowldegeBaseManagement.filename = fileNames.join(",");
            //   }

            if (id) {
                const url = "/knowledgeBase/upload/" + id;
                let updatedFormData = new FormData();
                this.knowldegeBaseManagement.filename = this.viewKnowledgeBaseListData.filename;
                let newFormData = Object.assign({}, this.knowldegeBaseManagement);
                delete newFormData.file;
                updatedFormData.append("knowledgeBaseDTO", JSON.stringify(newFormData));
                if (fileArray && fileArray.length > 0) {
                    Array.from(fileArray).forEach(file => {
                        updatedFormData.append("file", file);
                    });
                }

                this.knowledgeBaseManagementService.putMethod(url, updatedFormData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.isKnowledgebaseEdit = false;
                        this.knowledgeBaseFormGroup.reset();
                        this.knowledgeBaseFormGroup.controls.file.reset();
                        this.selectedFile = "";
                        this.selectedFilePreview = [];
                        this.selectedFilePreviewd = [];
                        this.multiFiles = "";
                        if (this.fileInput) {
                            this.fileInput.nativeElement.value = "";
                        }
                        this.toastr.success(`${response.message}`, 'Success!')
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: response.message,
                        //     icon: "far fa-check-circle"
                        // });

                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        // if (this.searchkey) {
                        //   this.searchKnowledgeBase();
                        // } else {
                        //   this.getKnowledgeBaseList("");
                        // }
                        this.clearFormData();
                        this.clearsearchKnowledgeBase();
                    },
                    (error: any) => {
                        if (error.error.status == 417 || error.error.status == 406) {
                            this.toastr.info(`${error.error.ERROR}`, 'Failed!')
                            // this.messageService.add({
                            //     severity: "info",
                            //     summary: "Info",
                            //     detail: error.error.ERROR,
                            //     icon: "far fa-times-circle"
                            // });
                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                            // this.messageService.add({
                            //     severity: "error",
                            //     summary: "Error",
                            //     detail: error.error.ERROR,
                            //     icon: "far fa-times-circle"
                            // });
                        }
                    }
                );
            }
            // Add new
            else {
                const url = "/knowledgeBase/upload";
                let newFormData = Object.assign({}, this.knowldegeBaseManagement);
                delete newFormData.file;

                formData.append("knowledgeBaseDTO", JSON.stringify(newFormData));

                this.knowledgeBaseManagementService.postMethod(url, formData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.knowledgeBaseFormGroup.reset();
                        this.selectedFile = "";
                        this.selectedFilePreview = [];
                        this.selectedFilePreviewd = [];
                        this.multiFiles = "";
                        if (this.fileInput) {
                            this.fileInput.nativeElement.value = "";
                        }
                        this.toastr.success(`${response.message}`, 'Success!')

                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: response.message,
                        //     icon: "far fa-check-circle"
                        // });
                        // if (this.searchkey) {
                        //   this.searchKnowledgeBase();
                        // } else {
                        //   this.getKnowledgeBaseList("");
                        // }
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        this.clearFormData();
                        this.clearsearchKnowledgeBase();
                    },
                    (error: any) => {
                        if (error.error.status == 417 || error.error.status == 406) {
                            // this.toastr.info(error.error.ERROR)
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                            // this.messageService.add({
                            //     severity: "info",
                            //     summary: "Info",
                            //     detail: error.error.ERROR,
                            //     icon: "far fa-times-circle"
                            // });
                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                            // this.messageService.add({
                            //     severity: "error",
                            //     summary: "Error",
                            //     detail: error.error.ERROR,
                            //     icon: "far fa-times-circle"
                            // });
                        }
                    }
                );
            }
        }
    }
    isWordFile(filename: string): boolean {
        const extension = filename.split(".").pop()?.toLowerCase();
        return extension === "doc" || extension === "docx";
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        this.knowledgebaseitemsPerPage = this.showItemPerPage;
        if (this.currentPageKnowledgeBaseSlab > 1) {
            this.currentPageKnowledgeBaseSlab = 1;
            if (this.paginator) {
                this.paginator.firstPage();
            }
        }
        if (!this.searchkey) {
            this.getKnowledgeBaseList(this.knowledgebaseitemsPerPage);
        } else {
            this.searchKnowledgeBase();
        }
    }

    editKnowledgebase(id) {
        if (id) {
            const url = "/knowledgeBase/getDocumentById/" + id;

            this.knowledgeBaseManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isKnowledgebaseEdit = true;
                    this.viewKnowledgeBaseListData = response.data;

                    if (this.viewKnowledgeBaseListData) {
                        this.viewKnowledgeBaseListData.filename = '';
                        this.viewKnowledgeBaseListData.uniqueName = '';
                    }
                    this.selectedFilePreviewd = [];
                    this.selectedFilePreview = [];
                    this.multiFiles = null;
                    this.knowledgeBaseFormGroup.controls.file.setValue(null);
                    if (this.fileInput) {
                        this.fileInput.nativeElement.value = '';
                    }

                    this.knowledgeBaseFormGroup.patchValue(this.viewKnowledgeBaseListData);
                    const filenames = response.data.filename?.split(",") || [];
                    const uniqueNames = response.data.uniqueName?.split(",") || [];
                    this.selectedFilePreviewd = [];
                    this.selectedFilePreviewd = filenames.map((name: string, index: number) => ({
                        name: name.trim(),
                        uniqueName: uniqueNames[index]?.trim()
                    }));
                    if (!this.isDialogOpen) {
                        this.openknowledgeBaseDialog(id);
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                }
            );
        }
    }

    clearsearchKnowledgeBase() {
        this.knowledgebaseName = "";
        this.searchkey = "";
        this.currentPageKnowledgeBaseSlab = 1;
        this.searchData.filters[0].filterValue = "";
        this.searchData.page = 1;
        this.searchData.pageSize = this.knowledgebaseitemsPerPage;

        if (this.paginator) {
            this.paginator.firstPage();
        }

        if (this.searchkey) {
            this.searchKnowledgeBase();
        } else {
            this.getKnowledgeBaseList("");
        }

        if (!this.isDialogOpen) {
            this.submitted = false;
            this.isKnowledgebaseEdit = false;
            this.clearFormData();
        }
        this.submitted = false;
        this.isKnowledgebaseEdit = false;
        this.knowledgeBaseFormGroup.reset();
        this.selectedFilePreview = [];
        this.selectedFilePreviewd = [];
        const fileInput: any = document.getElementById("fileInput");
        if (fileInput) {
            fileInput.value = "";
        }
    }

    deleteKnowledgeConfirmation(knowldegeBaseManagement) {
        if (knowldegeBaseManagement) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete "${knowldegeBaseManagement.eventName}"?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteKnowledgebase(knowldegeBaseManagement);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!')

                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Cancelled",
                    //     detail: "Delete operation was cancelled",
                    //     icon: "far fa-times-circle"
                    // });
                }
            });


            // this.confirmationService.confirm({
            //     message: "Do you want to delete this " + this.title + "?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deleteKnowledgebase(knowldegeBaseManagement);
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

    deleteKnowledgebase(knowldegeBaseManagement) {
        const url = "/knowledgeBase/delete/" + knowldegeBaseManagement?.id;
        this.knowledgeBaseManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPageKnowledgeBaseSlab != 1 && this.knowledgebaseListData.length == 1) {
                    this.currentPageKnowledgeBaseSlab = this.currentPageKnowledgeBaseSlab - 1;
                    if (this.paginator) {
                        this.paginator.previousPage();
                    }
                }
                if (
                    response.responseCode == 405 ||
                    response.responseCode == 406 ||
                    response.responseCode == 417
                ) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.success(`${response.message}`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-check-circle"
                    // });
                }
                this.clearsearchKnowledgeBase();
                // if (this.searchkey) {
                //   this.searchKnowledgeBase();
                // } else {
                //   this.getKnowledgeBaseList("");
                // }
            },
            (error: any) => {

                console.log(error, "error");
                this.toastr.error(`${error.responseMessage}`, 'Failed')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.responseMessage,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    ngAfterViewInit() {
        if (this.paginator) {
            this.paginator.page.subscribe((event: PageEvent) => {
                this.onPageChange(event);
            });
        }

        if (this.sort) {
            this.dataSource.sort = this.sort;
        }

        if (this.filePaginator) {
            this.filePaginator.page.subscribe((event: PageEvent) => {
                this.onKnowledgeBasePageChange(event);
            });
        }
    }


    pageChangedKnowledgebaseList(pageNumber) {
        this.currentPageKnowledgeBaseSlab = pageNumber;
        if (this.searchkey) {
            this.searchKnowledgeBase();
        } else {
            this.getKnowledgeBaseList("");
        }
    }

    onPageChange(event: PageEvent) {
        this.currentPageKnowledgeBaseSlab = event.pageIndex + 1;
        this.knowledgebaseitemsPerPage = event.pageSize;

        if (this.paginator) {
            this.paginator.pageIndex = event.pageIndex;
            this.paginator.pageSize = event.pageSize;
        }

        if (this.searchkey && this.searchkey.trim() !== '') {
            this.searchKnowledgeBase();
        } else {
            this.getKnowledgeBaseList("");
        }
    }

    onKnowledgeBasePageChange(event: PageEvent) {
        this.currentFilePage = event.pageIndex;
        this.pageSize = event.pageSize;
        this.updatePaginatedFileData();

    }

    getKnowledgeBaseList(list) {
        const url = "/knowledgeBase/search";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageKnowledgeBaseSlab;
        if (list) {
            size = list;
            this.knowledgebaseitemsPerPage = list;
        } else {
            size = this.knowledgebaseitemsPerPage;
        }
        this.searchData.page = pageList;
        this.searchData.pageSize = size;
        this.searchData.filters[0].filterValue = "";
        this.knowledgeBaseManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.knowledgebaseListData = response.dataList;
                this.knowledgebaseTotalRecords = response.totalRecords;
                this.dataSource = new MatTableDataSource(this.knowledgebaseListData);

                if (this.paginator) {
                    this.paginator.length = this.knowledgebaseTotalRecords;
                    this.paginator.pageIndex = this.currentPageKnowledgeBaseSlab - 1;
                    this.paginator.pageSize = this.knowledgebaseitemsPerPage;
                }
                this.searchkey = "";
            },
            (error: any) => {
                this.knowledgebaseListData = [];
                this.dataSource = new MatTableDataSource([]);
                this.knowledgebaseTotalRecords = 0;

                if (this.paginator) {
                    this.paginator.length = 0;
                    this.paginator.pageIndex = 0;
                }

                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}, 'Failed'`)
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    searchKnowledgeBase() {
        if (!this.searchkey || this.searchkey !== this.knowledgebaseName) {
            this.currentPageKnowledgeBaseSlab = 1;
            if (this.paginator) {
                this.paginator.firstPage();
            }
        }

        this.searchkey = this.knowledgebaseName;
        if (this.showItemPerPage) {
            this.knowledgebaseitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.knowledgebaseName.trim();
        this.searchData.page = this.currentPageKnowledgeBaseSlab;
        this.searchData.pageSize = this.knowledgebaseitemsPerPage;

        const url = "/knowledgeBase/search";
        this.knowledgeBaseManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.knowledgebaseListData = response.dataList;
                this.knowledgebaseTotalRecords = response.totalRecords;
                this.dataSource = new MatTableDataSource(this.knowledgebaseListData);
                if (this.paginator) {
                    this.paginator.length = this.knowledgebaseTotalRecords;
                    this.paginator.pageIndex = this.currentPageKnowledgeBaseSlab - 1;
                    this.paginator.pageSize = this.knowledgebaseitemsPerPage;
                }
            },
            (error: any) => {
                this.knowledgebaseTotalRecords = 0;
                this.knowledgebaseListData = [];
                this.dataSource = new MatTableDataSource([]);

                if (this.paginator) {
                    this.paginator.length = 0;
                    this.paginator.pageIndex = 0;
                }

                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.msg,
                    //     icon: "far fa-times-circle"
                    // });
                    this.knowledgebaseListData = [];
                } else {
                    this.toastr.info(`${error.response.ERROR}`, 'Info!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.response.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                }
            }
        );
    }


    onFileChange(event: any): void {
        this.selectedFilePreview = [];
        const inputElement = event.target as HTMLInputElement;
        if (inputElement.files && inputElement.files.length > 0) {
            const files: FileList = inputElement.files;
            for (let i = 0; i < files.length; i++) {
                const file = files.item(i);
                if (
                    (file &&
                        (file.type === "image/png" ||
                            file.type === "image/jpg" ||
                            file.type === "image/jpeg" ||
                            file.type === "application/pdf" ||
                            file.type === "video/mp4")) ||
                    file.type === "application/msword" || // .doc
                    file.type === "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // .docx
                ) {
                    this.selectedFilePreview.push(file);
                } else {
                    this.toastr.info(`Invalid file type: ${file?.name}. Must be png, jpg, jpeg, pdf, mp4, doc, or docx.`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: `Invalid file type: ${file?.name}. Must be png, jpg, jpeg, pdf, mp4, doc, or docx.`,
                    //     icon: "far fa-check-circle"
                    // });
                }
            }

            if (this.selectedFilePreview.length > 0) {
                this.multiFiles = this.createFileList(this.selectedFilePreview);
                this.selectedFile = this.selectedFilePreview[0];
                this.knowledgeBaseFormGroup.patchValue({
                    file: this.multiFiles[0]
                });
                if (!this.selectedFilePreviewd) {
                    this.selectedFilePreviewd = [];
                }

                this.selectedFilePreviewd.push(
                    ...this.selectedFilePreview.map(file => ({ name: file.name, status: "new" }))
                );

            } else {
                this.knowledgeBaseFormGroup.controls.file.reset();
                inputElement.value = "";
            }
        }
    }

    createFileList(files: File[]): FileList {
        const dataTransfer = new DataTransfer();
        files.forEach(file => dataTransfer.items.add(file));
        return dataTransfer.files;
    }

    deletSelectedFile(fileName: string) {
        this.selectedFilePreviewd = this.selectedFilePreviewd?.filter(
            (file: File) => file?.name !== fileName
        );
        if (this.multiFiles && this.multiFiles.length > 0) {
            const updatedFiles: File[] = [];
            for (let i = 0; i < this.multiFiles.length; i++) {
                if (this.multiFiles[i].name !== fileName) {
                    updatedFiles.push(this.multiFiles[i]);
                }
            }
            const dataTransfer = new DataTransfer();
            updatedFiles.forEach(file => dataTransfer.items.add(file));
            this.multiFiles = dataTransfer.files;
        }
        this.knowledgeBaseFormGroup.patchValue({
            file: this.selectedFilePreviewd
        });
    }
    //   downloadDocument(knowledgeBase) {
    //     this.knowledgebaseIdData = knowledgeBase.id;
    //     const url = "/knowledgeBase/getDocumentById/" + this.knowledgebaseIdData;

    //     this.knowledgeBaseManagementService.getMethod(url).subscribe(
    //       (response: any) => {
    //         if (response.data != null) {
    //           const filenameArray = response.data.filename ? response.data.filename.split(",") : [];
    //           const uniquenameArray = response.data.uniqueName
    //             ? response.data.uniqueName.split(",")
    //             : [];

    //           const fileDetails = filenameArray.map((filename, index) => ({
    //             filename: filename.trim(),
    //             uniqueName: uniquenameArray[index]?.trim() || ""
    //           }));

    //           this.knowledgeBaseFileData = {
    //             id: response.data.id,
    //             fileDetails
    //           };
    //         }
    //         console.log("knowledgeBaseFileData", this.knowledgeBaseFileData);
    //         if (response.responseCode == 200) {
    //           this.messageService.add({
    //             severity: "success",
    //             summary: "Successfully",
    //             detail: response.responseMessage,
    //             icon: "far fa-check-circle"
    //           });
    //           this.downloadDocumentId = true;
    //         } else if (response.responseCode == 404) {
    //           this.messageService.add({
    //             severity: "error",
    //             summary: "Error",
    //             detail: response.responseMessage,
    //             icon: "far fa-times-circle"
    //           });
    //         } else {
    //           this.messageService.add({
    //             severity: "error",
    //             summary: "Error",
    //             detail: response.responseMessage,
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
    downloadDocument(knowledgeBase) {
        this.knowledgebaseIdData = knowledgeBase.id;
        const url = "/knowledgeBase/getDocumentById/" + this.knowledgebaseIdData;

        this.knowledgeBaseManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.data != null) {
                    const filenameArray = response.data.filename ? response.data.filename.split(",") : [];
                    const uniquenameArray = response.data.uniqueName
                        ? response.data.uniqueName.split(",")
                        : [];

                    const fileDetails = filenameArray.map((filename, index) => ({
                        filename: filename.trim(),
                        uniqueName: uniquenameArray[index]?.trim() || ""
                    }));

                    this.knowledgeBaseFileData = {
                        id: response.data.id,
                        fileDetails
                    };
                }

                if (response.responseCode == 200) {
                    // Only show success message and open dialog if there are files
                    if (
                        this.knowledgeBaseFileData.fileDetails &&
                        this.knowledgeBaseFileData.fileDetails.length > 0
                    ) {
                        this.toastr.success(`${response.responseMessage}, 'Success!'`)
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: response.responseMessage,
                        //     icon: "far fa-check-circle"
                        // });
                        this.downloadDocumentId = true;
                    } else {
                        this.toastr.info(`No files available for this document`, 'Info!')
                        // this.messageService.add({
                        //     severity: "info",
                        //     summary: "Info",
                        //     detail: "No files available for this document",
                        //     icon: "far fa-info-circle"
                        // });
                    }
                } else if (response.responseCode == 404) {
                    this.toastr.error(`${response.responseMessage}, 'Failed'`)
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.error(`${response.responseMessage}, 'Failed'`)
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}, 'Failed'`)
                console.log(error, "error");
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    closeDownloadDocumentId() {
        this.downloadDocumentId = false;
        this.getKnowledgeBaseList("");
    }

    // closeDocumentPreview() {
    //     this.documentPreview = false;
    //     this.previewUrl = null;
    //     this.previewType = "";
    // }

    downloadDoc(fileName, knowledgeBaseId, uniquename) {
        this.knowledgeBaseManagementService.downloadFile(knowledgeBaseId, uniquename).subscribe(
            blob => {
                if (blob.status == 200) {
                    this.toastr.success(`Download Successfully!`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: "Download Successfully",
                    //     icon: "far fa-check-circle"
                    // });
                    importedSaveAs(blob.body, fileName);
                } else if (blob.status == 404) {
                    this.toastr.error(`File Not Found`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: "File Not Found",
                    //     icon: "far fa-times-circle"
                    // });
                } else if (blob.status == 204) {
                    this.toastr.info(`Can't Download, File is Remove From The Server Directory`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: "Can't Download, File is Remove From The Server Directory",
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.error(`Something went wrong!`, 'Info!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: "Something went wrong!",
                    //     icon: "far fa-times-circle"
                    // });
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}, 'Failed'`)
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    // deleteConfirm(knowledgeBaseId, uniqueName, fileName, fromFormEdit = false) {

    //     this.confirmationService.confirm({
    //         message: "Do you want to delete this file?",
    //         header: "Delete Confirmation",
    //         icon: "pi pi-info-circle",
    //         accept: () => {
    //             if (uniqueName) {
    //                 // If uniqueName exists, it's a server-side file
    //                 this.deleteDoc(knowledgeBaseId, uniqueName, fileName, fromFormEdit);
    //             } else {
    //                 this.deletSelectedFile(fileName); // Just remove from UI if it's a newly added file
    //             }
    //         },
    //         reject: () => {
    //             this.messageService.add({
    //                 severity: "info",
    //                 summary: "Rejected",
    //                 detail: "You have rejected"
    //             });
    //         }
    //     });
    // }

    deleteConfirm(knowledgeBaseId: any, uniqueName: string, fileName: string, fromFormEdit: boolean = false) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            disableClose: true,
            data: {
                title: "Delete Confirmation",
                description: `Do you want to delete the file "${fileName}"?`,
                yesLabel: "Confirm",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (uniqueName) {
                    // If uniqueName exists, it's a server-side file
                    this.deleteDoc(knowledgeBaseId, uniqueName, fileName, fromFormEdit);
                } else {
                    this.deletSelectedFile(fileName); // Just remove from UI if it's a newly added file
                }
            } else {
                this.toastr.info(`Delete operation was cancelled`, 'Info!')
                // this.messageService.add({
                //     severity: "info",
                //     summary: "Cancelled",
                //     detail: "Delete operation was cancelled",
                //     icon: "far fa-times-circle"
                // });
            }
        });
    }

    // Also update the deleteDoc method to handle updating both UIs after server deletion
    deleteDoc(knowledgeBaseId, uniqueName, fileName, fromFormEdit = false) {
        const urldoc = `/knowledgeBase/deleteSingleDoc/${knowledgeBaseId}?fileName=${fileName}&uniqueName=${uniqueName}`;
        this.knowledgeBaseManagementService.deleteMethod(urldoc).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-check-circle"
                    // });

                    // Update the selectedFilePreviewd array to remove the deleted file
                    if (fromFormEdit) {
                        this.selectedFilePreviewd = this.selectedFilePreviewd?.filter(
                            (file: any) => !(file?.name === fileName && file?.uniqueName === uniqueName)
                        );
                    }

                    if (this.knowledgeBaseFileDataSource && this.knowledgeBaseFileDataSource.data) {
                        const previousDataLength = this.knowledgeBaseFileDataSource.data.length;

                        this.knowledgeBaseFileDataSource.data = this.knowledgeBaseFileDataSource.data.filter(
                            (file: any) => !(file.filename === fileName && file.uniqueName === uniqueName)
                        );

                        const currentStartIndex = this.currentFilePage * this.pageSize;
                        if (currentStartIndex >= this.knowledgeBaseFileDataSource.data.length && this.currentFilePage > 0) {
                            this.currentFilePage = this.currentFilePage - 1;
                        }

                        this.updatePaginatedFileData();

                        if (this.filePaginator) {
                            this.filePaginator.length = this.knowledgeBaseFileDataSource.data.length;
                            this.filePaginator.pageIndex = this.currentFilePage;
                        }
                    }


                    // Update the knowledgeBaseFileData if it exists (for view document dialog)
                    if (this.knowledgeBaseFileData && this.knowledgeBaseFileData.fileDetails) {
                        this.knowledgeBaseFileData.fileDetails = this.knowledgeBaseFileData.fileDetails.filter(
                            (file: any) => !(file.filename === fileName && file.uniqueName === uniqueName)
                        );
                    }

                    // Only close the dialog if we're in the document preview dialog and not from form edit
                    if (this.downloadDocumentId && !fromFormEdit) {
                        // If file list is empty, close the dialog
                        if (!this.knowledgeBaseFileData.fileDetails.length) {
                            this.closeDownloadDocumentId();
                        }
                    }

                    // If deletion was from the edit form, also refresh the document view data
                    if (fromFormEdit && this.viewKnowledgeBaseListData) {
                        // Update the filename and uniqueName in the viewKnowledgeBaseListData
                        if (
                            this.viewKnowledgeBaseListData.filename &&
                            this.viewKnowledgeBaseListData.uniqueName
                        ) {
                            const filenames = this.viewKnowledgeBaseListData.filename
                                .split(",")
                                .map(f => f.trim());
                            const uniqueNames = this.viewKnowledgeBaseListData.uniqueName
                                .split(",")
                                .map(u => u.trim());

                            const fileIndex = filenames.indexOf(fileName);
                            if (fileIndex !== -1) {
                                filenames.splice(fileIndex, 1);
                                uniqueNames.splice(fileIndex, 1);

                                this.viewKnowledgeBaseListData.filename = filenames.join(", ");
                                this.viewKnowledgeBaseListData.uniqueName = uniqueNames.join(", ");
                            }
                        }
                    }
                } else if (response.responseCode == 404) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    showknowledgetDocData(fileName, knowledgeBaseId, uniqueName) {
        const extension = fileName.split(".").pop().toLowerCase();
        this.knowledgeBaseManagementService.downloadFile(knowledgeBaseId, uniqueName).subscribe(
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
                    this.openDocumentPreviewDialog();
                    this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
                    this.previewType = extension; // Add this line
                    this.documentPreview = true;
                } else if (data.status == 404) {
                    this.toastr.error(`File Not Found`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: "File Not Found",
                    //     icon: "far fa-times-circle"
                    // });
                } else if (data.status == 204) {
                    this.toastr.error(`File is Remove From The Server Directory`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: "File is Remove From The Server Directory",
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.toastr.error(`Something went wrong!`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: "Something went wrong!",
                    //     icon: "far fa-times-circle"
                    // });
                }
            },
            error => {
                console.log(error);
                this.toastr.error(`${error.error.ERROR}`, 'Failed')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    @ViewChild("knowledgeBaseDialog") knowledgeBaseDialog!: TemplateRef<any>;

    // openknowledgeBaseDialog(branchManagement: any) {
    //     this.dialogRef = this.dialog.open(this.knowledgeBaseDialog, {
    //         width: "800px",
    //         disableClose: true
    //     });
    //     this.dialogRef.afterClosed().subscribe(result => {
    //     });
    // }


    openknowledgeBaseDialog(knowledgebaseId?: any) {
        if (this.isDialogOpen) {
            return;
        }

        if (knowledgebaseId) {
            this.editKnowledgebase(knowledgebaseId);
        } else {
            this.clearFormData();
            this.isKnowledgebaseEdit = false;
        }

        this.isDialogOpen = true;

        this.dialogRef = this.dialog.open(this.knowledgeBaseDialog, {
            width: "800px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.isDialogOpen = false;
            this.clearFormData();
        });
    }

    clearFormData() {
        this.submitted = false;
        this.knowledgeBaseFormGroup.reset();
        this.selectedFilePreview = [];
        this.selectedFilePreviewd = [];
        this.multiFiles = null;
        this.viewKnowledgeBaseListData = null;

        // Clear file input
        if (this.fileInput && this.fileInput.nativeElement) {
            this.fileInput.nativeElement.value = "";
        }
    }


    @ViewChild("knowledgeBaseDialogContent") knowledgeBaseDialogContent!: TemplateRef<any>;
    @ViewChild("documentPreviewDialog") documentPreviewDialog!: TemplateRef<any>;



    openknowledgeBaseDialogData(knowledgebase: any) {
        if (knowledgebase?.id) {
            const url = "/knowledgeBase/getDocumentById/" + knowledgebase.id;

            this.knowledgeBaseManagementService.getMethod(url).subscribe(
                (response: any) => {
                    if (response.data) {
                        const filenameArray = response.data.filename ? response.data.filename.split(",") : [];
                        const uniquenameArray = response.data.uniqueName ? response.data.uniqueName.split(",") : [];

                        const fileDetails = filenameArray.map((filename: string, index: number) => ({
                            filename: filename.trim(),
                            uniqueName: uniquenameArray[index]?.trim() || "",
                            parentId: knowledgebase.id
                        }));

                        this.knowledgeBaseFileDataSource.data = fileDetails;

                        this.currentFilePage = 0;
                        this.pageSize = 5;
                        this.updatePaginatedFileData();

                        this.dialogRef = this.dialog.open(this.knowledgeBaseDialogContent, {
                            width: "800px",
                            disableClose: true
                        });

                        this.dialogRef.afterClosed().subscribe((result: any) => {
                            this.knowledgeBaseFileDataSource.data = [];
                            this.paginatedFileData = [];
                            this.currentFilePage = 0;
                            this.pageSize = 5;
                        });
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error?.ERROR || "Failed to load file data"}`, 'Failed')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error?.ERROR || "Failed to load file data",
                    //     icon: "far fa-times-circle"
                    // });
                }
            );
        }
    }

    updatePaginatedFileData() {
        const startIndex = this.currentFilePage * this.pageSize;
        const endIndex = startIndex + this.pageSize;
        this.paginatedFileData = this.knowledgeBaseFileDataSource.data.slice(startIndex, endIndex);
    }



    openDocumentPreviewDialog() {
        if (this.isDialogOpen) {
            return;
        }

        this.isDialogOpen = true;
        this.dialogRef = this.dialog.open(this.documentPreviewDialog, {
            width: "1000px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.isDialogOpen = false;
            // Clean up the blob URL when dialog closes
            if (this.previewUrl) {
                URL.revokeObjectURL(this.previewUrl);
            }
        });
    }

    closeDocumentPreview() {
        this.documentPreview = false;
        this.previewUrl = null;
        this.previewType = "";
        if (this.documentPreviewDialogRef) {
            this.documentPreviewDialogRef.close();
        }
        // Clean up the blob URL
        if (this.previewUrl) {
            URL.revokeObjectURL(this.previewUrl);
        }
    }




}
