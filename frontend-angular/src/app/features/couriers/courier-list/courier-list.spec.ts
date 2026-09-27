import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CourierList } from './courier-list';

describe('CourierList', () => {
  let component: CourierList;
  let fixture: ComponentFixture<CourierList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourierList],
    }).compileComponents();

    fixture = TestBed.createComponent(CourierList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
