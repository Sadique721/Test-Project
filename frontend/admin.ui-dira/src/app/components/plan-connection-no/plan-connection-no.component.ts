import { Component, Input, Output, OnInit, EventEmitter, Inject, ViewChild } from "@angular/core";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ChangeDetectorRef } from '@angular/core';

declare var $: any;

@Component({
    selector: "app-plan-connection-no",
    templateUrl: "./plan-connection-no.component.html",
    styleUrls: ["./plan-connection-no.component.css"],
    standalone: false
})
export class PlanConnectionNoComponent implements OnInit {
    @Input() planForConnection;
    @Output() closeDialog = new EventEmitter();
    @ViewChild("planConnectionDialog") planConnectionDialog;
    dialogRefInstance: MatDialogRef<any>;
    planNameOpen: boolean = false;
    constructor(
        private cdRef: ChangeDetectorRef,
        private spinner: NgxSpinnerService,
        private customerManagementService: CustomermanagementService,
        public confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private messageService: MessageService,
        private dialog: MatDialog,
        public dialogRef: MatDialogRef<PlanConnectionNoComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any
    ) {
        this.planForConnection = data.planForConnection;
    }

    ngOnInit(): void {
        this.planNameOpen = true;
    }

    ngAfterViewInit() {
        if (this.dialogRefInstance) {
            this.dialog.closeAll();
            this.dialogRefInstance.close();
            this.dialogRefInstance = null;
        }
        this.cdRef.detectChanges();
        this.dialogRefInstance = this.dialog.open(this.planConnectionDialog, {
            width: '850px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
        });
        this.dialogRefInstance.afterClosed().subscribe(() => {
             this.dialog.closeAll();
            this.dialogRefInstance = null; // Clear reference on close
        });
    }

    onCancel() {
        this.dialogRefInstance.close();
    }
    closeModal() {
        this.closeDialog.emit();
        this.planNameOpen = false;
    }
}
