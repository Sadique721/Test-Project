import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { Observable, Observer } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { TacacsDeviceGroupService } from "src/app/service/tacacs-device-group.service";
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-tacacs-staff",
    templateUrl: "./tacacs-staff.component.html",
    styleUrls: ["./tacacs-staff.component.css"],
    standalone: false
})
export class TacacsStaffComponent implements OnInit {
    title = "Tacacs";
    searchStaffName: any;
    staffFormGroup: UntypedFormGroup;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    staffitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    showItemPerPage: any;
    currentPageStaff = 1;
    tacacsData: any;
    staffList: any;
    stafftotalRecords: any;
    constructor(
        private spinner: NgxSpinnerService,
        private toastr: ToastrService,
        private messageService: MessageService,
        private TacasStaffService: TacacsDeviceGroupService,
        private fb: UntypedFormBuilder,
        private confirmationService: ConfirmationService
    ) { }

    ngOnInit(): void {
        this.staffFormGroup = this.fb.group({
            firstname: ["", Validators.required],
            lastname: ["", Validators.required],
            username: ["", Validators.required],
            email: ["", Validators.required],
            status: ["", Validators.required],
            id: [""],
        });
        this.getStaffListData("");
    }

    canExit() {
        if (!this.staffFormGroup.dirty) return true;
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

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageStaff > 1) {
            this.currentPageStaff = 1;
        }
        this.getStaffListData(this.showItemPerPage);
    }

    pageChangedCasList(pageNumber) {
        this.currentPageStaff = pageNumber;
        this.getStaffListData("");
    }

    getStaffListData(list) {
        const url = "/tacacs-staff-users/get-staff-users";
        let size;
        let pageList = this.currentPageStaff - 1;
        if (list) {
            size = list;
            this.staffitemsPerPage = list;
        } else {
            size = this.staffitemsPerPage;
        }
        let plandata = {
            page: pageList,
            pageSize: size,
        };
        this.TacasStaffService.getMethod(url, { params: plandata }).subscribe(
            (response: any) => {
                this.staffList = response.data.users.content;
                this.stafftotalRecords = response.data.users.totalElements;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }
}
