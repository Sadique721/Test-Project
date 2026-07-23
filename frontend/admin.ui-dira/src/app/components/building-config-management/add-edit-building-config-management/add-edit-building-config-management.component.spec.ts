import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditBuildingConfigManagementComponent } from './add-edit-building-config-management.component';

describe('AddEditBuildingConfigManagementComponent', () => {
  let component: AddEditBuildingConfigManagementComponent;
  let fixture: ComponentFixture<AddEditBuildingConfigManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditBuildingConfigManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditBuildingConfigManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
