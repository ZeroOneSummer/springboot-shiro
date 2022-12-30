package com.bocsoft.obss.one.test;

import java.util.Arrays;

public class FileByteSplitDemo {

    /**
     * 针对文件流传输限制1M大小
     * 分批传输算法，前端分批请求后merge数据转换成Blob对象然后生成File
     */
    public static void main(String[] args) {
        int size = 10;  //限制大小  1byte=8bit  1MB=1024kb*1024b=1048567 byte
        String msg = "0123456789112345";
        byte[] bytes = msg.getBytes();  //文件字节流
        int length = bytes.length;
        int pages = length % size == 0 ? length / size : length / size + 1;
        System.out.println("data length: " + length);
        System.out.println("size: " + size);
        System.out.println("pages: " + pages);
        System.out.println("msgs: " + Arrays.toString(bytes));
        System.out.println("======================");
        if (pages == 0) {
            System.out.println("no data!");
        } else {
            //把pages page data返回给前端
            for (int i = 0; i < pages; i++) {
                System.out.println("page: " + (i + 1));
                int sIndex = i * size;
                int eIndex = (i + 1) * size >= length ? length : (i + 1) * size;
                System.out.println("data: " + Arrays.toString(Arrays.copyOfRange(bytes, sIndex, eIndex)));
                System.out.println();
            }
        }
    }
}
