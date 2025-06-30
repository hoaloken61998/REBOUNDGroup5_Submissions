// src/app/add-reservation/add-reservation.component.ts
import { Component, OnInit, ViewEncapsulation, signal, Inject, LOCALE_ID } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, formatDate } from '@angular/common'; // Import CommonModule and formatDate
import { Router } from '@angular/router';
import { Database, ref, push, get, set } from '@angular/fire/database';

// Interfaces for data from Firebase
export interface UserData {
  UserID?: number;
  FullName?: string;
  PhoneNumber?: string;
  Email?: string;
  // Add other user fields if needed, e.g., Address, DateOfBirth
}

export interface ServiceData {
  ServiceID?: number;
  ServiceType?: string; // e.g., "Piercing Service", "After-care Service"
}

// Updated BranchData interface to reflect Firebase 'Branch' structure
export interface BranchData {
  BranchID?: number;
  Details?: string; // e.g., "24"
  District?: string; // e.g., "District 1"
  Street?: string;  // e.g., "Ly Tu Trong"
  Province?: string; // Assuming Province might exist in a full address
  BranchDisplayName?: string; // Custom field for display
}

// Interface for the data structure to be saved in Firebase's BookingSchedule node
export interface BookingSchedulePayload {
  BookingID?: number; // Will be derived from Firebase push key or generated
  BookingTime?: string; // Formatted date-time string
  BranchID?: number; // Linked from selected branch (was LocationID)
  ServiceID?: number; // Linked from selected service
  UserID?: number; // Linked from selected user
  Status?: string; // Default: 'Pending'
  JewelryType?: string; // Free text or linked, depends on requirement
  SpecialRequests?: string;
}

@Component({
  selector: 'add-reservation',
  standalone: true,
  imports: [FormsModule, CommonModule], // Add CommonModule
  templateUrl: './add-reservation.component.html',
  styleUrls: ['./add-reservation.component.css'],
  encapsulation: ViewEncapsulation.None // For global CSS
})
export class AddReservationComponent implements OnInit {
  // Signals for form data
  customerSearchTerm = signal('');
  customerSearchResults = signal<UserData[]>([]);
  selectedCustomer = signal<UserData | null>(null);

  // Form fields bind to these signals
  appointmentDateTime = signal('');
  selectedServiceId = signal<number | undefined>(undefined);
  selectedBranchId = signal<number | undefined>(undefined); // Changed from selectedLocationId
  jewelryType = signal('Ear Piercing Jewelry'); // Default value
  specialRequests = signal('');

  // Data loaded from Firebase for dropdowns and search
  allUsers: UserData[] = [];
  services: ServiceData[] = [];
  branches: BranchData[] = []; // Changed from locations

  // Static options for dropdowns (if not fully driven by Firebase)
  jewelryTypesOptions = [
    'Ear Piercing Jewelry',
    'Body Piercing Jewelry',
    'Other'
  ];

  constructor(
    private router: Router,
    private db: Database,
    @Inject(LOCALE_ID) private locale: string // For date formatting if needed
  ) {}

  ngOnInit(): void {
    this.loadAllReferenceData();
  }

  // Loads all necessary reference data (Users, Services, Branches) from Firebase
  async loadAllReferenceData(): Promise<void> {
    try {
      // Fetch all users for customer search
      const userSnapshot = await get(ref(this.db, 'User'));
      if (userSnapshot.exists()) {
        this.allUsers = Object.values(userSnapshot.val());
      } else {
        console.warn('Firebase node "User" does not exist or is empty.');
        this.allUsers = [];
      }

      // Fetch all services
      const serviceSnapshot = await get(ref(this.db, 'Service'));
      if (serviceSnapshot.exists()) {
        this.services = Object.values(serviceSnapshot.val());
        console.log('Service data loaded:', this.services);
      } else {
        console.warn('Firebase node "Service" does not exist or is empty.');
        this.services = [];
      }
      // Set default service type (Piercing Service) if available
      const defaultService = this.services.find(s => s.ServiceType === 'Piercing Service');
      if (defaultService) {
        this.selectedServiceId.set(defaultService.ServiceID);
        console.log('Default ServiceID set to:', this.selectedServiceId());
      } else {
        console.log('Default service "Piercing Service" not found. selectedServiceId remains:', this.selectedServiceId());
      }


      // Fetch all branches from 'Branch' node
      const branchSnapshot = await get(ref(this.db, 'Branch')); // Changed from 'Location' to 'Branch'
      if (branchSnapshot.exists()) {
        const rawBranches = Object.values(branchSnapshot.val()) as BranchData[];
        this.branches = rawBranches.map(br => {
          const parts = [];
          if (br.Details) parts.push(br.Details);
          if (br.Street) parts.push(br.Street);
          if (br.District) parts.push(br.District);
          if (br.Province) parts.push(br.Province); // Include Province if available
          
          let displayName = parts.join(', ');
          
          // Handle cases where displayName might just be commas due to missing parts
          if (displayName.trim() === ',' || displayName.trim() === ',,' || displayName.trim() === ',,,') {
            displayName = ''; // Treat as empty if it's just commas
          }

          // Fallback to 'N/A' if still empty
          if (!displayName) {
            displayName = 'N/A';
          }

          return {
            ...br,
            BranchDisplayName: displayName
          };
        });
        console.log('Branch data loaded and processed. Number of branches:', this.branches.length, this.branches);
      } else {
        console.warn('Firebase node "Branch" does not exist or is empty.');
        this.branches = []; // Ensure it's an empty array if no data
        console.log('Branch array is empty. Number of branches:', this.branches.length);
      }
      // Set default branch to the first one in the loaded list
      if (this.branches.length > 0) {
        this.selectedBranchId.set(this.branches[0].BranchID); // Changed to BranchID
        console.log('Default BranchID set to:', this.selectedBranchId(), ' (', this.branches[0].BranchDisplayName, ')');
      } else {
        console.log('No branches found to set default. selectedBranchId remains:', this.selectedBranchId());
      }


      console.log('Reference data loaded complete. Current selectedServiceId:', this.selectedServiceId(), 'Current selectedBranchId:', this.selectedBranchId());
    } catch (error) {
      console.error('Error loading reference data from Firebase:', error);
      this.allUsers = [];
      this.services = [];
      this.branches = [];
    }
  }

