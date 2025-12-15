import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PostService } from '../../services/post.service';
import { LikesService } from '../../services/likes.service';
import { CommentService } from '../../services/comment.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MainHeader } from '../../components/main-header/main-header';
import { MatIcon } from '@angular/material/icon';
import { BackedURL } from '../../../environments/environment';

@Component({
  selector: 'app-post',
  imports: [MainHeader, CommonModule, MatIcon, FormsModule , RouterLink],
  templateUrl: './post.html',
  styleUrls: ['./post.css'],
})
export class Post implements OnInit {
post = signal<any>(null);
postId = 0;
commentContent = '';

comments = signal<any[]>([]);  // store loaded comments
page = 0;                      // current page index
size = 10;                     // number of comments per request
loadingComments = false;       // loading state
allCommentsLoaded = false;     // flag when no more comments

constructor(
  private likesService: LikesService,
  private postService: PostService,
  private route: ActivatedRoute,
  private commentService: CommentService
) {}

ngOnInit(): void {
  this.postId = Number(this.route.snapshot.paramMap.get('id'));

  // load post
  this.postService.getSinglePost(this.postId).subscribe({
    next: (res) => this.post.set(res),
    error: (err) => console.error('Failed to fetch post', err)
  });

  // load first page of comments
  this.loadComments();
}

// Lazy load comments until finished
loadComments() {
  if (this.loadingComments || this.allCommentsLoaded) return;

  this.loadingComments = true;

  // call backend
this.commentService.getComment(this.postId, this.size, this.page).subscribe({
  next: (res) => {
    // if res is array
    const newComments = Array.isArray(res) ? res : res.comments;

    this.comments.update(curr => [...curr, ...newComments]);

    if (newComments.length === 0) this.allCommentsLoaded = true;

    this.page++;
    this.loadingComments = false;

    console.log('Updated comments signal:', this.comments()); // DEBUG
  },
  error: (err) => console.error(err)
});
}
  submitComment() {
    if (!this.commentContent.trim()) return;

    this.commentService.createComment({
      content: this.commentContent,
      postId: this.postId
    }).subscribe({
      next: (res) => {
        this.comments.update(curr => [res, ...curr]); // add new comment at top
        this.commentContent = '';
      },
      error: (err) => console.error('Error creating comment', err)
    });
    window.location.reload();
  }

  likePost(postId: number) {
    this.likesService.likePost(postId).subscribe({
      next: (res) => {
        this.post.update(post => {
          if (!post) return post;
          return { ...post, likesCount: res, likedByCurrentUser: !post.likedByCurrentUser };
        });
      },
      error: (err) => console.error('Error liking post', err)
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
