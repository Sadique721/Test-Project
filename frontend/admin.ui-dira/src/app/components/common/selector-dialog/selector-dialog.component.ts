import { ChangeDetectorRef, Component, Inject, Input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { SelectBuildingDialogComponent } from '../select-building-dialog/select-building-dialog.component';
import { SelectorDialogService } from './selector-dialog.service';

@Component({
    selector: 'app-selector-dialog',
    imports: [],
    templateUrl: './selector-dialog.component.html',
    styleUrl: './selector-dialog.component.css',
    standalone: false
})
export class SelectorDialogComponent {

    @Input() headerTitle: string = "";
    // Table Data
    dataSource = new MatTableDataSource<any>([]);
    displayedColumns: string[] = ['select', 'id', 'name'];

    // Pagination Controls
    totalItem = 0;
    itemPerPage = 5;
    currentPage = 1;

    // Selected Item
    selectedItemId: number | null = null;
    selectedItemName: string;
    searchDetail: string;

    constructor(
        private serviceAreaService: SelectorDialogService,
        private cdRef: ChangeDetectorRef,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private dialogRef: MatDialogRef<SelectBuildingDialogComponent>

    ) { }

    ngOnInit(): void {
        this.setDataSource();
        this.detectChanges();
    }

    /**
     * Fetch building list from API (paginated)
     */
    setDataSource(): void {
        this.searchByName(true);
    }

    /**
     * Pagination event handler
     */
    onPaginateChange(event: PageEvent): void {
        this.itemPerPage = event.pageSize;
        this.currentPage = event.pageIndex + 1;
        this.setDataSource();
    }

    /**
     * Save selected building
     */
    onSave(): void {
        if (this.selectedItemId) {
            this.dialogRef.close({
                id: this.selectedItemId,
                name: this.selectedItemName
            });
        }
    }

    onSelectItem(data: any): void {
        this.selectedItemId = data?.id;
        this.selectedItemName = data?.name;
        this.cdRef?.detectChanges();
    }

    clearSearchForm() {
        this.searchByName(true);
    }

    searchByName(isClearFilter = false): void {
        let payload: any = {
            filters: [
                {
                    "filterDataType": "",
                    "filterValue": isClearFilter ? "" : this.searchDetail,
                    "filterColumn": "any",
                    "filterOperator": "equalto",
                    "filterCondition": "and"
                },
                {
                    "filterDataType": "",
                    "filterValue": "Active",
                    "filterColumn": "Status",
                    "filterOperator": "equalto",
                    "filterCondition": "and"
                }
            ],
            page: this.currentPage,
            pageSize: this.itemPerPage,
        };

        if (this.data?.serviceAreaIdList) {
            payload?.filters?.push({
                "filterDataType": "",
                "filterListValues": this.data?.serviceAreaIdList,
                "filterColumn": "serviceArea",
                "filterOperator": "equalto",
                "filterCondition": "and"
            });
        }

        this.serviceAreaService?.getDataList(payload, this.data?.url)?.subscribe({
            next: (res) => {
                res?.dataList?.map(data => {
                    if (!data?.name) {
                        data['name'] = data?.firstname + " " + data?.lastname;
                    }
                })
                this.dataSource = res?.dataList || [];
                this.totalItem = res?.totalRecords || 0;
                this.detectChanges();
            },
            error: () => {
            }
        })
    }

    /**
     * Close dialog without saving
     */
    onCancel(): void {
        this.dialogRef.close(null);
    }

    /**
     * Detect UI changes safely (for OnPush strategy)
     */
    private detectChanges(): void {
        try {
            this.cdRef.detectChanges();
        } catch (e) {
            console.warn('Change detection skipped:', e);
        }
    }
}
