import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditLeadSourceMasterComponent } from './add-edit-lead-source-master.component';

describe('AddEditLeadSourceMasterComponent', () => {
  let component: AddEditLeadSourceMasterComponent;
  let fixture: ComponentFixture<AddEditLeadSourceMasterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditLeadSourceMasterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditLeadSourceMasterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
