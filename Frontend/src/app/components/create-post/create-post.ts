import { ChangeDetectorRef, Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-post.html',
  styleUrls: ['./create-post.css'], // fixed: styleUrls instead of styleUrl
})
export class CreatePost {
  postForm: FormGroup;
  selectedFile: File | null = null;
  previewUrl = signal<string | ArrayBuffer | null>(null); 
  isImage = false;
  isVideo = false;
  submitting = false;
  serverError = signal<string | null>(null);

  constructor(private fb: FormBuilder, private postService: PostService , private cdr: ChangeDetectorRef) {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(100)]],
      content: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(10000)]]
    });
    this.cdr = cdr;
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];

      const type = this.selectedFile.type;
      this.isImage = type.startsWith('image/');
      this.isVideo = type.startsWith('video/');

      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl.set(reader.result);
      };
      reader.readAsDataURL(this.selectedFile);
    }
    this.cdr.detectChanges();
  }

  onSubmit() {
    if (this.postForm.invalid) return;

    this.submitting = true;
    this.serverError = signal(null);

    const { title, content } = this.postForm.value;

    this.postService.createPost(title, content, this.selectedFile ?? undefined)
      .subscribe({
        next: (res: any) => {
          // Backend responds with { message, postId }
          // Reload the page so the feed is refreshed from the server
          window.location.reload();
        },
        error: (err) => {
          console.error('Error creating post', err);
          const body = err.error;
          if (body && body.errors) {
            const first = Object.values(body.errors)[0] as string;
            this.serverError.set(first || 'Failed to create post');
          } else {
            this.serverError.set(body?.error || 'Failed to create post');
          }
          this.submitting = false;
        }
      });
  }
}
