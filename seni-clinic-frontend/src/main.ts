import { enableProdMode, importProvidersFrom } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';


import { environment } from './environments/environment';
import { LocationStrategy, PathLocationStrategy } from '@angular/common';
import { AppLayoutModule } from './app/shared/layout/app.layout.module';
import { AppComponent } from './app/app.component';
import { bootstrapApplication } from '@angular/platform-browser';
import {provideRouter} from "@angular/router";
import {routes} from "./app/app-routes";

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
    providers: [
        importProvidersFrom(AppLayoutModule),
        { provide: LocationStrategy, useClass: PathLocationStrategy },
        provideRouter(routes)
    ]
})
  .catch(err => console.error(err));
