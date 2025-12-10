import { Component, Input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { Post } from '../../models/post.model';
import { BackedURL } from '../../../environments/environment';

@Component({
  selector: 'app-post-card',
  imports: [MatIcon, CommonModule],
  standalone: true,
  templateUrl: './post-card.html',
  styleUrl: './post-card.css',
})
export class PostCard {


  public timeAgo(date: string | Date): string {
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
      if (value > 0) {
        return value === 1 ? `1 ${key} ago` : `${value} ${key}s ago`;
      }
    }

    return 'just now';
  }


  public getFullFileUrl(fileUrl: string | undefined): string | undefined {
    if (!fileUrl) return undefined;
    console.log('Computed full file URL:', `${BackedURL}${fileUrl}`);
    return `${BackedURL}${fileUrl}`; // no extra slash if your API already returns '/uploads/...'
  }

  @Input() post!: Post; // receives a post object from parent
  @Input() fullFileUrl?: string;    // the computed URL from HomeComponent
}
