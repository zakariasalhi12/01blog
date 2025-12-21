import { Component, OnInit } from '@angular/core';
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
  imports: [MainHeader , MatIcon , CommonModule , FormsModule],
  templateUrl: './my-profile.html',
  styleUrl: './my-profile.css',
})
export class MyProfile implements OnInit {
  profile?: Profile;
  loading = false;
  success: string | null = null;
  error: string | null = null;

  username = '';
  email = '';
  age: number | null = null;
  password = '';
  avatarFile: File | null = null;
  avatarPreview: string | null = null;

  getFullUrl = getFullFileUrl

  constructor(private profileService: ProfileService) {}

  ngOnInit(): void {
    this.fetchProfile();
  }

  fetchProfile(): void {
    this.loading = true;
    this.profileService.me().subscribe({
      next: (p) => {
        this.profile = p;
        this.username = p.username;
        this.email = p.email ?? '';
        this.age = p.age ?? null;
        this.avatarPreview = p.avatarUrl ? getFullFileUrl(p.avatarUrl) : null;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load profile';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.avatarFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.avatarPreview = reader.result as string;
      };
      reader.readAsDataURL(this.avatarFile);
    }
  }

  save(): void {
    this.error = null;
    this.success = null;

    const payload: any = {
      username: this.username,
      email: this.email,
    };
    if (this.age !== null) payload.age = this.age;
    if (this.password.trim()) payload.password = this.password;

    this.profileService.updateProfile(payload, this.avatarFile ?? undefined).subscribe({
      next: () => {
        this.success = 'Profile updated';
        this.password = '';
        this.fetchProfile();
        setTimeout(() => this.success = null, 3000);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to update profile';
        console.error(err);
      }
    });
  }
}
