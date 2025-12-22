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
import { AuthService } from '../../services/auth.service';
import { ReportService } from '../../services/report.service';

@Component({
  selector: 'app-post',
  imports: [MainHeader, CommonModule, MatIcon, FormsModule, RouterLink],
  templateUrl: './post.html',
  styleUrls: ['./post.css'],
})
export class Post implements OnInit {
  post = signal<any>(null);
  postId = 0;
  commentContent = '';
  currentUser: any = null;

  comments = signal<any[]>([]);
  page = 0;
  size = 10;
  loadingComments = false;
  allCommentsLoaded = false;

  // UI State
  showMenu = false;
  showEditModal = false;
  showReportModal = false;

  // Edit State
  editTitle = '';
  editContent = '';
  editFile: File | null = null;
  editPreviewUrl: string | null = null;

  // Report State
  reportReason = '';

  timeAgo = timeAgo;
  getFullFileUrl = getFullFileUrl;

  constructor(
    private likesService: LikesService,
    private postService: PostService,
    private route: ActivatedRoute,
    private commentService: CommentService,
    private authService: AuthService,
    private reportService: ReportService
  ) { }

  ngOnInit(): void {
    // Get current user
    this.authService.logged().subscribe({
      next: (user) => this.currentUser = user,
      error: () => this.currentUser = null
    });

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');

      if (!id) {
        console.error('Post ID missing in route');
        return;
      }

      this.postId = Number(id);

      this.postService.getSinglePost(this.postId).subscribe({
        next: (res) => {
          // Check if reported by current user (this should ideally come from backend or separate call)
          // For now assume res has reportedByCurrentUser or we fetch generic
          this.post.set(res);
        },
        error: (err) => console.error('Failed to fetch post', err)
      });

      this.loadComments();
    });
  }

  // ... (loadComments existing code) ...
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
        location.reload();  
      },
      error: (err) => console.error('Error creating comment', err)
    });
  }
  deletePost() {
    
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

  // --- Menu Logic ---
  toggleMenu(): void {
    this.showMenu = !this.showMenu;
  }

  closeMenu(): void {
    this.showMenu = false;
  }

  // --- Edit Logic ---
  openEditModal(): void {
    const p = this.post();
    this.editTitle = p.title;
    this.editContent = p.content;
    this.editPreviewUrl = p.fileUrl ? this.getFullFileUrl(p.fileUrl) : null;
    this.showEditModal = true;
    this.closeMenu();
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editFile = null;
  }

  onEditFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.editFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.editPreviewUrl = reader.result as string;
      };
      reader.readAsDataURL(this.editFile);
    }
  }

  updatePost(): void {
    if (!this.editTitle.trim() || !this.editContent.trim()) return;

    this.postService.updatePost(
      this.postId,
      this.editTitle,
      this.editContent,
      this.editFile ?? undefined
    ).subscribe({
      next: () => {
        this.post.update(p => ({
          ...p,
          title: this.editTitle,
          content: this.editContent,
          fileUrl: (this.editFile && this.editPreviewUrl) ? this.editPreviewUrl : p.fileUrl
        }));
        this.closeEditModal();
        alert('Post updated successfully');
      },
      error: (err) => {
        console.error('Update failed', err);
        alert('Failed to update post');
      }
    });
  }

  // --- Report Logic ---
  openReportModal(): void {
    this.showReportModal = true;
    this.closeMenu();
  }

  closeReportModal(): void {
    this.showReportModal = false;
    this.reportReason = '';
  }

  reportPost(): void {
    if (!this.reportReason.trim()) return;

    this.reportService.reportPost(this.postId, this.reportReason).subscribe({
      next: () => {
        alert('Post reported successfully');
        this.closeReportModal();
      },
      error: (err) => {
        console.error('Report failed', err);
        alert('Failed to report post');
      }
    });
  }
}
