import { Component, OnInit, signal } from '@angular/core';
import { MainHeader } from '../../components/main-header/main-header';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { PostCard } from '../../components/post-card/post-card';
import { ProfileService } from '../../services/profile.service';
import { BackedURL } from '../../../environments/environment';
import { timeAgo } from '../../lib/timeAgo.helper';
import { getFullFileUrl } from '../../lib/getFullFileUrl.helper';

@Component({
  selector: 'app-profile',
  imports: [MainHeader, RouterLink, CommonModule, MatIcon, PostCard],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  profileId = 0;
  profile = signal<any>(null);
  sub = signal(false);

  timeAgo = timeAgo;
  getFullFileUrl = getFullFileUrl;

  constructor(
    private route: ActivatedRoute,
    private profileService: ProfileService,
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');

      if (!id) {
        console.error('Post ID missing in route');
        return;
      }

      this.profileId = Number(id);

      // load profile
      this.profileService.profile(this.profileId).subscribe({
        next: (res) => this.profile.set(res),
        // error: (err) => console.error('Failed to fetch post', err)
      })

      this.checksub();

    });
  }

  checksub(): void {
      this.profileService.checksub(this.profileId).subscribe({
        next: (res) => this.sub.set(res),
      // error: (err) => console.error('Failed to fetch sub checker', err)
    }) 
  }

  subscribe() : void {
    this.profileService.sub(this.profileId).subscribe({
      next: (res) => this.checksub(),
      // error: (err) => console.error('Failed to fetch sub checker', err)
    })
  }

}
