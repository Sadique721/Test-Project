import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditPincodeManagmentComponent } from './add-edit-pincode-managment.component';

describe('AddEditPincodeManagmentComponent', () => {
  let component: AddEditPincodeManagmentComponent;
  let fixture: ComponentFixture<AddEditPincodeManagmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditPincodeManagmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditPincodeManagmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
