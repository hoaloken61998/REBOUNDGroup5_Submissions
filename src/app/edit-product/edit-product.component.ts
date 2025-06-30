// src/app/edit-product/edit-product.component.ts
import { Component, OnInit, OnDestroy, ViewEncapsulation, Inject, LOCALE_ID } from '@angular/core';
import { Location, CommonModule, formatCurrency } from '@angular/common'; // Import formatCurrency
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Database, ref, get, update, DataSnapshot, onValue } from '@angular/fire/database'; // Import Firebase modules

// Định nghĩa giao diện ProductInterface
// Cập nhật để khớp chính xác với cấu trúc Firebase Product node
export interface ProductInterface {
  id?: string; // Firebase key (UID của sản phẩm)
  CategoryID?: number;
  ImageLink?: string; // URL của hình ảnh
  ProductDescription?: string;
  ProductID?: number; // ID nội bộ của sản phẩm (nếu có)
  ProductName?: string;
  ProductPrice?: string; // GIỮ LÀ CHUỖI CÓ DẤU CHẤM NHƯ FIREBASE
  ProductStockQuantity?: number;
  StatusID?: number; // Trạng thái sản phẩm (ví dụ: 1 = Active, 2 = Inactive)
  Origin?: string; // Thêm Origin nếu có
  Customize?: string; // Thêm Customize nếu có
  Rating?: number; // Thêm Rating nếu có
  SoldQuantity?: number; // Thêm SoldQuantity nếu có
}

// Giao diện cho ProductStatus từ Firebase
export interface ProductStatus {
  StatusID?: number;
  StatusName?: string;
}

// Giao diện cho Category từ Firebase
export interface Category {
  CategoryID?: number;
  CategoryName?: string;
}

@Component({
  selector: 'app-edit-product',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './edit-product.component.html',
  styleUrl: './edit-product.component.css',
  encapsulation: ViewEncapsulation.None,
})
export class EditProductComponent implements OnInit, OnDestroy {
  product: ProductInterface = {}; // Đối tượng sản phẩm sẽ chứa dữ liệu tải về
  productId: string | null = null; // Để lưu Firebase key (UID) của sản phẩm
  isLoading: boolean = true; // Biến trạng thái tải dữ liệu

  categories: Category[] = [];
  productStatuses: ProductStatus[] = [];

  private categoryListenerCleanup: (() => void) | undefined;
  private statusListenerCleanup: (() => void) | undefined;


  constructor(
    private location: Location,
    private route: ActivatedRoute,
    private db: Database, // Inject Database
    @Inject(LOCALE_ID) private locale: string // Inject LOCALE_ID cho formatCurrency
  ) {}

  ngOnInit(): void {
    this.productId = this.route.snapshot.paramMap.get('id'); // Lấy Firebase key từ URL

    // Tải dữ liệu tham chiếu trước, sau đó tải dữ liệu sản phẩm
    Promise.all([this.loadCategories(), this.loadProductStatuses()])
      .then(() => {
        if (this.productId) {
          this.loadProductData(this.productId);
        } else {
          console.warn('ID sản phẩm không được cung cấp trong URL.');
          this.isLoading = false;
        }
      })
      .catch(error => {
        console.error('Lỗi khi tải dữ liệu tham chiếu:', error);
        this.isLoading = false;
      });
  }

  ngOnDestroy(): void {
    if (this.categoryListenerCleanup) {
      this.categoryListenerCleanup();
    }
    if (this.statusListenerCleanup) {
      this.statusListenerCleanup();
    }
  }

  // Tải danh sách Categories từ Firebase
  private loadCategories(): Promise<void> {
    return new Promise((resolve, reject) => {
      const categoryRef = ref(this.db, 'Category');
      this.categoryListenerCleanup = onValue(categoryRef, (snapshot: DataSnapshot) => {
        const data = snapshot.val();
        const loadedCategories: Category[] = [];
        if (data) {
          Object.keys(data).forEach(key => {
            if (data[key] && data[key].CategoryID && data[key].CategoryName) {
              loadedCategories.push(data[key]);
            }
          });
        }
        this.categories = loadedCategories;
        console.log('Categories loaded:', this.categories);
        resolve();
      }, (error) => {
        console.error('Error loading categories:', error);
        reject(error);
      });
    });
  }

  // Tải danh sách Product Statuses từ Firebase
  private loadProductStatuses(): Promise<void> {
    return new Promise((resolve, reject) => {
      const productStatusRef = ref(this.db, 'ProductStatus');
      this.statusListenerCleanup = onValue(productStatusRef, (snapshot: DataSnapshot) => {
        const data = snapshot.val();
        const loadedStatuses: ProductStatus[] = [];
        if (data) {
          Object.keys(data).forEach(key => {
            if (data[key] && data[key].StatusID && data[key].StatusName) {
              loadedStatuses.push(data[key]);
            }
          });
        }
        this.productStatuses = loadedStatuses;
        console.log('Product Statuses loaded:', this.productStatuses);
        resolve();
      }, (error) => {
        console.error('Error loading product statuses:', error);
        reject(error);
      });
    });
  }


  // Tải dữ liệu sản phẩm từ Firebase Realtime Database
  private loadProductData(id: string): void {
    this.isLoading = true;
    get(ref(this.db, 'Product/' + id))
      .then((snapshot: DataSnapshot) => {
        if (snapshot.exists()) {
          this.product = { id: snapshot.key, ...snapshot.val() };
          console.log('Dữ liệu sản phẩm đã tải để chỉnh sửa:', this.product);

          // Định dạng lại giá để hiển thị trong input (nếu cần)
          // Input type="text" sẽ hiển thị chuỗi có dấu chấm đúng như Firebase
          // Nếu bạn muốn hiển thị số thuần, hãy dùng parseFloat(this.product.ProductPrice)

        } else {
          console.warn('Không tìm thấy sản phẩm nào với ID:', id);
          this.product = {};
        }
      })
      .catch((error) => {
        console.error('Lỗi khi tải dữ liệu sản phẩm:', error);
        this.product = {};
      })
      .finally(() => {
        this.isLoading = false;
      });
  }

