import { TestBed } from '@angular/core/testing';

import { WextService } from './wext.service';

describe('WextService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: WextService = TestBed.get(WextService);
    expect(service).toBeTruthy();
  });
});
