import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSortModule } from '@angular/material/sort';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import * as FileSaver from 'file-saver';
import { SelectionModel } from '@angular/cdk/collections';
import { KRAIntegrationService } from './revenue-authorities-integration.service';
import { ToastrService } from 'ngx-toastr';
import { CustomerSelectComponent } from 'src/app/components/customer-select/customer-select.component';
import { InvoiceMasterService } from 'src/app/service/invoice-master.service';
import { InvoiceDetalisModelComponent } from '../invoice-detalis-model/invoice-detalis-model.component';

@Component({
    selector: 'app-revenue-authorities-integration',
    standalone: true,
    imports: [
        CommonModule,
        MatTabsModule,
        MatTableModule,
        MatPaginatorModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatSortModule,
        MatCheckboxModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatTooltipModule,
        FormsModule,
    ],
    templateUrl: './revenue-authorities-integration.component.html',
    styleUrl: './revenue-authorities-integration.component.css',
})
export class KRAIntegrationComponent {

    pageSizeOptions: number[] = [5, 10, 20, 50, 100];
    pageSizeOptionsForInvoice: number[] = [5, 10, 20, 50, 100, 500, 1000]
    planCurrentPage: number = 1;
    searchFields = [
        { value: 'customerName', label: 'Customer Name' },
        { value: 'accountNumber', label: 'Account Number' },
        { value: 'mobileNumber', label: 'Mobile Number' }
    ];
    customerSearchField: string = 'customerName';
    creditNoteSearchField: string = 'customerName';
    customerSearchText: string = '';
    invoiceAccountNumberSearch: string = '';
    invoiceCustomerNameSearch: string = '';
    parentCustList: any[] = [];
    selectedParentCust: any = null;
    invoiceNumberSearch: string = '';
    invoiceBillFromDate: Date | null = null;
    invoiceBillToDate: Date | null = null;
    pendingInvoiceAccountNumberSearch: string = '';
    pendingInvoiceCustomerNameSearch: string = '';
    pendingInvoiceParentCustList: any[] = [];
    selectedPendingInvoiceCust: any = null;
    pendingInvoiceNumberSearch: string = '';
    pendingInvoiceBillFromDate: Date | null = null;
    pendingInvoiceBillToDate: Date | null = null;
    creditNoteAccountNumberSearch: string = '';
    creditNoteCustomerNameSearch: string = '';
    creditNoteParentCustList: any[] = [];
    selectedCreditNoteCust: any = null;
    creditNoteNumberSearch: string = '';
    creditNoteBillFromDate: Date | null = null;
    creditNoteBillToDate: Date | null = null;
    creditNoteSearchText: string = '';

    chargeDisplayedColumns: string[] = [
        'select', 'name', 'type', 'chargeAmount', 'tax', 'totalChargeAmount', 'status', 'ispName'
    ];
    chargeDataSource: any[] = [];
    chargeTotalRecords: number = 0;
    chargeItemsPerPage: number = 10;
    chargeCurrentPage: number = 1;
    chargeTypeList: any[] = [];
    chargeCategoryList: any[] = [];
    currency: string = '';
    chargeSelection = new SelectionModel<any>(true, []);
    chargeSearchOptionSelect = [
        { label: 'Any', value: 'any' },
        { label: 'Name', value: 'name' },
        { label: 'Category', value: 'chargecategory' },
        { label: 'Type', value: 'chargetype' }
    ];
    chargeSearchOption: string = 'any';
    chargeSearchInput: string = '';
    isChargeNameSearch: boolean = true;
    isChargeCategorySearch: boolean = false;
    isChargeTypeSearch: boolean = false;

    constructor(private kraService: KRAIntegrationService, private toastr: ToastrService, private invoiceMasterService: InvoiceMasterService,

        private dialog: MatDialog
    ) { }
    ngOnInit(): void {
        this.loadCustomerData();
        this.loadChargeTypeList();
        this.loadChargeCategoryList();
    }

