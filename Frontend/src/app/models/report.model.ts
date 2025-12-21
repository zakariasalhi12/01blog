export interface Report {
  id: number;
  postId?: number;
  postTitle?: string;
  reportedUserId?: number;
  reportedUsername?: string;
  reason: string;
  createdAt: string;
  reporterUsername: string;
}

export interface ReportsResponse {
  reports: Report[];
  totalReports: number;
  totalPages: number;
  currentPage: number;
}

