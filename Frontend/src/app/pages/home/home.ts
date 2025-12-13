import { Component, OnInit, HostListener, ChangeDetectorRef } from '@angular/core';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.model';
import { BackedURL } from '../../../environments/environment';
import { CommonModule } from '@angular/common';
import { PostCard } from '../../components/post-card/post-card';
import { CreatePost } from '../../components/create-post/create-post';
import { MainHeader } from '../../components/main-header/main-header';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, PostCard , CreatePost , MainHeader],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home implements OnInit {

  posts: Post[] = [];
  page = 0;
  size = 5;
  loading = false;
  hasMore = true;

  constructor(private postService: PostService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts() {
    if (this.loading || !this.hasMore) return;

    this.loading = true;

    this.postService.getPosts(this.page, this.size).subscribe({
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
