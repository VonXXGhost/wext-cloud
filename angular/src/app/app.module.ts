import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NavigationComponent } from './navigation/navigation.component';
import { LayoutModule } from '@angular/cdk/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatMenuModule } from '@angular/material/menu';
import { HomepageComponent } from './homepage/homepage.component';
import { TopMenuComponent } from './navigation/top-menu/top-menu.component';
import { PathPageComponent } from './path-page/path-page.component';
import { TimelineStreamComponent } from './timeline-stream/timeline-stream.component';
import { SignUpComponent } from './auth/sign-up/sign-up.component';
import { LoginComponent } from './auth/login/login.component';
import {MatFormFieldModule, MatInputModule} from "@angular/material";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {JWTInterceptor} from "./auth/jwtinterceptor";
import { UserHomeComponent } from './user-home/user-home.component';
import { UserProfilePageComponent } from './user-profile-page/user-profile-page.component';
import { UserInfoPanelComponent } from './user-profile-page/user-info-panel/user-info-panel.component';
import { FollowingFollowerComponent } from './user-profile-page/following-follower/following-follower.component';
import { UserProfileTimelineComponent } from './user-profile-page/user-profile-timeline/user-profile-timeline.component';
import { WextPageComponent } from './wext-page/wext-page.component';
import { DirectMessagePageComponent } from './direct-message-page/direct-message-page.component';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    HomepageComponent,
    TopMenuComponent,
    PathPageComponent,
    TimelineStreamComponent,
    SignUpComponent,
    LoginComponent,
    UserHomeComponent,
    UserProfilePageComponent,
    UserInfoPanelComponent,
    FollowingFollowerComponent,
    UserProfileTimelineComponent,
    WextPageComponent,
    DirectMessagePageComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatGridListModule,
    MatCardModule,
    MatMenuModule,
    RouterModule.forRoot([
        {path: '', component: HomepageComponent},
        {path: 'path', component: PathPageComponent,
          children: [
            {path: '**', component: PathPageComponent}
          ]},
        {path: 'sign_up', component: SignUpComponent},
        {path: 'login', component: LoginComponent},
        {path: 'home', component: UserHomeComponent},
        {path: 'user/:username', component: UserProfilePageComponent,
          children: [
            {path: 'following', component: FollowingFollowerComponent, data: {pageType: 'following'}},
            {path: 'follower', component: FollowingFollowerComponent, data: {pageType: 'follower'}},
            {path: '', component: UserProfileTimelineComponent},
          ]},
        {path: 'wext/:wextID', component: WextPageComponent},
        {path: 'dm', component: DirectMessagePageComponent}
      ],
      { enableTracing: false }
      ),
    HttpClientModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JWTInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
