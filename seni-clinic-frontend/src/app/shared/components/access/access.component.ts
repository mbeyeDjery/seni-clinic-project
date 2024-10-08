import { Component } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-access',
    templateUrl: './access.component.html',
    standalone: true,
    imports: [ButtonModule, RouterLink],
})
export class AccessComponent { }
