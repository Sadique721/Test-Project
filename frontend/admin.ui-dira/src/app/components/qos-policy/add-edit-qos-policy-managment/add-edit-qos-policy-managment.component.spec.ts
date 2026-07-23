import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditQosPolicyManagmentComponent } from './add-edit-qos-policy-managment.component';

describe('AddEditQosPolicyManagmentComponent', () => {
  let component: AddEditQosPolicyManagmentComponent;
  let fixture: ComponentFixture<AddEditQosPolicyManagmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditQosPolicyManagmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditQosPolicyManagmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
