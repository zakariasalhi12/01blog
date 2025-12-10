import { Component } from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-post-card',
  imports: [MatIcon , CommonModule],
  templateUrl: './post-card.html',
  styleUrl: './post-card.css',
})
export class PostCard {

}
