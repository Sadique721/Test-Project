import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditRejectedReasonMasterComponent } from './add-edit-rejected-reason-master.component';

describe('AddEditRejectedReasonMasterComponent', () => {
  let component: AddEditRejectedReasonMasterComponent;
  let fixture: ComponentFixture<AddEditRejectedReasonMasterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditRejectedReasonMasterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditRejectedReasonMasterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
