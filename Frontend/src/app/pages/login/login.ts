import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone : true,
  imports: [RouterModule ,FormsModule , CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {

  username = '';
  password = '';
  error = '';

  constructor(private auth: AuthService) {}

  onSubmit() {
      console.log("Clicked login!", this.username, this.password);
    this.error = '';

    this.auth.login(this.username, this.password).subscribe({
      next: (res) => {
        this.auth.saveToken(res.token);
        console.log('Logged in successfully');
        // redirect to home page
        window.location.href = '/';
      },
      error: (err) => {
        this.error = err.error.error || 'Login failed';
      }
    });
  }
}