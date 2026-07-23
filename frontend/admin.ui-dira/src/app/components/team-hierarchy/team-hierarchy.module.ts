import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { TeamHierarchyComponent } from "./team-hierarchy.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { DialogModule } from "primeng/dialog";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogActions, MatDialogContent, MatDialogModule } from "@angular/material/dialog";

const routes = [
  { path: "", component: TeamHierarchyComponent, canDeactivate: [DeactivateService] }
];

@NgModule({
  declarations: [TeamHierarchyComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedModule,
    DialogModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatDialogModule,
    MatPaginatorModule,
    MatDialogContent,
    MatDialogActions
  ]
})
export class TeamHierarchyModule {}

// import { NgModule } from "@angular/core";
// import { CommonModule } from "@angular/common";
// import { RouterModule } from "@angular/router";
// import { SharedModule } from "src/app/shared/shared.module";
// import { TeamHierarchyComponent } from "./team-hierarchy.component";
// import { DeactivateService } from "src/app/service/deactivate.service";
// import { DialogModule } from "primeng/dialog";
// import { MatCardContent, MatCardModule } from "@angular/material/card";
// import { MatPaginator } from "@angular/material/paginator";
// import { MatDialogModule } from '@angular/material/dialog';
// import { MatCheckbox } from "@angular/material/checkbox";
// import { ReactiveFormsModule } from "@angular/forms";
// import { MatCheckboxModule } from "@angular/material/checkbox";
// import { MatTableModule } from "@angular/material/table";
// import { MatSortModule } from "@angular/material/sort";

// const routes = [
//   { path: "", component: TeamHierarchyComponent, canDeactivate: [DeactivateService] },
// ];

// @NgModule({
//   declarations: [TeamHierarchyComponent],
//   imports: [CommonModule, RouterModule.forChild(routes), SharedModule,
// DialogModule,
// MatCardContent,
// MatCardModule,
// MatPaginator,
// MatDialogModule,
// MatCheckbox,
// ReactiveFormsModule,
// MatCheckboxModule,
// MatTableModule,
// MatSortModule,    ],
// })
// export class TeamHierarchyModule {}
