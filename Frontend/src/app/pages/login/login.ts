import { Component, signal } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {Header} from '../../components/header/header'
import { sign } from 'node:crypto';
@Component({
  selector: 'app-login',
  standalone : true,
  imports: [RouterModule ,FormsModule , CommonModule ,Header ],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {

  login = '';
  password = '';
  // General error
  error = signal('');
  // Field-level errors (if backend returns a map of field->message)
  errors = signal<Record<string, string>>({});

  constructor(private auth: AuthService) {}

  onSubmit() {
    this.error.set('');

    this.auth.login(this.login, this.password).subscribe({
      next: (res) => {
        this.auth.saveToken(res.token);
        console.log('Logged in successfully');
        // redirect to home page
        window.location.href = '/';
      },
      error: (err) => {
        // Prefer structured field errors, then general 'error' key, then fallback
        if (err && err.error && err.error.errors) {
          this.errors.set(err.error.errors);
          this.error.set('');
        } else if (err && err.error) {
          const general = err.error.error || err.error.message || JSON.stringify(err.error);
          this.error.set(general);
          this.errors.set({});
        } else {
          this.error.set('Login failed');
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
    if (this.error()) { this.error.set(''); }
  }
}