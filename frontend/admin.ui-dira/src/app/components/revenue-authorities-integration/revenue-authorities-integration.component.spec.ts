import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RevenueAuthoritiesIntegrationComponent } from './revenue-authorities-integration.component';

describe('RevenueAuthoritiesIntegrationComponent', () => {
  let component: RevenueAuthoritiesIntegrationComponent;
  let fixture: ComponentFixture<RevenueAuthoritiesIntegrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RevenueAuthoritiesIntegrationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RevenueAuthoritiesIntegrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
