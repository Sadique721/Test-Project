import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerTransfermodelComponent } from './customer-transfermodel.component';

describe('CustomerTransfermodelComponent', () => {
  let component: CustomerTransfermodelComponent;
  let fixture: ComponentFixture<CustomerTransfermodelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustomerTransfermodelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerTransfermodelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
