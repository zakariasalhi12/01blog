import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { Post } from '../../models/post.model';
import { BackedURL } from '../../../environments/environment';
import { LikesService } from '../../services/likes.service';
import { ReportService } from '../../services/report.service';
import { PostService } from '../../services/post.service';
import { RouterLink } from "@angular/router";
import { timeAgo } from '../../lib/timeAgo.helper';
import { getFullFileUrl } from '../../lib/getFullFileUrl.helper';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-post-card',
  imports: [MatIcon, CommonModule, RouterLink, FormsModule],
  standalone: true,
  templateUrl: './post-card.html',
  styleUrl: './post-card.css',
})
export class PostCard implements OnInit {
  timeAgo = timeAgo;
  getFullFileUrl = getFullFileUrl;
  showEditModal = false;
  showReportModal = false;
  editTitle = '';
  editContent = '';
  reportReason = '';
  editFile: File | null = null;
  editPreviewUrl: string | null = null;

  constructor(
    private likesService: LikesService,
    private cdr: ChangeDetectorRef,
    private reportService: ReportService,
    private postService: PostService
  ) { }

  @Input() post!: Post;
  @Input() fullFileUrl?: string;
  @Input() userReports?: Map<number, number>; // postId -> reportId (optional)

  ngOnInit(): void {
    // Check if this post is reported by current user
    if (this.userReports) {
      const reportId = this.userReports.get(this.post.id);
      if (reportId) {
        this.post.reportedByCurrentUser = true;
        this.post.reportId = reportId;
      }
    }
  }

  likePost(postId: number): void {
    this.likesService.likePost(postId).subscribe({
      next: (res) => {
        this.post.likesCount = res;
        this.post.likedByCurrentUser = !this.post.likedByCurrentUser;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error liking post', err);
      }
    });
  }

  openEditModal(): void {
    this.editTitle = this.post.title;
    this.editContent = this.post.content;
    const preview = this.post.fileUrl ? this.getFullFileUrl(this.post.fileUrl) : null;
    this.editPreviewUrl = preview ?? null;
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editTitle = '';
    this.editContent = '';
    this.editFile = null;
    this.editPreviewUrl = null;
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
      this.post.id,
      this.editTitle,
      this.editContent,
      this.editFile ?? undefined
    ).subscribe({
      next: () => {
        this.post.title = this.editTitle;
        this.post.content = this.editContent;
        if (this.editFile && this.editPreviewUrl) {
          this.post.fileUrl = this.editPreviewUrl;
        }
        this.closeEditModal();
        window.location.reload();
      },
      error: (err) => {
        console.error('Error updating post', err);
        alert('Failed to update post');
      }
    });
  }

  openReportModal(): void {
    this.showReportModal = true;
  }

  closeReportModal(): void {
    this.showReportModal = false;
    this.reportReason = '';
  }

  reportPost(): void {
    if (!this.reportReason.trim()) {
      alert('Please provide a reason for reporting');
      return;
    }

    this.reportService.reportPost(this.post.id, this.reportReason).subscribe({
      next: () => {
        alert('Post reported successfully');
        this.post.reportedByCurrentUser = true;
        this.closeReportModal();
        // Reload to get report ID
        window.location.reload();
      },
      error: (err) => {
        console.error('Error reporting post', err);
        alert(err.error?.message || 'Failed to report post');
      }
    });
  }

  undoReport(): void {
    if (!this.post.reportId) return;

    this.reportService.deleteUserReport(this.post.reportId).subscribe({
      next: () => {
        this.post.reportedByCurrentUser = false;
        this.post.reportId = undefined;
        if (this.userReports) {
          this.userReports.delete(this.post.id);
        }
        alert('Report removed successfully');
      },
      error: (err) => {
        console.error('Error removing report', err);
        alert('Failed to remove report');
      }
    });
  }
}
