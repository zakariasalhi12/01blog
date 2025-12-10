import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post, PostsResponse } from '../models/post.model';
import { APIUrl } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PostService {


  constructor(private http: HttpClient) { }

  getPosts(page: number = 0, size: number = 10): Observable<PostsResponse> {
    return this.http.get<PostsResponse>(`${APIUrl}/posts?page=${page}&size=${size}`);
  }

  createPost(title: string, content: string, file?: File): Observable<any> {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('content', content);

    if (file) {
      formData.append('file', file, file.name);
    }

    return this.http.post(`${APIUrl}/posts`, formData);
  }

}
