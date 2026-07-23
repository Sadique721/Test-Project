// import { DatePipe } from "@angular/common";
// import { Component, OnInit } from "@angular/core";
// import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
// import { ConfirmationService, MessageService } from "primeng/api";
// import { Observable, Observer } from "rxjs";
// import { SCHEDULARMANAGEMENT } from "src/app/RadiusUtils/RadiusConstants";
// import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
// import { SchedularManagementService } from "src/app/service/schedular-management.service";

// @Component({
//     selector: "app-schedular-management",
//     templateUrl: "./schedular-management.component.html",
//     styleUrls: ["./schedular-management.component.css"],
//     standalone: false
// })
// export class SchedularManagementComponent implements OnInit {
//   schedularForm: UntypedFormGroup;
//   title = SCHEDULARMANAGEMENT;
//   detailView: boolean = false;
//   listView: boolean = true;
//   createView: boolean = false;
//   currentPage = 1;
//   searchkey: any = [];
//   showItemPerPage = 1;
//   itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   schedularList: any;
//   totalRecords: any;
//   searchData: any;
//   first = 0;
//   isSchedularEdit: boolean = false;
//   submitted: boolean = false;
//   scheduleTypeList: any = [
//     {
//       label: "DAILY",
//       value: "DAILY"
//     },
//     {
//       label: "WEEKLY",
//       value: "WEEKLY"
//     },
//     {
//       label: "MONTHLY",
//       value: "MONTHLY"
//     }
//   ];
//   scheduleNameList: any = [
//     {
//       label: "Auto_Invoice_Export",
//       value: "Auto_Invoice_Export"
//     },
//     {
//       label: "Auto_Invoice_Distribution",
//       value: "Auto_Invoice_Distribution"
//     }
//   ];
//   dayOfMonthOptions: any[] = [];
//   viewSchedularDate: any;
//   status = [{ label: "Active" }, { label: "Inactive" }];
//   schedularId: any;
//   mvnoId: string;
//   search: any;

//   constructor(
//     private fb: UntypedFormBuilder,
//     private confirmationService: ConfirmationService,
//     private messageService: MessageService,
//     private service: SchedularManagementService,
//     private datePipe: DatePipe
//   ) {}

//   ngOnInit() {
//     this.mvnoId = localStorage.getItem("mvnoId");
//     this.schedularForm = this.fb.group({
//       id: [""],
//       schedulerName: ["", Validators.required],
//       schedulerTime: ["", Validators.required],
//       scheduleType: ["", Validators.required],
//       status: ["", Validators.required],
//       weekly: [null],
//       dayOfMonth: [""],
//       mvnoId: [this.mvnoId]
//     });

//     this.searchData = {
//       filters: [
//         {
//           filterDataType: "",
//           filterValue: "",
//           filterColumn: "any",
//           filterOperator: "equalto",
//           filterCondition: "and"
//         }
//       ],
//       page: "",
//       pageSize: ""
//     };
//     this.dayOfMonthOptions = Array.from({ length: 31 }, (_, i) => ({
//       label: `${i + 1}`,
//       value: i + 1
//     }));
//     this.schedularForm.get("scheduleType")?.valueChanges.subscribe(type => {
//       this.onScheduleTypeChange(type);
//     });
//   }

//   canExit() {
//     if (!this.schedularForm.dirty) return true;
//     {
//       return Observable.create((observer: Observer<boolean>) => {
//         this.confirmationService.confirm({
//           header: "Alert",
//           message: "The filled data will be lost. Do you want to continue? (Yes/No)",
//           icon: "pi pi-info-circle",
//           accept: () => {
//             observer.next(true);
//             observer.complete();
//           },
//           reject: () => {
//             observer.next(false);
//             observer.complete();
//           }
//         });
//         return false;
//       });
//     }
//   }

//   createSchedular() {
//     this.listView = false;
//     this.createView = true;
//     this.detailView = false;
//     this.isSchedularEdit = false;
//     this.schedularForm.reset();
//     this.schedularForm.patchValue({ mvnoId: this.mvnoId });
//   }

//   searchSchedular() {
//     this.listView = true;
//     this.createView = false;
//     this.detailView = false;
//     this.isSchedularEdit = false;
//     this.schedularForm.reset();
//     this.schedularForm.patchValue({ mvnoId: this.mvnoId });
//   }

