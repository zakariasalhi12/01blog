import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReportsResponse } from '../models/report.model';
import { APIUrl } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  constructor(private http: HttpClient) { }

  reportPost(postId: number, reason: string): Observable<any> {
    return this.http.post(`${APIUrl}/posts/${postId}/report`, { reason }, { responseType: 'text' });
  }

  getUserReports(page: number = 0, size: number = 10): Observable<ReportsResponse> {
    return this.http.get<ReportsResponse>(`${APIUrl}/posts/reports?page=${page}&size=${size}`);
  }

  deleteUserReport(reportId: number): Observable<any> {
    return this.http.delete(`${APIUrl}/posts/reports/${reportId}`, { responseType: 'text' });
  }

  getAllReports(page: number = 0, size: number = 10): Observable<ReportsResponse> {
    return this.http.get<ReportsResponse>(`${APIUrl}/admin/reports?page=${page}&size=${size}`);
  }
}

