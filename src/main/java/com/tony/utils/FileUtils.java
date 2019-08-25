package com.tony.utils;

import com.tony.FfmpegServer;

import java.io.File;

public class FileUtils {

    public static final String NGINX_ROOT = "{nginx_root}";
    public static final String NGINX_ROOT_REGEX = "\\{nginx_root}";

    public static void mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String parsePath(String path) {
        if (path.contains(NGINX_ROOT)) {
            path = path.replaceAll(NGINX_ROOT_REGEX, FfmpegServer.NGINX_PATH);
        }
        return path;
    }

    public static void main(String[] args) {
        String path = "{nginx_root}/1/demo.m3u8";
        System.out.println( parsePath(path));
    }
}
