export interface Notification {
    createdAt: string;
    notificationId: number;
    message: string;
    seen: boolean;
    user: User;
}

interface User {
    avatar: string;
    userId: number;
    username: string;
}

export interface NotificationResponse {
  notifications: Notification[];
  totalPosts: number;
  totalPages: number;
  currentPage: number;
}