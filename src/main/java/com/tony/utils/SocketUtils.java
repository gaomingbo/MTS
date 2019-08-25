package com.tony.utils;

import java.io.InputStream;

public class SocketUtils {

    /**
     * 字节位 0
     * 字节位 1
     * 字节位 2
     * 字节位 3
     * 字节位 4 Data 数据类型
     * 字节位 5 包序号，
     * 字节位 6 – 9 一共四位，数据长度
     */
    private static byte[] getHeader(int dataLen) {
        byte[] header = {'E', 'P', 'P', 'S', 0, 0, 0, 0, 0, 0};
        header[6] += (byte) ((dataLen >> 24) & 0xFF);
        header[7] += (byte) ((dataLen >> 16) & 0xFF);
        header[8] += (byte) ((dataLen >> 8) & 0xFF);
        header[9] += (byte) (dataLen & 0xFF);
        return header;
    }

    private static byte[] arraycopy(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static byte[] assembleData(byte[] data) {
        byte[] header = SocketUtils.getHeader(data.length);
        return SocketUtils.arraycopy(header, data);
    }

    public static int recvDataSize(InputStream is) {
        byte[] header = new byte[10];
        int recvLen = recvData(is, header, 10);
        if (recvLen <= 0) {
            return -1;
        }
        if (header[0] != 'E' || header[1] != 'P' || header[2] != 'P' || header[3] != 'S') {
            //格式不对
            return -1;
        }
        return convBytesToInt(header, 6);
    }

    //接收指定长度的数据，收完为止
    public static int recvData(InputStream is, byte[] buff, int len) {
        try {
            int totalRecvLen = 0;
            int recvLen;
            while (totalRecvLen < len) {
                recvLen = is.read(buff, totalRecvLen, len - totalRecvLen);
                totalRecvLen += recvLen;
            }
            return len;
        } catch (Exception e) {
            System.out.println("client is closed!");
            return -1;
        }
    }

    private static int convBytesToInt(byte[] buff, int offset) {
        //4bytes 转为int，要考虑机器的大小端问题
        int len, byteValue;
        len = 0;
        byteValue = (0x000000FF & ((int) buff[offset]));
        len += byteValue << 24;
        byteValue = (0x000000FF & ((int) buff[offset + 1]));
        len += byteValue << 16;
        byteValue = (0x000000FF & ((int) buff[offset + 2]));
        len += byteValue << 8;
        byteValue = (0x000000FF & ((int) buff[offset + 3]));
        len += byteValue;
        return len;
    }
}
