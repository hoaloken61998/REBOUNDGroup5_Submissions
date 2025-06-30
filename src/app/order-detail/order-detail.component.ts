import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Database, ref, get, DataSnapshot, update } from '@angular/fire/database';

// Interfaces for Firebase data structures based on provided images
export interface OrderData {
  id?: string; // Firebase key for the order
  OrderID: number;
  DeliveryFee: number;
  DiscountValue: number;
  OrderDate: string;
  PaymentMethodID: number;
  Status: string;
  Subtotal: number;
  TotalAmount: number;
  UserID: number;
  UserPromotion: number;
  // Note: No explicit 'Address' or 'Special Note/Request' in provided Order/User images.
  // We'll map customer address from User if available, otherwise display N/A or static.
  // For 'Special Note/Request', it will be static for now or marked N/A.
}

export interface OrderItemData {
  id?: string; // Firebase key for the order item
  OrderID: number;
  OrderItemID: number;
  Price: number; // Price per unit for this specific order item (could be different from ProductPrice)
  ProductID: number;
  Quantity: number;
  Rating: number;
  UserID: number;
}

export interface UserData {
  id?: string; // Firebase key for the user
  UserID: number;
  FullName: string;
  PhoneNumber: string;
  Email: string;
  // Add other fields from User image if needed, e.g., AvatarURL, DateOfBirth
  // Note: No explicit 'Address' field in provided User image.
}

export interface ProductData {
  id?: string; // Firebase key for the product
  ProductID: number;
  ProductName: string; // Assuming 'ProductName' exists in your Firebase 'Product' node
  ProductDescription: string;
  ProductPrice: number; // Base price of the product
  // Add other fields from Product image if needed, e.g., CategoryID, ImageLink
}

