import { Injectable } from '@angular/core';

import { Profile } from '../models/profile.model'
import { Observable } from 'rxjs';
import { APIUrl } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {

  constructor(private http : HttpClient) {}

    profile(Id: number): Observable<Profile> {
      return this.http.get<Profile>(`${APIUrl}/profile/${Id}`, {});
    }

    // my_profile(postId: number): Observable<Profile> {
      // return this.http.post(`${APIUrl}/posts/${postId}/like`, {});
    // }

    // subscribe(postId: number): Observable<any> {
      // return this.http.post(`${APIUrl}/posts/${postId}/like`, {});
    // }
  
    check(commentId: number): Observable<any> {
      return this.http.post(`${APIUrl}/comments/${commentId}/like`, {});
    }

}
