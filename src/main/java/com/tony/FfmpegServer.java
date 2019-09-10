package com.tony;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FfmpegServer {

    private static Logger logger = Logger.getLogger(FfmpegServer.class);

//     "ffmpeg -f rtsp -rtsp_transport tcp -i " +
//      "\"rtsp://admin:tn123456@zhengzhoutn1.wicp.vip:554/cam/realmonitor?channel=1&subtype=1\" -c copy -f " +
//      "hls -hls_time 2.0 -hls_list_size 0 -hls_wrap 15 /usr/share/nginx/html/hls/test.m3u8";

    /**
     * hls url: http://119.3.161.94:20000/live/cameraid/100000@0/0.m3u8
     *
     * record url: http://119.3.161.94:20000/record/20190825000001/0.m3u8
     */

    // 1.username 2.pwd 3.ip 4.port 5.channel 6.rate 0 or 1
//    private static String DAHUA_URL_FORMAT = "rtsp://%s:%s@%s:%s/cam/realmonitor?channel=%s&subtype=&subtype=";


    /**
     * -hls_time 切片时长
     * -hls_list_size HLS播放的列表
     * -hls_wrap 最大的TS循环数
     *
     * 6  -> rtsp url
     * 17 -> output file
     */
    public static String[] realTimeCmds = {
            "ffmpeg",
            "-f", "rtsp",
            "-rtsp_transport", "tcp",
            "-i", "xxxxx",
            "-c", "copy",
            "-f", "hls",
            "-hls_time", "2.0",
            "-hls_list_size", "0",
            "-hls_wrap", "5",
            "xxxxx"};

    /**
     * 转为完整的ts
     * ffmpeg -i demo.dav -c copy -bsf h264_mp4toannexb output.ts
     *
     * 2 -> file path
     * 7 -> output file path
     */
    public static String[] conversionFormatCmd = {
            "ffmpeg",
            "-i", "xxxxx",
            "-c", "copy",
            "-bsf", "h264_mp4toannexb",
            "xxxxx/output.ts"};

    /**
     * 转为完整的ts
     * ffmpeg -i output.ts -c copy -map 0 -f segment -segment_list playlist.m3u8 -segment_time 10 output%03d.ts
     *
     * 2 -> ts file
     * 10 -> m3u8 file path
     * 13 -> output file path
     */
    public static String[] segmentCmd = {
            "ffmpeg",
            "-i", "xxxxx/playlist.m3u8",
            "-c", "copy",
            "-map", "0",
            "-f", "segment",
            "-segment_list", "xxxxx",
            "-segment_time", "10",
            "xxxxx/output%03d.ts"};


    public static String NGINX_PATH = "/usr/share/nginx/html";


    public static Map<String, Process> processMap = new HashMap<>();

    public static Process cmd(String cameraId, String ... args) {
        List<String> commend = new ArrayList<>();
        for (String arg : args) {
            commend.add(arg);
        }
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commend);
        try {
            Process process = builder.start();
            if (cameraId != null) {
                processMap.put(cameraId, process);
            }
            dealStream(process);
            return process;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void dealStream(Process process) {
        if (process == null) {
            return;
        }
        // 处理InputStream的线程
        new Thread() {
            @Override
            public void run() {
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                try {
                    while ((line = in.readLine()) != null) {
                        logger.info("output: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        // 处理ErrorStream的线程
        new Thread() {
            @Override
            public void run() {
                BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = null;
                try {
                    while ((line = err.readLine()) != null) {
                        logger.info("err: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        err.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

}
