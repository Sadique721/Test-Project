import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditSubareaManagementComponent } from './add-edit-subarea-management.component';

describe('AddEditSubareaManagementComponent', () => {
  let component: AddEditSubareaManagementComponent;
  let fixture: ComponentFixture<AddEditSubareaManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditSubareaManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditSubareaManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
