import {
  Component,
  Input,
  Output,
  OnInit,
  EventEmitter,
  TemplateRef,
  ViewChild,
  Inject
} from "@angular/core";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { SharedModule } from "src/app/shared/shared.module";

declare var $: any;

export interface fetchData {
  planForConnectionData: any;
}

@Component({
  selector: "app-plan-connection-no",
  templateUrl: "./plan-connection-no.component.html",
  styleUrls: ["./plan-connection-no.component.css"],
  standalone: true,
  imports: [SharedModule]
})
export class PlanConnectionNoComponent implements OnInit {
  // @Input() planForConnection:any;
  // @Output() closeDialog = new EventEmitter();
  // @ViewChild("planConnectionDialog") planConnectionDialog!: TemplateRef<any>;
  private dialog: MatDialog;

  planForConnection: any;
  constructor(
    private spinner: NgxSpinnerService,
    private toastr: ToastrService,
    private customerManagementService: CustomermanagementService,
    public confirmationService: ConfirmationService,
    public commondropdownService: CommondropdownService,
    private messageService: MessageService,
    public dialogRef: MatDialogRef<PlanConnectionNoComponent>,
    @Inject(MAT_DIALOG_DATA) public data: fetchData
  ) {}

  ngOnInit(): void {
    if (this.data) {
      this.planForConnection = this.data.planForConnectionData;
    }

    // $("#planConnectionNo").modal("show");
  }

  closeModal() {
    // this.closeDialog.emit();
    // $("#planConnectionNo").modal("hide");
  }
}
