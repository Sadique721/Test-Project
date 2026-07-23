import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewMappingDetailsComponent } from './view-mapping-details.component';

describe('ViewMappingDetailsComponent', () => {
  let component: ViewMappingDetailsComponent;
  let fixture: ComponentFixture<ViewMappingDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewMappingDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewMappingDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
