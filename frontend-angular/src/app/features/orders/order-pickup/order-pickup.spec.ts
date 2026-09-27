import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OrderPickup } from './order-pickup';

describe('OrderPickup', () => {
  let component: OrderPickup;
  let fixture: ComponentFixture<OrderPickup>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderPickup],
    }).compileComponents();

    fixture = TestBed.createComponent(OrderPickup);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
