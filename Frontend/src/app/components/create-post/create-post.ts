import { ChangeDetectorRef, Component } from '@angular/core';
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
  previewUrl: string | ArrayBuffer | null = null;
  isImage = false;
  isVideo = false;

  constructor(private fb: FormBuilder, private postService: PostService , private cdr: ChangeDetectorRef) {
    this.postForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required]
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
        this.previewUrl = reader.result;
      };
      reader.readAsDataURL(this.selectedFile);
    }
    this.cdr.detectChanges();
  }

  onSubmit() {
    if (this.postForm.invalid) return;

    const { title, content } = this.postForm.value;

    this.postService.createPost(title, content, this.selectedFile ?? undefined)
      .subscribe({
        next: (res) => {
          console.log('Post created', res);

          // Reset form & preview
          this.postForm.reset();
          this.selectedFile = null;
          this.previewUrl = null;
          this.isImage = false;
          this.isVideo = false;
        },
        error: (err) => console.error('Error creating post', err)
      });
      window.location.reload();
  }
}
