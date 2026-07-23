import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditServiceAreaManagmentComponent } from './add-edit-service-area-managment.component';

describe('AddEditServiceAreaManagmentComponent', () => {
  let component: AddEditServiceAreaManagmentComponent;
  let fixture: ComponentFixture<AddEditServiceAreaManagmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditServiceAreaManagmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditServiceAreaManagmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
