import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-top-menu',
  templateUrl: './top-menu.component.html',
  styleUrls: ['./top-menu.component.css']
})
export class TopMenuComponent implements OnInit {

  menuItems = [
    {
      name: '话题节点',
      router: '/path'
    },
    {
      name: '个人主页',
      router: '/home'
    },
    {
      name: '私信信息',
      router: '/dm'
    },
    {
      name: '关于本站',
      router: '/about'
    },
  ];

  constructor() { }

  ngOnInit() {
  }

}
