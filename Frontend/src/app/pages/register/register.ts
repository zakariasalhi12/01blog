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
  error = signal('');

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
        this.error.set(JSON.stringify(err.error));
      }
    });
  }
}
