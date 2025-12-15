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
import { timeAgo } from '../../lib/timeAgo.helper';
import { getFullFileUrl } from '../../lib/getFullFileUrl.helper';

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

timeAgo = timeAgo;
getFullFileUrl = getFullFileUrl;

constructor(
  private likesService: LikesService,
  private postService: PostService,
  private route: ActivatedRoute,
  private commentService: CommentService
) {}

ngOnInit(): void {
  this.route.paramMap.subscribe(params => {
    const id = params.get('id');

    if (!id) {
      console.error('Post ID missing in route');
      return;
    }

    this.postId = Number(id);

    this.postService.getSinglePost(this.postId).subscribe({
      next: (res) => this.post.set(res),
      error: (err) => console.error('Failed to fetch post', err)
    });

    this.loadComments();
  });
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

}
