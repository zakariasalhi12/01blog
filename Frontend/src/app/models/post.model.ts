export interface Post {
  id: number;
  title: string;
  content: string;
  author: string;
  createdAt: string;
  likesCount: number;
  commentsCount: number;
  fileUrl?: string;
  avatar: string;
  mediaType?: 'image' | 'video' | 'unknown';
  likedByCurrentUser: boolean;
  owner :boolean;
  authorId : number;
}

export interface PostsResponse {
  posts: Post[];
  totalPosts: number;
  totalPages: number;
  currentPage: number;
}