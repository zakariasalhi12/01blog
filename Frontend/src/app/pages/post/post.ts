import { Component, OnInit, signal } from '@angular/core';
import { MainHeader } from '../../components/main-header/main-header';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { PostService } from '../../services/post.service';
import { ActivatedRoute } from '@angular/router';
import { BackedURL } from '../../../environments/environment';
import { LikesService } from '../../services/likes.service';

@Component({
  selector: 'app-post',
  imports: [MainHeader , CommonModule , MatIcon],
  templateUrl: './post.html',
  styleUrl: './post.css',
})
export class Post implements OnInit {
  post = signal<any>(null); 
  postId = 0;
  constructor(private likesService : LikesService, private postService : PostService , private route: ActivatedRoute) {}


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
      return `${BackedURL}${fileUrl}`; // no extra slash if your API already returns '/uploads/...'
    }
  
likePost(postId: number): void {
  this.likesService.likePost(postId).subscribe({
    next: (res) => {
      this.post.update(post => {
        if (!post) return post;

        return {
          ...post,
          likesCount: res,
          likedByCurrentUser: !post.likedByCurrentUser
        };
      });
    },
    error: (err) => {
      console.error('Error liking post', err);
    }
  });
}

  ngOnInit(): void {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    this.postService.getSinglePost(this.postId).subscribe({
      next: (res) => {
        this.post.set(res); // <-- update the signal
      },
      error: (err) => {
        console.error('Failed to fetch post', err);
      }
    });
  }
  

}
