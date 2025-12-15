import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { Post } from '../../models/post.model';
import { BackedURL } from '../../../environments/environment';
import { LikesService } from '../../services/likes.service';
import { RouterLink } from "@angular/router";
import { timeAgo } from '../../lib/timeAgo.helper';
import { getFullFileUrl } from '../../lib/getFullFileUrl.helper';
@Component({
  selector: 'app-post-card',
  imports: [MatIcon, CommonModule, RouterLink],
  standalone: true,
  templateUrl: './post-card.html',
  styleUrl: './post-card.css',
})
export class PostCard {
  timeAgo = timeAgo;
  getFullFileUrl = getFullFileUrl;

  constructor(private likesService : LikesService ,private cdr: ChangeDetectorRef) { }

  likePost(postId: number): void {
    this.likesService.likePost(postId).subscribe({
      next: (res) => {
        this.post.likesCount = res;
        this.post.likedByCurrentUser= !this.post.likedByCurrentUser;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error liking post', err);
      }
    });
  }

  @Input() post!: Post; // receives a post object from parent
  @Input() fullFileUrl?: string;    // the computed URL from HomeComponent
}
