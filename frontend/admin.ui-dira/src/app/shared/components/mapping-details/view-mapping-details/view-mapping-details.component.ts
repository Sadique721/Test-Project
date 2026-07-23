
import { Component, Inject, Input, Output, EventEmitter, ViewChild, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UntypedFormControl } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { ITEMS_PER_PAGE } from 'src/app/RadiusUtils/RadiusConstants';
import { InwardService } from 'src/app/service/inward.service';
import { ToastrService } from 'ngx-toastr';
import saveAs from 'file-saver';
import { MatPaginator } from '@angular/material/paginator';

@Component({
    selector: 'app-view-mapping-details',
    templateUrl: './view-mapping-details.component.html',
    styleUrl: './view-mapping-details.component.css',
    standalone: false
})
export class ViewMappingDetailsComponent implements OnInit {
    @Input() mappingList: any[] = [];
    @Input() mappingListdatatotalRecords: number = 0;
    @Input() mappingListitemsPerPage: number = 20;
    @Input() macOptionSelect: any[] = [];
    @Input() searchOption: string = '';
    @Input() searchMappingDeatil: string = '';
    @Input() headerTitle: string = '';

    @Output() searchMacEvent = new EventEmitter<{ searchOption: string, searchMacDeatil: string }>();
    // @Output() clearMacEvent = new EventEmitter<void>();
    @Output() clearMacEvent = new EventEmitter<any>();
    @Output() paginateEvent = new EventEmitter<any>();
    @Output() inventoryDetailsEvent = new EventEmitter<number>();
    @Output() deleteShowMACMappingEvent = new EventEmitter<any>();

    dataSource = new MatTableDataSource<any>();

    searchOptionControl = new UntypedFormControl('');
    searchMacDeatilControl = new UntypedFormControl('');
    @Input() displayedMacShowColumns: any

    macAddMode: 'manual' | 'bulk' = 'manual';
    showSkippedTable: boolean = false;
    displayedMacShowColumns2: any[] = [];
    // displayedMacShowColumnsFields: string[] = [];
    inwardId: any
    inwardSkipCall: any;
    outwardIdForMac: any;
    currentPageSkipList = 1;
    skiptemsPerPage = ITEMS_PER_PAGE;
    getData = new MatTableDataSource<any>([]);
    skippedTotalRecords: any;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    constructor(
        private inwardService: InwardService,
        private toastr: ToastrService,
        public dialogRef: MatDialogRef<ViewMappingDetailsComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any
    ) {
        if (data) {
            this.mappingList = data.mappingList;
            // this.inwardId = data.mappingList[0]?.currentInwardId;
            this.inwardId = data?.inwardId || '';
            this.outwardIdForMac = data?.outwardIdForMac;
            this.mappingListdatatotalRecords = data.mappingListdatatotalRecords || 0;
            this.mappingListitemsPerPage = data.mappingListitemsPerPage || 20;
            this.macOptionSelect = data.macOptionSelect;
            this.searchOption = data.searchOption || '';
            this.searchMappingDeatil = data.searchMappingDeatil || '';
            this.headerTitle = data.headerTitle || '';
            this.inwardSkipCall = data.headerTitle.split(' ')[0];
            this.displayedMacShowColumns = data?.displayedMacShowColumns || []

            // this.displayedMacShowColumns2 = data?.displayedMacShowColumns || []
            this.displayedMacShowColumns2 =
                // this.displayedMacShowColumns.filter(c => c !== 'action');
                // this.displayedMacShowColumns.filter(
                //     c => !['action', 'assetId', 'condition', 'id'].includes(c)
                // );
                this.displayedMacShowColumns2 =
                (this.displayedMacShowColumns || []).filter(
                    c => !['action', 'assetId', 'condition', 'id'].includes(c)
                ).map(c => {
                    if (c === 'macAddress') return 'mac';
                    if (c === 'serialNumber') return 'serial';
                    return c;
                });
            if (!this.displayedMacShowColumns2.includes('reason')) {
                this.displayedMacShowColumns2.push('Reason');
            }
        }

        this.dataSource.data = this.mappingList;

        this.searchOptionControl.setValue(this.searchOption);
        this.searchMacDeatilControl.setValue(this.searchMappingDeatil);
    }
    ngOnInit(): void {
        if (this.inwardSkipCall === 'Inward' && this.dataSource.data.length) {
            this.getSkipRecord(this.inwardId)
        }
        if (this.inwardSkipCall === 'Outward' && this.dataSource.data.length) {
            this.getSkipRecord(this.outwardIdForMac)
        }
    }

