import { Component, OnInit, TemplateRef } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
// Remove MessageService import since we're replacing it with toastr
// import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { Regex } from "src/app/constants/regex";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { PRODUCTS, SETTINGS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { FeedbackService } from "src/app/service/feedback.service";
import { status } from "./../../RadiusUtils/RadiusConstants";

import { ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { AfterViewInit } from '@angular/core';
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
// Add ToastrService import
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-feedback",
    templateUrl: "./feedback.component.html",
    styleUrls: ["./feedback.component.css"],
    standalone: false
})
export class FeedbackComponent implements OnInit, AfterViewInit {

    displayedColumns: string[] = [
        'id',
        'eventName',
        'channelType',
        'ratingDisplayType',
        'ratingScale',
        'mandatory',
        'status',
        'action'
    ];
    dataSource: MatTableDataSource<any>;

    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;

    isDialogOpen: boolean;
    totalRecords: number = 0;
    itemsPerPage: number = 5;
    pageSizeOptions: number[] = [5, 10, 20, 50, 100];
    currentPage: number = 0;
    dialogRef: any;
    charecter150 = "^.{0,150}$";
    submitted = false;
    isFeedbackEdit: boolean = false;
    listView: boolean = true;
    createView: boolean = false;
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    feedbackListData: any = [];
    editFeedbackId: any = [];
    dialogVisible: boolean;

    channelTypelist = [
        { label: "CWSC", value: "CWSC" },
        { label: "Mobile App", value: "MOBILE_APP" },
        { label: "Both", value: "BOTH" }
    ];
    ratingTypelist = [
        { label: "STAR", value: "STAR" },
        { label: "EMOJI", value: "EMOJI" },
        { label: "NUMERIC", value: "NUMERIC" }
    ];
    mandatorylist = [
        { label: "TRUE", value: "true" },
        { label: "FALSE", value: "false" }
    ];
    statuslist = [
        { label: "Active", value: "true" },
        { label: "Inactive", value: "false" }
    ];

