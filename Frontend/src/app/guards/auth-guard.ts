import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = async (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  try {
    const user = await authService.logged().toPromise();

    // Logged-in users (all roles) should not access login/register
    if (state.url === '/login' || state.url === '/register') {
      // Redirect based on role
      if (user.role === 'ADMIN') {
        router.navigate(['/admin']);
      } else {
        router.navigate(['/']);
      }
      return false;
    }

    // Admins can access everything else
    if (user.role === 'ADMIN') {
      return true;
    }

    // Normal users can access everything except admin routes
    if (state.url.startsWith('/admin')) {
      router.navigate(['/']);
      return false;
    }

    return true;
  } catch (err) {
    // Not logged-in users can only access login/register
    if (state.url === '/login' || state.url === '/register') {
      return true;
    }
    router.navigate(['/login']);
    return false;
  }
};
