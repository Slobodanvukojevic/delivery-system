import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LockerList } from './locker-list';

describe('LockerList', () => {
  let component: LockerList;
  let fixture: ComponentFixture<LockerList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LockerList],
    }).compileComponents();

    fixture = TestBed.createComponent(LockerList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
