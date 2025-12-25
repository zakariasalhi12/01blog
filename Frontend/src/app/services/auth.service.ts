import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APIUrl } from '../../environments/environment';
import { Observable, of } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private platform_id = inject(PLATFORM_ID);

  constructor(private http: HttpClient) { }

  login(login: string, password: string): Observable<any> {
    return this.http.post(`${APIUrl}/auth/login`, {
      login,
      password
    });
  }

  signup(data: any): Observable<any> {
    return this.http.post(`${APIUrl}/auth/signup`, data);
  }

  logout(): void{
    this.http.get(`${APIUrl}/auth/logout`).subscribe({
      error: (err) => {
        console.error("Logout error:", err);
      }
    })
  }

  logged(): Observable<any> {
    if (!isPlatformBrowser(this.platform_id)) {
      return of(null)
    }
    const token = localStorage.getItem('token') || '';
    return this.http.get(`${APIUrl}/auth/logged`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  // Store token
  saveToken(token: string) {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  clearToken() {
    localStorage.removeItem('token');
  }
}
