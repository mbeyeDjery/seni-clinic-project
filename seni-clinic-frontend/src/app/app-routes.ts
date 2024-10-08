import {Routes} from '@angular/router';
import {AppLayoutComponent} from "./shared/layout/app.layout.component";
import {NotfoundComponent} from "./shared/components/notfound/notfound.component";
import {ErrorComponent} from "./shared/components/error/error.component";
import {AccessComponent} from "./shared/components/access/access.component";
import {LoginComponent} from "./features/auth/login.component";


export const routes: Routes = [
    {
        path: '', component: AppLayoutComponent,
        children: [

        ]
    },
    { path: 'error', component: ErrorComponent },
    { path: 'login', component: LoginComponent },
    { path: 'access', component: AccessComponent },
    { path: 'notfound', component: NotfoundComponent },
    { path: '**', redirectTo: '/notfound' },
];