//   getAllSchedularList(list) {
//     let size;
//     this.searchkey = "";
//     if (list) {
//       size = list;
//       this.itemsPerPage = list;
//     } else {
//       size = this.itemsPerPage;
//     }
//     const url = "/schedulers/search";
//     this.searchData.page = this.currentPage;
//     this.searchData.pageSize = size;
//     if (this.search) {
//       this.searchData.filters[0].filterValue = this.search;
//     } else {
//       this.searchData.filters[0].filterValue = "";
//     }

//     this.service.postMethod(url, this.searchData).subscribe(
//       (response: any) => {
//         this.schedularList = response?.dataList;
//         this.totalRecords = response?.totalRecords;
//       },
//       (error: any) => {
//         // console.log(error, 'error')
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }
//   loadSchedulers(event: any) {
//     if (this.itemsPerPage !== event.rows) {
//       this.itemsPerPage = event.rows;
//       this.currentPage = 1;
//       this.first = 0;
//     } else {
//       this.itemsPerPage = event.rows;
//       this.currentPage = Math.floor(event.first / this.itemsPerPage) + 1;
//       this.first = event.first;
//     }
//     // this.isSearchActive
//     //   ? this.searchDatabasebyName(this.currentPage)
//     //   : this.getDatabaseWithPagination();
//     this.getAllSchedularList("");
//   }

//   onScheduleTypeChange(type: string): void {
//     this.submitted = false;
//     this.schedularForm.patchValue({ weekly: null, dayOfMonth: "" });

//     if (type === "WEEKLY") {
//       this.schedularForm.get("weekly")?.setValidators([Validators.required]);
//       this.schedularForm.get("dayOfMonth")?.clearValidators();
//     } else if (type === "MONTHLY") {
//       this.schedularForm.get("dayOfMonth")?.setValidators([Validators.required]);
//       this.schedularForm.get("weekly")?.clearValidators();
//     } else {
//       this.schedularForm.get("weekly")?.clearValidators();
//       this.schedularForm.get("dayOfMonth")?.clearValidators();
//     }

//     this.schedularForm.get("weekly")?.updateValueAndValidity();
//     this.schedularForm.get("dayOfMonth")?.updateValueAndValidity();
//   }

//   addOrUpdateScheduler() {
//     this.submitted = true;
//     if (this.schedularForm.invalid) {
//       return;
//     }

//     const formData = { ...this.schedularForm.value };

//     const isUpdate = this.isSchedularEdit;
//     if (typeof formData.schedulerTime === "object") {
//       formData.schedulerTime = this.formatSchedularForm(formData.schedulerTime);
//     }

//     const url = isUpdate ? `/schedulers/update/${this.schedularId}` : "/schedulers/save";
//     const method = isUpdate ? this.service.updateMethod : this.service.postMethod;

//     method.call(this.service, url, formData).subscribe(
//       (response: any) => {
//         console.log(response);
//         if (response.responseCode == 417) {
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: "Schedular Name already exists",
//             icon: "pi pi-times-circle"
//           });
//         } else {
//           this.messageService.add({
//             severity: "success",
//             summary: "Success",
//             detail: isUpdate ? "Scheduler updated successfully" : "Scheduler created successfully",
//             icon: "pi pi-check-circle"
//           });
//           this.schedularForm.reset();
//           this.schedularForm.patchValue({ mvnoId: this.mvnoId });
//           this.submitted = false;
//           this.createView = false;
//           this.listView = true;
//           // this.getAllSchedularList("");
//           this.isSchedularEdit = false;
//         }
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error?.ERROR || "Operation failed",
//           icon: "pi pi-times-circle"
//         });
//       }
//     );
//   }

//   editSchedular(id) {
//     this.isSchedularEdit = true;
//     this.createView = true;
//     this.listView = false;
//     if (id) {
//       const url = "/schedulers/getScheduler/" + id;
//       this.service.getMethod(url).subscribe(
//         (response: any) => {
//           this.viewSchedularDate = response.data;
//           this.schedularId = response?.data?.id;
//           this.schedularForm.patchValue(this.viewSchedularDate);
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle"
//           });
//         }
//       );
//     }
//   }

