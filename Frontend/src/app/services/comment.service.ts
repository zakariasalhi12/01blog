import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { commentResponse } from '../models/comment.model';
import { APIUrl } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = 'http://localhost:8000/api/comments';

  constructor(private http: HttpClient) { }

  createComment(payload: { content: string; postId: number }): Observable<any> {
    return this.http.post(`${APIUrl}/comments`, payload, { responseType: 'text' });
  }

  getComment(id: number, size: number = 0, page: number = 10): Observable<commentResponse> {
    return this.http.get<commentResponse>(`${APIUrl}/comments/${id}?page=${page}&size=${size}`);
  }

  deleteComment(id: number): Observable<any> {
    return this.http.delete(`${APIUrl}/comments/${id}`, { responseType: 'text' });
  }
}
