import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectBuildingDialogComponent } from './select-building-dialog.component';

describe('SelectBuildingDialogComponent', () => {
  let component: SelectBuildingDialogComponent;
  let fixture: ComponentFixture<SelectBuildingDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectBuildingDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectBuildingDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
