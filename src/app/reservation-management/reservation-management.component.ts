// src/app/reservation-management/reservation-management.component.ts
import { Component, OnInit, OnDestroy, Inject, LOCALE_ID, signal } from '@angular/core';
import { CommonModule, formatDate } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms'; // Thêm FormsModule
import { Database, ref, onValue, off, update, remove, DataSnapshot, get } from '@angular/fire/database';

// Cập nhật giao diện ReservationInterface để bao gồm thông tin chi tiết từ các node khác
export interface ReservationInterface {
  id?: string; // Firebase key (chẳng hạn như '0' trong ví dụ của bạn, hoặc ID tự động tạo)
  BookingID?: number;
  BookingTime?: string; // Chuỗi ngày giờ từ Firebase, thay vì Appoinment_Time
  LocationID?: number;
  ServiceID?: number;
  Status?: 'Confirmed' | 'Cancelled' | 'Pending' | string; // Cho phép các giá trị khác hoặc là string
  UserID?: number;

  // Các trường mới để hiển thị thông tin liên quan
  CustomerFullName?: string;
  CustomerPhoneNumber?: string;
  ServiceTypeName?: string; // Tên dịch vụ, sẽ lấy từ ServiceType
  BranchDisplayName?: string; // Tên chi nhánh đầy đủ (kết hợp từ Details, Street, District hoặc BranchName)

  selected?: boolean; // Để phục vụ chức năng chọn/bỏ chọn
}

// Giao diện cho dữ liệu người dùng từ node "User"
export interface UserData {
  id?: string; // Firebase key (UID)
  UserID?: number; // Internal ID
  FullName?: string;
  PhoneNumber?: string;
  Email?: string;
  // ... các trường khác
}

// Giao diện cho dữ liệu dịch vụ từ node "Service"
export interface ServiceData {
  id?: string; // Firebase key
  ServiceID?: number;
  ServiceType?: string; // ĐÃ THAY ĐỔI TỪ Service_Name THÀNH ServiceType để khớp Firebase
}

// Giao diện cho dữ liệu địa điểm từ node "Branch"
export interface BranchData {
  id?: string; // Firebase key
  BranchID?: number;
  BranchName?: string;
  Details?: string; // Thêm trường Details
  District?: string;
  Street?: string;
  // ... các trường khác
}

@Component({
  selector: 'app-reservation-management',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule], // Thêm FormsModule
  templateUrl: './reservation-management.component.html',
  styleUrls: ['./reservation-management.component.css']
})
export class ReservationManagementComponent implements OnInit, OnDestroy {
  reservations = signal<ReservationInterface[]>([]);
  searchQuery = signal('');
  selectedStatusFilter: string = 'All'; // Thêm thuộc tính mới cho bộ lọc trạng thái, mặc định là 'All'
  private allUsers: UserData[] = [];
  private allServices: ServiceData[] = [];
  private allBranches: BranchData[] = [];
  private reservationsListenerCleanup: (() => void) | undefined;

  constructor(
    @Inject(LOCALE_ID) private locale: string,
    private router: Router,
    private db: Database
  ) { }

  ngOnInit(): void {
    // Tải tất cả dữ liệu tham chiếu trước
    this.loadAllReferenceData().then(() => {
      // Sau khi dữ liệu tham chiếu có sẵn, lắng nghe sự thay đổi của BookingSchedule
      const reservationsRef = ref(this.db, 'BookingSchedule');
      this.reservationsListenerCleanup = onValue(reservationsRef, (snapshot: DataSnapshot) => {
        const data = snapshot.val();
        const loadedReservations: ReservationInterface[] = [];
        if (data) {
          Object.keys(data).forEach(key => {
            const reservation = { id: key, ...data[key] } as ReservationInterface;
            // Gắn thông tin User, Service, Branch vào reservation
            this.mapReferenceDataToReservation(reservation);
            loadedReservations.push(reservation);
          });
        }
        this.reservations.set(loadedReservations);
        console.log('Đặt chỗ đã tải từ Firebase:', this.reservations());
      }, (error) => {
        console.error('Lỗi khi tải đặt chỗ từ Firebase:', error);
      });
    }).catch(error => {
      console.error('Lỗi khi tải dữ liệu tham chiếu:', error);
    });
  }

