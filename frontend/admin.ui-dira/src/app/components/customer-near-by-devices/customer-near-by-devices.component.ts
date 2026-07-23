import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ConfirmationService, MessageService } from 'primeng/api';
import { NgxSpinnerService } from 'ngx-spinner';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';
import { CustomermanagementService } from 'src/app/service/customermanagement.service';
import { CommondropdownService } from 'src/app/service/commondropdown.service';
import { ToastrService } from 'ngx-toastr';

declare var $: any;

@Component({
    selector: 'app-customer-near-by-devices',
    templateUrl: './customer-near-by-devices.component.html',
    styleUrls: ['./customer-near-by-devices.component.css'],
    standalone: false
})
export class CustomerNearByDevicesComponent implements OnInit {

    // ============================================
    // PROPERTIES
    // ============================================

    nearDeviceLocationData: any[] = [];
    paginatedNearDeviceData: any[] = [];
    customerIdINLocationDevice: any;

    // Pagination
    currentPagenearDeviceLocationList: number = 1;
    nearDeviceLocationItemPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    nearDeviceLocationtotalRecords: number = 0;

    // Table columns
    displayedColumns: string[] = ['name', 'latitude', 'longitude', 'distance', 'address'];

    // Data
    NetworkDeviceData: any;

    // ============================================
    // CONSTRUCTOR
    // ============================================

    constructor(
        public dialogRef: MatDialogRef<CustomerNearByDevicesComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        private customerManagementService: CustomermanagementService,
        public confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private messageService: MessageService
    ) { }

    // ============================================
    // LIFECYCLE HOOKS
    // ============================================

    ngOnInit(): void {

        // Get customer ID from dialog data
        const custId = this.data?.custId || this.data?.customerId;

        if (custId) {
            this.loadCustomerAndDevices(custId);
        } else {
            console.error('❌ No customer ID provided');
            this.toastr.error(`Customer ID is required`, 'Failed!')
            // this.messageService.add({
            //     severity: 'error',
            //     summary: 'Error',
            //     detail: 'Customer ID is required'
            // });
        }
    }

    // ============================================
    // DATA LOADING METHODS
    // ============================================

    /**
     * Load customer details and nearby devices
     */
    private loadCustomerAndDevices(custId: any): void {

        this.spinner.show();
        const url = `/customers/${custId}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {

                const viewcustomerListData = response.customers;
                this.customerIdINLocationDevice = viewcustomerListData.id;

                // Load nearby devices using customer location
                this.nearMyLocation(viewcustomerListData);

                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                console.error('❌ Error loading customer:', error);

                this.toastr.error(error.error?.error || 'Failed to load customer details', 'Error');
            }
        );
    }

    /**
     * Load nearby devices based on customer location
     */
    nearMyLocation(data: any): void {

        const deviceData = {
            latitude: data.latitude,
            longitude: data.longitude
        };

        this.spinner.show();
        const url = `/NetworkDevice/getNearbyDevices`;

        this.customerManagementService.postMethodInventory(url, deviceData).subscribe(
            (response: any) => {
                this.spinner.hide();

                this.nearDeviceLocationData = response.locations || [];
                this.nearDeviceLocationtotalRecords = this.nearDeviceLocationData.length;

                // Update paginated data
                this.updatePaginatedData();
            },
            (error: any) => {
                this.spinner.hide();
                console.error('❌ Error loading nearby devices:', error);

                this.toastr.error(error.error?.error || 'Failed to fetch nearby devices', 'Error');
            }
        );
    }

    // ============================================
    // PAGINATION METHODS
    // ============================================

    /**
     * Update paginated data based on current page
     */
    private updatePaginatedData(): void {
        const startIndex = (this.currentPagenearDeviceLocationList - 1) * this.nearDeviceLocationItemPerPage;
        const endIndex = startIndex + this.nearDeviceLocationItemPerPage;

        this.paginatedNearDeviceData = this.nearDeviceLocationData.slice(startIndex, endIndex);


    }

    /**
     * Handle page change event
     */
    pageChangedNearDeviceList(pageNumber: number): void {
        this.currentPagenearDeviceLocationList = pageNumber;
        this.updatePaginatedData();
    }

    /**
     * Handle page size change
     */
    onNearDevicePageSizeChange(event: any): void {
        this.nearDeviceLocationItemPerPage = event.value;
        this.currentPagenearDeviceLocationList = 1; // Reset to first page
        this.updatePaginatedData();
    }

    // ============================================
    // BIND DEVICE METHOD
    // ============================================

    /**
     * Bind network device to customer
     */
    bindNetworkDevice(networkdeviceID: any): void {


        if (!this.customerIdINLocationDevice) {
            this.toastr.error('Customer ID not found', 'Error');
            return;
        }

        if (!networkdeviceID) {
            this.toastr.error('Network Device ID not found', 'Error');
            return;
        }

        this.spinner.show();

        const deviceData = {};
        const url = `/NetworkDevice/bindNetworkDevice?customerId=${this.customerIdINLocationDevice}&networkDeviceId=${networkdeviceID}`;

        this.customerManagementService.updateInventoryMethod(url, deviceData).subscribe(
            (response: any) => {
                this.spinner.hide();

                this.NetworkDeviceData = response.locations;

                this.toastr.success(response.customer || 'Network device bound successfully!', 'Success');

                // Close dialog and pass success result
                this.dialogRef.close({ success: true, data: response });
            },
            (error: any) => {
                this.spinner.hide();
                console.error('❌ Error binding device:', error);

                this.toastr.error(error.error?.ERROR || 'Failed to bind network device', 'Error');
            }
        );
    }

    // ============================================
    // MODAL CLOSE METHOD
    // ============================================

    /**
     * Close the dialog
     */
    nearsearchClose(success: boolean = false): void {

        // Reset data
        this.nearDeviceLocationData = [];
        this.paginatedNearDeviceData = [];
        this.currentPagenearDeviceLocationList = 1;

        // Close dialog
        this.dialogRef.close({ success });
    }
}
