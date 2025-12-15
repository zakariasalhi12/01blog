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

    checksub(Id: number): Observable<boolean> {
      return this.http.get<boolean>(`${APIUrl}/profile/subscribe/check?id=${Id}`, {});
    }

    sub(Id: number): Observable<any> {
      return this.http.get<any>(`${APIUrl}/profile/subscribe?id=${Id}`, {});
    }
}
