import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { User } from '../../models/user.model';
import { Report, ReportsResponse } from '../../models/report.model';
import { MainHeader } from '../../components/main-header/main-header';
import { ReportService } from '../../services/report.service';
import { timeAgo } from '../../lib/timeAgo.helper';

import { ConfirmationModal } from '../../components/confirmation-modal/confirmation-modal';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, MainHeader, ConfirmationModal],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin implements OnInit {
  // Signals for reactive state
  users = signal<User[]>([]);
  reports = signal<Report[]>([]);
  loading = signal(false);
  loadingReports = signal(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  // Pagination signals
  page = signal(0);
  size = signal(10);
  totalPages = signal(0);
  totalUsers = signal(0);
  currentPage = signal(0);

  reportsPage = signal(0);
  reportsSize = signal(10);
  reportsTotalPages = signal(0);
  reportsTotalReports = signal(0);
  reportsCurrentPage = signal(0);

  // Posts state
  posts = signal<any[]>([]);
  loadingPosts = signal(false);
  postsPage = signal(0);
  postsSize = signal(10);
  postsTotalPages = signal(0);
  postsTotalPosts = signal(0);
  postsCurrentPage = signal(0);

  activeTab = signal<'users' | 'reports' | 'posts'>('users');

  // Computed signals
  pageNumbers = computed(() => {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    const current = this.currentPage();
    const total = this.totalPages();
    let startPage = Math.max(0, current - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(total - 1, startPage + maxPagesToShow - 1);

    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  });

  reportsPageNumbers = computed(() => {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    const current = this.reportsCurrentPage();
    const total = this.reportsTotalPages();
    let startPage = Math.max(0, current - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(total - 1, startPage + maxPagesToShow - 1);

    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  });

  postsPageNumbers = computed(() => {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    const current = this.postsCurrentPage();
    const total = this.postsTotalPages();
    let startPage = Math.max(0, current - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(total - 1, startPage + maxPagesToShow - 1);

    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  });

  timeAgo = timeAgo;

  constructor(
    private adminService: AdminService,
    private reportService: ReportService
  ) { }

  ngOnInit(): void {
    this.loadUsers();
    this.loadReports();
    this.loadPosts();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.error.set(null);
    this.adminService.getAllUsers(this.page(), this.size()).subscribe({
      next: (response) => {
        this.users.set(response.users);
        this.totalPages.set(response.totalPages);
        this.totalUsers.set(response.totalUsers);
        this.currentPage.set(response.currentPage);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load users. Please try again.');
        this.loading.set(false);
        console.error('Error loading users:', err);
      }
    });
  }

  goToPage(pageNum: number): void {
    if (pageNum >= 0 && pageNum < this.totalPages()) {
      this.page.set(pageNum);
      this.loadUsers();
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.goToPage(this.currentPage() + 1);
    }
  }

  previousPage(): void {
    if (this.currentPage() > 0) {
      this.goToPage(this.currentPage() - 1);
    }
  }

  updateRole(userId: number, role: 'USER' | 'ADMIN' | 'BANNED'): void {
    this.error.set(null);
    this.success.set(null);

    this.adminService.updateUserRole(userId, role).subscribe({
      next: () => {
        this.success.set('User role updated successfully');
        this.loadUsers();
        setTimeout(() => {
          this.success.set(null);
        }, 3000);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Failed to update user role. Please try again.');
        console.error('Error updating user role:', err);
      }
    });
  }

  banUser(user: User): void {
    this.confirmAction(
      'Ban User',
      `Are you sure you want to ban ${user.username}?`,
      () => this.updateRole(user.id, 'BANNED')
    );
  }

  makeAdmin(user: User): void {
    this.confirmAction(
      'Make Admin',
      `Are you sure you want to make ${user.username} an admin?`,
      () => this.updateRole(user.id, 'ADMIN')
    );
  }

  makeNormalUser(user: User): void {
    this.confirmAction(
      'Convert to User',
      `Are you sure you want to make ${user.username} a normal user?`,
      () => this.updateRole(user.id, 'USER')
    );
  }

  getRoleBadgeClass(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'badge-admin';
      case 'BANNED':
        return 'badge-banned';
      default:
        return 'badge-user';
    }
  }

  loadReports(): void {
    this.loadingReports.set(true);
    this.error.set(null);
    this.adminService.getAllReports(this.reportsPage(), this.reportsSize()).subscribe({
      next: (response: ReportsResponse) => {
        this.reports.set(response.reports);
        this.reportsTotalPages.set(response.totalPages);
        this.reportsTotalReports.set(response.totalReports);
        this.reportsCurrentPage.set(response.currentPage);
        this.loadingReports.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load reports. Please try again.');
        this.loadingReports.set(false);
        console.error('Error loading reports:', err);
      }
    });
  }

  goToReportsPage(pageNum: number): void {
    if (pageNum >= 0 && pageNum < this.reportsTotalPages()) {
      this.reportsPage.set(pageNum);
      this.loadReports();
    }
  }

  nextReportsPage(): void {
    if (this.reportsCurrentPage() < this.reportsTotalPages() - 1) {
      this.goToReportsPage(this.reportsCurrentPage() + 1);
    }
  }

  previousReportsPage(): void {
    if (this.reportsCurrentPage() > 0) {
      this.goToReportsPage(this.reportsCurrentPage() - 1);
    }
  }

  deleteReport(reportId: number): void {
    this.confirmAction(
      'Delete Report',
      'Are you sure you want to delete this report?',
      () => {
        this.adminService.deleteReport(reportId).subscribe({
          next: () => {
            this.success.set('Report deleted successfully');
            this.loadReports();
            setTimeout(() => {
              this.success.set(null);
            }, 3000);
          },
          error: (err) => {
            this.error.set(err.error?.message || 'Failed to delete report. Please try again.');
            console.error('Error deleting report:', err);
          }
        });
      }
    );
  }

  // Posts management methods
  loadPosts(): void {
    this.loadingPosts.set(true);
    this.error.set(null);
    this.adminService.getAllPosts(this.postsPage(), this.postsSize()).subscribe({
      next: (response: any) => {
        this.posts.set(response.posts);
        this.postsTotalPages.set(response.totalPages);
        this.postsTotalPosts.set(response.totalPosts);
        this.postsCurrentPage.set(response.currentPage);
        this.loadingPosts.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load posts. Please try again.');
        this.loadingPosts.set(false);
        console.error('Error loading posts:', err);
      }
    });
  }

  goToPostsPage(pageNum: number): void {
    if (pageNum >= 0 && pageNum < this.postsTotalPages()) {
      this.postsPage.set(pageNum);
      this.loadPosts();
    }
  }

  nextPostsPage(): void {
    if (this.postsCurrentPage() < this.postsTotalPages() - 1) {
      this.goToPostsPage(this.postsCurrentPage() + 1);
    }
  }

  previousPostsPage(): void {
    this.goToPostsPage(this.postsCurrentPage() - 1);
  }
  // Confirmation Modal
  confirmModalShow = signal(false);
  confirmModalTitle = signal('');
  confirmModalMessage = signal('');
  confirmModalPendingAction = signal<(() => void) | null>(null);

  confirmAction(title: string, message: string, action: () => void): void {
    this.confirmModalTitle.set(title);
    this.confirmModalMessage.set(message);
    this.confirmModalPendingAction.set(action);
    this.confirmModalShow.set(true);
  }

  onModalConfirm(): void {
    const action = this.confirmModalPendingAction();
    if (action) {
      action();
    }
    this.closeModal();
  }

  onModalCancel(): void {
    this.closeModal();
  }

  closeModal(): void {
    this.confirmModalShow.set(false);
    this.confirmModalPendingAction.set(null);
  }

  deletePost(postId: number, postTitle: string): void {
    this.confirmAction(
      'Delete Post',
      `Are you sure you want to delete the post "${postTitle}"?`,
      () => {
        this.adminService.deletePost(postId).subscribe({
          next: () => {
            this.success.set('Post deleted successfully');
            this.loadPosts();
            setTimeout(() => {
              this.success.set(null);
            }, 3000);
          },
          error: (err) => {
            this.error.set(err.error?.message || 'Failed to delete post. Please try again.');
            console.error('Error deleting post:', err);
          }
        });
      }
    );
  }

  togglePostVisibility(postId: number, currentVisible: boolean, postTitle: string): void {
    const action = currentVisible ? 'hide' : 'show';
    this.confirmAction(
      `${action.charAt(0).toUpperCase() + action.slice(1)} Post`,
      `Are you sure you want to ${action} the post "${postTitle}"?`,
      () => {
        this.adminService.togglePostVisibility(postId, !currentVisible).subscribe({
          next: () => {
            this.success.set(`Post visibility updated successfully`);
            this.loadPosts();
            setTimeout(() => {
              this.success.set(null);
            }, 3000);
          },
          error: (err) => {
            this.error.set(err.error?.message || 'Failed to update post visibility. Please try again.');
            console.error('Error updating post visibility:', err);
          }
        });
      }
    );
  }
}
