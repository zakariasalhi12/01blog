import { Routes } from '@angular/router';

import {Login} from './pages/login/login'
import {Register} from './pages/register/register'
import { Home } from './pages/home/home';
import { authGuard } from './guards/auth-guard';
import { Post } from './pages/post/post';
import { Profile } from './pages/profile/profile';
import { MyProfile } from './pages/my-profile/my-profile';
import { Notifications } from './pages/notifications/notifications';

export const routes: Routes = [
    // {path: "admin", component: Home , canActivate:[authGuard]},
    {path: "login" , component: Login , canActivate:[authGuard]},
    {path: "register" , component: Register , canActivate:[authGuard]},
    {path: "profile/me", component: MyProfile , canActivate:[authGuard]},
    {path: "profile/:id", component: Profile , canActivate:[authGuard]},
    {path: "post/:id", component: Post, canActivate:[authGuard]},
    {path: "notifications" , component : Notifications , canActivate:[authGuard]},
    {path: "", component: Home , canActivate:[authGuard]},
];