// Interface for displaying combined OrderItem and Product details
export interface DisplayOrderItem {
  OrderItemID: number;
  ProductID: number;
  ProductName: string;
  Quantity: number;
  UnitPrice: number; // Price from OrderItem
  ItemSubTotal: number;
}

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [RouterModule, CommonModule], // Import CommonModule for ngIf, ngFor
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.css'
})
export class OrderDetailComponent implements OnInit {
  order = signal<OrderData | null>(null);
  customer = signal<UserData | null>(null);
  displayOrderItems = signal<DisplayOrderItem[]>([]);
  orderIdFromRoute: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private db: Database
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.orderIdFromRoute = params.get('id'); // 'id' should match the route parameter name
      if (this.orderIdFromRoute) {
        this.loadOrderDetails(this.orderIdFromRoute);
      } else {
        console.error('Order ID not found in route parameters.');
        // Optionally navigate back or show an error message
      }
    });
  }

  async loadOrderDetails(orderId: string): Promise<void> {
    try {
      // 1. Fetch Order Data
      const orderRef = ref(this.db, `Order/${orderId}`);
      const orderSnapshot: DataSnapshot = await get(orderRef);

      if (orderSnapshot.exists()) {
        const orderData: OrderData = { id: orderSnapshot.key, ...orderSnapshot.val() };
        this.order.set(orderData);
        console.log('Order Data:', orderData);

        // 2. Fetch Customer Data (using UserID from Order)
        if (orderData.UserID !== undefined) {
          const userSnapshot = await get(ref(this.db, 'User'));
          if (userSnapshot.exists()) {
            const allUsers: UserData[] = Object.values(userSnapshot.val());
            const customerData = allUsers.find(user => user.UserID === orderData.UserID);
            this.customer.set(customerData || null);
            console.log('Customer Data:', customerData);
          } else {
            console.warn('Node "User" không tồn tại hoặc rỗng.');
          }
        }

        // 3. Fetch OrderItem Data (filter by OrderID)
        const orderItemsRef = ref(this.db, 'OrderItem');
        const orderItemsSnapshot: DataSnapshot = await get(orderItemsRef);
        const fetchedOrderItems: OrderItemData[] = [];
        if (orderItemsSnapshot.exists()) {
          Object.values(orderItemsSnapshot.val()).forEach((item: any) => {
            // Đảm bảo OrderID khớp chính xác
            if (item.OrderID === orderData.OrderID) {
              fetchedOrderItems.push(item);
            }
          });
        }
        console.log('Fetched Order Items:', fetchedOrderItems);

        // 4. Fetch Product Data for each OrderItem and combine
        const productPromises = fetchedOrderItems.map(async item => {
          // Lấy TẤT CẢ sản phẩm để tìm theo ProductID
          const productsSnapshot = await get(ref(this.db, 'Product'));
          let productData: ProductData | undefined;
          if (productsSnapshot.exists()) {
            const allProducts: ProductData[] = Object.values(productsSnapshot.val());
            // Tìm sản phẩm có ProductID khớp
            productData = allProducts.find(p => p.ProductID === item.ProductID);
          }
          
          if (productData) {
            return {
              OrderItemID: item.OrderItemID,
              ProductID: item.ProductID,
              ProductName: productData.ProductName || 'N/A', // Lấy ProductName từ Product
              Quantity: item.Quantity,
              UnitPrice: item.Price, // Lấy Price từ OrderItem (giá đơn vị cho item này)
              ItemSubTotal: item.Quantity * item.Price
            } as DisplayOrderItem;
          } else {
            console.warn(`Sản phẩm với ProductID ${item.ProductID} không tồn tại.`);
            return {
              OrderItemID: item.OrderItemID,
              ProductID: item.ProductID,
              ProductName: 'Unknown Product', // Fallback nếu không tìm thấy sản phẩm
              Quantity: item.Quantity,
              UnitPrice: item.Price,
              ItemSubTotal: item.Quantity * item.Price
            } as DisplayOrderItem;
          }
        });

        const combinedOrderItems = await Promise.all(productPromises);
        this.displayOrderItems.set(combinedOrderItems);
        console.log('Display Order Items:', this.displayOrderItems());

      } else {
        console.error(`Order with ID ${orderId} not found.`);
        this.order.set(null); // Clear order if not found
      }
    } catch (error) {
      console.error('Lỗi khi tải chi tiết đơn hàng:', error);
      this.order.set(null);
    }
  }

  formatCurrency(value: number | undefined): string {
    if (value === undefined || value === null) {
      return 'N/A';
    }
    // Using Vietnamese locale for currency formatting (VND)
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  }

  async confirmOrder(): Promise<void> {
    if (this.orderIdFromRoute && this.order()) {
      try {
        const orderRef = ref(this.db, `Order/${this.orderIdFromRoute}`);
        await update(orderRef, { Status: 'Confirmed' });
        this.order.update(currentOrder => (currentOrder ? { ...currentOrder, Status: 'Confirmed' } : currentOrder));
        alert("Đơn hàng đã được xác nhận!");
      } catch (error) {
        console.error('Lỗi khi xác nhận đơn hàng:', error);
        alert('Đã xảy ra lỗi khi xác nhận đơn hàng.');
      }
    } else {
      alert("Không có đơn hàng để xác nhận.");
    }
  }

  async cancelOrder(): Promise<void> {
    if (this.orderIdFromRoute && this.order()) {
      try {
        const orderRef = ref(this.db, `Order/${this.orderIdFromRoute}`);
        await update(orderRef, { Status: 'Cancelled' });
        this.order.update(currentOrder => (currentOrder ? { ...currentOrder, Status: 'Cancelled' } : currentOrder));
        alert("Đơn hàng đã được hủy!");
      } catch (error) {
        console.error('Lỗi khi hủy đơn hàng:', error);
        alert('Đã xảy ra lỗi khi hủy đơn hàng.');
      }
    } else {
      alert("Không có đơn hàng để hủy.");
    }
  }

  // Phương thức goBack() để xử lý việc quay lại trang trước
  goBack(): void {
    window.history.back();
  }
}
