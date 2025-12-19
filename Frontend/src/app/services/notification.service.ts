import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NotificationResponse } from '../models/notification.model';
import { APIUrl } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NotificationsService {
  
  constructor(private http: HttpClient) {}

  getNotifications(page : number = 0 , size: number = 10): Observable<NotificationResponse>{
      return this.http.get<NotificationResponse>(`${APIUrl}/notifications?page=${page}&size=${size}`)
  }

  checkForNotifications(): Observable<boolean> {
      return this.http.get<boolean>(`${APIUrl}/notifications/check`)
  }
}
