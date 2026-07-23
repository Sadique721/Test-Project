// import { NgModule } from "@angular/core";
// import { CommonModule } from "@angular/common";
// import { RouterModule } from "@angular/router";
// import { SharedModule } from "src/app/shared/shared.module";
// import { ProductCategoryManagementComponent } from "./product-category-management.component";
// import { DeactivateService } from "src/app/service/deactivate.service";
// import { FormsModule } from "@angular/forms";
// import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
// import { DialogModule } from "primeng/dialog";

// const routes = [
//   { path: "", component: ProductCategoryManagementComponent, canDeactivate: [DeactivateService] },
// ];

// @NgModule({ declarations: [ProductCategoryManagementComponent], imports: [CommonModule,
//         RouterModule.forChild(routes),
//         FormsModule,
//         SharedModule,
//         DialogModule], providers: [provideHttpClient(withInterceptorsFromDi())] })
// export class ProductCategoryManagementModule {}
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ProductCategoryManagementComponent } from './product-category-management.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { DeactivateService } from 'src/app/service/deactivate.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { DialogModule } from 'primeng/dialog';
import { TableModule } from 'primeng/table';
import { CheckboxModule } from 'primeng/checkbox';
import { PaginatorModule } from 'primeng/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSortModule } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckboxModule } from '@angular/material/checkbox';

const routes = [
  {
    path: '',
    component: ProductCategoryManagementComponent,
    canDeactivate: [DeactivateService],
  },
];

@NgModule({
  declarations: [ProductCategoryManagementComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedModule,
    FormsModule,
    ReactiveFormsModule,
    DialogModule,
    TableModule,
    CheckboxModule,
    PaginatorModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    MatSortModule,
    MatDialogModule,
  ],
  providers: [provideHttpClient(withInterceptorsFromDi())],
})
export class ProductCategoryManagementModule { }