  goBack(): void {
    this.location.back();
  }

  // Phương thức để chuyển đổi StatusID thành tên trạng thái
  getProductStatusName(statusId: number | undefined): string {
    const status = this.productStatuses.find(s => s.StatusID === statusId);
    return status ? status.StatusName || 'Unknown' : 'N/A';
  }

  // Phương thức để chuyển đổi CategoryID thành tên Category
  getProductCategoryName(categoryId: number | undefined): string {
    const category = this.categories.find(c => c.CategoryID === categoryId);
    return category ? category.CategoryName || 'Unknown' : 'N/A';
  }

  // Xử lý khi chọn file hình ảnh (chỉ lấy URL tạm thời)
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      // Đây chỉ là cách hiển thị ảnh cục bộ tạm thời.
      // Để tải lên Firebase Storage hoặc dịch vụ khác, bạn cần tích hợp API.
      const reader = new FileReader();
      reader.onload = () => {
        this.product.ImageLink = reader.result as string;
      };
      reader.readAsDataURL(file);
      console.log('Selected file:', file.name);
      // alert('Lưu ý: Chức năng tải ảnh lên Firebase Storage cần tích hợp thêm API.'); // Dòng này đã được bỏ
    }
  }

  // Định dạng giá khi người dùng nhập
  formatPriceInput(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input) {
      // Loại bỏ tất cả ký tự không phải số và dấu chấm, chỉ giữ lại số
      let value = input.value.replace(/\D/g, "");
      // Định dạng lại thành chuỗi có dấu chấm phân cách hàng nghìn (ví dụ: 4.500.000)
      input.value = new Intl.NumberFormat('vi-VN').format(Number(value));
      // Lưu giá trị SỐ CHUỖI VÀO MODEL (không có dấu chấm)
      this.product.ProductPrice = value;
    }
  }

  // Phương thức để định dạng giá trị từ model cho hiển thị (nếu cần)
  displayFormattedPrice(price: string | undefined): string {
    if (price === undefined) {
      return '0 ₫';
    }
    const numericPrice = parseFloat(price.replace(/\./g, '').replace(',', '.'));
    if (isNaN(numericPrice)) {
      return '0 ₫';
    }
    return formatCurrency(numericPrice, this.locale, '₫', 'VND', '1.0-0');
  }

  // Đặt lại form về trạng thái ban đầu (làm sạch các trường chỉnh sửa)
  clear(): void {
    // Không tải lại dữ liệu mà chỉ làm sạch các trường input
    this.product = {
      ...this.product, // Giữ lại id và ProductID
      ProductName: '',
      ImageLink: 'https://placehold.co/150x150/eeeeee/black?text=Product+Image',
      CategoryID: undefined,
      StatusID: undefined,
      ProductDescription: '',
      ProductPrice: '',
      ProductStockQuantity: undefined,
      Origin: '',
      Customize: ''
    };
    // Nếu muốn reset hoàn toàn về dữ liệu gốc từ Firebase, bạn sẽ cần một bản sao khác
    // và gán lại this.product = { ...this.originalProduct };
    console.log('Các trường biểu mẫu đã được xóa.');
  }

  // Phương thức lưu thay đổi vào Firebase Realtime Database
  async save(): Promise<void> {
    if (!this.productId) {
      console.error('Không thể lưu: ID sản phẩm (Firebase UID) bị thiếu.');
      alert('Không thể lưu: ID sản phẩm bị thiếu.');
      return;
    }

    // Kiểm tra các trường bắt buộc trước khi lưu
    if (!this.product.ProductName || !this.product.ProductPrice || this.product.ProductStockQuantity === undefined || this.product.CategoryID === undefined || this.product.StatusID === undefined) {
      alert('Vui lòng điền đầy đủ các thông tin bắt buộc: Product Name, Price, Stock, Category, Status.');
      return;
    }

    // Tạo đối tượng dữ liệu để cập nhật, loại bỏ 'id' vì nó là key của Firebase
    const dataToUpdate: Partial<ProductInterface> = { ...this.product };
    delete dataToUpdate.id; // Không gửi id trở lại Firebase làm một trường

    // Chuẩn bị ProductPrice để lưu: chuyển đổi về chuỗi có dấu chấm
    // Firebase của bạn lưu "4.500.000" (dùng dấu chấm cho hàng ngàn)
    // formatPriceInput đã xử lý việc lưu vào newProduct.ProductPrice là chuỗi số thuần
    // Bây giờ cần format lại cho khớp với Firebase
    if (dataToUpdate.ProductPrice) {
      dataToUpdate.ProductPrice = new Intl.NumberFormat('vi-VN').format(parseFloat(dataToUpdate.ProductPrice));
    }


    try {
      await update(ref(this.db, 'Product/' + this.productId), dataToUpdate);
      console.log('Thông tin sản phẩm đã lưu thành công:', dataToUpdate);
      alert('Thông tin sản phẩm đã được lưu thành công!');
      this.location.back(); // Quay lại trang trước sau khi lưu
    } catch (error) {
      console.error('Lỗi khi lưu thông tin sản phẩm:', error);
      alert('Đã xảy ra lỗi khi lưu thông tin sản phẩm.');
    }
  }
}
