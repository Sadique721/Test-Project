import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { RouterModule } from "@angular/router";
import { NgxSpinnerModule } from "ngx-spinner";

// 🔹 Material modules
import { MatButtonToggleModule } from "@angular/material/button-toggle";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatSidenavModule } from "@angular/material/sidenav";
import { MatListModule } from "@angular/material/list";
import { MatDividerModule } from "@angular/material/divider";
import { TablerIconsModule } from "angular-tabler-icons";
import { IconHome } from "angular-tabler-icons/icons";

import { MatSelectModule } from "@angular/material/select";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatDatepicker } from "@angular/material/datepicker";
import { MatRadioModule } from "@angular/material/radio";
import { MatMenuModule } from "@angular/material/menu";
import { NgScrollbarModule } from "ngx-scrollbar";

import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatSortModule } from "@angular/material/sort";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTabsModule } from "@angular/material/tabs";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatCardContent } from "@angular/material/card";
import { MatCheckboxModule } from "@angular/material/checkbox";

import { MatPaginator } from "@angular/material/paginator";
import { MatCheckbox } from "@angular/material/checkbox";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule, provideNativeDateAdapter } from "@angular/material/core";
import { MatTimepickerModule } from "@angular/material/timepicker";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogModule,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatStepperModule } from "@angular/material/stepper";
// 🔹 Your shared components
// import { AppBreadcrumbComponent } from "./components/breadcrumb/breadcrumb.component";

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    NgxSpinnerModule,

    // Material
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatDividerModule,
    MatButtonToggleModule,
    TablerIconsModule.pick({ IconHome }),
    MatSelectModule,
    MatSlideToggleModule,
    NgScrollbarModule,
    MatSnackBarModule,
    MatDatepicker,
    MatRadioModule,
    MatMenuModule,
    MatPaginator,
    MatCardModule,
    MatCheckbox,
    MatCardContent,
    MatDatepickerModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatButtonModule,
    MatTableModule,
    MatTimepickerModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatTabsModule,
    MatTooltipModule,
    MatDialogModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatStepperModule,
    MatInputModule,
    MatFormFieldModule
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    NgxSpinnerModule,

    // Material
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatDividerModule,
    MatButtonToggleModule,
    MatSelectModule,
    MatSlideToggleModule,
    NgScrollbarModule,
    MatSnackBarModule,
    MatDatepicker,
    MatRadioModule,
    MatMenuModule,
    MatPaginator,
    MatCardModule,
    MatCheckbox,
    MatCardContent,
    MatDatepickerModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatButtonModule,
    MatTableModule,
    MatTimepickerModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatTabsModule,
    MatTooltipModule,
    MatDialogModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatStepperModule,
    MatInputModule,
    MatFormFieldModule
  ]
})
export class SharedModule {}
