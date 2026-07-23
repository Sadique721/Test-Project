import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditPrepaidRejectedReasonMasterComponent  } from './add-edit-prepaid-rejected-reason-master.component';

describe('AddEditPrepaidRejectedReasonMasterComponent', () => {
  let component: AddEditPrepaidRejectedReasonMasterComponent ;
  let fixture: ComponentFixture<AddEditPrepaidRejectedReasonMasterComponent >;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditPrepaidRejectedReasonMasterComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditPrepaidRejectedReasonMasterComponent );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
