import { ComponentFixture, TestBed } from "@angular/core/testing";

import { CustomerOldComponent } from "./customer-old.component";

describe("CustomerOldComponent", () => {
  let component: CustomerOldComponent;
  let fixture: ComponentFixture<CustomerOldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CustomerOldComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomerOldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
