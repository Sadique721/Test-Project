import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { AcctProfileComponent } from "./acct-profile.component";
import { AcctProfileRoutingModule } from "./acct-profile-routing.module";
import { AcctProfileListComponent } from "./acct-profile-list/acct-profile-list.component";
import { AcctProfileCreateComponent } from "./acct-profile-create/acct-profile-create.component";
import { ReactiveFormsModule } from "@angular/forms";
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
const routes = [{ path: "", component: AcctProfileComponent }];

@NgModule({
    declarations: [AcctProfileComponent, AcctProfileListComponent, AcctProfileCreateComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, AcctProfileRoutingModule, ReactiveFormsModule, MatTableModule
        , MatPaginatorModule, MatSortModule, MatIconModule, MatButtonModule, MatCardContent, MatCardModule, MatPaginator, MatDialogModule, MatCheckbox,
        MatSlideToggleModule],
})
export class AcctProfileModule { }
