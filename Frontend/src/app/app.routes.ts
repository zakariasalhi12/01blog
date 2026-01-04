import { Routes } from '@angular/router';

import { Login } from './pages/login/login'
import { Register } from './pages/register/register'
import { Home } from './pages/home/home';
import { authGuard } from './guards/auth-guard';
import { Profile } from './pages/profile/profile';
import { MyProfile } from './pages/my-profile/my-profile';
import { Notifications } from './pages/notifications/notifications';
import { Admin } from './pages/admin/admin';

import { BannedUserComponent } from './pages/banned-user/banned-user';
import { NotFoundComponent } from './pages/not-found/not-found';

export const routes: Routes = [
    { path: "admin", component: Admin, canActivate: [authGuard] },
    { path: "banned", component: BannedUserComponent, canActivate: [authGuard] },
    { path: "login", component: Login, canActivate: [authGuard] },
    { path: "register", component: Register, canActivate: [authGuard] },
    { path: "profile/me", component: MyProfile, canActivate: [authGuard] },
    { path: "profile/:id", component: Profile, canActivate: [authGuard] },
    { path: "notifications", component: Notifications, canActivate: [authGuard] },
    { path: "", component: Home, canActivate: [authGuard] },
    { path: "not-found", component: NotFoundComponent },
    { path: "**", component: NotFoundComponent },
];
