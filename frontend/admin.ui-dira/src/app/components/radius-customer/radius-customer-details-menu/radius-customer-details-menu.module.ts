import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { SharedModule } from "src/app/shared/shared.module";
import { CardModule } from "primeng/card";
import { RadiusCustomerDetailsMenuRoutingModule } from "./radius-customer-details-menu-routing.module";
import { RadiusCustomerDetailsComponent } from "../radius-customer-details/radius-customer-details.component";
import { RadiusCustomerPlansComponent } from "../radius-customer-plans/radius-customer-plans.component";
import { RadiusCustomerCDRSessionsComponent } from "../radius-customer-cdr-sessions/radius-customer-cdr-sessions.component";
import { RadiusCustomerDetailsMenuComponent } from "./radius-customer-details-menu.component";
import { SplitterModule } from "primeng/splitter";
import { AccordionModule } from "primeng/accordion";
import { DialogModule } from "primeng/dialog";
import { TableModule } from "primeng/table";
import { ButtonModule } from "primeng/button";
import { MatTab } from "@angular/material/tabs";
// -------------
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogModule, MatDialogTitle } from "@angular/material/dialog";
import { MatCheckbox } from "@angular/material/checkbox";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { MatTabsModule } from "@angular/material/tabs";
import { MatTableModule } from "@angular/material/table";
import { MatTooltipModule } from "@angular/material/tooltip";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTimepickerModule } from "@angular/material/timepicker";

@NgModule({
  imports: [
    DialogModule,
    CommonModule,
    RadiusCustomerDetailsMenuRoutingModule,
    SharedModule,
    CardModule,
    SplitterModule,
    AccordionModule,
    TableModule,
    ButtonModule,
    MatTab,
    MatCardContent, MatCardModule,
        MatPaginator, MatDialogModule, MatCheckbox,MatDatepickerModule,
         MatTabsModule,
    MatTableModule,
    MatPaginatorModule,
    MatCardModule,
    MatTooltipModule,
    ReactiveFormsModule,
    FormsModule,
    MatIconModule,
    MatDatepickerModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatButtonModule,
    MatTimepickerModule
],
  declarations: [
    RadiusCustomerDetailsMenuComponent,
    RadiusCustomerDetailsComponent,
    RadiusCustomerPlansComponent,
    RadiusCustomerCDRSessionsComponent,
  ],
})
export class RadiusCustomerDetailsMenuModule {}
