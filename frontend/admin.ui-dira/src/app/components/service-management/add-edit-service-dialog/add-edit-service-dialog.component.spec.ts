import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditServiceDialogComponent } from './add-edit-service-dialog.component';

describe('AddEditServiceDialogComponent', () => {
  let component: AddEditServiceDialogComponent;
  let fixture: ComponentFixture<AddEditServiceDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditServiceDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditServiceDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
