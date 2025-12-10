import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {MatIcon} from '@angular/material/icon';

import {PostCard} from '../../components/post-card/post-card'

@Component({
  selector: 'app-home',
  imports: [MatIcon , CommonModule , PostCard],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {

}
