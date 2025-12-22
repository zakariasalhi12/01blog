import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsersResponse } from '../models/user.model';
import { ReportsResponse } from '../models/report.model';
import { APIUrl } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http: HttpClient) { }

  getAllUsers(page: number = 0, size: number = 10): Observable<UsersResponse> {
    return this.http.get<UsersResponse>(`${APIUrl}/admin/users?page=${page}&size=${size}`);
  }

  updateUserRole(userId: number, role: 'USER' | 'ADMIN' | 'BANNED'): Observable<any> {
    return this.http.put(`${APIUrl}/admin/users/${userId}/role`, { role });
  }

  getAllReports(page: number = 0, size: number = 10): Observable<ReportsResponse> {
    return this.http.get<ReportsResponse>(`${APIUrl}/admin/reports?page=${page}&size=${size}`);
  }

  deleteReport(reportId: number): Observable<any> {
    return this.http.delete(`${APIUrl}/admin/report/${reportId}`);
  }

  // Posts management
  getAllPosts(page: number = 0, size: number = 10): Observable<any> {
    return this.http.get(`${APIUrl}/admin/posts?page=${page}&size=${size}`);
  }

  deletePost(postId: number): Observable<any> {
    return this.http.post(`${APIUrl}/admin/posts/${postId}/delete`, {});
  }

  togglePostVisibility(postId: number, visible: boolean): Observable<any> {
    return this.http.put(`${APIUrl}/admin/posts/${postId}/visibility`, { visible });
  }
}

