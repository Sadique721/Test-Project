import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { PasswordPolicyComponent } from "./password-policy.component";
import { PasswordCreateComponent } from "./password-create/password-create.component";
import { PasswordListComponent } from "./password-list/password-list.component";
import { PasswordRoutingModule } from "./password-policy-routing.module";
import { SharedModule } from "src/app/shared/shared.module";
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

const routes = [{ path: "", component: PasswordPolicyComponent }];

@NgModule({
  declarations: [PasswordPolicyComponent, PasswordCreateComponent, PasswordListComponent],
  imports: [CommonModule, RouterModule.forChild(routes), PasswordRoutingModule, SharedModule,
    ReactiveFormsModule,MatTableModule ,MatPaginatorModule,MatSortModule,MatIconModule,MatButtonModule,
    MatCardModule,MatCardContent,MatPaginator,MatDialogModule,MatCheckbox
  ]
})
export class PasswordPolicyModule {}
