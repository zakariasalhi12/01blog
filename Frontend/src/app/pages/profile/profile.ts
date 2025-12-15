import { Component, OnInit, signal } from '@angular/core';
import { MainHeader } from '../../components/main-header/main-header';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { PostCard } from '../../components/post-card/post-card';
import { ProfileService } from '../../services/profile.service';
import { BackedURL } from '../../../environments/environment';

@Component({
  selector: 'app-profile',
  imports: [MainHeader, RouterLink, CommonModule, MatIcon, PostCard],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  profileId = 0;
  profile = signal<any>(null);
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
        error: (err) => console.error('Failed to fetch post', err)
      })

    });
  }


    getFullFileUrl(fileUrl: string | undefined): string | undefined {
      if (!fileUrl) return undefined;
      return `${BackedURL}${fileUrl}`;
    }
  
    timeAgo(date: string | Date): string {
      const now = new Date();
      const past = new Date(date);
      const seconds = Math.floor((now.getTime() - past.getTime()) / 1000);
      const intervals: any = {
        year: 31536000,
        month: 2592000,
        week: 604800,
        day: 86400,
        hour: 3600,
        minute: 60,
        second: 1
      };
      for (const key in intervals) {
        const value = Math.floor(seconds / intervals[key]);
        if (value > 0) return value === 1 ? `1 ${key} ago` : `${value} ${key}s ago`;
      }
      return 'just now';
    }

}
