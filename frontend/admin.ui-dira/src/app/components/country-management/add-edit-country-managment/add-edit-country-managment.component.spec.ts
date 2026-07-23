import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditCountryManagmentComponent } from './add-edit-country-managment.component';

describe('AddEditCountryManagmentComponent', () => {
  let component: AddEditCountryManagmentComponent;
  let fixture: ComponentFixture<AddEditCountryManagmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditCountryManagmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditCountryManagmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
