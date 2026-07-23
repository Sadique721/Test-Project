import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewDetailCardComponent } from './view-detail-card.component';

describe('ViewDetailCardComponent', () => {
  let component: ViewDetailCardComponent;
  let fixture: ComponentFixture<ViewDetailCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewDetailCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewDetailCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
