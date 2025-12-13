import { Routes } from '@angular/router';

import {Login} from './pages/login/login'
import {Register} from './pages/register/register'
import { Home } from './pages/home/home';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
    {path: "login" , component: Login , canActivate:[authGuard]},
    {path: "register" , component: Register , canActivate:[authGuard]},
    {path: "profile/me", component: Home , canActivate:[authGuard]},
    {path: "", component: Home , canActivate:[authGuard]},
];
