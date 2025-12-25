import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-banned-user',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './banned-user.html',
    styleUrl: './banned-user.css'
})
export class BannedUserComponent {
    constructor(private authService: AuthService, private router: Router) { }

    logout(): void {
        localStorage.removeItem('token');
        this.authService.logout()
        this.router.navigate(['/login']);
    }
}
