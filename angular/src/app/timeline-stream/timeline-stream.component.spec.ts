import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TimelineStreamComponent } from './timeline-stream.component';

describe('TimelineStreamComponent', () => {
  let component: TimelineStreamComponent;
  let fixture: ComponentFixture<TimelineStreamComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TimelineStreamComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TimelineStreamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
