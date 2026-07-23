import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditCityManagmentComponent } from './add-edit-city-managment.component';

describe('AddEditCityManagmentComponent', () => {
  let component: AddEditCityManagmentComponent;
  let fixture: ComponentFixture<AddEditCityManagmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditCityManagmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditCityManagmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
