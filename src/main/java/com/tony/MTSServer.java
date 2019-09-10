package com.tony;


import com.tony.utils.Config;
import com.tony.utils.FileUtils;
import com.tony.utils.JsonUtils;
import com.tony.utils.SocketUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MTSServer {

  private static Logger logger = Logger.getLogger(MTSServer.class);

  private static MTSServer server;

  private static ServerSocketChannel serverSocketChannel;

  private static ExecutorService threadTool = Executors.newCachedThreadPool();

  private MTSServer() {

  }

  public static MTSServer getInstance() {
    synchronized ( MTSServer.class) {
      if (null == server) {
        server = new MTSServer();
      }
      return server;
    }
  }

  public MTSServer init(String serverIp, int port) throws IOException {
    InetSocketAddress inetSocketAddress = new InetSocketAddress(serverIp, port);
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.socket().bind(inetSocketAddress);
    return server;
  }

  public void startService() {
    Runnable start = () -> {
      while (true) {
        try {
          SocketChannel channel = serverSocketChannel.accept();
          threadTool.submit(handler(channel));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    threadTool.submit(start);
    logger.info("start mts server success");
  }

  private Runnable handler(SocketChannel channel) {
    return () -> {
      Socket socket = channel.socket();
      try {
        while (true) {
          InputStream is = socket.getInputStream();
          int len = SocketUtils.recvDataSize(is);
          byte[] buff = new byte[len];
          int recvLen = SocketUtils.recvData(is, buff, len);
          String param = new String(buff, 0, recvLen);
          String rlt = invoke(param);

          byte[] resDate = rlt.getBytes();
          OutputStream out = socket.getOutputStream();
          out.write(SocketUtils.assembleData(resDate));
          out.flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    };
  }

  private String invoke(String param) {
    logger.info("invoke param: " + param);
    Map<String, String> map = JsonUtils.string2Obj(param, Map.class);
    if (null == map) return "FAILED";
    try {
      // 2 - dav to m3u8
      // 1 - start real time
      // 0 - stop
      if ("2".equalsIgnoreCase(map.get("action"))) {
        String number = map.get("number"); // '20190825000001'
        String filePath = map.get("file"); // '/opt/20190825000001.dav'
        if (null == filePath || null == number) return "FAILED";
        String outputPath = FfmpegServer.NGINX_PATH + "/record/" + number; // '/usr/share/nginx/html/record/20190825000001'
        FileUtils.mkdir(outputPath);
        FileUtils.mkdir("/tmp/ffmpeg");
        String tmpPath = "/tmp/ffmpeg/output-" + number + ".ts"; // '/tmp/ffmpeg/output-20190825000001.ts'

        threadTool.submit(runSegmentCmd(filePath, tmpPath, outputPath));
        return "SUCCESS";
      }

      String cameraId = map.get("cameraId");
      if (null == cameraId) return "FAILED";
      if ("1".equalsIgnoreCase(map.get("action"))) {
        String rtspUrl = map.get("rtspUrl");
        String rate = map.get("rate");
        String path = FfmpegServer.NGINX_PATH + "/live/cameraid/" + cameraId;
        FileUtils.mkdir(path);
        path += "/" + rate + ".m3u8";

        String[] cmd = FfmpegServer.realTimeCmds;
        cmd[6] = rtspUrl;
        cmd[17] = path;
        FfmpegServer.cmd(cameraId, cmd);
      }
      if ("0".equalsIgnoreCase(map.get("action"))) {
        FfmpegServer.processMap.get(cameraId).destroy();
      }
      return "SUCCESS";
    } catch (Exception e) {
      logger.error(e);
      e.printStackTrace();
      return "FAILED";
    }
  }

  Runnable runSegmentCmd(String filePath, String tmpPath, String outputPath) {
    return () -> {

      String[] cmd = FfmpegServer.conversionFormatCmd;
      cmd[2] = filePath;
      cmd[7] = tmpPath;
      Process process = FfmpegServer.cmd(null, cmd);
      if (null == process) {
        System.out.println("cmd: " + Arrays.toString(cmd) + " FAILED");
        return;
      }
      Process segmentProcess = null;
      while (true) {
        if (!process.isAlive()) {
          String[] segmentCmd = FfmpegServer.segmentCmd;
          segmentCmd[2] = tmpPath;
          segmentCmd[10] = outputPath + "/0.m3u8";
          segmentCmd[13] = outputPath + "/0%03d.ts";
          segmentProcess = FfmpegServer.cmd(null, segmentCmd);
          break;
        }
      }
      while (null != segmentProcess) {
        if (!segmentProcess.isAlive()) {
          File file = new File(tmpPath);
          file.delete();
          break;
        }
      }
    };
  }

}
