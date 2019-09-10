# nginx+ffmpeg+rtsp+hls
rtsp拉流转hls直播，本地视频文件切片m3u8

*  nginx配置 /etc/nginx/nginx.conf
```nginx
....
server{
  listen 20000;
  server_name  localhost;
  ## 切片访问路径
  location /record { 
    types{
      application/vnd.apple.mpegurl m3u8;
      video/mp2t ts;
    }
    root html;
    add_header Cache-Control no-cache;
    add_header Access-Control-Allow-Origin *;
  }

  ## 直播播放地址
  location /live {
    types{
      application/vnd.apple.mpegurl m3u8;
      video/mp2t ts;
    }
    root html;
    add_header Cache-Control no-cache;
    add_header Access-Control-Allow-Origin *;
  }
}
```
*  nginx配置 /etc/nginx/mime.types
```nginx
application/x-mpegURL           m3u8;
application/vnd.apple.mpegurl   m3u8;
video/mp2t                      ts;
```
* 安装ffmpeg
````
wget https://github.com/FFmpeg/FFmpeg/archive/n4.2.zip
sudo apt-get install yasm
./configure
make
make install
````
