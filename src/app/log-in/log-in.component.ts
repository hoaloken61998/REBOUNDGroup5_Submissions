// src/app/login/log-in.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

// Import Firebase Authentication modules
import { Auth } from '@angular/fire/auth';
import { signInWithEmailAndPassword, setPersistence, sendPasswordResetEmail, browserLocalPersistence, browserSessionPersistence, User } from 'firebase/auth';
// QUAN TRỌNG: Import browserLocalPersistence và browserSessionPersistence TRỰC TIẾP từ 'firebase/auth'
// Đây là nguồn chính xác và đáng tin cậy của các hằng số Persistence



@Component({
  selector: 'app-log-in',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  private auth: Auth; // Inject Auth instance

  constructor(private fb: FormBuilder, private router: Router, auth: Auth) { // Inject Auth here
    this.auth = auth; // Assign injected Auth to private property

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false],
    });
  }

  ngOnInit(): void {
    console.log('Login Component: ngOnInit. Ready for login form.');
  }

  ngOnDestroy(): void {
    // No specific cleanup needed for Firebase auth listeners in this setup
  }

  async onSubmit(): Promise<void> {
    this.errorMessage = null;
    this.successMessage = null;

    if (this.loginForm.valid) {
      const { email, password, rememberMe } = this.loginForm.value;

      try {
        // Sử dụng browserLocalPersistence hoặc browserSessionPersistence đã được import trực tiếp từ 'firebase/auth'
        await setPersistence(this.auth, rememberMe ? browserLocalPersistence : browserSessionPersistence);
        
        const userCredential = await signInWithEmailAndPassword(this.auth, email, password);
        const user = userCredential.user;
        console.log('Login successful:', user.email ?? 'No Email');

        this.successMessage = 'Đăng nhập thành công!';
        
        // IMPORTANT: SET THE FLAG IN LOCALSTORAGE ON SUCCESSFUL LOGIN
        localStorage.setItem('isLoggedIn', 'true');

        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 1500);

      } catch (error: any) {
        console.error('Login error:', error);
        switch (error.code) {
          case 'auth/invalid-credential':
            this.errorMessage = 'Email hoặc mật khẩu không hợp lệ. Vui lòng thử lại.';
            break;
          case 'auth/user-disabled':
            this.errorMessage = 'Tài khoản của bạn đã bị vô hiệu hóa.';
            break;
          case 'auth/invalid-email':
            this.errorMessage = 'Địa chỉ email không hợp lệ.';
            break;
          case 'auth/too-many-requests':
            this.errorMessage = 'Quá nhiều yêu cầu đăng nhập không thành công. Vui lòng thử lại sau.';
            break;
          default:
            this.errorMessage = 'Đã xảy ra lỗi không xác định. Vui lòng thử lại.';
        }
        // IMPORTANT: REMOVE THE FLAG FROM LOCALSTORAGE ON FAILED LOGIN
        localStorage.removeItem('isLoggedIn');
      }
    } else {
      this.errorMessage = 'Vui lòng điền đầy đủ và đúng định dạng các trường.';
      // IMPORTANT: REMOVE THE FLAG FROM LOCALSTORAGE IF FORM IS INVALID
      localStorage.removeItem('isLoggedIn');
    }
  }

  async forgotPassword(): Promise<void> {
    this.errorMessage = null;
    this.successMessage = null;
    const email = this.loginForm.get('email')?.value;

    if (!email || !this.loginForm.get('email')?.valid) {
      this.errorMessage = 'Vui lòng nhập một địa chỉ email hợp lệ để reset mật khẩu.';
      return;
    }

    try {
      await sendPasswordResetEmail(this.auth, email);
      this.successMessage = 'Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn.';
    } catch (error: any) {
      console.error('Forgot password error:', error);
      switch (error.code) {
        case 'auth/user-not-found':
        case 'auth/invalid-email':
          this.errorMessage = 'No user found with this email. Please check the email address.';
          break;
        case 'auth/too-many-requests':
          this.errorMessage = 'Too many requests. Please try again later.';
          break;
        default:
          this.errorMessage = 'An error occurred while sending the password reset email. Please try again.';
      }
    }
  }

  async logout(): Promise<void> {
    try {
      await this.auth.signOut();
      console.log('Successfully logged out.');
      localStorage.removeItem('isLoggedIn');
      this.router.navigate(['/log-in']);
    } catch (error) {
      console.error('Error logging out:', error);
      alert('An error occurred during logout.');
    }
  }
}