    trackByFn(index: number, opt: any): any {
        return opt.value;
    }
    pageChangedSkipList(event: any) {
        this.currentPageSkipList = event.pageIndex + 1;  // pageIndex starts from 0
        this.skiptemsPerPage = event.pageSize;
        this.getSkipRecord(this.inwardId);   // common API calling method
    }

    onSearchMac() {
        this.searchMacEvent.emit({
            searchOption: this.searchOptionControl.value,
            searchMacDeatil: this.searchMacDeatilControl.value
        });
    }
    getSkipRecord(inwardId: any) {
        const body = {
            page: this.currentPageSkipList,
            pageSize: this.skiptemsPerPage
        };
        let url: string;
        if (this.inwardSkipCall === 'Inward') {
            // url = `inwards/downloadSkipInwardData?id=${this.inwardId}&type=inward`
            url = `/inwards/getAllRemarks/${inwardId}`;
        } else {
            url = `/outwards/getAllRemarks/${this.outwardIdForMac}`;
        }
        // const url = `/inwards/getAllRemarks/${inwardId}`;
        this.inwardService.postMethod(url, body)
            .subscribe(
                (res: any) => {
                    if (res.responseCode === 200) {
                        this.getData.data = res?.dataList || [];
                        this.toastr.success(`${res.responseMessage}`, 'Success');
                        this.skippedTotalRecords = res?.totalRecords || 0;
                    } else if (res.responseCode === 400) {
                        this.toastr.error(`${res.responseMessage}`, 'Failed!');
                    }
                    else {
                        this.toastr.error(`${res.responseMessage}`, 'Failed!');
                    }
                },
                (error: any) => {
                    if (error.error.responseCode == '400') {
                        this.toastr.success(`${error.error.responseMessage}`, 'Success');
                    } else {
                        this.toastr.error(`${error.error.responseMessage}`, 'Failed!');
                    }
                }
            );
    }
    downloadFile(inwardId: any) {
        let url: string;
        if (this.inwardSkipCall === 'Inward') {
            url = `/inwards/downloadSkipData?id=${this.inwardId}&type=inward`;
        } else {
            url = `/inwards/downloadSkipData?id=${this.outwardIdForMac}&type=outward`;
        }

        this.inwardService.getSkipMethod(url).subscribe(
            (res: Blob) => {
                const blob = new Blob([res], {
                    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                });

                saveAs(blob, 'Skip Data.xlsx');
                this.toastr.success('File downloaded successfully', 'Success');

            },
            (error: any) => {
                if (error.error instanceof Blob) {
                    error.error.text().then((text: string) => {
                        const json = JSON.parse(text);
                        this.toastr.error(json.responseMessage, 'Failed!');
                    });
                } else {
                    this.toastr.error(error.error?.responseMessage, 'Failed!');
                }
            }
        );
    }
    onClearMac() {
        this.searchOptionControl.setValue('');
        this.searchMacDeatilControl.setValue('');
        // this.clearMacEvent.emit();
        // reset paginator
        if (this.paginator) {
            this.paginator.pageIndex = 0;
            this.paginator.pageSize = this.mappingListitemsPerPage;
        }

        this.clearMacEvent.emit({
            page: 0,
            pageSize: this.mappingListitemsPerPage
        });
    }

    onPaginate(event: any) {
        this.paginateEvent.emit(event);
    }

    onInventoryDetails(itemId: number) {
        this.inventoryDetailsEvent.emit(itemId);
    }

    onDeleteShowMACMapping(item: any) {
        this.deleteShowMACMappingEvent.emit(item);
    }

    onClose() {
        this.onClearMac();
        this.dialogRef.close();
    }

    updateList(dataList: any[], totalRecords: number) {
        this.mappingList = dataList;
        this.mappingListdatatotalRecords = totalRecords;
        this.dataSource.data = [...dataList];
    }

    getHeaderName(col: string): string {
        const map: any = {
            id: 'Item Id',
            mac: 'MAC Address',
            serial: 'Serial Number',
            assetId: 'Asset Id',
            condition: 'Item Type',
            imsi: 'IMSI',
            iccid: 'ICCID',
            kik: 'KIK',
            msisdn: 'MSISDN',
            action: 'Action',
            reason: 'reason'
        };
        return map[col] ?? col;
    }

}
