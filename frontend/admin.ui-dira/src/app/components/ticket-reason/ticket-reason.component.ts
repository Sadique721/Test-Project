import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, UntypedFormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { TicketReasonService } from "src/app/service/ticket-reason.service";
import { Regex } from "src/app/constants/regex";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { ServiceAreaService } from "src/app/service/service-area.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-ticket-reason",
    templateUrl: "./ticket-reason.component.html",
    styleUrls: ["./ticket-reason.component.css"],
    standalone: false
})
export class TicketReasonComponent implements OnInit {
    ticketReasonGroupForm: UntypedFormGroup;
    caseReasonConfigArray: UntypedFormArray;
    resolutionReasonData: any;
    serviceAreaData: any;
    staffUserData: any;
    currentPageCaseReasonConfig = 1;
    caseReasonConfigitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    caseReasonConfigtotalRecords: String;
    submitted: boolean = false;
    createCaseReasonData: any;
    tatConsiderationData: any;
    ticketReasonData: any;
    currentPageTicketReasonConfig = 1;
    ticketReasonConfigitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    ticketReasonConfigtotalRecords: any;
    isTicketReasonEdit: boolean = false;
    viewTicketReasonData: any;
    deletedata: any = {
        id: "",
        cityId: "",
        cityName: "",
        code: "",
        countryId: "",
        countryName: "",
        name: "",
        pincodeId: "",
        stateId: "",
        stateName: "",
        status: "",
        timeUnit: "",
        time: "",
    };

    statusOptions = RadiusConstants.status;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    totalAreaListLength = 0;
    qutaUnitTime = [
        { label: "Minute", value: "MIN" },
        { label: "Hour", value: "HOUR" },
        { label: "Day", value: "DAY" },
    ];
    constructor(
        private toastr: ToastrService,

        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private ticketReasonService: TicketReasonService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private commondropdownService: CommondropdownService,
        private serviceAreaService: ServiceAreaService
    ) { }

    ngOnInit(): void {
        this.ticketReasonGroupForm = this.fb.group({
            name: ["", Validators.required],
            status: ["", Validators.required],
            // reasonId: ["", Validators.required],
            tatConsideration: ["", Validators.required],
            timeUnit: ["", Validators.required],
            time: ["", [Validators.required, Validators.pattern(Regex.numeric)]],
        });
        this.caseReasonConfigArray = this.fb.array([]);
        this.getResolutionReasons();
        this.getServiceArea();
        this.getStaffUser();
        this.onAddaseReasonConfigField();
        this.getTatConsideration();
        this.getTicketReason("");
    }

    createCaseReasonConfigFormGroup(): UntypedFormGroup {
        return this.fb.group({
            caseReasonName: ["", [Validators.required]],
            // reasonid: ['', [Validators.required]],
            serviceareaid: ["", [Validators.required]],
            staffid: ["", [Validators.required]],
            id: [""],
            // timeUnit: ["", [Validators.required]],
            // time: ["", [Validators.required]],
        });
    }

