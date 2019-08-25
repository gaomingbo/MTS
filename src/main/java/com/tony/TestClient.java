package com.tony;

import com.tony.utils.SocketUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class TestClient {

    public static void main(String[] args) throws IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("test.tony-traffic.com", 9011);
        SocketChannel channel = SocketChannel.open();
        channel.connect(inetSocketAddress);
        Socket socket = channel.socket();

        /**
         * {
         * 	"action":"1",
         * 	"cameraId":"100000@0",
         * 	"rtspUrl":"rtsp://admin:tn123456@zhengzhoutn1.wicp.vip:554/cam/realmonitor?channel=1&subtype=0",
         * 	"rate":"0"
         * }
         */
        String rlt = "{\"action\":\"0\",\"cameraId\":\"100000@0\"," +
                "\"rtspUrl\":\"rtsp://admin:tn123456@zhengzhoutn1.wicp.vip:554/cam/realmonitor?channel=1&subtype=1\"," +
                "\"rate\":\"1\"}";

        String rlt2 = "{\"action\":\"2\", " +
                "\"number\":\"20190825000001\"," +
                "\"file\":\"/opt/20190825000001.dav\"}";

        byte[] resDate = rlt2.getBytes();
        OutputStream out = socket.getOutputStream();
        out.write(SocketUtils.assembleData(resDate));
        out.flush();

        InputStream is = socket.getInputStream();
        int len = SocketUtils.recvDataSize(is);
        byte[] buff = new byte[len];
        int recvLen = SocketUtils.recvData(is, buff, len);
        String param = new String(buff, 0, recvLen);
        System.out.println(param);

    }
}
