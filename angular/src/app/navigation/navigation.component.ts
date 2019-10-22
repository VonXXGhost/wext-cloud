import {Component, DoCheck} from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map, share } from 'rxjs/operators';
import {MatToolbarModule} from '@angular/material/toolbar';
import { AuthService } from '../service/auth.service';
import {UserService} from "../service/user.service";
import {UserInfoItem} from "../class/user-info-item";

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements DoCheck {

  // isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
  //   .pipe(
  //     map(result => result.matches),
  //     share()
  //   );
  private $loginUser: UserInfoItem;

  constructor(
    private breakpointObserver: BreakpointObserver,
    private matToolbarModule: MatToolbarModule,
    private authService: AuthService,
    private userService: UserService
  ) {}

  ngDoCheck(): void {
    if (this.authService.isLogin() && !this.$loginUser) {
      this.userService.getLoginUser().subscribe(
        data => {
          this.$loginUser = data.data;
        },
        error1 => {
          console.error(error1);
          this.authService.logOut();
        }
      );
    }
  }

}
