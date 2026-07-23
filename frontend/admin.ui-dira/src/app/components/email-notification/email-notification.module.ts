import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { EmailNotificationComponent } from "./email-notification.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';


const routes = [
  { path: "", component: EmailNotificationComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [EmailNotificationComponent],
  imports: [CommonModule, RouterModule.forChild(routes), SharedModule,
    MatCardContent,MatCardModule,MatPaginator,MatDialogModule,MatCheckbox,ReactiveFormsModule,MatTableModule,MatPaginatorModule,
    MatSortModule,MatIconModule,MatButtonModule,
  ],
})
export class EmailNotificationModule {}
