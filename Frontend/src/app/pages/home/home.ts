import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Post } from '../../models/post.model';
import { PostService } from '../../services/post.service';
import { PostCard } from '../../components/post-card/post-card';
import { CommonModule } from '@angular/common';
import { BackedURL } from '../../../environments/environment';

@Component({
  selector: 'app-home',
  imports: [PostCard, CommonModule],
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home implements OnInit {
  posts: Post[] = [];
  loading = true;
  error = '';

  constructor(private postService: PostService, private cdr: ChangeDetectorRef) { }


  public getFullFileUrl(fileUrl: string | undefined): string | undefined {
    if (!fileUrl) return undefined;
    console.log('Computed full file URL:', `${BackedURL}${fileUrl}`);
    return `${BackedURL}${fileUrl}`; // no extra slash if your API already returns '/uploads/...'
  }

  ngOnInit(): void {
    this.postService.getPosts().subscribe({
      next: res => {
        this.posts = res.posts;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        this.error = 'Failed to load posts';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

}
