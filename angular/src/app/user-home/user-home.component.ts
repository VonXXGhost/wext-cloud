import { Component, OnInit } from '@angular/core';
import {WextService} from "../service/wext.service";
import {apiUrl} from "../config/apiUrl";

@Component({
  selector: 'app-user-home',
  templateUrl: './user-home.component.html',
  styleUrls: ['./user-home.component.css']
})
export class UserHomeComponent implements OnInit {
  private wextContent: string;
  private picURL: string;
  private picPath: string;

  constructor(
    private wextService: WextService
  ) {
    this.wextContent = '';
  }

  ngOnInit() {
  }

  doWext() {
    const body = {
      content: this.wextContent,
      picURLs: this.picPath ? [this.picPath] : null,
      path: '/'
    } as WextNewRequest;
    console.debug(body);
    this.wextService.putNewWext(body).subscribe(
      resp => {
        this.wextContent = '';
        alert('发布成功');
      }
    );
  }

  picChange(file: File) {
    console.debug(file);
    const picFile = file;
    if (!picFile) {
      return;
    }
    if (picFile.type !== 'image/jpeg' && picFile.type !== 'image/png' && picFile.type !== 'image/gif') {
      alert('只允许上传jpg、png或gif格式');
      return;
    }
    if (picFile.size > 10485760) {
      alert('文件过大，最大只允许上传10MB');
    }
    this.wextService.uploadPic(picFile).subscribe(
      resp => {
        this.picPath = resp.data.path;
        this.picURL = apiUrl.base + resp.data.path.substr(1);
      }
    );
  }
}