//   deleteSchedularConfirmation(scheduler) {
//     if (scheduler) {
//       this.confirmationService.confirm({
//         message: "Do you want to delete this " + this.title + "?",
//         header: "Delete Confirmation",
//         icon: "pi pi-info-circle",
//         accept: () => {
//           this.deleteSchedular(scheduler);
//         },
//         reject: () => {
//           this.messageService.add({
//             severity: "info",
//             summary: "Rejected",
//             detail: "You have rejected"
//           });
//         }
//       });
//     }
//   }
//   clearsearch() {
//     // this.knowledgebaseName = "";
//     this.searchkey = "";
//     this.search = "";
//     // if (this.searchkey) {
//     //   this.searchKnowledgeBase();
//     // } else {
//     //   this.getKnowledgeBaseList("");
//     // }
//     this.getAllSchedularList("");
//     this.submitted = false;
//     this.isSchedularEdit = false;
//     this.schedularForm.reset();
//     this.schedularForm.patchValue({ mvnoId: this.mvnoId });
//   }

//   deleteSchedular(scheduler) {
//     const url = "/schedulers/delete/" + scheduler?.id;
//     this.service.deleteMethod(url).subscribe(
//       (response: any) => {
//         if (this.currentPage != 1 && this.schedularList.length == 1) {
//           this.currentPage = this.currentPage - 1;
//         }
//         if (
//           response.responseCode == 405 ||
//           response.responseCode == 406 ||
//           response.responseCode == 417
//         ) {
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: response.responseMessage,
//             icon: "far fa-times-circle"
//           });
//         } else {
//           this.messageService.add({
//             severity: "success",
//             summary: "Successfully",
//             detail: response.responseMessage,
//             icon: "far fa-check-circle"
//           });
//         }
//         this.clearsearch();
//         // if (this.searchkey) {
//         //   this.searchKnowledgeBase();
//         // } else {
//         //   this.getKnowledgeBaseList("");
//         // }
//       },
//       (error: any) => {
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.responseMessage,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   formatSchedularForm(date: Date): string {
//     if (!date || isNaN(date.getTime())) return "";
//     const timePart = this.datePipe.transform(date, "HH:mm");
//     return timePart || "";
//   }
// }

import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild, AfterViewInit, TemplateRef } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Observable, Observer } from 'rxjs';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';
import { SchedularManagementService } from 'src/app/service/schedular-management.service';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: 'app-schedular-management',
    templateUrl: './schedular-management.component.html',
    styleUrls: ['./schedular-management.component.css'],
    standalone: false
})
export class SchedularManagementComponent implements OnInit, AfterViewInit {
    title = RadiusConstants.SCHEDULARMANAGEMENT;

    schedularForm: UntypedFormGroup;
    submitted = false;

    schedularList: any[] = [];
    dataSource = new MatTableDataSource<any>();
    displayedColumns: string[] = ['id', 'schedulerName', 'schedulerTime', 'scheduleType', 'status', 'weekly', 'dayOfMonth', 'action'];
    totalRecords = 0;
    @ViewChild('createEditDialog') createEditDialog: TemplateRef<any>;

    listView = true;
    createView = false;
    isSchedularEdit = false;
    searchData: any;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    search: string = '';
    schedularId: any;
    mvnoId: string;

    scheduleTypeList = [
        { label: 'DAILY', value: 'DAILY' },
        { label: 'WEEKLY', value: 'WEEKLY' },
        { label: 'MONTHLY', value: 'MONTHLY' }
    ];

    scheduleNameList = [
        { label: 'Auto_Invoice_Export', value: 'Auto_Invoice_Export' },
        { label: 'Auto_Invoice_Distribution', value: 'Auto_Invoice_Distribution' }
    ];

    dayOfMonthOptions = Array.from({ length: 31 }, (_, i) => i + 1);

    status = [
        { label: 'Active', value: 'Active' },
        { label: 'Inactive', value: 'Inactive' }
    ];

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    dialogRef: any;

    constructor(
        private fb: UntypedFormBuilder,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private service: SchedularManagementService,
        private datePipe: DatePipe, private toastr: ToastrService,
        private dialog: MatDialog
    ) { }

    ngOnInit(): void {
        this.mvnoId = localStorage.getItem('mvnoId') || '';

        this.initForm();

        // Initialize searchData for filters and pagination
        this.searchData = {
            filters: [{
                filterDataType: '',
                filterValue: '',
                filterColumn: 'any',
                filterOperator: 'equalto',
                filterCondition: 'and'
            }],
            page: this.currentPage,
            pageSize: this.itemsPerPage
        };

        this.loadSchedulers();

        // scheduleType change validator logic
        this.schedularForm.get('scheduleType')?.valueChanges.subscribe(type => {
            this.onScheduleTypeChange(type);
        });
    }


