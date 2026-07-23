import { Component, OnInit, Input, Inject } from "@angular/core";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { SharedModule } from "src/app/shared/shared.module";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
export interface fetchData {
  promiseToPayData: any;
}
@Component({
  selector: "app-promisetopay-details-modal",
  templateUrl: "./promisetopay-details-modal.component.html",
  styleUrls: ["./promisetopay-details-modal.component.css"],
  standalone: true,
  imports: [SharedModule]
})
export class PromiseToPayDetailsModalComponent implements OnInit {
  // @Input() dialogId: string;
  // @Input() promiseToPayData: any;

  startDate;
  endDate;
  days;
  promiseToPayData: any;
  constructor(
    private messageService: MessageService,
    private spinner: NgxSpinnerService,
    public dialogRef: MatDialogRef<PromiseToPayDetailsModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: fetchData
  ) {}

  ngOnInit(): void {
    if (this.data) {
      this.promiseToPayData = this.data.promiseToPayData;
    }
  }
}
