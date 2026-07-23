import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { ToastrService } from "ngx-toastr";
import { ConfirmationService, MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { ProfileService } from "src/app/service/profile.service";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Component({
    selector: "app-profile-list",
    templateUrl: "./profile-list.component.html",
    styleUrls: ["./profile-list.component.scss"],
    standalone: false
})
export class ProfileListComponent implements OnInit {
    profileTitle = RadiusConstants.PROFILE;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: any;
    profileListData: any;
    profileData: any;
    searchProfileName: any;
    searchData: any;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    mvnoIdData: any;
    mvnoNameList: any;
    mvnoOptions: any;
    mvnoMasterOptions: any[];
    //****************** */
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild('createProfileDialog') createProfileDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;

    constructor(
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private profileService: ProfileService,
        // 
        private dialog: MatDialog,
        private toastr: ToastrService
    ) { }

    async ngOnInit() {
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
        this.getProfileData("");
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }


    clearProfile() {
        this.searchProfileName = "";
        this.getProfileData("");
    }

    pageChangedMvnoList(event: PageEvent) {

        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;

        if (this.searchkey) {
            this.searchProfile();
        } else {
            this.getProfileData('');
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.getProfileData(this.showItemPerPage);
        } else {
            this.searchProfile();
        }
    }

    searchProfile(): void {
        if (!this.searchkey || this.searchkey != this.searchProfileName) {
            this.currentPage = 1;
        }
        this.searchkey = this.searchProfileName;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchProfileName
            ? this.searchProfileName.trim()
            : "";
        this.searchData.page = this.currentPage;
        this.searchData.pageSize = this.itemsPerPage;
        const url = "/custAccountProfile/search";
        this.profileService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode === 404 ||
                    !response.CustAccountProfile ||
                    response.CustAccountProfile.length === 0
                ) {
                    this.toastr.info(`${response.responseMessage} || "No records found`, 'Info!');
                    this.profileListData = [];
                    this.totalRecords = 0;
                } else {
                    this.profileListData = response.CustAccountProfile;
                    this.totalRecords = response.pageDetails.totalRecords;

                    this.dataSource = new MatTableDataSource<any>(this.profileListData);
                    if (this.paginator) {
                        this.dataSource.paginator = this.paginator;
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    getProfileData(list) {
        let size;
        this.searchkey = "";
        let pageList = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        const url = "/custAccountProfile/getAllWithPagination";
        let mvnodata = {
            page: pageList,
            pageSize: size
        };
        this.profileService.postMethod(url, mvnodata).subscribe(
            (response: any) => {
                this.profileListData = response.custAccountProfilesList;
                this.dataSource = new MatTableDataSource<any>(this.profileListData);
                this.totalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    deleteConfirmonProfile(profileData) {
        if (profileData) {

            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: `Delete ${this.profileTitle}`,
                    description: `Are you sure you want to delete "${profileData.name}"?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteProfile(profileData.id);
                } else {
                }
            });
            // this.confirmationService.confirm({
            //     message: "Do you want to delete this MVNO?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {

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

    deleteProfile(id) {
        const url = "/custAccountProfile/delete/" + id;
        this.profileService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && this.profileListData.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                this.toastr.success(`deleted Successfully`, 'Success!');
                if (this.searchkey) {
                    this.searchProfile();
                } else {
                    this.getProfileData("");
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    createClick(profileId: any) {
        if (!profileId) {
            this.dialogRef = this.dialog.open(this.createProfileDialog, {
                width: '1000px',
                data: {
                    isProfileEdit: false,
                    height: 'auto',
                    autoFocus: false,
                    disableClose: true
                }
            });
            this.dialogRef.afterClosed().subscribe(result => {
                this.dialogRef.close();
                this.getProfileData('');
            });
        } else {
            this.dialogRef = this.dialog.open(this.createProfileDialog, {
                width: '1000px',
                data: {
                    isProfileEdit: true,
                    height: 'auto',
                    autoFocus: false,
                    disableClose: true,
                    dataid: profileId
                }
            });
            this.dialogRef.afterClosed().subscribe(result => {
                this.dialogRef.close();
                this.getProfileData('');
            });
        }

    }

    closeDialog() {
        this.dialogRef.close();
    }
}
