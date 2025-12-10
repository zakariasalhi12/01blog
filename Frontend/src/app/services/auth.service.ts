import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APIUrl } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

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

  logout(): Observable<any> {
    return this.http.get(`${APIUrl}/logout`);
  }

  logged(): Observable<any> {
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
