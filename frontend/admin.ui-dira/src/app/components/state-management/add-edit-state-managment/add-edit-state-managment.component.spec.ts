import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditStateManagmentComponent } from './add-edit-state-managment.component';

describe('AddEditStateManagmentComponent', () => {
  let component: AddEditStateManagmentComponent;
  let fixture: ComponentFixture<AddEditStateManagmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditStateManagmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditStateManagmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
