import { Component } from '@angular/core';
import { LayoutService } from 'src/app/shared/layout/service/app.layout.service';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { FormsModule } from '@angular/forms';
import { CheckboxModule } from 'primeng/checkbox';
import { ButtonModule } from 'primeng/button';
import { RouterLink } from '@angular/router';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styles: [`
        :host ::ng-deep .pi-eye,
        :host ::ng-deep .pi-eye-slash {
            transform:scale(1.6);
            margin-right: 1rem;
            color: var(--primary-color) !important;
        }
    `],
    standalone: true,
    imports: [InputTextModule, PasswordModule, FormsModule, CheckboxModule, ButtonModule, RouterLink]
})
export class LoginComponent {

    valCheck: string[] = ['remember'];

    password!: string;

    constructor(public layoutService: LayoutService, private _httpClient: HttpClient) { }

    login(){
        console.log("Login");
        const body = new HttpParams({
            fromObject: {
                client_id: 'auth_client',
                username: 'djem',
                password: '123456',
                grant_type: 'password',
                client_secret: 'WyqYdtyXPVP8DYU98HqOCmA9IQuA9TjH'
            }
        });

        const httpOptions = {
            headers: new HttpHeaders({
                Accept: 'application/json',
                'Content-Type': `application/x-www-form-urlencoded`
            })};

        const headers = new HttpHeaders(
            {
                Accept: 'application/json',
                'Content-Type': `application/x-www-form-urlencoded`,
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            }
        );

        this._httpClient.post("http://localhost:1200/realms/my-realm/protocol/openid-connect/token", body, httpOptions)
            .subscribe(resp => {
                console.log(resp);
            });
    }
}
