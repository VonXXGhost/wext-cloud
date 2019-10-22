import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FollowingFollowerComponent } from './following-follower.component';

describe('FollowingFollowerComponent', () => {
  let component: FollowingFollowerComponent;
  let fixture: ComponentFixture<FollowingFollowerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FollowingFollowerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FollowingFollowerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
