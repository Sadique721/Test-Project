import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { SmsNotificationComponent } from "./sms-notification.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
const routes = [
  { path: "", component: SmsNotificationComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [SmsNotificationComponent],
  imports: [CommonModule, ReactiveFormsModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule,
        MatPaginator, MatDialogModule, MatCheckbox],
})
export class SmsNotificationModule {}
