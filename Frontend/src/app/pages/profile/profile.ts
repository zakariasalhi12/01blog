import { Component } from '@angular/core';
import { MainHeader } from '../../components/main-header/main-header';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { PostCard } from '../../components/post-card/post-card';

@Component({
  selector: 'app-profile',
  imports: [MainHeader , RouterLink , CommonModule , MatIcon , PostCard],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {

}
