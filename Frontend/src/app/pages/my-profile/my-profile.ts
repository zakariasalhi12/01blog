import { Component, OnInit, signal, computed } from '@angular/core';
import { MainHeader } from "../../components/main-header/main-header";
import { MatIcon } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../../services/profile.service';
import { Profile } from '../../models/profile.model';
import { getFullFileUrl } from '../../lib/getFullFileUrl.helper';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { PostCard } from '../../components/post-card/post-card';
import { CommentsSection } from '../../components/comments-section/comments-section';

@Component({
  selector: 'app-my-profile',
  standalone: true,
  imports: [MainHeader, CommonModule, FormsModule, PostCard, CommentsSection],
  templateUrl: './my-profile.html',
  styleUrl: './my-profile.css',
})
export class MyProfile implements OnInit {

  // Signals for reactive state
  profile = signal<Profile | null>(null);
  loading = signal(false);
  // posts
  posts = signal<Post[]>([]);
  postsLoading = signal(false);
  postsPage = signal(0);
  postsTotalPages = signal(0);
  success = signal<string | null>(null);
  error = signal<string | null>(null);

  // comments UI
  activePostId: number | null = null;
  commentsOpen = false;

  // Only email and password are editable
  email = signal('');
  password = signal('');
  avatarFile = signal<File | null>(null);
  avatarPreview = signal<string | null>(null);

  // Computed signal for avatar URL
  avatarUrl = computed(() => {
    const preview = this.avatarPreview();
    if (preview) return preview;
    const profileAvatar = this.profile()?.avatarUrl;
    return profileAvatar ? getFullFileUrl(profileAvatar) : null;
  });

  getFullUrl = getFullFileUrl;

  constructor(private profileService: ProfileService, private postService: PostService) { }

  ngOnInit(): void {
    this.fetchProfile();
    this.fetchPosts();
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

  fetchProfile(): void {
    this.loading.set(true);
    this.profileService.me().subscribe({
      next: (p) => {
        this.profile.set(p);
        this.email.set(p.email ?? '');
        this.avatarPreview.set(p.avatarUrl ? getFullFileUrl(p.avatarUrl) : null);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load profile');
        console.error(err);
        this.loading.set(false);
      }
    });
  }

  fetchPosts(page: number = 0, size: number = 10): void {
    this.postsLoading.set(true);
    this.postService.getmyPosts(page, size).subscribe({
      next: (res) => {
        this.posts.set(res.posts ?? []);
        this.postsPage.set(res.currentPage ?? 0);
        this.postsTotalPages.set(res.totalPages ?? 0);
        this.postsLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load my posts', err);
        this.postsLoading.set(false);
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.avatarFile.set(file);
      const reader = new FileReader();
      reader.onload = () => {
        this.avatarPreview.set(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  }

  save(): void {
    this.error.set(null);
    this.success.set(null);

    // Only send email and password (if changed)
    const payload: any = {
      email: this.email(),
    };
    const passwordValue = this.password();
    if (passwordValue.trim()) payload.password = passwordValue;

    this.profileService.updateProfile(payload, this.avatarFile() ?? undefined).subscribe({
      next: () => {
        this.success.set('Profile updated');
        this.password.set('');
        this.fetchProfile();
        setTimeout(() => this.success.set(null), 3000);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Failed to update profile');
        console.error(err);
      }
    });
  }
}
