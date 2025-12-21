import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../services/admin.service';
import { User } from '../../models/user.model';
import { Report, ReportsResponse } from '../../models/report.model';
import { MainHeader } from '../../components/main-header/main-header';
import { ReportService } from '../../services/report.service';
import { timeAgo } from '../../lib/timeAgo.helper';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, MainHeader],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin implements OnInit {
  users: User[] = [];
  reports: Report[] = [];
  loading = false;
  loadingReports = false;
  error: string | null = null;
  success: string | null = null;
  page = 0;
  size = 10;
  totalPages = 0;
  totalUsers = 0;
  currentPage = 0;
  
  reportsPage = 0;
  reportsSize = 10;
  reportsTotalPages = 0;
  reportsTotalReports = 0;
  reportsCurrentPage = 0;
  
  activeTab: 'users' | 'reports' = 'users';
  timeAgo = timeAgo;

  constructor(
    private adminService: AdminService,
    private reportService: ReportService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadReports();
  }

  loadUsers(): void {
    this.loading = true;
    this.error = null;
    this.adminService.getAllUsers(this.page, this.size).subscribe({
      next: (response) => {
        this.users = response.users;
        this.totalPages = response.totalPages;
        this.totalUsers = response.totalUsers;
        this.currentPage = response.currentPage;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load users. Please try again.';
        this.loading = false;
        console.error('Error loading users:', err);
      }
    });
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.page = page;
      this.loadUsers();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.goToPage(this.currentPage + 1);
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.goToPage(this.currentPage - 1);
    }
  }

  updateRole(userId: number, role: 'USER' | 'ADMIN' | 'BANNED'): void {
    this.error = null;
    this.success = null;
    
    this.adminService.updateUserRole(userId, role).subscribe({
      next: () => {
        this.success = 'User role updated successfully';
        // Reload current page to reflect changes
        this.loadUsers();
        setTimeout(() => {
          this.success = null;
        }, 3000);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to update user role. Please try again.';
        console.error('Error updating user role:', err);
      }
    });
  }

  banUser(user: User): void {
    if (confirm(`Are you sure you want to ban ${user.username}?`)) {
      this.updateRole(user.id, 'BANNED');
    }
  }

  makeAdmin(user: User): void {
    if (confirm(`Are you sure you want to make ${user.username} an admin?`)) {
      this.updateRole(user.id, 'ADMIN');
    }
  }

  makeNormalUser(user: User): void {
    if (confirm(`Are you sure you want to make ${user.username} a normal user?`)) {
      this.updateRole(user.id, 'USER');
    }
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

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(0, this.currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(this.totalPages - 1, startPage + maxPagesToShow - 1);
    
    // Adjust start if we're near the end
    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  }

  loadReports(): void {
    this.loadingReports = true;
    this.error = null;
      this.adminService.getAllReports(this.reportsPage, this.reportsSize).subscribe({
      next: (response: ReportsResponse) => {
        this.reports = response.reports;
        this.reportsTotalPages = response.totalPages;
        this.reportsTotalReports = response.totalReports;
        this.reportsCurrentPage = response.currentPage;
        this.loadingReports = false;
      },
      error: (err) => {
        this.error = 'Failed to load reports. Please try again.';
        this.loadingReports = false;
        console.error('Error loading reports:', err);
      }
    });
  }

  goToReportsPage(page: number): void {
    if (page >= 0 && page < this.reportsTotalPages) {
      this.reportsPage = page;
      this.loadReports();
    }
  }

  nextReportsPage(): void {
    if (this.reportsCurrentPage < this.reportsTotalPages - 1) {
      this.goToReportsPage(this.reportsCurrentPage + 1);
    }
  }

  previousReportsPage(): void {
    if (this.reportsCurrentPage > 0) {
      this.goToReportsPage(this.reportsCurrentPage - 1);
    }
  }

  deleteReport(reportId: number): void {
    if (confirm('Are you sure you want to delete this report?')) {
      this.adminService.deleteReport(reportId).subscribe({
        next: () => {
          this.success = 'Report deleted successfully';
          this.loadReports();
          setTimeout(() => {
            this.success = null;
          }, 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to delete report. Please try again.';
          console.error('Error deleting report:', err);
        }
      });
    }
  }

  getReportsPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(0, this.reportsCurrentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(this.reportsTotalPages - 1, startPage + maxPagesToShow - 1);
    
    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  }
}
