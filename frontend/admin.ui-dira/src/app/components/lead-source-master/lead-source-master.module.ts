// import { NgModule } from "@angular/core";
// import { CommonModule } from "@angular/common";
// import { RouterModule } from "@angular/router";
// import { SharedModule } from "src/app/shared/shared.module";
// import { LeadSourceMasterComponent } from "./lead-source-master.component";
// import { DeactivateService } from "src/app/service/deactivate.service";

// // Importing Angular Material modules similarly to the example
// import { MatCardContent, MatCardModule } from "@angular/material/card";
// import { MatPaginator } from "@angular/material/paginator";
// import { MatDialogModule } from "@angular/material/dialog";
// import { MatCheckbox } from "@angular/material/checkbox";

// import { FormsModule, ReactiveFormsModule } from "@angular/forms";
// import { AddEditLeadSourceMasterComponent } from "./add-edit-lead-source-master/add-edit-lead-source-master.component";
// import { MatTableModule } from "@angular/material/table";

// const routes = [
//   { path: "", component: LeadSourceMasterComponent, canDeactivate: [DeactivateService] },
// ];

// @NgModule({
//   declarations: [LeadSourceMasterComponent, AddEditLeadSourceMasterComponent],
//   imports: [
//     CommonModule,
//     ReactiveFormsModule,
//     RouterModule.forChild(routes),
//     SharedModule,
//     MatCardContent,
//     FormsModule,
//     MatCardModule,
//     MatTableModule,
//     MatPaginator,
//     MatDialogModule,
//     MatCheckbox,
//   ],
// })
// export class LeadSourceMasterModule { }
import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { LeadSourceMasterComponent } from "./lead-source-master.component";
import { DeactivateService } from "src/app/service/deactivate.service";

// Angular Material Modules imports (modules only)
import { MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatTableModule } from "@angular/material/table";

import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { AddEditLeadSourceMasterComponent } from "./add-edit-lead-source-master/add-edit-lead-source-master.component";
import { MaterialModule } from "src/app/material.module";

const routes = [
    { path: "", component: LeadSourceMasterComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [LeadSourceMasterComponent, AddEditLeadSourceMasterComponent],
    imports: [
        CommonModule,
        ReactiveFormsModule,
        FormsModule,
        RouterModule.forChild(routes),
        SharedModule,
        MatCardModule,
        MatTableModule,
        MatPaginatorModule,
        MatDialogModule,
        MatCheckboxModule,
    ],
})
export class LeadSourceMasterModule { }
