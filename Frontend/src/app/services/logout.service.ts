import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { APIUrl } from '../../environments/environment';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class LogoutService {
  constructor(
    private http : HttpClient,
    private router : Router
  ){}

  logout(): void{
    this.http.get(`${APIUrl}/auth/logout`).subscribe({
      error: (err) => {
        console.error("Logout error:", err);
      }
    })
  }
}
