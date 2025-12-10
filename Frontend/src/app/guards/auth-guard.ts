import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Console } from 'console';

export const authGuard: CanActivateFn = async (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  try {
    const user = await authService.logged().toPromise();
    console.log("AuthGuard - logged user:", user);

    if (user.role === 'ADMIN' && state.url !== '/admin') {
      router.navigate(['/admin']);
      return false;
    }

    if (user.role !== 'ADMIN' && state.url !== '/') {
      router.navigate(['/']);
      return false;
    }

    return true; // already at correct route
  } catch (err) {
    if (state.url === '/login' || state.url === '/register') {
      return true;
    }
    router.navigate(['/login']);
    return false;
  }
};
