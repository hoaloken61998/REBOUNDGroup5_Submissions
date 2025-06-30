// src/app/add-order/add-order.component.ts
import { Component, OnInit, signal, Inject, LOCALE_ID } from '@angular/core';
import { CommonModule, formatCurrency } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Required for ngModel
import { Router } from '@angular/router';
import { Database, ref, push, get, set } from '@angular/fire/database';

// Re-use interfaces from order-detail and product-management for consistency
// Adjust paths if your interfaces are in different files or not exported from order-detail
import { OrderData, OrderItemData, UserData, ProductData, DisplayOrderItem } from '../order-detail/order-detail.component'; // Assuming these are defined there

@Component({
  selector: 'app-add-order',
  standalone: true,
  imports: [CommonModule, FormsModule], // Add FormsModule
  templateUrl: './add-order.component.html',
  styleUrl: './add-order.component.css'
})
export class AddOrderComponent implements OnInit {
  // Customer Data
  customerSearchTerm = signal('');
  customerSearchResults = signal<UserData[]>([]);
  selectedCustomer = signal<UserData | null>(null);
  customerAddress = signal(''); // Store customer address if available
  specialNote = signal(''); // Special note for the order

  // Product Data
  productSearchTerm = signal('');
  productSearchResults = signal<ProductData[]>([]);
  selectedProducts = signal<DisplayOrderItem[]>([]); // Items added to the current order

  // Order Details
  totalAmount = signal(0);
  subtotal = signal(0);
  deliveryFee = signal(30000); // Example fixed delivery fee
  discountValue = signal(0); // Example discount percentage
  shippingMethod = signal('standard');
  paymentMethod = signal('cash');

  // Static options for dropdowns (can be fetched from Firebase if dedicated nodes exist)
  shippingMethodsOptions = [
    { value: 'standard', name: 'Standard Shipping' },
    { value: 'express', name: 'Express Shipping' },
    { value: 'pickup', name: 'Store Pickup' },
  ];
  paymentMethodsOptions = [
    { value: 'cash', name: 'Cash on Delivery' },
    { value: 'card', name: 'Credit Card' },
    { value: 'momo', name: 'Momo' },
  ];

  private allUsers: UserData[] = []; // Cache all users for efficient search
  private allProducts: ProductData[] = []; // Cache all products for efficient search

  constructor(
    private router: Router,
    private db: Database,
    @Inject(LOCALE_ID) private locale: string // Inject LOCALE_ID for currency formatting
  ) {}

  ngOnInit(): void {
    // Load all users and products once for efficient lookups
    this.loadAllReferenceData();
    this.calculateTotals(); // Initial calculation on component load
  }

  // Loads all necessary reference data (Users, Products) from Firebase
  async loadAllReferenceData(): Promise<void> {
    try {
      // Fetch all users
      const userRef = ref(this.db, 'User');
      const userSnapshot = await get(userRef);
      if (userSnapshot.exists()) {
        this.allUsers = Object.values(userSnapshot.val());
      } else {
        console.warn('Node "User" does not exist or is empty.');
      }

      // Fetch all products
      const productRef = ref(this.db, 'Product');
      const productSnapshot = await get(productRef);
      if (productSnapshot.exists()) {
        this.allProducts = Object.values(productSnapshot.val());
      } else {
        console.warn('Node "Product" does not exist or is empty.');
      }
      console.log('Reference data loaded:', { users: this.allUsers, products: this.allProducts });
    } catch (error) {
      console.error('Error loading reference data:', error);
    }
  }

  // --- Customer Search & Selection Logic ---
  // Searches for customers based on the searchTerm
  async searchCustomers(): Promise<void> {
    const query = this.customerSearchTerm().toLowerCase().trim();
    if (query.length < 2) { // Require at least 2 characters for search to avoid too many results
      this.customerSearchResults.set([]);
      return;
    }
    const results = this.allUsers.filter(user =>
      user.FullName?.toLowerCase().includes(query) ||
      user.PhoneNumber?.toLowerCase().includes(query) ||
      user.Email?.toLowerCase().includes(query)
    );
    this.customerSearchResults.set(results);
  }

  // Selects a customer from the search results
  selectCustomer(customer: UserData): void {
    this.selectedCustomer.set(customer);
    // You might want to pre-fill customer address from their profile if it exists in UserData
    // For now, it's a placeholder. Adapt this based on your User model's address structure.
    this.customerAddress.set('123 Main Street, Hanoi (Example Address)'); 
    this.customerSearchResults.set([]); // Clear search results after selection
    this.customerSearchTerm.set(''); // Clear search term
  }

