import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APIUrl } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LikesService {


  constructor(private http: HttpClient) { }

  likePost(postId: number): Observable<any> {
    return this.http.post(`${APIUrl}/posts/${postId}/like`, {});
  }

  likeComment(commentId: number): Observable<any> {
    return this.http.post(`${APIUrl}/comments/${commentId}/like`, {});
  }

}