  ngOnDestroy(): void {
    if (this.reservationsListenerCleanup) {
      this.reservationsListenerCleanup();
    }
  }

  // Tải tất cả dữ liệu tham chiếu (User, Service, Branch) một lần
  private async loadAllReferenceData(): Promise<void> {
    try {
      const userSnapshot = await get(ref(this.db, 'User'));
      if (userSnapshot.exists()) {
        this.allUsers = Object.values(userSnapshot.val());
      } else {
        console.warn('Node "User" không tồn tại hoặc rỗng.');
      }

      const serviceSnapshot = await get(ref(this.db, 'Service'));
      if (serviceSnapshot.exists()) {
        // Đảm bảo dữ liệu từ snapshot được gán đúng cách
        this.allServices = Object.values(serviceSnapshot.val());
      } else {
        console.warn('Node "Service" không tồn tại hoặc rỗng.');
      }

      const branchSnapshot = await get(ref(this.db, 'Branch'));
      if (branchSnapshot.exists()) {
        this.allBranches = Object.values(branchSnapshot.val());
      } else {
        console.warn('Node "Branch" không tồn tại hoặc rỗng.');
      }
      console.log('Dữ liệu tham chiếu đã tải:', { users: this.allUsers, services: this.allServices, branches: this.allBranches });
    } catch (error) {
      console.error('Không thể tải dữ liệu tham chiếu:', error);
      throw error;
    }
  }

  // Phương thức để gắn thông tin tham chiếu vào một đặt chỗ
  private mapReferenceDataToReservation(reservation: ReservationInterface): void {
    // Gắn thông tin khách hàng
    if (reservation.UserID !== undefined) {
      const customer = this.allUsers.find(u => u.UserID === reservation.UserID);
      reservation.CustomerFullName = customer?.FullName || 'N/A';
      reservation.CustomerPhoneNumber = customer?.PhoneNumber || 'N/A';
    } else {
      reservation.CustomerFullName = 'N/A';
      reservation.CustomerPhoneNumber = 'N/A';
    }

    // Gắn thông tin dịch vụ
    if (reservation.ServiceID !== undefined) {
      const service = this.allServices.find(s => s.ServiceID === reservation.ServiceID);
      // ĐÃ CẬP NHẬT: Lấy từ service?.ServiceType
      reservation.ServiceTypeName = service?.ServiceType || 'N/A';
    } else {
      reservation.ServiceTypeName = 'N/A';
    }

    // Gắn thông tin chi nhánh
    if (reservation.LocationID !== undefined) {
      const branch = this.allBranches.find(b => b.BranchID === reservation.LocationID);
      if (branch) {
        // Ưu tiên BranchName nếu có, nếu không thì kết hợp Details, Street và District
        reservation.BranchDisplayName = branch.BranchName || 
                                        `${branch.Details ? branch.Details + ', ' : ''}${branch.Street || ''}, ${branch.District || ''}`;
        
        // Xử lý trường hợp chuỗi chỉ chứa dấu phẩy sau khi kết hợp
        if (reservation.BranchDisplayName.trim() === ',' || reservation.BranchDisplayName.trim() === ',,') {
          reservation.BranchDisplayName = 'N/A'; 
        }
      } else {
        reservation.BranchDisplayName = 'N/A';
      }
    } else {
      reservation.BranchDisplayName = 'N/A';
    }
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    try {
      const date = new Date(dateString.replace(' ', 'T'));
      if (isNaN(date.getTime())) {
          return 'Ngày không hợp lệ';
      }
      return formatDate(date, 'medium', this.locale);
    } catch (e) {
      console.error('Lỗi định dạng ngày:', dateString, e);
      return 'Ngày không hợp lệ';
    }
  }

  updateSearchQuery(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.searchQuery.set(inputElement.value.trim().toLowerCase());
  }

