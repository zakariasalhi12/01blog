import { Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommentService } from '../../services/comment.service';
import { FormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { timeAgo } from '../../lib/timeAgo.helper';
import { getFullFileUrl } from '../../lib/getFullFileUrl.helper';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-comments-section',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIcon],
  templateUrl: './comments-section.html',
  styleUrls: ['./comments-section.css'],
})
export class CommentsSection {
  commentContent = '';
  maxCommentLength = 1000;
  commentError: string | null = null;
  currentUser: any = null;

  @Input() postId!: number | null;
  @Input() visible = false;
  @Output() close = new EventEmitter<void>();

  comments = signal<any[]>([]);
  page = 0;
  size = 10;
  loadingComments = false;
  allCommentsLoaded = false;

  timeAgo = timeAgo;
  getFullFileUrl = getFullFileUrl;

  constructor(
    private commentService: CommentService,
    private authService: AuthService,
  ) { }

  ngOnInit() {
    this.authService.logged().subscribe({
      next: user => this.currentUser = user,
      error: () => this.currentUser = null
    });
  }

  ngOnChanges() {
    if (this.visible && this.postId != null) {
      this.reset();
      this.loadComments();
    }
  }

  reset() {
    this.comments.set([]);
    this.page = 0;
    this.allCommentsLoaded = false;
  }


  // ... (loadComments existing code) ...
  loadComments() {
    if (this.loadingComments || this.allCommentsLoaded) return;

    this.loadingComments = true;

    // call backend
    this.commentService.getComment(this.postId!, this.size, this.page).subscribe({
      next: (res) => {
        // if res is array
        const newComments = Array.isArray(res) ? res : res.comments;

        this.comments.update(curr => [...curr, ...newComments]);

        if (newComments.length === 0) this.allCommentsLoaded = true;

        this.page++;
        this.loadingComments = false;

      },
      error: (err) => console.error(err)
    });
  }

  submitComment() {
    const trimmed = this.commentContent.trim();
    const len = trimmed.length;
    if (len === 0) {
      this.commentError = 'Comment cannot be empty';
      return;
    }
    if (len > this.maxCommentLength) {
      this.commentError = `Comment must be at most ${this.maxCommentLength} characters`;
      return;
    }

    this.commentError = null;
    this.commentService.createComment({
      content: trimmed,
      postId: this.postId!
    }).subscribe({
      next: (res) => {
        location.reload();
      },
      error: (err) => {
        console.error('Error creating comment', err);
        const body = err.error;
        if (body && body.errors) {
          const first = Object.values(body.errors)[0] as string;
          this.commentError = first || 'Failed to create comment';
        } else {
          this.commentError = body?.message || 'Failed to create comment';
        }
      }
    });
  }

  deleteComment(commentId: number) {
    if (!confirm('Are you sure you want to delete this comment?')) return;

    this.commentService.deleteComment(commentId).subscribe({
      next: () => {
        this.comments.update(curr => curr.filter((c: any) => c.commentId !== commentId));
        alert('Comment deleted successfully');
      },
      error: (err) => {
        console.error('Delete failed', err);
        alert('Failed to delete comment');
      }
    });
  }
}
