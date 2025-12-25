import { Component, OnInit, HostListener, ChangeDetectorRef } from '@angular/core';
import { PostService } from '../../services/post.service';
import { ReportService } from '../../services/report.service';
import { Post } from '../../models/post.model';
import { CommonModule } from '@angular/common';
import { PostCard } from '../../components/post-card/post-card';
import { CreatePost } from '../../components/create-post/create-post';
import { MainHeader } from '../../components/main-header/main-header';
import { CommentsSection } from '../../components/comments-section/comments-section';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, PostCard, CreatePost, MainHeader , CommentsSection],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home implements OnInit {

  posts: Post[] = [];
  page = 0;
  size = 5;
  loading = false;
  hasMore = true;
  userReports: Map<number, number> = new Map(); // postId -> reportId

  feedType: 'all' | 'subscribed' = 'subscribed';

  constructor(
    private postService: PostService,
    private reportService: ReportService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadUserReports();
    this.loadPosts();
  }


  switchFeed(type: 'all' | 'subscribed'): void {
    if (this.feedType === type) return;
    this.feedType = type;
    this.posts = [];
    this.page = 0;
    this.hasMore = true;
    this.loadPosts();
  }

  loadUserReports(): void {
    this.reportService.getUserReports(0, 100).subscribe({
      next: (res) => {
        res.reports.forEach(report => {
          if (report.postId) {
            this.userReports.set(report.postId, report.id);
          }
        });
      },
      error: (err) => {
        console.error('Error loading user reports', err);
      }
    });
  }

  loadPosts() {
    if (this.loading || !this.hasMore) return;

    this.loading = true;

    const request = this.feedType === 'subscribed'
      ? this.postService.getSubscribedPosts(this.page, this.size)
      : this.postService.getPosts(this.page, this.size);

    request.subscribe({
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

  activePostId: number | null = null;
  commentsOpen = false;

  openComments(postId: number) {
    // If comments are open for the same post, toggle them closed
    if (this.commentsOpen && this.activePostId === postId) {
      this.activePostId = null;
      this.commentsOpen = false;
      return;
    }

    // Otherwise open comments for the requested post (switch if different)
    this.activePostId = postId;
    this.commentsOpen = true;
  }

  closeComments() {
    this.commentsOpen = false;
    this.activePostId = null;
  }
}
