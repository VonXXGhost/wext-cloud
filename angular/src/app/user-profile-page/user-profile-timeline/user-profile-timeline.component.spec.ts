import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProfileTimelineComponent } from './user-profile-timeline.component';

describe('UserProfileTimelineComponent', () => {
  let component: UserProfileTimelineComponent;
  let fixture: ComponentFixture<UserProfileTimelineComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserProfileTimelineComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileTimelineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