    constructor(
        private fb: UntypedFormBuilder,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        // Replace MessageService with ToastrService
        private toastr: ToastrService,
        private feedbackService: FeedbackService,
        loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(SETTINGS.FEEDBACK_CONFIGURATION_CREATE);
        this.editAccess = loginService.hasPermission(SETTINGS.FEEDBACK_CONFIGURATION_EDIT);
        this.deleteAccess = loginService.hasPermission(SETTINGS.FEEDBACK_CONFIGURATION_DELETE);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    feedbackGroupForm: UntypedFormGroup;
    ngOnInit(): void {
        this.feedbackGroupForm = this.fb.group({
            event: ["", Validators.required],
            channel: ["CWSC", Validators.required],
            ratingScale: [5, [Validators.required, Validators.min(1)]],
            ratingDisplayType: ["STAR", Validators.required],
            isMandatory: ["true", Validators.required],
            isActive: ["true", Validators.required],
            feedBackMessage: ["", Validators.required]
        });
        this.dataSource = new MatTableDataSource();
        this.getFeedbackList();
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
    }

    createFeedback() {
        this.listView = false;
        this.createView = true;
        this.submitted = false;
        this.isFeedbackEdit = false;
        this.clearFeedbackForm();
    }

    onPageChange(event: any) {
        this.currentPage = event.pageIndex;
        this.itemsPerPage = event.pageSize;
        this.updatePaginatedData();
    }

    updatePaginatedData() {
        if (!this.feedbackListData) return;

        const startIndex = this.currentPage * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;
        const paginatedData = this.feedbackListData.slice(startIndex, endIndex);

        this.dataSource = new MatTableDataSource(paginatedData);
        this.dataSource.sort = this.sort;
    }

    listFeedback() {
        this.listView = true;
        this.createView = false;
        this.getFeedbackList();
    }

    getFeedbackList() {
        this.feedbackService.getAllFeedback().subscribe(
            (response: any) => {
                this.feedbackListData = response.FeedbackConfigList.reverse();
                this.totalRecords = this.feedbackListData.length;
                this.updatePaginatedData();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    addFeedback(id?: number) {
        this.submitted = true;

        if (this.feedbackGroupForm.valid) {
            const feedbackData = this.feedbackGroupForm.value;

            if (id) {
                this.isFeedbackEdit = true;
                this.feedbackService.updateFeedback(id, feedbackData).subscribe(
                    (res: any) => {
                        if (res.responseCode === 200) {
                            this.toastr.success(`${res.msg}`, 'Success!');
                            this.listView = true;
                            this.createView = false;
                            this.clearFeedbackForm();

                            if (this.dialogRef) {
                                this.dialogRef.close();
                            }

                            this.getFeedbackList();
                        } else {
                            this.toastr.info(`${res.msg}`, 'Info!');
                        }
                    },
                    (err: any) => {
                        this.toastr.error(`${err.error?.msg || "Something went wrong"}`, 'Failed!');
                    }
                );
            } else {
                // CREATE FLOW
                this.feedbackService.createFeedback(feedbackData).subscribe(
                    (res: any) => {
                        if (res.responseCode === 201) {
                            this.toastr.success(`${res.responseMessage}`, 'Success!');
                            this.listView = true;
                            this.createView = false;
                            this.submitted = false;
                            this.clearFeedbackForm();

                            if (this.dialogRef) {
                                this.dialogRef.close();
                            }

                            this.getFeedbackList();
                        } else {
                            this.toastr.info(`${res.responseMessage}`, 'Info!');
                        }
                    },
                    (err: any) => {
                        this.toastr.error(`${err.error?.responseMessage || "Something went wrong"}`, 'Failed!');
                    }
                );
            }
        }
    }

    clearFeedbackForm() {
        this.feedbackGroupForm.reset({
            event: "",
            feedBackMessage: "",
            isMandatory: "true",
            isActive: "true",
            channel: "CWSC",
            ratingScale: 5,
            ratingDisplayType: "STAR"
        });

        this.submitted = false;
    }

    editFeedback(id: number) {
        this.listView = false;
        this.createView = true;
        this.isFeedbackEdit = true;
        this.openFeedbackConfigDialog(id);
        this.feedbackService.getFeedbackById(id).subscribe(
            (response: any) => {
                const feedbackData = response.data;

                this.feedbackGroupForm.patchValue({
                    event: feedbackData.event,
                    channel: feedbackData.channel,
                    feedBackMessage: feedbackData.feedBackMessage,
                    ratingScale: feedbackData.ratingScale,
                    ratingDisplayType: feedbackData.ratingDisplayType,
                    isMandatory: String(feedbackData.isMandatory),
                    isActive: String(feedbackData.isActive)
                });

                this.editFeedbackId = id;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    deleteFeedback(feed: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            disableClose: true,
            data: {
                title: "Delete Confirmation",
                description: `Are you sure you want to delete "${feed.event}"?`,
                yesLabel: "Confirm",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.feedbackService.deleteFeedback(feed.id).subscribe(
                    (response: any) => {
                        this.toastr.success(`${response.msg || "Feedback deleted successfully"}`, 'Success!');
                        this.getFeedbackList(); // refresh the list after deletion
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error?.ERROR || "An error occurred"}`, 'Failed!');
                    }
                );
            } else {
                this.toastr.info(`Delete operation was cancelled`, 'Info!');
            }
        });
    }

    // deleteFeedback(id: number) {
    //     this.confirmationService.confirm({
    //         message: "Are you sure you want to delete this feedback?",
    //         header: "Confirmation",
    //         icon: "pi pi-exclamation-triangle",
    //         accept: () => {
    //             this.feedbackService.deleteFeedback(id).subscribe(
    //                 (response: any) => {
    //                     this.toastr.success(`${response.msg || "Feedback deleted successfully"}`, 'Success!');

    //                     this.getFeedbackList(); // refresh the list after deletion
    //                 },
    //                 (error: any) => {
    //                     this.toastr.error(`${error.error?.ERROR || "An error occurred"}`, 'Failed!');
    //                 }
    //             );
    //         }
    //     });
    // }

    getRefresh() {
        this.currentPage = 0;
        this.getFeedbackList();
    }

    @ViewChild("FeedbackConfigDialog") FeedbackConfigDialog!: TemplateRef<any>;

    openFeedbackConfigDialog(feedbackId?: number): void {
        if (this.isDialogOpen) {
            return;
        }

        this.isDialogOpen = true;
        this.isFeedbackEdit = !!feedbackId;
        this.editFeedbackId = feedbackId;

        this.clearFeedbackForm();
        this.submitted = false;

        this.dialogRef = this.dialog.open(this.FeedbackConfigDialog, {
            width: "900px",
            disableClose: true
        });

        if (feedbackId) {
            this.loadFeedbackData(feedbackId);
        }
        this.dialogRef.afterClosed().subscribe(result => {
            this.isDialogOpen = false;
            this.submitted = false;
            this.clearFeedbackForm();
        });
    }

    private loadFeedbackData(id: number): void {
        this.feedbackService.getFeedbackById(id).subscribe(
            (response: any) => {
                const feedbackData = response.data;
                this.feedbackGroupForm.patchValue({
                    event: feedbackData.event,
                    channel: feedbackData.channel,
                    feedBackMessage: feedbackData.feedBackMessage,
                    ratingScale: feedbackData.ratingScale,
                    ratingDisplayType: feedbackData.ratingDisplayType,
                    isMandatory: String(feedbackData.isMandatory),
                    isActive: String(feedbackData.isActive)
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                if (this.dialogRef) {
                    this.dialogRef.close();
                }
            }
        );
    }
}
