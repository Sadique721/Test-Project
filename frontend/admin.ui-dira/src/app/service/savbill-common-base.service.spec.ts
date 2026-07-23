import { TestBed } from '@angular/core/testing';

import { SavbillCommonBaseService } from './savbill-common-base.service';

describe('SavbillCommonBaseServiceService', () => {
  let service: SavbillCommonBaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SavbillCommonBaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