    searchSchedular() {
        this.currentPage = 1;  // reset page on new search
        this.listView = true;
        this.createView = false;
        this.isSchedularEdit = false;
        this.schedularForm.reset();
        this.schedularForm.patchValue({ mvnoId: this.mvnoId });
        this.getAllSchedularList(this.itemsPerPage);
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    getAllSchedularList(list?: number): void {
        let size = list || this.itemsPerPage;
        this.itemsPerPage = size;

        this.search = this.search?.trim() || '';

        const url = "/schedulers/search";
        this.searchData.page = this.currentPage;
        this.searchData.pageSize = size;
        this.searchData.filters[0].filterValue = this.search;

        this.service.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.schedularList = response?.dataList ?? [];
                this.totalRecords = response?.totalRecords ?? 0;

                // Update dataSource so table reflects empty data and not stale data
                this.dataSource.data = this.schedularList;

                // Update paginator length, reset pageIndex if necessary
                if (this.paginator) {
                    this.paginator.length = this.totalRecords;
                    if (this.totalRecords === 0) {
                        this.paginator.pageIndex = 0;
                        this.currentPage = 1;
                    }
                }
            },
            (error: any) => {
                this.dataSource.data = []; // clear table on error
                this.totalRecords = 0;
                if (this.paginator) {
                    this.paginator.length = 0;
                }
                this.toastr.error(`${error.error.ERROR}`, ' "Failed to load data"');
            }
        );
    }


    initForm(): void {
        this.schedularForm = this.fb.group({
            id: [''],
            schedulerName: ['', Validators.required],
            schedulerTime: ['', Validators.required],
            scheduleType: ['', Validators.required],
            status: ['', Validators.required],
            weekly: [null],
            dayOfMonth: [''],
            mvnoId: [this.mvnoId]
        });
    }

    onScheduleTypeChange(type: string): void {
        this.submitted = false;
        const weeklyCtrl = this.schedularForm.get('weekly');
        const dayOfMonthCtrl = this.schedularForm.get('dayOfMonth');

        weeklyCtrl?.clearValidators();
        dayOfMonthCtrl?.clearValidators();

        if (type === 'WEEKLY') {
            weeklyCtrl?.setValidators([Validators.required]);
        } else if (type === 'MONTHLY') {
            dayOfMonthCtrl?.setValidators([Validators.required]);
        }

        weeklyCtrl?.updateValueAndValidity();
        dayOfMonthCtrl?.updateValueAndValidity();
    }

    loadSchedulers(event?: PageEvent): void {
        if (event) {
            this.itemsPerPage = event.pageSize;
            this.currentPage = event.pageIndex + 1;
        }
        this.getAllSchedularList(this.itemsPerPage);
    }

    clearSearch(): void {
        this.search = '';
        this.submitted = false;
        this.isSchedularEdit = false;
        this.schedularForm.reset({ mvnoId: this.mvnoId });
        this.loadSchedulers();
    }

    createSchedular(): void {
        this.listView = false;
        this.createView = true;
        this.isSchedularEdit = false;
        this.schedularForm.reset({ mvnoId: this.mvnoId });
    }

    addEditSchedulDialog() {
        this.isSchedularEdit = false;
        this.schedularForm.reset({ mvnoId: this.mvnoId });
        this.dialogRef = this.dialog.open(this.createEditDialog, {
            width: '800px',
            data: {
                isEdit: false,
                title: 'Create ' + this.title,
            }
        });

        this.dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.schedularForm = this.fb.group({
                    schedulerName: result.schedulerName,
                    schedulerTime: result.schedulerTime,
                    scheduleType: result.scheduleType,
                    status: result.status,
                    weekly: result.weekly,
                    dayOfMonth: result.dayOfMonth,
                    mvnoId: this.mvnoId,
                });
                this.addOrUpdateScheduler();
            }
        });
    }

    onCancel(): void {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    viewSchedularDate: any
    editSchedular(id) {
        this.isSchedularEdit = true;
        this.createView = true;
        this.listView = true;
        if (id) {
            const url = "/schedulers/getScheduler/" + id;
            this.service.getMethod(url).subscribe(
                (response: any) => {
                    this.viewSchedularDate = response.data;
                    this.schedularId = response?.data?.id;
                    this.schedularForm.patchValue(this.viewSchedularDate);
                    const timeValue = response.data.schedulerTime;
                    const [h, m] = timeValue.split(':');
                    const date = new Date();
                    date.setHours(+h, +m, 0, 0);
                    this.schedularForm.patchValue({ schedulerTime: date });
                    this.dialogRef = this.dialog.open(this.createEditDialog, {
                        width: '800px',
                        data: { isEdit: true, title: 'Update ' + this.title }
                    });
                },
                (error: any) => {

                    this.toastr.error(`${error.error.ERROR}`, 'error');
                }
            );
        }
    }

    addOrUpdateScheduler(): void {
        this.submitted = true;
        this.markFormGroupTouched(this.schedularForm);
        if (this.schedularForm.invalid) return;
        let formData = { ...this.schedularForm.value };

        if (typeof formData.schedulerTime === 'object') {
            formData.schedulerTime = this.formatSchedularForm(formData.schedulerTime);
        }
        const url = this.isSchedularEdit ? `/schedulers/update/${this.schedularId}` : '/schedulers/save';
        const method = this.isSchedularEdit ? this.service.updateMethod : this.service.postMethod;

        method.call(this.service, url, formData).subscribe(
            (response: any) => {
                if (response.responseCode === 417) {
                    this.toastr.info(response.responseMessage, 'Scheduler Name already exists');
                } else {
                    this.toastr.success(this.isSchedularEdit ? 'Scheduler updated successfully' : 'Scheduler created successfully', "Success!");
                    this.createView = false;
                    this.listView = true;
                    this.isSchedularEdit = false;
                    this.submitted = false;
                    this.schedularForm.reset({ mvnoId: this.mvnoId });
                    this.loadSchedulers();
                    this.dialogRef.close();
                }
            },
            error => {

                this.toastr.error(`${error.error.ERROR}`, 'Operation failed');
            }
        );
    }

    private markFormGroupTouched(formGroup: UntypedFormGroup) {
        Object.keys(formGroup.controls).forEach(field => {
            const control = formGroup.get(field);
            if (control instanceof UntypedFormGroup) {
                this.markFormGroupTouched(control);
            } else {
                control?.markAsTouched({ onlySelf: true });
            }
        });
    }
    deleteConfirmonSchedularDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete ' + this.title,
                description: `Are you sure you want to delete "${item.schedulerName}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteSchedular(item);
            }
        });
    }

    deleteSchedular(scheduler: any): void {
        const url = '/schedulers/delete/' + scheduler?.id;
        this.service.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && this.schedularList.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                if ([405, 406, 417].includes(response.responseCode)) {
                    this.toastr.info(response.responseMessage, 'Info!');
                } else {
                    this.toastr.success(`${response.responseMessage}`, "Successfully ");
                }
                this.clearSearch();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Delete failed');
            }
        );
    }



    formatSchedularForm(dateInput: any): string {
        if (!dateInput) return '';

        let dateObj: Date;

        if (dateInput instanceof Date) {
            dateObj = dateInput;
        } else if (typeof dateInput === 'string') {
            // Try parse string "HH:mm" to Date with today’s date
            const parts = dateInput.split(':');
            if (parts.length === 2) {
                dateObj = new Date();
                dateObj.setHours(+parts[0], +parts[1], 0, 0);
            } else {
                return '';
            }
        } else if (dateInput._isAMomentObject) {
            // It’s a Moment.js object
            dateObj = dateInput.toDate();
        } else {
            return '';
        }

        if (isNaN(dateObj.getTime())) return '';

        return this.datePipe.transform(dateObj, 'HH:mm') || '';
    }



    canExit(): Observable<boolean> | boolean {
        if (!this.schedularForm.dirty) return true;
        return new Observable((observer: Observer<boolean>) => {
            this.confirmationService.confirm({
                header: 'Alert',
                message: 'The filled data will be lost. Do you want to continue? (Yes/No)',
                icon: 'pi pi-info-circle',
                accept: () => {
                    observer.next(true);
                    observer.complete();
                },
                reject: () => {
                    observer.next(false);
                    observer.complete();
                }
            });
        });
    }
}
