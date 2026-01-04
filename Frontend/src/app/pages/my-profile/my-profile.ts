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
  emailError = signal<string | null>(null);
  passwordError = signal<string | null>(null);

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
    this.emailError.set(null);
    this.passwordError.set(null);

    // Basic client-side validation
    const emailVal = this.email().trim();
    const passwordVal = this.password();

    const emailRegex = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
    if (emailVal && !emailRegex.test(emailVal)) {
      this.emailError.set('Please enter a valid email address');
      return;
    }

    if (passwordVal && passwordVal.trim().length > 0 && passwordVal.length < 6) {
      this.passwordError.set('Password must be at least 6 characters');
      return;
    }

    // Determine what actually changed
    const originalEmail = this.profile()?.email ?? '';
    const emailChanged = emailVal !== originalEmail;
    const passwordChanged = passwordVal.trim().length > 0;
    const avatarChanged = !!this.avatarFile();

    if (!emailChanged && !passwordChanged && !avatarChanged) {
      this.error.set('Nothing to change');
      return;
    }

    // Only include changed fields
    const payload: any = {};
    if (emailChanged) payload.email = emailVal;
    if (passwordChanged) payload.password = passwordVal;

    this.profileService.updateProfile(payload, this.avatarFile() ?? undefined).subscribe({
      next: (res) => {
        // Backend returns plain text like "User updated successfully"
        const msg = typeof res === 'string' ? res : 'Profile updated';
        this.success.set(msg);
        this.password.set('');
        // Clear avatarFile since upload may have been used
        this.avatarFile.set(null);
        this.fetchProfile();
        setTimeout(() => this.success.set(null), 3000);
      },
      error: (err) => {
        // If server returns structured validation errors, show them
        const errBody = err.error;
        if (errBody && errBody.errors) {
          const first = Object.values(errBody.errors)[0] as string;
          this.error.set(first || 'Failed to update profile');
        } else if (typeof errBody === 'string') {
          // Server might return plain text error
          this.error.set(errBody);
        } else {
          this.error.set(err?.error || 'Failed to update profile');
        }
      }
    });
  }
}
