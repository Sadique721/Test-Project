import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketmanagementComponent } from './ticketmanagement.component';

describe('TicketmanagementComponent', () => {
  let component: TicketmanagementComponent;
  let fixture: ComponentFixture<TicketmanagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketmanagementComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TicketmanagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
