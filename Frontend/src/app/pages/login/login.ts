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
  error = signal('');

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
        this.error.set(err.error.error || 'Login failed');
      }
    });
  }
}