// src/app/dashboard/dashboard.component.ts
import { Component, OnInit, OnDestroy, signal, Inject, LOCALE_ID } from '@angular/core';
import { CommonModule, formatDate, formatCurrency } from '@angular/common';
import { Database, ref, onValue, off, DataSnapshot } from '@angular/fire/database';

// Interfaces to match Firebase data structures
export interface OrderData {
  OrderID?: number;
  OrderDate?: string;
  Status?: string;
  Subtotal?: number;
  TotalAmount?: number;
  UserID?: number;
  // Add other fields from Order node if needed
}

export interface OrderItemData {
  OrderItemID?: number;
  OrderID?: number;
  ProductID?: number;
  Quantity?: number;
  Price?: number;
  // Add other fields from OrderItem node
}

export interface ProductData {
  ProductID?: number;
  ProductName?: string;
  ProductDescription?: string;
  ProductPrice?: string; // Stored as string, will need parsing
  // Add other fields from Product node
}

export interface BookingScheduleData {
  BookingID?: number;
  BookingTime?: string;
  Status?: string;
  BranchID?: number; // Linked to Branch node
  ServiceID?: number;
  UserID?: number;
  // Add other fields from BookingSchedule node
}

export interface UserData {
  UserID?: number;
  FullName?: string;
  // Add other user fields if needed
}

export interface BranchData {
  BranchID?: number;
  BranchName?: string;
  Details?: string;
  Street?: string;
  District?: string;
  Province?: string;
  BranchDisplayName?: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  // Summary Metrics
  totalRevenue = signal(0);
  totalOrders = signal(0);
  totalReservations = signal(0);
  
  // Data from Firebase (raw data, no longer processed for charts)
  private allOrders: OrderData[] = [];
  private allOrderItems: OrderItemData[] = [];
  private allProducts: ProductData[] = [];
  private allBookings: BookingScheduleData[] = [];
  private allBranches: BranchData[] = [];

  // Firebase listener cleanup functions
  private orderListener: (() => void) | undefined;
  private orderItemListener: (() => void) | undefined;
  private productListener: (() => void) | undefined;
  private bookingListener: (() => void) | undefined;
  private branchListener: (() => void) | undefined;

  constructor(
    private db: Database,
    @Inject(LOCALE_ID) private locale: string // Inject LOCALE_ID for currency formatting
  ) { }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  ngOnDestroy(): void {
    // Clean up Firebase listeners
    this.orderListener?.();
    this.orderItemListener?.();
    this.productListener?.();
    this.bookingListener?.();
    this.branchListener?.();
  }

  // Helper to format currency for display
  formatCurrencyDisplay(value: number | undefined): string {
    if (value === undefined || value === null || isNaN(value)) {
      return 'N/A';
    }
    // Formats to Vietnamese Dong (VND) with no decimal places
    return formatCurrency(value, this.locale, '₫', 'VND', '1.0-0');
  }

  // Load all necessary dashboard data from Firebase
  private loadDashboardData(): void {
    // Using onValue for real-time updates for dashboard metrics
    this.orderListener = onValue(ref(this.db, 'Order'), (snapshot: DataSnapshot) => {
      this.allOrders = Object.values(snapshot.val() || {});
      this.processOrderData(); // Recalculate summary metrics
    }, (error) => console.error('Error loading orders:', error));

    this.orderItemListener = onValue(ref(this.db, 'OrderItem'), (snapshot: DataSnapshot) => {
      this.allOrderItems = Object.values(snapshot.val() || {});
      // No chart to render, but you could process this data for other displays if needed
    }, (error) => console.error('Error loading order items:', error));

    this.productListener = onValue(ref(this.db, 'Product'), (snapshot: DataSnapshot) => {
      this.allProducts = Object.values(snapshot.val() || {});
      // No chart to render
    }, (error) => console.error('Error loading products:', error));

    this.bookingListener = onValue(ref(this.db, 'BookingSchedule'), (snapshot: DataSnapshot) => {
      this.allBookings = Object.values(snapshot.val() || {});
      this.processBookingData(); // Recalculate summary metrics
    }, (error) => console.error('Error loading bookings:', error));

    this.branchListener = onValue(ref(this.db, 'Branch'), (snapshot: DataSnapshot) => {
      const rawBranches = Object.values(snapshot.val() || {}) as BranchData[];
      this.allBranches = rawBranches.map(br => {
        const parts = [];
        if (br.Details) parts.push(br.Details);
        if (br.Street) parts.push(br.Street);
        if (br.District) parts.push(br.District);
        if (br.Province) parts.push(br.Province);
        let displayName = parts.join(', ');
        if (displayName.trim() === ',' || displayName.trim() === ',,' || displayName.trim() === ',,,') {
          displayName = '';
        }
        if (!displayName) {
          displayName = br.BranchName || 'N/A'; // Fallback to BranchName or 'N/A'
        }
        return { ...br, BranchDisplayName: displayName };
      });
      // No chart to render
    }, (error) => console.error('Error loading branches:', error));
  }

  // --- Data Processing Methods for Summary Metrics ---

  private processOrderData(): void {
    let revenue = 0;
    this.allOrders.forEach(order => {
      if (order.Status === 'Confirmed' && typeof order.TotalAmount === 'number') {
        revenue += order.TotalAmount;
      }
    });
    this.totalRevenue.set(revenue);
    this.totalOrders.set(this.allOrders.length);
  }

  private processBookingData(): void {
    this.totalReservations.set(this.allBookings.length);
  }

  // No chart rendering methods needed anymore
}
