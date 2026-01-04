import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import {Header} from '../../components/header/header'

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterModule , FormsModule , CommonModule , Header],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  username = '';
  email = '';
  password = '';
  age = 15;
  // General error message
  error = signal('');
  // Field-specific errors returned by the backend, e.g. { password: '...', email: '...'}
  errors = signal<Record<string, string>>({});

  constructor(private auth: AuthService) {}

  onSubmit() {
    this.error.set('');
    
    // Prepare data for backend
    const data = {
      username: this.username,
      email: this.email,
      password: this.password,
      age: this.age
    };

    this.auth.signup(data).subscribe({
      next: (res) => {
        console.log('Registered successfully!', res);
        this.auth.saveToken(res.token);
        // Redirect to homepage or login
        window.location.href = '/';
      },
      error: (err) => {
        // If backend sends { errors: { field: message } }
        if (err && err.error && err.error.errors) {
          this.errors.set(err.error.errors);
          // clear general error
          this.error.set('');
        } else if (err && err.error) {
          // fallback: show message or entire error object
          const msg = err.error.message || err.error.error || JSON.stringify(err.error);
          this.error.set(msg);
          this.errors.set({});
        } else {
          this.error.set('Registration failed');
          this.errors.set({});
        }
      }
    });
  }

  clearField(field: string) {
    const current = { ...this.errors() };
    if (current[field]) {
      delete current[field];
      this.errors.set(current);
    }
    // also clear general error when user edits
    if (this.error()) {
      this.error.set('');
    }
  }
}
