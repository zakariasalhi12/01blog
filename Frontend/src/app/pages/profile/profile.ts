import { ChangeDetectorRef, Component, HostListener, OnInit, signal } from '@angular/core';
import { MainHeader } from '../../components/main-header/main-header';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { PostCard } from '../../components/post-card/post-card';
import { ProfileService } from '../../services/profile.service';
import { timeAgo } from '../../lib/timeAgo.helper';
import { getFullFileUrl } from '../../lib/getFullFileUrl.helper';
import { Post } from '../../models/post.model';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-profile',
  imports: [MainHeader, RouterLink, CommonModule, MatIcon, PostCard],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  profileId = 0;
  profile = signal<any>(null);
  sub = signal(false);

  posts: Post[] = [];
  page = 0;
  size = 5;
  loading = false;
  hasMore = true;

  timeAgo = timeAgo;
  getFullFileUrl = getFullFileUrl;

  constructor(
    private route: ActivatedRoute,
    private profileService: ProfileService,
    private cdr : ChangeDetectorRef,
    private postService : PostService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');

      if (!id) {
        console.error('Post ID missing in route');
        return;
      }

      this.profileId = Number(id);

      // load profile
      this.profileService.profile(this.profileId).subscribe({
        next: (res) => this.profile.set(res),
        // error: (err) => console.error('Failed to fetch post', err)
      })

      this.checksub();
      this.loadPosts();

    });
  }

  checksub(): void {
      this.profileService.checksub(this.profileId).subscribe({
        next: (res) => this.sub.set(res),
      // error: (err) => console.error('Failed to fetch sub checker', err)
    }) 
  }

  subscribe() : void {
    this.profileService.sub(this.profileId).subscribe({
      next: (res) => this.checksub(),
      // error: (err) => console.error('Failed to fetch sub checker', err)
    })
  }

  loadPosts() {
    if (this.loading || !this.hasMore) return;

    this.loading = true;

    this.postService.getbyAuthor(this.profileId ,this.page, this.size).subscribe({
      next: res => {
        this.posts = [...this.posts, ...res.posts];

        this.hasMore = this.page < res.totalPages - 1;
        this.page++;

        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    const pos = window.innerHeight + window.scrollY;
    const max = document.body.scrollHeight - 300;

    if (pos >= max) {
      this.loadPosts();
    }
  }

}
