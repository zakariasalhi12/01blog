import { Component, Input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { Post } from '../../models/post.model';
import { BackedURL } from '../../../environments/environment';

@Component({
  selector: 'app-post-card',
  imports: [MatIcon, CommonModule],
  standalone: true,
  templateUrl: './post-card.html',
  styleUrl: './post-card.css',
})
export class PostCard {

    public getFullFileUrl(fileUrl: string | undefined): string | undefined {
      if (!fileUrl) return undefined;
      console.log('Computed full file URL:', `${BackedURL}${fileUrl}`);
      return `${BackedURL}${fileUrl}`; // no extra slash if your API already returns '/uploads/...'
    }

  @Input() post!: Post; // receives a post object from parent
  @Input() fullFileUrl?: string;    // the computed URL from HomeComponent
}
