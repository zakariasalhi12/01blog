export interface Profile {
    username: string;
    avatarUrl: string;
    createdAt: string;
    subscribers: number;
    subscriptions: number;
    // Optional fields for the logged-in user view
    email?: string;
    age?: number;
    role?: string;
}