    customerDisplayedColumns: string[] = [
        'select', 'name', 'username', 'acctno', 'mobile', 'serviceArea', 'connectionMode'
    ];

    customerDataSource: any[] = [];
    customerTotalRecords: number = 0;
    customerItemsPerPage: number = 5;
    customerCurrentPage: number = 1;
    customerSelection = new SelectionModel<any>(true, []);

    isAllCustomerSelected(): boolean {
        return this.customerDataSource.length > 0 &&
            this.customerSelection.selected.length === this.customerDataSource.length;
    }

    toggleAllCustomer(): void {
        if (this.isAllCustomerSelected()) {
            this.customerSelection.clear();
        } else {
            this.customerDataSource.forEach(row => this.customerSelection.select(row));
        }
    }

    planDisplayedColumns: string[] = [
        'select', 'name', 'code', 'mode', 'createdByName', 'createdate'
    ];
    planDataSource: any[] = [];
    planTotalRecords: number = 4;
    planItemsPerPage: number = 10;
    planSelection = new SelectionModel<any>(true, []);

    isAllPlanSelected(): boolean {
        return this.planDataSource.length > 0 &&
            this.planSelection.selected.length === this.planDataSource.length;
    }

    toggleAllPlan(): void {
        if (this.isAllPlanSelected()) {
            this.planSelection.clear();
        } else {
            this.planDataSource.forEach(row => this.planSelection.select(row));
        }
    }

    invoiceDisplayedColumns: string[] = [
        'select', 'customer', 'accountNumber', 'docnumber', 'startdate', 'totalamount', 'billrunstatus', 'paymentStatus'
    ];
    invoiceDataSource: any[] = [];
    invoiceTotalRecords: number = 5;
    invoiceItemsPerPage: number = 10;
    invoiceCurrentPage: number = 1;
    invoiceSelection = new SelectionModel<any>(true, []);

    pendingInvoiceDisplayedColumns: string[] = [
        'select', 'customer', 'accountNumber', 'docnumber', 'startdate', 'totalamount', 'billrunstatus', 'paymentStatus', 'download'
    ];
    pendingInvoiceDataSource: any[] = [];
    pendingInvoiceTotalRecords: number = 5;
    pendingInvoiceItemsPerPage: number = 10;
    pendingInvoiceCurrentPage: number = 1;
    pendingInvoiceSelection = new SelectionModel<any>(true, []);

    isAllInvoiceSelected(): boolean {
        return this.invoiceDataSource.length > 0 &&
            this.invoiceSelection.selected.length === this.invoiceDataSource.length;
    }

    toggleAllInvoice(): void {
        if (this.isAllInvoiceSelected()) {
            this.invoiceSelection.clear();
        } else {
            this.invoiceDataSource.forEach(row => this.invoiceSelection.select(row));
        }
    }

    isAllPendingInvoiceSelected(): boolean {
        return this.pendingInvoiceDataSource.length > 0 &&
            this.pendingInvoiceSelection.selected.length === this.pendingInvoiceDataSource.length;
    }

    toggleAllPendingInvoice(): void {
        if (this.isAllPendingInvoiceSelected()) {
            this.pendingInvoiceSelection.clear();
        } else {
            this.pendingInvoiceDataSource.forEach(row => this.pendingInvoiceSelection.select(row));
        }
    }

    creditNoteDisplayedColumns: string[] = [
        'select', 'customer', 'accountNumber', 'invoiceNumber', 'docnumber', 'creditAmount', 'reason', 'status', 'createdByName'
    ];
    creditNoteDataSource: any[] = [];
    creditNoteTotalRecords: number = 5;
    creditNoteItemsPerPage: number = 10;
    creditNoteCurrentPage: number = 1;
    creditNoteSelection = new SelectionModel<any>(true, []);

    isAllCreditNoteSelected(): boolean {
        return this.creditNoteDataSource.length > 0 &&
            this.creditNoteSelection.selected.length === this.creditNoteDataSource.length;
    }

