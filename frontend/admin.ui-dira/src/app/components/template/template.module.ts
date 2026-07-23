// import { NgModule } from "@angular/core";
// import { CommonModule } from "@angular/common";
// import { RouterModule } from "@angular/router";
// import { SharedModule } from "src/app/shared/shared.module";
// import { TemplateComponent } from "./template.component";

// const routes = [{ path: "", component: TemplateComponent }];

// @NgModule({
//   declarations: [TemplateComponent],
//   imports: [CommonModule, RouterModule.forChild(routes), SharedModule],
// })
// export class TemplateModule {}
import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { TemplateComponent } from "./template.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckboxModule } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";

const routes = [
  { path: "", component: TemplateComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [TemplateComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    SharedModule,
    MatCardModule,
    MatPaginatorModule,
    MatDialogModule,
    MatCheckboxModule,
  ],
})
export class TemplateModule {}
