import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditDepartmentManagmentComponent } from './add-edit-department-managment.component';

describe('AddEditDepartmentManagmentComponent', () => {
  let component: AddEditDepartmentManagmentComponent;
  let fixture: ComponentFixture<AddEditDepartmentManagmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditDepartmentManagmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditDepartmentManagmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
