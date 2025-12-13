export interface comment {
    commentId: number;
    content: string;
    createdAt: string;
    likes: number;
    userId: number;
    username: string;
    avatar :string;
    owner: boolean;
}

export interface commentResponse {
    totalPages: number;
    comments: comment[];
}