  // Removes the selected customer
  removeSelectedCustomer(): void {
    this.selectedCustomer.set(null);
    this.customerAddress.set('');
    this.specialNote.set('');
  }

  // --- Product Search & Addition Logic ---
  // Searches for products based on the searchTerm
  async searchProducts(): Promise<void> {
    const query = this.productSearchTerm().toLowerCase().trim();
    if (query.length < 2) { // Require at least 2 characters for search
      this.productSearchResults.set([]);
      return;
    }
    const results = this.allProducts.filter(product =>
      product.ProductName?.toLowerCase().includes(query) ||
      product.ProductDescription?.toLowerCase().includes(query)
    );
    this.productSearchResults.set(results);
  }

  // Adds a selected product to the current order
  addProductToOrder(product: ProductData): void {
    // Check if product already exists in the order to increment quantity
    const existingItem = this.selectedProducts().find(item => item.ProductID === product.ProductID);
    
    // Safely parse ProductPrice to a number
    // IMPORTANT FIX: Ensure product.ProductPrice is treated as a string before calling replace()
    const rawPriceString = String(product.ProductPrice || '0'); // Explicitly convert to string
    const parsedUnitPrice = parseFloat(
      rawPriceString.replace(/\./g, '').replace(',', '.')
    );
    const unitPrice = isNaN(parsedUnitPrice) ? 0 : parsedUnitPrice;

    if (existingItem) {
      this.selectedProducts.update(items => items.map(item =>
        item.ProductID === product.ProductID ? { ...item, Quantity: item.Quantity + 1, ItemSubTotal: (item.Quantity + 1) * item.UnitPrice } : item
      ));
    } else {
      // If not exists, add as a new item
      const newItem: DisplayOrderItem = {
        OrderItemID: 0, // Placeholder, actual ID will be a Firebase key
        ProductID: product.ProductID || 0,
        ProductName: product.ProductName || 'N/A',
        Quantity: 1,
        UnitPrice: unitPrice,
        ItemSubTotal: unitPrice // Quantity is 1 initially
      };
      this.selectedProducts.update(items => [...items, newItem]);
    }
    this.calculateTotals(); // Recalculate totals after adding product
    this.productSearchTerm.set(''); // Clear search term
    this.productSearchResults.set([]); // Clear search results
  }

  // Updates the quantity of an item in the order
  updateItemQuantity(item: DisplayOrderItem, newQuantity: number): void {
    this.selectedProducts.update(items => items.map(i => {
      if (i.ProductID === item.ProductID) {
        const quantity = Math.max(1, newQuantity || 1); // Ensure quantity is at least 1 and not NaN
        return { ...i, Quantity: quantity, ItemSubTotal: quantity * i.UnitPrice };
      }
      return i;
    }));
    this.calculateTotals(); // Recalculate totals after quantity change
  }

  // Removes an item from the order
  removeItemFromOrder(item: DisplayOrderItem): void {
    this.selectedProducts.update(items => items.filter(i => i.ProductID !== item.ProductID));
    this.calculateTotals(); // Recalculate totals after removing product
  }

  // --- Total Calculation Logic ---
  // Calculates subtotal and total amount for the order
  calculateTotals(): void {
    let currentSubtotal = 0;
    this.selectedProducts().forEach(item => {
      currentSubtotal += item.ItemSubTotal;
    });
    this.subtotal.set(currentSubtotal);

    let calculatedTotal = currentSubtotal + this.deliveryFee();
    if (this.discountValue() > 0) {
      calculatedTotal -= calculatedTotal * this.discountValue();
    }
    this.totalAmount.set(calculatedTotal);
  }