  // --- Customer Search & Selection Logic ---
  async searchCustomers(): Promise<void> {
    const query = this.customerSearchTerm().toLowerCase().trim();
    if (query.length < 2) {
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

  selectCustomer(customer: UserData): void {
    this.selectedCustomer.set(customer);
    this.customerSearchResults.set([]);
    this.customerSearchTerm.set('');
  }

  removeSelectedCustomer(): void {
    this.selectedCustomer.set(null);
  }

  // --- Form Actions ---
  async addReservation(): Promise<void> {
    if (!this.selectedCustomer() || !this.selectedCustomer()?.UserID) {
      alert('Vui lòng chọn một khách hàng.');
      return;
    }
    if (!this.appointmentDateTime()) {
      alert('Vui lòng nhập ngày và giờ đặt chỗ.');
      return;
    }
    if (this.selectedServiceId() === undefined) {
      alert('Vui lòng chọn loại dịch vụ.');
      return;
    }
    if (this.selectedBranchId() === undefined) { // Changed to selectedBranchId
      alert('Vui lòng chọn chi nhánh.');
      return;
    }

    try {
      const bookingDate = new Date(this.appointmentDateTime());
      if (isNaN(bookingDate.getTime())) {
          alert('Định dạng ngày giờ không hợp lệ.');
          return;
      }
      const formattedBookingTime = formatDate(bookingDate, 'yyyy-MM-dd HH:mm:ss', this.locale);
      
      const newReservationRef = push(ref(this.db, 'BookingSchedule')); // Get a new unique Firebase key
      const firebaseBookingIdKey = newReservationRef.key;

      if (!firebaseBookingIdKey) {
        console.error('Failed to generate Booking ID.');
        alert('Đã xảy ra lỗi khi tạo đặt chỗ. Vui lòng thử lại.');
        return;
      }

      const numericBookingId = parseInt(firebaseBookingIdKey.replace(/[^0-9]/g, '').slice(-6) || '0');


      const reservationPayload: BookingSchedulePayload = {
        BookingID: numericBookingId,
        BookingTime: formattedBookingTime,
        BranchID: this.selectedBranchId(), // Changed to BranchID
        ServiceID: this.selectedServiceId(),
        UserID: this.selectedCustomer()?.UserID,
        Status: 'Pending', // Default status for a new reservation
        JewelryType: this.jewelryType(),
        SpecialRequests: this.specialRequests(),
      };

      await set(newReservationRef, reservationPayload); // Save the reservation to Firebase
      
      console.log('Đặt chỗ đã được tạo thành công:', reservationPayload);
      alert('Đặt chỗ thành công!');
      this.clear(); // Clear the form after successful submission
      this.router.navigate(['/reservation-management']); // Navigate back to the list
    } catch (error) {
      console.error('Lỗi khi tạo đặt chỗ:', error);
      alert('Đã xảy ra lỗi khi tạo đặt chỗ. Vui lòng thử lại.');
    }
  }

  clear(): void {
    this.customerSearchTerm.set('');
    this.customerSearchResults.set([]);
    this.selectedCustomer.set(null);
    this.appointmentDateTime.set('');
    this.selectedServiceId.set(undefined); // Clear service selection
    this.selectedBranchId.set(undefined); // Changed to selectedBranchId, clear branch selection
    this.jewelryType.set('Ear Piercing Jewelry');
    this.specialRequests.set('');
    console.log('Form đã được xóa.');
    // Re-load to set defaults again for both Service and Branch
    this.loadAllReferenceData(); 
  }

  goBack() {
    this.router.navigate(['/reservation-management']);
  }
}