    toggleAllCreditNote(): void {
        if (this.isAllCreditNoteSelected()) {
            this.creditNoteSelection.clear();
        } else {
            this.creditNoteDataSource.forEach(row => this.creditNoteSelection.select(row));
        }
    }

    onCustomerSearchFieldChange(): void {
        this.customerSearchText = '';
    }

    onTabChange(event: any): void {
        const index = event.index;
        if (index === 0) {
            this.customerCurrentPage = 1;
            this.loadCustomerData();
        } else if (index === 1) {
            this.planCurrentPage = 1;
            this.loadPlanData();
        } else if (index === 2) {
            this.invoiceCurrentPage = 1;
            this.loadInvoiceData(false);
        } else if (index === 3) {
            this.pendingInvoiceCurrentPage = 1;
            this.loadInvoiceData(true);
        } else if (index === 4) {
            this.creditNoteCurrentPage = 1;
            this.loadCreditNoteData();
        } else if (index === 5) {
            this.chargeCurrentPage = 1;
            this.loadChargeData();
        }
    }


    loadCustomerData(): void {
        const payload = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "false",
                    filterColumn: "isKRASynced",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.customerCurrentPage,
            pageSize: this.customerItemsPerPage
        };

        if (this.customerSearchText && this.customerSearchText.trim() !== '') {
            let filterColumn = 'fullname';
            if (this.customerSearchField === 'accountNumber') {
                filterColumn = 'accountNumber';
            } else if (this.customerSearchField === 'mobileNumber') {
                filterColumn = 'mobile';
            }
            payload.filters.push({
                filterDataType: "",
                filterValue: this.customerSearchText.trim(),
                filterColumn,
                filterOperator: "contains",
                filterCondition: "and"
            });
        }