  // --- Form Submission & Reset ---
  // Confirms and saves the order to Firebase
  async confirmOrder(): Promise<void> {
    if (!this.selectedCustomer()) {
      alert('Please select a customer.');
      return;
    }
    if (this.selectedProducts().length === 0) {
      alert('Please add products to the order.');
      return;
    }

    try {
      // 1. Save Order to Firebase's 'Order' node
      const newOrderRef = push(ref(this.db, 'Order')); // Get a new unique Firebase key
      const firebaseOrderIdKey = newOrderRef.key; // This key will be the actual ID in Firebase

      if (!firebaseOrderIdKey) {
        console.error('Failed to generate order ID.');
        alert('An error occurred while creating the order. Please try again.');
        return;
      }

      // Generate a simple numeric OrderID from the Firebase key for consistency with your DB structure
      // This is a simple conversion; ensure it's unique enough or use a more robust method if needed.
      const numericOrderId = parseInt(firebaseOrderIdKey.replace(/[^0-9]/g, '').slice(-6) || '0');

      const orderPayload: OrderData = {
        OrderID: numericOrderId, // Numeric ID for Order (from Firebase key)
        DeliveryFee: this.deliveryFee(),
        DiscountValue: this.discountValue(),
        OrderDate: new Date().toISOString().slice(0, 19).replace('T', ' '), // Current date-time string (e.g., "2024-06-30 17:30:00")
        PaymentMethodID: 1, // Placeholder: Map this based on 'paymentMethod' selection
        Status: 'Pending', // Default status for new order
        Subtotal: this.subtotal(),
        TotalAmount: this.totalAmount(),
        UserID: this.selectedCustomer()?.UserID || 0, // Link to selected customer's internal UserID
        UserPromotion: 0, // Placeholder
      };
      await set(newOrderRef, orderPayload); // Save the order to Firebase
      console.log('Order saved successfully:', orderPayload);

      // 2. Save each OrderItem to Firebase's 'OrderItem' node
      const orderItemsPromises = this.selectedProducts().map(async (item, index) => {
        const newOrderItemRef = push(ref(this.db, 'OrderItem')); // Get a new unique Firebase key for each item
        const orderItemFirebaseIdKey = newOrderItemRef.key;

        const orderItemPayload: OrderItemData = {
          OrderID: numericOrderId, // Link to the newly created order's numeric ID
          OrderItemID: parseInt(orderItemFirebaseIdKey?.replace(/[^0-9]/g, '').slice(-6) || '0'), // Simple numeric ID for order item
          Price: item.UnitPrice,
          ProductID: item.ProductID,
          Quantity: item.Quantity,
          Rating: 0, // Default rating for new order items
          UserID: this.selectedCustomer()?.UserID || 0, // Link to selected customer's internal UserID
        };
        await set(newOrderItemRef, orderItemPayload); // Save each order item to Firebase
        console.log(`OrderItem ${index + 1} saved successfully:`, orderItemPayload);
      });

      await Promise.all(orderItemsPromises); // Wait for all order items to be saved
      
      // Use alert() for now, but for production, replace with a custom modal/snackbar
      alert('Order confirmed and saved successfully!');
      
      this.clearForm(); // Clear form after successful submission
      this.router.navigate(['/order-management']); // Navigate back to the order management page
    } catch (error) {
      console.error('Error confirming and saving order:', error);
      alert('An error occurred while confirming and saving the order.');
    }
  }

  // Clears all form fields and resets state
  clearForm(): void {
    this.selectedCustomer.set(null);
    this.customerAddress.set('');
    this.specialNote.set('');
    this.customerSearchTerm.set('');
    this.customerSearchResults.set([]);
    this.productSearchTerm.set('');
    this.productSearchResults.set([]);
    this.selectedProducts.set([]);
    this.calculateTotals(); // Reset totals to initial state
    this.shippingMethod.set('standard');
    this.paymentMethod.set('cash');
    console.log('Form cleared.');
  }

  // Navigates back to the order management page
  goBack(): void {
    this.router.navigate(['/order-management']); // Always navigate back to order management
  }

  // Formats a number as currency (VND) for display
  formatCurrencyDisplay(value: number | undefined): string {
    if (value === undefined || value === null || isNaN(value)) {
      return 'N/A';
    }
    return formatCurrency(value, this.locale, '₫', 'VND', '1.0-0');
  }

  // Helper method to get formatted product price for display in template
  getFormattedProductDisplayPrice(product: ProductData): string {
    // IMPORTANT FIX: Ensure product.ProductPrice is treated as a string before calling replace()
    const rawPriceString = String(product.ProductPrice || '0'); // Explicitly convert to string
    const parsedPrice = parseFloat(
      rawPriceString.replace(/\./g, '').replace(',', '.')
    );
    return this.formatCurrencyDisplay(isNaN(parsedPrice) ? 0 : parsedPrice);
  }
}
