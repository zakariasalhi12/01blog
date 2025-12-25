import { Component, HostListener, OnInit, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { LogoutService } from '../../services/logout.service';
import { AuthService } from '../../services/auth.service';
import { BackedURL } from '../../../environments/environment';
import { CommonModule } from '@angular/common';
import { NotificationsService } from '../../services/notification.service';
import { Notification } from '../../models/notification.model';
import { getFullFileUrl } from '../../lib/getFullFileUrl.helper';
import { timeAgo } from '../../lib/timeAgo.helper';

@Component({
  selector: 'app-main-header',
  standalone: true,
  imports: [RouterLink, MatIcon, CommonModule],
  templateUrl: './main-header.html',
  styleUrls: ['./main-header.css'],
})
export class MainHeader implements OnInit {

  constructor(
    private logoutService: LogoutService,
    private router: Router,
    private auth: AuthService,
    private notificationsService: NotificationsService
  ) { }

  avatar = signal('');
  profile_popup = signal(false);
  userRole = signal<string>('');

  notifications: Notification[] = [];
  notifications_open = signal(false);

  needToSeen = signal(false);

  getFullFileUrl = getFullFileUrl;
  timeAgo = timeAgo;

  page = 0;
  size = 10;
  loading = false;

  ngOnInit(): void {
    this.loadNotifications();
    this.checkNotification()

    this.auth.logged().subscribe({
      next: (res) => {
        this.avatar.set(`${BackedURL}${res.avatar}`);
        this.userRole.set(res.role || '');
      },
      error: (e) => console.error(e),
    });
  }


  checkNotification() {
    this.notificationsService.checkForNotifications().subscribe({
      next: (res) => {
        this.needToSeen.set(res);
      }
    })
  }

  markNotificationAsSeen(n : Notification , notificationId: number) {
    if (!this.needToSeen()) return;
    this.notificationsService.markAsSeen(notificationId).subscribe({
      next: (res) => {
        n.seen = true;
        this.checkNotification()
      }
    })
  }

  onLogout(): void {
    localStorage.removeItem('token');
    this.logoutService.logout();
    this.router.navigate(['/login']);
  }

  togglePopup(): void {
    this.profile_popup.update(v => !v);
  }

  loadNotifications(): void {
    if (this.loading) return;

    this.loading = true;

    this.notificationsService
      .getNotifications(this.page, this.size)
      .subscribe({
        next: (res) => {
          this.notifications.push(...res.notifications);
          this.page++;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        },
      });
  }

  // Called from template (scroll event)
  onScroll(event: Event): void {
    const element = event.target as HTMLElement;

    if (element.scrollTop + element.clientHeight >= element.scrollHeight - 150) {
      this.loadNotifications();
    }
  }


  toggleNotifications(event: MouseEvent): void {
    event.stopPropagation();
    this.notifications_open.update(v => !v);
    this.checkNotification()
  }


  @HostListener('document:click')
  closeOnOutsideClick(): void {
    if (this.notifications_open()) {
      this.notifications_open.set(false);
    }
  }
}
