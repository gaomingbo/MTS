# nginx+ffmpeg rstp转rtmp

* 安装ffmpeg
````
*****************************

先安装nasm
wget http://www.nasm.us/pub/nasm/releasebuilds/2.13.03/nasm-2.13.03.tar.xz
tar -xvf nasm-2.13.03.tar.xz
cd nasm-2.13.03
./configure  --prefix=/usr 
make
sudo make install

*****************************

缺少libx264库，需要安装该库：
wget https://code.videolan.org/videolan/x264/-/archive/master/x264-master.zip
unzip x264-master.zip
cd x264-master
./configure --enable-static --enable-share --disable-opencl
make
sudo make install

*****************************

wget https://github.com/FFmpeg/FFmpeg/archive/n4.2.zip
sudo apt-get install yasm
unzip n4.2.zip
cd FFmpeg-n4.2
./configure --enable-gpl --enable-libx264
make
make install
````

* 安装nginx
````
sudo apt-get install build-essential libpcre3 libpcre3-dev openssl libssl-dev zlib1g-dev
wget http://nginx.org/download/nginx-1.12.0.tar.gz
wget https://github.com/arut/nginx-rtmp-module/archive/master.zip
unzip master.zip     
tar -zxvf nginx-1.12.0.tar.gz
cd nginx-1.12.0
./configure --prefix=/opt/nginx --with-http_ssl_module --add-module=../nginx-rtmp-module-master
make
sudo make install
````

*  nginx配置 nginx.conf
```nginx
worker_processes 1;
events {
    worker_connections 1024;
}
rtmp {
    server {
        listen 1935;
        chunk_size 4000;
		application mylive {
            live on;
        }
    }
}
http {
    include mime.types;
    default_type application/octet-stream;
    sendfile on;
    keepalive_timeout 65;

    server {
        listen 20000;
        server_name localhost;
        location / {
            root html;
            index index.html index.htm;
        }
        error_page 500 502 503 504 /50x.html;
			location = /50x.html {
			root html;
		}
    }
}
```

* 运行ffmpeg命令
ffmpeg -re -rtsp_transport tcp -i "rtsp://admin:admin@192.168.0.100:554/cam/realmonitor?channel=1&subtype=1" -f flv -vcodec libx264 -vprofile baseline -acodec aac -ar 44100 -strict -2 -ac 1 -f flv -s 890*540 -q 10 "rtmp://192.168.0.121:1935/mylive/1"
