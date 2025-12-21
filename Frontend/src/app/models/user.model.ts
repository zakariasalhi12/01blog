export interface User {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN' | 'BANNED';
}

export interface UsersResponse {
  users: User[];
  totalUsers: number;
  totalPages: number;
  currentPage: number;
}

