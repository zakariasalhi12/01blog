import { Component, OnInit, signal, computed } from '@angular/core';
import { MainHeader } from "../../components/main-header/main-header";
import { MatIcon } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../../services/profile.service';
import { Profile } from '../../models/profile.model';
import { getFullFileUrl } from '../../lib/getFullFileUrl.helper';

@Component({
  selector: 'app-my-profile',
  standalone: true,
  imports: [MainHeader, MatIcon, CommonModule, FormsModule],
  templateUrl: './my-profile.html',
  styleUrl: './my-profile.css',
})
export class MyProfile implements OnInit {

  // Signals for reactive state
  profile = signal<Profile | null>(null);
  loading = signal(false);
  success = signal<string | null>(null);
  error = signal<string | null>(null);

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

  constructor(private profileService: ProfileService) { }

  ngOnInit(): void {
    this.fetchProfile();
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
