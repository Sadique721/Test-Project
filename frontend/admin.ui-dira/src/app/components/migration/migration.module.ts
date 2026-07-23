import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { MigrationComonent } from "./migration.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatDialogModule } from "@angular/material/dialog";

const routes = [{ path: "", component: MigrationComonent }];

@NgModule({
    declarations: [MigrationComonent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardModule, MatCardContent, MatDialogModule]
})
export class MigrationModule { }