        this.kraService.postMethod('/customers/searchNew/Prepaid', payload).subscribe({
            next: (response: any) => {
                if (response && response.status === 200) {
                    this.customerDataSource = response.customerList || [];
                    this.customerTotalRecords = response.pageDetails?.totalRecords || 0;
                    this.customerSelection.clear();
                }
            },
            error: (error: any) => {
                this.customerDataSource = [];
                this.customerTotalRecords = 0;
                this.customerSelection.clear();
                this.toastr.error(error?.error?.message || 'Failed to fetch customers', 'Error!');
            }
        });
    }

    loadPlanData(): void {
        const payload = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: " false",
                    filterColumn: "isKRASynced",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.planCurrentPage,
            pageSize: this.planItemsPerPage
        };

        this.kraService.postMethod('/postpaidplan/search', payload).subscribe({
            next: (response: any) => {
                if (response && response.status === 200) {
                    this.planDataSource = response.postpaidplanList || [];
                    this.planTotalRecords = response.pageDetails?.totalRecords || 0;
                    this.planSelection.clear();
                }
            },
            error: (error: any) => {
                this.planDataSource = [];
                this.planTotalRecords = 0;
                this.planSelection.clear();
                this.toastr.error(error?.error?.message || 'Failed to fetch plans', 'Error!');
            }
        });
    }

    private formatInvoiceDate(input: Date | null): string {
        if (!input) {
            return '';
        }

        const date = input instanceof Date ? input : new Date(input);
        if (isNaN(date.getTime())) {
            return '';
        }

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    loadInvoiceData(isPending: boolean = false): void {
        const currentPage = isPending ? this.pendingInvoiceCurrentPage : this.invoiceCurrentPage;
        const pageSize = isPending ? this.pendingInvoiceItemsPerPage : this.invoiceItemsPerPage;

        const body: any = {
            page: currentPage,
            pageSize
        };
        let queryParams;
        if (isPending) {
            queryParams = ['isInvoiceVoid=true', 'isKraSynced=true'];
        }
        else {
            queryParams = ['isInvoiceVoid=true', 'isKraSynced=false'];
        }


        const accountNumber = isPending ? this.pendingInvoiceAccountNumberSearch?.trim() : this.invoiceAccountNumberSearch?.trim();
        if (accountNumber) {
            queryParams.push(`acctno=${encodeURIComponent(accountNumber)}`);
        }

        const customerName = isPending ? this.pendingInvoiceCustomerNameSearch : this.invoiceCustomerNameSearch;
        if (customerName) {
            queryParams.push(`customerid=${encodeURIComponent(customerName)}`);
        }

        const invoiceNumber = isPending ? this.pendingInvoiceNumberSearch?.trim() : this.invoiceNumberSearch?.trim();
        if (invoiceNumber) {
            queryParams.push(`docnumber=${encodeURIComponent(invoiceNumber)}`);
        }

        const billFromDate = this.formatInvoiceDate(isPending ? this.pendingInvoiceBillFromDate : this.invoiceBillFromDate);
        if (billFromDate) {
            queryParams.push(`startfromdate=${encodeURIComponent(billFromDate)}`);
        }

        const billToDate = this.formatInvoiceDate(isPending ? this.pendingInvoiceBillToDate : this.invoiceBillToDate);
        if (billToDate) {
            queryParams.push(`starttodate=${encodeURIComponent(billToDate)}`);
        }

        this.kraService.postMethodRevenue(`/invoice/search?${queryParams.join('&')}`, body).subscribe({
            next: (response: any) => {
                if (response && response.status === 200) {
                    if (isPending) {
                        this.pendingInvoiceDataSource = response.invoicesearchlist || [];
                        this.pendingInvoiceTotalRecords = response.pageDetails?.totalRecords || 0;
                        this.pendingInvoiceSelection.clear();
                    } else {
                        this.invoiceDataSource = response.invoicesearchlist || [];
                        this.invoiceTotalRecords = response.pageDetails?.totalRecords || 0;
                        this.invoiceSelection.clear();
                    }
                }
            },
            error: (error: any) => {
                if (isPending) {
                    this.pendingInvoiceDataSource = [];
                    this.pendingInvoiceTotalRecords = 0;
                    this.pendingInvoiceSelection.clear();
                } else {
                    this.invoiceDataSource = [];
                    this.invoiceTotalRecords = 0;
                    this.invoiceSelection.clear();
                }
                this.toastr.error(error?.error?.message || 'Failed to fetch invoices', 'Error!');
            }
        });
    }

    private openCustomerSelectDialog(currentId: any, onSelected: (id: any, title: string, name: string) => void): void {
        this.dialog.open(CustomerSelectComponent, {
            width: '1500px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            data: { type: 'both', custId: currentId },
            disableClose: true
        }).afterClosed().subscribe((result: any) => {
            if (result) {
                const selected = Array.isArray(result) ? result[0] : result;
                if (selected?.id) {
                    onSelected(selected.id, selected.title, selected.name);
                }
            }
        });
    }

    modalOpenParentCustomer(): void {
        this.openCustomerSelectDialog(this.selectedParentCust, (id, title, name) => {
            this.selectedParentCust = id;
            this.parentCustList = [{ id, title, name }];
            this.invoiceCustomerNameSearch = id;
        });
    }

    removeSelParentCust(): void {
        this.selectedParentCust = null;
        this.parentCustList = [];
        this.invoiceCustomerNameSearch = '';
    }

    modalOpenPendingInvoiceParentCustomer(): void {
        this.openCustomerSelectDialog(this.selectedPendingInvoiceCust, (id, title, name) => {
            this.selectedPendingInvoiceCust = id;
            this.pendingInvoiceParentCustList = [{ id, title, name }];
            this.pendingInvoiceCustomerNameSearch = id;
        });
    }

    removeSelPendingInvoiceParentCust(): void {
        this.selectedPendingInvoiceCust = null;
        this.pendingInvoiceParentCustList = [];
        this.pendingInvoiceCustomerNameSearch = '';
    }

    loadCreditNoteData(): void {
        const queryParams: string[] = [
            `type=CreditNote`,
            `page=${this.creditNoteCurrentPage}`,
            `pageSize=${this.creditNoteItemsPerPage}`,
            `isKraSynced=false`
        ];

        const accountNumber = this.creditNoteAccountNumberSearch?.trim();
        if (accountNumber) {
            queryParams.push(`acctno=${encodeURIComponent(accountNumber)}`);
        }

        const customerName = this.creditNoteCustomerNameSearch;
        if (customerName) {
            queryParams.push(`customerid=${encodeURIComponent(customerName)}`);
        }

        const creditNoteNumber = this.creditNoteNumberSearch?.trim();
        if (creditNoteNumber) {
            queryParams.push(`creditDocumentNumber=${encodeURIComponent(creditNoteNumber)}`);
        }

        const billFromDate = this.formatInvoiceDate(this.creditNoteBillFromDate);
        if (billFromDate) {
            queryParams.push(`payfromdate=${encodeURIComponent(billFromDate)}`);
        }

        const billToDate = this.formatInvoiceDate(this.creditNoteBillToDate);
        if (billToDate) {
            queryParams.push(`paytodate=${encodeURIComponent(billToDate)}`);
        }

        this.kraService.getMethodRevenue(`/payment/filter?${queryParams.join('&')}`).subscribe({
            next: (response: any) => {
                if (response && response.status === 200) {
                    this.creditNoteDataSource = response.creditDocumentPojoList || [];
                    this.creditNoteTotalRecords = response.pageDetails?.totalRecords || 0;
                    this.creditNoteSelection.clear();
                }
            },
            error: (error: any) => {
                this.creditNoteDataSource = [];
                this.creditNoteTotalRecords = 0;
                this.creditNoteSelection.clear();
                this.toastr.error(error?.error?.message || 'Failed to fetch credit notes', 'Error!');
            }
        });
    }

    modalOpenCreditNoteParentCustomer(): void {
        this.openCustomerSelectDialog(this.selectedCreditNoteCust, (id, title, name) => {
            this.selectedCreditNoteCust = id;
            this.creditNoteParentCustList = [{ id, title, name }];
            this.creditNoteCustomerNameSearch = id;
        });
    }

    removeSelCreditNoteParentCust(): void {
        this.selectedCreditNoteCust = null;
        this.creditNoteParentCustList = [];
        this.creditNoteCustomerNameSearch = '';
    }

    searchCustomer(): void {
        if (!this.customerSearchText)
            return
        this.customerCurrentPage = 1;
        this.loadCustomerData();
    }

    clearCustomerSearch(): void {
        this.customerSearchText = '';
        this.customerCurrentPage = 1;
        this.loadCustomerData();
    }

    preventScrollOnNumber(event: Event): void {
        const target = event.target as HTMLInputElement;
        if (!target || target.type !== 'number') {
            return;
        }

        if (event instanceof WheelEvent) {
            event.preventDefault();
            return;
        }

        if (event instanceof KeyboardEvent) {
            const blockedKeys = ['ArrowUp', 'ArrowDown'];
            if (blockedKeys.includes(event.key)) {
                event.preventDefault();
            }
        }
    }

    getStatusClass(status: any): string {
        const s = (status || '').toString().toLowerCase();
        if (s.includes('fully paid') || s.includes('paid') || s.includes('fullypaid')) {
            return 'status-paid';
        }
        if (s.includes('payable') || s.includes('pending')) {
            return 'status-payable';
        }
        if (!s || s.includes('unpaid')) {
            return 'status-unpaid';
        }
        return 'status-payable';
    }

    getBillRunStatusClass(status: string): string {
        const s = (status || '').toString().toLowerCase();
        if (s === 'generated' || s === 'exported') {
            return 'status-paid';
        }
        if (s === 'pending') {
            return 'status-payable';
        }
        if (s === 'void' || s === 'cancelled') {
            return 'status-unpaid';
        }
        return 'status-payable';
    }

    searchInvoice(): void {
        this.invoiceCurrentPage = 1;
        this.loadInvoiceData(false);
    }

    clearInvoiceSearch(): void {
        this.invoiceAccountNumberSearch = '';
        this.invoiceCustomerNameSearch = '';
        this.selectedParentCust = null;
        this.parentCustList = [];
        this.invoiceNumberSearch = '';
        this.invoiceBillFromDate = null;
        this.invoiceBillToDate = null;
        this.invoiceCurrentPage = 1;
        this.loadInvoiceData(false);
    }

    searchPendingInvoice(): void {
        this.pendingInvoiceCurrentPage = 1;
        this.loadInvoiceData(true);
    }

    clearPendingInvoiceSearch(): void {
        this.pendingInvoiceAccountNumberSearch = '';
        this.selectedPendingInvoiceCust = null;
        this.pendingInvoiceParentCustList = [];
        this.pendingInvoiceCustomerNameSearch = '';
        this.pendingInvoiceNumberSearch = '';
        this.pendingInvoiceBillFromDate = null;
        this.pendingInvoiceBillToDate = null;
        this.pendingInvoiceCurrentPage = 1;
        this.loadInvoiceData(true);
    }

    searchCreditNote(): void {
        this.creditNoteCurrentPage = 1;
        this.loadCreditNoteData();
    }

    clearCreditNoteSearch(): void {
        this.creditNoteAccountNumberSearch = '';
        this.selectedCreditNoteCust = null;
        this.creditNoteParentCustList = [];
        this.creditNoteCustomerNameSearch = '';
        this.creditNoteNumberSearch = '';
        this.creditNoteBillFromDate = null;
        this.creditNoteBillToDate = null;
        this.creditNoteCurrentPage = 1;
        this.loadCreditNoteData();
    }

    pageChangedCustomer(event: PageEvent): void {
        this.customerItemsPerPage = event.pageSize;
        this.customerCurrentPage = event.pageIndex + 1;

        this.loadCustomerData();
    }

    pageChangedPlan(event: PageEvent): void {
        this.planItemsPerPage = event.pageSize;
        this.planCurrentPage = event.pageIndex + 1;
        this.loadPlanData();
    }

    pageChangedInvoice(event: PageEvent): void {
        this.invoiceItemsPerPage = event.pageSize;
        this.invoiceCurrentPage = event.pageIndex + 1;
        this.invoiceSelection.clear();
        this.loadInvoiceData(false);
    }

    pageChangedPendingInvoice(event: PageEvent): void {
        this.pendingInvoiceItemsPerPage = event.pageSize;
        this.pendingInvoiceCurrentPage = event.pageIndex + 1;
        this.pendingInvoiceSelection.clear();
        this.loadInvoiceData(true);
    }

    pageChangedCreditNote(event: PageEvent): void {
        this.creditNoteItemsPerPage = event.pageSize;
        this.creditNoteCurrentPage = event.pageIndex + 1;
        this.loadCreditNoteData();
    }

    loadChargeTypeList(): void {
        this.kraService.getMethod('/commonList/generic/chargetype').subscribe({
            next: (response: any) => {
                this.chargeTypeList = response.dataList || [];
            },
            error: () => { }
        });
    }

    loadChargeCategoryList(): void {
        this.kraService.getMethod('/commonList/chargeCategory').subscribe({
            next: (response: any) => {
                this.chargeCategoryList = response.dataList || [];
            },
            error: () => { }
        });
    }

    selChargeSearchOption(event: any): void {
        const val = event.value;
        this.isChargeNameSearch = val === 'any' || val === 'name';
        this.isChargeCategorySearch = val === 'chargecategory';
        this.isChargeTypeSearch = val === 'chargetype';
        this.chargeSearchInput = '';
    }

    searchCharge(): void {
        this.chargeCurrentPage = 1;
        this.loadChargeData();
    }

    clearChargeSearch(): void {
        this.chargeSearchOption = 'any';
        this.chargeSearchInput = '';
        this.isChargeNameSearch = true;
        this.isChargeCategorySearch = false;
        this.isChargeTypeSearch = false;
        this.chargeCurrentPage = 1;
        this.loadChargeData();
    }

    getChargeTypeName(chargetype: string): string {
        const found = this.chargeTypeList.find(t => t.value === chargetype);
        return found ? found.text : (chargetype || '-');
    }

    loadChargeData(): void {
        const payload = {
            filters: [{
                filterDataType: '',
                filterValue: this.chargeSearchInput?.trim() || '',
                filterColumn: this.chargeSearchOption || 'any',
                filterOperator: 'equalto',
                filterCondition: 'and'
            }],
            page: this.chargeCurrentPage,
            pageSize: this.chargeItemsPerPage
        };
        this.kraService.postMethod('/charge/search', payload).subscribe({
            next: (response: any) => {
                this.chargeDataSource = response.chargelist || [];
                this.chargeTotalRecords = response.pageDetails?.totalRecords || 0;
                this.chargeSelection.clear();
            },
            error: (error: any) => {
                if (error.error.status == 404) {
                    this.chargeDataSource = [];
                    this.chargeTotalRecords = 0;

                    this.toastr.info(`${error.error.msg}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');

                }
            }
        });
    }

    pageChangedCharge(event: PageEvent): void {
        this.chargeItemsPerPage = event.pageSize;
        this.chargeCurrentPage = event.pageIndex + 1;
        this.loadChargeData();
    }

    isAllChargeSelected(): boolean {
        return this.chargeDataSource.length > 0 &&
            this.chargeSelection.selected.length === this.chargeDataSource.length;
    }

    toggleAllCharge(): void {
        if (this.isAllChargeSelected()) {
            this.chargeSelection.clear();
        } else {
            this.chargeDataSource.forEach(row => this.chargeSelection.select(row));
        }
    }

    addSelectedCharges(): void {
        const selected = this.chargeSelection.selected;
        if (!selected.length) return;
        this.kraService.postMethodWithTextResponse('/intg/kra/charge', selected.map(c => c.id)).subscribe({
            next: (response: any) => {
                this.toastr.success(response, 'Success!');
                this.chargeSelection.clear();
                this.chargeCurrentPage = 1;
                this.loadChargeData();
            },
            error: (error: any) => {
                this.toastr.error(error?.error?.ERROR || 'Failed to sync charges', 'Error!');
            }
        });
    }

    generateInvoice(element: any): void {
        if (!element || !element.id) {
            this.toastr.error('Invoice id not found', 'Error!');
            return;
        }
        const url = `/generatePdfByInvoiceId/${element.id}`;
        this.invoiceMasterService.generateMethod(url).subscribe(
            (response: any) => {
                this.toastr.success(`${response.responseMessage}`, 'Success!');
                this.loadInvoiceData(true);
            },
            (error: any) => {
                this.toastr.error(`${error?.error?.message || 'Failed to generate invoice'}`, 'Error!');
            }
        );
    }

    viewInvoice(element: any): void {
        if (!element || !element.id) {
            this.toastr.error('Invoice id not found', 'Error!');
            return;
        }
        this.dialog.open(InvoiceDetalisModelComponent, {
            width: '80%',
            disableClose: true,
            data: {
                invoiceID: element.id,
                custID: element.custid || element.customerid || '',
                sourceType: 'invoiceMaster',
                InvoiceDATA: '',
                dialogId: ''
            }
        });
    }

    downloadInvoice(invoiceId: any, custId?: any, filename?: string): void {
        if (!invoiceId) {
            this.toastr.error('Invoice id not found', 'Error!');
            return;
        }
        const downloadUrl = '/invoicePdf/download/' + invoiceId;
        this.invoiceMasterService.downloadPDFInvoice(downloadUrl).subscribe(
            (response: any) => {
                const file = new Blob([response], { type: 'application/pdf' });
                const fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, (filename || invoiceId) + '');
            },
            (error: any) => {
                this.toastr.error(`${error?.error?.message || 'Failed to download invoice'}`, 'Failed!');
            }
        );
    }

    addSelectedCustomers(): void {
        const selected = this.customerSelection.selected;
        if (!selected.length) return;
        this.kraService.postMethodWithTextResponse('/intg/kra/customer', selected.map(c => c.id)).subscribe({
            next: (response: any) => {
                this.toastr.success(response, 'Success!');
                this.customerSelection.clear();
                this.customerCurrentPage = 1;
                this.loadCustomerData();
            },
            error: (error: any) => {
                this.toastr.error(error?.error?.message || 'Failed to sync customers', 'Error!');
            }
        });
    }

    addSelectedPlans(): void {
        const selected = this.planSelection.selected;
        if (!selected.length) return;
        this.kraService.postMethodWithTextResponse('/intg/kra/plan', selected.map(p => p.id)).subscribe({
            next: (response: any) => {
                this.toastr.success(response, 'Success!');
                this.planSelection.clear();
                this.planCurrentPage = 1;
                this.loadPlanData();
            },
            error: (error: any) => {
                this.toastr.error(error?.error?.message || 'Failed to sync plans', 'Error!');
            }
        });
    }

    addSelectedInvoices(): void {
        const selected = this.invoiceSelection.selected;
        if (!selected.length) return;
        this.kraService.postMethodRevenueWithTextResponse('/intg/kra/invoice', selected.map(i => i.id)).subscribe({
            next: (response: any) => {
                this.toastr.success(response, 'Success!');
                this.invoiceSelection.clear();
                this.invoiceCurrentPage = 1;
                this.invoiceTotalRecords = 0;
                this.loadInvoiceData(false);
            },
            error: (error: any) => {
                this.toastr.error(error?.error?.message || 'Failed to sync invoices', 'Error!');
            }
        });
    }

    addSelectedPendingInvoices(): void {
        const selected = this.pendingInvoiceSelection.selected;
        if (!selected.length) return;
        this.kraService.postMethodRevenueWithTextResponse('/intg/kra/invoice', selected.map(i => i.id)).subscribe({
            next: (response: any) => {
                this.toastr.success(response, 'Success!');
                this.pendingInvoiceSelection.clear();
                this.pendingInvoiceCurrentPage = 1;
                this.pendingInvoiceTotalRecords = 0;
                this.loadInvoiceData(true);
            },
            error: (error: any) => {
                this.toastr.error(error?.error?.message || 'Failed to sync invoices', 'Error!');
            }
        });
    }

    bulkDownloadSyncedInvoices(): void {
        const selected = this.pendingInvoiceSelection.selected;
        if (!selected.length) return;
        const payload = { debitDocIds: selected.map(i => i.id) };
        this.kraService.bulkDownloadInvoicePdf('/invoicePdf/download/bulk', payload).subscribe({
            next: (response: any) => {
                const file = new Blob([response], { type: 'application/zip' });
                FileSaver.saveAs(file, 'Bulk_Invoices.zip');
                this.pendingInvoiceSelection.clear();
            },
            error: (error: any) => {
                this.toastr.error(error?.error?.message || 'Failed to download bulk invoices', 'Error!');
            }
        });
    }

    addSelectedCreditNotes(): void {
        const selected = this.creditNoteSelection.selected;
        if (!selected.length) return;
        this.kraService.postMethodRevenueWithTextResponse('/intg/kra/creditNote', selected.map(cn => cn.id)).subscribe({
            next: (response: any) => {
                this.toastr.success(response, 'Success!');
                this.creditNoteSelection.clear();
                this.creditNoteCurrentPage = 1;
                this.loadCreditNoteData();
            },
            error: (error: any) => {
                this.toastr.error(error?.error?.message || 'Failed to sync credit notes', 'Error!');
            }
        });
    }
}
