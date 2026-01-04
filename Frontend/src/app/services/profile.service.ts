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

    me(): Observable<Profile> {
      return this.http.get<Profile>(`${APIUrl}/profile/me`, {});
    }

    checksub(Id: number): Observable<boolean> {
      return this.http.get<boolean>(`${APIUrl}/profile/subscribe/check?id=${Id}`, {});
    }

    sub(Id: number): Observable<any> {
      return this.http.get<any>(`${APIUrl}/profile/subscribe?id=${Id}`, {});
    }

    checkReported(Id: number): Observable<{ reported: boolean, reportId?: number }> {
      return this.http.get<{ reported: boolean, reportId?: number }>(`${APIUrl}/profile/${Id}/report/check`);
    }

    updateProfile(data: Partial<Profile> & { password?: string }, avatar?: File): Observable<string> {
      const formData = new FormData();
      formData.append('updateData', new Blob([JSON.stringify(data)], { type: 'application/json' }));
      if (avatar) {
        formData.append('avatar', avatar, avatar.name);
      }
      // Backend returns plain text on success (e.g. "User updated successfully").
      // Request responseType 'text' so Angular does not try to parse JSON.
      return this.http.put(`${APIUrl}/profile`, formData, { responseType: 'text' });
    }
}
