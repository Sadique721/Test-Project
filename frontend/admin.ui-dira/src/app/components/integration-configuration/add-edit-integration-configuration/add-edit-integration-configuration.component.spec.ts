import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditIntegrationConfigurationComponent } from './add-edit-integration-configuration.component';

describe('AddEditIntegrationConfigurationComponent', () => {
  let component: AddEditIntegrationConfigurationComponent;
  let fixture: ComponentFixture<AddEditIntegrationConfigurationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddEditIntegrationConfigurationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddEditIntegrationConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