  get filteredReservations(): ReservationInterface[] {
    let filtered = this.reservations(); // Bắt đầu với tất cả các đặt chỗ

    // Lọc theo tìm kiếm (searchQuery)
    const query = this.searchQuery();
    if (query) {
      filtered = filtered.filter(reservation =>
        reservation.id?.toLowerCase().includes(query) ||
        reservation.BookingID?.toString().includes(query) ||
        reservation.Status?.toLowerCase().includes(query) ||
        reservation.BookingTime?.toLowerCase().includes(query) ||
        reservation.UserID?.toString().includes(query) ||
        reservation.CustomerFullName?.toLowerCase().includes(query) ||
        reservation.CustomerPhoneNumber?.toLowerCase().includes(query) ||
        reservation.ServiceTypeName?.toLowerCase().includes(query) ||
        reservation.BranchDisplayName?.toLowerCase().includes(query)
      );
    }

    // Lọc theo trạng thái (selectedStatusFilter)
    if (this.selectedStatusFilter && this.selectedStatusFilter !== 'All') {
      filtered = filtered.filter(reservation =>
        reservation.Status?.toLowerCase() === this.selectedStatusFilter.toLowerCase()
      );
    }

    return filtered;
  }

  // ĐÃ CẬP NHẬT: Phương thức updateStatus nhận Event và xử lý giá trị bên trong
  async updateStatus(reservation: ReservationInterface, event: Event): Promise<void> {
    const selectElement = event.target as HTMLSelectElement;
    const newStatus = selectElement.value; // Lấy giá trị từ dropdown

    if (!reservation.id) {
      console.error('ID đặt chỗ không xác định, không thể cập nhật trạng thái.');
      return;
    }

    const reservationRef = ref(this.db, `BookingSchedule/${reservation.id}`);

    try {
      await update(reservationRef, { Status: newStatus });
      console.log(`Trạng thái đặt chỗ ${reservation.id} đã được cập nhật thành ${newStatus} trong Firebase.`);
    } catch (error) {
      console.error('Lỗi khi cập nhật trạng thái đặt chỗ trong Firebase:', error);
      alert('Đã xảy ra lỗi khi cập nhật trạng thái đặt chỗ.');
    }
  }

  editReservation(reservation: ReservationInterface): void {
    if (reservation.id) {
      console.log('Chức năng chỉnh sửa đang được phát triển. Reservation ID:', reservation.id);
    } else {
      console.warn('ID đặt chỗ không xác định, không thể chỉnh sửa.');
    }
  }

  selectAllReservations(event: any): void {
    const isChecked = event.target.checked;
    this.reservations.set(this.reservations().map(reservation => ({ ...reservation, selected: isChecked })));
  }

  toggleReservationSelection(reservation: ReservationInterface): void {
    if (reservation && reservation.id) {
      this.reservations.set(this.reservations().map(r =>
        r.id === reservation.id ? { ...r, selected: !r.selected } : r
      ));
    }
  }

  deleteReservation(reservationId: string | undefined): void {
    if (!reservationId) {
      console.error('ID đặt chỗ không xác định, không thể xóa.');
      return;
    }
    if (confirm('Bạn có chắc muốn xóa đặt chỗ này không?')) {
      remove(ref(this.db, 'BookingSchedule/' + reservationId))
        .then(() => {
          console.log('Đặt chỗ đã được xóa thành công từ Firebase:', reservationId);
        })
        .catch((error) => {
          console.error('Lỗi khi xóa đặt chỗ khỏi Firebase:', error);
          alert('Đã xảy ra lỗi khi xóa đặt chỗ.');
        });
    }
  }

  deleteSelectedReservations(): void {
    const selectedReservationIds = this.reservations()
      .filter(reservation => reservation.selected && reservation.id)
      .map(reservation => reservation.id as string);

    if (selectedReservationIds.length === 0) {
      alert('Vui lòng chọn ít nhất một đặt chỗ để xóa.');
      return;
    }

    if (confirm(`Bạn có chắc muốn xóa ${selectedReservationIds.length} đặt chỗ đã chọn không?`)) {
      const deletePromises = selectedReservationIds.map(reservationId =>
        remove(ref(this.db, 'BookingSchedule/' + reservationId))
      );

      Promise.all(deletePromises)
        .then(() => {
          console.log('Các đặt chỗ đã chọn đã được xóa thành công từ Firebase.');
        })
        .catch((error) => {
          console.error('Lỗi khi xóa các đặt chỗ đã chọn khỏi Firebase:', error);
          alert('Đã xảy ra lỗi khi xóa các đặt chỗ đã chọn.');
        });
    }
  }
}
