import {ApplicationConfig, enableProdMode, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import {AppLayoutModule} from './shared/layout/app.layout.module';
import {LocationStrategy, PathLocationStrategy} from '@angular/common';
import {routes} from "./app-routes";


export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(AppLayoutModule),
    { provide: LocationStrategy, useClass: PathLocationStrategy },
    provideRouter(routes)
  ]
};
