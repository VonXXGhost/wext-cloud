import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WextPageComponent } from './wext-page.component';

describe('WextPageComponent', () => {
  let component: WextPageComponent;
  let fixture: ComponentFixture<WextPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WextPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WextPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
