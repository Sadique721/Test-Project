import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddServiceParameterDialogComponent } from './add-service-parameter-dialog.component';

describe('AddServiceParameterDialogComponent', () => {
  let component: AddServiceParameterDialogComponent;
  let fixture: ComponentFixture<AddServiceParameterDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddServiceParameterDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddServiceParameterDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