    onAddaseReasonConfigField() {
        this.caseReasonConfigArray.push(this.createCaseReasonConfigFormGroup());
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageTicketReasonConfig > 1) {
            this.currentPageTicketReasonConfig = 1;
        }
        if (!this.searchkey) {
            this.getTicketReason(this.showItemPerPage);
        }
    }

    getTicketReason(list) {
        let size;
        this.searchkey = "";
        let page_list = this.currentPageTicketReasonConfig;
        if (list) {
            size = list;
            this.ticketReasonConfigitemsPerPage = list;
        } else {
            // if (this.showItemPerPage == 0) {
            //   this.ticketReasonConfigitemsPerPage = this.pageITEM
            // } else {
            //   this.ticketReasonConfigitemsPerPage = this.showItemPerPage
            // }
            size = this.ticketReasonConfigitemsPerPage;
        }

        const url = "/caseReason";
        let ticketreasondata = {
            page: page_list,
            pageSize: size,
        };
        this.ticketReasonService.postMethod(url, ticketreasondata).subscribe(
            (response: any) => {
                this.ticketReasonData = response.dataList;
                this.ticketReasonConfigtotalRecords = response.totalRecords;

                // if (this.showItemPerPage > this.ticketReasonConfigitemsPerPage) {
                //   this.totalAreaListLength =
                //     this.ticketReasonData.length % this.showItemPerPage
                // } else {
                //   this.totalAreaListLength =
                //     this.ticketReasonData.length % this.ticketReasonConfigitemsPerPage
                // }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getTatConsideration() {
        const url = "/commonList/TATConsideration";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.tatConsiderationData = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getResolutionReasons() {
        const url = "/resolutionReasons/all";
        this.ticketReasonService.getMethod(url).subscribe(
            (response: any) => {
                this.resolutionReasonData = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getServiceArea() {
        const url = "/serviceArea/all";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.serviceAreaData = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getStaffUser() {
        const url = "/staffuser/allActive";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.staffUserData = response.staffUserlist;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    addEditTicketReason(ticketReasonId) {
        this.submitted = true;
        if (this.ticketReasonGroupForm.valid) {
            if (ticketReasonId) {
                const url = "/caseReason/update";
                this.createCaseReasonData = this.ticketReasonGroupForm.value;
                // this.createCaseReasonData.caseReasonConfigList = this.caseReasonConfigArray.value;
                // this.createCaseReasonData.caseReasonConfigList[0].reasonid = "1";
                this.createCaseReasonData.reasonId = ticketReasonId;
                //return
                this.ticketReasonService.postMethod(url, this.createCaseReasonData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');


                        } else {
                            this.ticketReasonGroupForm.reset();
                            this.getTicketReason("");
                            this.isTicketReasonEdit = false;
                            this.toastr.success(`${response.message}`, 'Success!');


                            this.submitted = false;
                        }
                    },
                    (error: any) => {
                        console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            } else {
                const url = "/caseReason/save";
                this.createCaseReasonData = this.ticketReasonGroupForm.value;
                // this.createCaseReasonData.caseReasonConfigList = this.caseReasonConfigArray.value;
                // this.createCaseReasonData.caseReasonConfigList[0].reasonid = "1";
                delete this.createCaseReasonData.reasonId;
                //return
                this.ticketReasonService.postMethod(url, this.createCaseReasonData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.success(`${response.responseMessage}`, 'Success!');


                        } else {
                            this.ticketReasonGroupForm.reset();
                            this.getTicketReason("");
                            this.toastr.success(`${response.message}`, 'Success!');


                            this.submitted = false;
                        }
                    },
                    (error: any) => {
                        console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            }
        }
    }

    editTicketReason(ticketReasonId) {
        if (ticketReasonId) {
            this.isTicketReasonEdit = true;
            // this.getTicketReasonById(ticketReasonId);
            // setTimeout(() => {
            //   this.ticketReasonGroupForm.patchValue(this.viewTicketReasonData);
            //
            // }, 1000)

            const url = "/caseReason/" + ticketReasonId;
            this.ticketReasonService.getMethod(url).subscribe(
                (response: any) => {
                    this.viewTicketReasonData = response.data;
                    this.ticketReasonGroupForm.patchValue(this.viewTicketReasonData);
                    this.deletedata = this.viewTicketReasonData;
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    getTicketReasonById(ticketReasonId) {
        const url = "/caseReason/" + ticketReasonId;
        this.ticketReasonService.getMethod(url).subscribe(
            (response: any) => {
                this.viewTicketReasonData = response.data;
                this.deletedata = this.viewTicketReasonData;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    deleteConfirmonTicketReason(ticketReasonId: number) {
        this.getTicketReasonById(ticketReasonId);
        if (ticketReasonId) {
            this.confirmationService.confirm({
                message: "Do you want to delete this Ticket Reason?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteTicketReason(ticketReasonId);
                },
                reject: () => {
                    (error: any) => {

                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');


                    }


                },
            });
        }
    }

    deleteTicketReason(ticketReasonId) {
        const url = "/caseReason/delete";
        //this.deletedata.pincodeId = pincodeId;
        this.ticketReasonService.postMethod(url, this.deletedata).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');


                } else {
                    if (this.currentPageTicketReasonConfig != 1 && this.totalAreaListLength == 1) {
                        this.currentPageTicketReasonConfig = this.currentPageTicketReasonConfig - 1;
                    }
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.getTicketReason("");
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.responseMessage}`, 'Failed!');


            }
        );
    }

    deleteConfirmonCaseReasonConfigField(
        caseReasonConfigFieldIndex: number,
        caseReasonConfigFieldId: number
    ) {
        if (caseReasonConfigFieldIndex || caseReasonConfigFieldIndex == 0) {
            this.confirmationService.confirm({
                message: "Do you want to delete this Case Reason Config?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.onRemoveCaseReasonConfig(caseReasonConfigFieldIndex, caseReasonConfigFieldId);
                },
                reject: () => {
                    (error: any) => {

                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');


                    }

                },
            });
        }
    }

    async onRemoveCaseReasonConfig(
        caseReasonConfigFieldIndex: number,
        caseReasonConfigFieldId: number
    ) {
        this.caseReasonConfigArray.removeAt(caseReasonConfigFieldIndex);
    }

    pageChangedCaseReasonConfig(pageNumber) {
        this.currentPageCaseReasonConfig = pageNumber;
    }

    pageChangedTicketReasonConfig(pageNumber) {
        this.currentPageTicketReasonConfig = pageNumber;
        this.getTicketReason("");
    }
}
