import { Component, OnInit, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { Router, RouterLink } from "@angular/router";
import { LogoutService } from '../../services/logout.service';
import { AuthService } from '../../services/auth.service';
import { BackedURL } from '../../../environments/environment';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-main-header',
  standalone: true,
  imports: [RouterLink, MatIcon, CommonModule],
  templateUrl: './main-header.html',
  styleUrls: ['./main-header.css'],
})
export class MainHeader implements OnInit {
  constructor(private logoutService: LogoutService, private router : Router , private auth :AuthService) {}

  avatar = signal("");
  profile_popup = signal(false);

  ngOnInit(): void {
    this.auth.logged().subscribe({
      next: (res) => {
        this.avatar.set(`${BackedURL}${res.avatar}`)
      },
      error:(e) => {
        console.error(e);
      }
    })
  }

  onLogout(): void {
    localStorage.removeItem("token")
    this.logoutService.logout();
    this.router.navigate(['/login'])
  }

  togglePopup(): void {
    this.profile_popup.update((v) => !v);
  }
}