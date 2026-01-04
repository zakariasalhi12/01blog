import { ChangeDetectorRef, Component, HostListener, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MainHeader } from '../../components/main-header/main-header';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { PostCard } from '../../components/post-card/post-card';
import { CommentsSection } from '../../components/comments-section/comments-section';
import { ProfileService } from '../../services/profile.service';
import { timeAgo } from '../../lib/timeAgo.helper';
import { getFullFileUrl } from '../../lib/getFullFileUrl.helper';
import { Post } from '../../models/post.model';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';
import { ReportService } from '../../services/report.service';

@Component({
  selector: 'app-profile',
  imports: [MainHeader, CommonModule, MatIcon, PostCard, CommentsSection, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  profileId = 0;
  profile = signal<any>(null);
  sub = signal(false);
  isOwnProfile = signal(false);
  profileReportId: number | null = null;
  profileReported = signal(false);
  // report modal state
  reportModalOpen = signal(false);
  reportReason = signal('');
  reportStep = signal(0); // 0 = edit, 1 = confirm
  reportLoading = signal(false);

  posts: Post[] = [];
  page = 0;
  size = 5;
  loading = false;
  hasMore = true;
  userReports: Map<number, number> = new Map(); // postId -> reportId

  // comments UI
  activePostId: number | null = null;
  commentsOpen = false;

  timeAgo = timeAgo;
  getFullFileUrl = getFullFileUrl;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private profileService: ProfileService,
    private cdr: ChangeDetectorRef,
    private postService: PostService,
    private authService: AuthService,
    private reportService: ReportService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');

      if (!id) {
        console.error('Post ID missing in route');
        return;
      }

      this.profileId = Number(id);

      // Check if viewing own profile
      this.authService.logged().subscribe({
        next: (user) => {
          if (user && user.id === this.profileId) {
            this.isOwnProfile.set(true);
            this.router.navigate(['/profile/me']);
          } else {
            this.isOwnProfile.set(false);
          }
        }
      });

      this.profileService.profile(this.profileId).subscribe({
        next: (res) => this.profile.set(res),
        error: (err) => {
          if (err.status === 404) {
            this.router.navigate(['/not-found']);
          } else {
            console.error('Failed to load profile', err);
          }
        }
      });

      // check if current user already reported this profile
      this.profileService.checkReported(this.profileId).subscribe({
        next: (res) => {
          if (res.reported) {
            this.profileReported.set(true);
            if (res.reportId) this.profileReportId = res.reportId;
          } else {
            this.profileReported.set(false);
            this.profileReportId = null;
          }
        },
        error: () => {
          // ignore errors (not logged in etc.) and leave as not reported
        }
      });

      this.checksub();
      this.loadUserReports();
      this.loadPosts();

    });
  }

  openComments(postId: number) {
    if (this.commentsOpen && this.activePostId === postId) {
      this.activePostId = null;
      this.commentsOpen = false;
      return;
    }

    this.activePostId = postId;
    this.commentsOpen = true;
  }

  closeComments() {
    this.commentsOpen = false;
    this.activePostId = null;
  }

  checksub(): void {
    this.profileService.checksub(this.profileId).subscribe({
      next: (res) => this.sub.set(res),
      // error: (err) => console.error('Failed to fetch sub checker', err)
    })
  }

  subscribe(): void {
    this.profileService.sub(this.profileId).subscribe({
      next: (res) => this.checksub(),
      // error: (err) => console.error('Failed to fetch sub checker', err)
    })
  }

  loadUserReports(): void {
    this.reportService.getUserReports(0, 100).subscribe({
      next: (res) => {
        res.reports.forEach(report => {
          if (report.postId) {
            this.userReports.set(report.postId, report.id);
          }
        });
      },
      error: (err) => {
        console.error('Error loading user reports', err);
      }
    });
  }

  reportProfile(): void {
    // open modal for reporting
    if (this.isOwnProfile()) return;
    this.reportReason.set('');
    this.reportStep.set(0);
    this.reportModalOpen.set(true);
  }

  undoReport(): void {
    if (!this.profileReportId) return;
    if (!confirm('Are you sure you want to undo this report?')) return;

    this.reportService.deleteUserReport(this.profileReportId).subscribe({
      next: () => {
        alert('Report removed successfully');
        this.profileReportId = null;
        this.profileReported.set(false);
      },
      error: (err) => {
        console.error('Failed to delete report', err);
        alert('Failed to remove report');
      }
    });
  }

  // Report modal handlers
  openReportModal(): void {
    if (this.isOwnProfile()) return;
    this.reportReason.set('');
    this.reportStep.set(0);
    this.reportModalOpen.set(true);
  }

  closeReportModal(): void {
    this.reportModalOpen.set(false);
    this.reportReason.set('');
    this.reportStep.set(0);
    this.reportLoading.set(false);
  }

  onReportSubmitClick(): void {
    const reason = this.reportReason();
    if (!reason || !reason.trim()) {
      alert('Please provide a reason for reporting');
      return;
    }
    this.reportStep.set(1);
  }

  confirmReport(): void {
    const reason = this.reportReason();
    if (!reason || !reason.trim()) return;
    this.reportLoading.set(true);
    this.reportService.reportUser(this.profileId, reason.trim()).subscribe({
      next: (res) => {
        this.reportLoading.set(false);
        this.profileReported.set(true);
        if (res && res.reportId) this.profileReportId = res.reportId;
        this.closeReportModal();
        alert('Profile reported successfully');
      },
      error: (err) => {
        this.reportLoading.set(false);
        console.error('Failed to report profile', err);
        alert(err.error?.message || 'Failed to report profile');
      }
    });
  }

  loadPosts() {
    if (this.loading || !this.hasMore) return;

    this.loading = true;

    this.postService.getbyAuthor(this.profileId, this.page, this.size).subscribe({
      next: res => {
        this.posts = [...this.posts, ...res.posts];

        this.hasMore = this.page < res.totalPages - 1;
        this.page++;

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    const pos = window.innerHeight + window.scrollY;
    const max = document.body.scrollHeight - 300;

    if (pos >= max) {
      this.loadPosts();
    }
  }

}
