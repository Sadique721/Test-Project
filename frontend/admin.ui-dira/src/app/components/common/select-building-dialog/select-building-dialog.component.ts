import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, Input, OnInit } from '@angular/core';
import { SelectBuildingDialogService } from './select-building-dialog.service';
import { MatTableDataSource } from '@angular/material/table';
import { PageEvent } from '@angular/material/paginator';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
    selector: 'app-select-building-dialog',
    templateUrl: './select-building-dialog.component.html',
    styleUrls: ['./select-building-dialog.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SelectBuildingDialogComponent implements OnInit {


    // Table Data
    buildingDataSource = new MatTableDataSource<any>([]);
    displayedColumns: string[] = ['select', 'id', 'name'];

    // Pagination Controls
    totalBuildings = 0;
    buildingsPerPage = 5;
    currentPage = 1;

    // Selected Building
    selectedBuildingId: number | null = null;
    selectedBuildingName: string;
    searchDetail: string;

    constructor(
        private buildingService: SelectBuildingDialogService,
        private cdRef: ChangeDetectorRef,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private dialogRef: MatDialogRef<SelectBuildingDialogComponent>

    ) { }

    ngOnInit(): void {
        this.loadBuildingList();
        this.detectChanges();
    }

    /**
     * Fetch building list from API (paginated)
     */
    loadBuildingList(): void {
        const params = {
            page: this.currentPage, // API usually starts pages from 1
            pageSize: this.buildingsPerPage,
        }
        if (this.data.areaId) {
            params["area"] = this.data?.areaId;
        }
        const queryParams = {
            params: params,
        };

        this.buildingService.getBuildingList(queryParams)?.subscribe({
            next: (res: any) => {
                this.buildingDataSource = res?.dataList || [];
                this.totalBuildings = res?.totalRecords || 0;
                this.detectChanges();
            },
            error: (err) => {
                console.error('Error fetching building list:', err);
            },
        });
    }

    /**
     * Pagination event handler
     */
    onPaginateChange(event: PageEvent): void {
        this.buildingsPerPage = event.pageSize;
        this.currentPage = event.pageIndex + 1;
        this.loadBuildingList();
    }

    /**
     * Save selected building
     */
    onSave(): void {
        if (this.selectedBuildingId) {
            this.dialogRef.close({
                id: this.selectedBuildingId,
                name: this.selectedBuildingName
            });
        }
    }

    onSelectBuilding(buildingData: any): void {
        // set selected id
        this.selectedBuildingId = buildingData?.id;
        this.selectedBuildingName = buildingData?.name;

        // if you use MatTableDataSource and not swapping reference, ensure change detection
        this.cdRef?.detectChanges();
    }

    clearSearchForm() {
        this.searchBuildingByName(true);
    }

    searchBuildingByName(isClearFilter = false): void {
        let payload = {
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
                    "filterColumn": "status",
                    "filterOperator": "equalto",
                    "filterCondition": "and"
                }
            ],
            page: this.currentPage,
            pageSize: this.buildingsPerPage,
        };
        if (this.data.areaId) {
            payload.filters?.push({
                "filterDataType": "",
                "filterValue": this.data.areaId,
                "filterColumn": "area",
                "filterOperator": "equalto",
                "filterCondition": "and"
            })
        }

        this.buildingService?.getBuildingListBySearchValue(payload)?.subscribe({
            next: (res) => {
                this.buildingDataSource = res?.dataList || [];
                this.totalBuildings = res?.totalRecords || 0;
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
        }
    }
}
